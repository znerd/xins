/*
 * $Id: Properties.java,v 1.26 2007/08/27 11:18:21 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.text.FormatException;
import org.xins.common.text.TextUtils;
import org.xins.common.text.URLEncoding;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;

/**
 * Standard type <em>_properties</em>.
 *
 * @version $Revision: 1.26 $ $Date: 2007/08/27 11:18:21 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class Properties extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Properties SINGLETON = new Properties();

   /**
    * The type for property names. Cannot be <code>null</code>.
    */
   private final Type _nameType;

   /**
    * The type for property values. Cannot be <code>null</code>.
    */
   private final Type _valueType;

   /**
    * Constructs a new <code>Properties</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Properties() {
      this("_properties", null, null);
   }

   /**
    * Constructs a new <code>Properties</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param nameType
    *    the type for property names, or <code>null</code> if {@link Text}
    *    should be assumed.
    *
    * @param valueType
    *    the type for property values, or <code>null</code> if {@link Text}
    *    should be assumed.
    */
   protected Properties(String name, Type nameType, Type valueType) {
      super(name, PropertyReader.class);

      _nameType  = nameType  == null ? Text.SINGLETON : nameType;
      _valueType = valueType == null ? Text.SINGLETON : valueType;
   }

   /**
    * Constructs a <code>PropertyReader</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link PropertyReader} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static PropertyReader fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (PropertyReader) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>PropertyReader</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link PropertyReader}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static PropertyReader fromStringForOptional(String string)
   throws TypeValueException {
      return (PropertyReader) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified <code>PropertyReader</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(PropertyReader value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      // Use a buffer to create the string
      StringBuffer buffer = new StringBuffer(255);

      // Iterate over all properties
      boolean first = true;
      for (String propName : value.names()) {

         // Prepend an ampersand before all but the first
         if (!first) {
            buffer.append('&');
         } else {
            first = false;
         }

         // Get the value
         String propValue = value.get(propName);

         // Append the name encoded
         buffer.append(URLEncoding.encode(propName));
         buffer.append('=');

         // Append the value encoded, iff it is not null
         if (propValue != null) {
            buffer.append(URLEncoding.encode(propValue));
         }
      }

      return buffer.toString();
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {

      // Store the property keys
      HashSet<String> propertyKeys = new HashSet<String>();

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(value, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         int    index = token.indexOf('=');

         // No equals-sign
         if (index < 1) {
            throw new TypeValueException(this, value, "No equals-sign found.");

         // Multiple equals-signs
         } else if (token.length() > (index + 1) && token.indexOf('=', index + 1) >= 0) {
            throw new TypeValueException(this, value, "Found multiple equals-signs within the same token.");

         }

         // Determine the property key and check it against its type
         String propKey;
         try {
            propKey = URLEncoding.decode(token.substring(0, index));
         } catch (FormatException cause) {
            throw new TypeValueException(this, value, "Failed to decode property key.", cause);
         }
         if (propertyKeys.contains(propKey)) {
            throw new TypeValueException(this, value, "Duplicate property key " + TextUtils.quote(propKey) + " found.");
         }
         try {
            _nameType.checkValue(propKey);
         } catch (TypeValueException cause) {
            throw new TypeValueException(this, value, "Invalid property key " + TextUtils.quote(propKey) + '.', cause);
         }
         propertyKeys.add(propKey);

         // Determine the property value and check it against its type
         String propValue;
         try {
            propValue = token.substring(index + 1);
            propValue = (propValue.length() < 1) ? null : URLEncoding.decode(propValue);
         } catch (FormatException cause) {
            throw new TypeValueException(this, value, "Failed to decode property value.", cause);
         }
         try {
            _valueType.checkValue(propValue);
         } catch (TypeValueException cause) {
            throw new TypeValueException(this, value, "Invalid property value " + TextUtils.quote(propValue) + '.', cause);
         }
      }
   }

   @Override
   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      // Construct a PropertyReader to store the properties in
      BasicPropertyReader pr = new BasicPropertyReader();

      // Store the property keys
      HashSet<String> propertyKeys = new HashSet<String>();

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         int index = token.indexOf('=');
         if (index < 1) {
            throw new TypeValueException(SINGLETON, string, "The token \"" + token + "\" does not contain an equals sign ('=').");
         } else if (token.length() > (index + 1) && token.indexOf('=', index + 1) >= 0) {
            throw new TypeValueException(SINGLETON, string);
         } else {
            try {
               String name  = URLEncoding.decode(token.substring(0, index));
               if (propertyKeys.contains(name)) {
                  throw new TypeValueException(SINGLETON, string, "The key \"" + name + "\" is already set.");
               }
               propertyKeys.add(name);
               String value = token.substring(index + 1);
               if (value.length() < 1) {
                  value = null;
               } else {
                  value = URLEncoding.decode(value);
               }

               _nameType.checkValue(name);
               _valueType.checkValue(value);

               pr.set(name, value);
            } catch (FormatException fe) {
               throw new TypeValueException(SINGLETON, string, fe.getReason());
            } catch (IllegalArgumentException iae) {
               throw Utils.logProgrammingError(iae);
            }
         }
      }

      return pr;
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a PropertyReader
      return toString((PropertyReader) value);
   }

   @Override
   public String getDescription() {
      return "An ampersand separated list of key value pair in the format key=value.";
   }
}
