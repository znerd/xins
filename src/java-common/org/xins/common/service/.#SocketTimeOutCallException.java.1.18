/*
 * $Id: SocketTimeOutCallException.java,v 1.18 2007/03/12 10:40:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

/**
 * Exception that indicates that data was not received on a socket within a
 * designated time-out period.
 *
 * @version $Revision: 1.18 $ $Date: 2007/03/12 10:40:49 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class SocketTimeOutCallException
extends GenericCallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = 3960746542315816035L;

   /**
    * Constructs a new <code>SocketTimeOutCallException</code>.
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
   public SocketTimeOutCallException(CallRequest      request,
                                     TargetDescriptor target,
                                     long             duration)
   throws IllegalArgumentException {
      super("Socket time-out", request, target, duration, null, null);
   }
}
