/*
 * $Id: IPFilterTests.java,v 1.29 2007/03/16 10:30:43 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.server.IPFilter;
import org.xins.common.text.ParseException;

/**
 * Tests for class <code>IPFilter</code>.
 *
 * @version $Revision: 1.29 $ $Date: 2007/03/16 10:30:43 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class IPFilterTests extends TestCase {

   /**
    * Constructs a new <code>IPFilterTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public IPFilterTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(IPFilterTests.class);
   }

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   public void testGetExpression() throws Throwable {

      doTestGetExpression("1.2.3.4/1");
      doTestGetExpression("0.0.0.0/0");
      doTestGetExpression("194.134.168.213/32");
      doTestGetExpression("194.13.1.213/20");
      doTestGetExpression("12.12.12.12");
   }

   private void doTestGetExpression(String expression)
   throws Throwable {

      IPFilter filter = IPFilter.parseIPFilter(expression);
      assertNotNull(filter);

      if (expression.indexOf('/') >= 0) {
         assertEquals(expression, filter.getExpression());
         assertEquals(expression, filter.getExpression());
      } else {
         assertEquals(expression + "/32", filter.getExpression());
      }
   }

   public void testParseIPFilter() throws Throwable {

      try {
         IPFilter.parseIPFilter(null);
         fail("IPFilter.parseIPFilter(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Test invalid patterns
      doTestParseIPFilter_INVALID("abcd");
      doTestParseIPFilter_INVALID("abcd/24");
      doTestParseIPFilter_INVALID("a.b.c.d/12");
      doTestParseIPFilter_INVALID("1.");
      doTestParseIPFilter_INVALID("1.2.");
      doTestParseIPFilter_INVALID("1.2.3");
      doTestParseIPFilter_INVALID("1.2.3.");
      doTestParseIPFilter_INVALID("1./");
      doTestParseIPFilter_INVALID("1.2./");
      doTestParseIPFilter_INVALID("1.2.3/");
      doTestParseIPFilter_INVALID("1.2.3./");
      doTestParseIPFilter_INVALID("1.2.3.4/");
      doTestParseIPFilter_INVALID("1./0");
      doTestParseIPFilter_INVALID("1.2./0");
      doTestParseIPFilter_INVALID("1.2.3/0");
      doTestParseIPFilter_INVALID("1.2.3./0");
      doTestParseIPFilter_INVALID("1.2.3.4/a");
      doTestParseIPFilter_INVALID("1.2.3.4/-1");
      doTestParseIPFilter_INVALID(" 1.2.3.4/0");
      doTestParseIPFilter_INVALID("1.2.3.4/0 ");
      doTestParseIPFilter_INVALID(" 1.2.3.4/0 ");
      doTestParseIPFilter_INVALID("1.2.3.4/5a");
      doTestParseIPFilter_INVALID("1.2.3.4a/5");
      doTestParseIPFilter_INVALID("1.2.3.4//5");
      doTestParseIPFilter_INVALID("01.2.3.4/32");
      doTestParseIPFilter_INVALID("1.02.3.4/32");
      doTestParseIPFilter_INVALID("1.2.3.4/00");
      doTestParseIPFilter_INVALID("1.2.3.4/01");
      doTestParseIPFilter_INVALID("1.2.3.4/032");
      doTestParseIPFilter_INVALID("1.2.3.4/33");
      doTestParseIPFilter_INVALID("1.2.3.4/1234567890123456");
      doTestParseIPFilter_INVALID("1.2.3.4.5/0");

      // Test valid patterns
      for (int i = 0; i <= 32; i++) {
         doTestParseIPFilter("1.2.3.4",         i);
         doTestParseIPFilter("1.20.3.4",        i);
         doTestParseIPFilter("10.20.30.40",     i);
         doTestParseIPFilter("1.102.3.4",       i);
         doTestParseIPFilter("194.134.168.213", i);
      }

      doTestParseIPFilter("1.2.3.4");
      doTestParseIPFilter("12.12.12.12");
      doTestParseIPFilter("194.134.168.213");
   }

   private void doTestParseIPFilter_INVALID(String expression)
   throws Throwable {

      try {
         IPFilter.parseIPFilter(expression);
         fail("IPFilter.parse(\"" + expression + "\") should throw a ParseException.");
      } catch (ParseException exception) {
         // as expected
      }
   }

   private void doTestParseIPFilter(String baseIP, int mask)
   throws Throwable {

      String expression = baseIP + '/' + String.valueOf(mask);
      IPFilter filter = IPFilter.parseIPFilter(expression);
      assertNotNull(filter);

      assertEquals(baseIP,              filter.getBaseIP());
      assertEquals(mask,                filter.getMask());
      assertEquals(baseIP + '/' + mask, filter.toString());
   }

   private void doTestParseIPFilter(String baseIP)
   throws Throwable {

      IPFilter filter = IPFilter.parseIPFilter(baseIP);
      assertNotNull(filter);

      final int mask = 32;

      assertEquals(baseIP,              filter.getBaseIP());
      assertEquals(mask,                filter.getMask());
      assertEquals(baseIP + '/' + mask, filter.toString());
   }

   public void testMatch() throws Throwable {

      IPFilter filter = IPFilter.parseIPFilter("194.134.168.213/32");
      assertNotNull(filter);

      try {
         filter.match(null);
         fail("IPFilter.match(null) should throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      doTestMatch(filter, "abcd",                     false, false);
      doTestMatch(filter, "abcd",                     false, false);
      doTestMatch(filter, "abcd/24",                  false, false);
      doTestMatch(filter, "a.b.c.d",                  false, false);
      doTestMatch(filter, "a.b.c.d/12",               false, false);
      doTestMatch(filter, "1.2.3.4/",                 false, false);
      doTestMatch(filter, "1.2.3.4/a",                false, false);
      doTestMatch(filter, "1.2.3.4/-1",               false, false);
      doTestMatch(filter, "1.2.3.4/0",                false, false);
      doTestMatch(filter, "1.2.3.4/5",                false, false);
      doTestMatch(filter, "1.2.3.4/5a",               false, false);
      doTestMatch(filter, "1.2.3.4a/5",               false, false);
      doTestMatch(filter, "1.2.3.4//5",               false, false);
      doTestMatch(filter, "1.2.3.4/32",               false, false);
      doTestMatch(filter, "1.2.3.4/33",               false, false);
      doTestMatch(filter, "1.2.3.4/1234567890123456", false, false);
      doTestMatch(filter, "1.",                       false, false);
      doTestMatch(filter, "1.2",                      false, false);
      doTestMatch(filter, "1.2.",                     false, false);
      doTestMatch(filter, "1.2.3",                    false, false);
      doTestMatch(filter, "1.2.3.",                   false, false);
      doTestMatch(filter, "1.2.34.",                  false, false);
      doTestMatch(filter, "1.2.3.4",                  true,  false);
      doTestMatch(filter, "1.2.3.4.5",                false, false);
      doTestMatch(filter, "01.2.3.4/32",              false, false);
      doTestMatch(filter, "1.02.3.4/32",              false, false);
      doTestMatch(filter, "1.102.3.4/32",             false, false);
      doTestMatch(filter, "1.2.3.4/00",               false, false);
      doTestMatch(filter, "1.2.3.4/01",               false, false);
      doTestMatch(filter, "194.103.168.213",          true,  false);
      doTestMatch(filter, "194.134.168.213",          true,  true);
      doTestMatch(filter, "194.134.168.212",          true,  false);
      doTestMatch(filter, "194.134.168.214",          true,  false);

      filter = IPFilter.parseIPFilter("194.134.168.213/32");
      assertNotNull(filter);
      doTestMatch(filter, "a",               false, false);
      doTestMatch(filter, "194.134.168.211", true,  false);
      doTestMatch(filter, "194.134.168.212", true,  false);
      doTestMatch(filter, "194.134.168.213", true,  true);
      doTestMatch(filter, "194.134.168.214", true,  false);

      filter = IPFilter.parseIPFilter("194.134.168.213/31");
      assertNotNull(filter);
      doTestMatch(filter, "a",               false, false);
      doTestMatch(filter, "194.134.168.211", true,  false);
      doTestMatch(filter, "194.134.168.212", true,  true);
      doTestMatch(filter, "194.134.168.213", true,  true);
      doTestMatch(filter, "194.134.168.214", true,  false);

      filter = IPFilter.parseIPFilter("1.2.3.4/0");
      assertNotNull(filter);
      doTestMatch(filter, "a",               false, false);
      doTestMatch(filter, "1.2.3.4",         true,  true);
      doTestMatch(filter, "194.134.168.213", true,  true);
      doTestMatch(filter, "194.134.168.212", true,  true);
      doTestMatch(filter, "194.134.168.211", true,  true);
      doTestMatch(filter, "194.134.168.214", true,  true);

      filter = IPFilter.parseIPFilter("1.2.3.4/24");
      assertNotNull(filter);
      doTestMatch(filter, "a",               false, false);
      doTestMatch(filter, "1.2.3.4",         true,  true);
      doTestMatch(filter, "1.2.3.128",       true,  true);
      doTestMatch(filter, "1.2.3.132",       true,  true);
      doTestMatch(filter, "1.2.3.255",       true,  true);
      doTestMatch(filter, "1.2.4.4",         true,  false);
      doTestMatch(filter, "1.2.2.4",         true,  false);
      doTestMatch(filter, "1.2.3.0",         true,  true);
   }

   private void doTestMatch(IPFilter filter,
                            String   ip,
                            boolean  validIP,
                            boolean  shouldBeAuth)
   throws Throwable {

      if (validIP) {
         boolean auth = filter.match(ip);
         if (shouldBeAuth && !auth) {
            fail("IPFilter.match(\"" + ip + "\") should return true for \"" + filter + "\".");
         } else if (!shouldBeAuth && auth) {
            fail("IPFilter.match(\"" + ip + "\") should return false for \"" + filter + "\".");
         }
      } else {
         try {
            filter.match(ip);
            fail("IPFilter.match(\"" + ip + "\") should throw a ParseException.");
            return;
         } catch (ParseException exception) {
            // as expected
         }
      }
   }
}
