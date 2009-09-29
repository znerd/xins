/*
 * $Id: ChainedMapTests.java,v 1.11 2007/09/18 11:20:56 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.ChainedMap;

/**
 * Tests for the <code>ChainedMap</code> class.
 *
 * @version $Revision: 1.11 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class ChainedMapTests extends TestCase {

   /**
    * Constructs a new <code>ChainedMapTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ChainedMapTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ChainedMapTests.class);
   }

   public void testChainedMap() {
      int itemsCount = 20;
      Random rd = new Random();
      List arrayList = new ArrayList(itemsCount);
      Map sortedMap = new ChainedMap();
      for (int i = 0; i < itemsCount; i++) {
         byte[] randomText = new byte[8];
         rd.nextBytes(randomText);
         String randomString  = new String(randomText);
         if (arrayList.contains(randomString)) {
            i--;
            continue;
         }
         arrayList.add(randomString);
         sortedMap.put(randomString, "value" + i);
      }

      Iterator itSortedMap = sortedMap.values().iterator();
      int i = 0;
      while (itSortedMap.hasNext()) {
         String nextValue = (String) itSortedMap.next();
         assertEquals("value" + i, nextValue);
         i++;
      }

      assertEquals(sortedMap.size(), arrayList.size());
      Iterator itEntries = sortedMap.entrySet().iterator();
      Iterator itKeysMap = sortedMap.keySet().iterator();
      Iterator itKeys = arrayList.iterator();
      while (itEntries.hasNext() & itKeysMap.hasNext() & itKeys.hasNext()) {
         Map.Entry nextEntry = (Map.Entry) itEntries.next();
         String nextKeyMap = (String) itKeysMap.next();
         String nextKey = (String) itKeys.next();
         assertEquals(nextEntry.getKey(), nextKey);
         assertEquals(nextKeyMap, nextKey);
      }

      assertEquals(itemsCount, sortedMap.size());
   }
}
