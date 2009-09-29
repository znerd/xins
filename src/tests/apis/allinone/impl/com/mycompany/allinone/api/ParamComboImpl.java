/*
 * $Id: ParamComboImpl.java,v 1.4 2007/03/12 10:40:53 agoubard Exp $
 */
package com.mycompany.allinone.api;

import java.util.Calendar;
import org.xins.common.types.standard.Date;

/**
 * Implementation of the <code>ParamCombo</code> function.
 *
 * @version $Revision: 1.4 $ $Date: 2007/03/12 10:40:53 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public class ParamComboImpl extends ParamCombo  {

   /**
    * Constructs a new <code>ParamComboImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ParamComboImpl(APIImpl api) {
      super(api);
   }

   public final Result call(Request request) throws Throwable {
      int age;
      if (request.isSetAge()) {
         age = request.getAge().intValue();

         SuccessfulResult result = new SuccessfulResult();
         Calendar calendar = Calendar.getInstance();
         result.setRegistrationYear(calendar.get(Calendar.YEAR) - age + 1);
         result.setRegistrationMonth(calendar.get(Calendar.MONTH));
         return result;
      } else {
         int year;
         int month;
         int day;
         if (request.isSetBirthDate()) {
            year = request.getBirthDate().getYear();
            month = request.getBirthDate().getMonthOfYear();
            day = request.getBirthDate().getDayOfMonth();
         } else {
            year = request.getBirthYear().intValue();
            month = request.getBirthMonth().intValue();
            day = request.getBirthDay().intValue();
         }

         // Create an invalid response
         // This is only for demonstration purpose as no API should normally
         // return an invalid response.
         if (year > 2005) {
            return new SuccessfulResult();
         }

         SuccessfulResult result = new SuccessfulResult();
         result.setRegistrationDate(new Date.Value(year + 1, month, 1));
         return result;
      }
   }
}
