/*
 * $Id: HTTPCallResultData.java,v 1.13 2007/03/12 10:40:46 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

/**
 * Abstraction of the data part of an HTTP call result.
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/12 10:40:46 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public interface HTTPCallResultData {

   /**
    * Returns the HTTP status code.
    *
    * @return
    *    the HTTP status code.
    */
   int getStatusCode();

   /**
    * Returns the result data as a byte array. Note that this is not a copy or
    * clone of the internal data structure, but it is a link to the actual
    * data structure itself.
    *
    * @return
    *    a byte array of the result data, never <code>null</code>.
    */
   byte[] getData();
}
