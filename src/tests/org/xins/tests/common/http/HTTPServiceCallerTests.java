/*
 * $Id: HTTPServiceCallerTests.java,v 1.42 2008/10/23 17:53:40 agoubard Exp $
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.http;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallConfig;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPMethod;
import org.xins.common.http.HTTPServiceCaller;

import org.xins.common.service.CallException;
import org.xins.common.service.ConnectionRefusedCallException;
import org.xins.common.service.Descriptor;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.SocketTimeOutCallException;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.service.UnsupportedProtocolException;

/**
 * Tests for class <code>HTTPServiceCallerTests</code>.
 *
 * @version $Revision: 1.42 $ $Date: 2008/10/23 17:53:40 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class HTTPServiceCallerTests extends TestCase {

   /**
    * Total time-out to use for HTTP connections.
    */
   private final static int TOTAL_TO = 30000;

   /**
    * Connection time-out to use when making HTTP connections.
    */
   private final static int CONN_TO = 15000;

   /**
    * Socket time-out to use on HTTP connections.
    */
   private final static int SOCKET_TO = 15000;

   /**
    * Constructs a new <code>HTTPServiceCaller</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public HTTPServiceCallerTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(HTTPServiceCallerTests.class);
   }

   private static long checksum(String s) {
      CRC32 crc = new CRC32();
      try {
         byte[] bytes = s.getBytes("UTF-8");
         crc.update(bytes);
         return crc.getValue();
      } catch (UnsupportedEncodingException exception) {
         throw new Error(exception);
      }
   }

   public void testConstructor() throws Exception {

      TargetDescriptor descriptor;

      // One-argument constructor
      HTTPServiceCaller caller = new HTTPServiceCaller(null);
      assertEquals(null, caller.getDescriptor());

      try {
         descriptor = new TargetDescriptor("blah://www.google.com");
         new HTTPServiceCaller(descriptor);
         fail("The \"blah\" protocol should not be supported.");
      } catch (UnsupportedProtocolException upe) {
         // as expected
      }

      descriptor = new TargetDescriptor("http://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("https://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("hTTp://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("HTTPs://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("HTTP://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("https://sourceforge.net");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      // TODO: Add tests for 2-argument constructor
   }

   public void testAntURL() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET);
      Descriptor descriptor = new TargetDescriptor("http://ant.apache.org/manual/index.html", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      String text = result.getString();
      boolean correctContent = text.indexOf("Licensed to the Apache Software Foundation (ASF)") != -1;
      assertTrue("Unexpected HTML received.", correctContent);
   }

   /* Disabled as patterntest.php doesn't work anymore
   public void testPostParameters() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("pattern", "^([A-Za-z]([A-Za-z\\- ]{0,26}[A-Za-z])?)$");
      parameters.set("string", "Janwillem");
      parameters.set("submit", "submit");
      // XXX GET method doesn't work
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST, parameters);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      String text = result.getString();
      assertTrue("Incorect content.", text.indexOf("\"Janwillem\" <span style='color:blue'>matches</span>") != -1);
   }

    public void testGetParameters() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("pattern", "^([A-Za-z]([A-Za-z\\- ]{0,26}[A-Za-z])?)$");
      parameters.set("string", "Janwillem");
      parameters.set("submit", "submit");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      String text = result.getString();
      assertTrue("Incorect content.", text.indexOf("\"Janwillem\" <span style='color:blue'>matches</span>") != -1);
   }*/

   public void testWrongURL() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("hello", "world");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://ant.apache.org/manual/nOnExIsTeNt.html", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", result.getStatusCode(), 404);
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
   }

   public void testFailOverGet() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, null, false, null);
      TargetDescriptor failedTarget = new TargetDescriptor("http://anthony.xins.org", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor succeededTarget = new TargetDescriptor("http://ant.apache.org/manual/index.html", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      assertEquals("Incorrect succeeded target.", succeededTarget, result.getSucceededTarget());
      String text = result.getString();
      assertTrue("Incorrect content.", text.indexOf("Apache Ant User Manual") > 0);

      // Check that the request does not have side effect on the HTTPServiceCaller object
      assertEquals("Incorrect HTTP method.", HTTPMethod.POST, caller.getHTTPCallConfig().getMethod());
   }

   public void testFailOverPost() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST, null, true, null);
      TargetDescriptor failedTarget = new TargetDescriptor("http://anthony.xins.org", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor succeededTarget = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      assertEquals("Incorrect succeeded target.", succeededTarget, result.getSucceededTarget());
      String text = result.getString();
      assertTrue("Incorrect content.", text.indexOf("Pattern test form") != -1);
   }

   public void testSocketTimeOut() throws Exception {
      // Set socket time-out to 1 ms
      TargetDescriptor target = new TargetDescriptor("http://xins.sourceforge.net/", TOTAL_TO, CONN_TO, 1);
      HTTPServiceCaller caller = new HTTPServiceCaller(target);
      HTTPCallRequest request = new HTTPCallRequest();
      try {
         caller.call(request);
         fail("Expected SocketTimeOutCallException.");
      } catch (SocketTimeOutCallException exception) {
         // as expected

         // Test some aspects of the exception
         assertNull(exception.getNext());
         assertEquals(request, exception.getRequest());
         assertEquals(target,  exception.getTarget());
      }
   }

   public void testCallExceptionLinking() throws Exception {

      // Define targets
      TargetDescriptor target1 = new TargetDescriptor("http://127.0.0.1:5/", 7000, 5500, 5500);
      TargetDescriptor target2 = new TargetDescriptor("http://xins.sf.net/", 2000, 1500, 1);
      Descriptor members[] =  new Descriptor[] { target1, target2 };
      GroupDescriptor group = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, members);

      // Construct a caller and a request
      HTTPServiceCaller caller = new HTTPServiceCaller(group);
      HTTPCallRequest request = new HTTPCallRequest();

      // Perform the call
      CallException exception;
      try {
         caller.call(request);
         fail("Expected ConnectionRefusedCallException.");
         return;
      } catch (ConnectionRefusedCallException e) {
         exception = e;
      }

      // Test some aspects of the exception
      assertEquals(request, exception.getRequest());
      assertEquals(target1, exception.getTarget());

      // Test the next CallException
      CallException next = exception.getNext();
      assertNotNull(next);
      assertEquals(request, next.getRequest());
      assertEquals(target2, next.getTarget());
      assertTrue("Next exception is an instance of class " + next.getClass().getName() + " instead of " + SocketTimeOutCallException.class.getName(),
                 next instanceof SocketTimeOutCallException);

      // Test the exception message on the first exception
      String em1 = exception.getMessage();
      assertNotNull("Expected message for first exception to be not null.", em1);
      String start1 = "Connection refused in ";
      assertTrue("Expected first exception message \"" + em1 + "\" to start with \"" + start1 + '"', em1.startsWith(start1));

      // Test the exception message on the second exception
      String em2 = next.getMessage();
      assertNotNull("Expected message for second exception to be not null.", em2);
      String start2 = "Socket time-out in ";
      assertTrue("Expected second exception message \"" + em2 + "\" to start with \"" + start2 + '"', em2.startsWith(start2));

      // First exception message must contain second one
      assertTrue("Expected first exception message to contain second one. First one is: \"" + em1 + "\" and second one is \"" + em2 + '"', em1.indexOf(em2) >= 0);
   }
}
