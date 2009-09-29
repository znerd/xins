/*
 * $Id: IPAddressUtilsTests.java,v 1.12 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.net;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.net.IPAddressUtils;
import org.xins.common.text.ParseException;

/**
 * Tests for class <code>IPAddressUtils</code>.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class IPAddressUtilsTests extends TestCase {

   /**
    * Constructs a new <code>IPAddressUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public IPAddressUtilsTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(IPAddressUtilsTests.class);
   }

   public void testIpToInt() throws Throwable {
      doTestIpToInt_INVALID("1");
      doTestIpToInt_INVALID("1.2.3.");
      doTestIpToInt_INVALID("1.2.3.4.5");
      doTestIpToInt_INVALID("...");
      doTestIpToInt_INVALID("256.1.1.1");
      doTestIpToInt_INVALID("1.1.1.256");
      doTestIpToInt_INVALID("1111.1.1.1");
      doTestIpToInt_INVALID("1.1.1.1111");
      doTestIpToInt_INVALID("1.1.1.1111");
      doTestIpToInt_INVALID("01.2.3.4");
      doTestIpToInt_INVALID("001.2.3.4");
      doTestIpToInt_INVALID("1.2.3.04");
      doTestIpToInt_INVALID("1.2.3.004");

      assertEquals(0x00000000, IPAddressUtils.ipToInt("0.0.0.0"));
      assertEquals(0x00000001, IPAddressUtils.ipToInt("0.0.0.1"));
      assertEquals(0x00000002, IPAddressUtils.ipToInt("0.0.0.2"));
      assertEquals(0x00000004, IPAddressUtils.ipToInt("0.0.0.4"));
      assertEquals(0x00000008, IPAddressUtils.ipToInt("0.0.0.8"));
      assertEquals(0x0000000a, IPAddressUtils.ipToInt("0.0.0.10"));
      assertEquals(0x0000000f, IPAddressUtils.ipToInt("0.0.0.15"));
      assertEquals(0x00000010, IPAddressUtils.ipToInt("0.0.0.16"));
      assertEquals(0x00000065, IPAddressUtils.ipToInt("0.0.0.101"));
      assertEquals(0x000000ff, IPAddressUtils.ipToInt("0.0.0.255"));
      assertEquals(0x0000ff00, IPAddressUtils.ipToInt("0.0.255.0"));
      assertEquals(0x00ff0000, IPAddressUtils.ipToInt("0.255.0.0"));
      assertEquals(0xffffffff, IPAddressUtils.ipToInt("255.255.255.255"));
   }

   private void doTestIpToInt_INVALID(String ip) throws Throwable {
      try {
         IPAddressUtils.ipToInt(ip);
         fail("IPAddressUtils.ipToInt(\"" + ip + "\") should throw a ParseException.");
         return;
      } catch (ParseException exception) {
         // as expected
      }
   }
}
