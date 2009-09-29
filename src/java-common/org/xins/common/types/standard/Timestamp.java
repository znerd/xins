/*
 * $Id: Timestamp.java,v 1.41 2007/09/18 11:21:04 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;

/**
 * Standard type <em>_timestamp</em>. A value of this type represents a
 * certain moment in time, with second-precision, without an indication of the
 * time zone.
 *
 * <p>The textual representation of a timestamp is always 14 numeric
 * characters, in the format:
 *
 * <blockquote><em>YYYYMMDDhhmmss</em></blockquote>
 *
 * where:
 *
 * <ul>
 *    <li><em>YYYY</em> is the year, including the century, between 1970 and
 *        2999, for example <code>"2005"</code>.
 *    <li><em>MM</em> is the month of the year, 1-based, for example
 *        <code>"12"</code> for December.
 *    <li><em>DD</em> is the day of the month, 1-based, for example
 *        <code>"31"</code> for the last day of December.
 *    <li><em>hh</em> is the hour of the day, 0-based, for example
 *        <code>"23"</code> for the last hour of the day.
 *    <li><em>mm</em> is the minute within the hour, 0-based, for example
 *        <code>"59"</code> for the last minute within the hour.
 *    <li><em>ss</em> is the second within the minute, 0-based, for example
 *        <code>"59"</code> for the last second within the minute.
 * </ul>
 *
 * <p>Note that all timestamps will be based on the current time zone (see
 * {@link java.util.TimeZone#getDefault()}).
 *
 * <p>A number of milliseconds can be used to indicate a specific instant in
 * time. This number of milliseconds is since the
 * <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
 *
 * @version $Revision: 1.41 $ $Date: 2007/09/18 11:21:04 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class Timestamp extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Timestamp SINGLETON = new Timestamp();

   /**
    * Formatter that converts a date to a string.
    */
   private static final SimpleDateFormat FORMATTER =
      new SimpleDateFormat("yyyyMMddHHmmss");


   /**
    * Constructs a new <code>Timestamp</code> instance.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Timestamp() {
      super("_timestamp", Value.class);
   }
   /**
    * Constructs a <code>Timestamp.Value</code> with the value of the current
    * time.
    *
    * @return
    *    the {@link Value} initialized with the current time,
    *    never <code>null</code>.
    */
   public static Value now() {
      return new Value(System.currentTimeMillis());
   }

   /**
    * Constructs a <code>Timestamp.Value</code> from the specified
    * non-<code>null</code> string.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link Value} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>Timestamp.Value</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Value}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForOptional(String string)
   throws TypeValueException {
      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified <code>Timestamp.Value</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value;
    *    or <code>null</code> if and only if <code>value == null</code>.
    */
   public static String toString(Value value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      return toString(value.getYear(),
                      value.getMonthOfYear(),
                      value.getDayOfMonth(),
                      value.getHourOfDay(),
                      value.getMinuteOfHour(),
                      value.getSecondOfMinute());
   }

   /**
    * Converts the specified combination of a year, month, day, hour,
    * minute and second to a string.
    *
    * @param year
    *    the year, must be &gt;=1970 and &lt;= 2999.
    *
    * @param month
    *    the month of the year, must be &gt;= 1 and &lt;= 12.
    *
    * @param day
    *    the day of the month, must be &gt;= 1 and &lt;= 31.
    *
    * @param hour
    *    the hour of the day, must be &gt;= 0 and &lt;= 23.
    *
    * @param minute
    *    the minute of the hour, must be &gt;= 0 and &lt;= 59.
    *
    * @param second
    *    the second of the minute, must be &gt;= 0 and &lt;= 59.
    *
    * @return
    *    the textual representation of the value in the format
    *    <em>YYYYMMDDhhmmss</em>, never <code>null</code>.
    */
   private static String toString(int year,
                                  int month,
                                  int day,
                                  int hour,
                                  int minute,
                                  int second) {

      // Use a buffer to create the string
      StringBuffer buffer = new StringBuffer(14);

      // Append the year
      buffer.append(year);

      // Append the month
      if (month < 10) {
         buffer.append('0');
      }
      buffer.append(month);

      // Append the day
      if (day < 10) {
         buffer.append('0');
      }
      buffer.append(day);

      // Append the hour
      if (hour < 10) {
         buffer.append('0');
      }
      buffer.append(hour);

      // Append the minute
      if (minute < 10) {
         buffer.append('0');
      }
      buffer.append(minute);

      // Append the second
      if (second < 10) {
         buffer.append('0');
      }
      buffer.append(second);

      return buffer.toString();
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {

      // First check the length
      if (value.length() != 14) {
         throw new TypeValueException(this, value, "String length (" + value.length() + ") is not 14.");
      }

      // Convert all 3 components of the string to integers
      int y, m, d, h, mi, s;
      try {
         y  = Integer.parseInt(value.substring( 0,  4));
         m  = Integer.parseInt(value.substring( 4,  6));
         d  = Integer.parseInt(value.substring( 6,  8));
         h  = Integer.parseInt(value.substring( 8, 10));
         mi = Integer.parseInt(value.substring(10, 12));
         s  = Integer.parseInt(value.substring(12, 14));
      } catch (NumberFormatException cause) {
         throw new TypeValueException(this, value, "Parsing error for int.", cause);
      }

      // Check that the values are in the correct range
      if (y < 0) {
         throw new TypeValueException(this, value, "Parsed year ("    + y  + ") is less than 0.");
      } else if (m < 1) {
         throw new TypeValueException(this, value, "Parsed month ("   + m  + ") is less than 1.");
      } else if (m > 12) {
         throw new TypeValueException(this, value, "Parsed month ("   + m  + ") is greater than 12.");
      } else if (d < 1) {
         throw new TypeValueException(this, value, "Parsed day ("     + d  + ") is less than 1.");
      } else if (d > 31) {
         throw new TypeValueException(this, value, "Parsed day ("     + d  + ") is greater than 31.");
      } else if (mi < 0) {
         throw new TypeValueException(this, value, "Parsed minutes (" + mi + ") is less than 0.");
      } else if (mi > 59) {
         throw new TypeValueException(this, value, "Parsed minutes (" + mi + ") is greater than 59.");
      } else if (s < 0) {
         throw new TypeValueException(this, value, "Parsed seconds (" + s  + ") is less than 0.");
      } else if (s > 59) {
         throw new TypeValueException(this, value, "Parsed seconds (" + s  + ") is greater than 59.");
      }
   }

   @Override
   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
      java.util.Date date;
      try {
         date = format.parse(string);
      } catch (ParseException exception) {
         throw new TypeValueException(this, string); // XXX: Add detail?
      }

      return new Value(date);
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // Convert the Value object to a String
      return toString((Value) value);
   }

   @Override
   public String getDescription() {
      return "A timestamp. The format is YYYYMMDDhhmmss.";
   }

   /**
    * Value for the type <em>_timestamp</em>. Represents a specific moment in
    * time, with second-precision.
    *
    * @version $Revision: 1.41 $
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    *
    * @since XINS 1.0.0
    */
   public static final class Value implements Cloneable {

      /**
       * Calendar representing the moment in time.
       */
      private Calendar _calendar;

      /**
       * Constructs a new timestamp value. The values will not be checked.
       *
       * @param year
       *    the year, including century, e.g. <code>2005</code>.
       *
       * @param month
       *    the month of the year in the range 1-12, e.g. <code>11</code> for
       *    November.
       *
       * @param day
       *    the day of the month in the range 1-31, e.g. <code>1</code> for
       *    the first day of the month.
       *
       * @param hour
       *    the hour of the day in the range 0-23, e.g. <code>22</code> for 10
       *    o'clock at night.
       *
       * @param minute
       *    the minute of the hour in the range 0-59, e.g. <code>0</code> for
       *    first minute of the hour.
       *
       * @param second
       *    the second of the minute in the range 0-59, e.g. <code>0</code>
       *    for the first second of the minute.
       */
      public Value(int year, int month,  int day,
                   int hour, int minute, int second) {

         // Construct the Calendar
         _calendar = Calendar.getInstance();
         _calendar.set(year, month - 1, day, hour, minute, second);
      }

      /**
       * Constructs a new timestamp value based on the specified
       * <code>Calendar</code>.
       *
       * @param calendar
       *    the {@link java.util.Calendar} object to get the exact date from,
       *    cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>calendar == null</code>.
       *
       * @since XINS 1.2.0
       */
      public Value(Calendar calendar)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("calendar", calendar);

         // Initialize fields
         _calendar = (Calendar) calendar.clone();
      }

      /**
       * Constructs a new timestamp value based on the specified
       * <code>java.util.Date</code> object.
       *
       * @param date
       *    the {@link java.util.Date} object to get the exact date from,
       *    cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>date == null</code>.
       *
       * @since XINS 1.2.0
       */
      public Value(java.util.Date date)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("date", date);

         // Construct the Calendar
         _calendar = Calendar.getInstance();
         _calendar.setTime(date);
      }

      /**
       * Constructs a new timestamp value based on the specified number of
       * milliseconds since the UNIX Epoch.
       *
       * @param millis
       *    the number of milliseconds since the
       *    <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
       *
       * @throws IllegalArgumentException
       *    if <code>millis &lt; 0L</code>.
       *
       * @see System#currentTimeMillis()
       *
       * @since XINS 1.2.0
       */
      public Value(long millis) throws IllegalArgumentException {

         // Check preconditions
         if (millis < 0L) {
            throw new IllegalArgumentException("millis (" + millis + " < 0L");
         }

         // Convert the number of milliseconds to a Date object
         java.util.Date date = new java.util.Date(millis);

         // Construct the Calendar
         _calendar = Calendar.getInstance();
         _calendar.setTime(date);
      }

      /**
       * Creates and returns a copy of this object.
       *
       * @return
       *    a copy of this object, never <code>null</code>.
       *
       * @see Object#clone()
       */
      public Object clone() {
         return new Value(_calendar);
      }

      /**
       * Returns the year.
       *
       * @return
       *    the year, between 1970 and 2999 (inclusive).
       */
      public int getYear() {
         return _calendar.get(Calendar.YEAR);
      }

      /**
       * Returns the month of the year.
       *
       * @return
       *    the month of the year, between 1 and 12 (inclusive).
       */
      public int getMonthOfYear() {
         return _calendar.get(Calendar.MONTH) + 1;
      }

      /**
       * Returns the day of the month.
       *
       * @return
       *    the day of the month, between 1 and 31 (inclusive).
       */
      public int getDayOfMonth() {
         return _calendar.get(Calendar.DAY_OF_MONTH);
      }

      /**
       * Returns the hour of the day.
       *
       * @return
       *    the hour of the day, between 0 and 23 (inclusive).
       */
      public int getHourOfDay() {
         return _calendar.get(Calendar.HOUR_OF_DAY);
      }

      /**
       * Returns the minute of the hour.
       *
       * @return
       *    the minute of the hour, between 0 and 59 (inclusive).
       */
      public int getMinuteOfHour() {
         return _calendar.get(Calendar.MINUTE);
      }

      /**
       * Returns the second of the minute.
       *
       * @return
       *    the second of the minute, between 0 and 59 (inclusive).
       */
      public int getSecondOfMinute() {
         return _calendar.get(Calendar.SECOND);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Value)) {
            return false;
         }

         // Compare relevant values
         Value that = (Value) obj;
         return (getYear()           == that.getYear()          )
             && (getMonthOfYear()    == that.getMonthOfYear()   )
             && (getDayOfMonth()     == that.getDayOfMonth()    )
             && (getHourOfDay()      == that.getHourOfDay()     )
             && (getMinuteOfHour()   == that.getMinuteOfHour()  )
             && (getSecondOfMinute() == that.getSecondOfMinute());
      }


      public int hashCode() {
         return _calendar.hashCode();
      }

      /**
       * Converts to a <code>java.util.Date</code> object.
       *
       * @return
       *    the {@link java.util.Date} corresponding to this value.
       *
       * @since XINS 1.2.0
       */
      public java.util.Date toDate() {
         return _calendar.getTime();
      }

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    the textual representation of this timestamp, never
       *    <code>null</code>.
       */
      public String toString() {
         synchronized (FORMATTER) {
            return FORMATTER.format(_calendar.getTime());
         }
      }
   }
}
