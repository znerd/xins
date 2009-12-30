/*
 * $Id: PropertyException.java 7938 2009-01-27 13:29:15Z ernst $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception thrown to indicate a problem with a property.
 *
 * @version $Revision: 7938 $ $Date: 2009-01-27 14:29:15 +0100 (di, 27 jan 2009) $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public abstract class PropertyException extends Exception {

   /**
    * Constructs a new <code>PropertyException</code> with the specified
    * property name.
    *
    * @param name
    *    the property name, cannot be <code>null</code>.
    *
    * @param message
    *    the detail message to be returned by {@link #getMessage()},
    *    can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, to be returned by {@link #getCause()},
    *    can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   PropertyException(String name, String message, Throwable cause) {
      super(message);
      MandatoryArgumentChecker.check("name", name);
      _name = name;

      if (cause != null) {
         initCause(cause);
      }
   }

   /**
    * The name of the property. Cannot be <code>null</code>.
    */
   private final String _name;

   /**
    * Returns the name of the property.
    *
    * @return
    *    the name of the property, never <code>null</code>.
    */
   public final String getPropertyName() {
      return _name;
   }
}
