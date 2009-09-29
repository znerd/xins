/*
 * $Id: DescriptorBuilderTests.java,v 1.13 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.service;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertiesPropertyReader;

import org.xins.common.service.Descriptor;
import org.xins.common.service.DescriptorBuilder;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.TargetDescriptor;

/**
 * Tests for class <code>DescriptorBuilder</code>.
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class DescriptorBuilderTests extends TestCase {

   private BasicPropertyReader _properties;

   /**
    * Constructs a new <code>DescriptorBuilderTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DescriptorBuilderTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(DescriptorBuilderTests.class);
   }

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      _properties = new BasicPropertyReader();
   }

   protected void tearDown() {
      _properties = null;
   }

   public void testDescriptorBuilder() throws Throwable {

      // Pass null to constructor
      try {
         DescriptorBuilder.build(null, null);
         fail("Expected build(null, null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      try {
         DescriptorBuilder.build(null, "Hello");
         fail("Expected build(null, non-null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      try {
         DescriptorBuilder.build(_properties, null);
         fail("Expected build(non-null, null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      try {
         DescriptorBuilder.build(_properties, "ldap");
         fail("Expected build(non-null, nonexistent-property) to throw an IllegalArgumentException.");
      } catch (MissingRequiredPropertyException exception) { /* as expected */ }

      // Simple example
      String url = "http://somehost.somecompany.com:3003/something/else";
      long timeOut = 1500;
      String base = "server";
      String prop = DescriptorBuilder.TARGET_DESCRIPTOR_TYPE
                  + DescriptorBuilder.DELIMITER
                  + url
                  + DescriptorBuilder.DELIMITER
                  + String.valueOf(timeOut);
      _properties.set(base, prop);
      Descriptor d = DescriptorBuilder.build(_properties, base);
      assertNotNull(d);
      assertEquals(TargetDescriptor.class, d.getClass());
      TargetDescriptor target = (TargetDescriptor) d;
      assertEquals(url,     target.getURL());
      assertEquals(timeOut, target.getTotalTimeOut());

      // GroupDescriptor with no members
      String gprop = DescriptorBuilder.GROUP_DESCRIPTOR_TYPE
                   + DescriptorBuilder.DELIMITER
                   + GroupDescriptor.RANDOM_TYPE;
      _properties.set(base, gprop);
      try {
         DescriptorBuilder.build(_properties, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      // GroupDescriptor with no members, with delimiter at end
      gprop += DescriptorBuilder.DELIMITER;
      _properties.set(base, gprop);
      try {
         DescriptorBuilder.build(_properties, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      // GroupDescriptor with one non-existing member
      gprop += "t1";
      _properties.set(base, gprop);
      try {
         DescriptorBuilder.build(_properties, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      // GroupDescriptor with one existing member
      _properties.set(base + ".t1", prop);
      try {
         DescriptorBuilder.build(_properties, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }
   }
}
