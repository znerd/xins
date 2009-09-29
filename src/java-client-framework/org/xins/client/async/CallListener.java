/*
 * $Id: CallListener.java,v 1.7 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import java.util.EventListener;

/**
 * Listener notified when the call to an API is finished whether it has
 * succeeded or failed.
 *
 * @version $Revision: 1.7 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
public interface CallListener extends EventListener {

   /**
    * Invoked when a successful result has been returned by the function.
    *
    * @param event
    *    the call event that has the result of the call.
    */
   void callSucceeded(CallSucceededEvent event);

   /**
    * Invoked when the call to the function failed.
    *
    * @param event
    *    the call event that has the details of the failure.
    */
   void callFailed(CallFailedEvent event);
}
