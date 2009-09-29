/*
 * $Id: List.java,v 1.28 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

import java.util.StringTokenizer;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.FormatException;
import org.xins.common.text.URLEncoding;
import org.xins.common.types.standard.Text;

/**
 * Abstract base class for list types.
 *
 * @version $Revision: 1.28 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public abstract class List extends Type {

   /**
    * The type for the values. Cannot be <code>null</code>.
    */
   private final Type _itemType;

   /**
    * Constructs a new <code>List</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param itemType
    *    the type for the values, or <code>null</code> if {@link Text}
    *    should be assumed.
    */
   protected List(String name, Type itemType) {
      super(name, ItemList.class);

      _itemType = itemType == null ? Text.SINGLETON : itemType;
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(value, "&");
      while (tokenizer.hasMoreTokens()) {

         String token = tokenizer.nextToken();
         try {
            _itemType.checkValue(URLEncoding.decode(token));

         // Handle URL decoding error
         } catch (FormatException cause) {
            throw new TypeValueException(this, value, cause);

         // Item type does not accept the item
         } catch (TypeValueException cause) {
            throw new TypeValueException(this, value, cause);

         } catch (IllegalArgumentException cause) { // TODO: Review, should we really catch this one?
            throw new TypeValueException(this, value, cause);
         }
      }
   }

   @Override
   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      // Construct a ItemList to store the values in
      ItemList list = createList();

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         try {
            String itemString = URLEncoding.decode(token);
            Object item = _itemType.fromString(itemString);
            list.addItem(item);
         } catch (FormatException fe) {
            throw new TypeValueException(this, string, fe.getReason());
         } catch (IllegalArgumentException iae) {
            throw Utils.logProgrammingError(iae);
         }
      }

      return list;
   }

   /**
    * Creates a new <code>ItemList</code>.
    *
    * @return
    *    the new list created, never <code>null</code>.
    */
   public abstract ItemList createList();

   /**
    * Converts the specified <code>ItemList</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public String toString(ItemList value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      // Use a buffer to create the string
      StringBuffer buffer = new StringBuffer(255);

      // Iterate over the list
      int listSize = value.getSize();
      for (int i=0; i < listSize; i++) {
         if (i != 0) {
            buffer.append('&');
         }

         Object nextItem = value.getItem(i);
         String stringItem;
         try {
            stringItem = _itemType.toString(nextItem);
         } catch (Exception ex) {

            // Should never happens as only add() is able to add items in the list.
            throw new IllegalArgumentException("Incorrect value for type: " + nextItem);
         }
         buffer.append(URLEncoding.encode(stringItem));
      }

      return buffer.toString();
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a ItemList
      return toString((ItemList) value);
   }
}
