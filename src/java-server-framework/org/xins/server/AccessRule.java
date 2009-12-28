/*
 * $Id: AccessRule.java,v 1.49 2007/09/18 08:45:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.ParseException;
import org.xins.common.text.SimplePatternParser;

/**
 * Access rule. This class can take a character string to produce an
 * {@link AccessRule} object from it.
 *
 * <h3>Descriptor format</h3>
 *
 * <p>A descriptor must comply to the following format:
 * <ul>
 *    <li>start with either <code>"allow"</code> or <code>"deny"</code>;
 *    <li>followed by any number of white space characters;
 *    <li>followed by a valid IP address;
 *    <li>followed by a slash character (<code>'/'</code>);
 *    <li>followed by a mask between 0 and 32 in decimal format, without
 *        leading zeroes;
 *    <li>followed by any number of white space characters;
 *    <li>followed by a simple pattern, see class {@link SimplePatternParser}.
 * </ul>
 *
 * <h3>Descriptor examples</h3>
 *
 * <p>Example of access rule descriptors:
 *
 * <dl>
 *    <dt><code>"allow&nbsp;194.134.168.213/32&nbsp;*"</code></dt>
 *    <dd>Allows 194.134.168.213 to access any function.</dd>
 *
 *    <dt><code>"deny&nbsp;194.134.168.213/24&nbsp;_*"</code></dt>
 *    <dd>Denies all 194.134.168.x IP addresses to access any function
 *        starting with an underscore (<code>'_'</code>).</dd>
 * </dl>
 *
 * @version $Revision: 1.49 $ $Date: 2007/09/18 08:45:06 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:chris.gilbride@orange-ftgroup.com">Chris Gilbride</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public final class AccessRule implements AccessRuleContainer {

   /**
    * If the access method is 'allow' or not.
    */
   private final boolean _allow;

   /**
    * The IP address filter used to create the access rule. Cannot be
    * <code>null</code>.
    */
   private final IPFilter _ipFilter;

   /**
    * The function name pattern. Cannot be <code>null</code>.
    */
   private final Pattern _functionNameRegex;

   /**
    * The calling convention name pattern. Cannot be <code>null</code>.
    */
   private final Pattern _conventionNameRegex;

   /**
    * String representation of this object. Cannot be <code>null</code>.
    */
   private final String _asString;

   /**
    * Flag that indicates whether this object is disposed.
    */
   private boolean _disposed;

   /**
    * Constructs a new <code>AccessRule</code>.
    *
    * @param allow
    *    flag that indicates if this rule grants access (<code>true</code>) or
    *    denies access (<code>false</code>).
    *
    * @param ipFilter
    *    filter used for matching (or not) IP addresses, cannot be
    *    <code>null</code>.
    *
    * @param asString
    *    textual presentation of this access rule, cannot be
    *    <code>null</code>.
    *
    * @param functionNameRegex
    *    regular expression used for matching (or not) a function name; cannot
    *    be <code>null</code>.
    *
    * @param conventionNameRegex
    *    regular expression used for matching (or not) a calling convention name; cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>ipFilter          == null
    *          || functionNameRegex == null
    *          || conventionNameRegex == null
    *          || asString          == null</code>.
    */
   private AccessRule(boolean      allow,
                      IPFilter     ipFilter,
                      Pattern functionNameRegex,
                      Pattern conventionNameRegex,
                      String       asString)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("ipFilter",          ipFilter,
                                     "functionNameRegex", functionNameRegex,
                                     "conventionNameRegex", conventionNameRegex,
                                     "asString",          asString);

      // Store the data
      _allow             = allow;
      _ipFilter          = ipFilter;
      _functionNameRegex = functionNameRegex;
      _conventionNameRegex = conventionNameRegex;
      _asString          = asString;
   }

   /**
    * Parses the specified character string to construct a new
    * <code>AccessRule</code> object.
    *
    * @param descriptor
    *    the access rule descriptor, the character string to parse, cannot be
    *    <code>null</code>.
    *
    * @return
    *    an {@link AccessRule} instance, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws ParseException
    *    If there was a parsing error.
    */
   public static AccessRule parseAccessRule(String descriptor)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      StringTokenizer tokenizer = new StringTokenizer(descriptor," \t\n\r");

      // Determine if it is an 'allow' or a 'deny' rule
      boolean allow;
      String sAllow = nextToken(descriptor, tokenizer);
      if ("allow".equals(sAllow)) {
         allow = true;
      } else if ("deny".equals(sAllow)) {
         allow = false;
      } else {
         String message = "First token of descriptor is \""
                        + sAllow
                        + "\", instead of either 'allow' or 'deny'.";
         throw new ParseException(message);
      }

      // Determine the IP address filter
      String   sFilter = nextToken(descriptor, tokenizer);
      IPFilter filter  = IPFilter.parseIPFilter(sFilter);

      SimplePatternParser parser   = new SimplePatternParser();
      // Determine the function the access is to be checked for
      String functionPatternString = nextToken(descriptor, tokenizer);
      Pattern      functionPattern = parser.parseSimplePattern(functionPatternString);

      // Determine the function the access is to be checked for
      String conventionPatternString = "*";
      if (tokenizer.hasMoreTokens()) {
         conventionPatternString = tokenizer.nextToken();
      }
      Pattern conventionPattern = parser.parseSimplePattern(conventionPatternString);

      // Construct a description
      String asString = sAllow + ' ' + filter.toString() + ' ' +
            functionPatternString + ' ' + conventionPatternString;

      return new AccessRule(allow, filter, functionPattern, conventionPattern, asString);
   }

   /**
    * Returns the next token in the descriptor.
    *
    * @param descriptor
    *    the original descriptor, useful when constructing the message for a
    *    {@link ParseException}, when appropriate, should not be
    *    <code>null</code>.
    *
    * @param tokenizer
    *    the {@link StringTokenizer} to retrieve the next token from, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the next token, never <code>null</code>.
    *
    * @throws ParseException
    *    if <code>tokenizer.{@link StringTokenizer#hasMoreTokens()
    *    hasMoreTokens}() == false</code>.
    */
   private static String nextToken(String          descriptor,
                                   StringTokenizer tokenizer)
   throws ParseException {

      if (! tokenizer.hasMoreTokens()) {
         String message = "The string \""
                        + descriptor
                        + "\" is invalid as an access rule descriptor. "
                        + "More tokens expected.";
         throw new ParseException(message);
      } else {
         return tokenizer.nextToken();
      }
   }

   /**
    * Returns if this rule is an <em>allow</em> or a <em>deny</em> rule.
    *
    * @return
    *    <code>true</code> if this is an <em>allow</em> rule, or
    *    <code>false</code> if this is a <em>deny</em> rule.
    */
   public boolean isAllowRule() {
      return _allow;
   }

   /**
    * Returns the IP filter.
    *
    * @return
    *    the {@link IPFilter} associated with this access rule, never
    *    <code>null</code>.
    */
   public IPFilter getIPFilter() {
      return _ipFilter;
   }

   /**
    * Determines if the specified IP address and function match this rule.
    *
    * <p>Calling this function is equivalent to calling:
    *
    * <blockquote><code>{@link #isAllowed(String,String,String) isAllowed}(ip,
    * functionName,conventionName) != null</code></blockquote>
    *
    * @param ip
    *    the IP address to match, cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function to match, cannot be <code>null</code>.
    *
    * @param conventionName
    *    the name of the calling convention to match, can be <code>null</code>.
    *
    * @return
    *    <code>true</code> if this rule matches, <code>false</code> otherwise.
    *
    * @throws IllegalStateException
    *    if this access rule is disposed (<em>since XINS 1.3.0</em>).
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    *
    * @throws ParseException
    *    if the specified IP address cannot be parsed.
    *
    * @since XINS 2.1
    */
   public boolean match(String ip, String functionName, String conventionName)
   throws IllegalStateException, IllegalArgumentException, ParseException {

      // Check state
      if (_disposed) {
         String detail = "This AccessRule is disposed.";
         Utils.logProgrammingError(detail);
         throw new IllegalStateException(detail);
      }

      // Delegate to the isAllowed method
      return isAllowed(ip, functionName, conventionName) != null;
   }

   /**
    * Determines if the specified IP address is allowed to access the
    * specified function, returning a <code>Boolean</code> object or
    * <code>null</code>.
    *
    * <p>This method finds the first matching rule and then returns the
    * <em>allow</em> property of that rule (see
    * {@link AccessRule#isAllowRule()}). If there is no matching rule, then
    * <code>null</code> is returned.
    *
    * @param ip
    *    the IP address, cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param conventionName
    *    the name of the calling convention, can be <code>null</code>.
    *
    * @return
    *    {@link Boolean#TRUE} if the specified IP address is allowed to access
    *    the specified function, {@link Boolean#FALSE} if it is disallowed
    *    access or <code>null</code> if there is no match.
    *
    * @throws IllegalStateException
    *    if this object is disposed (<em>since XINS 1.3.0</em>).
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    *
    * @throws ParseException
    *    if the specified IP address is malformed.
    *
    * @since XINS 2.1
    */
   public Boolean isAllowed(String ip, String functionName, String conventionName)
   throws IllegalStateException, IllegalArgumentException, ParseException {

      // Check state
      if (_disposed) {
         String detail = "This AccessRule is disposed.";
         Utils.logProgrammingError(detail);
         throw new IllegalStateException(detail);
      }

      // Check arguments
      MandatoryArgumentChecker.check("ip", ip, "functionName", functionName);

      // First check if the IP filter matches
      if (_ipFilter.matcher(ip).find()) {

         // Then check if the function name matches
         if (_functionNameRegex.matcher(functionName).find() &&
               (conventionName == null || _conventionNameRegex.matcher(conventionName).find())) {
            return _allow ? Boolean.TRUE : Boolean.FALSE;
         }
      }

      return null;
   }

   /**
    * Disposes this access rule. All claimed resources are freed as much as
    * possible.
    *
    * <p>Once disposed, neither {@link #match} nor {@link #isAllowed} should
    * be called.
    *
    * @throws IllegalStateException
    *    if this access rule is already disposed (<em>since XINS 1.3.0</em>).
    */
   public void dispose() {

      // Check state
      if (_disposed) {
         String detail = "This AccessRule is already disposed.";
         Utils.logProgrammingError(detail);
         throw new IllegalStateException(detail);
      }

      // Mark this object as disposed
      _disposed = true;
   }

   /**
    * Returns a character string representation of this object. The returned
    * string is in the form:
    *
    * <blockquote><em>type a.b.c.d/m pattern</em></blockquote>
    *
    * where <em>type</em> is either <code>"allow"</code> or
    * <code>"deny"</code>, <em>a.b.c.d</em> is the base IP address, <em>m</em>
    * is the mask, and <em>pattern</em> is the function name simple pattern.
    *
    * @return
    *    a character string representation of this access rule, never
    *    <code>null</code>.
    */
   public String toString() {
      return _asString;
   }
}
