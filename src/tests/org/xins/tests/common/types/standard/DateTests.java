package org.xins.tests.common.types.standard;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.standard.Date;

/**
 * Tests for the <code>Date</code> type class.
 *
 * @version $Revision: 1.9 $ $Date: 2007/03/16 10:30:41 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class DateTests extends TestCase {

   /**
    * Constructs a new <code>DateTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DateTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(DateTests.class);
   }

   /**
    * Tests the <code>Date$Value</code> constructor that accepts a
    * <code>Calendar</code> instance.
    */
   public void testDateValue_Calendar() throws Exception {

      Calendar cal = null;
      Date.Value v;
      try {
         v = new Date.Value(cal);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      cal = Calendar.getInstance();
      cal.set(2005, 4, 6);
      v = new Date.Value(cal);
      assertEquals(2005, v.getYear());
      assertEquals(5,    v.getMonthOfYear());
      assertEquals(6,    v.getDayOfMonth());
      assertEquals("20050506", v.toString());
   }

   /**
    * Tests the <code>Date$Value</code> constructor that accepts a
    * <code>Date</code> instance.
    */
   public void testDateValueWithDate() throws Exception {

      java.util.Date d = null;
      Date.Value v;
      try {
         v = new Date.Value(d);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      d = new java.util.Date(2005 - 1900, 4, 6);
      v = new Date.Value(d);
      assertEquals(2005, v.getYear());
      assertEquals(5,    v.getMonthOfYear());
      assertEquals(6,    v.getDayOfMonth());
      assertEquals("20050506", v.toString());
   }

   /**
    * Tests the <code>Date$Value</code> constructor that accepts a
    * <code>long</code>.
    */
   public void testDateValueWithLong() throws Exception {

      long n = -1L;
      Date.Value v;
      try {
         v = new Date.Value(n);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // 2 days after the Epoch (which is the start of January 1, 1970, in the
      // UTC time zone) at 1 o'clock in the afternoon
      long dayInMillis = 24 * 60 * 60 * 1000;
      int halfDay = (int) (dayInMillis / 2L);
      n = 5L * ((long) halfDay);

      // Compensate for the time zone offset
      TimeZone tz = TimeZone.getDefault();
      int offset = tz.getOffset(1, 1970, 0, 2, Calendar.FRIDAY, halfDay);
      n -= offset;

      v = new Date.Value(n);
      assertEquals(1970, v.getYear());
      assertEquals(1,    v.getMonthOfYear());
      assertEquals(3,    v.getDayOfMonth());
      assertEquals("19700103", v.toString());
   }

   /**
    * Tests the <code>Date$Value</code> constructor that accepts a number
    * of <code>int</code> values.
    */
   public void testDateValueWithInts() throws Exception {

      int year=2005, month=12, day=31;
      String asString = ""+year+month+day;
      Date.Value v;

      v = new Date.Value(year, month, day);
      assertEquals(year,     v.getYear());
      assertEquals(month,    v.getMonthOfYear());
      assertEquals(day,      v.getDayOfMonth());
      assertEquals(asString, v.toString());
   }

   /**
    * Tests the <code>equals</code> method in the <code>Date$Value</code>
    * class.
    */
   public void testDateValueEquals() throws Exception {

      Date.Value v1 = new Date.Value(2005, 5, 9);
      Date.Value v2 = new Date.Value(2005, 5, 9);
      Date.Value v3 = (Date.Value) v1.clone();

      assertFalse(v1.equals(null));
      assertFalse(v1.equals("20050509"));
      assertTrue(v1.equals(v2));
      assertTrue(v1.equals(v3));
      assertTrue(v2.equals(v1));
      assertTrue(v2.equals(v3));
      assertTrue(v3.equals(v1));
      assertTrue(v3.equals(v2));

      // Create 2 Calendar instances with a slight offset, but both the same
      // up to the second
      Calendar c1 = Calendar.getInstance();
      Calendar c2 = Calendar.getInstance();
      c1.set(2005, 5, 9, 20, 57, 42);
      c2.set(2005, 5, 9, 20, 57, 42);
      c1.set(Calendar.MILLISECOND, 0);
      c2.set(Calendar.MILLISECOND, 500);

      v1 = new Date.Value(c1);
      v2 = new Date.Value(c2);
      assertEquals(v1, v2);
   }
}
