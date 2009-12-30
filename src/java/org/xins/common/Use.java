/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

/**
 * Simple enumeration type for differentiating <em>optional</em> versus
 * <em>required</em> items.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public enum Use {

   /**
    * Constant indicating an item is considered <em>optional</em>.
    */
   OPTIONAL,

   /**
    * Constant indicating an item is considered <em>required</em>.
    */
   REQUIRED
}
