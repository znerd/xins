/*
 * $Id: ExpiryFolderTests.java,v 1.31 2007/09/18 11:21:09 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections.expiry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.expiry.ExpiryFolder;
import org.xins.common.collections.expiry.ExpiryListener;
import org.xins.common.collections.expiry.ExpiryStrategy;

/**
 * Tests for class <code>ExpiryFolder</code>.
 *
 * @version $Revision: 1.31 $ $Date: 2007/09/18 11:21:09 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class ExpiryFolderTests extends TestCase {

   private final static int    DURATION  = 500;
   private final static int    PRECISION = 100;
   private final static String NAME      = "TestFolder";

   /**
    * Constructs a new <code>ExpiryFolderTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ExpiryFolderTests(String name) {
      super(name);
   }
   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ExpiryFolderTests.class);
   }

   // TODO: Stop all expiry strategies globally

   public void testExpiryFolder() throws Exception {

      // Construct an ExpiryStrategy
      ExpiryStrategy strategy = new ExpiryStrategy(DURATION, PRECISION);
      try {
         doTestExpiryFolder(strategy);
      } finally {
         strategy.stop();
      }

   }

   public void testExpiryFolderEquals() throws Exception {

      // Construct ExpiryStrategy instances
      ExpiryStrategy strategy1a = new ExpiryStrategy(4000, 2000);
      ExpiryStrategy strategy1b = new ExpiryStrategy(4000, 2000);
      ExpiryStrategy strategy2  = new ExpiryStrategy(4000, 2001);

      try {
         // Construct ExpiryFolder instances
         ExpiryFolder folder1a = new ExpiryFolder("Folder1",  strategy1a);
         ExpiryFolder folder1b = new ExpiryFolder("Folder1",  strategy1b);
         ExpiryFolder folder2a = new ExpiryFolder("Folder2a", strategy2);

         // Test equals(Object) method
         assertEquals("Expected ExpiryFolders with same ExpiryStrategy to be equal", folder1a, folder1b);
         assertEquals("Expected ExpiryFolders with same ExpiryStrategy to be equal", folder1b, folder1a);
         assertEquals("Expected hash codes to be equal for ExpiryFolders instances that are considered equal", folder1a.hashCode(), folder1b.hashCode());
         assertNotSame(folder1a, folder2a);
         assertNotSame(folder2a, folder1a);
         assertNotSame(folder1b, folder2a);
         assertNotSame(folder2a, folder1b);

         // Put something in both folders
         folder1a.put("name", "Ernst Le Coq");
         folder1b.put("name", "Ernst Le Coq");
         assertTrue("Expected ExpiryFolders with same ExpiryStrategy and content to be equal", folder1a.equals(folder1b));
         assertTrue("Expected ExpiryFolders with same ExpiryStrategy and content to be equal", folder1b.equals(folder1a));
         assertEquals("Expected hash codes to be equal for ExpiryFolders instances that are considered equal", folder1a.hashCode(), folder1b.hashCode());
         assertNotSame(folder1a, folder2a);
         assertNotSame(folder2a, folder1a);
         assertNotSame(folder1b, folder2a);
         assertNotSame(folder2a, folder1b);
      } finally {
         strategy1a.stop();
         strategy1b.stop();
         strategy2.stop();
      }
   }

   public void testExpiryFolderCopy() throws Exception {

      // Construct ExpiryStrategy instances
      ExpiryStrategy strategy1 = new ExpiryStrategy(DURATION, PRECISION);
      ExpiryStrategy strategy2 = new ExpiryStrategy(DURATION, PRECISION + 1);

      try {
         // Construct ExpiryFolder instances
         ExpiryFolder folder1a = new ExpiryFolder("Folder1", strategy1);
         ExpiryFolder folder1b = new ExpiryFolder("Folder1", strategy1);
         ExpiryFolder folder2a = new ExpiryFolder("Folder2a", strategy2);

         // Test equals(Object) method
         assertEquals("Expected ExpiryFolders with same ExpiryStrategy to be equal, initially.", folder1b, folder1a);

         // Put something in the first expiry folder
         folder1a.put("name", "Ernst Le Coq");

         // Pass null to copy(ExpiryFolder)
         try {
            folder1a.copy(null);
            fail("Expected ExpiryFolder.copy(<null>) to throw IllegalArgumentException.");
            return;
         } catch (IllegalArgumentException exception) {
            // as expected
         }

         // Pass same object to copy method
         try {
            folder1a.copy(folder1a);
            fail("Expected ExpiryFolder.copy(<self>) to throw IllegalArgumentException.");
            return;
         } catch (IllegalArgumentException exception) {
            // as expected
         }

         // Pass folder with different precision to copy method
         try {
            folder1a.copy(folder2a);
            fail("Expected ExpiryFolder.copy(<folder with different precision>) to throw IllegalArgumentException.");
            return;
         } catch (IllegalArgumentException exception) {
            // as expected
         }

         folder1a.copy(folder1b);
         assertEquals("Expected ExpiryFolders with same ExpiryStrategy to be equal after copy operation.", folder1b, folder1a);
      } finally {
         strategy1.stop();
         strategy2.stop();
      }
   }

   public void doTestExpiryFolder(ExpiryStrategy strategy)
   throws Exception {

      assertEquals(DURATION,             strategy.getTimeOut());
      assertEquals(PRECISION,            strategy.getPrecision());
      assertEquals(DURATION / PRECISION, strategy.getSlotCount());

      // Construct an ExpiryFolder
      ExpiryFolder folder = new ExpiryFolder(NAME, strategy);
      assertEquals(NAME,     folder.getName());
      assertEquals(strategy, folder.getStrategy());
      assertEquals(0,        folder.size());

      // Nothing should be in the ExpiryFolder
      final String KEY_1 = "hello";
      final String VAL_1 = "world";
      assertNull(folder.get(KEY_1));
      assertNull(folder.find(KEY_1));
      assertEquals(0, folder.size());

      // Test get, find and put with null values
      try {
         folder.get(null);
         fail("IllegalArgumentException expected.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.find(null);
         fail("IllegalArgumentException expected.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.put(KEY_1, null);
         fail("IllegalArgumentException expected.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.put(null, VAL_1);
         fail("IllegalArgumentException expected.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.remove(null);
         fail("IllegalArgumentException expected.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Put something in and make sure it is in there indeed
      folder.put(KEY_1, VAL_1);
      assertEquals(VAL_1, folder.find(KEY_1));
      assertEquals(VAL_1, folder.get(KEY_1));
      assertEquals(1,     folder.size());

      // Check expiry
      final String KEY_2 = "something";
      final String VAL_2 = "else";
      folder.put(KEY_2, VAL_2);
      assertEquals(2,     folder.size());
      assertEquals(VAL_2, folder.find(KEY_2));
      assertEquals(VAL_2, folder.get(KEY_2));
      Thread.sleep(DURATION + 1);
      assertEquals(0, folder.size());
      assertNull("Entry should have expired.", folder.find(KEY_2));
      assertEquals(0, folder.size());
      assertNull("Entry should have expired.", folder.get(KEY_2));
      assertEquals(0, folder.size());

      // Test entry removal
      assertNull(folder.remove(KEY_1));
      assertEquals(0, folder.size());
      folder.put(KEY_2, VAL_2);
      assertEquals(VAL_2, folder.find(KEY_2));
      assertEquals(VAL_2, folder.get(KEY_2));
      assertEquals(VAL_2, folder.remove(KEY_2));
      assertEquals(0, folder.size());
      assertNull(folder.remove(KEY_2));
      assertEquals(0, folder.size());
      assertNull(folder.find(KEY_2));
      assertNull(folder.get(KEY_2));
      assertNull(folder.remove("This is a key that was never entered"));
      assertEquals(0, folder.size());

      // Test addition and retrieval of listener
      try {
         folder.addListener(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.removeListener(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      ExpiryFolderListener listener = new ExpiryFolderListener();
      folder.removeListener(listener);

      // Make sure the listeners notified by the ExpiryStrategy will take some
      // time
      ExpiryFolder folder2 = new ExpiryFolder("SomeName", strategy);
      folder2.put(KEY_2, VAL_2);
      folder2.addListener(new TimeEater(DURATION / 2L));
      Thread.sleep(DURATION - PRECISION);

      // Test detailed expiry behavior
      folder.addListener(listener);
      folder.removeListener(listener);
      folder.addListener(listener);
      assertEquals(0, folder.size());
      folder.put(KEY_2, VAL_2);
      assertEquals(1, folder.size());
      final long WAIT_TIME = DURATION * 2L;
      long before = System.currentTimeMillis();
      Thread.sleep(WAIT_TIME);
      assertNull(folder.get(KEY_2));
      assertEquals(0, folder.size());
      long after = System.currentTimeMillis();

      // Listener should have been called exactly once
      assertEquals(1, listener._callbacks.size());

      // Get the one and only Callback object
      Callback cb = (Callback) listener._callbacks.get(0);

      // Source ExpiryFolder should match
      assertTrue("ExpiryFolder passed to listener mismatches real source.",
                 cb._folder == folder);

      // The map should contain expected key/value pair, nothing else
      Map expired = cb._expired;
      assertNotNull(expired);
      assertTrue(expired.size() == 1);
      assertEquals(VAL_2, expired.get(KEY_2));

      Iterator it = expired.keySet().iterator();
      assertTrue(it.hasNext());
      assertEquals(KEY_2, it.next());
      assertFalse(it.hasNext());

      // The entry should have been expired in the right time frame
      long callbackTime = cb._timeStamp - before;
      String message = "Entry expired in "
                     + callbackTime
                     + " ms while folder time-out is "
                     + DURATION
                     + " ms and precision is "
                     + PRECISION
                     + " ms.";
      assertTrue(message, callbackTime >= DURATION);
      assertTrue(message, callbackTime <= (DURATION + PRECISION));
   }

   /**
    * Listener for an ExpiryFolder.
    */
   private class ExpiryFolderListener implements ExpiryListener {

      private ExpiryFolderListener() {
         _callbacks = new ArrayList();
      }

      private final List _callbacks;

      public void expired(ExpiryFolder folder, Map expired) {

         // Create Callback object
         Callback cb   = new Callback();
         cb._timeStamp = System.currentTimeMillis();
         cb._folder    = folder;
         cb._expired   = expired;

         // Store the Callback
         _callbacks.add(cb);
      }
   }

   private class TimeEater implements ExpiryListener {

      private TimeEater(long sleepTime) {
         _sleepTime = sleepTime;
      }

      private final long _sleepTime;

      public void expired(ExpiryFolder folder, Map expired) {

        folder.put(new Object(), new Object());

        try {
           Thread.sleep(_sleepTime);
        } catch (InterruptedException exception) {
           // XXX: ignore
        }
      }
   }

   private class Callback
   {

      private long _timeStamp;
      private ExpiryFolder _folder;
      private Map _expired;
   }
}
