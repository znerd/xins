/*
 * $Id: ExceptionUtilsTests.java,v 1.14 2007/06/07 08:27:52 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.logdoc.ExceptionUtils;

/**
 * Tests for class <code>ExceptionUtils</code>
 *
 * @version $Revision: 1.14 $ $Date: 2007/06/07 08:27:52 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ExceptionUtilsTests extends TestCase {

   /**
    * Constructs a new <code>ExceptionUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ExceptionUtilsTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ExceptionUtilsTests.class);
   }

   /**
    * Tests the constructor.
    */
   public void testGetRootCause() throws Throwable {

      // Test with null argument
      Throwable cause = ExceptionUtils.getRootCause(null);
      assertNull(cause);

      // Test with IOException with no defined cause
      Exception ex = new Exception();
      cause = ExceptionUtils.getRootCause(ex);
      assertEquals(ex, cause);

      // Test 2 levels
      Exception ex2 = new Exception();
      ExceptionUtils.setCause(ex2, ex);
      cause = ExceptionUtils.getRootCause(ex2);
      assertEquals(ex, cause);

      // Test 3 levels
      Exception ex3 = new Exception();
      ExceptionUtils.setCause(ex3, ex2);
      cause = ExceptionUtils.getRootCause(ex3);
      assertEquals(ex, cause);

      // Test IllegalStateException
      try {
         ExceptionUtils.setCause(ex3, ex2);
         fail("Expected IllegalStateException.");
      } catch (IllegalStateException illegalState) {
         // as expected
      }
   }
}
