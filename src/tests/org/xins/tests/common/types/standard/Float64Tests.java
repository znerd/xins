package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Float64;


/**
 * Tests for class <code>Float64</code>.
 *
 * @version $Revision: 1.9 $ $Date: 2007/09/18 11:20:31 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class Float64Tests extends TestCase {

   ZeroToTenThousand lowerLimit = new ZeroToTenThousand();

   /**
    * Constructs a new <code>Float64Tests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public Float64Tests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(Float64Tests.class);
   }

   public void testToString() {
      assertEquals("lowerLimit.toString(12.0) should return a value of \"12.0\"", "12.0", lowerLimit.toString(12.0));
      assertEquals("lowerLimit.toString(Double.valueOf(\"12.0\")) should return a value of \"12.0\"","12.0", lowerLimit.toString(Double.valueOf("12.0")));
      assertNull("lowerLimit.toString(null) should return null", lowerLimit.toString(null));
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
         assertEquals(7072.0, lowerLimit.fromStringForRequired("7072"), 0.01);
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
         assertEquals(new Double(4.3), lowerLimit.fromStringForOptional("4.3"));
      } catch (Exception e1) {
         fail("lowerLimit.fromStringForOptional(\"4.3\") caught unexpected error.");
      }

      assertNull("lowerLimit.fromStringForOptional(null) should return a null.", lowerLimit.fromStringForOptional(null));
   }

   public void testValidValue() throws Throwable {

      assertFalse("fred is not a valid value.",lowerLimit.isValidValue("fred"));

      assertFalse("1253232.65 is outside the bounds of the instance.",lowerLimit.isValidValue("1253232.65"));

      assertTrue("9.81 is a valid value as it is within the bounds.",lowerLimit.isValidValue("9.81"));

      assertTrue("null is considered to be a valid object",lowerLimit.isValidValue(null));
   }

   class ZeroToTenThousand extends Float64 {

      // constructor
      public ZeroToTenThousand() {
         super("ZeroToTenThousand", 0, 10000);
      }

  }

}
