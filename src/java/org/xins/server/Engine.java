/*
 * $Id: Engine.java,v 1.112 2007/09/24 12:46:37 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.NDC;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.io.IOReader;
import org.xins.common.manageable.InitializationException;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.InvalidSpecificationException;
import org.xins.common.text.TextUtils;

import static org.xins.server.ConfigManager.LOG_STACK_TRACE_AT_MESSAGE_LEVEL;

/**
 * XINS server engine. The engine is a delegate of the {@link APIServlet} that
 * is responsible for initialization and request handling.
 *
 * @version $Revision: 1.112 $ $Date: 2007/09/24 12:46:37 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 */
final class Engine {

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Property used to start JMX.
    */
   private static final String JMX_PROPERTY = "org.xins.server.jmx";

   /**
    * The state machine for this engine. Never <code>null</code>.
    */
   private final EngineStateMachine _stateMachine = new EngineStateMachine();

   /**
    * The starter of this engine. Never <code>null</code>.
    */
   private final EngineStarter _starter;

   /**
    * The stored servlet configuration object. Never <code>null</code>.
    */
   private final ServletConfig _servletConfig;

   /**
    * The API that this engine forwards requests to. Never <code>null</code>.
    */
   private final API _api;

   /**
    * Diagnostic context ID generator. Never <code>null</code>.
    */
   private ContextIDGenerator _contextIDGenerator;

   /**
    * The name of the API. Never <code>null</code>.
    */
   private String _apiName;

   /**
    * The manager for the runtime configuration file. Never <code>null</code>.
    */
   private final ConfigManager _configManager;

   /**
    * The manager for the calling conventions. This field can be and initially
    * is <code>null</code>. This field is initialized by
    * {@link #bootstrapAPI()}.
    */
   private CallingConventionManager _conventionManager;

   /**
    * Pattern which incoming diagnostic context identifiers must match. Can be
    * <code>null</code> in case no pattern has been specified. Initially this
    * field is indeed <code>null</code>.
    */
   private Pattern _contextIDPattern;

   /**
    * The SMD (Simple Method Description) of this API. This value is <code>null</code>
    * until the meta function <i>_SMD</i> is called.
    */
   private String _smd;

   /**
    * Constructs a new <code>Engine</code> object.
    *
    * @param config
    *    the {@link ServletConfig} object which contains build-time properties
    *    for this servlet, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>config == null</code>.
    *
    * @throws ServletException
    *    if the engine could not be constructed.
    */
   Engine(ServletConfig config)
   throws IllegalArgumentException, ServletException {

      // Check preconditions
      MandatoryArgumentChecker.check("config", config);

      // Construct the EngineStarter
      _starter = new EngineStarter(config);

      // Construct a configuration manager and store the servlet configuration
      _configManager = new ConfigManager(this, config);
      _servletConfig = config;

      // Proceed to first actual stage
      _stateMachine.setState(EngineState.BOOTSTRAPPING_FRAMEWORK);

      // Read configuration details
      _configManager.determineConfigFile();
      _configManager.readRuntimeProperties();
      if (!_configManager.propertiesRead()) {
         _stateMachine.setState(EngineState.FRAMEWORK_BOOTSTRAP_FAILED);
         throw new ServletException();
      }

      // Log boot messages
      _starter.logBootMessages();

      // Construct and bootstrap the API
      _stateMachine.setState(EngineState.CONSTRUCTING_API);
      try {
         _api = _starter.constructAPI();
      } catch (ServletException se) {
         _stateMachine.setState(EngineState.API_CONSTRUCTION_FAILED);
         throw se;
      }
      boolean bootstrapped = bootstrapAPI();
      if (!bootstrapped) {
         throw new ServletException();
      }

      // Done bootstrapping the framework
      Log.log_3225(Library.getVersion());

      // Initialize the configuration manager
      _configManager.init();

      // Check post-conditions
      if (_api == null) {
         throw Utils.logProgrammingError("_api == null");
      } else if (_apiName == null) {
         throw Utils.logProgrammingError("_apiName == null");
      }
   }

   /**
    * Retrieves the container <code>ConfigManager</code>.
    *
    * @return
    *    the {@link ConfigManager}, never <code>null</code>.
    */
   ConfigManager getConfigManager() {
      return _configManager;
   }

   /**
    * Bootstraps the API. The following steps will be performed:
    *
    * <ul>
    *   <li>determine the API name;
    *   <li>load the Logdoc, if available;
    *   <li>bootstrap the API;
    *   <li>construct and bootstrap the calling conventions;
    *   <li>link the engine to the API;
    *   <li>construct and bootstrap a context ID generator;
    *   <li>perform JMX initialization.
    * </ul>
    *
    * @return
    *    <code>true</code> if the bootstrapping of the API succeeded,
    *    <code>false</code> if it failed.
    */
   private boolean bootstrapAPI() {

      // Proceed to next stage
      _stateMachine.setState(EngineState.BOOTSTRAPPING_API);

      // Make the API have a link to this Engine
      _api.setEngine(this);

      PropertyReader bootProps;
      try {

         // Determine the name of the API
         _apiName = _starter.determineAPIName();

         // Load the Logdoc if available
         _starter.loadLogdoc();

         // Actually bootstrap the API
         bootProps = _starter.bootstrap(_api);

         // Handle any failures
      } catch (ServletException se) {
         _stateMachine.setState(EngineState.API_BOOTSTRAP_FAILED);
         return false;
      }

      // Create the calling convention manager
      _conventionManager = new CallingConventionManager(_api);

      // Bootstrap the calling convention manager
      try {
         _conventionManager.bootstrap(bootProps);

         // Missing required property
      } catch (MissingRequiredPropertyException exception) {
         Log.log_3209(exception.getPropertyName(), exception.getDetail());
         return false;

         // Invalid property value
      } catch (InvalidPropertyValueException exception) {
         Log.log_3210(exception.getPropertyName(),
               exception.getPropertyValue(),
               exception.getReason());
         return false;

         // Other bootstrap error
      } catch (Throwable exception) {
         Log.log_3211(exception);
         return false;
      }

      // Construct a generator for diagnostic context IDs
      _contextIDGenerator = new ContextIDGenerator(_api.getName());
      try {
         _contextIDGenerator.bootstrap(bootProps);
      } catch (Exception exception) {
         return false;
      }

      // Perform JMX initialization if asked
      String enableJmx = _configManager.getRuntimeProperties().get(JMX_PROPERTY);
      if ("true".equals(enableJmx)) {
         _starter.registerMBean(_api);
      } else if (enableJmx != null && !enableJmx.equals("false")) {
         Log.log_3251(enableJmx);
         return false;
      }

      // Succeeded
      return true;
   }

   /**
    * Initializes the API using the current runtime settings. This method
    * should be called whenever the runtime properties changed.
    *
    * @return
    *    <code>true</code> if the initialization succeeded, otherwise
    *    <code>false</code>.
    */
   boolean initAPI() {

      _stateMachine.setState(EngineState.INITIALIZING_API);

      // Determine the locale for logging
      boolean localeInitialized = _configManager.determineLogLocale();
      if (!localeInitialized) {
         _stateMachine.setState(EngineState.API_INITIALIZATION_FAILED);
         return false;
      }

      // Set the log filter
      boolean logFilterSetOK = _configManager.determineLogFilter();
      if (! logFilterSetOK) {
         _stateMachine.setState(EngineState.API_INITIALIZATION_FAILED);
         return false;
      }

      // Check that the runtime properties were correct
      if (!_configManager.propertiesRead()) {
         _stateMachine.setState(EngineState.API_INITIALIZATION_FAILED);
         return false;
      }

      // Determine the current runtime properties
      PropertyReader properties = _configManager.getRuntimeProperties();

      // Determine at what level should the stack traces be displayed
      String stackTraceAtMessageLevel = properties.get(LOG_STACK_TRACE_AT_MESSAGE_LEVEL);
      if ("true".equals(stackTraceAtMessageLevel)) {
         org.znerd.logdoc.Library.setStackTraceAtMessageLevel(true);
      } else if ("false".equals(stackTraceAtMessageLevel)) {
         org.znerd.logdoc.Library.setStackTraceAtMessageLevel(false);
      } else if (stackTraceAtMessageLevel != null) {
         // XXX: Report this error in some way
         _stateMachine.setState(EngineState.API_INITIALIZATION_FAILED);
         return false;
      }

      boolean succeeded = false;

      try {

         // Determine filter for incoming diagnostic context IDs
         _contextIDPattern = determineContextIDPattern(properties);

         // Initialize the diagnostic context ID generator
         _contextIDGenerator.init(properties);

         // Initialize the API
         _api.init(properties);

         // Initialize the default calling convention for this API
         _conventionManager.init(properties);

         succeeded = true;

         // Missing required property
      } catch (MissingRequiredPropertyException exception) {
         Log.log_3411(exception.getPropertyName(), exception.getDetail());

         // Invalid property value
      } catch (InvalidPropertyValueException exception) {
         Log.log_3412(exception.getPropertyName(),
               exception.getPropertyValue(),
               exception.getReason());

         // Initialization of API failed for some other reason
      } catch (InitializationException exception) {
         Log.log_3413(exception);

         // Other error
      } catch (Throwable exception) {
         Log.log_3414(exception);

         // Always leave the object in a well-known state
      } finally {
         if (succeeded) {
            _stateMachine.setState(EngineState.READY);
         } else {
            _stateMachine.setState(EngineState.API_INITIALIZATION_FAILED);
         }
      }

      return succeeded;
   }

   /**
    * Determines the filter for diagnostic context identifiers.
    *
    * @param properties
    *    the runtime properties to retrieve information from, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the filter as a {@link Pattern} object, or <code>null</code> if no
    *    filter is specified.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>.
    *
    * @throws InvalidPropertyValueException
    *    if the value for the filter property is considered invalid.
    */
   private Pattern determineContextIDPattern(PropertyReader properties)
   throws IllegalArgumentException, InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties);

      // Determine pattern string
      // XXX: Store "org.xins.server.contextID.filter" in a constant
      String propName  = "org.xins.server.contextID.filter";
      String propValue = properties.get(propName);

      // If the property value is empty, then there is no pattern
      Pattern pattern;
      if (TextUtils.isEmpty(propValue)) {
         pattern = null;
         Log.log_3431();

         // Otherwise we must provide a Pattern instance
      } else {

         // Convert the string to a Pattern
         try {
            // XXX: Why is the pattern made case-insensitive?
            int mask = Perl5Compiler.READ_ONLY_MASK
            | Perl5Compiler.CASE_INSENSITIVE_MASK;
            pattern  = PATTERN_COMPILER.compile(propValue, mask);
            Log.log_3432(propValue);

            // Malformed pattern indicates an invalid value
         } catch (MalformedPatternException exception) {
            Log.log_3433(propValue);
            InvalidPropertyValueException ipve = new InvalidPropertyValueException(propName, propValue);
            ipve.initCause(exception);
            throw ipve;
         }
      }

      return pattern;
   }

   /**
    * Handles a request to this servlet (wrapper method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   void service(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

      // Set the correct character encoding for the request
      if (request.getCharacterEncoding() == null) {
         request.setCharacterEncoding("UTF-8");
      }

      // Associate the current diagnostic context identifier with this thread
      // This method will call NDC.push(contextID) if appropriate
      pushContextID(request);

      // Handle the request
      try {
         doService(request, response);

         // Catch and log all exceptions
      } catch (Throwable exception) {
         Log.log_3003(exception);

         // Finally always disassociate the diagnostic context identifier from
         // this thread
      } finally {
         NDC.pop();
         NDC.remove();
      }
   }

   /**
    * Determines the applicable diagnostic context identifier and then pushes
    * it to the stack, if appropriate. If the setting of context IDs is 
    * disabled altogether (see {@link ConfigManager#isPushContextID()},
    * this method will immediately return.
    *
    * <p>If the request specifies a valid diagnostic context
    * identifier, then that will be used. Otherwise a new one will be
    * generated.
    *
    * @param request
    *    the HTTP servlet request, should not be <code>null</code>.
    */
   private void pushContextID(HttpServletRequest request) {

      // Short-circuit if context IDs should be left alone
      if (! _configManager.isPushContextID()) {
         return;
      }

      // XXX: Store "_context" in a constant

      // Determine if the incoming ID is valid
      // and whether a new one should be generated
      String originalContextID = request.getParameter("_context");
      boolean originalInvalid, generateNew;
      if (TextUtils.isEmpty(originalContextID)) {
         originalInvalid = false;
         generateNew     = true;
      } else if (isValidContextID(originalContextID)) {
         originalInvalid = false;
         generateNew     = false;
      } else {
         originalInvalid = true;
         generateNew     = true;
      }

      // Determine the context ID to use
      String contextID = generateNew ? _contextIDGenerator.generate() : originalContextID;

      // Push the right context ID
      NDC.push(contextID);

      // Perform logging
      if (originalInvalid) {
         Log.log_3582(originalContextID);
      } else if (generateNew) {
         Log.log_3583();
      } else {
         Log.log_3581();
      }
   }

   /**
    * Determines if the specified incoming context identifier is considered
    * valid.
    *
    * @param contextID
    *    the incoming diagnostic context identifier, should not be
    *    <code>null</code>.
    *
    * @return
    *    <code>true</code> if <code>contextID</code> is considered acceptable,
    *    <code>false</code> if it is considered unacceptable.
    */
   private boolean isValidContextID(String contextID) {

      // If a filter is specified, validate that the ID matches it
      if (_contextIDPattern != null) {
         Perl5Matcher matcher = new Perl5Matcher();
         return matcher.matches(contextID, _contextIDPattern);

         // No filter is specified, everything is allowed
      } else {
         return true;
      }
   }

   /**
    * Handles a request to this servlet (implementation method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * <p>This method is called from the corresponding wrapper method,
    * {@link #service(HttpServletRequest,HttpServletResponse)}.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private void doService(HttpServletRequest  request, HttpServletResponse response)
   throws IOException {

      // Determine current time
      long start = System.currentTimeMillis();

      // Log that we have received an HTTP request
      String remoteIP    = request.getRemoteAddr();
      String method      = request.getMethod();
      String path        = request.getRequestURI();
      String queryString = request.getQueryString();
      Log.log_3521(remoteIP, method, path, queryString);

      // If the current state is not usable, then return an error immediately
      EngineState state = _stateMachine.getState();
      if (! state.allowsInvocations()) {
         handleUnusableState(state, request, response);

         // Support the HTTP method "OPTIONS"
      } else if ("OPTIONS".equals(method)) {
         if ("*".equals(path)) {
            handleOptions(null, request, response);
         } else {
            delegateToCC(start, request, response);
         }

         // The request should be handled by a calling convention
      } else {
         delegateToCC(start, request, response);
      }
   }

   /**
    * Handles an unprocessable request (low-level function). The response is
    * filled for the request.
    *
    * @param request
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @param response
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @param statusCode
    *    the HTTP status code to return.
    *
    * @param reason
    *    explanation, can be <code>null</code>.
    *
    * @param exception
    *    the exception thrown, can be <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private void handleUnprocessableRequest(HttpServletRequest  request,
         HttpServletResponse response,
         int                 statusCode,
         String              reason,
         Throwable           exception)
   throws IOException {

      // Log
      Log.log_3523(exception,
            request.getRemoteAddr(),
            request.getMethod(),
            request.getRequestURI(),
            request.getQueryString(),
            statusCode,
            reason);

      // Send the HTTP status code to the client
      response.sendError(statusCode);
   }

   /**
    * Handles a request that comes in while function invocations are currently
    * not allowed.
    *
    * @param state
    *    the current state, cannot be <code>null</code>.
    *
    * @param request
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @param response
    *    the HTTP response to fill, cannot be <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private void handleUnusableState(EngineState         state,
         HttpServletRequest  request,
         HttpServletResponse response)
   throws IOException {

      // Log and respond
      int statusCode = state.isError()
                     ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                     : HttpServletResponse.SC_SERVICE_UNAVAILABLE;
      String reason  = "XINS/Java Server Framework engine state \""
                     + state
                     + "\" does not allow incoming requests.";
      handleUnprocessableRequest(request, response, statusCode, reason, null);
   }

   /**
    * Delegates the specified incoming request to the appropriate
    * <code>CallingConvention</code>. The request may either be a function
    * invocation or an <em>OPTIONS</em> request.
    *
    * @param start
    *    timestamp indicating when the call was received by the framework, in
    *    milliseconds since the
    *    <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private void delegateToCC(long                start,
         HttpServletRequest  request,
         HttpServletResponse response)
   throws IOException {

      // Determine the calling convention to use
      CallingConvention cc = determineCC(request, response);

      // If it is null, then there was an error. This error will have been
      // handled completely, including logging and response output.
      if (cc != null) {

         // Handle OPTIONS calls separately
         String method = request.getMethod();
         if ("OPTIONS".equals(method)) {
            handleOptions(cc, request, response);

            // Non-OPTIONS requests are function invocations
         } else {
            invokeFunction(start, cc, request, response);
         }
      }
   }

   /**
    * Determines which calling convention should be used for the specified
    * request. In case of an error, an error response will be produced and
    * sent to the client.
    *
    * @param request
    *    the HTTP request for which to determine the calling convention to use
    *    cannot be <code>null</code>.
    *
    * @param response
    *    the HTTP response, cannot be <code>null</code>.
    *
    * @return
    *    the {@link CallingConvention} to use, or <code>null</code> if the
    *    calling convention to use could not be determined.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private final CallingConvention determineCC(HttpServletRequest  request,
         HttpServletResponse response)
   throws IOException {

      // Determine the calling convention; if an existing calling convention
      // is specified in the request, then use that, otherwise use the default
      // calling convention for this engine
      CallingConvention cc = null;
      try {
         cc = _conventionManager.getCallingConvention(request);

         // Only an InvalidRequestException is expected. If a different kind of
         // exception is received, then that is considered a programming error.
      } catch (Throwable exception) {
         int    statusCode;
         String reason;
         if (exception instanceof InvalidRequestException) {

            String method = request.getMethod();
            String ccName = request.getParameter(CallingConventionManager.CALLING_CONVENTION_PARAMETER);
            // Check if the method is known by at least one CC (otherwise 501)
            if (!_conventionManager.getSupportedMethods().contains(method)) {
               statusCode = HttpServletResponse.SC_NOT_IMPLEMENTED;
               reason = "The HTTP method \"" + method + "\" is not known by any of the usable calling conventions.";

               // Check if the method is known for the specified CC (otherwise 405)
            } else if (ccName != null &&
                  _conventionManager.getCallingConvention2(ccName) != null &&
                  !Arrays.asList(_conventionManager.getCallingConvention2(ccName).getSupportedMethods(request)).contains(method)) {
               statusCode = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
               reason = "The HTTP method \"" + method + "\" is not allowed for the calling convention \"" + ccName + "\".";
            } else {
               statusCode = HttpServletResponse.SC_BAD_REQUEST;
               reason     = "Unable to activate appropriate calling convention";
               String exceptionMessage = exception.getMessage();
               if (TextUtils.isEmpty(exceptionMessage)) {
                  reason += '.';
               } else {
                  reason += ": " + exceptionMessage;
               }
            }
         } else {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            reason     = "Internal error while trying to determine " + "appropriate calling convention.";
         }

         // Log and respond
         handleUnprocessableRequest(request, response, statusCode, reason, exception);
      }

      return cc;
   }

   /**
    * Invokes a function, using the specified calling convention to from an
    * HTTP request and to an HTTP response.
    *
    * @param start
    *    timestamp indicating when the call was received by the framework, in
    *    milliseconds since the
    *    <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
    *
    * @param cc
    *    the calling convention to use, cannot be <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @param httpResponse
    *    the HTTP response, cannot be <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private void invokeFunction(long                start,
         CallingConvention   cc,
         HttpServletRequest  httpRequest,
         HttpServletResponse httpResponse)
   throws IOException {

      // Convert the HTTP request to a XINS request
      FunctionRequest xinsRequest;
      try {
         xinsRequest = cc.convertRequest(httpRequest);

         // Only an InvalidRequestException or a FunctionNotSpecifiedException is
         // expected. If a different kind of exception is received, then that is
         // considered a programming error.
      } catch (Throwable exception) {

         int    statusCode;
         String reason;

         if (exception instanceof InvalidRequestException) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            reason     = "Calling convention \"" + cc.getClass().getName() + "\" cannot process the request";
            String exceptionMessage = exception.getMessage();
            if (! TextUtils.isEmpty(exceptionMessage)) {
               reason += ": " + exceptionMessage;
            } else {
               reason += '.';
            }
         } else if (exception instanceof FunctionNotSpecifiedException) {
            statusCode = HttpServletResponse.SC_NOT_FOUND;
            reason     = "Cannot determine which function to invoke.";
         } else {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            reason     = "Internal error.";
         }

         // Log and respond
         handleUnprocessableRequest(httpRequest, httpResponse, statusCode, reason, exception);

         return;
      }

      // Do not handle the call if the API is disabled
      if (_api.isDisabled() && !"_EnableAPI".equals(xinsRequest.getFunctionName())) {
         httpResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
         return;
      }

      // Call the function
      FunctionResult result;
      try {
         result = _api.handleCall(start, xinsRequest, httpRequest.getRemoteAddr(), cc);

         // The only expected exceptions are NoSuchFunctionException and
         // AccessDeniedException. Other exceptions are considered to indicate
         // a programming error.
      } catch (Throwable exception) {

         int    statusCode;
         String reason;

         // Access denied
         if (exception instanceof AccessDeniedException) {
            statusCode = HttpServletResponse.SC_FORBIDDEN;
            reason     = "Access is denied.";

            // No such function
         } else if (exception instanceof NoSuchFunctionException) {
            statusCode = HttpServletResponse.SC_NOT_FOUND;
            reason     = "The specified function \""
               + xinsRequest.getFunctionName()
               + "\" is unknown.";

            // Internal error
         } else {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            reason     = "Internal error while processing function call.";
         }

         // Log and respond
         handleUnprocessableRequest(httpRequest, httpResponse, statusCode, reason, exception);

         return;
      }

      // Shortcut for the _WSDL meta function
      if (xinsRequest.getFunctionName().equals("_WSDL")) {
         handleWsdlRequest(httpResponse);
         return;
      }

      // Shortcut for the _SMD meta function
      if (xinsRequest.getFunctionName().equals("_SMD")) {
         handleSmdRequest(httpRequest, httpResponse);
         return;
      }

      // Convert the XINS result to an HTTP response
      try {
         cc.convertResult(httpRequest, xinsRequest, httpResponse, result);

         // NOTE: If the convertResult method throws an exception, then it
         //       will have been logged within the CallingConvention class
         //       already.
      } catch (Throwable exception) {
         httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return;
      }
   }

   /**
    * Handles an <em>OPTIONS</em> request for a specific calling convention
    * or for the resource <code>*</code> if no calling convention is given.
    *
    * @param cc
    *    the calling convention, can be <code>null</code>. if no calling
    *    convention is specified all possible method names are returned.
    *
    * @param request
    *    the request, never <code>null</code>.
    *
    * @param response
    *    the response to fill, never <code>null</code>.
    *
    * @throws IOException
    *    in case of an I/O error.
    */
   private void handleOptions(CallingConvention   cc,
         HttpServletRequest  request,
         HttpServletResponse response)
   throws IOException {

      // Create the string with the supported HTTP methods
      String[] methods;
      if (cc != null) {
         methods = cc.getSupportedMethods(request);
      } else {
         Set supportedMethods = _conventionManager.getSupportedMethods();
         methods = (String[]) supportedMethods.toArray(new String[supportedMethods.size()]);
      }
      String methodsList = "OPTIONS";
      for (int i = 0; i < methods.length; i++) {

         methodsList += ", " + methods[i];
      }

      // Return the full response
      response.setStatus(HttpServletResponse.SC_OK);
      response.setHeader("Accept", methodsList);
      response.setContentLength(0);
   }

   /**
    * Destroys this servlet. A best attempt will be made to release all
    * resources.
    *
    * <p>After this method has finished, it will set the state to
    * <em>disposed</em>. In that state no more requests will be handled.
    */
   void destroy() {

      // Log: Shutting down XINS/Java Server Framework
      Log.log_3600();

      // Set the state temporarily to DISPOSING
      _stateMachine.setState(EngineState.DISPOSING);

      // Destroy the configuration manager
      if (_configManager != null) {
         try {
            _configManager.destroy();
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }
      }

      // Destroy the API
      if (_api != null) {
         try {
            _api.deinit();
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }
      }

      // Set the state to DISPOSED
      _stateMachine.setState(EngineState.DISPOSED);

      // Log: Shutdown completed
      Log.log_3602();
   }

   /**
    * Re-initializes the configuration file listener if there is no file
    * watcher; otherwise interrupts the file watcher.
    */
   void reloadPropertiesIfChanged() {
      _configManager.reloadPropertiesIfChanged();
   }

   /**
    * Returns the <code>ServletConfig</code> object which contains the
    * build-time properties for this servlet. The returned
    * {@link ServletConfig} object is the one that was passed to the
    * constructor.
    *
    * @return
    *    the {@link ServletConfig} object that was used to initialize this
    *    servlet, never <code>null</code>.
    */
   ServletConfig getServletConfig() {
      return _servletConfig;
   }

   /**
    * Gets the location of a file or a directory included in the WAR file.
    *
    * @param path
    *    the relative path in the WAR to locate the file or the directory.
    *
    * @return
    *    the String representation of the URL of the given path or <code>null</code>
    *    if the path cannot be found.
    */
   String getFileLocation(String path) {
      String baseURL = null;
      ServletConfig  config  = getServletConfig();
      ServletContext context = config.getServletContext();
      try {
         String realPath = context.getRealPath(path);
         if (realPath != null) {
            baseURL = new File(realPath).toURI().toURL().toExternalForm();
         } else {
            URL pathURL = context.getResource(path);
            if (pathURL == null) {
               pathURL = getClass().getResource(path);
            }
            if (pathURL != null) {
               baseURL = pathURL.toExternalForm();
            } else {
               Log.log_3517(path, null);
            }
         }
      } catch (MalformedURLException muex) {
         // Let the base URL be null
         Log.log_3517(path, muex.getMessage());
      }
      return baseURL;
   }

   /**
    * Gets the resource in the WAR file.
    *
    * @param path
    *    the path for the resource, cannot be <code>null</code> and should start with /.
    *
    * @return
    *    the InputStream to use to read this resource or <code>null</code> if
    *    the resource cannot be found.
    *
    * @throws IllegalArgumentException
    *    if <code>path == null</code> or if the path doesn't start with /.
    *
    * @since XINS 2.1.
    */
   InputStream getResourceAsStream(String path) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("path", path);
      if (!path.startsWith("/")) {
         throw new IllegalArgumentException("The path '" + path + "' should start with /.");
      }
      String resource = getFileLocation(path);
      if (resource != null) {
         try {
            return new URL(resource).openStream();
         } catch (IOException ioe) {
            // Fall through and return null
         }
      }
      return null;
   }

   /**
    * Handles the request for the _WSDL meta function.
    *
    * @param response
    *    the response to fill, never <code>null</code>.
    *
    * @throws IOException
    *    if the WSDL cannot be found in the WAR file.
    */
   private void handleWsdlRequest(HttpServletResponse response) throws IOException {
      String wsdlLocation = getFileLocation("/WEB-INF/" + _apiName + ".wsdl");
      if (wsdlLocation == null) {
         throw new FileNotFoundException("/WEB-INF/" + _apiName + ".wsdl not found.");
      }
      InputStream inputXSLT = new URL(wsdlLocation).openStream();
      String wsdlText = IOReader.readFully(inputXSLT);

      // Write the text to the output
      response.setContentType("text/xml");
      response.setStatus(HttpServletResponse.SC_OK);
      Writer outputResponse = response.getWriter();
      outputResponse.write(wsdlText);
      outputResponse.close();
   }

   /**
    * Handles the request for the _SMD meta function.
    *
    * @param request
    *    the request asking for the SMD, never <code>null</code>.
    *
    * @param response
    *    the response to fill, never <code>null</code>.
    *
    * @throws IOException
    *    if the SMD cannot be created or sent to the output stream.
    */
   private void handleSmdRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
      if (_smd == null) {
         try {
            _smd = createSMD(request);
         } catch (Exception ex) {
            throw new IOException(ex.getMessage());
         }
      }

      // Write the text to the output
      response.setContentType(JSONRPCCallingConvention.RESPONSE_CONTENT_TYPE);
      response.setStatus(HttpServletResponse.SC_OK);
      Writer outputResponse = response.getWriter();
      outputResponse.write(_smd);
      outputResponse.close();
   }

   /**
    * Creates the SMD for this API.
    * More info at http://dojo.jot.com/SMD and
    * http://manual.dojotoolkit.org/WikiHome/DojoDotBook/Book9.
    *
    * @param request
    *    the request asking for the SMD, never <code>null</code>.
    *
    * @return
    *    the String representation of the SMD JSON Object, never <code>null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification of the API cannot be found.
    *
    * @throws EntityNotFoundException
    *    if the specification of a function cannot be found.
    *
    * @throws JSONException
    *    if the JSON object cannot be created correctly.
    */
   private String createSMD(HttpServletRequest request)
   throws InvalidSpecificationException, EntityNotFoundException, JSONException {
      APISpec apiSpec = _api.getAPISpecification();
      JSONObject smdObject = new JSONObject();
      smdObject.put("SMDVersion", ".1");
      smdObject.put("objectName", _api.getName());
      smdObject.put("serviceType", "JSON-RPC");
      String requestURL = request.getRequestURI();
      if (requestURL.indexOf('?') != -1) {
         requestURL = requestURL.substring(0, requestURL.indexOf('?'));
      }
      smdObject.put("serviceURL", requestURL + "?_convention=_xins-jsonrpc");
      JSONArray methods = new JSONArray();
      Iterator itFunctions = _api.getFunctionList().iterator();
      while (itFunctions.hasNext()) {
         String nextFunction = ((Function) itFunctions.next()).getName();
         JSONObject functionObject = new JSONObject();
         functionObject.put("name", nextFunction);
         JSONArray inputParameters = new JSONArray();
         Map inputParamSpecs = apiSpec.getFunction(nextFunction).getInputParameters();
         Iterator itParamNames = inputParamSpecs.keySet().iterator();
         while (itParamNames.hasNext()) {
            String nextParam = (String) itParamNames.next();
            JSONObject paramObject = new JSONObject();
            paramObject.put("name", nextParam);
            inputParameters.put(paramObject);
         }
         functionObject.put("parameters",inputParameters);
         methods.put(functionObject);
      }
      smdObject.put("methods", methods);
      return smdObject.toString();
   }
}
