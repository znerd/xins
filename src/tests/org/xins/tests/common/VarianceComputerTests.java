/*
 * $Id$
 *
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.VarianceComputer;

/**
 * Tests for class <code>VarianceComputer</code>
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class VarianceComputerTests extends TestCase {

   /**
    * Constructs a new <code>VarianceComputerTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public VarianceComputerTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(VarianceComputerTests.class);
   }

   public void testVarianceComputer_simple() throws Exception {

      VarianceComputer vc = new VarianceComputer();

      // Initial state
      assertTrue("Expected mean to be NaN initially.",   Double.isNaN(vc.getMean()));
      assertTrue("Expected variance to be 0 initially.", vc.getVariance() == 0.0);
      assertEquals(0, vc.getCount());

      // Add a single value
      vc.add(1L);
      assertEquals(1.0, vc.getMean(),     0.0);
      assertEquals(0.0, vc.getVariance(), 0.0);
      assertEquals(1,   vc.getCount()        );

      // Add the same value again
      vc.add(1L);
      assertEquals(1.0, vc.getMean(),     0.0);
      assertEquals(0.0, vc.getVariance(), 0.0);
      assertEquals(2,   vc.getCount()        );

      // Add value #3 (1-based)
      vc.add(4L);
      assertEquals(2.0, vc.getMean(),     0.0);
      assertEquals(3.0, vc.getVariance(), 0.0);
      assertEquals(3,   vc.getCount()        );

      // Add value #4 (1-based)
      vc.add(0L);
      assertEquals(1.5, vc.getMean(),     0.0);
      assertEquals(3.0, vc.getVariance(), 0.0);
      assertEquals(4,   vc.getCount()        );

      // Add value #5 (1-based)
      vc.add(32L);
      assertEquals(7.6,   vc.getMean(),     0.0    );
      assertEquals(188.3, vc.getVariance(), 0.00001);
      assertEquals(5,     vc.getCount()            );
   }

   public void testVarianceComputer_edge() throws Exception {

      VarianceComputer vc = new VarianceComputer();

      // Add some values
      vc.add((long) (Math.pow(10.0, 9.0) +  4));
      vc.add((long) (Math.pow(10.0, 9.0) +  7));
      vc.add((long) (Math.pow(10.0, 9.0) + 13));
      vc.add((long) (Math.pow(10.0, 9.0) + 16));

      assertEquals(1000000010.0, vc.getMean(),     0.0);
      assertEquals(        30.0, vc.getVariance(), 0.0);
      assertEquals(         4  , vc.getCount()        );
   }
}
