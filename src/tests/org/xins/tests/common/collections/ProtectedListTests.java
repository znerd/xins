/*
 * $Id: ProtectedListTests.java,v 1.14 2007/09/18 11:20:57 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.ArrayList;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.IncorrectSecretKeyException;
import org.xins.common.collections.ProtectedList;

/**
 * Tests for class <code>ProtectedList</code>.
 *
 * @version $Revision: 1.14 $ $Date: 2007/09/18 11:20:57 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ProtectedListTests extends TestCase {

   /**
    * The secret key for the test protected list.
    */
   private final static Object SECRET_KEY = new Object();

   /**
    * Constructs a new <code>ProtectedList</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ProtectedListTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ProtectedListTests.class);
   }

   public void testProtectedList() {

      // Construct a ProtectedList with null as secret key (should fail)
      try {
         new ProtectedList(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         new ProtectedList(null, 15);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         new ProtectedList(null, new ArrayList());
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Construct a ProtectedList with the secret key
      ProtectedList list = new ProtectedList(SECRET_KEY);
      assertEquals(0, list.size());

      // Try unsupported standard add-operation
      try {
         list.add("hello1");
         fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException exception) {
         // as expected
      }

      // Add using secret key should succeed
      list.add(SECRET_KEY, "hello2");
      assertEquals(1,        list.size());
      assertEquals("hello2", list.get(0));
      assertEquals(0,        list.indexOf("hello2"));
      assertEquals(0,        list.lastIndexOf("hello2"));
      assertTrue(list.contains("hello2"));

      // Add using incorrect secret key should fail
      try {
         list.add(null, "hello3");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      try {
         list.add(new Object(), "hello3");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertEquals(1,        list.size());
      assertEquals("hello2", list.get(0));
      assertEquals(0,        list.indexOf("hello2"));
      assertEquals(0,        list.lastIndexOf("hello2"));
      assertTrue(list.contains("hello2"));
      assertFalse(list.contains("hello3"));

      // Try removing with incorrect secret key
      try {
         list.remove(null, -1);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      try {
         list.remove(new Object(), -1);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }

      // Try removing with correct secret key, but invalid index
      try {
         list.remove(SECRET_KEY, -1);
         fail("Expected IndexOutOfBoundsException.");
      } catch (IndexOutOfBoundsException exception) {
         // as expected
      }
      try {
         list.remove(SECRET_KEY, 1);
         fail("Expected IndexOutOfBoundsException.");
      } catch (IndexOutOfBoundsException exception) {
         // as expected
      }
      assertEquals(1,        list.size());
      assertEquals("hello2", list.get(0));
      assertEquals(0,        list.indexOf("hello2"));
      assertEquals(0,        list.lastIndexOf("hello2"));
      assertTrue(list.contains("hello2"));
      assertFalse(list.contains("hello3"));

      // Really remove
      list.remove(SECRET_KEY, 0);
      assertEquals(0,        list.size());
      assertEquals(-1,       list.indexOf("hello2"));
      assertEquals(-1,       list.lastIndexOf("hello2"));
      assertFalse(list.contains("hello2"));
      assertFalse(list.contains("hello3"));
   }

   public void testProtectedListClone() {

      ProtectedList p1 = new ProtectedList(SECRET_KEY);
      ProtectedList p2 = (ProtectedList) p1.clone();
      assertEquals(0, p1.size());
      assertEquals(0, p2.size());
      assertTrue("Expected ProtectedList to be equal to itself.", p1.equals(p1));
      assertTrue("Expected ProtectedList to be equal to itself.", p2.equals(p2));
      assertTrue("Expected cloned version of empty ProtectedList to be equal to the original.", p1.equals(p2));
      assertTrue("Expected cloned version of empty ProtectedList to be equal to the original.", p2.equals(p1));

      p1.add(SECRET_KEY, "Hello");
      assertEquals(1, p1.size());
      assertEquals(0, p2.size());

      p2 = (ProtectedList) p1.clone();
      assertEquals(1, p1.size());
      assertEquals(1, p2.size());
      assertTrue("Expected ProtectedList to be equal to itself.", p1.equals(p1));
      assertTrue("Expected ProtectedList to be equal to itself.", p2.equals(p2));
      assertTrue("Expected cloned version of ProtectedList to be equal to the original.", p1.equals(p2));
      assertTrue("Expected cloned version of ProtectedList to be equal to the original.", p2.equals(p1));
   }
}
