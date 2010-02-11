/*
 * $Id: CAPITests.java,v 1.17 2007/03/16 10:30:27 agoubard Exp $
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

import org.xins.client.UnsuccessfulXINSCallException;
import org.xins.client.XINSCallConfig;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.service.*;

import org.xins.tests.AllTests;

/**
 * Tests the generated <em>allinone</em> CAPI class, other than calling the
 * actual functions.
 *
 * @version $Revision: 1.17 $ $Date: 2007/03/16 10:30:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class CAPITests extends TestCase {

   /**
    * Constructs a new <code>CAPITests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CAPITests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(CAPITests.class);
   }

   public void testCAPIConstruction_XINS_1_0() throws Exception {

      TargetDescriptor td;
      CAPI capi;

      // Pass null to constructor (should fail)
      try {
         new CAPI((Descriptor) null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { }

      // Pass URL with unsupported protocol
      td = new TargetDescriptor("bla://www.xins.org/");
      try {
         new CAPI(td);
         fail("Expected UnsupportedProtocolException.");
      } catch (UnsupportedProtocolException exception) {
         assertEquals(td, exception.getTargetDescriptor());
      }

      // Pass URL with supported protocol
      td = new TargetDescriptor("http://www.xins.org/");
      capi = new CAPI(td);
      assertNotNull(capi.getXINSCallConfig());
      assertNotNull(capi.getXINSVersion());
   }

   public void testCompatibility() throws Exception {

      // This test does not work on Java 1.3, because XINS 1.1.0 generates
      // Java 1.4-specific bytecode.
      if (Utils.getJavaVersion() < 1.4) {
         // TODO: Log warning
         return;
      }

      // Add the servlet
      AllTests.HTTP_SERVER.addServlet("org.xins.tests.client.MyProjectServlet", "/myproject");

      try {
         TargetDescriptor descriptor = new TargetDescriptor(AllTests.url() + "myproject");
         com.mycompany.myproject.capi.CAPI capi = new com.mycompany.myproject.capi.CAPI(descriptor);
         capi.callMyFunction(com.mycompany.myproject.types.Gender.MALE, "Bnd");
         fail("callMyFunction succeeded even with a invalid name");
      } catch (UnsuccessfulXINSCallException ex) {
         assertEquals("NoVowel", ex.getErrorCode());
      }

      // Remove the servlet
      AllTests.HTTP_SERVER.removeServlet("/myproject");
   }
}
