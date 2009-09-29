/*
 * $Id: ProtectedPropertyReaderTests.java,v 1.10 2007/09/24 12:18:48 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.IncorrectSecretKeyException;
import org.xins.common.collections.ProtectedPropertyReader;

/**
 * Tests for class <code>ProtectedPropertyReader</code>.
 *
 * @version $Revision: 1.10 $ $Date: 2007/09/24 12:18:48 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ProtectedPropertyReaderTests extends TestCase {

   /**
    * Constructs a new <code>ProtectedPropertyReader</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ProtectedPropertyReaderTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ProtectedPropertyReaderTests.class);
   }

   private final void assertSize0(ProtectedPropertyReader p) {
      assertEquals(0, p.size());
      assertNotNull(p.getNames());
      assertFalse(p.getNames().hasNext());
      assertNull(p.get(""));
      assertNull(p.get("a"));
      assertNull(p.get("0"));
      assertNull(p.get("1"));
      Iterator names = p.getNames();
      assertNotNull(names);
      assertFalse(names.hasNext());
   }

   private final void assertSize1(ProtectedPropertyReader p) {
      assertEquals(1, p.size());
      Iterator names = p.getNames();
      assertNotNull(names);
      assertTrue(names.hasNext());
      assertEquals("0", names.next());
      assertFalse(names.hasNext());
      assertNull(p.get(""));
      assertNull(p.get("a"));
      assertEquals("zero", p.get("0"));
   }

   private final void assertSize2(ProtectedPropertyReader p) {
      assertEquals(2, p.size());
      Iterator names = p.getNames();
      assertNotNull(names);
      assertTrue(names.hasNext());
      String name = (String) names.next();
      assertTrue(names.hasNext());
      assertNotNull(name);
      if (name.equals("0")) {
         assertEquals("1", names.next());
      } else {
         assertEquals("1", name);
         assertEquals("0", names.next());
      }
      assertFalse(names.hasNext());
      assertNull(p.get(""));
      assertNull(p.get("a"));
      assertEquals("zero", p.get("0"));
      assertEquals("one",  p.get("1"));
   }

   public void testSize() {

      // Create an empty object and check the size is 0
      Object secretKey = new Object();
      ProtectedPropertyReader reader = new ProtectedPropertyReader(secretKey);
      assertEquals(0, reader.size());

      // Set a couple of properties and check the size
      reader.set(secretKey, "a", "1");
      reader.set(secretKey, "b", "2");
      reader.set(secretKey, "c", "3");
      reader.set(secretKey, "d", "4");
      assertEquals(4, reader.size());

      // Remove a property and confirm the size changed
      reader.remove(secretKey, "d");
      assertEquals(3, reader.size());

      // Set a property value to null and confirm the size changed as well
      reader.set(secretKey, "c", null);
      assertEquals(2, reader.size());

      // Add them back in and check the size again
      reader.set(secretKey, "d", "4");
      reader.set(secretKey, "c", "3");
      assertEquals(4, reader.size());

      // Remove them all and check the size becomes 0
      reader.set(secretKey, "d", null);
      reader.set(secretKey, "c", null);
      reader.set(secretKey, "a", null);
      reader.remove(secretKey, "b");
      assertEquals(0, reader.size());
   }

   public void testProtectedPropertyReader() throws Exception {

      // Test constructor
      try {
         new ProtectedPropertyReader(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      Object secretKey = new Object();
      ProtectedPropertyReader p = new ProtectedPropertyReader(secretKey);
      assertSize0(p);

      try {
         p.get(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Set with correct secret key
      p.set(secretKey, "0", "zero");
      assertSize1(p);

      // Set with incorrect secret key
      try {
         p.set(new Object(), "1", "one");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertSize1(p);
      try {
         p.set(null, "1", "one");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertSize1(p);
      try {
         p.set(null, null, null);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertSize1(p);

      // Set with correct secret key again
      p.set(secretKey, "1", "one");
      assertSize2(p);

      // Remove with incorrect secret key
      try {
         p.remove(new Object(), "2");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      try {
         p.remove(null, "2");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }

      // Remove with correct secret key but null name
      try {
         p.remove(secretKey, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      assertSize2(p);

      // Remove correctly
      p.remove(secretKey, "1");
      assertSize1(p);

      // Clear with wrong secret key
      try {
         p.clear(new Object());
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      try {
         p.clear(null);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertSize1(p);

      // Clear correctly
      p.clear(secretKey);
      assertSize0(p);

      // Copy from another PropertyReader
      BasicPropertyReader p2 = new BasicPropertyReader();
      p2.set("0", "zero");
      p2.set("1", "one");

      // Call copyFrom incorrectly
      try {
         p.copyFrom(new Object(), p2);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertSize0(p);
      try {
         p.copyFrom(new Object(), null);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertSize0(p);
      try {
         p.copyFrom(secretKey, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      assertSize0(p);

      // Correctly call copyFrom with a BasicPropertyReader
      p.copyFrom(secretKey, p2);
      assertSize2(p);

      // Perhaps the implementation is different when a
      // ProtectedPropertyReader instance is passed, test this as well!
      p.clear(secretKey);
      assertSize0(p);
      ProtectedPropertyReader p3 = new ProtectedPropertyReader(secretKey);
      p3.set(secretKey, "1", "one");
      p3.set(secretKey, "0", "zero");
      p.copyFrom(secretKey, p3);
      assertSize2(p);
   }
}
