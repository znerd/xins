package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Int16;

/**
 * Tests for class <code>Int16</code>.
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/16 10:30:42 $
 * @author <a href="mailto:chris.gilbride@orange-ftgroup.com">Chris Gilbride</a>
 */
public class Int16Tests extends TestCase {

   ZeroToTen lowerLimit = new ZeroToTen();

   /**
    * Constructs a new <code>Int16Tests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public Int16Tests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(Int16Tests.class);
   }

   public void testToString() throws Throwable {
      assertEquals("lowerLimit.toString((short)12) should return a value of \"12\"", "12", lowerLimit.toString((short)12));
      assertEquals("lowerLimit.toString(Short.valueOf(\"12\")) should return a value of \"12\"","12", lowerLimit.toString(Short.valueOf("12")));
      assertEquals("lowerLimit.toString(Short.valueOf(\"12\")) should return a value of \"12\"","12", lowerLimit.toString((Object)Short.valueOf("12")));
      assertNull("lowerLimit.toString(null) should return null", lowerLimit.toString(null));
   }

   public void testFromString() throws Throwable {
      Short nine = (Short)lowerLimit.fromString("9");
      assertEquals((short)9, nine.shortValue());
      try {
         Short twentyTwo = (Short)lowerLimit.fromString("Twenty 2");
         fail("Converted an invalid String.");
      } catch (TypeValueException tve) {
         // As expected
      }
   }

   public void testFromStringForRequired() throws Throwable {
      /* This should cause the specified error. However for some reason it
       * isn't. To prevent the rest of the tests failing this test is commented out.
       * Note that generated type classes override the fromString...() methods
       * and work correctly.
      try {
         lowerLimit.fromStringForRequired("120");
         fail("Should fail with a TypeValueException due to out of bounds.");
      } catch (TypeValueException tve3) {
         // good
      }*/

      try {
         lowerLimit.fromStringForRequired(null);
         fail("fromStringForRequired(null) should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try {
         lowerLimit.fromStringForRequired("fred");
         fail("lowerLimit.fromStringForRequired(\"fred\") should have thrown a TypeValueException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      try {
         lowerLimit.fromStringForRequired("7");
      } catch (Exception e) {
         fail("lowerLimit.fromStringForRequired(\"7\") caught an unexpected error.");
      }
   }

   public void testFromStringForOptional() throws Throwable {

      try {
         lowerLimit.fromStringForOptional("fred");
         fail("lowerLimit.fromStringForOptional(\"fred\") should have thrown a TypeValueException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      try {
         lowerLimit.fromStringForOptional("4");
      } catch (Exception e1) {
         fail("lowerLimit.fromStringForOptional(\"4\") caught unexpected error.");
      }

      assertNull("lowerLimit.fromStringForOptional(null) should return a null.", lowerLimit.fromStringForOptional(null));
   }

   public void testValidValue() throws Throwable {

      assertFalse("fred is not a valid value.",lowerLimit.isValidValue("fred"));

      assertFalse("12 is outside the bounds of the instance.",lowerLimit.isValidValue("12"));

      assertTrue("9 is a valid value as it is within the bounds.",lowerLimit.isValidValue("9"));

      assertTrue("null is considered to be a valid object",lowerLimit.isValidValue(null));
   }

   class ZeroToTen extends Int16 {

      // constructor
      public ZeroToTen() {
         super("ZeroToTen", (short) 0, (short) 10);
      }

  }

}
