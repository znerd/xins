/*
 * $Id: ServletRequestPropertyReaderTests.java,v 1.10 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReaderConverter;
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.text.ParseException;

/**
 * Tests for class <code>ServletRequestPropertyReader</code>.
 *
 * @version $Revision: 1.10 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ServletRequestPropertyReaderTests extends TestCase {

   /**
    * Constructs a new <code>ServletRequestPropertyReaderTests</code> test
    * suite with the specified name. The name will be passed to the
    * superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ServletRequestPropertyReaderTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ServletRequestPropertyReaderTests.class);
   }

   public void testServletRequestPropertyReader() {

      ServletRequest r = null;
      try {
         new ServletRequestPropertyReader(r);
         fail("Expected NullPointerException.");
      } catch (NullPointerException exception) {
         // as expected
      }
   }

   public void testServletRequestPropertyReaderHTTPParameters()
	throws Exception {

		// Pass null to the constructor
      HttpServletRequest r = null;
      try {
         new ServletRequestPropertyReader(r);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      ServletRequestPropertyReader pr;
		Properties p;
		Iterator names;

		// Empty query string
      r = new ServletRequestMockup("");
      pr = new ServletRequestPropertyReader(r);
      assertEquals(0, pr.size());

		// Simple query string with only one parameter
      r = new ServletRequestMockup("name=value");
      pr = new ServletRequestPropertyReader(r);
      assertEquals(1, pr.size());
		names = pr.getNames();
		assertNotNull(names);
		assertTrue(names.hasNext());
		assertEquals("name", names.next());
		assertFalse(names.hasNext());
		assertEquals("value", pr.get("name"));

		// Simple query string with 2 parameters
      r = new ServletRequestMockup("a=1&b=2");
      pr = new ServletRequestPropertyReader(r);
		p = new Properties();
		p.put("a", "1");
		p.put("b", "2");
      assertEquals(p, PropertyReaderConverter.toProperties(pr));

		// Query string with some special situations
      r = new ServletRequestMockup("&a=1&b=2&c=&d&&a=1&e=%20+%C2%a9&&");
      pr = new ServletRequestPropertyReader(r);
		p = new Properties();
		p.put("a", "1");
		p.put("b", "2");
		p.put("c", "");
		p.put("d", "");
		p.put("e", "  \u00a9");
      assertEquals(p, PropertyReaderConverter.toProperties(pr));

		// Invalid URL-encoded stuff
		String[] invalid = new String[] {
			"a=%fg", "a=%gf", "a=%f", "a=%", "a=%g",
			"b=1&a=%fg", "b=1&a=%gf", "b=1&a=%f", "b=1&a=%",
			"b=1&b=2"
		};
		for (int i = 0; i < invalid.length; i++) {
			String qs = invalid[i];
			r = new ServletRequestMockup(qs);
		   try {
			   new ServletRequestPropertyReader(r);
				fail("Expected ParseException on query string \"" + qs + "\".");
			} catch (ParseException exception) {
				// as expected
			}
		}
   }

   private static class ServletRequestMockup
   extends Object
   implements HttpServletRequest {

      private ServletRequestMockup(String queryString) {
         _queryString = queryString;
      }

      private final String _queryString;

      public Object getAttribute(String name) {
         throw new UnsupportedOperationException();
      }

      public Enumeration getAttributeNames() {
         throw new UnsupportedOperationException();
      }

      public String getCharacterEncoding() {
         throw new UnsupportedOperationException();
      }

      public void setCharacterEncoding(String env)
      throws UnsupportedEncodingException {
         throw new UnsupportedOperationException();
      }

      public int getContentLength() {
         throw new UnsupportedOperationException();
      }

      public String getContentType() {
         throw new UnsupportedOperationException();
      }

      public ServletInputStream getInputStream()
      throws IOException {
         throw new UnsupportedOperationException();
      }

      public String getParameter(String name) {
         throw new UnsupportedOperationException();
      }

      public Enumeration getParameterNames() {
         throw new UnsupportedOperationException();
      }

      public String[] getParameterValues(String name) {
         throw new UnsupportedOperationException();
      }

      public Map getParameterMap() {
         throw new UnsupportedOperationException();
      }

      public String getProtocol() {
         throw new UnsupportedOperationException();
      }

      public String getScheme() {
         throw new UnsupportedOperationException();
      }

      public String getServerName() {
         throw new UnsupportedOperationException();
      }

      public int getServerPort() {
         throw new UnsupportedOperationException();
      }

      public BufferedReader getReader() throws IOException {
         throw new UnsupportedOperationException();
      }

      public String getRemoteAddr() {
         throw new UnsupportedOperationException();
      }

      public String getRemoteHost() {
         throw new UnsupportedOperationException();
      }

      public void setAttribute(String name, Object o) {
         throw new UnsupportedOperationException();
      }

      public void removeAttribute(String name) {
         throw new UnsupportedOperationException();
      }

      public Locale getLocale() {
         throw new UnsupportedOperationException();
      }

      public Enumeration getLocales() {
         throw new UnsupportedOperationException();
      }

      public boolean isSecure() {
         throw new UnsupportedOperationException();
      }

      public RequestDispatcher getRequestDispatcher(String path) {
         throw new UnsupportedOperationException();
      }

      public String getRealPath(String path) {
         throw new UnsupportedOperationException();
      }

      public int getRemotePort() {
         throw new UnsupportedOperationException();
      }

      public String getLocalName() {
         throw new UnsupportedOperationException();
      }

      public String getLocalAddr() {
         throw new UnsupportedOperationException();
      }

      public int getLocalPort() {
         throw new UnsupportedOperationException();
      }

      // From HttpServletRequest

      public String getAuthType() {
         throw new UnsupportedOperationException();
      }

      public Cookie[] getCookies() {
         throw new UnsupportedOperationException();
      }

      public long getDateHeader(String name) {
         throw new UnsupportedOperationException();
      }

      public String getHeader(String name) {
         throw new UnsupportedOperationException();
      }

      public Enumeration getHeaders(String name) {
         throw new UnsupportedOperationException();
      }

      public Enumeration getHeaderNames() {
         throw new UnsupportedOperationException();
      }

      public int getIntHeader(String name) {
         throw new UnsupportedOperationException();
      }

      public String getMethod() {
         throw new UnsupportedOperationException();
      }

      public String getPathInfo() {
         throw new UnsupportedOperationException();
      }

      public String getPathTranslated() {
         throw new UnsupportedOperationException();
      }

      public String getContextPath() {
         throw new UnsupportedOperationException();
      }

      public String getQueryString() {
         return _queryString;
      }

      public String getRemoteUser() {
         throw new UnsupportedOperationException();
      }

      public boolean isUserInRole(String role) {
         throw new UnsupportedOperationException();
      }

      public java.security.Principal getUserPrincipal() {
         throw new UnsupportedOperationException();
      }

      public String getRequestedSessionId() {
         throw new UnsupportedOperationException();
      }

      public String getRequestURI() {
         throw new UnsupportedOperationException();
      }

      public StringBuffer getRequestURL() {
         throw new UnsupportedOperationException();
      }

      public String getServletPath() {
         throw new UnsupportedOperationException();
      }

      public HttpSession getSession(boolean create) {
         throw new UnsupportedOperationException();
      }

      public HttpSession getSession() {
         throw new UnsupportedOperationException();
      }

      public boolean isRequestedSessionIdValid() {
         throw new UnsupportedOperationException();
      }

      public boolean isRequestedSessionIdFromCookie() {
         throw new UnsupportedOperationException();
      }

      public boolean isRequestedSessionIdFromURL() {
         throw new UnsupportedOperationException();
      }

      public boolean isRequestedSessionIdFromUrl() {
         throw new UnsupportedOperationException();
      }
   }
}
