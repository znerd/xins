/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

/**
 * Interface for objects that support a <code>toXML()</code> method.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public interface ToXML {

   /**
    * Serializes this object to an XML element.
    *
    * @return
    *    an XML {@link Element} representing this object,
    *    never <code>null</code>. 
    */
   Element toXML();
}
