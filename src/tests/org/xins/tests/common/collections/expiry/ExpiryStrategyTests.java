/*
 * $Id: ExpiryStrategyTests.java,v 1.12 2007/03/16 10:30:34 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections.expiry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.expiry.ExpiryStrategy;

/**
 * Tests for class <code>ExpiryStrategy</code>.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/16 10:30:34 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ExpiryStrategyTests extends TestCase {

   /**
    * Constructs a new <code>ExpiryStrategyTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ExpiryStrategyTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ExpiryStrategyTests.class);
   }

   public void testExpiryStrategy() throws Throwable {
      try {
         new ExpiryStrategy(0L, 0L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         new ExpiryStrategy(1L, 0L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         new ExpiryStrategy(0L, 1L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         new ExpiryStrategy(1L, 2L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      ExpiryStrategy strategy = null;;
      long timeOut;
      long precision;

      timeOut   = 2000L;
      precision = 1001L;
      try {
         strategy  = new ExpiryStrategy(timeOut, precision);
         assertEquals(timeOut,   strategy.getTimeOut());
         assertEquals(precision, strategy.getPrecision());
         assertEquals(2, strategy.getSlotCount());
      } finally {
         if (strategy != null) {
            strategy.stop();
         }
         strategy = null;
      }

      timeOut   = 2000L;
      precision = 1000L;
      try {
         strategy  = new ExpiryStrategy(timeOut, precision);
         assertEquals(timeOut,   strategy.getTimeOut());
         assertEquals(precision, strategy.getPrecision());
         assertEquals(2, strategy.getSlotCount());
      } finally {
         if (strategy != null) {
            strategy.stop();
         }
         strategy = null;
      }

      timeOut   = 2000L;
      precision = 999L;
      try {
         strategy  = new ExpiryStrategy(timeOut, precision);
         assertEquals(timeOut,   strategy.getTimeOut());
         assertEquals(precision, strategy.getPrecision());
         assertEquals(3,         strategy.getSlotCount());
      } finally {
         if (strategy != null) {
            strategy.stop();
         }
         strategy = null;
      }

      // Test equality comparison method
      ExpiryStrategy es1 = new ExpiryStrategy(timeOut, precision);
      ExpiryStrategy es2 = new ExpiryStrategy(timeOut, precision);
      try {
         assertEquals("Expected different ExpiryStrategy instances with same properties to be considered equal.", es1, es2);
         assertEquals("Expected different ExpiryStrategy instances with same properties to be considered equal.", es2, es1);
         assertEquals("Expected hash codes to be equal for ExpiryStrategy instances that are considered equal", es1.hashCode(), es2.hashCode());
      } finally {
         es1.stop();
         es2.stop();
      }
   }
}
