/*
 * $Id: SOAPMapCallingConventionTests.java,v 1.2 2007/09/18 11:20:52 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.text.HexConverter;
import org.xins.common.xml.Element;

import org.xins.tests.AllTests;

/**
 * Tests for calling conventions.
 *
 * @version $Revision: 1.2 $ $Date: 2007/09/18 11:20:52 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class SOAPMapCallingConventionTests extends TestCase {

   /**
    * Constructs a new <code>SOAPMapCallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public SOAPMapCallingConventionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(SOAPMapCallingConventionTests.class);
   }

   /**
    * Tests the SOAP calling convention.
    */
   public void testSOAPMapCallingConvention() throws Throwable {
      String randomLong = HexConverter.toHexString(CallingConventionTests.RANDOM.nextLong());
      String randomFive = randomLong.substring(0, 5);

      // Successful call
      postSOAPRequest(randomFive, true);

      // Unsuccessful call
      //postSOAPRequest(randomFive, false);
   }

   /**
    * Posts SOAP request.
    *
    * @param randomFive
    *    A randomly generated String.
    * @param success
    *    <code>true</code> if the expected result should be successfal,
    *    <code>false</code> otherwise.
    *
    * @throws Exception
    *    If anything goes wrong.
    */
   private void postSOAPRequest(String randomFive, boolean success) throws Exception {
      String destination = AllTests.url() + "allinone/?_convention=_xins-soap-map";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">" +
              "  <soap:Body xmlns:m=\"http://www.example.org/stock\">" +
              "    <m:ResultCodeRequest>" +
              "      <m:useDefault>false</m:useDefault>" +
              "      <m:inputText>" + randomFive + "</m:inputText>" +
              "    </m:ResultCodeRequest>" +
              "  </soap:Body>" +
              "</soap:Envelope>";
      int expectedStatus = success ? 200 : 500;
      Element result = CallingConventionTests.postXML(destination, data, expectedStatus);
      assertEquals("Envelope", result.getLocalName());
      assertEquals("soap", result.getNamespacePrefix());
      assertEquals("http://schemas.xmlsoap.org/soap/envelope/", result.getNamespaceURI());
      Element.QualifiedName encodingStyle = new Element.QualifiedName("soap", "http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");
      assertEquals("http://www.w3.org/2001/12/soap-encoding", result.getAttribute(encodingStyle));
      assertEquals("Incorrect number of \"Fault\" elements.", 0, result.getChildElements("Fault").size());
      assertEquals("Incorrect number of \"Body\" elements.", 1, result.getChildElements("Body").size());
      Element bodyElem = result.getUniqueChildElement("Body");
      if (success) {
         assertEquals("Incorrect number of response elements.", 1, bodyElem.getChildElements("ResultCodeResponse").size());
         assertEquals("Incorrect namespace prefix of the response:" + bodyElem.getNamespacePrefix(), "soap", bodyElem.getNamespacePrefix());
         Element responseElem = (Element) bodyElem.getChildElements("ResultCodeResponse").get(0);
         assertEquals("Incorrect number of \"outputText\" elements.", 1, responseElem.getChildElements("outputText").size());
         Element outputTextElem = (Element) responseElem.getChildElements("outputText").get(0);
         assertEquals("Incorrect returned text", randomFive + " added.", outputTextElem.getText());
         assertNull("Incorrect namespace prefix of the outputText.", outputTextElem.getNamespacePrefix());
      } else {
         assertEquals("Incorrect number of \"Fault\" elements.", 1, bodyElem.getChildElements("Fault").size());
         Element faultElem = (Element) bodyElem.getChildElements("Fault").get(0);
         assertEquals("Incorrect number of \"faultcode\" elements.", 1, faultElem.getChildElements("faultcode").size());
         Element faultCodeElem = (Element) faultElem.getChildElements("faultcode").get(0);
         assertEquals("Incorrect faultcode text", "soap:Server", faultCodeElem.getText());
         assertEquals("Incorrect number of \"faultstring\" elements.", 1, faultElem.getChildElements("faultstring").size());
         Element faultStringElem = (Element) faultElem.getChildElements("faultstring").get(0);
         assertEquals("Incorrect faultstring text", "AlreadySet", faultStringElem.getText());
      }
   }

   /**
    * Tests the SOAP calling convention for the type convertion.
    */
   public void testSOAPMapCallingConvention2() throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-soap-map";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
              "  <SOAP-ENV:Body>" +
              "    <gs:SimpleTypes xmlns:gs=\"urn:WhatEver\">" +
              "      <inputBoolean>0</inputBoolean>" +
              "      <inputByte>0</inputByte>" +
              "      <inputInt>0</inputInt>" +
              "      <inputLong>0</inputLong>" +
              "      <inputFloat>1.0</inputFloat>" +
              "      <inputText>0</inputText>" +
              "    </gs:SimpleTypes>" +
              "  </SOAP-ENV:Body>" +
              "</SOAP-ENV:Envelope>";
      Element result = CallingConventionTests.postXML(destination, data);
      assertEquals("Envelope", result.getLocalName());
      assertEquals("SOAP-ENV", result.getNamespacePrefix());
      assertEquals("Incorrect number of \"Fault\" elements.", 0, result.getChildElements("Fault").size());
      assertEquals("Incorrect number of \"Body\" elements.", 1, result.getChildElements("Body").size());
      Element bodyElem = result.getUniqueChildElement("Body");
      Element responseElem = bodyElem.getUniqueChildElement("SimpleTypesResponse");
      assertEquals("Incorrect response namespace prefix.", "gs", responseElem.getNamespacePrefix());
   }

   /**
    * Tests the SOAP calling convention with a data section.
    */
   public void testSOAPMapCallingConvention3() throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-soap-map";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns0=\"urn:allinone\">" +
              "  <soap:Body>" +
              "    <ns0:DataSection3Request>" +
              "      <address><company>McDo</company><postcode>1234</postcode></address>" +
              "      <address><company>Drill</company><postcode>4567</postcode></address>" +
              "    </ns0:DataSection3Request>" +
              "  </soap:Body>" +
              "</soap:Envelope>";
      Element result = CallingConventionTests.postXML(destination, data);
      assertEquals("Envelope", result.getLocalName());
      assertEquals("Incorrect number of \"Fault\" elements.", 0, result.getChildElements("Fault").size());
      assertEquals("Incorrect number of \"Body\" elements.", 1, result.getChildElements("Body").size());
      Element bodyElem = (Element) result.getChildElements("Body").get(0);
      assertEquals("Incorrect number of response elements.", 1, bodyElem.getChildElements("DataSection3Response").size());
   }
}
