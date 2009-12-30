/*
 * $Id: ContextIDGenerator.java,v 1.28 2007/04/25 14:13:18 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.Random;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.manageable.Manageable;
import org.xins.common.manageable.InitializationException;
import org.xins.common.net.IPAddressUtils;
import org.xins.common.text.DateConverter;
import org.xins.common.text.TextUtils;

/**
 * Generator for diagnostic context identifiers. Generated context
 * identifiers will be in the format:
 *
 * <blockquote><em>app</em>@<em>host</em>:<em>time</em>:<em>rnd</em></blockquote>
 *
 * where:
 *
 * <ul>
 *    <li><em>app</em> is the name of the deployed application, e.g.
 *       <code>"sso"</code>;
 *
 *    <li><em>host</em> is the hostname of the computer running this
 *    engine, e.g. <code>"freddy.bravo.com"</code>;
 *
 *    <li><em>time</em> is the current date and time in the format
 *    <code>yyMMdd-HHmmssNNN</code>, e.g. <code>"050806-171522358"</code>;
 *
 *    <li><em>rnd</em> is a 5 hex-digits randomly generated number, e.g.
 *        <code>"2f4e6"</code>.
 * </ul>
 *
 * @version $Revision: 1.28 $ $Date: 2007/04/25 14:13:18 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
final class ContextIDGenerator extends Manageable {

   /**
    * The hexadecimal digits.
    */
   private static final char[] HEX_DIGITS = new char[] {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
   };

   /**
    * The name of the runtime property that hostname for the server
    * running the API.
    */
   private static final String HOSTNAME_PROPERTY = "org.xins.server.hostname";

   /**
    * The name of the API. Never <code>null</code>.
    */
   private final String _apiName;

   /**
    * The name for the local host. Never <code>null</code>.
    */
   private String _hostname;

   /**
    * The fixed prefix for generated context identifiers, as a character
    * buffer. Never <code>null</code> when this instance is initialized.
    */
   private char[] _prefixBuffer;

   /**
    * The length of the prefix.
    */
   private int _prefixLength;

   /**
    * A date converter. Never <code>null</code>. Needs to be locked before
    * usage.
    */
   private final DateConverter _dateConverter;

   /**
    * A pseudo-random number generator. Never <code>null</code>
    */
   private final Random _random;

   /**
    * Constructs a new <code>ContextIDGenerator</code>.
    *
    * @param apiName
    *    the name of the API, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>apiName == null</code>.
    */
   ContextIDGenerator(String apiName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("apiName", apiName);

      // Store API name and determine host name
      _apiName  = apiName;
      _hostname = IPAddressUtils.getLocalHost();

      // Create a DateConverter that will not prepend the century
      _dateConverter = new DateConverter(false);

      // Initialize a pseudo-random number generator
      _random = new Random();
   }

   /**
    * Performs the initialization procedure (actual implementation). When this
    * method is called from {@link #init(PropertyReader)}, the state and the
    * argument will have been checked and the state will have been set to
    * {@link #INITIALIZING}.
    *
    * @param properties
    *    the initialization properties, not <code>null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if a required property is not given.
    *
    * @throws InvalidPropertyValueException
    *    if the value of a certain property is invalid.
    *
    * @throws InitializationException
    *    if the initialization failed, for any other reason.
    */
   protected void initImpl(PropertyReader properties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {

      // Determine if the hostname has changed
      String hostname = properties.get(HOSTNAME_PROPERTY);
      if (!TextUtils.isEmpty(hostname) && !hostname.equals(_hostname)) {
         Log.log_3310(_hostname, hostname);
         _hostname = hostname;
      }

      // Determine prefix and total context ID length
      String prefix = _apiName + '@' + _hostname + ':';
      _prefixBuffer = prefix.toCharArray();
      _prefixLength = prefix.length();
   }

   /**
    * Generates a diagnostic context identifier.
    *
    * @return
    *    the generated diagnostic context identifier, never <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this object is currently not usable, i.e. in the
    *    {@link #USABLE} state.
    */
   String generate() throws IllegalStateException {

      // Check preconditions
      assertUsable();

      // Construct a new string buffer with the exact needed capacity
      int    prefixLength = _prefixLength;
      int    length       = prefixLength + 22;
      char[] buffer       = new char[length];

      // Copy the template into the buffer
      System.arraycopy(_prefixBuffer, 0, buffer, 0, prefixLength);

      // Determine the current time and append the timestamp
      long date = System.currentTimeMillis();
      synchronized (_dateConverter) {
         _dateConverter.format(date, buffer, prefixLength);
      }

      // Append 5 pseudo-random hex digits
      int random = _random.nextInt() & 0x0fffffff;
      int pos = prefixLength + 16;
      buffer[pos++] = ':';
      buffer[pos++] = HEX_DIGITS[ random        & 15];
      buffer[pos++] = HEX_DIGITS[(random >>  4) & 15];
      buffer[pos++] = HEX_DIGITS[(random >>  8) & 15];
      buffer[pos++] = HEX_DIGITS[(random >> 12) & 15];
      buffer[pos  ] = HEX_DIGITS[(random >> 16) & 15];

      // Log and return the context ID
      return new String(buffer);
   }
}
