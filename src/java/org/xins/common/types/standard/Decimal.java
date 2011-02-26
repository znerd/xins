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
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public class Decimal extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Decimal SINGLETON = new Decimal();

   /**
    * Constructs a new <code>Decimal</code> instance.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Decimal() {
      this("_decimal", null, null, Integer.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Decimal</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param minimum
    *    the minimum for the value.
    *
    * @param maximum
    *    the maximum for the value.
    */
   protected Decimal(String name, BigDecimal minimum, BigDecimal maximum, int maxDecimals) {
      super(name, BigDecimal.class);

      _minimum = minimum;
      _maximum = maximum;
      _maxDecimals = maxDecimals;
   }

   /**
    * The minimum value that this type can have.
    * If it is <code>null</code>, then there is no minimum.
    */
   private final BigDecimal _minimum;

   /**
    * The maximum value that this type can have.
    * If it is <code>null</code>, then there is no maximum.
    */
   private final BigDecimal _maximum;

   private final int _maxDecimals;

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
    *    the {@link BigDecimal}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static BigDecimal fromStringForOptional(String string)
   throws TypeValueException {
      try {
         return (string == null) ? null : new BigDecimal(string);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(SINGLETON, string, cause);
      }
   }

   /**
    * Converts the specified <code>BigDecimal</code> to a string.
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

      // FIXME TODO: Check _maximum
      // FIXME TODO: Check _minimum
      // FIXME TODO: Check _maxDecimals
   }

   @Override
   protected final Object fromStringImpl(String string)
   throws TypeValueException {
      try {
         return new BigDecimal(string);
      } catch (NumberFormatException cause) {
         throw new TypeValueException(this, string, cause);
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
