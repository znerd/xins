/*
 * $Id: LoginOkayImpl.java,v 1.4 2007/05/08 10:41:44 agoubard Exp $
 */
package com.mycompany.portal.api;


/**
 * Implementation of the <code>LoginOkay</code> function.
 *
 * <p>Description: Log a user in.
 *
 * @version $Revision: 1.4 $ $Date: 2007/05/08 10:41:44 $
 * @author TODO
 */
public final class LoginOkayImpl extends LoginOkay {

   /**
    * Constructs a new <code>LoginOkayImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public LoginOkayImpl(APIImpl api) {
      super(api);
   }

   /**
    * Calls this function. If the function fails, it may throw any kind of
    * exception. All exceptions will be handled by the caller.
    *
    * @param request
    *    the request, never <code>null</code>.
    *
    * @return
    *    the result of the function call, should never be <code>null</code>.
    *
    * @throws Throwable
    *    if anything went wrong.
    */
   public Result call(Request request) throws Throwable {
      SuccessfulResult result = new SuccessfulResult();
      // TODO do this in the framework when the login page is successful
      if (request.getUserName().equals("superman")) {
         throw new Exception("kriptonite");
      }
      _session.setProperty(_session.getSessionId(), Boolean.TRUE);
      _session.removeProperty("password");
      return result;
   }
}
