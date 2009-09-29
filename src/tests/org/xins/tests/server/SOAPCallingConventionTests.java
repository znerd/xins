/*
 * $Id: SOAPCallingConventionTests.java,v 1.6 2007/10/12 14:00:56 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.xins.common.text.HexConverter;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.xins.tests.AllTests;

/**
 * Tests for calling conventions.
 *
 * @version $Revision: 1.6 $ $Date: 2007/10/12 14:00:56 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class SOAPCallingConventionTests extends TestCase {

   /**
    * Constructs a new <code>SOAPCallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public SOAPCallingConventionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(SOAPCallingConventionTests.class);
   }

   /**
    * Tests the SOAP calling convention.
    */
   public void testSOAPCallingConvention() throws Throwable {
      String randomLong = HexConverter.toHexString(CallingConventionTests.RANDOM.nextLong());
      String randomFive = randomLong.substring(0, 5);

      // Successful call
      postSOAPRequest(randomFive, true);

      // Unsuccessful call
      postSOAPRequest(randomFive, false);
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
      String destination = AllTests.url() + "allinone/?_convention=_xins-soap";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns0=\"urn:allinone\">" +
              "  <soap:Body>" +
              "    <ns0:ResultCodeRequest>" +
              "      <useDefault>false</useDefault>" +
              "      <inputText>" + randomFive + "</inputText>" +
              "    </ns0:ResultCodeRequest>" +
              "  </soap:Body>" +
              "</soap:Envelope>";
      int expectedStatus = success ? 200 : 500;
      Element result = CallingConventionTests.postXML(destination, data, expectedStatus);
      assertEquals("Envelope", result.getLocalName());
      assertEquals("Incorrect number of \"Fault\" elements.", 0, result.getChildElements("Fault").size());
      assertEquals("Incorrect number of \"Body\" elements.", 1, result.getChildElements("Body").size());
      Element bodyElem = (Element) result.getChildElements("Body").get(0);
      if (success) {
         assertEquals("Incorrect number of response elements.", 1, bodyElem.getChildElements("ResultCodeResponse").size());
         Element responseElem = (Element) bodyElem.getChildElements("ResultCodeResponse").get(0);
         assertEquals("Incorrect number of \"outputText\" elements.", 1, responseElem.getChildElements("outputText").size());
         Element outputTextElem = (Element) responseElem.getChildElements("outputText").get(0);
         assertEquals("Incorrect returned text", randomFive + " added.", outputTextElem.getText());
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
   public void testSOAPCallingConvention2() throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-soap";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns0=\"urn:allinone\">" +
              "  <soap:Body>" +
              "    <ns0:SimpleTypesRequest>" +
              "      <inputBoolean>0</inputBoolean>" +
              "      <inputByte>0</inputByte>" +
              "      <inputInt>0</inputInt>" +
              "      <inputLong>0</inputLong>" +
              "      <inputFloat>1.0</inputFloat>" +
              "      <inputText>0</inputText>" +
              "    </ns0:SimpleTypesRequest>" +
              "  </soap:Body>" +
              "</soap:Envelope>";
      Element result = CallingConventionTests.postXML(destination, data);
      assertEquals("Envelope", result.getLocalName());
      assertEquals("Incorrect number of \"Fault\" elements.", 0, result.getChildElements("Fault").size());
      assertEquals("Incorrect number of \"Body\" elements.", 1, result.getChildElements("Body").size());
      Element bodyElem = (Element) result.getChildElements("Body").get(0);
      assertEquals("Incorrect number of response elements.", 1, bodyElem.getChildElements("SimpleTypesResponse").size());
   }

   /**
    * Tests the SOAP calling convention with a data section.
    */
   public void testSOAPCallingConvention3() throws Throwable {
      String destination = AllTests.url() + "allinone/?_convention=_xins-soap";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns0=\"urn:allinone\">" +
              "  <soap:Body>" +
              "    <ns0:DataSection3Request>" +
              "      <data>" +
              "        <address company=\"McDo\" postcode=\"1234\" />" +
              "        <address company=\"Drill\" postcode=\"4567\" />" +
              "      </data>" +
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
   
   /**
    * Test concurent calls to the _xins-soap calling convention
    */
   public void testSOAPConcurentCalls() throws Throwable {
      for (int i = 0; i < 5; i++) {
         EchoThread t1 = new EchoThread("test1");
         EchoThread t2 = new EchoThread("test2");
         EchoThread t3 = new EchoThread("test3");
         EchoThread t4 = new EchoThread("test4");
         EchoThread t5 = new EchoThread("test5");
         t1.start();
         t2.start();
         t3.start();
         t4.start();
         t5.start();
         t1.join();
         t2.join();
         t3.join();
         t4.join();
         t5.join();
         assertTrue("Incorrect result '" + t1.getResult() + "' while 'test1' was sent.", t1.hasSucceeded());
         assertTrue("Incorrect result '" + t2.getResult() + "' while 'test1' was sent.", t2.hasSucceeded());
         assertTrue("Incorrect result '" + t3.getResult() + "' while 'test1' was sent.", t3.hasSucceeded());
         assertTrue("Incorrect result '" + t4.getResult() + "' while 'test1' was sent.", t4.hasSucceeded());
         assertTrue("Incorrect result '" + t5.getResult() + "' while 'test1' was sent.", t5.hasSucceeded());
      }
   }
   
   HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
   
   class EchoThread extends Thread {
     
      private String echoMessage;
      private String echoResult;
      private boolean succeeded = false;
      
      EchoThread(String message) {
         echoMessage = message;
      }
      
      public void run() {
         String destination = AllTests.url() + "allinone/?_convention=_xins-soap";
         String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                 "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns0=\"urn:allinone\">" +
                 "  <soap:Body>" +
                 "    <ns0:EchoRequest>" +
                 "      <in>" + echoMessage + "</in>" +
                 "    </ns0:EchoRequest>" +
                 "  </soap:Body>" +
                 "</soap:Envelope>";
         try {
            //Element result = CallingConventionTests.postXML(destination, data);
            Element result = postHTTPClient(destination, data);
            Element body = result.getUniqueChildElement("Body");
            Element response = body.getUniqueChildElement("EchoResponse");
            Element out = response.getUniqueChildElement("out");
            echoResult = out.getText();
            succeeded = echoMessage.equals(echoResult);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      
      String getResult() {
         return echoResult;
      }
      
      boolean hasSucceeded() {
         return succeeded;
      }
   }
   
   private Element postHTTPClient(String destination, String data) throws Exception {
      PostMethod post = new PostMethod(destination);
      StringRequestEntity request = new StringRequestEntity(data, "text/xml", "UTF-8");
      post.setRequestEntity(request);
      int code = client.executeMethod(post);
      if (code != 200) {
         System.err.println("Code: " + code);
         System.err.println("response " + post.getResponseBodyAsString());
      }
      String response = post.getResponseBodyAsString();
      ElementParser parser = new ElementParser();
      return parser.parse(response);
   }
}
