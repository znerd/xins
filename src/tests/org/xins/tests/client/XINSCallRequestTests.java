/*
 * $Id: XINSCallRequestTests.java,v 1.11 2007/03/16 10:30:30 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import org.xins.client.XINSCallRequest;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPMethod;

/**
 * Tests for class <code>XINSCallRequest</code>.
 *
 * @version $Revision: 1.11 $ $Date: 2007/03/16 10:30:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class XINSCallRequestTests extends TestCase {

   /**
    * Constructs a new <code>XINSCallRequestTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public XINSCallRequestTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(XINSCallRequestTests.class);
   }

   /**
    * Tests the behaviour of the <code>XINSCallRequestTests</code>
    * class.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testXINSCallRequest() throws Exception {

      // Test first constructor
      try {
         new XINSCallRequest(null, null);
         fail("XINSCallRequest(null, null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }
      try {
         new XINSCallRequest(null, new BasicPropertyReader());
         fail("XINSCallRequest(null, new BasicPropertyReader()) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

/* TODO: Consider adding the following tests. These tests will not succeed for
 * XINS 1.0, so it should be determined whether this breaks compatibility.

      try {
         new XINSCallRequest("", new BasicPropertyReader());
         fail("XINSCallRequest(\"\", new BasicPropertyReader()) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }
      try {
         new XINSCallRequest(" abcd", new BasicPropertyReader());
         fail("XINSCallRequest(\" abcd\", new BasicPropertyReader()) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }
      try {
         new XINSCallRequest("abcd ", new BasicPropertyReader());
         fail("XINSCallRequest(\"abcd \", new BasicPropertyReader()) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }
*/

      // TODO: Test second constructor
      // TODO: Test third constructor

      final int constructorCount = 3;
      XINSCallRequest[] r = new XINSCallRequest[constructorCount];

      String functionName = "SomeFunction";
      r[0] = new XINSCallRequest(functionName, null);
      r[1] = new XINSCallRequest(functionName, null, false);
      r[2] = new XINSCallRequest(functionName, null, false, null);

      for (int i = 0; i < constructorCount; i++) {
         assertEquals(functionName, r[i].getFunctionName());
         if (r[i].describe().indexOf(functionName) < 0) {
            fail("XINSCallRequest.describe() should return a string that contains the function name. Function name is: \"" + functionName + "\". Description is: \"" + r[i].describe() + "\".");
         }
      }

      String contextID = "f54b715f249bd02c";
      NDC.push("f54b715f249bd02c");
      BasicPropertyReader p = new BasicPropertyReader();
      p.set("channel",     "USR_REG_WEB_W");
      p.set("lineType",    "PSTN");
      p.set("postCode",    "1011PZ");
      p.set("houseNumber", "1");
      XINSCallRequest req = new XINSCallRequest("GetUpgradePlanList", p, false, HTTPMethod.POST);
      NDC.pop();
      NDC.remove();

      if (req.describe().indexOf(contextID) < 0) {
         fail("XINSCallRequest.describe() should return a string that contains the diagnostic context ID. Context ID is: \"" + contextID + "\". Description is: \"" + req.describe() + "\".");
      }
   }
}
