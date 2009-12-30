/*
 * $Id: StandardCallingConvention.java,v 1.64 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Standard calling convention. The technical name for this calling convention
 * is <em>_xins-std</em>.
 *
 * @version $Revision: 1.64 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class StandardCallingConvention extends CallingConvention {

   /**
    * The response encoding format.
    */
   protected static final String RESPONSE_ENCODING = "UTF-8";

   /**
    * The content type of the HTTP response.
    */
   protected static final String RESPONSE_CONTENT_TYPE = "text/xml; charset=" + RESPONSE_ENCODING;

   /**
    * The API, or <code>null</code>.
    */
   private final API _api;

   /**
    * Creates a new <code>StandardCallingConvention</code> instance.
    */
   public StandardCallingConvention() {
      _api = null;
   }

   /**
    * Creates a new <code>StandardCallingConvention</code> instance, for the
    * specified API.
    *
    * @param api
    *    the API, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    *
    * @since XINS 3.0
    */
   public StandardCallingConvention(API api) throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("api", api);

      // Store the API
      _api = api;
   }

   /**
    * Checks if the specified request can be handled by this calling
    * convention.
    *
    * <p>This method will not throw any exception.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if this calling convention is <em>possibly</em>
    *    able to handle this request, or <code>false</code> if it
    *    <em>definitely</em> not able to handle this request.
    */
   protected boolean matches(HttpServletRequest httpRequest) {

      // If no _function parameter is specified, then there is no match
      return ! TextUtils.isEmpty(httpRequest.getParameter("_function"));
   }

   /**
    * Converts an HTTP request to a XINS request (implementation method). This
    * method should only be called from class CallingConvention. Only
    * then it is guaranteed that the <code>httpRequest</code> argument is not
    * <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, will not be <code>null</code>.
    *
    * @return
    *    the XINS request object, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException {

      // Parse the parameters in the HTTP request
      BasicPropertyReader params = gatherParams(httpRequest);

      // Remove all invalid parameters
      cleanUpParameters(params);

      // Determine function name
      String functionName = httpRequest.getParameter("_function");
      if (TextUtils.isEmpty(functionName)) {
         throw new FunctionNotSpecifiedException();
      }

      // Get data section
      String dataSectionValue = httpRequest.getParameter("_data");
      Element dataElement = null;
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         ElementParser parser = new ElementParser();

         // Parse the data section
         try {
            dataElement = parser.parse(new StringReader(dataSectionValue));

         // I/O error, should never happen on a StringReader
         } catch (IOException exception) {
            throw Utils.logProgrammingError(exception);
         // Parsing error
         } catch (ParseException exception) {
            String detail = "Cannot parse the data section.";
            throw new InvalidRequestException(detail, exception);
         }
      }

      // Construct the request object
      FunctionRequest xinsRequest = new FunctionRequest(functionName, params, dataElement);

      return xinsRequest;
   }

   @Override
   protected void convertResultImpl(HttpServletRequest  httpRequest,
                                    FunctionRequest     xinsRequest,
                                    HttpServletResponse httpResponse,
                                    FunctionResult      xinsResult)
   throws IOException {

      // Set the status code and the content type
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);

      // Determine the method
      String method = httpRequest.getMethod();

      // Try to get the appropriate function specification object
      FunctionSpec functionSpec = null;
      String       functionName = xinsRequest.getFunctionName();
      if (_api != null && functionName != null) {
         try {
            APISpec apiSpec = _api.getAPISpecification();
            if (apiSpec != null) {
               functionSpec = apiSpec.getFunction(functionName);
            }
         } catch (Throwable t) {
            Utils.logIgnoredException(t);
         }
      }

      // XXX: Temporarily disabled support for real XML parameter values,
      //      because the XINS Client Framework needs to add support as well.
      functionSpec = null;

      // Handle HEAD requests
      if ("HEAD".equals(method)) {
         StringWriter out = new StringWriter();
         CallResultOutputter.output(out, xinsResult, functionSpec);
         httpResponse.setContentLength(out.getBuffer().length());

      // Handle non-HEAD requests
      } else {
         Writer out = httpResponse.getWriter();
         CallResultOutputter.output(out, xinsResult, functionSpec);
         out.close();
      }
   }
}
