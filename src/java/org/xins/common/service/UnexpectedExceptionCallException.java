/*
 * $Id: UnexpectedExceptionCallException.java,v 1.20 2007/05/15 12:28:56 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates an unexpected exception was caught while
 * performing a call. The cause exception is a required argument for the
 * constructor. It may be retrieved later using {@link #getCause()}.
 *
 * @version $Revision: 1.20 $ $Date: 2007/05/15 12:28:56 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class UnexpectedExceptionCallException
extends GenericCallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = -3071445845610955883L;

   /**
    * Constructs a new <code>UnexpectedExceptionCallException</code>.
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
    *    if <code>request     == null
    *          || target      == null
    *          || cause       == null
    *          || duration  &lt; 0</code>.
    */
   public UnexpectedExceptionCallException(CallRequest      request,
                                           TargetDescriptor target,
                                           long             duration,
                                           String           detail,
                                           Throwable        cause)
   throws IllegalArgumentException {
      super("Unexpected exception caught", request, target, duration, detail, cause);
      MandatoryArgumentChecker.check("cause", cause);
   }
}
