/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

/**
 * Interface for objects that support a <code>toPropertyReader()</code>
 * method.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public interface ToPropertyReader {

   /**
    * Serializes this object to a set of string to string mappings.
    *
    * @return
    *    an {@link PropertyReader} representing this object,
    *    never <code>null</code>. 
    */
   PropertyReader toPropertyReader();
}
