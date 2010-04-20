/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Exception that indicates that a calling convention failed to convert an
 * incoming HTTP request to a XINS <code>FunctionRequest</code>.
 *
 * @version $Revision: 1.9 $ $Date: 2007/03/12 10:40:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class ConvertRequestException
extends Exception {

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = -9033187236433455358L;

   /**
    * Constructs a new <code>ConvertRequestException</code>
    * with no detail message.
    */
   public ConvertRequestException() {
      // empty
   }

   /**
    * Constructs a new <code>ConvertRequestException</code>
    * with the specified detail message.
    *
    * @param detail
    *    the detail message, or <code>null</code>.
    */
   public ConvertRequestException(String detail) {
      super(detail);
   }

   /**
    * Constructs a new <code>ConvertRequestException</code>
    * with the specified cause exception.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public ConvertRequestException(Throwable cause) {
      if (cause != null) {
         initCause(cause);
      }
   }

   /**
    * Constructs a new <code>ConvertRequestException</code>
    * with the specified detail message and cause exception.
    *
    * @param detail
    *    the detail message, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public ConvertRequestException(String detail, Throwable cause) {
      super(detail);
      if (cause != null) {
         initCause(cause);
      }
   }
}
