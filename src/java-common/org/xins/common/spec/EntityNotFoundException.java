/*
 * $Id: EntityNotFoundException.java,v 1.11 2007/09/18 11:20:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Thrown when the required entity cannot be found in the API.
 *
 * @version $Revision: 1.11 $ $Date: 2007/09/18 11:20:49 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public class EntityNotFoundException extends Exception {

   /**
    * Creates a new <code>InvalidSpecificationException</code> with the reason
    * of the problem.
    *
    * @param message
    *    the reason why this exception has been thrown, can be <code>null</code>.
    */
   EntityNotFoundException(String message) {
      super(message);
   }
}
