/*
 * $Id: CAPIRequestTests.java,v 1.12 2007/09/18 11:21:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.standard.Date;
import org.xins.common.types.standard.Timestamp;

import com.mycompany.allinone.capi.DefaultValueRequest;
import com.mycompany.allinone.capi.DefinedTypesRequest;
import com.mycompany.allinone.capi.SimpleTypesRequest;
import com.mycompany.allinone.types.Salutation;
import com.mycompany.allinone.types.TextList;

/**
 * This class tests the generated CAPI Request object.
 *
 * @version $Revision: 1.12 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class CAPIRequestTests extends TestCase {

   /**
    * Constructs a new <code>CAPIRequestTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CAPIRequestTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(CAPIRequestTests.class);
   }

   /**
    * Tests that the generated get methods return the last value set.
    */
   public void testGetMethods() {
      DefinedTypesRequest request1 = new DefinedTypesRequest();
      assertNull("Incorrect initial value for age in the CAPI request", request1.getInputAge());

      request1.setInputAge((byte)8);
      assertEquals("The age value returned by the request is not the same as the one set.",
            8, request1.getInputAge().intValue());

      request1.setInputAge(null);
      assertNull("Incorrect reseted value for age in the CAPI request", request1.getInputAge());
   }

   /**
    * Test for RFE 1359740, titled "Add get methods for CAPI Request objects".
    *
    * @since XINS 1.4.0
    */
   public void testGetCAPIMethodsSimple() throws Exception {

      SimpleTypesRequest request = new SimpleTypesRequest();
      assertNull(request.getInputByte());
      assertNull(request.getInputShort());
      assertNull(request.getInputInt());
      assertNull(request.getInputLong());
      assertNull(request.getInputFloat());
      assertNull(request.getInputDouble());
      assertNull(request.getInputText());
      assertNull(request.getInputText2());
      assertNull(request.getInputDate());
      assertNull(request.getInputTimestamp());
      assertNull(request.getInputBinary());

      byte b = (byte) 8;
      request.setInputByte(b);
      assertEquals(b, request.getInputByte().byteValue());
      request.setInputByte(null);
      assertNull(request.getInputByte());

      int i = -65;
      request.setInputInt(i);
      assertEquals(i, request.getInputInt().intValue());
      request.setInputInt(null);
      assertNull(request.getInputInt());

      long l = 88L;
      request.setInputLong(88L);
      assertEquals(l, request.getInputLong().longValue());
      request.setInputLong(null);
      assertNull(request.getInputLong());

      float f = 32.5F;
      request.setInputFloat(f);
      assertEquals(f, request.getInputFloat().floatValue(), 0.0F);
      request.setInputFloat(null);
      assertNull(request.getInputFloat());

      double d = 37.2;
      request.setInputDouble(d);
      assertEquals(d, request.getInputDouble().doubleValue(), 0.0);
      request.setInputDouble(null);
      assertNull(request.getInputDouble());

      String t = "text";
      request.setInputText(t);
      assertEquals(t, request.getInputText());
      t = "";
      request.setInputText(t);
      assertEquals(t, request.getInputText());
      request.setInputText(null);
      assertNull(request.getInputText());

      Date.Value dv = Date.fromStringForRequired("20061231");
      request.setInputDate(dv);
      assertEquals(dv, request.getInputDate());
      request.setInputDate(null);
      assertNull(request.getInputDate());

      Timestamp.Value ts = Timestamp.fromStringForOptional("20041225153255");
      request.setInputTimestamp(ts);
      assertEquals(ts, request.getInputTimestamp());
      request.setInputTimestamp(null);
      assertNull(request.getInputTimestamp());

      byte[] ba = new byte[] { 25, 88, 66 };
      request.setInputBinary(ba);
      assertEquals(ba.length, request.getInputBinary().length);
      for (int j = 0; j < ba.length; j++) {
         assertEquals(ba[j], request.getInputBinary()[j]);
      }
      request.setInputBinary(null);
      assertNull(request.getInputBinary());
   }

   public void testGetCAPIMethodsDefined() throws Exception {

      DefinedTypesRequest request = new DefinedTypesRequest();
      assertNull(request.getInputIP());
      assertNull(request.getInputSalutation());
      assertNull(request.getInputAge());
      assertNull(request.getInputList());

      String ip = "127.0.0.1";
      request.setInputIP(ip);
      assertEquals(ip, request.getInputIP());
      request.setInputIP(null);
      assertNull(request.getInputIP());

      Salutation.Item salut = Salutation.MISTER;
      request.setInputSalutation(salut);
      assertEquals(salut, request.getInputSalutation());
      request.setInputSalutation(null);
      assertNull(request.getInputSalutation());

      byte age = (byte) 28;
      request.setInputAge(age);
      assertEquals(age, request.getInputAge().byteValue());
      request.setInputAge(null);
      assertNull(request.getInputAge());

      TextList.Value textList = new TextList.Value();
      textList.add("hello");
      textList.add("world");
      request.setInputList(textList);
      assertEquals(textList.getSize(), request.getInputList().getSize());
      request.setInputList(null);
      assertNull(request.getInputList());
   }

   public void testDefaultValues() {
      DefaultValueRequest request = new DefaultValueRequest();
      assertTrue(request.getInputBoolean().booleanValue());
      assertEquals(33, request.getInputInt().intValue());
   }
}
