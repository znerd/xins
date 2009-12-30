/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Standard type <em>_xml</em>. A value of this type represents an XML
 * fragment.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 2.2
 */
public class XML extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final XML SINGLETON = new XML();

   /**
    * Constructs a new <code>XML</code> instance.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private XML() {
      super("_xml", Element.class);
   }

   /**
    * Constructs an <code>Element</code> from the specified
    * non-<code>null</code> string.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link Element} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Element fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (Element) SINGLETON.fromString(string);
   }

   /**
    * Constructs an <code>Element</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Element}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Element fromStringForOptional(String string)
   throws TypeValueException {
      return (string == null) ? null : fromStringForRequired(string);
   }

   /**
    * Converts the specified <code>Element</code> to a string.
    *
    * @param element
    *    the XML fragment to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value;
    *    or <code>null</code> if and only if <code>value == null</code>.
    */
   public static String toString(Element element) {

      // Short-circuit if the argument is null
      if (element == null) {
         return null;
      }

      return element.toString();
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {
      fromStringImpl(value);
   }

   @Override
   protected final Object fromStringImpl(String string) throws TypeValueException {
      try {
         return new ElementParser().parse(string);
      } catch (ParseException cause) {
         throw new TypeValueException(this, string, "XML parsing error.", cause);
      }
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // Convert the object to a String
      return ((Element) value).toString();
   }

   @Override
   public String getDescription() {
      return "XML fragment.";
   }
}
