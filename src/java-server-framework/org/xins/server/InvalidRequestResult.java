/*
 * $Id: InvalidRequestResult.java,v 1.25 2007/09/18 08:45:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Result code that indicates that an input parameter is either missing or invalid.
 *
 * @version $Revision: 1.25 $ $Date: 2007/09/18 08:45:06 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class InvalidRequestResult extends InvalidMessageResult {

   /**
    * Constructs a new <code>InvalidRequestResult</code> object.
    */
   public InvalidRequestResult() {
      super(DefaultResultCodes._INVALID_REQUEST.getName());
   }
}
