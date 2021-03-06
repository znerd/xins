/*
 * $Id: ParseException.java,v 1.18 2007/03/15 17:08:30 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

/**
 * Exception thrown to indicate a parsing failure.
 *
 * @version $Revision: 1.18 $ $Date: 2007/03/15 17:08:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class ParseException extends Exception {

   private static final long serialVersionUID = -701230123463647121L;

   /**
    * Description of the parse error. Can be <code>null</code>.
    */
   private final String _detail;

   /**
    * Constructs a new <code>ParseException</code> with no message, no cause
    * exception and no detailed description of the parse problem.
    */
   public ParseException() {
      this(null, null, null);
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified message.
    *
    * @param message
    *    the message, can be <code>null</code>.
    */
   public ParseException(String message) {
      this(message, null, null);
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified message
    * and cause exception.
    *
    * @param message
    *    the message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @since XINS 3.0
    */
   public ParseException(String message, Throwable cause) {
      this(message, cause, null);
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified
    * message, cause exception and detail.
    *
    * @param message
    *    the message to be returned by {@link #getMessage()}, can be
    *    <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @param detail
    *    description of the parse problem only, to be returned by
    *    {@link #getDetail()}, can be <code>null</code>.
    */
   public ParseException(String message, Throwable cause, String detail) {
      super(message);
      if (cause != null) {
         initCause(cause);
      }

      _detail = detail;
   }

   /**
    * Returns a description of the parse error.
    *
    * @return
    *    a description of the parse error, or <code>null</code> if none is
    *    available.
    */
   public String getDetail() {
      return _detail;
   }
}
