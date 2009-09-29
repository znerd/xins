/*
 * $Id: AccessRuleList.java,v 1.51 2007/09/11 13:24:21 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.StringTokenizer;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.ParseException;

/**
 * Access rule list.
 *
 * <h3>Descriptor format</h3>
 *
 * <p>An access rule list <em>descriptor</em>, a character string, can be
 * converted to produce an {@link AccessRuleList} object. A valid descriptor
 * consists of a list of access rule descriptors (see class
 * {@link AccessRule}) and/or access rule file descriptors (see class
 * {@link AccessRuleFile}), separated by semi-colon characters (<code>';'</code>).
 * Optionally, the rules can have any amount of whitespace (space-, tab-,
 * newline- and carriage return-characters), before and after them. The last
 * descriptor cannot end with a semi-colon.
 *
 * <h3>Descriptor examples</h3>
 *
 * <p>An example of an access rule list descriptor is:
 *
 * <blockquote><code>allow 194.134.168.213/32 *;
 * <br>deny  194.134.168.213/24 _*;
 * <br>allow 194.134.168.213/24 *;
 * <br>file  /var/conf/file1.acl;
 * <br>deny  0.0.0.0/0 *</code></blockquote>
 *
 * <p>The above access control list grants the IP address 194.134.168.213
 * access to all functions. Then in the second rule it denies
 * access to all IP addresses in the range 194.134.168.0 to 194.134.168.255 to
 * all functions that start with an underscore (<code>'_'</code>). Then it
 * allows access for those IP addresses to all other functions, then it
 * applies the rules in the <code>/var/conf/file1.acl</code> file and finally
 * all other IP addresses are denied access to any of the functions.
 *
 * @version $Revision: 1.51 $ $Date: 2007/09/11 13:24:21 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class AccessRuleList implements AccessRuleContainer {

   /**
    * An empty access rule list. This field is never <code>null</code>.
    */
   static final AccessRuleList EMPTY = new AccessRuleList(new AccessRuleContainer[0]);

   /**
    * The list of rules. Cannot be <code>null</code>.
    */
   private AccessRuleContainer[] _rules;

   /**
    * The string representation of this instance. Cannot be <code>null</code>.
    */
   private String _asString;

   /**
    * Flag that indicates whether this object is disposed.
    */
   private boolean _disposed;

   /**
    * Creates a new <code>AccessRuleList</code> object. The passed
    * {@link AccessRuleContainer} array is assumed to be owned by the
    * constructor.
    *
    * @param rules
    *    the list of rules, not <code>null</code> and should not contain any
    *    duplicate or <code>null</code> elements; if one of these latter 2
    *    constraints are violated, the behaviour is undefined.
    *
    * @throws NullPointerException
    *    if <code>rules == null</code>.
    */
   private AccessRuleList(AccessRuleContainer[] rules)
   throws NullPointerException {

      // Count number of rules (may throw NPE)
      int ruleCount = rules.length;

      // Build string representation and log
      StringBuffer buffer = new StringBuffer(ruleCount * 40);
      if (ruleCount > 0) {
         String s = rules[0].toString();
         buffer.append(s);
         Log.log_3429(0, s);

         for (int i = 1; i < ruleCount; i++) {
            s = rules[i].toString();

            buffer.append(';');
            buffer.append(s);

            Log.log_3429(i, s);
         }
      }
      _asString = buffer.toString();

      // Store the rules
      _rules = rules;
   }

   /**
    * Parses the specified character string to construct a new
    * <code>AccessRuleList</code> object, with the specified watch interval
    * for referenced files.
    *
    * <p>If the specified interval is <code>0</code>, then no watching will be
    * performed.
    *
    * @param descriptor
    *    the access rule list descriptor, the character string to parse,
    *    cannot be <code>null</code>.
    *
    * @param interval
    *    the interval used to check the ACL files for modification, in
    *    seconds, must be &gt;= 0.
    *
    * @return
    *    an {@link AccessRuleList} instance, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null || interval &lt; 0</code>.
    *
    * @throws ParseException
    *    if there was a parsing error.
    *
    * @since XINS 1.1.0
    */
   public static final AccessRuleList parseAccessRuleList(String descriptor,
                                                          int    interval)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);
      if (interval < 0) {
         throw new IllegalArgumentException("interval ("
                                          + interval
                                          + ") < 0");
      }

      // First trim whitespace from the descriptor
      descriptor = descriptor.trim();

      // Tokenize the descriptor, separator is semi-colon
      StringTokenizer tokenizer = new StringTokenizer(descriptor, ";");
      int             ruleCount = tokenizer.countTokens();

      // Parse all tokens
      AccessRuleContainer[] rules = new AccessRuleContainer[ruleCount];
      for (int i = 0; i < ruleCount; i++) {

         // Remove leading and trailing whitespace from the next token
         String token = tokenizer.nextToken().trim();

         // Parse and add the rule
         if (token.startsWith("allow") || token.startsWith("deny")) {
            rules[i] = AccessRule.parseAccessRule(token);
         } else if (token.startsWith("file")) {
            rules[i] = new AccessRuleFile(token, interval);
         } else {
            String detail = "Failed to parse access rule list. "
                          + "Expected token \""
                          + token
                          + "\" to start with "
                          + "\"allow\", \"deny\" or \"file\".";
            throw new ParseException(detail);
         }
      }

      return new AccessRuleList(rules);
   }

   /**
    * Counts the number of rules in this list.
    *
    * @return
    *    the number of rules, always &gt;= 0.
    */
   public int getRuleCount() {
      return _rules.length;
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
    *    the name of the calling convention to match, can be <code>null</code>.
    *
    * @return
    *    {@link Boolean#TRUE} if the specified IP address is allowed to access
    *    the specified function, {@link Boolean#FALSE} if it is disallowed
    *    access or <code>null</code> if no match is found.
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
    * @since XINS 2.1.
    */
   public Boolean isAllowed(String ip, String functionName, String conventionName)
   throws IllegalStateException, IllegalArgumentException, ParseException {

      // Check state
      if (_disposed) {
         String detail = "This AccessRuleList is disposed.";
         Utils.logProgrammingError(detail);
         throw new IllegalStateException(detail);
      }

      // Check preconditions
      MandatoryArgumentChecker.check("ip", ip, "functionName", functionName);

      int ruleCount = _rules.length;
      for (int i = 0; i < ruleCount; i++) {
         AccessRuleContainer rule = _rules[i];

         String ruleString = rule.toString();

         Boolean allowed = rule.isAllowed(ip, functionName, conventionName);
         if (allowed != null) {

            // Choose between 'allow' and 'deny'
            boolean allow = allowed.booleanValue();

            // Log this match
            // XXX: Should this logging really be done in this class?
            if (allow) {
               Log.log_3550(ip, functionName, conventionName, i, ruleString);
            } else {
               Log.log_3551(ip, functionName, conventionName, i, ruleString);
            }

            return allowed;
         }
      }
      return null;
   }

   /**
    * Disposes this access rule. All claimed resources are freed as much as
    * possible.
    *
    * <p>Once disposed, the {@link #isAllowed} method should no longer be
    * called.
    */
   public void dispose() {

      // Check state
      if (_disposed) {
         String detail = "This AccessRule is already disposed.";
         Utils.logProgrammingError(detail);
         throw new IllegalStateException(detail);
      }

      // Do not dispose the EMPTY list
      if (this == EMPTY) {
         return;
      }

      // Mark this object as disposed
      _disposed = true;

      // Dispose the current rules
      int count = _rules == null ? 0 : _rules.length;
      for (int i = 0; i < count; i++) {
         AccessRuleContainer rule = _rules[i];
         if (rule != null) {
            try {
               rule.dispose();
            } catch (Throwable exception) {
               Utils.logIgnoredException(exception);
            }
         }
      }
      _rules = null;
   }

   /**
    * Returns a character string representation of this object. The returned
    * string is in the form:
    *
    * <blockquote><em>type a.b.c.d/m pattern;type a.b.c.d/m pattern</em></blockquote>
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
