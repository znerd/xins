/*
 * $Id: PropertyReaderUtilsTests.java,v 1.14 2007/07/04 15:13:50 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.collections.ProtectedPropertyReader;

/**
 * Tests for class <code>PropertyReaderUtils</code>.
 *
 * @version $Revision: 1.14 $ $Date: 2007/07/04 15:13:50 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class PropertyReaderUtilsTests extends TestCase {

   /**
    * Constructs a new <code>PropertyReaderUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public PropertyReaderUtilsTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(PropertyReaderUtilsTests.class);
   }

   public void testEmptyReader() {
      PropertyReader r = PropertyReaderUtils.EMPTY_PROPERTY_READER;
      assertNotNull(r);
      assertEquals(0, r.size());
   }

   public void testGetBooleanProperty()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.getBooleanProperty(null, null, false);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(null, null, true);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, null, false);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, null, true);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(null, "propertyName", false);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(null, "propertyName", true);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      r1.set("a", "true");
      r1.set("b", "false");
      r1.set("c", "");
      r1.set("d", null);
      r1.set("e", "something");
      r1.set("f", "TRUE");

      assertEquals(true,  PropertyReaderUtils.getBooleanProperty(r0, "a", false));
      assertEquals(false, PropertyReaderUtils.getBooleanProperty(r0, "b", true));
      assertEquals(true,  PropertyReaderUtils.getBooleanProperty(r0, "c", true));
      assertEquals(false, PropertyReaderUtils.getBooleanProperty(r0, "c", false));
      assertEquals(true,  PropertyReaderUtils.getBooleanProperty(r0, "d", true));
      assertEquals(false, PropertyReaderUtils.getBooleanProperty(r0, "d", false));

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "e", true);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "e", false);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "f", true);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "f", false);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }
   }

   public void testGetIntProperty()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.getIntProperty(null, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(null, "propertyName");
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      final long MIN_VALUE           = (long) Integer.MIN_VALUE;
      final long MAX_VALUE           = (long) Integer.MAX_VALUE;
      final long LESS_THAN_MIN_VALUE = MIN_VALUE - 1L;
      final long MORE_THAN_MAX_VALUE = MAX_VALUE + 1L;

      r1.set("a", null);
      r1.set("b", "");
      r1.set("c", "-1");
      r1.set("d", "0");
      r1.set("e", "1");
      r1.set("f", Long.toString(MIN_VALUE));
      r1.set("g", Long.toString(MAX_VALUE));
      r1.set("h", Long.toString(LESS_THAN_MIN_VALUE));
      r1.set("i", Long.toString(MORE_THAN_MAX_VALUE));
      r1.set("j", "a");

      try {
         PropertyReaderUtils.getIntProperty(r0, "unsetProperty");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "a");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "b");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      assertEquals(-1, PropertyReaderUtils.getIntProperty(r0, "c"));
      assertEquals(0,  PropertyReaderUtils.getIntProperty(r0, "d"));
      assertEquals(1,  PropertyReaderUtils.getIntProperty(r0, "e"));
      assertEquals(Integer.MIN_VALUE, PropertyReaderUtils.getIntProperty(r0, "f"));
      assertEquals(Integer.MAX_VALUE, PropertyReaderUtils.getIntProperty(r0, "g"));

      try {
         PropertyReaderUtils.getIntProperty(r0, "h");
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "i");
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "j");
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }
   }

   public void testGetRequiredProperty()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.getRequiredProperty(null, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(r0, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(null, "propertyName");
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      r1.set("a", null);
      r1.set("b", "");
      r1.set("c", "value");

      try {
         PropertyReaderUtils.getRequiredProperty(r0, "unsetProperty");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(r0, "a");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(r0, "b");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      assertEquals("value", PropertyReaderUtils.getRequiredProperty(r0, "c"));
   }

   public void testCreatePropertyReader()
   throws Exception {

      try {
         PropertyReaderUtils.createPropertyReader(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      InputStream in0 = new ByteArrayInputStream("".getBytes("US-ASCII"));
      InputStream in1 = new ByteArrayInputStream("a=\nb= \nc=1\nd=1\ne=2\nf=3 \n\n\t\n".getBytes("US-ASCII"));

      PropertyReader r = PropertyReaderUtils.createPropertyReader(in0);
      assertEquals(0, r.size());

      r = PropertyReaderUtils.createPropertyReader(in1);
      assertEquals(4, r.size());

      assertEquals(null, r.get("a"));
      assertEquals(null, r.get("b"));
      assertEquals("1",  r.get("c"));
      assertEquals("1",  r.get("d"));
      assertEquals("2",  r.get("e"));
      assertEquals("3 ", r.get("f"));
   }

   public void testPropertyReaderEquals() {

      // Compare nulls
      BasicPropertyReader b1 = null;
      assertTrue(PropertyReaderUtils.equals(b1, null));

      // Compare null with non-null
      b1 = new BasicPropertyReader();
      assertFalse(PropertyReaderUtils.equals(b1, null));
      assertFalse(PropertyReaderUtils.equals(null, b1));

      // Compare identity equals
      assertTrue(PropertyReaderUtils.equals(b1, b1));
      b1.set("greeting", "hello");
      assertTrue(PropertyReaderUtils.equals(b1, b1));

      // Compare identical classes
      BasicPropertyReader b2 = new BasicPropertyReader();
      b2.set("greeting", "hello");
      assertTrue(PropertyReaderUtils.equals(b1, b2));
      assertTrue(PropertyReaderUtils.equals(b2, b1));
      b2.set("x", "1.0");
      assertFalse(PropertyReaderUtils.equals(b1, b2));
      assertFalse(PropertyReaderUtils.equals(b2, b1));

      // Compare different classes
      Object secretKey = new Object();
      ProtectedPropertyReader p1 = new ProtectedPropertyReader(secretKey);
      p1.copyFrom(secretKey, b2);
      assertTrue(PropertyReaderUtils.equals(p1, b2));
      assertTrue(PropertyReaderUtils.equals(b2, p1));
      b2.set("y", "2.0");
      assertFalse(PropertyReaderUtils.equals(p1, b2));
      assertFalse(PropertyReaderUtils.equals(b2, p1));
   }

   public void testParsePropertyReader() throws Exception {
      String       propertyName = "descriptor";
      String      propertyValue = "service, http://www.test88.eu, 5000";
      String             string = propertyName + "=" + propertyValue;
      String           encoding = "ISO-8859-1";
      byte[]              bytes = string.getBytes(encoding);
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      PropertyReader properties = PropertyReaderUtils.parsePropertyReader(bais);

      assertNotNull(properties);
      assertEquals(1, properties.size());
      assertEquals("descriptor", properties.names().iterator().next());
      assertEquals("service, http://www.test88.eu, 5000", properties.get("descriptor"));
   }
}
