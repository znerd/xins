/*
 * $Id: PropertyReaderUtils.java,v 1.59 2007/09/13 11:31:30 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Use;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.text.URLEncoding;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Utility functions for dealing with <code>PropertyReader</code> objects.
 *
 * @version $Revision: 1.59 $ $Date: 2007/09/13 11:31:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see PropertyReader
 */
public final class PropertyReaderUtils {

   /**
    * An empty and unmodifiable <code>PropertyReader</code> instance. This
    * field is not <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   public static final PropertyReader EMPTY_PROPERTY_READER = new ProtectedPropertyReader(new Object());

   /**
    * Secret key object used when dealing with
    * <code>ProtectedPropertyReader</code> instances.
    */
   private static final Object SECRET_KEY = new Object();

   /**
    * Constructs a new <code>PropertyReaderUtils</code> object. This
    * constructor is marked as <code>private</code>, since no objects of this
    * class should be constructed.
    */
   private PropertyReaderUtils() {
      // empty
   }

   /**
    * Gets the property with the specified name and converts it to a
    * <code>boolean</code>.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @param fallbackDefault
    *    the fallback default value, returned if the value of the property is
    *    either <code>null</code> or <code>""</code> (an empty string).
    *
    * @return
    *    the value of the property.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || propertyName == null</code>.
    *
    * @throws InvalidPropertyValueException
    *    if the value of the property is neither <code>null</code> nor
    *    <code>""</code> (an empty string), nor <code>"true"</code> nor
    *    <code>"false"</code>.
    */
   public static boolean getBooleanProperty(PropertyReader properties,
                                            String         propertyName,
                                            boolean        fallbackDefault)
   throws IllegalArgumentException,
          InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",   properties,
                                     "propertyName", propertyName);

      // Fallback to the default, if necessary
      String value = properties.get(propertyName);
      if (TextUtils.isEmpty(value)) {
         return fallbackDefault;
      }

      // Parse the string
      if ("true".equals(value)) {
         return true;
      } else if ("false".equals(value)) {
         return false;
      } else {
         throw new InvalidPropertyValueException(propertyName, value);
      }
   }

   /**
    * Gets the property with the specified name and converts it to an
    * <code>int</code>.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, as an <code>int</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || propertyName == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if the specified property is not set, or if it is set to an empty
    *    string.
    *
    * @throws InvalidPropertyValueException
    *    if the conversion to an <code>int</code> failed.
    */
   public static int getIntProperty(PropertyReader properties,
                                    String         propertyName)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",   properties,
                                     "propertyName", propertyName);

      // Make sure the value is set
      String value = properties.get(propertyName);
      if (value == null || value.length() == 0) {
         throw new MissingRequiredPropertyException(propertyName);
      }

      // Parse the string
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException exception) {
         throw new InvalidPropertyValueException(propertyName, value);
      }
   }

   /**
    * Gets the required property with the specified name and converts it to an
    * <code>int</code>, with boundaries.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @param minValue
    *    the minimum value for the property, a smaller value will cause an 
    *    {@link InvalidPropertyValueException}.
    *
    * @param maxValue
    *    the maximum value for the property, a larger value will cause an 
    *    {@link InvalidPropertyValueException}.
    *
    * @return
    *    the value of the property, as an <code>int</code>, with the guarantee 
    *    that <code>minValue &lt;= <em>n</em> &lt;= maxValue</code>, where 
    *    <code><em>n</em></code> is the return value.
    *
    * @throws IllegalArgumentException
    *    if <code>properties   == null
    *          || propertyName == null
    *          || minValue   &gt; maxValue</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if the specified property is not set, or if it is set to an empty
    *    string.
    *
    * @throws InvalidPropertyValueException
    *    if the conversion to an <code>int</code> failed or if the value is 
    *    too small or too large.
    *
    * @since XINS 3.0
    */
   public static int getIntProperty(PropertyReader properties,
                                    String         propertyName,
                                    int            minValue,
                                    int            maxValue)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",   properties,
                                     "propertyName", propertyName);
      if (minValue > maxValue) {
         throw new IllegalArgumentException("minValue (" + minValue + ") > maxValue (" + maxValue + ')');
      }

      // Make sure the value is set
      String propertyValue = properties.get(propertyName);
      if (TextUtils.isEmpty(propertyValue, true)) {
         throw new MissingRequiredPropertyException(propertyName);
      }

      // Parse the string
      int n;
      try {
         n = Integer.parseInt(propertyValue);
      } catch (NumberFormatException exception) {
         throw new InvalidPropertyValueException(propertyName, propertyValue);
      }

      // Check the minimum and maximum
      if (n < minValue) {
         throw new InvalidPropertyValueException(propertyName, propertyValue, "property value (" + n + ") < minValue (" + minValue + ')');
      } else if (n > maxValue) {
         throw new InvalidPropertyValueException(propertyName, propertyValue, "property value (" + n + ") > maxValue (" + maxValue + ')');
      } else {
         return n;
      }
   }

   /**
    * Gets the optional property with the specified name and converts it to an
    * <code>int</code>, with boundaries. If the property is unset, then a 
    * specified fallback value is returned instead.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @param minValue
    *    the minimum value for the property, a smaller value will cause an 
    *    {@link InvalidPropertyValueException}.
    *
    * @param maxValue
    *    the maximum value for the property, a larger value will cause an 
    *    {@link InvalidPropertyValueException}.
    *
    * @param fallback
    *    the fallback default value.
    *
    * @return
    *    the value of the property, as an <code>int</code>, with the guarantee 
    *    that
    *
    *       <code><em>n</em> == fallback || minValue &lt;= <em>n</em> &lt;= maxValue</code>,
    *
    *    where <code><em>n</em></code> is the return value.
    *
    * @throws IllegalArgumentException
    *    if <code>properties   == null
    *          || propertyName == null
    *          || minValue   &gt; maxValue</code>.
    *
    * @throws InvalidPropertyValueException
    *    if the conversion to a <code>double</code> failed or if the value is 
    *    too small or too large.
    *
    * @since XINS 3.0
    */
   public static int getIntProperty(PropertyReader properties,
                                    String         propertyName,
                                    int            minValue,
                                    int            maxValue,
                                    int            fallback)
   throws IllegalArgumentException,
          InvalidPropertyValueException {

      try {
         return getIntProperty(properties, propertyName, minValue, maxValue);
      } catch (MissingRequiredPropertyException ignored) {
         return fallback;
      }
   }

   /**
    * Gets the required property with the specified name and converts it to a
    * <code>double</code>, with boundaries.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @param minValue
    *    the minimum value for the property, a smaller value will cause an 
    *    {@link InvalidPropertyValueException};
    *    if set to {@link Double#NaN} then no minimum will be applied.
    *
    * @param maxValue
    *    the maximum value for the property, a larger value will cause an 
    *    {@link InvalidPropertyValueException};
    *    if set to {@link Double#NaN} then no maximum will be applied.
    *
    * @return
    *    the value of the property, as a <code>double</code>, with the guarantee 
    *    that <code>minValue &lt;= <em>n</em> &lt;= maxValue</code>, where 
    *    <code><em>n</em></code> is the return value.
    *
    * @throws IllegalArgumentException
    *    if <code>properties   == null
    *          || propertyName == null
    *          || minValue   &gt; maxValue</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if the property is unset.
    *
    * @throws InvalidPropertyValueException
    *    if the conversion to a <code>double</code> failed or if the value is 
    *    too small or too large.
    *
    * @since XINS 3.0
    */
   public static double getDoubleProperty(PropertyReader properties,
                                          String         propertyName,
                                          double         minValue,
                                          double         maxValue)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",   properties,
                                     "propertyName", propertyName);
      if (minValue == Double.NaN || maxValue == Double.NaN) {
         // empty
      } else if (minValue > maxValue) {
         throw new IllegalArgumentException("minValue (" + minValue + ") > maxValue (" + maxValue + ')');
      }

      // Make sure the value is set
      String value = properties.get(propertyName);
      if (TextUtils.isEmpty(value)) {
         throw new MissingRequiredPropertyException(propertyName);
      }

      // Parse the string
      double n;
      try {
         n = Double.parseDouble(value);
      } catch (NumberFormatException exception) {
         throw new InvalidPropertyValueException(propertyName, value);
      }

      // Check the minimum and maximum
      if (minValue != Double.NaN && n < minValue) {
         throw new InvalidPropertyValueException(propertyName, value, "property value (" + n + ") < minumum (" + minValue + ')');
      } else if (maxValue != Double.NaN && n > maxValue) {
         throw new InvalidPropertyValueException(propertyName, value, "property value (" + n + ") > maximum (" + minValue + ')');
      } else {
         return n;
      }
   }

   /**
    * Gets the optional property with the specified name and converts it to a
    * <code>double</code>, with boundaries. If the property is unset, then a 
    * specified fallback value is returned instead.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @param minValue
    *    the minimum value for the property, a smaller value will cause an 
    *    {@link InvalidPropertyValueException};
    *    if set to {@link Double#NaN} then no minimum will be applied.
    *
    * @param maxValue
    *    the maximum value for the property, a larger value will cause an 
    *    {@link InvalidPropertyValueException};
    *    if set to {@link Double#NaN} then no maximum will be applied.
    *
    * @param fallback
    *    the fallback default value.
    *
    * @return
    *    the value of the property, as a <code>double</code>, with the guarantee 
    *    that
    *
    *       <code><em>n</em> == fallback || minValue &lt;= <em>n</em> &lt;= maxValue</code>,
    *
    *    where <code><em>n</em></code> is the return value.
    *
    * @throws IllegalArgumentException
    *    if <code>properties   == null
    *          || propertyName == null
    *          || minValue   &gt; maxValue</code>.
    *
    * @throws InvalidPropertyValueException
    *    if the conversion to a <code>double</code> failed or if the value is 
    *    too small or too large.
    *
    * @since XINS 3.0
    */
   public static double getDoubleProperty(PropertyReader properties,
                                          String         propertyName,
                                          double         minValue,
                                          double         maxValue,
                                          double         fallback)
   throws IllegalArgumentException,
          InvalidPropertyValueException {

      try {
         return getDoubleProperty(properties, propertyName, minValue, maxValue);
      } catch (MissingRequiredPropertyException ignored) {
         return fallback;
      }
   }

   /**
    * Gets the property with the specified name and converts it to an enum 
    * constant.
    *
    * @param properties
    *    the set of properties to read from, cannot be <code>null</code>.
    *
    * @param propertyName
    *    the name of the property to read, cannot be <code>null</code>.
    *
    * @param use
    *    value that indicates whether the property is considered
    *    {@link Use#OPTIONAL} or {@link Use#REQUIRED},
    *    cannot be <code>null</code>.
    *
    * @param enumClass
    *    the class of the enumeration type, cannot be <code>null</code>.
    *
    * @return
    *    the property value, as an {@link Enum} of the specified class,
    *    possibly <code>null</code>
    *    (but only if <code>use == {@link Use#OPTIONAL}</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties   == null
    *          || propertyName == null
    *          || use          == null
    *          || enumClass    == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if <code>use == {@link Use#REQUIRED}</code>, but the property is unset.
    *
    * @throws InvalidPropertyValueException
    *    if the conversion to an {@link Enum} instance failed.
    *
    * @since XINS 3.0
    */
   public static <T extends Enum<T>> T getEnumProperty(PropertyReader properties,
                                                       String         propertyName,
                                                       Use            use,
                                                       Class<T>       enumClass)
   throws IllegalArgumentException,
          MissingRequiredPropertyException,
          InvalidPropertyValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",   properties,
                                     "propertyName", propertyName,
                                     "use",          use,
                                     "enumClass",    enumClass);

      // Make sure the value is set
      String propertyValue = properties.get(propertyName);
      if (TextUtils.isEmpty(propertyValue, true)) {
         if (use == Use.REQUIRED) {
            throw new MissingRequiredPropertyException(propertyName);
         } else {
            return null;
         }
      }

      // Convert to an enum
      try {
         return Enum.valueOf(enumClass, propertyValue);

      // If not found, then try again using an uppercased version
      } catch (IllegalArgumentException e1) {
         try {
            return Enum.valueOf(enumClass, propertyValue.toUpperCase());
         } catch (IllegalArgumentException e2) {
            throw new InvalidPropertyValueException(propertyName, propertyValue, "Enum class " + enumClass.getName() + " has no constant named \"" + propertyValue + "\".");
         }
      }
   }

   /**
    * Retrieves the specified property and throws a
    * <code>MissingRequiredPropertyException</code> if it is not set.
    *
    * @param properties
    *    the set of properties to retrieve a specific property from, cannot be
    *    <code>null</code>.
    *
    * @param name
    *    the name of the property, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, guaranteed not to be <code>null</code> and
    *    guaranteed to contain at least one character.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || name == null</code>.
    *
    * @throws MissingRequiredPropertyException
    *    if the value of the property is either <code>null</code> or an empty
    *    string.
    */
   public static String getRequiredProperty(PropertyReader properties,
                                            String         name)
   throws IllegalArgumentException,
          MissingRequiredPropertyException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties,
                                     "name",       name);

      // Retrieve the value
      String value = properties.get(name);

      // The property is required
      if (value == null || value.length() < 1) {
         throw new MissingRequiredPropertyException(name);
      }

      return value;
   }
 
   /**
    * Retrieves a property with the specified name, falling back to a default 
    * value if the property is not set.
    *
    * @param properties
    *    the set of properties to retrieve a property from,
    *    cannot be <code>null</code>.
    *
    * @param key
    *    the property key, 
    *    cannot be <code>null</code>.
    *
    * @param fallbackValue
    *    the fallback default value, returned in case the property is not set 
    *    in <code>properties</code>, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property or the fallback value.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || key == null || fallbackValue == null</code>.
    * 
    * @since XINS 2.1
    */
   public String getWithDefault(PropertyReader properties,
                                String         key,
                                String         fallbackValue)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",    properties,
                                     "key",           key,
                                     "fallbackValue", fallbackValue);

      // Get value
      String value = properties.get(key);
      if (value != null) {
         return value;

      // Fallback if necessary
      } else {
         return fallbackValue;
      }
   }

   /**
    * Constructs a <code>PropertyReader</code> containing just the one
    * specified property.
    *
    * @param name
    *    the property name, cannot be <code>null</code>.
    *
    * @param value
    *    the property value, cannot be <code>null</code>.
    *
    * @return
    *    a {@link PropertyReader} instance that contains one property,
    *    matching the specified name and value; never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || value == null</code>.
    *
    * @since XINS 3.0
    */
   public static BasicPropertyReader createPropertyReader(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name, "value", value);

      // Create and fill a new PropertyReader object
      BasicPropertyReader props = new BasicPropertyReader();
      props.set(name, value);
      return props;
   }

   /**
    * Constructs a <code>PropertyReader</code> from the specified input
    * stream.
    *
    * <p>The parsing done is similar to the parsing done by the
    * {@link Properties#load(InputStream)} method. Empty values will be
    * ignored.
    *
    * @param in
    *    the input stream to read from, cannot be <code>null</code>.
    *
    * @return
    *    a {@link PropertyReader} instance that contains all the properties
    *    defined in the specified input stream.
    *
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while reading from the stream.
    *
    * @deprecated
    *    Since XINS 2.2.
    *    Renamed to {@link #parsePropertyReader(InputStream)}.
    */
   public static PropertyReader createPropertyReader(InputStream in)
   throws IllegalArgumentException, IOException {
      return parsePropertyReader(in);
   }

   /**
    * Parses the specified byte input stream to produce a modifiable
    * <code>PropertyReader</code>. The input stream can be either XML or a
    * regular text format (one property per line, property name and value
    * separated by an equals sign <code>'='</code>).
    *
    * <p>The input is first parsed as XML. If that does not succeed, then it
    * is parsed in a way similar to {@link Properties#load(InputStream)}.
    *
    * <p>Since XINS 3.0, this method also supports XML input, automatically
    * choosing the appropriate format.
    *
    * @param in
    *    the input stream to read from, cannot be <code>null</code>.
    *
    * @return
    *    a {@link PropertyReader} instance that contains all the properties
    *    defined in the specified input stream.
    *
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while reading from the stream.
    *
    * @since XINS 2.2
    */
   public static BasicPropertyReader parsePropertyReader(InputStream in)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      // Get the whole stream
      InputStream byteStream = new ByteArrayInputStream(IOUtils.toByteArray(in));
      byteStream.mark(-1);

      // First try to parse as XML
      try {
         return parsePropertyReader(new ElementParser().parse(byteStream));

      // not XML, reset the stream to the beginning
      } catch (ParseException e) {
         byteStream.reset();
      }

      // Parse the input stream using java.util.Properties
      Properties properties = new Properties();
      properties.load(byteStream);

      // Convert from java.util.Properties to PropertyReader
      BasicPropertyReader r = new BasicPropertyReader();
      Enumeration names = properties.propertyNames();
      while (names.hasMoreElements()) {
         String key   = (String) names.nextElement();
         String value = properties.getProperty(key);

         if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            r.set(key, value);
         }
      }

      return r;
   }

   /**
    * Parses the specified XML file to produce a modifiable
    * <code>PropertyReader</code>.
    * Within the root XML element, all elements of type <code>Property</code>
    * (case-sensitive) that have the attribute <code>name</code> and
    * <code>value</code> set will be parsed. The name of the root element is
    * irrelevant.
    *
    * <p>Example of an input file:
    *
    * <blockquote><code>&lt;Properties&gt;
    * <br>&nbsp;&nbsp;&lt;Property name="euro" value="1.332" /&gt;
    * <br>&nbsp;&nbsp;&lt;Property name="yen" value="0.8723" /&gt;
    * <br>&lt;/Properties&gt;</code></blockquote>
    *
    * @param file
    *    the file to read from, cannot be <code>null</code>.
    *
    * @return
    *    a {@link BasicPropertyReader} instance that contains all the
    *    properties defined in the specified XML file.
    *
    * @throws IllegalArgumentException
    *    if <code>file == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while finding, opening or reading the file.
    *
    * @since XINS 2.2
    */
   public static BasicPropertyReader parsePropertyReaderXML(File file)
   throws IllegalArgumentException, IOException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("file", file);
      if (! file.exists()) {
         throw new IOException("File \"" + file.getPath() + "\" does not exist.");
      } else if (! file.canRead()) {
         throw new IOException("File \"" + file.getPath() + "\" is not readable.");
      } else if (! file.isFile()) {
         throw new IOException("File \"" + file.getPath() + "\" is not a regular file.");
      }

      return parsePropertyReaderXML(new FileReader(file));
   }

   /**
    * Parses the specified input stream as XML to produce a modifiable
    * <code>PropertyReader</code>.
    * Within the root XML element, all elements of type <code>Property</code>
    * (case-sensitive) that have the attribute <code>name</code> and
    * <code>value</code> set will be parsed. The name of the root element is
    * irrelevant.
    *
    * <p>Example of valid input:
    *
    * <blockquote><code>&lt;Properties&gt;
    * <br>&nbsp;&nbsp;&lt;Property name="euro" value="1.332" /&gt;
    * <br>&nbsp;&nbsp;&lt;Property name="yen" value="0.8723" /&gt;
    * <br>&lt;/Properties&gt;</code></blockquote>
    *
    * @param in
    *    the character stream to read from, cannot be <code>null</code>.
    *
    * @return
    *    a {@link BasicPropertyReader} instance that contains all the
    *    properties defined in the specified input stream.
    *
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there was an I/O error while reading from the stream.
    *
    * @throws ParseException
    *    if there was an error while parsing the input stream as XML.
    *
    * @since XINS 2.2
    */
   public static BasicPropertyReader parsePropertyReaderXML(Reader in)
   throws IllegalArgumentException, IOException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      // Parse XML
      Element xml = new ElementParser().parse(in);

      // Convert the XML to a BasicPropertyReader instance
      return parsePropertyReader(xml);
   }

   /**
    * Parses the specified XML element to produce a modifiable
    * <code>PropertyReader</code>.
    * Within the element, all elements of type <code>Property</code>
    * (case-sensitive) that have the attribute <code>name</code> and
    * <code>value</code> set will be parsed. The name of the containing
    * element (i.e.
    * <code>element.{@linkplain Element#getLocalName() getLocalName}()</code>)
    * is irrelevant.
    *
    * <p>Example of a valid XML fragment:
    *
    * <blockquote><code>&lt;Properties&gt;
    * <br>&nbsp;&nbsp;&lt;Property name="euro" value="1.332" /&gt;
    * <br>&nbsp;&nbsp;&lt;Property name="yen" value="0.8723" /&gt;
    * <br>&lt;/Properties&gt;</code></blockquote>
    *
    * @param element
    *    the XML fragment to parse, cannot be <code>null</code>.
    *
    * @return
    *    a {@link BasicPropertyReader} instance that contains all the
    *    properties defined in the specified input stream,
    *    never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>element == null</code>.
    *
    * @since XINS 2.2
    */
   public static BasicPropertyReader parsePropertyReader(Element element)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("element", element);

      // Some name constants
      final String    elementName = "Property";
      final String  nameAttribute = "name";
      final String valueAttribute = "value";

      // Parse all matching child elements
      BasicPropertyReader properties = new BasicPropertyReader();
      List children = element.getChildElements(elementName);
      int childCount = (children == null) ? 0 : children.size();
      for (int i = 0; i < childCount; i++) {
         Element child = (Element) children.get(i);
         String   name = child.getAttribute(nameAttribute );
         String  value = child.getAttribute(valueAttribute);

         // Set the property, unless the name or value is null
         if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
            properties.set(name, value);
         }
      }

      return properties;
   }

   /**
    * Returns the String representation of the specified <code>PropertyReader</code>.
    * For each entry, both the key and the value are encoded using the URL
    * encoding (see {@link URLEncoding}).
    * The key and value are separated by a literal equals sign
    * (<code>'='</code>). The entries are separated using an ampersand
    * (<code>'&amp;'</code>).
    *
    * <p>If the value for an entry is either <code>null</code> or an empty
    * string (<code>""</code>), then nothing is added to the String for that
    * entry.
    *
    * @param properties
    *    the {@link PropertyReader} to serialize, cannot be <code>null</code>.
    *
    * @return
    *    the String representation of the specified <code>PropertyReader</code>.
    *
    * @since XINS 2.0.
    */
   public static String toString(PropertyReader properties) {
      return toString(properties, null, null, null, -1);
   }

   /**
    * Serializes the specified <code>PropertyReader</code> to a
    * <code>String</code>. For each entry, both the key and the
    * value are encoded using the URL encoding (see {@link URLEncoding}).
    * The key and value are separated by a literal equals sign
    * (<code>'='</code>). The entries are separated using
    * an ampersand (<code>'&amp;'</code>).
    *
    * <p>If the value for an entry is either <code>null</code> or an empty
    * string (<code>""</code>), then nothing is added to the String for that
    * entry.
    *
    * @param properties
    *    the {@link PropertyReader} to serialize, can be <code>null</code>.
    *
    * @param valueIfEmpty
    *    the string to append to the buffer in case
    *    <code>properties == null || properties.size() == 0</code>.
    *
    * @return
    *    the String representation of the PropertyReader or the valueIfEmpty, never <code>null</code>.
    *    If all parameters are <code>null</code> then an empty String is returned.
    */
   public static String toString(PropertyReader properties,
                                 String         valueIfEmpty) {
      return toString(properties, valueIfEmpty, null, null, -1);
   }


   /**
    * Returns the <code>String</code> representation for the specified
    * <code>PropertyReader</code>.
    *
    * @param properties
    *    the {@link PropertyReader} to construct a String for, or <code>null</code>.
    *
    * @param valueIfEmpty
    *    the value to return if the specified set of properties is either
    *    <code>null</code> or empty, can be <code>null</code>.
    *
    * @param prefixIfNotEmpty
    *    the prefix to add to the value if the <code>PropertyReader</code>
    *    is not empty, can be <code>null</code>.
    *
    * @param suffix
    *    the suffix to add to the value, can be <code>null</code>. The suffix
    *    will be added even if the PropertyReaderis empty.
    *
    * @return
    *    the String representation of the PropertyReader with the different artifacts, never <code>null</code>.
    *    If all parameters are <code>null</code> then an empty String is returned.
    *
    * @since XINS 2.0
    */
   public static String toString(PropertyReader properties,
                                 String         valueIfEmpty,
                                 String         prefixIfNotEmpty,
                                 String         suffix) {
      return toString(properties, valueIfEmpty, prefixIfNotEmpty, suffix, -1);
   }

   /**
    * Returns the <code>String</code> representation for the specified
    * <code>PropertyReader</code>.
    *
    * @param properties
    *    the {@link PropertyReader} to construct a String for, or <code>null</code>.
    *
    * @param valueIfEmpty
    *    the value to return if the specified set of properties is either
    *    <code>null</code> or empty, can be <code>null</code>.
    *
    * @param prefixIfNotEmpty
    *    the prefix to add to the value if the <code>PropertyReader</code>
    *    is not empty, can be <code>null</code>.
    *
    * @param suffix
    *    the suffix to add to the value, can be <code>null</code>. The suffix
    *    will be added even if the PropertyReaderis empty.
    *
    * @param maxValueLength
    *    the maximum of characters to set for the value, if the value is longer
    *    than this limit '...' will be added after the limit.
    *    If the value is -1, no limit will be set.
    *
    * @return
    *    the String representation of the PropertyReader with the different artifacts, never <code>null</code>.
    *    If all parameters are <code>null</code> then an empty String is returned.
    *
    * @since XINS 2.0
    */
   public static String toString(PropertyReader properties,
                                 String         valueIfEmpty,
                                 String         prefixIfNotEmpty,
                                 String         suffix,
                                 int            maxValueLength) {

      // If the property set if null, return the fallback
      if (properties == null) {
         if (suffix != null) {
            return suffix;
         } else {
            return valueIfEmpty;
         }
      }

      // If there are no parameters, then return the fallback
      if (properties.size() == 0) {
         if (suffix != null) {
            return suffix;
         } else {
            return valueIfEmpty;
         }
      }

      StringBuffer buffer = new StringBuffer(299);

      boolean first = true;
      for (String name : properties.names()) {

         // Get the value
         String value = properties.get(name);

         // If the value is null or an empty string, then output nothing
         if (value == null || value.length() == 0) {
            continue;
         }

         // Append an ampersand, except for the first entry
         if (!first) {
            buffer.append('&');
         } else {
            first = false;
            if (prefixIfNotEmpty != null) {
               buffer.append(prefixIfNotEmpty);
            }
         }

         // Append the key and the value, separated by an equals sign
         buffer.append(URLEncoding.encode(name));
         buffer.append('=');
         String encodedValue;
         if (maxValueLength == -1 || value.length() <= maxValueLength) {
            encodedValue = URLEncoding.encode(value);
         } else {
            encodedValue = URLEncoding.encode(value.substring(0, maxValueLength)) + "...";
         }
         buffer.append(encodedValue);
      }

      if (suffix != null) {
         buffer.append('&');
         buffer.append(suffix);
      }

      return buffer.toString();
   }

   /**
    * Compares a <code>PropertyReader</code> instance with another object for
    * equality.
    *
    * @param pr
    *    the <code>PropertyReader</code>, can be <code>null</code>.
    *
    * @param toCompare
    *    the object to compare the <code>PropertyReader</code> with,
    *    can be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the objects are considered to be equal,
    *    <code>false</code> if they are considered different.
    *
    * @since XINS 2.1
    */
   public static final boolean equals(PropertyReader pr, Object toCompare) {

      // Test for identity equality
      if (pr == toCompare) {
         return true;

      // If either one is null, then they are not equal (otherwise they would
      // both be null in which case they are identity equal)
      } else if (pr == null || toCompare == null) {
         return false;

      // The 2nd object must implement the PropertyReader interface
      } else if (! (toCompare instanceof PropertyReader)) {
         return false;
      }

      // Size must be the same
      PropertyReader pr2 = (PropertyReader) toCompare;
      if (pr.size() != pr2.size()) {
         return false;
      }

      // Loop over all key/value pairs
      for (String key : pr.names()) {
         String value1 = pr.get(key);
         String value2 = pr2.get(key);
         if (value1 == null && value2 != null) {
            return false;
         } else if (value1 != null && !value1.equals(value2)) {
            return false;
         }
      }

      // No differences found
      return true;
   }

   /**
    * Computes a hash code value for the specified <code>PropertyReader</code>
    * object.
    *
    * @param pr
    *    the <code>PropertyReader</code> instance to compute a hash code value
    *    for, cannot be <code>null</code>.
    *
    * @return
    *    the hash code value.
    *
    * @throws NullPointerException
    *    if <code>pr == null</code>.
    *
    * @since XINS 2.1
    */
   public static final int hashCode(PropertyReader pr)
   throws NullPointerException {

      int hash = 0;

      // Loop over all key/value pairs
      for (String key : pr.names()) {
         String value = pr.get(key);

         // XOR the hash code value with the key string hash code
         if (key != null) {
            hash ^= key.hashCode();
         }

         // XOR the hash code value with the value string hash code
         if (value != null) {
            hash ^= value.hashCode();
         }
      }

      return hash;
   }

   /**
    * Converts the specified property reader to XML, using the specified root
    * element name. Each property is represented by a
    * <code>&lt;Property&gt;</code> element.
    *
    * <p>If <code>"Properties"</code> is specified as the root element name,
    * then the output can be something like this:
    *
    * <blockquote><code>&lt;Properties&gt;
    * &nbsp;&nbsp;&nbsp;&lt;Property name="first" value="1.23" /&gt;
    * &nbsp;&nbsp;&nbsp;&lt;Property name="second" value="7.89" /&gt;
    * &lt;/Properties&gt;</code></blockquote>
    *
    * @param properties
    *    the {@link PropertyReader} to serialize, cannot be <code>null</code>.
    *
    * @param elementName
    *    the name of the containing element, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || elementName == null</code>.
    *
    * @since XINS 3.0
    */
   public static final Element toXML(PropertyReader properties, String elementName)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("properties",  properties,
                                     "elementName", elementName);

      Element xml = new Element(elementName);

      for (String name : properties.names()) {

         Element child = new Element("Property");
         child.setAttribute("name",  name                );
         child.setAttribute("value", properties.get(name));

         xml.addChild(child);
      }
      
      return xml;
   }

   /**
    * Converts the specified property reader to XML, using
    * <code>"Properties"</code> as the root element name.
    * Each property is represented by a
    * <code>&lt;Property&gt;</code> element.
    *
    * <p>The output can be something like this:
    *
    * <blockquote><code>&lt;Properties&gt;
    * &nbsp;&nbsp;&nbsp;&lt;Property name="first" value="1.23" /&gt;
    * &nbsp;&nbsp;&nbsp;&lt;Property name="second" value="7.89" /&gt;
    * &lt;/Properties&gt;</code></blockquote>
    *
    * @param properties
    *    the {@link PropertyReader} to serialize, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>.
    *
    * @since XINS 3.0
    */
   public static final Element toXML(PropertyReader properties)
   throws IllegalArgumentException {
      return toXML(properties, "Properties");
   }

   /**
    * Makes an unmodifiable copy of the specified <code>PropertyReader</code>.
    *
    * @param source
    *    the source {@link PropertyReader}, cannot be <code>null</code>.
    *
    * @return
    *    an unmodifiable copy of the <code>source</code> that has no
    *    relation with <code>source</code> anymore, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>source == null</code>.
    *
    * @since XINS 3.0
    */
   public static final PropertyReader copyUnmodifiable(PropertyReader source)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("source", source);

      // Create a ProtectedPropertyReader
      Object             secretKey = new Object();
      ProtectedPropertyReader copy = new ProtectedPropertyReader(secretKey);

      // Copy all properties from source to copy
      copy.copyFrom(secretKey, source);

      return copy;
   }
}
