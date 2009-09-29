/*
 * $Id: InvalidResponseTests.java,v 1.23 2007/09/18 11:21:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import com.mycompany.allinone.capi.*;
import com.mycompany.allinone.types.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.UnacceptableResultXINSCallException;
import org.xins.client.UnacceptableErrorCodeXINSCallException;
import org.xins.client.UnsuccessfulXINSCallException;
import org.xins.common.service.*;
import org.xins.common.types.standard.Date;
import org.xins.common.types.standard.Timestamp;

import org.xins.tests.AllTests;

/**
 * Tests the CAPI when it receives invalid result.
 *
 * @version $Revision: 1.23 $ $Date: 2007/09/18 11:21:06 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class InvalidResponseTests extends TestCase {

   /**
    * The <code>CAPI</code> object used to call the API. This field is
    * initialized by {@link #setUp()}.
    */
   private CAPI _capi;

   /**
    * Constructs a new <code>InvalidResponseTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InvalidResponseTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(InvalidResponseTests.class);
   }

   public void setUp() throws Exception {
      AllTests.HTTP_SERVER.addServlet("org.xins.tests.client.InvalidResponseServlet", "/invalid");
      TargetDescriptor target = new TargetDescriptor(AllTests.url() + "invalid", 5000, 1000, 4000);
      _capi = new CAPI(target);
   }

   public void testMissingParameter() throws Exception {
      try {
         SimpleTypesResult result = _capi.callSimpleTypes(null, (byte)8, null, 65, 88L, 72.5f, new Double(37.2),
            "test", null, null, Date.fromStringForRequired("20041213"), Timestamp.fromStringForOptional("20041225153222"), null);
         fail("Expected UnacceptableResultXINSCallException.");
      } catch (UnacceptableResultXINSCallException exception) {
         // as expected
      } catch (Exception exception) {
         exception.printStackTrace();
         fail("Expected UnacceptableResultXINSCallException instead of " + exception.getClass().getName() + '.');
      }
      try {
         DefinedTypesResult result = _capi.callDefinedTypes("127.0.0.1", Salutation.LADY, (byte) 20, null, null);
         fail("Expected UnacceptableResultXINSCallException.");
      } catch (UnacceptableResultXINSCallException exception) {
         // as expected
      } catch (Exception exception) {
         exception.printStackTrace();
         fail("Expected UnacceptableResultXINSCallException instead of " + exception.getClass().getName() + '.');
      }
      try {
         RuntimePropsResult result = _capi.callRuntimeProps(0.0f);
         fail("Expected UnsuccessfulXINSCallException.");
      } catch (UnsuccessfulXINSCallException exception) {
         // as expected
         assertEquals("_InvalidRequest", exception.getErrorCode());
      } catch (Exception exception) {
         exception.printStackTrace();
         fail("Expected UnsuccessfulXINSCallException instead of " + exception.getClass().getName() + '.');
      }
   }

   public void testInvalidErrorCode() throws Exception {
      try {
         _capi.callResultCode(false, "hello");
         fail("Expected UnacceptableErrorCodeXINSCallException");
      } catch (UnacceptableErrorCodeXINSCallException exception) {
         // as expected
      } catch (Exception exception) {
         exception.printStackTrace();
         fail("The result is invalid, the function should throw an UnacceptableErrorCodeXINSCallException exception");
      }
   }

   /**
    * Tests that the required returned attribute are checked.
    */
   public void testInvalidDataSection() throws Exception {
      try {
         _capi.callDataSection("hello");
         fail("Expected UnacceptableResultXINSCallException");
      } catch (UnacceptableResultXINSCallException exception) {
         // as expected
      } catch (Exception exception) {
         exception.printStackTrace();
         fail("The result is invalid, the function should throw an UnacceptableResultXINSCallException exception");
      }
   }

   /**
    * Tests that the returned attribute types are checked.
    */
   public void testInvalidDataSection2() throws Exception {
      try {
         _capi.callDataSection2("hello");
         fail("Expected UnacceptableResultXINSCallException");
      } catch (UnacceptableResultXINSCallException exception) {
         // as expected
      } catch (Exception exception) {
         exception.printStackTrace();
         fail("The result is invalid, the function should throw an UnacceptableResultXINSCallException exception");
      }
   }

   public void tearDown() throws Exception {
      AllTests.HTTP_SERVER.removeServlet("/invalid");
   }
}
