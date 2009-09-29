/*
 * $Id: CallCAPIThread.java,v 1.13 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.xins.client.AbstractCAPI;
import org.xins.client.AbstractCAPICallRequest;
import org.xins.client.AbstractCAPICallResult;
import org.xins.common.service.CallException;

/**
 * Class used to call an API in a separate thread.
 * To call the API, you will need to invoke the {@link #start()} method.
 * If you want to wait for the result at a certain point in your program,
 * invoke the {@link #join()} method.
 *
 * @version $Revision: 1.13 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
public class CallCAPIThread extends Thread {

   /**
    * The CAPI.
    */
   private AbstractCAPI _capi;

   /**
    * The request of the function.
    */
   private AbstractCAPICallRequest _request;

   /**
    * The duration of the call.
    */
   private long _duration = -1L;

   /**
    * The successful result returned by the function.
    */
   private AbstractCAPICallResult _result;

   /**
    * The exception thrown by the call.
    */

   /**
    * Calls a CAPI function on a separate thread.
    *
    * @param capi
    *    the CAPI to use to call the function.
    *
    * @param request
    *    the input parameters for this call.
    */
   public CallCAPIThread(AbstractCAPI capi, AbstractCAPICallRequest request) {
      _capi = capi;
      _request = request;
   }
   private Exception _exception;
   public void run() {
      long startTime = System.currentTimeMillis();
      try {
         // Execute the function
         String functionName = "call" + _request.functionName();
         Class[] callArgumentsClass = {_request.getClass()};
         Object[] callArguments = {_request};
         Method callMethod = _capi.getClass().getMethod(functionName, callArgumentsClass);
         _result = (AbstractCAPICallResult) callMethod.invoke(_capi, callArguments);

         // Get the result of the call and notify the listeners
         _duration = _result.duration();
      } catch (InvocationTargetException itex) {
         _exception = (Exception) itex.getTargetException();

         // Get the exception thrown by the call and notify the listeners
         if (_exception instanceof CallException) {
            _duration = ((CallException) _exception).getDuration();
         } else {
            _duration = System.currentTimeMillis() - startTime;
         }
      } catch (Exception ex) {
         _exception = ex;
         _duration = -1L;
      }
   }

   /**
    * Gets the CAPI used to call the function.
    *
    * @return
    *    the CAPI used to call the function.
    */
   public AbstractCAPI getCAPI() {
      return _capi;
   }

   /**
    * Gets the request used to the call the function.
    *
    * @return
    *    the request used to the call the function.
    */
   public AbstractCAPICallRequest getRequest() {
      return _request;
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

   /**
    * Gets the time it took to call the function.
    *
    * @return
    *    the duration of the call in milliseconds.
    */
   public long getDuration() {
      return _duration;
   }
}
