/*
 * $Id: InvalidResponseResult.java,v 1.26 2007/09/18 08:45:04 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Result code that indicates that an output parameter is either missing or
 * invalid.
 *
 * @version $Revision: 1.26 $ $Date: 2007/09/18 08:45:04 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class InvalidResponseResult extends InvalidMessageResult {

   /**
    * Constructs a new <code>InvalidResponseResult</code> object.
    */
   public InvalidResponseResult() {
      super(DefaultResultCodes._INVALID_RESPONSE.getName());
   }
}
