/*
 * $Id: HTTPServletHandler.java,v 1.70 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.NullEnumeration;

import org.xins.common.Library;
import org.xins.common.Log;

/**
 * HTTP server used to invoke the XINS servlet.
 *
 * @version $Revision: 1.70 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class HTTPServletHandler {

   /**
    * The default port number is 8080.
    */
   public static final int DEFAULT_PORT_NUMBER = 8080;

   /**
    * The web server.
    */
   private ServerSocket _serverSocket;

   /**
    * The thread that waits for connections from the client.
    */
   private SocketAcceptor _acceptor;

   /**
    * Flag indicating if the server should wait for other connections or stop.
    */
   private boolean _running;

   /**
    * Mapping between the path and the servlet.
    */
   private Map _servlets = new HashMap();

   /**
    * Creates a new HTTPSevletHandler with no Servlet. Use the addServlet
    * methods to add the WAR files or the Servlets.
    *
    * @param port
    *    The port of the servlet server.
    *
    * @param daemon
    *    <code>true</code> if the thread listening to connection should be a
    *    daemon thread, <code>false</code> otherwise.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(int port, boolean daemon) throws IOException {

      // Configure log4j if not already done.
      Enumeration appenders = LogManager.getLoggerRepository().getRootLogger().getAllAppenders();
      if (appenders instanceof NullEnumeration) {
         configureLoggerFallback();
      }

      // Start the HTTP server.
      startServer(port, daemon);
   }

   /**
    * Creates a new <code>HTTPServletHandler</code>. This servlet handler
    * starts a web server on port 8080 and wait for calls from the
    * <code>XINSServiceCaller</code>.
    *
    * <p>Note that all the libraries used by this WAR file should already be
    * in the classpath.
    *
    * @param warFile
    *    the war file of the application to deploy, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(File warFile)
   throws ServletException, IOException {
      this(DEFAULT_PORT_NUMBER, true);
      addWAR(warFile, "/");
   }

   /**
    * Creates a new <code>HTTPSevletHandler</code>. This servlet handler
    * starts a web server on the specified port and waits for calls from the XINSServiceCaller.
    * Note that all the libraries used by this WAR file should already be in
    * the classpath.
    *
    * @param warFile
    *    the war file of the application to deploy, cannot be
    *    <code>null</code>.
    *
    * @param port
    *    The port of the servlet server.
    *
    * @param daemon
    *    <code>true</code> if the thread listening to connection should be a
    *    daemon thread, <code>false</code> otherwise.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(File warFile, int port, boolean daemon)
   throws ServletException, IOException {
      this(port, daemon);
      addWAR(warFile, "/");
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(String servletClassName) throws ServletException, IOException {
      this(DEFAULT_PORT_NUMBER, true);
      addServlet(servletClassName, "/");
   }

   /**
    * Creates a new HTTPSevletHandler. This Servlet handler starts a web server
    * and wait for calls from the XINSServiceCaller.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the servlet server.
    *
    * @param daemon
    *    <code>true</code> if the thread listening to connection should be a
    *    daemon thread, <code>false</code> otherwise.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletHandler(String servletClassName, int port, boolean daemon) throws ServletException, IOException {
      this(port, daemon);
      addServlet(servletClassName, "/");
   }

   /**
    * Initializes the logging subsystem with fallback default settings.
    */
   private static final void configureLoggerFallback() {
      Properties settings = new Properties();
      settings.setProperty("log4j.rootLogger",                                "ALL, console");
      settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
      settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%6c{1} %-6p %x %m%n");
      settings.setProperty("log4j.logger.org.xins.",                          "INFO");
      PropertyConfigurator.configure(settings);
   }

   /**
    * Adds a WAR file to the server.
    * The servlet with the virtual path "/" will be the default one.
    * Note that all the libraries used by this WAR file should already be in
    * the classpath.
    *
    * @param warFile
    *    The war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param virtualPath
    *    The virtual path of the HTTP server that links to this WAR file, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    */
   public void addWAR(File warFile, String virtualPath) throws ServletException {
      LocalServletHandler servlet = new LocalServletHandler(warFile);
      if (! virtualPath.endsWith("/")) {
         virtualPath += '/';
      }
      _servlets.put(virtualPath, servlet);
   }

   /**
    * Adds a new servlet.
    * The servlet with the virtual path "/" will be the default one.
    *
    * @param servletClassName
    *    The name of the servlet's class to load, cannot be <code>null</code>.
    *
    * @param virtualPath
    *    The virtual path of the HTTP server that links to this WAR file, cannot be <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    */
   public void addServlet(String servletClassName, String virtualPath) throws ServletException{
      LocalServletHandler servlet = new LocalServletHandler(servletClassName);
      if (! virtualPath.endsWith("/")) {
         virtualPath += '/';
      }
      _servlets.put(virtualPath, servlet);
   }

   /**
    * Remove a servlet from the server.
    *
    * @param virtualPath
    *    The virtual path of the servlet to remove, cannot be <code>null</code>.
    */
   public void removeServlet(String virtualPath) {
      if (! virtualPath.endsWith("/")) {
         virtualPath += '/';
      }
      LocalServletHandler servlet = (LocalServletHandler) _servlets.get(virtualPath);
      servlet.close();
      _servlets.remove(virtualPath);
   }

   /**
    * Starts the web server.
    *
    * @param port
    *    the port of the servlet server.
    *
    * @param daemon
    *    <code>true</code> if the thread listening to connection should be a
    *    daemon thread, <code>false</code> otherwise.
    *
    * @throws IOException
    *    if the web server cannot be started.
    */
   public void startServer(int port, boolean daemon) throws IOException {
      // Create the server socket
      _serverSocket = new ServerSocket(port, 5);
      _running = true;

      _acceptor = new SocketAcceptor(daemon);
      _acceptor.start();
   }

   /**
    * Returns the port the server is accepting connections on.
    *
    * @return
    *    the server socket, e.g. <code>8080</code>.
    *
    * @throws IllegalStateException
    *    if the port cannot be determined, for example because the server is
    *    not started.
    *
    * @since XINS 1.5.0
    */
   public int getPort() throws IllegalStateException {
      int port;
      try {
         port = _serverSocket.getLocalPort();
      } catch (NullPointerException exception) {
         port = -1;
      }

      if (port < 0) {
         throw new IllegalStateException("Unable to determine port.");
      }

      return port;
   }

   /**
    * Disposes the servlet and stops the web server.
    */
   public void close() {
      _running = false;
      Iterator itServlets = _servlets.values().iterator();
      while (itServlets.hasNext()) {
         LocalServletHandler servlet = (LocalServletHandler) itServlets.next();
         servlet.close();
      }
      try {
         _serverSocket.close();
      } catch (IOException ioe) {
         Log.log_1502(ioe);
      }
   }

   /**
    * Thread waiting for connection from the client.
    */
   private class SocketAcceptor extends Thread {

      /**
       * Create the thread.
       *
       * @param daemon
       *    <code>true</code> if the server should be a daemon thread,$
       *    <code>false</code> otherwise.
       */
      public SocketAcceptor(boolean daemon) {
         setDaemon(daemon);
         setName("XINS " + Library.getVersion() + " Servlet container.");
      }

      /**
       * Executes the thread.
       */
      public void run() {
         Log.log_1500(_serverSocket.getLocalPort());
         try {
            while (_running) {
               // Wait for a connection
               Socket clientSocket = _serverSocket.accept();
               HTTPQueryHandler queryHandler = new HTTPQueryHandler(clientSocket, _servlets);
               queryHandler.start();
            }
         } catch (SocketException ie) {
            // fall through
         } catch (IOException ioe) {
            Log.log_1501(ioe);
         }
      }
   }
}
