package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Descriptor;

/**
 * Tests for the <code>Descriptor</code> type class.
 *
 * @version $Revision: 1.9 $ $Date: 2007/09/18 11:20:29 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class DescriptorTests extends TestCase {


   /**
    * Constructs a new <code>DescriptorTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DescriptorTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(DescriptorTests.class);
   }

   /**
    * Tests the fromStringForRequired method.
    */
   public void testFromStringForRequired() throws Throwable {

      // test the fromStringForRequired method with all possabilities
      try {
         Descriptor.fromStringForRequired(null);
         fail("Should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try {
         Descriptor.fromStringForRequired("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      assertFalse(Descriptor.fromStringForRequired("descriptor=service, http://www.test.com/, 5000").isGroup());
      assertFalse(Descriptor.fromStringForRequired("descriptor=service, http://www.test.com, 5000").isGroup());

      assertEquals(1, Descriptor.fromStringForRequired("descriptor=service, http://www.test.com, 5000, 5000").getTargetCount());
   }

   /**
    * Tests the fromStringForOptional method.
    */
   public void testFromStringForOptional() throws Throwable {
      // test the fromStringForOptional method with all possabilities
      try {
         Descriptor.fromStringForOptional("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      assertNull("Should return a null from a null parameter.", Descriptor.fromStringForOptional(null));

      assertFalse(Descriptor.fromStringForRequired("descriptor=service, http://www.test.com, 5000").isGroup());

      assertEquals(1, Descriptor.fromStringForRequired("descriptor=service, http://www.test.com, 5000, 5000").getTargetCount());
   }

   /**
    * Tests the isValidValue method.
    */
   public void testIsValidValue() throws Exception {

      Descriptor.SINGLETON.checkValue("descriptor=service, http://www.test.com, 5000");

      assertTrue("Descriptor.SINGLETON.isValidValue(null) is considered invalid.", Descriptor.SINGLETON.isValidValue(null));

      assertFalse("fred is considered valid.", Descriptor.SINGLETON.isValidValue("fred"));
   }

   /**
    * Tests the fromString method.
    */
   public void testFromString() throws Throwable  {

      assertNull("Descriptor.SINGLETON.fromString(null) should return a null.", Descriptor.SINGLETON.fromString(null));

      try {
         Descriptor.SINGLETON.fromString("fred");
         fail("Should throw a type value exception.");
      } catch (TypeValueException tve) {
         // this is good
      }
   }

   /**
    * Tests the toString method.
    */
   public void testToString() throws Throwable {

      assertNull(Descriptor.toString(null));

      TargetDescriptor target = new TargetDescriptor("http://www.test.com");
      assertEquals("Descriptor.SINGLETON.toString(String) returned an incorrect value.", "descriptor=service, http://www.test.com, 5000, 5000, 5000", Descriptor.SINGLETON.toString(target));
   }

}
