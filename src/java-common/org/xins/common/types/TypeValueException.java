/*
 * $Id: TypeValueException.java,v 1.17 2007/03/16 09:54:59 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception thrown to indicate a value is invalid for a certain type.
 *
 * @version $Revision: 1.17 $ $Date: 2007/03/16 09:54:59 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class TypeValueException extends Exception {

   /**
    * The concerning parameter type. This field is never <code>null</code>.
    */
   private final Type _type;

   /**
    * The value that is considered invalid. This field is never <code>null</code>.
    */
   private final String _value;

   /**
    * The additional detail information passed to the constructor. Can be
    * <code>null</code>.
    */
   private final String _detail;

   /**
    * Creates a new <code>TypeValueException</code>.
    *
    * @param type
    *    the type, not <code>null</code>.
    *
    * @param value
    *    the value, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || value == null</code>.
    */
   public TypeValueException(Type type, String value)
   throws IllegalArgumentException {
      this(type, value, (String) null);
   }

   /**
    * Creates a new <code>TypeValueException</code>.
    *
    * @param type
    *    the type, not <code>null</code>.
    *
    * @param value
    *    the value, not <code>null</code>.
    *
    * @param detail
    *    additional detail information, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || value == null</code>.
    */
   public TypeValueException(Type type, String value, String detail)
   throws IllegalArgumentException {
      this(type, value, detail, (Throwable) null);
   }

   /**
    * Creates a new <code>TypeValueException</code>.
    *
    * @param type
    *    the type, not <code>null</code>.
    *
    * @param value
    *    the value, not <code>null</code>.
    *
    * @param cause
    *    the cause exception, if any, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || value == null</code>.
    */
   public TypeValueException(Type type, String value, Throwable cause)
   throws IllegalArgumentException {
      this(type, value, (String) null, cause);
   }

   /**
    * Creates a new <code>TypeValueException</code>.
    *
    * @param type
    *    the type, not <code>null</code>.
    *
    * @param value
    *    the value, not <code>null</code>.
    *
    * @param detail
    *    additional detail information, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, if any, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || value == null</code>.
    */
   public TypeValueException(Type type, String value, String detail, Throwable cause)
   throws IllegalArgumentException {

      super(createMessage(type, value, detail));

      // Store the arguments
      _type   = type;
      _value  = value;
      _detail = detail;

      if (cause != null) {
         initCause(cause);
      }
   }

   /**
    * Creates a message for the constructor after checking the arguments.
    *
    * @param type
    *    the type, not <code>null</code>.
    *
    * @param value
    *    the value, not <code>null</code>.
    *
    * @param detail
    *    additional detail information, can be <code>null</code>.
    *
    * @return
    *    the message to be passed up to the superconstructor, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || value == null</code>.
    */
   private static final String createMessage(Type   type,
                                             String value,
                                             String detail)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("type", type, "value", value);

      String message = "The string \"" + value + "\" does not represent a valid value for the type " + type.getName();
      if (detail != null) {
         message += ": " + detail;
      } else {
         message += '.';
      }

      return message;
   }

   /**
    * Retrieves the type.
    *
    * @return
    *    the type, never <code>null</code>.
    */
   public final Type getType() {
      return _type;
   }

   /**
    * Retrieves the value that was considered invalid.
    *
    * @return
    *    the value that was considered invalid, not <code>null</code>.
    */
   public final String getValue() {
      return _value;
   }

   /**
    * Retrieves the additional detail information passed to the constructor.
    * This information is optional.
    *
    * @return
    *    the additional detailed information, can be <code>null</code>.
    *
    * @since XINS 1.2.0
    */
   public final String getDetail() {
      return _detail;
   }
}
