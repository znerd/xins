package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.URL;

/**
 * Tests for class <code>URL</code>.
 *
 * @version $Revision: 1.9 $ $Date: 2007/09/18 11:20:30 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class URLTests extends TestCase {


   /**
    * Constructs a new <code>URLTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public URLTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(URLTests.class);
   }

   /**
    * Tests the fromStringForRequired method of the Boolean type class.
    */
   public void testFromStringForRequired() throws Throwable {

      // test the fromStringForRequired method with all possabilities
      try {
         URL.fromStringForRequired(null);
         fail("Should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try {
         URL.fromStringForRequired("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      assertEquals("http://www.test.com/", URL.fromStringForRequired("http://www.test.com/"));

      assertEquals("ldap://www.test.com/", URL.fromStringForRequired("ldap://www.test.com/"));
   }

   /**
    * Tests the fromStringForOptional method of the Boolean type class.
    */
   public void testFromStringForOptional() throws Throwable {
      // test the fromStringForOptional method with all possabilities
      try {
         URL.fromStringForOptional("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      assertNull("Should return a null from a null parameter.", URL.fromStringForOptional(null));

      assertEquals("http://www.test.com/", URL.fromStringForRequired("http://www.test.com/"));

      assertEquals("ldap://www.test.com/", URL.fromStringForRequired("ldap://www.test.com/"));
   }

   /**
    * Tests the isValidValue method of hte Boolean type class.
    */
   public void testIsValidValue() {

      assertTrue("http://www.test.com/ is considered invalid.", URL.SINGLETON.isValidValue("http://www.test.com/"));

      assertTrue("ldap://www.test.com/ is considered invalid.", URL.SINGLETON.isValidValue("ldap://www.test.com/"));

      assertTrue("URL.SINGLETON.isValidValue(null) is considered invalid.", URL.SINGLETON.isValidValue(null));

      assertFalse("fred is considered invalid.", URL.SINGLETON.isValidValue("fred"));
   }

   /**
    * Tests the fromString method of Boolean type class.
    */
   public void testFromString() throws Throwable  {

      assertEquals("URL.SINGLETON.fromString(null) should return a null.", null, URL.SINGLETON.fromString(null));

      try {
         URL.SINGLETON.fromString("fred");
         fail("Should throw a type value exception.");
      } catch (TypeValueException tve) {
         // this is good
      }
   }

   /**
    * Tests the toString method of the Boolean type class which is inherited from the Type class.
    */
   public void testToString() throws Throwable {

      try {
         URL.SINGLETON.toString(null);
         fail("null is not a possible value for toString()");
      } catch (IllegalArgumentException eaex) {
         // As expected
      }

      assertEquals("URL.SINGLETON.toString(String) should return \"http://www.test.com/test.html\"", "http://www.test.com/test.html", URL.SINGLETON.toString("http://www.test.com/test.html"));
   }

}
