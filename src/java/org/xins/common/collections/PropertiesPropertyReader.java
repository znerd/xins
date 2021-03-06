/*
 * $Id: PropertiesPropertyReader.java,v 1.12 2007/03/12 10:40:46 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Properties;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Property reader based on a <code>Properties</code> object. When the
 * encapsulated {@link Properties} object changes, then this
 * <code>PropertiesPropertyReader</code> instance changes with it,
 * automatically.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/12 10:40:46 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class PropertiesPropertyReader
extends AbstractPropertyReader {

   /**
    * Constructs a new <code>PropertiesPropertyReader</code>.
    *
    * @param properties
    *    the {@link Properties} object to read from, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>.
    */
   public PropertiesPropertyReader(Properties properties)
   throws IllegalArgumentException {

      // Explicitly invoke superclass constructor
      super(properties);

      // Check preconditions
      MandatoryArgumentChecker.check("properties", properties);
   }
}
