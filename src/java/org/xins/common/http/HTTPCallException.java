/*
 * $Id: HTTPCallException.java,v 1.17 2007/03/12 10:40:46 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.service.CallException;
import org.xins.common.service.TargetDescriptor;

/**
 * HTTP-specific call exception.
 *
 * @version $Revision: 1.17 $ $Date: 2007/03/12 10:40:46 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class HTTPCallException extends CallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = 4843166618439269741L;

   /**
    * Constructs a new <code>HTTPCallException</code> based on a short
    * reason, the original request, target called, call duration, detail
    * message and cause exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
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
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    */
   HTTPCallException(String           shortReason,
                     HTTPCallRequest  request,
                     TargetDescriptor target,
                     long             duration,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {
      super(shortReason, request, target, duration, detail, cause);
   }
}
