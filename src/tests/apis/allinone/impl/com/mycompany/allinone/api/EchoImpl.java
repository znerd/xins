/*
 * $Id: EchoImpl.java,v 1.2 2007/03/12 10:40:53 agoubard Exp $
 */
package com.mycompany.allinone.api;


/**
 * Implementation of the <code>Echo</code> function.
 *
 * @version $Revision: 1.2 $ $Date: 2007/03/12 10:40:53 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public final class EchoImpl extends Echo {

   /**
    * Constructs a new <code>EchoImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public EchoImpl(APIImpl api) {
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
      if (request.isSetIn()) {
         result.setOut(request.getIn());
      }
      return result;
   }
}
