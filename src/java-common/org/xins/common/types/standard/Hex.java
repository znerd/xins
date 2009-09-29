/*
 * $Id: Hex.java,v 1.12 2007/08/27 11:18:20 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.HexConverter;

/**
 * Standard type <em>_hex</em>.
 *
 * @version $Revision: 1.12 $ $Date: 2007/08/27 11:18:20 $
 * @author gveiog
 *
 * @since XINS 1.5.0.
 */
public class Hex extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Hex SINGLETON = new Hex();

   /**
    * The minimum number of bytes this Hex can have.
    */
   private final int _minimum;

   /**
    * The maximum number of bytes this Hex can have.
    */
   private final int _maximum;

   /**
    * Constructs a new <code>Hex</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Hex() {
      this("_hex", 0, Integer.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Hex</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param minimum
    *    the minimum for the value.# minimum number of bytes this Hex can have
    *
    * @param maximum
    *    the maximum for the value.# maximum number of bytes this Hex can have
    */
   protected Hex(String name, int minimum, int maximum) {
      super(name, byte[].class);

      _minimum = minimum;
      _maximum = maximum;
   }

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>byte[]</code> value.
    *
    * @param string
    *    the hexadecimal string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>byte[]</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.If the string does not have a hexadecimal value or have a character
    *    that is not hexadecimal digit.
    */
   public static byte[] fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      int index = 0;
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         try {

            // this method converts the string to byte and also checks if the string has hex digits
            return HexConverter.parseHexBytes(string,index,string.length());

         } catch (Exception ex){
            throw new TypeValueException(SINGLETON, string);
         }
      }
   }

   /**
    * Converts the specified string value to a <code>byte[]</code> value.
    *
    * @param string
    *    the hexadecimal string to convert, can be <code>null</code>.
    *
    * @return
    *    the byte[], or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.If the string does not have a hexadecimal value or
    *    have a character that is not hexadecimal digit.
    */
   public static byte[] fromStringForOptional(String string)
   throws TypeValueException {
      int index = 0;
      if (string == null) {
         return null;
      }
      try {
         return HexConverter.parseHexBytes(string,index,string.length());

      } catch (Exception e){
         throw new TypeValueException(SINGLETON, string);
      }
   }


   /**
    * Converts the specified <code>byte[]</code> to a hexadecimal string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(byte[] value) {
      if (value == null) {
         return null;
      } else {
         return HexConverter.toHexString(value);
      }
   }

   @Override
   protected void checkValueImpl(String value) throws TypeValueException {
      try {
         for (int i = 0; i < value.length(); i++) {
            if (! HexConverter.isHexDigit(value.charAt(i))) {
               throw new TypeValueException(this, value, "Character " + i + " is not a hex digit.");
            }
         }
         byte[] number = HexConverter.parseHexBytes(value, 0, value.length());
         if (number.length < _minimum) {
            throw new TypeValueException(this, value, "Number of digits (" + number.length + ") is less than the minimum (" + _minimum + ").");
         } else if (number.length > _maximum) {
            throw new TypeValueException(this, value, "Number of digits (" + number.length + ") is greater than the maximum (" + _maximum + ").");
         }

      } catch (Exception cause) {
         throw new TypeValueException(this, value, cause);
      }
   }

   @Override
   protected Object fromStringImpl(String string) {
      return HexConverter.parseHexBytes(string, 0, string.length());
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // Convert the argument to a byte array (may throw ClassCastException)
      byte[] b = (byte[]) value;

      // Try converting the byte array as a Hex string
      try {
         return HexConverter.toHexString(b);
      } catch (Exception e) {

         throw new TypeValueException(SINGLETON, new String(b), e.getMessage());
      }
   }

   @Override
   public String getDescription() {
      return "Binary data where each byte is represented by its hexadecimal value.";
   }
}
