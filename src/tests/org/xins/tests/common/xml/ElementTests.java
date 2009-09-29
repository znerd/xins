/*
 * $Id: ElementTests.java,v 1.4 2007/09/18 11:21:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.text.ParseException;

import org.xins.common.xml.Element;

/**
 * Tests for class <code>Element</code>.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 11:21:07 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class ElementTests extends TestCase {

   /**
     * Constructs a new <code>ElementTests</code> test suite with
     * the specified name. The name will be passed to the superconstructor.
     *
     *
     * @param name
     *    the name for this test suite.
     */
   public ElementTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ElementTests.class);
   }

   /**
    * Tests the behaviour of the <code>Element.QualifiedName</code> class.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testElementCreation() throws Exception {
      Element element1 = new Element("hello");
      assertEquals("hello", element1.getLocalName());
      assertNull(element1.getNamespaceURI());
      assertEquals(0, element1.getChildElements().size());
      assertNull(element1.getText());

      Element element2 = new Element("ns0", "hello2");
      assertEquals("hello2", element2.getLocalName());
      assertEquals("ns0", element2.getNamespaceURI());
   }

   /**
    * Tests the set methods of Element.
    */
   public void testElementSet() throws Exception {
      createElement();
   }

   /**
    * Tests add child methods.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testElementAdd() throws Exception {
      Element mainElement = new Element("main");
      Element element1 = createElement();
      Element element2 = createElement();
      assertEquals(0, mainElement.getChildElements().size());
      assertEquals(0, mainElement.getChildElements("hello").size());
      try {
         Element child = mainElement.getUniqueChildElement("hello1");
         fail("getUniqueChildElement should have failed.");
      } catch (ParseException pe) {
         // normal
      }

      mainElement.addChild(element1);
      assertEquals(1, mainElement.getChildElements().size());
      assertEquals(1, mainElement.getChildElements("hello1").size());
      Element child = mainElement.getUniqueChildElement("hello1");
      mainElement.setText("content2");
      assertEquals("content2", mainElement.getText());

      mainElement.addChild(element2);
      assertEquals(2, mainElement.getChildElements().size());
      try {
         mainElement.getUniqueChildElement("hello1");
         fail("getUniqueChildElement should have failed.");
      } catch (ParseException pe) {
         // normal
      }
      assertEquals(2, mainElement.getChildElements("hello1").size());
      assertEquals("content2", mainElement.getText());
   }

   /**
    * Tests add child methods.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testElementUpdate() throws Exception {
      Element element1 = createElement();
      element1.setLocalName("hello2");
      assertEquals("hello2", element1.getLocalName());
      element1.setAttribute("attr1", "valueA");
      element1.setAttribute("ns1", "attr1", "valueB");
      assertEquals("valueA", element1.getAttribute("attr1"));
      assertEquals("valueB", element1.getAttribute("ns1", "attr1"));
      element1.setText("contentA");
      assertEquals("contentA", element1.getText());
   }

   public void testElementEquals() throws Exception {
      assertFalse(new Element("Test").equals(new Object()));
      assertFalse(new Element("Test").equals(""          ));
      assertFalse(new Element("Test").equals(null        ));

      assertEquals(new Element("Test"), new Element("Test"));

      Element elem1 = new Element("Test");
      elem1.setAttribute("a", "0");
      elem1.setAttribute("b", "1");
      elem1.setAttribute("c", "2");

      Element elem2 = new Element("Test");
      elem2.setAttribute("c", "2");
      elem2.setAttribute("b", "1");
      elem2.setAttribute("a", "0");

      assertTrue(elem1.equals(elem1));
      assertTrue(elem1.equals(elem2));
      assertTrue(elem2.equals(elem1));
      assertTrue(elem2.equals(elem2));

      elem1.addChild(new Element("Test2"));
      assertFalse(elem1.equals(elem2));
      assertFalse(elem2.equals(elem1));
      elem2.addChild(new Element("Test2"));
      assertTrue(elem1.equals(elem2));
      assertTrue(elem2.equals(elem1));

      elem1.addText("Bla");
      assertFalse(elem1.equals(elem2));
      assertFalse(elem2.equals(elem1));
      elem2.addText("Bla");
      assertTrue(elem1.equals(elem2));
      assertTrue(elem2.equals(elem1));

      elem1.addChild(new Element("Test3"));
      assertFalse(elem1.equals(elem2));
      assertFalse(elem2.equals(elem1));
      elem2.addChild(new Element("Test3"));
      assertTrue(elem1.equals(elem2));
      assertTrue(elem2.equals(elem1));

      elem2.setAttribute("a899", null);
      assertTrue(elem1.equals(elem2));
      assertTrue(elem2.equals(elem1));
   }

   /**
    * Creates and tests an element.
    */
   private Element createElement() {
      Element element1 = new Element("hello");
      element1.setAttribute("attr1", "value1");
      element1.setAttribute("ns1", "attr1", "value2");
      assertEquals("value1", element1.getAttribute("attr1"));
      assertEquals("value2", element1.getAttribute("ns1", "attr1"));
      element1.setLocalName("hello1");
      assertEquals("hello1", element1.getLocalName());
      element1.setText("content1");
      assertEquals("content1", element1.getText());
      return element1;
   }

   /**
    * Tests the behaviour of the <code>Element.QualifiedName</code> class.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testDataElementQualifiedName() throws Exception {

      Element.QualifiedName qn1, qn2, qn3;

      String uri = "SomeURI";
      String localName = "SomeName";

      try {
         new Element.QualifiedName(null, null);
         fail("Element.QualifiedName(null, null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

      try {
         new Element.QualifiedName(uri, null);
         fail("Element.QualifiedName(\"" + uri + "\", null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

      qn1 = new Element.QualifiedName(null, localName);
      assertEquals(null,      qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());

      qn2 = new Element.QualifiedName(null, localName);
      assertEquals(qn1, qn1);
      assertEquals(qn1, qn2);
      assertEquals(qn2, qn1);
      assertEquals(qn2, qn2);

      qn3 = new Element.QualifiedName("", localName);
      assertEquals(null,      qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());
      assertEquals(qn1, qn2);
      assertEquals(qn1, qn3);
      assertEquals(qn2, qn1);
      assertEquals(qn2, qn3);
      assertEquals(qn3, qn1);
      assertEquals(qn3, qn2);

      qn1 = new Element.QualifiedName(uri, localName);
      assertEquals(uri,       qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());

      qn2 = new Element.QualifiedName(uri, localName);
      assertEquals(qn1, qn2);
      assertEquals(qn2, qn1);
   }

   public void testElementText() throws Exception {
      Element element = new Element("a");
      element.setText("Hello");
      assertEquals("Hello", element.getText());

      element.addChild(new Element("b"));
      assertEquals("Hello", element.getText());
      assertEquals(1, element.getChildElements().size());

      element.addText(" there");
      assertEquals("Hello there", element.getText());
      assertEquals(1, element.getChildElements().size());

      element.setText("guys");
      assertEquals("guys", element.getText());
      assertEquals(1, element.getChildElements().size());

      element.setText(null);
      assertEquals(null, element.getText());
      assertEquals(1, element.getChildElements().size());

      try {
         element.addText(null);
      } catch (IllegalArgumentException asExpected) { }

      element.addText("");
      assertEquals(null, element.getText());
      assertEquals(1, element.getChildElements().size());
   }
}
