/*
 * $Id: InitializationException.java,v 1.28 2007/09/13 11:09:54 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.manageable;

import org.xins.common.text.TextUtils;

/**
 * Exception thrown when the initialization of a <code>Manageable</code>
 * object failed.
 *
 * @version $Revision: 1.28 $ $Date: 2007/09/13 11:09:54 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see Manageable#init(org.xins.common.collections.PropertyReader)
 */
public final class InitializationException extends Exception {

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * detail message.
    *
    * @param detail
    *    the detail message, or <code>null</code>.
    */
   public InitializationException(String detail) {
      this(detail, null);
   }

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * cause exception.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public InitializationException(Throwable cause) {
      this(null, cause);
   }

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * detail message and cause exception.
    *
    * @param detail
    *    the detail message, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public InitializationException(String detail, Throwable cause) {
      super(createMessage(detail, cause));
      if (cause != null) {
         initCause(cause);
      }
   }

   /**
    * Creates a message based on the specified constructor arguments.
    *
    * @param detail
    *    the detail message passed to the constructor, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    */
   private static String createMessage(String detail, Throwable cause) {

      String message = "Initialization failed";

      if (detail != null) {
         message += ": \"" + detail + '"';
      }

      if (cause != null) {
         message += ". Caught "  +cause.getClass().getName();

         String causeMessage = TextUtils.trim(cause.getMessage(), null);
         if (causeMessage != null) {
            message += " with message \"" + causeMessage + '"';
         }
      }
      message += '.';

      return message;
   }
}
