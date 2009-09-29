/*
 * $Id: StandardCallingConventionTests.java,v 1.4 2007/09/18 11:20:50 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.io.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.HexConverter;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.xins.tests.AllTests;

/**
 * Tests for calling conventions.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 11:20:50 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class StandardCallingConventionTests extends TestCase {

   /**
    * Constructs a new <code>StandardCallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public StandardCallingConventionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(StandardCallingConventionTests.class);
   }

   /**
    * Tests the standard calling convention is detected correctly.
    */
   public void testStandardCallingConvention1() throws Throwable {
      callResultCodeStandard(null);
   }

   /**
    * Tests the standard calling convention.
    */
   public void testStandardCallingConvention2() throws Throwable {
      callResultCodeStandard("_xins-std");
   }

   /**
    * Tests that when different parameter values are passed to the
    * _xins-std calling convention, it must return a 400 status code
    * (invalid HTTP request).
    */
   public void testStandardCallingConvention3() throws Throwable {
      CallingConventionTests.doTestMultipleParamValues("_xins-std");
   }

   /**
    * Calls the ResultCode function and expect the standard calling convention back.
    *
    * @param convention
    *    the name of the calling convention parameter, or <code>null</code>
    *    if no calling convention parameter should be sent.
    *
    * @throw Throwable
    *    if anything goes wrong.
    */
   public void callResultCodeStandard(String convention) throws Throwable {
      ElementParser parser = new ElementParser();
      String randomLong = HexConverter.toHexString(CallingConventionTests.RANDOM.nextLong());
      String randomFive = randomLong.substring(0, 5);

      String result1Data = CallingConventionTests.callResultCode(convention, randomFive);
      Element result1 = parser.parse(new StringReader(result1Data));
      assertNull("The method returned an error code for the first call: " + result1.getAttribute("errorcode"), result1.getAttribute("errorcode"));
      assertNull("The method returned a code attribute for the first call: " + result1.getAttribute("code"), result1.getAttribute("code"));
      assertNull("The method returned a success attribute for the first call: " + result1.getAttribute("success"), result1.getAttribute("success"));

      String result2Data = CallingConventionTests.callResultCode(convention, randomFive);
      Element result2 = parser.parse(new StringReader(result2Data));
      assertNotNull("The method did not return an error code for the second call.", result2.getAttribute("errorcode"));
      assertNull("The method returned a code attribute for the second call: " + result2.getAttribute("code"), result2.getAttribute("code"));
      assertNull("The method returned a success attribute for the second call: " + result2.getAttribute("success"), result2.getAttribute("success"));
   }
}
