/*
 * $Id: TimeOutException.java,v 1.10 2007/03/12 10:40:57 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

/**
 * Exception that indicates the total time-out for a service call was reached.
 *
 * @version $Revision: 1.10 $ $Date: 2007/03/12 10:40:57 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class TimeOutException extends Exception {

   /**
    * Constructs a new <code>TimeOutException</code>.
    */
   public TimeOutException() {
      // empty
   }
}
