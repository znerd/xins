/*
 * $Id: IPAddressUtils.java,v 1.38 2007/09/11 12:39:57 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.net;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;

/**
 * IP address-related utility functions.
 *
 * @version $Revision: 1.38 $ $Date: 2007/09/11 12:39:57 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class IPAddressUtils {

   /**
    * Constructs a new <code>IPAddressUtils</code> object.
    */
   private IPAddressUtils() {
      // empty
   }

   /**
    * Converts an IP address in the form <em>a.b.c.d</em> to an
    * <code>int</code>.
    *
    * @param ip
    *    the IP address, must be in the form:
    *    <em>a.a.a.a.</em>, where <em>a</em> is a number between 0 and 255,
    *    with no leading zeroes; cannot be <code>null</code>.
    *
    * @return
    *    the IP address as an <code>int</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null</code>.
    *
    * @throws ParseException
    *    if <code>ip</code> cannot be parsed as an IP address.
    */
   public static int ipToInt(String ip)
   throws IllegalArgumentException, ParseException {
      MandatoryArgumentChecker.check("ip", ip);

      int value;

      // Tokenize the string
      StringTokenizer tokenizer = new StringTokenizer(ip, ".", false);

      try {

         // Token 1 must be an IP address part
         value = ipPartToInt(ip, tokenizer.nextToken());

         // Token 3 must be an IP address part
         value <<= 8;
         value += ipPartToInt(ip, tokenizer.nextToken());

         // Token 5 must be an IP address part
         value <<= 8;
         value += ipPartToInt(ip, tokenizer.nextToken());

         // Token 7 must be an IP address part
         value <<= 8;
         value += ipPartToInt(ip, tokenizer.nextToken());

      } catch (NoSuchElementException nsee) {
         throw newParseException(ip);
      }
      if (tokenizer.hasMoreTokens()) {
         throw newParseException(ip);
      }

      return value;
   }

   /**
    * Converts the specified component of an IP address to a number between 0
    * and 255.
    *
    * @param ip
    *    the complete IP address, needed when throwing a
    *    {@link ParseException}, should not be <code>null</code>; if it is,
    *    then the behaviour is undefined.
    *
    * @param part
    *    the part to convert to an <code>int</code> number, should not be
    *    <code>null</code>; if it is, then the behaviour is undefined.
    *
    * @return
    *    the <code>int</code> value of the part, between 0 and 255
    *    (inclusive).
    *
    * @throws ParseException
    *    if the part cannot be parsed.
    */
   private static int ipPartToInt(String ip, String part)
   throws ParseException {

      char[] partString = part.toCharArray();
      int length = partString.length;

      if (length == 1) {
         char c0 = partString[0];
         if (c0 >= '0' && c0 <= '9') {
            return c0 - '0';
         }

      } else if (length == 2) {
         char c0 = partString[0];
         char c1 = partString[1];

         if (c0 >= '1' && c0 <= '9' && c1 >= '0' && c1 <= '9') {
            return ((c0 - '0') * 10) + (c1 - '0');
         }

      } else if (length == 3) {
         char c0 = partString[0];
         char c1 = partString[1];
         char c2 = partString[2];

         if (c0 >= '1' && c0 <= '2' &&
             c1 >= '0' && c1 <= '9' &&
             c2 >= '0' && c2 <= '9') {

            int value = ((c0 - '0') * 100) + ((c1 - '0') * 10) + (c2 - '0');
            if (value <= 255) {
               return value;
            }
         }
      }

      throw newParseException(ip);
   }

   /**
    * Retrieves the local host IP address.
    *
    * @return
    *    if possible the IP address for localhost, otherwise
    *    the string <code>"127.0.0.1"</code>.
    *
    * @since XINS 1.3.0
    */
   public static String getLocalHostIPAddress() {
      try {
         return InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException exception) {
         return "127.0.0.1";
      }
   }

   /**
    * Retrieves the local host name. This method applies several
    * techniques to attempt to retrieve the local host name.
    *
    * @return
    *    if possible the fully qualified name for the local host, otherwise if
    *    possible the non-qualified host name for the local host, otherwise
    *    the string <code>"localhost"</code>.
    */
   public static String getLocalHost() {

      String hostname = "localhost";

      try {
         hostname = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException unknownHostException) {
         String unknownMessage = unknownHostException.getMessage();
         int twoDotPos = unknownMessage.indexOf(':');
         if (twoDotPos != -1) {
            hostname = unknownMessage.substring(0, twoDotPos);
         }
      } catch (SecurityException securityException) {
         // fall through
      }

      return hostname;
   }

   /**
    * Retrieves the localhost host name, using the specified fallback default.
    *
    * @param fallback
    *    the fallback default, can be <code>null</code>.
    *
    * @return
    *    if possible the fully qualified name for the local host, otherwise if
    *    possible the non-qualified host name for the local host, otherwise
    *    <code>fallback</code>.
    *
    * @since XINS 3.0
    */
   public static String getLocalHost(String fallback) {

      // First try using the standard approach
      String hostname = getLocalHost();

      // No hostname yet? Try running the 'hostname' command on POSIX systems
      if (TextUtils.isEmpty(hostname, true) || "localhost".equals(hostname)) {
         hostname = runHostnameCommand();
      }

      // Still no hostname, fallback to a default and then log a warning
      if (hostname == null) {
         hostname = fallback;
      }

      return hostname;
   }

   /**
    * Tries running the POSIX <code>hostname</code> command to determine the
    * name of the local host. If this does not succeed, then <code>null</code>
    * is returned instead of the hostname.
    *
    * @return
    *    the name of the local host, or <code>null</code>.
    */
   private static String runHostnameCommand() {

      final String command = "hostname";

      String hostname;
      try {
         Process process = Runtime.getRuntime().exec(command);
         process.waitFor();

         int exitValue = process.exitValue();
         if (exitValue != 0) {
            Utils.logInfo("Execution of command \"" + command + "\" failed with exit code " + exitValue + '.');
            return null;
         }

         // Get the stdout output from the process
         InputStream in = process.getInputStream();

         // Configure max expected hostname length
         int maxHostNameLength = 500;

         // Read the whole output
         byte[] bytes = new byte[maxHostNameLength];
         int read = in.read(bytes);
         if (read < 0) {
            return null;
         }

         // Convert the bytes to a String
         final String ENCODING = "US-ASCII";
         hostname = new String(bytes, 0, read, ENCODING);

         // Check all characters in the hostname
         for (int i = 0; i < read; i++) {
            char ch = hostname.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
               // OK: fall through
            } else if (ch > 'A' && ch <= 'Z') {
               // OK: fall through
            } else if (ch > '0' && ch <= '9') {
               // OK: fall through
            } else if (ch == '-' || ch == '_' || ch == '.') {
               // OK: fall through
            } else if (ch == '\n' || ch == '\r') {
               hostname = hostname.substring(0, i);
               i = read;
            } else {
               Utils.logInfo("Found invalid character " + (int) ch + " in output of command \"" + command + "\".");
               return null;
            }
         }

      } catch (Exception exception) {
         String message = "Caught unexpected "
                        + exception.getClass().getName()
                        + " while attempting to execute command \""
                        + command
                        + "\".";
         Utils.logIgnoredException(exception);
         hostname = null;
      }

      if (TextUtils.isEmpty(hostname, true)) {
         hostname = null;
      }

      return hostname;
   }

   /**
    * Constructs a new <code>ParseException</code> for the specified malformed
    * IP address.
    *
    * @param ip
    *    the malformed IP address, not <code>null</code>.
    *
    * @return
    *    the {@link ParseException} to throw.
    */
   private static ParseException newParseException(String ip) {

      // Construct the message for the exception
      String detail = "The string \"" + ip + "\" is not a valid IP address.";

      // Return the exception
      return new ParseException(detail);
   }
}
