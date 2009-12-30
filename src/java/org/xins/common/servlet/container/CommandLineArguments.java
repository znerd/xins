/*
 * $Id: CommandLineArguments.java,v 1.4 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class used to parse and get the command line arguments when the internal
 * Servlet container is started.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
class CommandLineArguments {

   /**
    * The port number to use for the Servlet container or -1 if the Servlet
    * container should not be started.
    */
   private int port;

   /**
    * The WAR file to execute.
    */
   private File warFile;

   /**
    * The ClassLoader mode with which the Servlet should be loaded.
    */
   private int loaderMode = -1;

   /**
    * <code>true</code> if the graphical user interface should be started,
    * <code>false</code> to run the Servlet in the console.
    */
   private boolean showGUI;

   /**
    * Parses the command line arguments.
    *
    * @param args
    *    the command line arguments as passed to the <code>main()</code> method.
    */
   CommandLineArguments(String[] args) {
      port = HTTPServletStarter.DEFAULT_PORT_NUMBER;
      showGUI = false;
      if (args.length == 1 && args[0].equals("-help")) {
         System.out.println("Usage: java [-Dorg.xins.server.config=<xins properties>] -jar <api name>.war [-port:<port number>] [-gui] [-war:<war file>] [-loader:<classloader mode>]");
         System.out.println("  if port number = -1, the Servlet is not started.");
         System.exit(0);
      }
      for (int i = 0; i < args.length; i++) {
         String arg = args[i];
         if (arg.startsWith("-port:") || arg.startsWith("-port=")) {
            try {
               port = Integer.parseInt(arg.substring(6));
            } catch (NumberFormatException nfe) {
               System.err.println("Warning: Incorrect port number \"" + args[1] +
                     "\", using " + HTTPServletStarter.DEFAULT_PORT_NUMBER + " as port number.");
            }
         } else if (arg.startsWith("-war:") || arg.startsWith("-war=")) {
            warFile = new File(arg.substring(5));
         } else if (arg.startsWith("-loader:") || arg.startsWith("-loader=")) {
            try {
               loaderMode = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
               System.err.println("Warning: Incorrect ClassLoader \"" + args[2] +
                     "\", using " + ServletClassLoader.USE_WAR_LIB + " as default.");
            }
         } else if (arg.equalsIgnoreCase("-gui")) {
            showGUI = true;

         // for backward compatibility
         } else if (arg.endsWith(".war") && warFile == null) {
            warFile = new File(arg);
         } else if (port == HTTPServletStarter.DEFAULT_PORT_NUMBER) {
            try {
               port = Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
            }
         }
      }

      // Detect the location of the WAR file if needed.
      if (warFile == null) {
         URL codeLocation = HTTPServletStarter.class.getProtectionDomain().getCodeSource().getLocation();
         System.out.println("No WAR file passed as argument, using: " + codeLocation);
         try {
            warFile = new File(new URI(codeLocation.toString()));
         } catch (URISyntaxException murlex) {
            murlex.printStackTrace();
         }
      }

      if (warFile == null || !warFile.exists()) {
         System.err.println("WAR file \"" + warFile + "\" not found.");
         System.exit(-1);
      }

      // Detect the ClassLoader mode
      if (loaderMode == -1) {
         String classPath = System.getProperty("java.class.path");
         if (classPath.indexOf("xins-common.jar") != -1 && classPath.indexOf("servlet.jar") != -1 &&
               classPath.indexOf("xins-server.jar") != -1 && classPath.indexOf("xmlenc.jar") != -1) {
            loaderMode = ServletClassLoader.USE_WAR_EXTERNAL_LIB;
         } else {
            loaderMode = ServletClassLoader.USE_WAR_LIB;
         }
      }
   }

   /**
    * Gets the port number specified. If no default port number is specified
    * return the default port number.
    *
    * @return
    *    the port number.
    */
   int getPort() {
      return port;
   }

   /**
    * Gets the location of the WAR file to execute.
    *
    * @return
    *    the WAR file or <code>null</code> if not found.
    */
   File getWarFile() {
      return warFile;
   }

   /**
    * Gets the class loader mode.
    *
    * @return
    *    the class loader mode to use to load the WAR classes.
    */
   int getLoaderMode() {
      return loaderMode;
   }

   /**
    * Indicates whether to run it in console mode or with the Swing user interface.
    *
    * @return
    *    <code>true</code> for the graphical user interface mode,
    *    <code>false</code> for the console mode.
    */
   boolean showGUI() {
      return showGUI;
   }
}