/*
 * $Id: FormatException.java,v 1.17 2007/03/16 09:54:59 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception thrown if a character string does not match a certain format.
 *
 * @version $Revision: 1.17 $ $Date: 2007/03/16 09:54:59 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class FormatException extends RuntimeException {

   /**
    * The string that is considered invalid. Cannot be <code>null</code>.
    */
   private final String _string;

   /**
    * The reason for the string to be considered invalid. Can be
    * <code>null</code>.
    */
   private final String _reason;

   /**
    * Constructs a <code>FormatException</code>.
    *
    * @param string
    *    the character string that mismatches the format, cannot be
    *    <code>null</code>.
    *
    * @param reason
    *    description of the problem, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    */
   public FormatException(String string, String reason)
   throws IllegalArgumentException {

      // Call superclass
      super(createMessage(string, reason));

      // Store information
      _string = string;
      _reason = reason;
   }

   /**
    * Creates a message for the constructor.
    *
    * @param string
    *    the character string that mismatches the format, cannot be
    *    <code>null</code>.
    *
    * @param reason
    *    description of the problem, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @return
    *    the message the constructor can pass up to the superclass
    *    constructor, never <code>null</code>.
    */
   private static String createMessage(String string, String reason)
   throws IllegalArgumentException {

      // Check the precondition
      MandatoryArgumentChecker.check("string", string);

      String message = "The string \"" + string + "\" is invalid.";
      if (reason != null) {
         message += " Reason: " + reason;
      }

      return message;
   }

   /**
    * Returns the string that is considered invalid.
    *
    * @return
    *    the string that is considered invalid, cannot be <code>null</code>.
    */
   public String getString() {
      return _string;
   }

   /**
    * Returns the reason.
    *
    * @return
    *    the reason for the string to be considered invalid, can be
    *    <code>null</code>.
    */
   public String getReason() {
      return _reason;
   }
}
