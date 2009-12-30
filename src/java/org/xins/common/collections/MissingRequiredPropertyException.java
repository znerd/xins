/*
 * $Id: MissingRequiredPropertyException.java,v 1.20 2007/05/21 08:34:42 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import org.xins.common.text.TextUtils;

/**
 * Exception thrown to indicate a required property has no value set for it.
 *
 * @version $Revision: 1.20 $ $Date: 2007/05/21 08:34:42 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see InvalidPropertyValueException
 */
public final class MissingRequiredPropertyException extends PropertyException {

   /**
    * Detailed description of why this property is required in the current
    * context. Can be <code>null</code>.
    */
   private final String _detail;

   /**
    * Constructs a new <code>MissingRequiredPropertyException</code>, with the
    * specified detail message.
    *
    * @param propertyName
    *    the name of the required property, not <code>null</code>.
    *
    * @param detail
    *    a more detailed description of why this property is required in this
    *    context, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    *
    * @since XINS 1.3.0
    */
   public MissingRequiredPropertyException(String propertyName, String detail)
   throws IllegalArgumentException {

      // Construct message and call superclass constructor
      super(propertyName, createMessage(propertyName, detail), null);

      // Store data
      _detail = detail;
   }

   /**
    * Constructs a new <code>MissingRequiredPropertyException</code>.
    *
    * @param propertyName
    *    the name of the required property, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    */
   public MissingRequiredPropertyException(String propertyName)
   throws IllegalArgumentException {
      this(propertyName, null);
   }

   /**
    * Creates message based on the specified constructor argument.
    *
    * @param propertyName
    *    the name of the property, may be <code>null</code>.
    *
    * @param detail
    *    a more detailed description of why this property is required in this
    *    context, can be <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    */
   private static String createMessage(String propertyName, String detail) {

      // Construct the message
      String message = "No value is set for the required property \"" + propertyName;

      // Append the detail message, if any
      message += (detail == null)
               ? "\"."
               : "\": " + detail;

      return message;
   }

   /**
    * Returns the detail message.
    *
    * @return
    *    the detail message, can be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   public String getDetail() {
      return _detail;
   }
}
