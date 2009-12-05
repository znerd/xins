/*
 * $Id: PatternCompileException.java,v 1.12 2007/03/12 10:40:50 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

/**
 * Exception thrown to indicate a pattern string could not be compiled.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/12 10:40:50 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class PatternCompileException extends RuntimeException {

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = -234987621378228272L;

   /**
    * Creates a new <code>PatternCompileException</code>.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    */
   protected PatternCompileException(String message) {
      super(message);
   }
}
