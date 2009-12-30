/*
 * $Id: InvalidRequestException.java,v 1.12 2007/03/12 10:40:31 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Exception that indicates that an incoming request is considered invalid.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/12 10:40:31 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class InvalidRequestException
extends Exception {

   /**
    * Constructs a new <code>InvalidRequestException</code> with the specified
    * detail message and cause exception.
    *
    * @param message
    *    the message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    */
   public InvalidRequestException(String message, Throwable cause) {
      super(message);
      if (cause != null) {
         initCause(cause);
      }
   }

   /**
    * Constructs a new <code>InvalidRequestException</code> with the specified
    * detail message.
    *
    * @param message
    *    the message, can be <code>null</code>.
    */
   public InvalidRequestException(String message) {
      this(message, null);
   }
}
