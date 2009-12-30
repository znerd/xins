/*
 * $Id: InternalErrorException.java,v 1.10 2007/03/12 10:40:45 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception thrown to indicate a standard error code was received that
 * indicates a server-side internal error.
 *
 * @version $Revision: 1.10 $ $Date: 2007/03/12 10:40:45 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public class InternalErrorException
extends StandardErrorCodeException {

   /**
    * Constructs a new <code>InternalErrorException</code>.
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
    * @param resultData
    *    the result data, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0
    *          || resultData  == null
    *          || resultData.{@link XINSCallResult#getErrorCode() getErrorCode()} == null</code>.
    */
   InternalErrorException(XINSCallRequest    request,
                          TargetDescriptor   target,
                          long               duration,
                          XINSCallResultData resultData)
   throws IllegalArgumentException {
      super(request, target, duration, resultData, null);
   }

   // XXX: Add methods for retrieval of details?
}
