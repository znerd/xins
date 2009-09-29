/*
 * $Id: BasicPropertyReader.java,v 1.13 2007/09/24 12:18:48 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.HashMap;
import java.util.Iterator;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Modifiable implementation of a property reader.
 *
 * @version $Revision: 1.13 $ $Date: 2007/09/24 12:18:48 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class BasicPropertyReader
extends AbstractPropertyReader {

   /**
    * Constructs an empty <code>BasicPropertyReader</code>.
    */
   public BasicPropertyReader() {
      super(new HashMap<String,String>(89));
   }

   /**
    * Constructs a <code>BasicPropertyReader</code> and copies all specified
    * properties into it. If the passed {@link PropertyReader} instance is
    * <code>null</code>, then it is simply ignored and an empty
    * <code>BasicPropertyReader</code> instance is created.
    *
    * @param properties
    *    the properties to copy into the new instance, or <code>null</code>.
    *
    * @since XINS 3.0
    */
   public BasicPropertyReader(PropertyReader properties) {
      this();

      if (properties != null) {
         for (String name : properties.names()) {
            set(name, properties.get(name));
         }
      }
   }

   /**
    * Sets the specified property.
    *
    * <p>If <code>value == null</code>, then the property is removed.
    *
    * @param name
    *    the name of the property to set or reset, cannot be
    *    <code>null</code>.
    *
    * @param value
    *    the value for the property, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public void set(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Remove the current value
      if (value == null) {
         getPropertiesMap().remove(name);

      // Store a new value
      } else {
         getPropertiesMap().put(name, value);
      }
   }

   /**
    * Removes the specified property. If the property is not found, then
    * nothing happens.
    *
    * @param name
    *    the name of the property to remove, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public void remove(String name) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Remove the property
      getPropertiesMap().remove(name);
   }
}
