/*
 * $Id: DataSection2Impl.java,v 1.4 2007/03/12 10:40:53 agoubard Exp $
 */
package com.mycompany.allinone.api;

/**
 * Implementation of the <code>DataSection2</code> function.
 *
 * @version $Revision: 1.4 $ $Date: 2007/03/12 10:40:53 $
 * @author John Doe (<a href="mailto:john.doe@mycompany.com">john.doe@mycompany.com</a>)
 */
public class DataSection2Impl extends DataSection2  {

   /**
    * Constructs a new <code>DataSection2Impl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public DataSection2Impl(APIImpl api) {
      super(api);
   }

   public final Result call(Request request) throws Throwable {
      SuccessfulResult result = new SuccessfulResult();

      // Create the products that will be added to the packets
      Product product1 = new Product();
      product1.setId(123456);
      product1.setPrice(12);
      Product product2 = new Product();
      product2.setId(321654);

      // Create the packet
      Packet packet1 = new Packet();
      packet1.setDestination("20 West Street, New York");
      packet1.addProduct(product1);
      packet1.addProduct(product2);

      Packet packet2 = new Packet();
      packet2.setDestination("55 Kennedy lane, Washinton DC");
      packet2.addProduct(product1);

      // Add the packets
      result.addPacket(packet1);
      result.addPacket(packet2);

      return result;
   }
}
