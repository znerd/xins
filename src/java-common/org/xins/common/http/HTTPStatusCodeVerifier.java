/*
 * $Id: HTTPStatusCodeVerifier.java,v 1.9 2007/01/04 10:17:26 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

/**
 * Abstraction of an HTTP status code verifier.
 *
 * @version $Revision: 1.9 $ $Date: 2007/01/04 10:17:26 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public interface HTTPStatusCodeVerifier {

   /**
    * Checks if the specified HTTP status code is considered acceptable or
    * unacceptable.
    *
    * @param code
    *    the HTTP status code to check.
    *
    * @return
    *    <code>true</code> if the specified HTTP status code is considered
    *    acceptable, <code>false</code> otherwise.
    */
   boolean isAcceptable(int code);
}
