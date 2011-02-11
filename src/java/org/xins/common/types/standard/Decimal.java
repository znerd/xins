/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.math.BigDecimal;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_decimal</em>, for decimal numbers. A value of this type
 * represents a decimal number, an instance of the {@link BigDecimal} class.
 *
 * @version $Revision: 1.37 $ $Date: 2007/09/18 11:21:02 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public class Decimal extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Decimal SINGLETON = new Date();

   /**
    * Constructs a new <code>Decimal</code> instance.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Decimal() {
      super("_decimal", Value.class);
   }

   /**
    * Constructs a <code>BigDecimal</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link BigDecimal} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static BigDecimal fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return new BigDecimal(string);
   }

   /**
    * Constructs a <code>BigDecimal</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Value}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForOptional(String string)
   throws TypeValueException {
      try {
         return (string == null) ? null : new BigDecimal(string);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(SINGLETON, string, cause);
      }
   }

   /**
    * Converts the specified <code>Date.Value</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value in the ISO format YYYYMMDD,
    *    or <code>null</code> if and only if <code>value == null</code>.
    */
   public static String toString(BigDecimal value) {
      return (value == null) ? null : value.toString();
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {
      try {
         new BigDecimal(value);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(this, value, cause);
      }
   }

   @Override
   protected final Object fromStringImpl(String string)
   throws TypeValueException {
      try {
         return new BigDecimal(value);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(this, value, cause);
      }
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a PropertyReader
      return value.toString();
   }

   @Override
   public String getDescription() {
      return "A decimal number.";
   }
}
