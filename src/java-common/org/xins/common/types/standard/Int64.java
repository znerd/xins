/*
 * $Id: Int64.java,v 1.20 2007/08/27 11:18:20 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_int64</em>.
 *
 * @version $Revision: 1.20 $ $Date: 2007/08/27 11:18:20 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class Int64 extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Int64 SINGLETON = new Int64();

   /**
    * The minimum value that this Int34 can have.
    */
   private final long _minimum;

   /**
    * The maximum value that this Int34 can have.
    */
   private final long _maximum;

   /**
    * Constructs a new <code>Int64</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Int64() {
      this("_int64", Long.MIN_VALUE, Long.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Int64</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param minimum
    *    the minimum for the value.
    *
    * @param maximum
    *    the maximum for the value.
    */
   protected Int64(String name, long minimum, long maximum) {
      super(name, Long.class);

      _minimum = minimum;
      _maximum = maximum;
   }

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>long</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>long</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static long fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         try {
            return Long.parseLong(string);
         } catch (NumberFormatException cause) {
            throw new TypeValueException(SINGLETON, string, cause);
         }
      }
   }

   /**
    * Converts the specified string value to a <code>Long</code> value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Long}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Long fromStringForOptional(String string)
   throws TypeValueException {
      return (string == null) ? null : fromStringForRequired(string);
   }

   /**
    * Converts the specified <code>Long</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(Long value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.longValue());
      }
   }

   /**
    * Converts the specified <code>long</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(long value) {
      return String.valueOf(value);
   }

   @Override
   protected void checkValueImpl(String value) throws TypeValueException {
      try {
         long number = Long.parseLong(value);
         if (number < _minimum) {
            throw new TypeValueException(this, value, "Number " + number + " is less than minimum (" + _minimum + ").");
         } else if (number > _maximum) {
            throw new TypeValueException(this, value, "Number " + number + " is greater than maximum (" + _maximum + ").");
         }
      } catch (NumberFormatException cause) {
         throw new TypeValueException(this, value, "Parsing error for long.", cause);
      }
   }

   @Override
   protected Object fromStringImpl(String string) {
      return Long.valueOf(string);
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      Long l = (Long) value;
      return l.toString();
   }

   @Override
   public String getDescription() {
      return "A 64-bit signed integer number.";
   }
}
