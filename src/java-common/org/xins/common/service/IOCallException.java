/*
 * $Id: IOCallException.java,v 1.21 2007/05/21 08:34:42 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.io.IOException;

/**
 * Exception that indicates that an I/O error interrupted a service call.
 *
 * @version $Revision: 1.21 $ $Date: 2007/05/21 08:34:42 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class IOCallException extends GenericCallException {

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = -1118963769763850776L;

   /**
    * Constructs a new <code>IOCallException</code>.
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
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || ioException == null
    *          || duration  &lt; 0</code>.
    */
   public IOCallException(CallRequest      request,
                          TargetDescriptor target,
                          long             duration,
                          IOException      ioException)
   throws IllegalArgumentException {
      super(getShortReason(request, target, ioException),
            request,
            target,
            duration,
            null,
            ioException);
   }

   /**
    * Checks the arguments for the constructor and then returns the short
    * reason.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @return
    *    the short reason, never <code>null</code>.
    */
   private static String getShortReason(CallRequest      request,
                                        TargetDescriptor target,
                                        IOException      ioException) {

      // Return the short reason
      return "I/O error";
   }
}
