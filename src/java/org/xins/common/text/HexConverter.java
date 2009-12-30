/*
 * $Id: HexConverter.java,v 1.32 2007/03/16 09:54:59 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Utility class for converting numbers to unsigned hex strings and vice
 * versa.
 *
 * @version $Revision: 1.32 $ $Date: 2007/03/16 09:54:59 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class HexConverter {

   /**
    * The number of characters written when converting a <code>byte</code> to
    * an unsigned hex string.
    */
   private static final int BYTE_LENGTH = 2;

   /**
    * The number of characters written when converting a <code>short</code> to
    * an unsigned hex string.
    */
   private static final int SHORT_LENGTH = 4;

   /**
    * The number of characters written when converting a <code>char</code> to
    * an unsigned hex string.
    */
   private static final int CHAR_LENGTH = 4;

   /**
    * The number of characters written when converting a <code>int</code> to
    * an unsigned hex string.
    */
   private static final int INT_LENGTH = 8;

   /**
    * The number of characters written when converting a <code>long</code> to
    * an unsigned hex string.
    */
   private static final int LONG_LENGTH = 16;

   /**
    * The radix when converting (16).
    */
   private static final byte RADIX = 16;

   /**
    * The radix mask as an <code>int</code>. Equal to {@link #RADIX}<code> -
    * 1</code>.
    */
   private static final int INT_MASK = RADIX - 1;

   /**
    * The radix mask as a <code>long</code>. Equal to {@link #RADIX}<code> -
    * 1L</code>.
    */
   private static final long LONG_MASK = RADIX - 1L;

   /**
    * Array of 2 zero characters.
    */
   private static final char[] TWO_ZEROES = {
      '0', '0'
   };

   /**
    * Array of 4 zero characters.
    */
   private static final char[] FOUR_ZEROES = {
      '0', '0', '0', '0'
   };

   /**
    * Array of 8 zero characters.
    */
   private static final char[] EIGHT_ZEROES = {
      '0', '0', '0', '0', '0', '0', '0', '0'
   };

   /**
    * Array of 16 zero characters.
    */
   private static final char[] SIXTEEN_ZEROES = {
      '0', '0', '0', '0', '0', '0', '0', '0',
      '0', '0', '0', '0', '0', '0', '0', '0'
   };

   /**
    * Array that contains the hexadecimal digits, from 0 to 9 and from a to z.
    */
   private static final char[] DIGITS = {
      '0', '1', '2', '3', '4', '5', '6', '7' ,
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
   };

   /**
    * The '0' character.
    */
   private static final int CHAR_ZERO = (int) '0';

   /**
    * The '9' character.
    */
   private static final int CHAR_NINE = (int) '9';

   /**
    * The 'a' character.
    */
   private static final int CHAR_A = (int) 'a';

   /**
    * The 'f' character.
    */
   private static final int CHAR_F = (int) 'f';

   /**
    * The 'A' character.
    */
   private static final int CHAR_UP_A = (int) 'A';

   /**
    * The 'f' character.
    */
   private static final int CHAR_UP_F = (int) 'F';

   /**
    * The 'a' character lowered by 0xA.
    */
   private static final int CHAR_A_FACTOR = CHAR_A - 10;

   /**
    * The 'A' character lowered by 0xA.
    */
   private static final int CHAR_UP_A_FACTOR = CHAR_UP_A - 10;

   /**
    * Creates a new <code>HexConverter</code> object.
    */
   private HexConverter() {
      // empty
   }

   /**
    * Checks if the specified character is a hexadecimal digit. The following
    * ranges of characters are considered hexadecimal digits:
    *
    * <ul>
    *    <li><code>'0'</code> to <code>'9'</code>
    *    <li><code>'a'</code> to <code>'f'</code>
    *    <li><code>'A'</code> to <code>'F'</code>
    * </ul>
    *
    * @param c
    *    the character to check.
    *
    * @return
    *    <code>true</code> if the specified character is a hexadecimal digit,
    *    <code>false</code> otherwise.
    */
   public static final boolean isHexDigit(char c) {
      return (c >= '0' && c <= '9')
          || (c >= 'a' && c <= 'f')
          || (c >= 'A' && c <= 'F');
   }

   /**
    * Converts the specified <code>byte</code> array to an unsigned number hex
    * string. The number of characters in the returned string will always be
    * equal to the number of input bytes times 2.
    *
    * <p>Since XINS 3.0, this method accepts a byte array of length 0 as
    * input. Such an empty array results in an empty string as output.
    *
    * @param input
    *    the <code>byte[]</code> array to be converted to a hex string,
    *    cannot be <code>null</code>.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 2
    *    times the length of the input array
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == (input.length * 2)</code>).
    *
    * @throws IllegalArgumentException
    *    if <code>input == null</code>.
    */
   public static String toHexString(byte[] input)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("input", input);

      // Empty input is empty output
      if (input.length < 1) {
         return "";
      }

      // Construct a new char array to store the hex digits in
      int length   = input.length;
      char[] chars = new char[length * 2];

      int pos = 0;
      for (int i = 0; i < length; i++) {

         int n = (int) input[i];
         chars[pos++] = DIGITS[(n & 0x000000f0) >> 4];
         chars[pos++] = DIGITS[(n & 0x0000000f)];
      }

      return new String(chars, 0, length * 2);
   }

   /**
    * Converts the specified <code>byte</code> to an unsigned number hex
    * string. The returned string will always consist of 2 hex characters,
    * a zero will be prepended if necessary.
    *
    * @param n
    *    the number to be converted to a hex string.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 2
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == 2</code>).
    */
   public static String toHexString(byte n) {

      // First convert to int, since there are no Java opcodes for bytes
      byte i = (byte) n;

      char[] chars = new char[BYTE_LENGTH];
      chars[0] = DIGITS[(i & 0x000000f0) >> 4];
      chars[1] = DIGITS[(i & 0x0000000f)];

      return new String(chars);
   }

   /**
    * Converts the specified <code>short</code> to an unsigned number hex
    * string. The returned string will always consist of 4 hex characters,
    * zeroes will be prepended as necessary.
    *
    * @param n
    *    the number to be converted to a hex string.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 4
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == 4</code>).
    */
   public static String toHexString(short n) {

      // First convert to int, since there are no Java opcodes for shorts
      int i = ((int) n) & 0x0000ffff;

      char[] chars = new char[SHORT_LENGTH];
      int pos      = SHORT_LENGTH - 1;

      // Convert the number to a hex string until the remainder is 0
      for (; i != 0; i >>>= 4) {
         chars[pos--] = DIGITS[i & INT_MASK];
      }

      // Fill the rest with '0' characters
      for (; pos >= 0; pos--) {
         chars[pos] = '0';
      }

      return new String(chars, 0, SHORT_LENGTH);
   }

   /**
    * Converts the specified <code>char</code> to an unsigned number hex
    * string. The returned string will always consist of 4 hex characters,
    * zeroes will be prepended as necessary.
    *
    * @param n
    *    the character to be converted to a hex string.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 4
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == 4</code>).
    */
   public static String toHexString(char n) {

      // First convert to int, since there are no Java opcodes for shorts
      int i = (int) n;

      char[] chars = new char[CHAR_LENGTH];
      int pos      = CHAR_LENGTH - 1;

      // Convert the number to a hex string until the remainder is 0
      for (; i != 0; i >>>= 4) {
         chars[pos--] = DIGITS[i & INT_MASK];
      }

      // Fill the rest with '0' characters
      for (; pos >= 0; pos--) {
         chars[pos] = '0';
      }

      return new String(chars, 0, CHAR_LENGTH);
   }

   /**
    * Converts the specified <code>int</code> to an unsigned number hex
    * string. The returned string will always consist of 8 hex characters,
    * zeroes will be prepended as necessary.
    *
    * @param n
    *    the number to be converted to a hex string.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 8
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == 8</code>).
    */
   public static String toHexString(int n) {

      char[] chars = new char[INT_LENGTH];
      int pos      = INT_LENGTH - 1;

      // Convert the int to a hex string until the remainder is 0
      for (; n != 0; n >>>= 4) {
         chars[pos--] = DIGITS[n & INT_MASK];
      }

      // Fill the rest with '0' characters
      for (; pos >= 0; pos--) {
         chars[pos] = '0';
      }

      return new String(chars, 0, INT_LENGTH);
   }

   /**
    * Convert the specified <code>long</code> to an unsigned number hex
    * string. The returned string will always consist of 16 hex characters,
    * zeroes will be prepended as necessary.
    *
    * @param n
    *    the number to be converted to a hex string.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 16
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == 16</code>).
    */
   public static String toHexString(long n) {

      char[] chars = new char[LONG_LENGTH];
      int pos      = LONG_LENGTH - 1;

      // Convert the long to a hex string until the remainder is 0
      for (; n != 0; n >>>= 4) {
         chars[pos--] = DIGITS[(int) (n & LONG_MASK)];
      }

      // Fill the rest with '0' characters
      for (; pos >= 0; pos--) {
         chars[pos] = '0';
      }

      return new String(chars, 0, LONG_LENGTH);
   }

   /**
    * Parses a single byte from the specified string by interpreting 2 hex
    * digits.
    *
    * @param s
    *    the string that contains a hexadecimal substring of 2 characters,
    *    cannot be <code>null</code>.
    *
    * @param index
    *    the starting index in the string, must be &gt;= 0.
    *
    * @return
    *    the value of the parsed byte, as a (signed) <code>int</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null || index &lt; 0</code>).
    *
    * @throws NumberFormatException
    *    if any of the characters in the specified range of the string is not
    *    a hex digit (<code>'0'</code> to <code>'9'</code>,
    *    <code>'a'</code> to <code>'f'</code> and
    *    <code>'A'</code> to <code>'F'</code>).
    */
   public static int parseHexByte(String s, int index)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      if (s == null) {
         throw new IllegalArgumentException("s == null");
      } else if (index < 0) {
         throw new IllegalArgumentException("index (" + index + ") < 0");
      }

      int c = (int) s.charAt(index);

      int upper;
      if (c >= CHAR_ZERO && c <= CHAR_NINE) {
         upper = (c - CHAR_ZERO);
      } else if (c >= CHAR_A && c <= CHAR_F) {
         upper = (c - CHAR_A_FACTOR);
      } else if (c >= CHAR_UP_A && c <= CHAR_UP_F) {
         upper = (c - CHAR_UP_A_FACTOR);
      } else {
         throw new NumberFormatException("s.charAt(" + index + ") == '" + s.charAt(index) + '\'');
      }

      // Proceed to next char, which is the lower nibble of the byte
      index++;

      int lower = 0;
      c = (int) s.charAt(index);

      if (c >= CHAR_ZERO && c <= CHAR_NINE) {
         lower = (c - CHAR_ZERO);
      } else if (c >= CHAR_A && c <= CHAR_F) {
         lower = (c - CHAR_A_FACTOR);
      } else if (c >= CHAR_UP_A && c <= CHAR_UP_F) {
         lower = (c - CHAR_UP_A_FACTOR);
      } else {
         throw new NumberFormatException("s.charAt(" + index + ") == '" + s.charAt(index) + '\'');
      }

      upper <<= 4;
      return (upper | lower);
   }

   /**
    * Parses the specified string as a set of hex digits and converts it to a
    * byte array.
    *
    * @param s
    *    the hexadecimal string, cannot be <code>null</code>.
    *
    * @param index
    *    the starting index in the string, must be &gt;= 0.
    *
    * @param length
    *    the number of characters to convert in the string, must be &gt;= 0.
    *
    * @return
    *    the value of the parsed hexadecimal string,
    *    as an array of (signed) bytes.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null || index &lt; 0 || length &lt; 1 || s.{@link String#length() length()} &lt; index + length</code>).
    *
    * @throws NumberFormatException
    *    if any of the characters in the specified range of the string is not
    *    a hex digit (<code>'0'</code> to <code>'9'</code>,
    *    <code>'a'</code> to <code>'f'</code> and
    *    <code>'A'</code> to <code>'F'</code>).
    */
   public static byte[] parseHexBytes(String s, int index, int length)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      if (s == null) {
         throw new IllegalArgumentException("s == null");
      } else if (index < 0) {
         throw new IllegalArgumentException("index (" + index + ") < 0");
      } else if (length < 1) {
         throw new IllegalArgumentException("length (" + length + ") < 1");
      } else if (s.length() < index + length) {
         throw new IllegalArgumentException("s.length() (" + s.length() + ") < index (" + index + ") + length (" + length + ')');
      }

      byte[] bytes = new byte[(length / 2) + (length % 2)];

      // Loop through all characters
      int top = index + length;
      int pos = 0;
      for (int i = index; i < top; i++) {
         int c = (int) s.charAt(i);

         int upper;
         if (c >= CHAR_ZERO && c <= CHAR_NINE) {
            upper = (c - CHAR_ZERO);
         } else if (c >= CHAR_A && c <= CHAR_F) {
            upper = (c - CHAR_A_FACTOR);
         } else if (c >= CHAR_UP_A && c <= CHAR_UP_F) {
            upper = (c - CHAR_UP_A_FACTOR);
         } else {
            throw new NumberFormatException("s.charAt(" + i + ") == '" + s.charAt(i) + '\'');
         }

         // Proceed to next char, which is the lower nibble of the byte
         i++;

         int lower = 0;
         if (i < top) {
            c = (int) s.charAt(i);

            if (c >= CHAR_ZERO && c <= CHAR_NINE) {
               lower = (c - CHAR_ZERO);
            } else if (c >= CHAR_A && c <= CHAR_F) {
               lower = (c - CHAR_A_FACTOR);
            } else if (c >= CHAR_UP_A && c <= CHAR_UP_F) {
               lower = (c - CHAR_UP_A_FACTOR);
            } else {
               throw new NumberFormatException("s.charAt(" + i + ") == '" + s.charAt(i) + '\'');
            }
         }

         upper <<= 4;
         bytes[pos++] = (byte) (upper | lower);
      }

      return bytes;
   }

   /**
    * Parses the 8-digit unsigned hex number in the specified string.
    *
    * @param s
    *    the hexadecimal string, cannot be <code>null</code>.
    *
    * @param index
    *    the starting index in the string, must be &gt;= 0.
    *
    * @return
    *    the value of the parsed unsigned hexadecimal string.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null
    *          || index &lt; 0
    *          || s.{@link String#length() length()} &lt; index + 8</code>).
    *
    * @throws NumberFormatException
    *    if any of the characters in the specified range of the string is not
    *    a hex digit (<code>'0'</code> to <code>'9'</code> and
    *    <code>'a'</code> to <code>'f'</code>).
    */
   public static int parseHexInt(String s, int index)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      if (s == null) {
         throw new IllegalArgumentException("s == null");
      } else if (s.length() < index + 8) {
         throw new IllegalArgumentException("s.length() (" + s.length() + ") < index (" + index + ") + 8 (" + (index + 8) + ')');
      }

      int n = 0;

      // Loop through all characters
      int last = index + 8;
      for (int i = index; i < last; i++) {
         int c = (int) s.charAt(i);
         n <<= 4;
         if (c >= CHAR_ZERO && c <= CHAR_NINE) {
            n |= (c - CHAR_ZERO);
         } else if (c >= CHAR_A && c <= CHAR_F) {
            n |= (c - CHAR_A_FACTOR);
         } else if (c >= CHAR_UP_A && c <= CHAR_UP_F) {
            n |= (c - CHAR_UP_A_FACTOR);
         } else {
            throw new NumberFormatException("s.charAt(" + i + ") == '" + s.charAt(i) + '\'');
         }
      }

      return n;
   }

   /**
    * Parses the specified 8-digit unsigned hex string.
    *
    * @param s
    *    the hexadecimal string, cannot be <code>null</code> and must have
    *    size 8
    *    (i.e. <code>s.</code>{@link String#length() length()}<code> == 8</code>).
    *
    * @return
    *    the value of the parsed unsigned hexadecimal string.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null || s.</code>{@link String#length() length()}<code> != 8</code>.
    *
    * @throws NumberFormatException
    *    if any of the characters in the specified string is not a hex digit
    *    (<code>'0'</code> to <code>'9'</code> and <code>'a'</code> to
    *    <code>'f'</code>).
    */
   public static int parseHexInt(String s)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      if (s == null) {
         throw new IllegalArgumentException("s == null");
      } else if (s.length() != 8) {
         throw new IllegalArgumentException("s.length() != 8");
      }

      return parseHexInt(s, 0);
   }

   /**
    * Parses the 16-digit unsigned hex number in the specified string.
    *
    * @param s
    *    the hexadecimal string, cannot be <code>null</code>.
    *
    * @param index
    *    the starting index in the string, must be &gt;= 0.
    *
    * @return
    *    the value of the parsed unsigned hexadecimal string.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null
    *          || index &lt; 0
    *          || s.{@link String#length() length()} &lt; index + 16</code>).
    *
    * @throws NumberFormatException
    *    if any of the characters in the specified range of the string is not
    *    a hex digit (<code>'0'</code> to <code>'9'</code> and
    *    <code>'a'</code> to <code>'f'</code>).
    */
   public static long parseHexLong(String s, int index)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      if (s == null) {
         throw new IllegalArgumentException("s == null");
      } else if (s.length() < index + 16) {
         throw new IllegalArgumentException("s.length() (" + s.length() + ") < index (" + index + ") + 16 (" + (index + 16) + ')');
      }

      long n = 0L;

      // Loop through all characters
      int last = index + 16;
      for (int i = index; i < last; i++) {
         int c = (int) s.charAt(i);
         n <<= 4;
         if (c >= CHAR_ZERO && c <= CHAR_NINE) {
            n |= (c - CHAR_ZERO);
         } else if (c >= CHAR_A && c <= CHAR_F) {
            n |= (c - CHAR_A_FACTOR);
         } else if (c >= CHAR_UP_A && c <= CHAR_UP_F) {
            n |= (c - CHAR_UP_A_FACTOR);
         } else {
            throw new NumberFormatException("s.charAt(" + i + ") == '" + s.charAt(i) + '\'');
         }
      }

      return n;
   }

   /**
    * Parses the specified 16-digit unsigned hex string.
    *
    * @param s
    *    the hexadecimal string, cannot be <code>null</code> and must have
    *    size 16
    *    (i.e. <code>s.</code>{@link String#length() length()}<code> == 16</code>).
    *
    * @return
    *    the value of the parsed unsigned hexadecimal string.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null || s.</code>{@link String#length() length()}<code> != 16</code>.
    *
    * @throws NumberFormatException
    *    if any of the characters in the specified string is not a hex digit
    *    (<code>'0'</code> to <code>'9'</code> and <code>'a'</code> to
    *    <code>'f'</code>).
    */
   public static long parseHexLong(String s)
   throws IllegalArgumentException, NumberFormatException {

      // Check preconditions
      if (s == null) {
         throw new IllegalArgumentException("s == null");
      } else if (s.length() != 16) {
         throw new IllegalArgumentException("s.length() != 16");
      }

      return parseHexLong(s, 0);
   }
}
