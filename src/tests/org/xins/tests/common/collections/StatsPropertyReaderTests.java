/*
 * $Id: StatsPropertyReaderTests.java,v 1.8 2007/03/16 10:30:34 agoubard Exp $
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
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.StatsPropertyReader;

/**
 * Tests for class <code>StatsPropertyReader</code>
 *
 * @version $Revision: 1.8 $ $Date: 2007/03/16 10:30:34 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class StatsPropertyReaderTests extends TestCase {

   /**
    * Constructs a new <code>StatsPropertyReaderTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public StatsPropertyReaderTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(StatsPropertyReaderTests.class);
   }

   public void testStatsPropertyReader() {

      // Construct a BasicPropertyReader
      BasicPropertyReader bpr = new BasicPropertyReader();
      bpr.set("a", "1");
      bpr.set("b", "2");
      assertEquals("1", bpr.get("a"));
      assertEquals("2", bpr.get("b"));
      Iterator it = bpr.getNames();
      assertTrue(it.hasNext());
      String name1 = (String) it.next();
      assertTrue(it.hasNext());
      assertTrue("a".equals(name1) || "b".equals(name1));
      if ("a".equals(name1)) {
         assertTrue("b".equals(it.next()));
      } else {
         assertTrue("a".equals(it.next()));
      }

      // Create a StatsPropertyReader based on the BasicPropertyReader
      StatsPropertyReader spr = new StatsPropertyReader(bpr);
      assertEquals("1", spr.get("a"));
      assertEquals("2", spr.get("b"));
      it = spr.getNames();
      assertTrue(it.hasNext());
      name1 = (String) it.next();
      assertTrue(it.hasNext());
      assertTrue("a".equals(name1) || "b".equals(name1));
      if ("a".equals(name1)) {
         assertTrue("b".equals(it.next()));
      } else {
         assertTrue("a".equals(it.next()));
      }

      // Create another BasicPropertyReader with 4 entries
      bpr = new BasicPropertyReader();
      bpr.set("a", "1");
      bpr.set("b", "2");
      bpr.set("c", "3");
      bpr.set("d", "4");

      // Construct another StatsPropertyReader based on that
      spr = new StatsPropertyReader(bpr);
      PropertyReader unused = spr.getUnused();
      assertEquals(4, unused.size());

      // Get one from the StatsPropertyReader
      spr.get("a");
      unused = spr.getUnused();
      assertEquals(3, unused.size());

      // TODO: Extend this test
   }
}
