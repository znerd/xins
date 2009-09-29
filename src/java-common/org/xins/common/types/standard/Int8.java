/*
 * $Id: Int8.java,v 1.20 2007/08/27 11:18:20 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_int8</em>.
 *
 * @version $Revision: 1.20 $ $Date: 2007/08/27 11:18:20 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class Int8 extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Int8 SINGLETON = new Int8();

   /**
    * The minimum value that this Int8 can have.
    */
   private final byte _minimum;

   /**
    * The maximum value that this Int8 can have.
    */
   private final byte _maximum;

   /**
    * Constructs a new <code>Int8</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Int8() {
      this("_int8", Byte.MIN_VALUE, Byte.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Int8</code> object (constructor for
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
   protected Int8(String name, byte minimum, byte maximum) {
      super(name, Byte.class);

      _minimum = minimum;
      _maximum = maximum;
   }

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>byte</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>byte</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static byte fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      MandatoryArgumentChecker.check("string", string);

      try {
         return Byte.parseByte(string);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(SINGLETON, string, "Number format error.", cause);
      }
   }

   /**
    * Converts the specified string value to a <code>Byte</code> value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Byte}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Byte fromStringForOptional(String string)
   throws TypeValueException {
      return (string == null) ? null : fromStringForRequired(string);
   }

   /**
    * Converts the specified <code>Byte</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(Byte value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.byteValue());
      }
   }

   /**
    * Converts the specified <code>byte</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(byte value) {
      return String.valueOf(value);
   }

   @Override
   protected void checkValueImpl(String value) throws TypeValueException {
      try {
         byte number = Byte.parseByte(value);
         if (number < _minimum) {
            throw new TypeValueException(this, value, "Number " + number + " is less than minimum (" + _minimum + ").");
         } else if (number > _maximum) {
            throw new TypeValueException(this, value, "Number " + number + " is greater than maximum (" + _maximum + ").");
         }
      } catch (NumberFormatException cause) {
         throw new TypeValueException(this, value, "Parsing error for byte.", cause);
      }
   }

   @Override
   protected Object fromStringImpl(String string)
   throws TypeValueException {
      try {
         return Byte.valueOf(string);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(SINGLETON, string, "Number format error.", cause);
      }
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      Byte b = (Byte) value;
      return b.toString();
   }

   @Override
   public String getDescription() {
      return "An 8-bit signed integer number.";
   }
}
