/*
 * $Id: ResultCodeImpl.java,v 1.7 2007/09/18 11:21:10 agoubard Exp $
 */
package com.mycompany.allinone.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the <code>ResultCode</code> function.
 *
 * @version $Revision: 1.7 $ $Date: 2007/09/18 11:21:10 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class ResultCodeImpl extends ResultCode  {

   /**
    * Constructs a new <code>ResultCodeImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ResultCodeImpl(APIImpl api) {
      super(api);
   }

   private Map paramsCount = new HashMap();

   public final Result call(Request request) throws Throwable {

      if (!request.getUseDefault() && !request.isSetInputText()) {
         MissingInputResult invalidResult = new MissingInputResult();
         MissingInputResult.InputParameter parameter = new MissingInputResult.InputParameter();
         parameter.setName("inputText");
         parameter.setDetails("If the default is not enabled, a value should be passed.");
         invalidResult.addInputParameter(parameter);
         return invalidResult;
      }

      String inputValue = null;
      if (request.isSetInputText()) {
         inputValue = request.getInputText();
      } else {
         inputValue = "XINS";
      }

      // Test in the local map
      if (paramsCount.containsKey(inputValue)) {
         int count = ((Integer) paramsCount.get(inputValue)).intValue();
         AlreadySetResult invalidResult = new AlreadySetResult();
         invalidResult.setCount(count);
         count++;
         paramsCount.put(inputValue, new Integer(count));
         return invalidResult;
      }

      // Lookup in the shared map
      if (_sharedInstance.get(inputValue) != null) {
         AlreadySetResult invalidResult = new AlreadySetResult();
         invalidResult.setCount(-1);
         return invalidResult;
      }

      paramsCount.put(inputValue, new Integer(1));

      SuccessfulResult result = new SuccessfulResult();
      result.setOutputText(inputValue + " added.");
      return result;
   }
}
