/*
 * $Id: IPFilter.java,v 1.44 2010/04/29 22:03:32 agoubard Exp $
 *
 * Copyright 2003-2010 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;

/**
 * Filter for IP addresses.
 *
 * <a name="format"></a>
 * <h3>Filter expression format</h3>
 *
 * <p>An <code>IPFilter</code> instance is created using a so-called
 * <em>filter expression</em>. This filter expression specifies the IP address
 * and mask to use for matching a subject IP address.
 *
 * <p>IPv4 filters will only accept IPv4 addresses and IPv6 filters will only
 * accept IPv6 addresses.
 *
 * <h3>Example code</h3>
 *
 * <p>An <code>IPFilter</code> object is
 * created and used as follows:
 *
 * <blockquote><code>IPFilter filter = IPFilter.parseFilter("10.0.0.0/24");
 * <br>if (filter.match("10.0.0.1")) {
 * <br>&nbsp;&nbsp;&nbsp;// IP is granted access
 * <br>} else {
 * <br>&nbsp;&nbsp;&nbsp;// IP is denied access
 * <br>}</code></blockquote>
 *
 * <blockquote><code>IPFilter filter = IPFilter.parseFilter("3FFE:200::/32");
 * <br>if (filter.match("1fff:0:a88:85a3::ac1f:8001")) {
 * <br>&nbsp;&nbsp;&nbsp;// IP is granted access
 * <br>} else {
 * <br>&nbsp;&nbsp;&nbsp;// IP is denied access
 * <br>}</code></blockquote>
 *
 * @version $Revision: 1.44 $ $Date: 2010/04/29 22:03:32 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author Peter Troon
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.1.0
 */
public final class IPFilter {

   /**
    * The IPv4 pattern.
    */
   private static final String IP_4_PATTERN = "((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})";

   /**
    * The IPv6 pattern.
    */
   private static final String IP_6_PATTERN = "((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|" + IP_4_PATTERN +
      "|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:" + IP_4_PATTERN +
      "|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:" + IP_4_PATTERN +
      ")|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:" + IP_4_PATTERN +
      ")|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:" + IP_4_PATTERN +
      ")|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:" + IP_4_PATTERN +
      ")|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:" + IP_4_PATTERN + ")|:)))(%.+)?";

   /**
    * The character that delimits the IP address and the mask of the provided
    * filter.
    */
   private static final char IP_MASK_DELIMETER = '/';

   /**
    * The expression of this filter, cannot be <code>null</code>.
    */
   private final String _expression;

   /**
    * The base IP address, as a <code>String</code>. Never <code>null</code>.
    */
   private final String _baseIPString;

   /**
    * The base IP address.
    */
   private final BitSet _baseIP;

   /**
    * The mask of this filter. Can only have a value between 0 and 128.
    */
   private final int _mask;

   /**
    * <code>true</code> only if the filter is for IPv6 addresses.
    */
   private final boolean _isIPv6Filter;

   /**
    * Creates an <code>IPFilter</code> object for the specified filter
    * expression. The expression consists of a base IP address and a bit
    * count. The bit count indicates how many bits in an IP address must match
    * the bits in the base IP address.
    *
    * @param ipString
    *    the base IP address, as a character string,
    *    should not be <code>null</code>.
    *
    * @param baseIP
    *    the base IP address, as a {@link BitSet},
    *    should not be <code>null</code>.
    *
    * @param mask
    *    the mask, between 0 and 128 (inclusive).
    */
   private IPFilter(String ipString, BitSet baseIP, int mask) {
      _expression   = ipString + IP_MASK_DELIMETER + mask;
      _baseIPString = ipString;
      _baseIP       = baseIP;
      _mask         = mask;
      _isIPv6Filter = ipString.indexOf(':') != -1;
   }

   /**
    * Creates an <code>IPFilter</code> object for the specified filter
    * expression. The expression consists of a base IP address and a bit
    * count. The bit count indicates how many bits in an IP address must match
    * the bits in the base IP address.
    *
    * @param expression
    *    the filter expression, cannot be <code>null</code> and must match
    *    <a href="#format">the format for a filter expression</a>.
    *    if no mask is passed 32 is assumed for IPv4 addresses and 128
    *    for IPv6 addresses.
    *
    * @return
    *    the constructed <code>IPFilter</code> object, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>expression == null</code>.
    *
    * @throws ParseException
    *    if <code>expression</code> does not match the specified format.
    */
   public static final IPFilter parseIPFilter(String expression)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("expression", expression);

      // Find the slash ('/') character
      int slashPosition = expression.indexOf(IP_MASK_DELIMETER);

      String ipString;
      int mask;

      // If we have a slash, then it cannot be at the first or last position
      if (slashPosition >= 0) {
         if (slashPosition == 0 || slashPosition == expression.length() - 1) {
            throw new ParseException("The string \"" + expression + "\" is not a valid IP filter expression.");
         }

         // Split the IP and the mask
         ipString = expression.substring(0, slashPosition);
         mask     = parseMask(expression.substring(slashPosition + 1));

      // If we don't have a slash, then parse the IP address only and assume
      // the mask to be 32 bits
      } else {
         ipString = expression;
         if (ipString.indexOf(':') == -1) {
            mask = 32;
         } else {
            mask = 128;
         }
      }

      BitSet ipBase = ipStringToBitSet(ipString);

      // Create and return an IPFilter object
      return new IPFilter(ipString, ipBase, mask);
   }

   /**
    * Parses the specified mask.
    *
    * @param maskString
    *    the mask string, may not be <code>null</code>.
    *
    * @return
    *    an integer representing the value of the mask, between 0 and 128.
    *
    * @throws ParseException
    *    if the specified string is not a mask between 0 and 128, with no
    *    leading zeroes.
    */
   private static final int parseMask(String maskString)
   throws ParseException {

      // Convert to an int
      int mask;
      try {
         mask = Integer.parseInt(maskString);

      // Catch conversion exception
      } catch (NumberFormatException nfe) {
         throw new ParseException("The mask string \"" + maskString + "\" is not a valid number.");
      }

      // Number must be between 0 and 32
      if (mask < 0 || mask > 128) {
         throw new ParseException("The mask string \"" + maskString + "\" is not a number between 0 and 128.");
      }

      // Disallow a leading zero
      if (maskString.length() >= 2 && maskString.charAt(0) == '0') {
         throw new ParseException("The mask string \"" + maskString + "\" starts with a leading zero.");
      }

      return mask;
   }

   /**
    * Returns the filter expression.
    *
    * @return
    *    the original filter expression, never <code>null</code>.
    */
   public final String getExpression() {
      return _expression;
   }

   /**
    * Returns the base IP address.
    *
    * @return
    *    the base IP address, in the form
    *    <code><em>a</em>.<em>a</em>.<em>a</em>.<em>a</em>/<em>n</em></code>,
    *    where <em>a</em> is a number between 0 and 255, with no leading
    *    zeroes; never <code>null</code>.
    */
   public final String getBaseIP() {
      return _baseIPString;
   }

   /**
    * Returns the mask.
    *
    * @return
    *    the mask, between 0 and 128.
    */
   public final int getMask() {
      return _mask;
   }

   /**
    * Determines if the specified IP address is authorized.
    *
    * <p>Note IPv6 addresses are only accepted by IPv6 ACLs and
    * IPv4 addresses are only accepted by IPv4 ACLs.
    *
    * @param ipString
    *    the IP address of which must be determined if it is authorized,
    *    cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the IP address is authorized to access the
    *    protected resource, otherwise <code>false</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>ipString == null</code>.
    *
    * @throws ParseException
    *    if <code>ip</code> does not match the specified format.
    */
   public final boolean match(String ipString)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("ipString", ipString);

      BitSet ipBits = ipStringToBitSet(ipString);

      if ((ipString.indexOf(':') == -1 && _isIPv6Filter) ||
              (ipString.indexOf(':') != -1 && !_isIPv6Filter)) {
         return false;
      }

      // Short-circuit if mask is 0 bits
      if (_mask == 0) {
         return true;
      }

      // Perform the match
      ipBits.xor(_baseIP);
      int maxLength = ipString.indexOf(':') == -1 ? 32 : 128;
      ipBits.clear(0, maxLength - _mask);
      boolean match = ipBits.isEmpty();

      return match;
   }

   /**
    * Transforms the IP address in a series of bits.
    *
    * @param ipString
    *    the String representation of the IP address, cannot be <code>null</code>
    * @return
    *    the series of bits representing the IP address, never <code>null</code>
    *
    * @throws ParseException
    *    if the IP address is not a valid IP address.
    */
   private static BitSet ipStringToBitSet(String ipString) throws ParseException {
      BitSet ipBits = new BitSet();

      if (!ipString.matches(IP_4_PATTERN) && !ipString.matches(IP_6_PATTERN)) {
         throw new ParseException("The string \"" + ipString + "\" is not a valid IP address as it does not match the patterns.");
      }
      byte[] ipBytes;
      try {
         ipBytes = InetAddress.getByName(ipString).getAddress();
      } catch (UnknownHostException ex) {
         throw new ParseException("The string \"" + ipString + "\" is not a valid IP address.");
      }
      if (ipBytes.length != 4 && ipBytes.length != 16) {
         throw new ParseException("Incorrect transformation as " + ipBytes.length + " bytes array are created for ip " + ipString);
      }
      for (int i = 0; i < ipBytes.length * 8; i++) {
         boolean isTrue = (ipBytes[ipBytes.length - i / 8 - 1] & (1 << (i % 8))) > 0;
         ipBits.set(i, isTrue);
      }
      return ipBits;
   }

   /**
    * Returns a textual representation of this filter. The implementation of
    * this method returns the filter expression passed.
    *
    * @return
    *    a textual presentation, never <code>null</code>.
    */
   public final String toString() {
      return getExpression();
   }
}
