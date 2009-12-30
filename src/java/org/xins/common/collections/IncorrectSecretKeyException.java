/*
 * $Id: IncorrectSecretKeyException.java,v 1.7 2007/03/12 10:40:46 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

/**
 * Exception that indicates a secret key argument did not match the actual
 * secret key.
 *
 * @version $Revision: 1.7 $ $Date: 2007/03/12 10:40:46 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public final class IncorrectSecretKeyException
extends IllegalArgumentException {

   /**
    * Constructs a new <code>IncorrectSecretKeyException</code>.
    */
   IncorrectSecretKeyException() {
      super("Incorrect secret key.");
   }
}
