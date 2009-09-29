/*
 * $Id: EnumType.java,v 1.25 2007/05/21 10:55:50 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Abstract base class for enumeration types. An enumeration type only accepts
 * a defined set of values.
 *
 * @version $Revision: 1.25 $ $Date: 2007/05/21 10:55:50 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see EnumItem
 */
public abstract class EnumType extends Type {

   /**
    * Map that links symbolic names to enumeration values.
    */
   private final Map _namesToValues;

   /**
    * Map that links enumeration values to their symbolic names.
    */
   private final Map _valuesToNames;

   /**
    * Map that links symbolic names to enumeration item objects.
    */
   protected final Map _namesToItems;

   /**
    * Map that links enumeration values to enumeration item objects.
    */
   protected final Map _valuesToItems;

   /**
    * List of the <code>EnumItem</code>.
    */
   private final List _items;

   /**
    * The list of accepted values.
    */
   private final String[] _values;

   /**
    * Creates a new <code>EnumType</code> instance. The name of the type needs
    * to be specified. The value class (see {@link Type#getValueClass()}) is
    * set to {@link String String.class}.
    *
    * <p />The items this type accepts should be passed. If
    * <code>items == null</code>, then this type will contain no items. This
    * is the same as passing a zero-size {@link EnumItem} array.
    *
    * <p />Note that the <code>items</code> array may contain
    * <code>null</code> values. These will be ignored.
    *
    * @param name
    *    the name of the type, not <code>null</code>.
    *
    * @param items
    *    the items for the type, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public EnumType(String name, EnumItem[] items)
   throws IllegalArgumentException {
      super(name, EnumItem.class);

      _namesToValues = new HashMap();
      _valuesToNames = new HashMap();
      _namesToItems  = new HashMap();
      _valuesToItems = new HashMap();
      _items = new ArrayList();

      int count = items == null ? 0 : items.length;
      String[] values = new String[count];
      int actualItems = 0;
      for (int i = 0; i < count; i++) {
         EnumItem item = items[i];
         if (item != null) {
            String itemName  = item.getName();
            String itemValue = item.getValue();

            _namesToValues.put(itemName,  itemValue);
            _valuesToNames.put(itemValue, itemName);
            values[actualItems++] = itemValue;
            _namesToItems.put(itemName,   item);
            _valuesToItems.put(itemValue, item);
            _items.add(item);
         }
      }

      _values = new String[actualItems];
      System.arraycopy(values, 0, _values, 0, actualItems);
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {
      for (int i = 0; i < _values.length; i++) {
         if (_values[i].equals(value)) {
            return;
         }
      }
      throw new TypeValueException(this, value, "Not an item in this enumeration type.");
   }

   /**
    * Converts the specified <code>EnumItem</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public String toString(EnumItem value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      } else {
         return value.getValue();
      }
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      EnumItem item = (EnumItem) value;
      return item.getValue();
   }

   /**
    * Gets the value matching the specified name.
    *
    * @param name
    *    the name to match a corresponding value by, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the corresponding value, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public final String getValueByName(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      return (String) _namesToValues.get(name);
   }

   /**
    * Gets the name matching the specified value.
    *
    * @param value
    *    the value to match a corresponding name by, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the corresponding name, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    */
   public final String getNameByValue(String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      return (String) _valuesToNames.get(value);
   }

   /**
    * Get the list of the EnumItem included in this <code>EnumType</code>.
    *
    * @return
    *    the list of {@link EnumItem} included in this <code>EnumType</code>.
    */
   public final List getEnumItems() {
      return _items;
   }
}
