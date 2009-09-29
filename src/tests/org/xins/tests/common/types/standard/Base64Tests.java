package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Base64;


/**
 * Tests for class <code>Base64</code>.
 *
 * @version $Revision: 1.11 $ $Date: 2007/09/18 11:20:31 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class Base64Tests extends TestCase {

   ShortBinary lowerLimit = new ShortBinary();

   /**
    * Constructs a new <code>Base64Tests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public Base64Tests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(Base64Tests.class);
   }

   public void testToString() throws Throwable {
      byte[] hello = { 'h', 'e', 'l', 'l', 'o' };
      assertEquals("aGVsbG8=", lowerLimit.toString(hello));
      assertEquals("aGVsbG8=", lowerLimit.toString((Object)hello));
      assertNull("lowerLimit.toString(null) should return null", lowerLimit.toString(null));
   }

   public void testFromString() throws Throwable {
      byte[] hello = (byte[])lowerLimit.fromString("aGVsbG8=");
      assertEquals(5, hello.length);
      assertEquals('o', hello[4]);
      try {
         byte[] hello2 = (byte[])lowerLimit.fromString("h\u00e9llo");
         fail("Converted an invalid base64 String.");
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
         byte[] hello = lowerLimit.fromStringForRequired("aGVsbG8=");
         assertEquals(5, hello.length);
         assertEquals('o', hello[4]);
      } catch (Exception e) {
         fail("lowerLimit.fromStringForRequired(\"aGVsbG8=\") caught an unexpected error.");
      }
   }

   public void testFromStringForOptional() throws Throwable {

      try {
         String fred = new String(lowerLimit.fromStringForOptional("f\t.,red"));
         fail("lowerLimit.fromStringForOptional(\"f\\t.,red\") should have thrown a TypeValueException but returned \"" + fred + "\".");
      } catch (TypeValueException tve2) {
         // this is good
      }

      try {
         byte[] hello = lowerLimit.fromStringForOptional("aGVsbG8=");
         assertEquals(5, hello.length);
         assertEquals('o', hello[4]);
      } catch (Exception e1) {
         fail("lowerLimit.fromStringForOptional(\"aGVsbG8=\") caught an unexpected error.");
      }

      assertNull("lowerLimit.fromStringForOptional(null) should return a null.", lowerLimit.fromStringForOptional(null));
   }

   public void testValidValue() throws Throwable {

      assertFalse("f\\t.,red is not a valid value.",lowerLimit.isValidValue("f\t.,red"));

      assertFalse("aGVsbG8gc2ly is outside the bounds of the instance.",lowerLimit.isValidValue("aGVsbG8gc2ly"));

      assertTrue("aGVsbG8= is a valid value as it is within the bounds.",lowerLimit.isValidValue("aGVsbG8="));

      assertTrue("null is considered to be a valid object",lowerLimit.isValidValue(null));
   }

   class ShortBinary extends Base64 {

      // constructor
      public ShortBinary() {
         super("ShortBinary", 0, 6);
      }

  }

}
