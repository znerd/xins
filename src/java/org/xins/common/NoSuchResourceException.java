// See the COPYRIGHT file for copyright and license information
package org.xins.common;

/**
 * Exception that indicates that a meta data resource could not be found.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
class NoSuchResourceException extends RuntimeException {

   /**
    * Serialization ID.
    */
   private static final long serialVersionUID = 4494839011837180109L;

   /**
    * Constructs a new <code>NoSuchResourceException</code>.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    */
   public NoSuchResourceException(String message) {
      super(message);
   }

   /**
    * Constructs a new <code>NoSuchResourceException</code>.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code> if none.
    */
   public NoSuchResourceException(String message, Throwable cause) {
      this(message);
      if (cause != null) {
         initCause(cause);
      }
   }
}
