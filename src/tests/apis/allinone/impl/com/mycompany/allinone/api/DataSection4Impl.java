/*
 * $Id: DataSection4Impl.java,v 1.4 2007/03/12 10:40:53 agoubard Exp $
 */
package com.mycompany.allinone.api;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the <code>DataSection3</code> function.
 *
 * @version $Revision: 1.4 $ $Date: 2007/03/12 10:40:53 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public final class DataSection4Impl extends DataSection4 {

   /**
    * Constructs a new <code>DataSection3Impl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public DataSection4Impl(APIImpl api) {
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

      // List the person and address data and fails if
      // the input is not as excepted.
      List persons = request.listPerson();
      if (persons.size() == 0) {
         throw new Exception("Nobody received");
      }
      Iterator itPersons = persons.iterator();
      while (itPersons.hasNext()) {
         Request.Person nextPerson = (Request.Person) itPersons.next();
         String name = nextPerson.getName();
         if (name.length() < 1) {
            throw new Exception("Incorrect name");
         }
         Byte age = nextPerson.getAge();
         if (age != null && age.intValue() < 1) {
            throw new Exception("Incorrect age");
         }
      }
      List addresses = request.listAddress();
      if (addresses.size() == 0) {
         throw new Exception("No address received");
      }
      Request.Address address = (Request.Address) addresses.iterator().next();
      String postAddress = address.pcdata();
      if (postAddress.length() == 0) {
         throw new Exception("No address given");
      }

      SuccessfulResult result = new SuccessfulResult();
      return result;
   }
}
