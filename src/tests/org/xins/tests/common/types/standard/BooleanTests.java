package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.TextUtils;
import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Boolean;

/**
 * Tests for class <code>Boolean</code>.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/16 10:30:41 $
 * @author <a href="mailto:chris.gilbride@orange-ftgroup.com">Chris Gilbride</a>
 */
public class BooleanTests extends TestCase {

   /**
    * Constructs a new <code>BooleanTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public BooleanTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(BooleanTests.class);
   }

   /**
    * Tests the fromStringForRequired method of the Boolean type class.
    */
   public void testFromStringForRequired() throws Throwable {

      // test the fromStringForRequired method with all possabilities
      try {
         Boolean.fromStringForRequired(null);
         fail("Should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try {
         Boolean.fromStringForRequired("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      assertFalse("fromStringForRequired(false) should return false.", Boolean.fromStringForRequired("false"));

      assertTrue("fromStringForRequired(true) should return true.", Boolean.fromStringForRequired("true"));
   }

   /**
    * Tests the fromStringForOptional method of the Boolean type class.
    */
   public void testFromStringForOptional() throws Throwable {
      // test the fromStringForOptional method with all possabilities
      try {
         Boolean.fromStringForOptional("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      assertNull("Should return a null from a null parameter.", Boolean.fromStringForOptional(null));

      assertTrue("fromStringForOptional(true) should return true.", Boolean.fromStringForOptional("true").booleanValue());

      assertFalse("fromStringForOptional(false) should return a false.", Boolean.fromStringForOptional("false").booleanValue());
   }

   /**
    * Tests the isValidValue method of hte Boolean type class.
    */
   public void testIsValidValue() {

      assertTrue("Boolean.SINGLETON.isValidValue('true') is valid.", Boolean.SINGLETON.isValidValue("true"));

      assertTrue("Boolean.SINGLETON.isValidValue('false') is valid.", Boolean.SINGLETON.isValidValue("false"));

      assertTrue("Boolean.SINGLETON.isValidValue(null) is valid.", Boolean.SINGLETON.isValidValue(null));
   }

   /**
    * Tests the fromString method of Boolean type class.
    */
   public void testFromString() throws Throwable  {

      assertEquals("Boolean.SINGLETON.fromString(null) should return a null.", null, Boolean.SINGLETON.fromString(null));

      try {
         String  input = "fred";
         Object output = Boolean.SINGLETON.fromString(input);
         fail("Expected an exception when converting " + TextUtils.quote(input) + " to a boolean. Instead received " + TextUtils.quote(output) + '.');
      } catch (TypeValueException tve) {
         // this is good
      }

      try {
         String  input = "fred";
         Object output = Boolean.SINGLETON.getValueClass().isInstance(Boolean.SINGLETON.fromString(input));
         fail("Expected an exception when converting " + TextUtils.quote(input) + " to a boolean. Instead received " + TextUtils.quote(output) + '.');
      } catch (Exception e) {
         // this is good
      }
   }

   /**
    * Tests the toString method of the Boolean type class which is inherited from the Type class.
    */
   public void testToString() throws Throwable {

      assertNull("toString(Boolean value) should return a null for a null.", Boolean.SINGLETON.toString(null));

      assertEquals("toString(f) should return a value of \"false\"", "false", Boolean.SINGLETON.toString(false));

//      if (! "false".equals(Boolean.SINGLETON.toString(f))) {
//         fail("Should return string value of false from toString(f).");
//      }

      assertEquals("toString(t) should return a value of \"true\"", "true", Boolean.SINGLETON.toString(true));

//      if (! "true".equals(Boolean.SINGLETON.toString(t))) {
//         fail("Should return string value of true from toString(t).");
//      }


      assertEquals("Boolean.SINGLETON.toString(java.lang.Boolean.TRUE) should return \"true\"", "true", Boolean.SINGLETON.toString(java.lang.Boolean.TRUE));
//      if (! "true".equals(Boolean.SINGLETON.toString(java.lang.Boolean.TRUE))) {
//         fail("Should have returned true from Boolean.SINGLETON.toString(java.lang.Boolean.TRUE).");
//      }

      assertEquals("Boolean.SINGLETON.toString(java.lang.Boolean.FALSE) should return \"false\"", "false", Boolean.SINGLETON.toString(java.lang.Boolean.FALSE));
//      if (! "false".equals(Boolean.SINGLETON.toString(java.lang.Boolean.FALSE))) {
//         fail("Should have returned false from Boolean.SINGLETON.toString(java.lang.Boolean.FALSE).");
//      }

   }

}
