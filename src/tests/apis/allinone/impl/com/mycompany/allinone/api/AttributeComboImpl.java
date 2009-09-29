/*
 * $Id: AttributeComboImpl.java,v 1.3 2007/03/12 10:40:53 agoubard Exp $
 */
package com.mycompany.allinone.api;

import com.mycompany.allinone.api.AttributeCombo.Request.Person;
import java.util.Calendar;
import java.util.Iterator;
import org.xins.common.types.standard.Date;

/**
 * Implementation of the <code>AttributeCombo</code> function.
 *
 * <p>Description: A function to test the attribute-combo.
 *
 * @version $Revision: 1.3 $ $Date: 2007/03/12 10:40:53 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public final class AttributeComboImpl extends AttributeCombo {

   /**
    * Constructs a new <code>AttributeComboImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public AttributeComboImpl(APIImpl api) {
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
      Calendar calendar = Calendar.getInstance();
      SuccessfulResult result = new SuccessfulResult();
      Iterator itPersons = request.listPerson().iterator();
      while (itPersons.hasNext()) {
         Person nextPerson = (Person)itPersons.next();
         Registration registration = new Registration();
         if (nextPerson.isSetAge()) {
            int age = nextPerson.getAge().intValue();
            registration.setRegistrationYear(calendar.get(Calendar.YEAR) - age + 1);
            registration.setRegistrationMonth(calendar.get(Calendar.MONTH));
         } else {
            int year;
            int month;
            int day;
            if (nextPerson.isSetBirthDate()) {
               year = nextPerson.getBirthDate().getYear();
               month = nextPerson.getBirthDate().getMonthOfYear();
               day = nextPerson.getBirthDate().getDayOfMonth();
            } else {
               year = nextPerson.getBirthYear().intValue();
               month = nextPerson.getBirthMonth().intValue();
               day = nextPerson.getBirthDay().intValue();
            }

            // Create an invalid response for year above 2006
            // This is only for demonstration purpose as no API should normally
            // return an invalid response.
            if (year <= 2006) {
               registration.setRegistrationDate(new Date.Value(year + 1, month, 1));
            }
         }
         result.addRegistration(registration);
      }
      return result;
   }
}
