/*
 * $Id: APIServletTests.java,v 1.13 2007/03/16 10:30:42 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.ProgrammingException;
import org.xins.server.APIServlet;

/**
 * Tests for class <code>APIServlet</code>.
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/16 10:30:42 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class APIServletTests extends TestCase {

   /**
    * Constructs a new <code>APIServletTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public APIServletTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(APIServletTests.class);
   }

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   public void testAPIServlet() throws Throwable {

      APIServlet servlet = new APIServlet();

      assertTrue("Expected getServletConfig() to return null, initially.", servlet.getServletConfig() == null);

      String servletInfo = servlet.getServletInfo();
      assertNotNull(servletInfo);
      assertTrue(servletInfo.indexOf("XINS") > -1);

      try {
         servlet.init(null);
         fail("Expected APIServlet.init(null) to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
         String msg = exception.getMessage();
         assertNotNull(msg);
         assertTrue("Expected exception message (" + msg + ") to contain \"null\".",   msg.indexOf("null")   > -1);
      }

      TestServletConfig config = new TestServletConfig();
      try {
         servlet.init(config);
         fail("Expected APIServlet.init(ServletConfig) to throw an IllegalArgumentException if ServletConfig.getServletContext() == null.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
         String msg = exception.getMessage();
         assertNotNull(msg);
         assertTrue("Expected exception message (" + msg + ") to contain \"null\".",  msg.indexOf("null")  > -1);
         assertTrue("Expected exception message (" + msg + ") to contain \"onfig\".", msg.indexOf("onfig") > -1);
      }

      TestServletContext context = new TestServletContext();
      context._major = 2;
      context._minor = 0;
      config  = new TestServletConfig();
      config._context = context;
      servlet.init(config);
      // TODO: Test that state indicates failure

      context._serverInfo = getClass().getName();
      try {
         servlet.init(config);
      } catch (ServletException exception) {
         // as expected
      }

      // TODO
   }

   private class TestServletConfig
   extends Object
   implements ServletConfig {

      public ServletContext _context;

      public String getServletName() {
         return "servlet 1";
      }

      public ServletContext getServletContext() {
         return _context;
      }

      public String getInitParameter(String name) {
         return null;
      }

      public Enumeration getInitParameterNames() {
         return null;
      }
   }

   private class TestServletContext
   extends Object
   implements ServletContext {

      public int _major = 2;
      public int _minor = 4;
      public String _serverInfo;

      public TestServletContext() {
         // TODO
      }

      public ServletContext getContext(String uripath) {
         return null;
      }

      public int getMajorVersion() {
         return _major;
      }

      public int getMinorVersion() {
         return _minor;
      }

      public String getMimeType(String file) {
         return null;
      }

      public Set getResourcePaths(String path) {
         return null;
      }

      public URL getResource(String path)
      throws MalformedURLException {
         return null;
      }

      public InputStream getResourceAsStream(String path) {
         return null;
      }

      public RequestDispatcher getRequestDispatcher(String path) {
         return null;
      }

      public RequestDispatcher getNamedDispatcher(String name) {
         return null;
      }

      public Servlet getServlet(String name) {
         return null;
      }

      public Enumeration getServlets() {
         return null;
      }

      public Enumeration getServletNames() {
         return null;
      }

      public void log(String msg) {
         // empty
      }

      public void log(Exception exception, String msg) {
         // empty
      }

      public void log(String message, Throwable throwable) {
         // empty
      }

      public String getRealPath(String path) {
         return null;
      }

      public String getServerInfo() {
         return _serverInfo;
      }

      public String getInitParameter(String name) {
         return null;
      }

      public Enumeration getInitParameterNames() {
         return null;
      }

      public Object getAttribute(String name) {
         return null;
      }

      public Enumeration getAttributeNames() {
         return null;
      }

      public void setAttribute(String name, Object object) {
         // empty
      }

      public void removeAttribute(String name) {
         // empty
      }

      public String getServletContextName() {
         return "context 1";
      }
   }
}
