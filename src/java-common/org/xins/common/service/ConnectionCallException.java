/*
 * $Id: ConnectionCallException.java,v 1.16 2007/03/12 10:40:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

/**
 * Exception that indicates that a connection to a service could not be
 * established.
 *
 * @version $Revision: 1.16 $ $Date: 2007/03/12 10:40:49 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class ConnectionCallException
extends GenericCallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = -331358001038403428L;

   /**
    * Constructs a new <code>ConnectionCallException</code>.
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
    *          || request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    *
    */
   ConnectionCallException(String           shortReason,
                           CallRequest      request,
                           TargetDescriptor target,
                           long             duration,
                           String           detail,
                           Throwable        cause)
   throws IllegalArgumentException {
      super(shortReason, request, target, duration, detail, cause);
   }

   /**
    * Determines whether the call has possibly (or definitely) been processed 
    * by the target service.
    *
    * @return
    *    <code>false</code>, since a connection failure indicates the call was 
    *    definitely not processed yet (so fail-over should be allowable).
    *
    * @since XINS 2.2
    */
   public final boolean isCallPossiblyProcessed() {
      return false;
   }
}
