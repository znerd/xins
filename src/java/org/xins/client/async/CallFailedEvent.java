/*
 * $Id: CallFailedEvent.java,v 1.11 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import org.xins.client.AbstractCAPI;
import org.xins.client.AbstractCAPICallRequest;

/**
 * Event fired the call to the function failed.
 *
 * @version $Revision: 1.11 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
public final class CallFailedEvent extends CallEvent {

   /**
    * The exception thrown by the call.
    */
   private Exception _exception;

   /**
    * Creates a failed call event.
    *
    * @param capi
    *    the CAPI used to call the function. The CAPI is used as the event source.
    *
    * @param request
    *    the request of the call to the function.
    *
    * @param duration
    *    the duration of the call.
    *
    * @param exception
    *    the exception thrown by the CAPI call.
    */
   public CallFailedEvent(AbstractCAPI capi, AbstractCAPICallRequest request,
                          long duration, Exception exception) {
      super(capi, request, duration);
      _exception = exception;
   }

   /**
    * Gets the exception thrown by the CAPI call.
    *
    * @return
    *    the exception, most probably a sub class of the
    *    {@link org.xins.common.service.CallException CallException}.
    */
   public Exception getException() {
      return _exception;
   }
}
