/*
 * $Id: AccessRuleListTests.java,v 1.20 2007/07/24 14:30:39 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.server.AccessRuleList;

/**
 * Tests for class <code>AccessRuleList</code>.
 *
 * @version $Revision: 1.20 $ $Date: 2007/07/24 14:30:39 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class AccessRuleListTests extends TestCase {

   /**
    * Constructs a new <code>AccessRuleListTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public AccessRuleListTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(AccessRuleListTests.class);
   }

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   public void testParseAccessRuleList() throws Throwable {

      try {
         AccessRuleList.parseAccessRuleList(null, 0);
         fail("AccessRule.parseAccessRuleList(null,0) should throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         AccessRuleList.parseAccessRuleList(null, 1);
         fail("AccessRule.parseAccessRuleList(null,1) should throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      AccessRuleList arl = AccessRuleList.parseAccessRuleList("", 1);
      assertNotNull(arl);
      assertEquals(0, arl.getRuleCount());

      arl = AccessRuleList.parseAccessRuleList("", 1);
      assertNotNull(arl);
      assertEquals(0, arl.getRuleCount());

      arl = AccessRuleList.parseAccessRuleList(" \t\n\r", 1);
      assertNotNull(arl);
      assertEquals(0, arl.getRuleCount());

      int interval = 60;
      arl = AccessRuleList.parseAccessRuleList(" \r\nallow 194.134.168.213/32 *\t", interval);
      assertNotNull(arl);
      assertEquals(1, arl.getRuleCount());
      Boolean allow = arl.isAllowed("194.134.168.213", "_GetVersion", null);
      assertEquals("Expected AccessRuleList(" + arl + ") to allow 194.134.168.213 to access function \"_GetVersion\".", Boolean.TRUE, allow);

      arl = AccessRuleList.parseAccessRuleList(" \r\nallow 194.134.168.213/32 *\t", 1);
      assertNotNull(arl);
      assertEquals(1, arl.getRuleCount());
      allow = arl.isAllowed("194.134.168.213", "_GetVersion", null);
      assertEquals("Expected AccessRuleList(" + arl + ") to allow 194.134.168.213 to access function \"_GetVersion\".", Boolean.TRUE, allow);

      arl = AccessRuleList.parseAccessRuleList(" \r\nallow 194.134.168.213/32 *\t;\ndeny 1.2.3.4/0 * ", interval);
      assertNotNull(arl);
      assertEquals(2, arl.getRuleCount());
      // TODO: More tests
   }
}
