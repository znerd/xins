/*
 * $Id: SAXParserProviderTests.java,v 1.8 2007/03/16 10:30:42 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.xml;

import javax.xml.parsers.SAXParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.xml.SAXParserProvider;

/**
 * Tests for class <code>SAXParserProvider</code>
 *
 * @version $Revision: 1.8 $ $Date: 2007/03/16 10:30:42 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class SAXParserProviderTests extends TestCase {

   /**
    * Constructs a new <code>SAXParserProviderTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public SAXParserProviderTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(SAXParserProviderTests.class);
   }

   /**
    * Tests the <code>SAXParserProvider</code> class.
    */
   public void testSAXParserProvider() throws Exception {
      Object o1, o2, o3, o4, o5;

      o1 = SAXParserProvider.get();
      assertNotNull(o1);
      o2 = SAXParserProvider.get();
      assertTrue("Expected SAXParserProvider to return same SAXParser instance when called twice from the same thread.", o1 == o2);

      TestThread thread = new TestThread();
      thread.start();
      thread.join();
      o3 = thread._result1;
      o4 = thread._result2;
      assertNotNull(o3);
      assertTrue("Expected SAXParserProvider to return same SAXParser instance when called twice from the same thread.", o3 == o4);
      assertFalse("Expected SAXParserProvider to return different SAXParser instance when called from different threads.", o3 == o1);

      o5 = SAXParserProvider.get();
      assertTrue("Expected SAXParserProvider to return same SAXParser instance when called twice from the same thread.", o1 == o5);
   }

   private class TestThread extends Thread {
      private Object _result1;
      private Object _result2;

      public void run() {
         _result1 = SAXParserProvider.get();
         _result2 = SAXParserProvider.get();
      }
   }
}
