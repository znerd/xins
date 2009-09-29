/*
 * $Id: RuntimePropsImpl.java,v 1.2 2007/03/12 10:40:52 agoubard Exp $
 */
package com.mycompany.allinone.api;


/**
 * Implementation of the <code>RuntimeProps</code> function.
 *
 * @version $Revision: 1.2 $ $Date: 2007/03/12 10:40:52 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public class RuntimePropsImpl extends RuntimeProps {

   /**
    * Constructs a new <code>RuntimePropsImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public RuntimePropsImpl(APIImpl api) {
      super(api);
   }

   public final Result call(Request request) throws Throwable {
      RuntimeProperties props = (RuntimeProperties) getAPI().getProperties();
      SuccessfulResult result = new SuccessfulResult();
      result.setTaxes(props.getAllinoneRate() * request.getPrice());
      if (props.getCurrency() != null) {
         result.setCurrency(props.getCurrency());
      }
      return result;
   }
}
