/*
 * $Id: FormatExceptionTests.java,v 1.8 2007/03/16 10:30:41 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.FormatException;

/**
 * Tests for class <code>FormatException</code>.
 *
 * @version $Revision: 1.8 $ $Date: 2007/03/16 10:30:41 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class FormatExceptionTests extends TestCase {

   /**
    * Constructs a new <code>FormatExceptionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public FormatExceptionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(FormatExceptionTests.class);
   }

   public void testFormatException() throws Throwable {
      try {
         new FormatException(null, "");
         fail("Expected FormatException(null, <irrelevant>) to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      String string = "Bla";
      String reason = null;
      FormatException f = new FormatException(string, reason);
      assertEquals(string, f.getString());
      assertEquals(reason, f.getReason());

      reason = "Something";
      f      = new FormatException(string, reason);
      assertEquals(string, f.getString());
      assertEquals(reason, f.getReason());
   }
}
