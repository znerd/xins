/*
 * $Id: Boolean.java,v 1.19 2007/08/27 11:18:20 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_boolean</em>.
 *
 * @version $Revision: 1.19 $ $Date: 2007/08/27 11:18:20 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class Boolean extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Boolean SINGLETON = new org.xins.common.types.standard.Boolean();

   /**
    * Constructs a new <code>Boolean</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Boolean() {
      super("_boolean", java.lang.Boolean.class);
   }

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>boolean</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>boolean</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static boolean fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if ("true".equals(string)) {
         return true;
      } else if ("false".equals(string)) {
         return false;
      } else if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified string value to a <code>java.lang.Boolean</code>
    * value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link java.lang.Boolean}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static java.lang.Boolean fromStringForOptional(String string)
   throws TypeValueException {
      if ("true".equals(string)) {
         return java.lang.Boolean.TRUE;
      } else if ("false".equals(string)) {
         return java.lang.Boolean.FALSE;
      } else if (string == null) {
         return null;
      } else {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified <code>Boolean</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(java.lang.Boolean value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.booleanValue());
      }
   }

   /**
    * Converts the specified <code>boolean</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(boolean value) {
      return value ? "true" : "false";
   }

   @Override
   protected void checkValueImpl(String string) throws TypeValueException {
      if (! ("true".equals(string) || "false".equals(string))) {
         throw new TypeValueException(this, string, "Expected either \"true\" or \"false\".");
      }
   }

   @Override
   protected Object fromStringImpl(String string) {
      return "true".equals(string) ? java.lang.Boolean.TRUE
                                   : java.lang.Boolean.FALSE;
   }

   @Override
   public String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      java.lang.Boolean b = (java.lang.Boolean) value;
      return b.toString();
   }
   
   @Override
   public String getDescription() {
      return "A boolean, either 'true' or 'false'.";
   }
}
