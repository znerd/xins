/*
 * $Id: InvalidRequestTests.java,v 1.14 2007/03/16 10:30:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.XINSCallRequest;
import org.xins.client.XINSServiceCaller;
import org.xins.client.InvalidRequestException;
import org.xins.common.service.TargetDescriptor;

import org.xins.tests.AllTests;

/**
 * Tests the <code>XINSServiceCaller</code> when it receives an invalid
 * request.
 *
 * @version $Revision: 1.14 $ $Date: 2007/03/16 10:30:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class InvalidRequestTests extends TestCase {

   /**
    * The <code>XINSServiceCaller</code> used to call the API. This field is
    * initialized by {@link #setUp()}.
    */
   private XINSServiceCaller _caller;

   /**
    * Constructs a new <code>InvalidRequestTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InvalidRequestTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(InvalidRequestTests.class);
   }

   public void setUp() throws Exception {
      String           url    = AllTests.url();
      TargetDescriptor target = new TargetDescriptor(url);
      _caller = new XINSServiceCaller(target);
   }

   public void testRequiredParams() throws Exception {

      // Make a call to the SimpleTypes function
      XINSCallRequest request = new XINSCallRequest("SimpleTypes");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      // Error code must be _InvalidRequest
      assertEquals("_InvalidRequest", exception.getErrorCode());

      // The names of all parameters should be in the exception message
      String[] params = {
         "inputByte", "inputInt", "inputLong", "inputFloat", "inputText"
      };
      String exDetail = exception.getDetail();
      for (int i = 0; i < params.length; i++) {
         String p     = params[i];
         String error = "Expected InvalidRequestException detail (\""
                      + exDetail
                      + "\") to contain a reference to missing required "
                      + "parameter \""
                      + p
                      + "\".";
         String find = "No value given for required parameter \""
                     + p
                     + "\".";
         assertTrue(error, exDetail.indexOf(find) >= 0);
      }
   }

   public void testTypedParams() throws Exception {

      // Make a call to the ParamComboNotAll function
      XINSCallRequest request = new XINSCallRequest("ParamComboNotAll");
      request.setParameter("param1", "a");
      request.setParameter("param2", "a");
      request.setParameter("param3", "a");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      // Error code must be _InvalidRequest
      assertEquals("_InvalidRequest", exception.getErrorCode());

      // The names of all parameters should be in the exception message
      String[] params = { "param1", "param2", "param3" };
      String exDetail = exception.getDetail();
      for (int i = 0; i < params.length; i++) {
         String p     = params[i];
         String error = "Expected InvalidRequestException detail (\""
                      + exDetail
                      + "\") to contain a reference to parameter \""
                      + p
                      + "\" which has an invalid value.";
         String find = "The value \"a\" for the parameter \""
                     + p
                     + "\" is considered invalid";
         assertTrue(error, exDetail.indexOf(find) >= 0);
      }
   }

   public void testParamCombo() throws Exception {

      // Make a call to the ParamComboNotAll function
      XINSCallRequest request = new XINSCallRequest("ParamComboNotAll");
      request.setParameter("param1", "1");
      request.setParameter("param2", "1");
      request.setParameter("param3", "1");
      request.setParameter("param4", "1");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      // Error code must be _InvalidRequest
      assertEquals("_InvalidRequest", exception.getErrorCode());

      // The names of all parameters should be in the exception message
      String exDetail = exception.getDetail();
      String find = "Violated param-combo constraint of type \"not-all\" on"
                  + " parameters"
                  + " \"param1\", \"param2\", \"param3\" and \"param4\".";
      String error = "Expected InvalidRequestException detail (\""
                   + exDetail
                   + "\") to contain the following string: "
                   + find;
      assertTrue(error, exDetail.indexOf(find) >= 0);
   }
}
