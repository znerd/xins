/*
 * $Id: MandatoryArgumentCheckerTests.java,v 1.16 2007/09/18 11:21:11 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingException;

/**
 * Tests for class <code>MandatoryArgumentChecker</code>
 *
 * @version $Revision: 1.16 $ $Date: 2007/09/18 11:21:11 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class MandatoryArgumentCheckerTests extends TestCase {

   /**
    * Constructs a new <code>MandatoryArgumentCheckerTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public MandatoryArgumentCheckerTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(MandatoryArgumentCheckerTests.class);
   }

   /**
    * Tests the check method that checks 1 argument.
    */
   public void testMandatoryArgumentChecker1() throws Throwable {
      MandatoryArgumentChecker.check("hello", "world");
      try {
         MandatoryArgumentChecker.check("hello", null);
         fail("The MandatoryArgumentChecker did not throw an IllegalArgumentException when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check(null, "world");
         fail("The MandatoryArgumentChecker did not throw a ProgrammingException when a null name was passed.");
      } catch (ProgrammingException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check(null, null);
         fail("The MandatoryArgumentChecker did not throw a ProgrammingException when a null name and value were passed.");
      } catch (ProgrammingException exception) {
         // as expected
      }
   }

   /**
    * Tests the check method that checks 2 arguments.
    */
   public void testMandatoryArgumentChecker2() throws Throwable {

      MandatoryArgumentChecker.check("hello", "world", "hello", "you!");
       try {
         MandatoryArgumentChecker.check("hello", "world", null, "you!");
         fail("The MandatoryArgumentChecker did not throw a ProgrammingException when a null name was passed.");
      } catch (ProgrammingException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check(null, null, null, null);
         fail("The MandatoryArgumentChecker did not throw a ProgrammingException when a null name and value were passed.");
      } catch (ProgrammingException exception) {
         // as expected
      }
   }

   /**
    * Tests the check method that checks 3 arguments.
    */
   public void testMandatoryArgumentChecker3() throws Throwable {

      MandatoryArgumentChecker.check("hello", "world", "hello", "you!", "hi", "me");
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null, "hi", "me");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", "me");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!", "hi", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
   }

   /**
    * Tests the check method that checks 4 arguments.
    */
   public void testMandatoryArgumentChecker4() throws Throwable {

      MandatoryArgumentChecker.check("a", "1", "b", "2", "c", "3", "d", "4");
      try {
         MandatoryArgumentChecker.check(null, "1", "b", "2", "c", "3", "d", "4");
         fail("The MandatoryArgumentChecker did not throw a ProgrammingException when a null name was passed.");
      } catch (ProgrammingException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!", "hi", "me", "bonjour", "tout le monde");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null, "hi", "me", "bonjour", "tout le monde");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!", "hi", null, "bonjour", "tout le monde");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null, "hi", "me", "bonjour", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", "me", "bonjour", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", null, "bonjour", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
   }
}
