/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that a connection to a service could not be
 * established due to an SSL-level error.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public final class SSLConnectCallException
extends ConnectionCallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = 7017728324642133321L;

   /**
    * Constructs a new <code>SSLConnectCallException</code>.
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
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0
    *          || cause       == null</code>.
    *
    */
   public SSLConnectCallException(CallRequest      request,
                                  TargetDescriptor target,
                                  long             duration,
                                  Throwable        cause)
   throws IllegalArgumentException {
      super("SSL-level connect failed", request, target, duration, null, cause);
      MandatoryArgumentChecker.check("cause", cause);
   }
}
