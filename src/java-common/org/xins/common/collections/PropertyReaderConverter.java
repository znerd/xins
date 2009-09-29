/*
 * $Id: PropertyReaderConverter.java,v 1.16 2007/03/16 09:54:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Iterator;
import java.util.Properties;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Converter that is able to convert <code>PropertyReader</code> objects to
 * other kinds of objects.
 *
 * @version $Revision: 1.16 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.1
 */
public final class PropertyReaderConverter {

   /**
    * Constructs a new <code>PropertyReaderConverter</code>.
    */
   private PropertyReaderConverter() {
      // empty
   }

   /**
    * Converts the specified <code>PropertyReader</code> object to a new
    * <code>Properties</code> object.
    *
    * @param propertyReader
    *    the {@link PropertyReader} object, cannot be <code>null</code>.
    *
    * @return
    *    a new {@link Properties} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyReader == null</code>.
    */
   public static Properties toProperties(PropertyReader propertyReader)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("propertyReader", propertyReader);

      Properties prop = new Properties();
      for (String key : propertyReader.names()) {
         prop.setProperty(key, propertyReader.get(key));
      }

      return prop;
   }
}
