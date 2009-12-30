/*
 * $Id: HTTPServletStarter.java,v 1.26 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.lang.reflect.Constructor;
import javax.swing.JFrame;

/**
 * HTTP Server used to invoke the XINS Servlet.
 *
 * @version $Revision: 1.26 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class HTTPServletStarter {

   /**
    * The default port number.
    */
   public static final int DEFAULT_PORT_NUMBER = 8080;

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the default port, as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    the WAR file of the application to deploy, cannot be
    *    <code>null</code>.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   // Exception is thrown as ServletException is not in the classpath
   public HTTPServletStarter(File warFile)
   throws Exception {
      this(warFile, DEFAULT_PORT_NUMBER, true);
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the specified port, as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    the WAR file of the application to deploy, cannot be
    *    <code>null</code>.
    *
    * @param port
    *    the port to run the web server on.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(File warFile, int port)
   throws Exception {
      this(warFile, port, true);
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the specified port, optionally as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    The war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the web server, cannot be <code>null</code>.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a
    *    deamon thread, <code>false</code> otherwise.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(File warFile, int port, boolean deamon)
   throws Exception {
      this(warFile, port, deamon, ServletClassLoader.USE_WAR_EXTERNAL_LIB);
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified WAR
    * file, on the specified port, optionally as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param warFile
    *    The war file of the application to deploy, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the web server, cannot be <code>null</code>.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a
    *    deamon thread, <code>false</code> otherwise.
    *
    * @param loaderMode
    *    the way the ClassLoader should locate and load the classes,
    *    see {@link ServletClassLoader}.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    *
    * @since XINS 2.1
    */
   public HTTPServletStarter(File warFile, int port, boolean deamon, int loaderMode)
   throws Exception {

      // Create the servlet
      ClassLoader loader = ServletClassLoader.getServletClassLoader(warFile, loaderMode);

      Class[] constClasses = {File.class, Integer.TYPE, Boolean.TYPE};
      Object[] constArgs = {warFile, new Integer(port), deamon ? Boolean.TRUE : Boolean.FALSE};
      try {
         Class delegate = loader.loadClass("org.xins.common.servlet.container.HTTPServletHandler");
         Constructor constructor  = delegate.getConstructor(constClasses);
         constructor.newInstance(constArgs);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Creates a new <code>HTTPServletStarter</code> for the specified servlet
    * class, on the specified port, optionally as a daemon thread.
    *
    * <p>A listener is started on the port immediately.
    *
    * @param servletClassName
    *    The name of the servlet to load, cannot be <code>null</code>.
    *
    * @param port
    *    The port of the web server, cannot be <code>null</code>.
    *
    * @param deamon
    *    <code>true</code> if the thread listening to connection should be a
    *    deamon thread, <code>false</code> otherwise.
    *
    * @throws ServletException
    *    if the servlet cannot be initialized.
    *
    * @throws IOException
    *    if the servlet container cannot be started.
    */
   public HTTPServletStarter(String servletClassName, int port, boolean deamon)
   throws Exception {

      // Create the servlet
      Class[] constClasses = {String.class, Integer.TYPE, Boolean.TYPE};
      Object[] constArgs = {servletClassName, new Integer(port), Boolean.valueOf(deamon)};
      try {
         Class delegate = getClass().getClassLoader().loadClass("org.xins.common.servlet.container.HTTPServletHandler");
         Constructor constructor  = delegate.getConstructor(constClasses);
         constructor.newInstance(constArgs);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Starts the Servlet container for the specific API.
    *
    * @param args
    *    The command line arguments, the first argument should be the location
    *    of the WAR file or the name of the class of the servlet to load,
    *    the optional second argument is the port number.
    *    If no port number is specified, 8080 is used as default.
    */
   public static void main(String[] args) {

      CommandLineArguments cmdArgs = new CommandLineArguments(args);
      if (cmdArgs.getPort() == -1) {
         try {
            ClassLoader loader = ServletClassLoader.getServletClassLoader(cmdArgs.getWarFile(), cmdArgs.getLoaderMode());
            loader.loadClass("org.xins.common.spec.SpecGUI").newInstance();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      } else {
         if (cmdArgs.showGUI()) {
            JFrame apiFrame = new JFrame();
            new ConsoleGUI(apiFrame, cmdArgs);
            apiFrame.setVisible(true);
         }
         try {
            // Starts the server and wait for connections
            new HTTPServletStarter(cmdArgs.getWarFile(), cmdArgs.getPort(), false, cmdArgs.getLoaderMode());
         } catch (Exception ioe) {
            ioe.printStackTrace();
         }
      }
   }
}
