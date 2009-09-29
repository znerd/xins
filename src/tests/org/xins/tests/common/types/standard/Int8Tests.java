package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Int8;

/**
 * Tests for class <code>Int8</code>.
 *
 * @version $Revision: 1.14 $ $Date: 2007/03/16 10:30:42 $
 * @author <a href="mailto:chris.gilbride@orange-ftgroup.com">Chris Gilbride</a>
 */
public class Int8Tests extends TestCase {

   ZeroToTen lowerLimit = new ZeroToTen();

   /**
    * Constructs a new <code>Int8Tests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public Int8Tests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(Int8Tests.class);
   }

   public void testToString() throws Throwable {
      assertEquals("lowerLimit.toString((byte)12) should return a value of \"12\"", "12", lowerLimit.toString((byte)12));
      assertEquals("lowerLimit.toString(Byte.valueOf(\"12\")) should return a value of \"12\"", "12", lowerLimit.toString(Byte.valueOf("12")));
      assertEquals("lowerLimit.toString(Byte.valueOf(\"12\")) should return a value of \"12\"", "12", lowerLimit.toString((Object)Byte.valueOf("12")));
      assertNull("lowerLimit.toString(null) should return null", lowerLimit.toString(null));
   }

   public void testFromString() throws Throwable {
      Byte nine = (Byte)lowerLimit.fromString("9");
      assertEquals((byte)9, nine.byteValue());
      try {
         Byte twentyTwo = (Byte)lowerLimit.fromString("Twenty 2");
         fail("Converted an invalid String.");
      } catch (TypeValueException tve) {
         // As expected
      }
   }

   public void testFromStringForRequired() throws Throwable {

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
         fail("lowerLimit.fromStringForRequired(\"7072\") caught an unexpected error.");
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

   class ZeroToTen extends Int8 {

      // constructor
      public ZeroToTen() {
         super("ZeroToTen", (byte) 0, (byte) 10);
      }

  }

}
