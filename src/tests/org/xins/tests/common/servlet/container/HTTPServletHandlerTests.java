/*
 * $Id: HTTPServletHandlerTests.java,v 1.2 2007/09/18 11:21:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.servlet.container;

import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.HexConverter;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.xins.tests.AllTests;
import org.xins.tests.server.HTTPCaller;
import org.xins.tests.server.HTTPCallerResult;

/**
 * Tests for calling conventions.
 *
 * @version $Revision: 1.2 $ $Date: 2007/09/18 11:21:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class HTTPServletHandlerTests extends TestCase {

   /**
    * The random number generator.
    */
   private final static Random RANDOM = new Random();

   /**
     * Constructs a new <code>HTTPServletHandlerTests</code> test suite with
     * the specified name. The name will be passed to the superconstructor.
     *
     *
     * @param name
     *    the name for this test suite.
     */
   public HTTPServletHandlerTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(HTTPServletHandlerTests.class);
   }

   /**
    * Tests that a HEAD request returns a correct Content-Length header, but
    * no actual content.
    */
   public void testContentLengthFromHEAD() throws Exception {

      String     queryString = "/?_convention=_xins-std&_function=Echo";
      String     host        = AllTests.host();
      int        port        = AllTests.port();
      Properties headers     = null;

      // Perform a GET
      String method = "GET";
      HTTPCallerResult result = HTTPCaller.call("1.1", host, port, method, queryString, headers);

      // Content-length header should be set and correct
      List lengthHeaders = result.getHeaderValues("content-length");
      assertEquals("Expected GET request to return 1 \"Content-Length\" header. Instead it returned " + lengthHeaders.size(), 1, lengthHeaders.size());
      int bodyLength = result.getBody().length();
      int lengthHeader = Integer.parseInt((String) lengthHeaders.get(0));
      assertEquals("Expected \"Content-Length\" header from GET request (" + lengthHeader + ") to match actual body length (" + bodyLength + ").", bodyLength, lengthHeader);

      // Perform a HEAD
      method = "HEAD";
      result = HTTPCaller.call("1.1", host, port, method, queryString, headers);

      // Content-length header should be set and correct
      lengthHeaders = result.getHeaderValues("content-length");
      assertEquals("Expected HEAD request to return 1 \"Content-Length\" header. Instead it returned " + lengthHeaders.size(), 1, lengthHeaders.size());
      assertEquals("Expected actual HEAD response length to be 0.", 0, result.getBody().length());
      lengthHeader = Integer.parseInt((String) lengthHeaders.get(0));
      assertEquals("Expected \"Content-Length\" header from HEAD request (" + lengthHeader + ") to match the one from GET (" + bodyLength + ").", bodyLength, lengthHeader);
   }

   public void testFileContentLength() throws Exception {

      String     fileName    = "src/tests/apis/allinone/spec/Age.typ";
      String     queryString = "/specs/Age.typ";
      String     host        = AllTests.host();
      int        port        = AllTests.port();
      Properties headers     = null;

      // Perform a GET
      String method = "GET";
      HTTPCallerResult result = HTTPCaller.call("1.1", host, port, method, queryString, headers);

      // Status should be 200 OK
      assertEquals("Expected 200 OK in response to HTTP/1.1 GET request.", "200 OK", result.getStatus());

      // Content-length header should be set and correct
      List lengthHeaders = result.getHeaderValues("content-length");
      assertEquals("Expected GET request to return 1 \"Content-Length\" header instead of " + lengthHeaders.size() + '.', 1, lengthHeaders.size());
      long lengthHeader = Long.parseLong((String) lengthHeaders.get(0));
      long expectedLength = determineFileSize(fileName);;
      assertEquals("Expected \"Content-Length\" header for \"" + queryString + "\" to return " + expectedLength + '.', expectedLength, lengthHeader);
      long bodyLength = result.getBody().length();
      assertEquals("Expected \"Content-Length\" header from GET request (" + lengthHeader + ") to match actual body length (" + bodyLength + ").", bodyLength, lengthHeader);
   }

   private long determineFileSize(String fileName) throws Exception {
      File file = new File(fileName);
      if (!file.exists()) {
         throw new Exception("File \"" + file.getName() + "\" does not exist. Absolute: \"" + file.getAbsolutePath() + "\". Canonical: \"" + file.getCanonicalPath() + "\".");
      }
      return file.length();
   }

   public void testHTTP_1_0() throws Exception {

      String     queryString = "/?_convention=_xins-std&_function=Echo";
      String     host        = AllTests.host();
      int        port        = AllTests.port();
      Properties headers     = null;

      // Perform a GET
      String method = "GET";
      HTTPCallerResult result = HTTPCaller.call("1.0", host, port, method, queryString, headers);

      // Status should be 200 OK
      assertEquals("Expected 200 OK in response to HTTP/1.0 GET request.", "200 OK", result.getStatus());
   }
}
