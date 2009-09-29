/*
 * $Id: ElementSerializationException.java 7484 2009-01-06 14:33:15Z ernst $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

/**
 * Exception that is thrown when the serialization of an XML element fails.
 * Cause may be that an option is set to an invalid or unsupported value.
 *
 * @version $Revision: 7484 $ $Date: 2009-01-06 15:33:15 +0100 (di, 06 jan 2009) $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public final class ElementSerializationException extends RuntimeException {

   /**
    * Constructs a new <code>ElementSerializationException</code>.
    *
    * @param message
    *    the message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    */
   public ElementSerializationException(String message, Throwable cause) {
      super(message, cause);
   }
}
