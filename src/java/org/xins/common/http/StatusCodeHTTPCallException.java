/*
 * $Id: StatusCodeHTTPCallException.java,v 1.19 2007/03/15 17:08:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception that indicates that an HTTP call failed because the returned HTTP
 * status code was considered invalid.
 *
 * @version $Revision: 1.19 $ $Date: 2007/03/15 17:08:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class StatusCodeHTTPCallException
extends HTTPCallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = 5165140514693822383L;

   /**
    * The returned HTTP status code.
    */
   private final int _code;

   /**
    * Constructs a new <code>StatusCodeHTTPCallException</code> based on the
    * original request, target called, call duration and HTTP status code.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param code
    *    the HTTP status code.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    *
    * @since XINS 1.5.0.
    */
   public StatusCodeHTTPCallException(HTTPCallRequest  request,
                                      TargetDescriptor target,
                                      long             duration,
                                      int              code)
   throws IllegalArgumentException {
      super("Unsupported HTTP status code " + code,
            request, target, duration, null, null);

      _code = code;
   }

   /**
    * Returns the HTTP status code.
    *
    * @return
    *    the HTTP status code that is considered unacceptable.
    */
   public int getStatusCode() {
      return _code;
   }

   /**
    * Determines whether the call has possibly (or definitely) been processed 
    * by the target service.
    *
    * <p>The implementation of this method in class
    * {@link StatusCodeHTTPCallException} returns <code>true</code> if and
    * only if <code>200 &lt;= getStatusCode() &lt;= 299</code>.
    *
    * @return
    *    <code>true</code> if the call has possibly or definitely been 
    *    processed, <code>false</code> if the call was definitely not 
    *    processed yet (so fail-over should be allowable).
    *
    * @since XINS 2.2
    */
   public boolean isCallPossiblyProcessed() {
      return _code >= 200 && _code <= 299;
   }
}
