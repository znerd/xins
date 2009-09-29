/*
 * $Id: ParamComboNotAllImpl.java,v 1.3 2007/03/12 10:40:52 agoubard Exp $
 */
package com.mycompany.allinone.api;

/**
 * Implementation of the <code>ParamComboNotAll</code> function.
 *
 * @version $Revision: 1.3 $ $Date: 2007/03/12 10:40:52 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public final class ParamComboNotAllImpl extends ParamComboNotAll {

   /**
    * Constructs a new <code>ParamComboNotAllImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ParamComboNotAllImpl(APIImpl api) {
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
    * @throws java.lang.Throwable
    *    if anything went wrong.
    */
   public Result call(Request request)
   throws java.lang.Throwable {
      SuccessfulResult result = new SuccessfulResult();
      return result;
   }
}
