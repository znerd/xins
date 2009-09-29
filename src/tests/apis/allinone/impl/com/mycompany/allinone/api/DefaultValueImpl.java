/*
 * $Id: DefaultValueImpl.java,v 1.3 2007/03/12 10:40:53 agoubard Exp $
 */
package com.mycompany.allinone.api;

import java.util.Iterator;


/**
 * Implementation of the <code>DefaultValue</code> function.
 *
 * <p>Description: An example for default values as input as output parameters.
 *
 * @version $Revision: 1.3 $ $Date: 2007/03/12 10:40:53 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public final class DefaultValueImpl extends DefaultValue {

   /**
    * Constructs a new <code>DefaultValueImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public DefaultValueImpl(APIImpl api) {
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

      if (!request.getInputText().equals("copyright")) {

         // copy the input value
         result.setOutputText(request.getInputText());
      }

      Iterator itPersons = request.listPerson().iterator();
      if (itPersons.hasNext()) {
         Request.Person nextPerson = (Request.Person) itPersons.next();
         result.setCopyAge(nextPerson.getAge());
      }

      OutputElement elem1 = new OutputElement();
      result.addOutputElement(elem1);
      OutputElement elem2 = new OutputElement();
      elem2.setOutputAttribute("another output");
      result.addOutputElement(elem2);

      return result;
   }
}
