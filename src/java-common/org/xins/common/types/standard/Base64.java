/*
 * $Id: Base64.java,v 1.22 2007/09/18 11:21:04 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_base64</em>.
 *
 * @version $Revision: 1.22 $ $Date: 2007/09/18 11:21:04 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.1.0
 */
public class Base64 extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Base64 SINGLETON = new Base64();

   /**
    * The encoding used to convert a String to a byte[] and vice versa.
    */
   private static final Charset CHARSET = Charset.forName("US-ASCII");

   /**
    * The minimum number of bytes this Base64 can have.
    */
   private final int _minimum;

   /**
    * The maximum number of bytes this Base64 can have.
    */
   private final int _maximum;

   /**
    * Constructs a new <code>Base64</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Base64() {
      this("_base64", 0, Integer.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Base64</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param minimum
    *    the minimum length that the byte[] should be.
    *
    * @param maximum
    *    the maximum length that the byte[] should be.
    */
   protected Base64(String name, int minimum, int maximum) {
      super(name, byte[].class);

      _minimum = minimum;
      _maximum = maximum;
   }

   private static final byte[] stringToBytes(String string) {
      return CHARSET.encode(string).array();
   }

   private static final String bytesToString(byte[] bytes) {
      return CHARSET.decode(ByteBuffer.wrap(bytes)).toString();
   }

   /**
    * Converts the specified non-<code>null</code> string base64 value to a
    * <code>byte[]</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>byte[]</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static byte[] fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         return fromStringForOptional(string);
      }
   }

   /**
    * Converts the specified base64 string value to a <code>byte[]</code> value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the byte[], or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static byte[] fromStringForOptional(String string)
   throws TypeValueException {

      if (string == null) {
         return null;
      }

      try {
         byte[] encoded = stringToBytes(string);
         if (!org.apache.commons.codec.binary.Base64.isArrayByteBase64(encoded)) {
            throw new TypeValueException(SINGLETON, string);
         }
         return org.apache.commons.codec.binary.Base64.decodeBase64(encoded);
      } catch (Exception ex) {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified <code>byte[]</code> to a base64 string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the base64 representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(byte[] value) {
      return (value == null) ? null : bytesToString(org.apache.commons.codec.binary.Base64.encodeBase64(value));
   }

   @Override
   protected void checkValueImpl(String string) throws TypeValueException {

      // Convert the string to bytes
      byte[] encoded = stringToBytes(string);
      if (! org.apache.commons.codec.binary.Base64.isArrayByteBase64(encoded)) {
         throw new TypeValueException(this, string, "Invalid character(s) found.");
      }

      // Interpret the bytes as Base64
      byte[] number;
      try {
         number = org.apache.commons.codec.binary.Base64.decodeBase64(encoded);
      } catch (Exception cause) {
         TypeValueException e = new TypeValueException(this, string);
         e.initCause(cause);
         throw e;
      }

      // Check minimum and maximum
      if (number.length < _minimum) {
         throw new TypeValueException(this, string, "Number of bytes (" + number.length + ") is less than the minimum (" + _minimum + ").");
      } else if (number.length > _maximum) {
         throw new TypeValueException(this, string, "Number of bytes (" + number.length + ") is more than the maximum (" + _maximum + ").");
      }
   }

   @Override
   protected Object fromStringImpl(String string) throws TypeValueException {
      try {
         byte[] encoded = stringToBytes(string);
         return org.apache.commons.codec.binary.Base64.decodeBase64(encoded);
      } catch (Exception ex) {
         throw new TypeValueException(SINGLETON, string, ex.getMessage());
      }
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // Convert the argument to a byte array (may throw ClassCastException)
      byte[] b = (byte[]) value;

      // Try encoding the byte array as a Base64 string
      return bytesToString(org.apache.commons.codec.binary.Base64.encodeBase64(b));
   }

   public String getDescription() {
      return "Binary format coded using base64 enconding.";
   }
}
