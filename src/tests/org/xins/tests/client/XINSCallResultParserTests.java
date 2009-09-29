/*
 * $Id: XINSCallResultParserTests.java,v 1.33 2007/03/16 10:30:30 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;

import org.xins.client.XINSCallResultData;
import org.xins.client.XINSCallResultParser;

/**
 * Tests for class <code>XINSCallResultParser</code>.
 *
 * @version $Revision: 1.33 $ $Date: 2007/03/16 10:30:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class XINSCallResultParserTests extends TestCase {

   /**
    * Constructs a new <code>XINSCallResultParserTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public XINSCallResultParserTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(XINSCallResultParserTests.class);
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * parse(byte[]) behaviour, in general and specifically also with regard to
    * parsing output parameters.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult1() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;
      XINSCallResultData result;
      PropertyReader params;

      // Passing null: Should fail
      try {
         parser.parse(null);
         fail("Passing <null> to XINSCallResultParser.parse(byte[]) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }

      // Only a product element: Should fail
      xml = "<product/>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("Root element 'product' should cause XINSCallResultParser.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // Only a result element: Should succeed
      xml = "<result/>";
      parser.parse(xml.getBytes(ENCODING));

      // Empty keys, empty values, non-conflicting duplicates
      xml = "<result><param name='a'/><param name='c'>z</param><param name='c'>z</param></result>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNotNull(result);
      assertEquals(null, result.getErrorCode());
      params = result.getParameters();
      assertNotNull(params);
      assertEquals(1, params.size());
      assertEquals(null, params.get("a"));
      assertEquals(null, params.get("b"));
      assertEquals("z", params.get("c"));

      // Conflicting duplicate should fail
      xml = "<result><param name='c'>1st value</param><param name='c'>2nd value</param></result>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("Conflicting values for parameter should cause XINSCallResultParser.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // Unknown element as child of result should be ignored
      xml = "<result><extra /><param name='a'>1</param></result>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNotNull(result);
      assertEquals(null, result.getErrorCode());
      params = result.getParameters();
      assertNotNull(params);
      assertEquals(1, params.size());
      assertEquals("1", params.get("a"));

      // Unknown element, parameter before and after data section (with CDATA section)
      xml = "<result><extra /><param name='a'>1</param><data><a/></data><param name='b'><![CDATA[2]]></param></result>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNotNull(result);
      assertEquals(null, result.getErrorCode());
      params = result.getParameters();
      assertNotNull(params);
      assertEquals(2, params.size());
      assertEquals("1", params.get("a"));
      assertEquals("2", params.get("b"));

      // There should be a data section
      Element dataElement = result.getDataElement();
      assertNotNull(dataElement);

      // Root element should be <data/>
      assertEquals("data", dataElement.getLocalName());

      // There should be no attributes in the root element
      assertEquals(0, dataElement.getAttributeMap().size());

      // There should be 1 child element
      List children = dataElement.getChildElements();
      assertEquals(1, children.size());

      // Do not allow PCDATA content within 'result'
      xml = "<result>Some PCDATA content</result>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("PCDATA content within 'result' should cause XINSCallResultParser.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * parse(byte[]) behaviour with regard to parsing data sections.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult2() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";

      // The parser should not return null
      String xml = "<?xml version=\"1.0\" encoding='" + ENCODING + "' ?>" +
                   " <result><data>" +
                   "<product available='false' name=\"FOO\" />" +
                   " <product available=\"true\"  name=\"BAR\" />" +
                   "</data></result>";
      XINSCallResultData result = parser.parse(xml.getBytes(ENCODING));
      verifyCorrectResult(result);

      // Parse the same XML with spaces between elements
      String xml2 = "<?xml version=\"1.0\" encoding='" + ENCODING + "' ?>" +
                    " <result>\n\t<data>\n" +
                    "\t\t<product available='false' name=\"FOO\" />\n" +
                    "\t\t<product available=\"true\"  name=\"BAR\" />\n" +
                    "\t</data>\n</result>";
      XINSCallResultData result2 = parser.parse(xml2.getBytes(ENCODING));
      verifyCorrectResult(result2);

      // Parse the same XML with spaces between parameters
      String xml3 = "<?xml version=\"1.0\" encoding='" + ENCODING + "' ?>" +
                    " <result>\n\t\n" +
                    "   <param name=\"test1\">hello</param>\n" +
                    "\t\t<param name='test2'>world</param>\n" +
                    "\t\t<param name=\"test3\"> 1 2 \n\t3 </param>\n" +
                    "\t</result>";
      XINSCallResultData result3 = parser.parse(xml3.getBytes(ENCODING));
      PropertyReader params = result3.getParameters();
      assertTrue(params == null || params.size() == 3);
      assertEquals("hello", params.get("test1"));
      assertEquals("world", params.get("test2"));
      assertEquals(" 1 2 \n\t3 ", params.get("test3"));
   }

   /**
    * Verifies that the result is correctly formatted as expected.
    *
    * @param result
    *    the XML result as parsed by the XINSCallResultParser.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void verifyCorrectResult(XINSCallResultData result) throws Exception {
      assertNotNull(result);

      // There should be no error code
      assertNull(result.getErrorCode());

      // There should be no parameters
      PropertyReader params = result.getParameters();
      assertTrue(params == null || params.size() == 0);

      // There should be a data section
      Element dataElement = result.getDataElement();
      assertNotNull(dataElement);

      // Root element should be <data/>
      assertEquals("data", dataElement.getLocalName());

      // There should be no attributes in the root element
      assertEquals(0, dataElement.getAttributeMap().size());

      // There should be 2 child elements
      List children = dataElement.getChildElements();
      assertEquals(2, children.size());

      // Get both child elements
      Element childOne = (Element) children.get(0);
      Element childTwo = (Element) children.get(1);

      // First element should have 2 attributes
      Map childOneAttr = childOne.getAttributeMap();
      assertEquals(2, childOneAttr.size());

      // There should be an 'available' attribute and a 'name' attribute
      assertEquals("false", childOne.getAttribute("available"));
      assertEquals("FOO",   childOne.getAttribute("name"));

      // Second element should have 2 attributes
      Map childTwoAttr = childTwo.getAttributeMap();
      assertEquals(2, childTwoAttr.size());

      // There should be an 'available' attribute and a 'name' attribute
      assertEquals("true", childTwo.getAttribute("available"));
      assertEquals("BAR",   childTwo.getAttribute("name"));
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * parse(byte[]) behaviour with regard to error codes.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult3() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;
      XINSCallResultData result;
      PropertyReader params;

      // Result with error code defined in 'errorcode' attribute
      xml = "<result errorcode='SomeError'/>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNotNull(result);
      assertEquals("SomeError", result.getErrorCode());

      // Result with error code defined in 'code' attribute
      xml = "<result code='SomeError'/>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNotNull(result);
      assertEquals("SomeError", result.getErrorCode());

      // Error code both in 'error' and in 'code' attribute (non-conflicting)
      xml = "<result code='SomeError' errorcode='SomeError'/>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNotNull(result);
      assertEquals("SomeError", result.getErrorCode());

      // Error code both in 'error' and in 'code' attribute (conflicting)
      xml = "<result code='SomeError' errorcode='SomethingElse'/>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("Conflicting values for error code should cause XINSCallResultParser.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * <code>parse(byte[])</code>, with regard to having a <code>result</code>
    * element within the root element.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult4() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;

      // Unknown elements under 'result' root element should be ignored
      xml = "<result>  <result/><result /><result errorcode='none' /></result>";
      parser.parse(xml.getBytes(ENCODING));
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * <code>parse(byte[])</code>, with regard to having a <code>data</code>
    * or <code>result</code> element within the data section.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult5() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;
      XINSCallResultData result;

      xml = "<result>  <data><result /><data /></data></result>";
      result = parser.parse(xml.getBytes(ENCODING));
      Element dataElement = result.getDataElement();
      List children = dataElement.getChildElements();
      Element child = (Element) children.get(0);
      assertEquals("result", child.getLocalName());
      child = (Element) children.get(1);
      assertEquals("data", child.getLocalName());
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * <code>parse(byte[])</code>, with regard to namespace handling.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult6() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;
      XINSCallResultData result;

      // Result element with namespace should not be accepted
      xml = "<rs:result xmlns:rs='http://somenamespace/' />";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("The XML document \"" + xml + "\" should cause XINSCallResultData.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // Result element with namespace should not be accepted
      xml = "<result xmlns='http://somenamespace/' />";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("The XML document \"" + xml + "\" should cause XINSCallResultData.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // Parameters with namespace should be ignored
      xml = "<result><param xmlns='http://somenamespace/' name='a'>b</param></result>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNull(result.getParameters());

      // Parameters with namespace should be ignored
      xml = "<result><p:param xmlns:p='http://somenamespace/' name='a'>b</p:param></result>";
      result = parser.parse(xml.getBytes(ENCODING));
      assertNull(result.getParameters());
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * <code>parse(byte[])</code>, with regard to a double data section.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult7() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;

      // Both data sections empty
      xml = "<result><data/><data/></result>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("The XML document \"" + xml + "\" should cause XINSCallResultData.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // First data section is empty
      xml = "<result><data/><data><c><d/></c></data></result>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("The XML document \"" + xml + "\" should cause XINSCallResultData.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // Second data section is empty
      xml = "<result><data><a><b/></a></data><data/></result>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("The XML document \"" + xml + "\" should cause XINSCallResultData.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }

      // Neither data section is empty
      xml = "<result><data><a><b/></a></data><data><c><d/></c></data></result>";
      try {
         parser.parse(xml.getBytes(ENCODING));
         fail("The XML document \"" + xml + "\" should cause XINSCallResultData.parse(byte[]) to throw a ParseException.");
      } catch (ParseException ex) {
         // as expected
      }
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code>, method
    * <code>parse(byte[])</code>, with regard to ignorable elements.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testParseXINSCallResult8() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;

      // Ignorable element, 1 level
      xml = "<result><a/></result>";
      parser.parse(xml.getBytes(ENCODING));

      // Ignorable element, more levels
      xml = "<result><a><b><result/><data/><c/></b><data/></a><data></data><f/><g><h>PCDATA</h><i><![CDATA[j]]></i></g></result>";
      parser.parse(xml.getBytes(ENCODING));

      // TODO: Add more
   }

   /**
    * Tests the behaviour of <code>XINSCallResultParser</code> when it is used
    * for parsing multiple times.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   /*public void testParseXINSCallResult9() throws Exception {

      XINSCallResultParser parser = new XINSCallResultParser();

      // Prepare the string to parse
      final String ENCODING = "UTF-8";
      String xml;
      XINSCallResultData result;
      PropertyReader params;

      // TODO
   }*/
}
