/*
 * $Id: Set.java,v 1.12 2007/09/18 11:21:04 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.types.ItemList;
import org.xins.common.types.TypeValueException;

/**
 * Standard type <em>_set</em>.
 *
 * @version $Revision: 1.12 $ $Date: 2007/09/18 11:21:04 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.5.0.
 */
public final class Set extends org.xins.common.types.List {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Set SINGLETON = new Set();

   /**
    * Constructs a new <code>Set</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Set() {
      super("_set", Text.SINGLETON);
   }

   public ItemList createList() {
      return new Value();
   }

   /**
    * Constructs a <code>Set.Value</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link Set.Value} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>Set.Value</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Set.Value}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForOptional(String string)
   throws TypeValueException {
      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified <code>Set.Value</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(Value value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }
      return SINGLETON.toString((ItemList) value);
   }

   public String getDescription() {
      return "An ampersand separated set of text.";
   }

   /**
    * Inner class that represents a set of String.
    */
   public static final class Value extends ItemList {

      /**
       * Creates a new set.
       */
      public Value() {
         super(true);
      }

      /**
       * Add a new element in the set.
       *
       * @param value
       *    the new value to add, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>value == null</code>.
       */
      public void add(String value) throws IllegalArgumentException {
         MandatoryArgumentChecker.check("value", value);
         addItem(value);
      }

      /**
       * Get an element from the set.
       *
       * @param index
       *    The position of the required element.
       *
       * @return
       *    The element at the specified position, cannot be <code>null</code>.
       */
      public String get(int index) {
         return (String) getItem(index);
      }
   }
}