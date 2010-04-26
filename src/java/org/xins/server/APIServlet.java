/*
 * $Id: APIServlet.java,v 1.286 2007/12/17 13:36:43 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;

/**
 * HTTP servlet that forwards requests to an <code>API</code>.
 *
 * <h3>HTTP status codes</h3>
 *
 * <p>This servlet supports various HTTP methods, depending on the calling
 * conventions. A request with an unsupported method makes this servlet
 * return the HTTP status code <code>405 Method Not Allowed</code>.
 *
 * <p>If no matching function is found, then this servlet returns HTTP status
 * code <code>404 Not Found</code>.
 *
 * <p>If the servlet is temporarily unavailable, then the HTTP status
 * <code>503 Service Unavailable</code> is returned.
 *
 * <p>If the servlet encountered an initialization error, then the HTTP status
 * code <code>500 Internal Server Error</code> is returned.
 *
 * <p>If the state is <em>ready</em> then the HTTP status code
 * <code>200 OK</code> is returned.
 *
 *
 * <h3>Initialization</h3>
 *
 * <p>When the servlet is initialized, it gathers configuration information
 * from different sources:
 *
 * <dl>
 *    <dt><strong>1. Build-time settings</strong></dt>
 *    <dd>The application package contains a <code>web.xml</code> file with
 *        build-time settings. Some of these settings are required in order
 *        for the XINS/Java Server Framework to start up, while others are
 *        optional. These build-time settings are passed to the servlet by the
 *        application server as a {@link ServletConfig} object. See
 *        {@link #init(ServletConfig)}.
 *        <br>The servlet configuration is the responsibility of the
 *        <em>assembler</em>.</dd>
 *
 *    <dt><strong>2. System properties</strong></dt>
 *    <dd>The location of the configuration file must be passed to the Java VM
 *        at startup, as a system property.
 *        <br>System properties are the responsibility of the
 *        <em>system administrator</em>.
 *        <br>Example:
 *        <br><code>java -Dorg.xins.server.config=`pwd`/config/xins.properties
 *        -jar orion.jar</code></dd>
 *
 *    <dt><strong>3. Configuration file</strong></dt>
 *    <dd>The configuration file should contain runtime configuration
 *        settings, like the settings for the logging subsystem.
 *        <br>Runtime properties are the responsibility of the
 *        <em>system administrator</em>.
 *        <br>Example contents for a configuration file:
 *        <blockquote><code>log4j.rootLogger=DEBUG, console
 *           <br>log4j.appender.console=org.apache.log4j.ConsoleAppender
 *           <br>log4j.appender.console.layout=org.apache.log4j.PatternLayout
 *           <br>log4j.appender.console.layout.ConversionPattern=%d %-5p [%c]
 *           %m%n</code></blockquote></dd>
 * </dl>
 *
 * @version $Revision: 1.286 $ $Date: 2007/12/17 13:36:43 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 *
 * @since XINS 1.0.0
 */
public class APIServlet extends HttpServlet {

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = 4002348764498221122L;

   /**
    * XINS server engine. Initially <code>null</code> but set to a
    * non-<code>null</code> value in the {@link #init(ServletConfig)} method.
    */
   private Engine _engine;

   /**
    * Allows the config manager to self-initialize.
    */
   static {
      ConfigManager.systemStartup();
   }

   /**
    * Constructs a new <code>APIServlet</code> object.
    */
   public APIServlet() {
      // empty
   }

   /**
    * Returns information about this servlet, as plain text.
    *
    * @return
    *    textual description of this servlet, not <code>null</code> and not an
    *    empty character string.
    */
   public String getServletInfo() {
      return "XINS/Java Server Framework " + Library.getVersion();
   }

   /**
    * Initializes this servlet using the specified configuration.
    *
    * @param config
    *    the {@link ServletConfig} object which contains bootstrap properties for
    *    this servlet, as specified by the <em>assembler</em>, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>config == null
    *    || config.{@link ServletConfig#getServletContext()} == null</code>.
    *
    * @throws ServletException
    *    if the servlet could not be initialized.
    */
   public void init(ServletConfig config)
   throws IllegalArgumentException, ServletException {

      // Check arguments
      MandatoryArgumentChecker.check("config", config);

      // Get the ServletContext
      ServletContext context = config.getServletContext();
      if (context == null) {
         String message = "config.getServletContext() == null";
         Log.log_3202(message);
         throw new IllegalArgumentException(message);
      }

      // Compare the expected with the implemented Java Servlet API version;
      // versions 2.2, 2.3, 2.4 and 2.5 are supported
      int major = context.getMajorVersion();
      int minor = context.getMinorVersion();
      if (major != 2 || minor < 2 || minor > 5) {
         String expected = "2.2/2.3/2.4/2.5";
         String actual   = major + "." + minor;
         Log.log_3203(actual, expected);
      }

      // Construct an engine
      try {
         _engine = new Engine(config);

      // Fail silently, so that the servlet container will not keep trying to
      // re-initialize this servlet (possibly on each call!)
      } catch (Throwable exception) {
         Log.log_3444(exception);
         return;
      }
   }

   /**
    * Returns the <code>ServletConfig</code> object which contains the
    * bootstrap properties for this servlet. The returned {@link ServletConfig}
    * object is the one passed to the {@link #init(ServletConfig)} method.
    *
    * @return
    *    the {@link ServletConfig} object that was used to initialize this
    *    servlet, or <code>null</code> if this servlet is not yet
    *    initialized.
    */
   public ServletConfig getServletConfig() {
      return (_engine == null) ? null : _engine.getServletConfig();
   }

   /**
    * Handles an HTTP request to this servlet. If any of the arguments is
    * <code>null</code>, then the behaviour of this method is undefined.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws NullPointerException
    *    if this servlet is yet uninitialized.
    *
    * @throws IOException
    *    if there is an error error writing to the response output stream.
    */
   public void service(HttpServletRequest  request,
                       HttpServletResponse response)
   throws NullPointerException,
          IOException {

      // Engine failed to initialize, return '500 Internal Server Error'
      if (_engine == null) {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return;
      }

      // Pass control to the Engine
      _engine.service(request, response);
   }

   /**
    * Destroys this servlet. A best attempt will be made to release all
    * resources.
    *
    * <p>After this method has finished, no more requests will be handled
    * successfully.
    */
   public void destroy() {
      if (_engine != null) {
         _engine.destroy();
         _engine = null;
      }
   }
}
