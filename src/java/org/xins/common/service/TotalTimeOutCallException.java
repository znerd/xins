/*
 * $Id: TotalTimeOutCallException.java,v 1.19 2007/03/12 10:40:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

/**
 * Exception that indicates the total time-out for a request was reached, so
 * the request was aborted.
 *
 * @version $Revision: 1.19 $ $Date: 2007/03/12 10:40:49 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class TotalTimeOutCallException
extends GenericCallException {

   /**
    * Constructs a new <code>TotalTimeOutCallException</code>.
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
   public TotalTimeOutCallException(CallRequest      request,
                                    TargetDescriptor target,
                                    long             duration)
   throws IllegalArgumentException {
      super("Total time-out", request, target, duration, null, null);
   }
}
