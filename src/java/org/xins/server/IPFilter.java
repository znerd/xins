/*
 * $Id: IPFilter.java,v 1.42 2007/03/16 09:55:00 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.net.IPAddressUtils;
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
 * <p>A filter expression must match the following format:
 *
 * <blockquote><code>"<em>a</em>.<em>a</em>.<em>a</em>.<em>a</em>"</code>,
 * optionally followed by: <code>/<em>n</em></code>, where <em>a</em> is a
 * number between 0 and 255, with no leading zeroes, and <em>n</em> is a
 * number between 0 and 32, no leading zeroes; if <em>n</em> is not
 * specified.</blockquote>
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
 * @version $Revision: 1.42 $ $Date: 2007/03/16 09:55:00 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author Peter Troon
 *
 * @since XINS 1.0.0
 */
public final class IPFilter {

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
   private final int _baseIP;

   /**
    * The mask of this filter. Can only have a value between 0 and 32.
    */
   private final int _mask;

   /**
    * The shift value, which equals <code>32 - </code>{@link #_mask}. Always
    * between 0 and 32.
    */
   private final int _shift;

   /**
    * Creates an <code>IPFilter</code> object for the specified filter
    * expression. The expression consists of a base IP address and a bit
    * count. The bit count indicates how many bits in an IP address must match
    * the bits in the base IP address.
    *
    * @param ipString
    *    the base IP address, as a character string, should not be
    *    <code>null</code>.
    *
    * @param ip
    *    the base IP address, as an <code>int</code>.
    *
    * @param mask
    *    the mask, between 0 and 32 (inclusive).
    */
   private IPFilter(String ipString, int ip, int mask) {
      _expression   = ipString + IP_MASK_DELIMETER + mask;
      _baseIPString = ipString;
      _baseIP       = ip;
      _mask         = mask;
      _shift        = 32 - _mask;
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
    *    then 32 is assumed.
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
         mask     = 32;
      }

      // Convert the IP string to an int
      int ip = IPAddressUtils.ipToInt(ipString);

      // Create and return an IPFilter object
      return new IPFilter(ipString, ip, mask);
   }

   /**
    * Parses the specified mask.
    *
    * @param maskString
    *    the mask string, may not be <code>null</code>.
    *
    * @return
    *    an integer representing the value of the mask, between 0 and 32.
    *
    * @throws ParseException
    *    if the specified string is not a mask between 0 and 32, with no
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
      if (mask < 0 || mask > 32) {
         throw new ParseException("The mask string \"" + maskString + "\" is not a number between 0 and 32.");
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
    *    the mask, between 0 and 32.
    */
   public final int getMask() {
      return _mask;
   }

   /**
    * Determines if the specified IP address is authorized.
    *
    * @param ipString
    *    the IP address of which must be determined if it is authorized,
    *    cannot be <code>null</code> and must match the form:
    *    <code><em>a</em>.<em>a</em>.<em>a</em>.<em>a</em>/<em>n</em></code>,
    *    where <em>a</em> is a number between 0 and 255, with no leading
    *    zeroes.
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

      // Convert the IP string to an 'int'
      int ip = IPAddressUtils.ipToInt(ipString);

      // Short-circuit if mask is 0 bits
      if (_mask == 0) {
         return true;
      }

      // Perform the match
      boolean match = (ip >> _shift) == (_baseIP >> _shift);

      return match;
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
