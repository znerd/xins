/*
 * $Id: CallSucceededEvent.java,v 1.11 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import org.xins.client.AbstractCAPI;
import org.xins.client.AbstractCAPICallRequest;
import org.xins.client.AbstractCAPICallResult;

/**
 * Event fired when the call is finished and a succeeded result is returned
 * from the call to the function.
 *
 * @version $Revision: 1.11 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
public final class CallSucceededEvent extends CallEvent {

   /**
    * The successful result returned by the function.
    */
   private AbstractCAPICallResult _result;

   /**
    * Creates a successful call event.
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
    * @param result
    *    the result of the call.
    */
   public CallSucceededEvent(AbstractCAPI capi, AbstractCAPICallRequest request,
         long duration, AbstractCAPICallResult result) {

      super(capi, request, duration);
      _result = result;
   }

   /**
    * Gets the result returned by the function. You may want then to cast the
    * {@link org.xins.client.AbstractCAPICallResult AbstractCAPICallResult}
    * to the generated result file normally returned by the CAPI call.
    *
    * @return
    *    the successful result returned by the function.
    */
   public AbstractCAPICallResult getResult() {
      return _result;
   }
}
