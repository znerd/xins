/*
 * $Id: StartServer.java,v 1.13 2007/03/16 10:30:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.servlet.container.HTTPServletHandler;

/**
 * Starts the web server.
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/16 10:30:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class StartServer extends TestCase {

   /**
    * Constructs a new <code>StartServer</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public StartServer(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(StartServer.class);
   }

   public void testStartServer() throws Exception {

      // Determine on which server socket to listen
      int port = AllTests.port();

      // Start the web server
      // Start allinone API
      File xinsProps = new File(System.getProperty("user.dir"), "src/tests/xins.properties".replace('/', File.separatorChar));
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      AllTests.HTTP_SERVER = startServer("allinone", AllTests.port());

      // Start portal API
      System.setProperty("org.xins.server.config", "");
      startServer("portal", port + 1);
   }

   public static HTTPServletHandler startServer(String apiName, int port) throws Exception {
      String warLocation = "src/tests/build/webapps/" + apiName + "/" + apiName + ".war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);

      // Start the web server
      System.out.println("Starting web server on port " + port + '.');
      HTTPServletHandler servletHandler = new HTTPServletHandler(warFile, port, false);
      System.out.println("Web server started on port " + port + '.');
      return servletHandler;
   }
}
