/*
 * $Id: StatsPropertyReader.java,v 1.12 2007/07/04 15:13:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Immutable property reader that remembers which properties have not been
 * accessed.
 *
 * @version $Revision: 1.12 $ $Date: 2007/07/04 15:13:49 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.3.0
 */
public final class StatsPropertyReader implements PropertyReader {

   /**
    * Confidential object used to protect the internal
    * <code>ProtectedPropertyReader</code> instances from unauthorized
    * changes. Not <code>null</code>.
    */
   private static final Object SECRET_KEY = new Object();

   /**
    * The set of properties to retrieve values from. Never <code>null</code>.
    */
   private final Map<String,String> _properties;

   /**
    * The set of unused properties. Initially contains all properties. Becomes
    * <code>null</code> if there are no more unused properties.
    */
   private ProtectedPropertyReader _unused;

   /**
    * Constructs a new <code>StatsPropertyReader</code> based on the specified
    * <code>PropertyReader</code>. The properties in <code>source</code> are
    * copied to an internal store.
    *
    * @param source
    *    the source property reader, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>source == null</code>.
    */
   public StatsPropertyReader(PropertyReader source)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("source", source);

      // Prepare collections for storing the name/value combinations
      _properties = new HashMap<String,String>();
      _unused     = new ProtectedPropertyReader(SECRET_KEY);

      // Copy the property reader to an internal HashMap and to a set of
      // unused properties
      for (String name : source.names()) {
         String value = source.get(name);
         _properties.put(name, value);
         _unused.set(SECRET_KEY, name, value);
      }
   }

   /**
    * Gets the value of the property with the specified name.
    *
    * @param name
    *    the name of the property, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, or <code>null</code> if it is not set.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String get(String name) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Retrieve the value
      String value = _properties.get(name);

      // If the property is found, then mark it used
      if (value != null && _unused != null) {
         _unused.remove(SECRET_KEY, name);

         // If the size of the ProtectedPropertyReader becomes zero, then
         // remove it to save memory
         if (_unused.size() < 1) {
            _unused = null;
         }
      }

      return value;
   }

   /**
    * Gets an iterator that iterates over all the property names. The
    * {@link Iterator} will return only {@link String} instances.
    *
    * @return
    *    the {@link Iterator} that will iterate over all the names, never
    *    <code>null</code>.
    *
    * @deprecated
    *    Since XINS 3.0. Use {@link #names()} instead, which is type-safe
    *    and supports the
    *    <a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/foreach.html">Java 5 foreach-operator</a>.
    */
   public Iterator getNames() {
      return names().iterator();
   }

   /**
    * Gets all property names, as a <code>Collection</code> of
    * <code>String</code> objects.
    *
    * @return
    *    all property names, never <code>null</code>
    *    (but may be an empty {@link Collection}).
    *
    * @since XINS 3.0
    */
   public Collection<String> names() {
      return _properties.keySet();
   }

   /**
    * Returns the number of entries.
    *
    * @return
    *    the size, always &gt;= 0.
    */
   public int size() {
      return _properties.size();
   }

   /**
    * Retrieves the set of unused properties.
    *
    * @return
    *    a {@link PropertyReader} containing which were not queried, never
    *    <code>null</code>.
    */
   public PropertyReader getUnused() {
      if (_unused != null) {
         return _unused;
      } else {
         return PropertyReaderUtils.EMPTY_PROPERTY_READER;
      }
   }

   /**
    * Compares this object with the specified argument for equality.
    *
    * @param obj
    *    the object to compare with, can be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the objects <code>a</code> and <code>b</code>
    *    are considered to be equal, <code>false</code> if they are considered
    *    different.
    */
   public boolean equals(Object obj) {
      return PropertyReaderUtils.equals(this, obj);
   }

   /**
    * Returns a hash code value for this object.
    *
    * @return
    *    a hash code value for this object.
    */
   public int hashCode() {
      return PropertyReaderUtils.hashCode(this);
   }
}
