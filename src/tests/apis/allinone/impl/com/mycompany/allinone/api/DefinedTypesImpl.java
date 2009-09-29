/*
 * $Id: DefinedTypesImpl.java,v 1.7 2007/09/18 11:21:10 agoubard Exp $
 */
package com.mycompany.allinone.api;

import org.xins.common.collections.BasicPropertyReader;

import com.mycompany.allinone.types.Salutation;
import com.mycompany.allinone.types.Salutation.Item;
import com.mycompany.allinone.types.TextList;

/**
 * Implementation of the <code>DefinedTypes</code> function.
 *
 * @version $Revision: 1.7 $ $Date: 2007/09/18 11:21:10 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class DefinedTypesImpl extends DefinedTypes  {

   /**
    * Constructs a new <code>DefinedTypesImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public DefinedTypesImpl(APIImpl api) {
      super(api);
   }

   public final Result call(Request request) throws Throwable {

      // Get the IP address
      String ip = null;
      if (request.isSetInputIP()) {
         ip = request.getInputIP();
      }

      // Get the salutation
      Salutation.Item salutation = request.getInputSalutation();
      System.out.println("Salutation value: " + salutation.getValue());

      // Get the age
      byte age = request.getInputAge();

      // Print the list passed as parameter
      if (request.isSetInputList()) {
         TextList.Value inputList = request.getInputList();
         for (int i = 0; i < inputList.getSize(); i++) {
            System.out.println("item " + i + " : " + inputList.get(i));
         }
      }

      SuccessfulResult result = new SuccessfulResult();

      // Set the output IP
      result.setOutputIP("127.0.0.1");

      // Set the output salutation
      result.setOutputSalutation(Salutation.LADY);

      // Set the output age
      result.setOutputAge((byte)35);

      // Set an output list
      TextList.Value outputList = new TextList.Value();
      outputList.add("Test1");
      outputList.add("Test2");
      result.setOutputList(outputList);

      // Set the output properties
      BasicPropertyReader prop = new BasicPropertyReader();
      prop.set("Doe", "28");
      // If the age input value is between 56 and 65, you will receive an _InvalidResponse errorcode
      prop.set("Irene", String.valueOf(age + 10));
      result.setOutputProperties(prop);

      return result;
   }
}
