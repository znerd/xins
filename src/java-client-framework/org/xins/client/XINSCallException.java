/*
 * $Id: XINSCallException.java,v 1.18 2007/03/12 10:40:41 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.service.CallException;
import org.xins.common.service.TargetDescriptor;

/**
 * XINS-specific call exception.
 *
 * @version $Revision: 1.18 $ $Date: 2007/03/12 10:40:41 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class XINSCallException extends CallException {

   /**
    * Constructs a new <code>XINSCallException</code> based on a short reason,
    * the original request, target called, call duration, detail message and
    * cause exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, can be <code>null</code>.
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
    *          || duration &lt; 0</code>.
    */
   XINSCallException(String           shortReason,
                     XINSCallRequest  request,
                     TargetDescriptor target,
                     long             duration,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {

      // Call superconstructor
      super(shortReason, request, target, duration, detail, cause);
   }

   /**
    * Constructs a new <code>XINSCallException</code> based on a short reason,
    * a XINS call result, detail message and cause exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
    *
    * @param result
    *    the call result, cannot be <code>null</code>.
    *
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null || result == null</code>.
    */
   XINSCallException(String           shortReason,
                     XINSCallResult   result,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {

      // Call superconstructor
      super(shortReason,
            (result == null) ? null : result.getRequest(),
            (result == null) ? null : result.getSucceededTarget(),
            (result == null) ?   0L : result.getDuration(),
            detail,
            cause);
   }
}
