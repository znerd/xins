/*
 * $Id: NoRouteToHostCallException.java,v 1.8 2007/03/12 10:40:50 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

/**
 * Exception that indicates that a connection to a service could not be
 * established because no network route could be found to the host.
 *
 * @version $Revision: 1.8 $ $Date: 2007/03/12 10:40:50 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.3.0
 */
public final class NoRouteToHostCallException
extends ConnectionCallException {

   /**
    * Constructs a new <code>NoRouteToHostCallException</code>.
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
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    */
   public NoRouteToHostCallException(CallRequest      request,
                                     TargetDescriptor target,
                                     long             duration)
   throws IllegalArgumentException {
      super("No route to host", request, target, duration, null, null);
   }
}
