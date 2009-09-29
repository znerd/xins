/*
 * $Id: JSONCallingConventionTests.java,v 1.4 2007/09/18 11:20:52 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

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
import org.json.JSONObject;

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

/**
 * Tests for calling conventions.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 11:20:52 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class JSONCallingConventionTests extends TestCase {

   /**
    * Constructs a new <code>XMLCallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public JSONCallingConventionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(JSONCallingConventionTests.class);
   }

   /**
    * Test the JSON calling convention.
    */
   public void testJSONCallingConvention() throws Throwable {
      String randomLong = HexConverter.toHexString(CallingConventionTests.RANDOM.nextLong());
      String randomFive = randomLong.substring(0, 5);

      // Successful call
      postJSONRequest(randomFive, true);

      // Unsuccessful call
      postJSONRequest(randomFive, false);
   }

   /**
    * Posts JSON request.
    *
    * @param randomFive
    *    A randomly generated String.
    * @param success
    *    <code>true</code> if the expected result should be successful,
    *    <code>false</code> otherwise.
    *
    * @throws Throwable
    *    If anything goes wrong.
    */
   private void postJSONRequest(String randomFive, boolean success) throws Throwable {
      String jsonResult = callResultCode(randomFive);
      assertNotNull(jsonResult);
      assertTrue("Incorrect returned message: " + jsonResult, jsonResult.trim().startsWith("{"));
      JSONObject jsonObject = new JSONObject(jsonResult);
      Object outputText = jsonObject.opt("outputText");
      Object errorCode = jsonObject.opt("errorCode");
      if (success) {
         assertEquals("Incorrect result received: " + outputText, randomFive + " added.", (String) outputText);
         assertNull(errorCode);
      } else {
         assertNotNull(errorCode);
         assertEquals("Incorrect error code received: " + errorCode, "AlreadySet", (String) errorCode);
         assertNull(outputText);
         int count = jsonObject.getInt("count");
         assertTrue(count > 0);
      }
   }

   /**
    * Call the ResultCode function with the specified calling convention.
    * Parameters are pass in as URL parameters.
    *
    * @param inputText
    *    the value of the parameter to send as input.
    *
    * @return
    *    the data returned by the API.
    *
    * @throw Throwable
    *    if anything goes wrong.
    */
   private String callResultCode(String inputText) throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor(AllTests.url() + "allinone/ResultCode", 2000);
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("_convention", "_xins-json");
      params.set("useDefault", "false");
      params.set("inputText", inputText);
      params.set("output", "json");
      HTTPCallRequest request = new HTTPCallRequest(params);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      return result.getString();
   }
}
