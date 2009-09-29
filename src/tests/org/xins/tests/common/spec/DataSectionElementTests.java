/*
 * $Id: DataSectionElementTests.java,v 1.23 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.DataSectionElementSpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.types.standard.Int32;
import org.xins.common.types.standard.Int64;
import org.xins.common.types.standard.Text;

import com.mycompany.allinone.capi.CAPI;

/**
 * DataSectionElement spec TestCase. The testcases use the <i>allinone</i> API
 * to test the API specification.
 *
 * @version $Revision: 1.23 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 */
public class DataSectionElementTests extends TestCase {

   /**
    * The API specification of the <i>allinone</i> API.
    */
   private APISpec _allInOneAPI;

   /**
    * The first data section element of the DataSection function.
    */
   private DataSectionElementSpec _userElement;

   /**
    * The first data section element of the DataSection2 function.
    */
   private DataSectionElementSpec _packetElement;

   /**
    * Constructs a new <code>DataSectionElementTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DataSectionElementTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(DataSectionElementTests.class);
   }

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      _allInOneAPI = allInOne.getAPISpecification();
      _userElement = _allInOneAPI.getFunction("DataSection").getOutputDataSectionElement("user");
      _packetElement = _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElement("packet");
   }

   /**
    * Tests that {@link DataSectionElementSpec#getName() getName()} returns
    * the correct name for a data section element for a function of the API.
    */
   public void testDataSectionGetName() throws Exception {
      assertEquals("Function 'DataSection' has an incorrect data section element " +
         "name: " + _userElement.getName(), "user", _userElement.getName());
   }

   /**
    * Tests that {@link DataSectionElementSpec#getDescription() getDescription()}
    * returns the correct description for a data section element for a function of
    * the API.
    */
   public void testDataSectionGetDescription() {
      assertEquals("Function 'DataSection' has an incorrect " +
         "data section element description: " + _userElement.getDescription(),
         "A user.", _userElement.getDescription());
   }

   /**
    * Tests that {@link DataSectionElementSpec#getSubElements() getSubElements()}
    * returns the correct sub-elements of a data section for a function of the API.
    */
   public void testDataSectionGetSubElements() throws Exception {
      assertEquals("Data Element 'packet' in the function 'DataSection2' has an " +
         "incorrect number of the sub-elements: " + _packetElement.getSubElements().size(),
         1, _packetElement.getSubElements().size());
      assertEquals("Data Element 'product' in the function 'DataSection2' has an " +
         "incorrect name of the sub-element: " + _packetElement.getSubElement("product").getName(),
         "product", _packetElement.getSubElement("product").getName());
   }

   /**
    * Tests that {@link DataSectionElementSpec#getSubElement(String)}
    * returns the correct sub-elements of a data section for a function of the
    * API when specified with a name.
    */
   public void testDataSectionGetSubElement() throws Exception {
      assertEquals("Data Element 'product' in the function 'DataSection2' has an " +
         "incorrect name of the sub-element: " + _packetElement.getSubElement("product").getName(),
         "product", _packetElement.getSubElement("product").getName());

      try {
         _packetElement.getSubElement("RubbishName");
         fail("Expected getSubElementString) to throw an EntityNotFoundException" +
         " for a element which does not exist.");
      } catch (EntityNotFoundException e) {
         // Consume, as it was expected.
      }
   }

   /**
    * Tests that {@link DataSectionElementSpec#getAttributes() getAttributes()}
    * returns the correct attributes for a data section of a function of the API.
    */
   public void testDataSectionGetAttributes() throws Exception {
      assertEquals(1, _packetElement.getAttributes().size());
      ParameterSpec attribute = _packetElement.getAttribute("destination");
      assertEquals("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect name: " + attribute.getName(),
         "destination", attribute.getName());
      assertEquals("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect description: "
         + attribute.getDescription(),
         "The destination of the packet.", attribute.getDescription());
      assertTrue("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect 'is required' value: " ,
         attribute.isRequired());
      assertTrue("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect type: "  + attribute.getType(),
         attribute.getType() instanceof Text);

      assertEquals("The output data section element for the function 'DataSection2'" +
         " has an incorrect number of the sub-elements: " +
         _packetElement.getSubElements().size(), 1, _packetElement.getSubElements().size());
   }

   /**
    * Tests that {@link DataSectionElementSpec#getAttribute(String)}
    * returns the correct attributes for a data section of a function of the API
    * when specified with a name.
    */
   public void testDataSectionGetAttribute() {

      try {
         ParameterSpec attribute = _packetElement.getAttribute("destination");
         assertEquals("The attribute in the output data section element for the " +
            "function 'DataSection2' has an incorrect name: " + attribute.getName(),
            "destination", attribute.getName());
      } catch (EntityNotFoundException e) {
         fail("Could not find the attribute 'destination' in datasection" +
            " of 'DataSection2' function of allinone API.");
      }

      try {
         _packetElement.getAttribute("RubbishName");
         fail("Expected getAttribute(String) to throw an EntityNotFoundException" +
            " for a attribute which does not exist.");
      } catch (EntityNotFoundException e) {
       // Consume, as it was expected.
      }
   }

   /**
    * Tests that {@link DataSectionElementSpec#getAttributes() getAttributes()}
    * returns correct attributes for the sub-element in a data section
    * of the API.
    */
   public void testDataSectionGetAttributesSubElement() throws Exception {
      DataSectionElementSpec subElement = _packetElement.getSubElement("product");
      Map subElementAttributes = subElement.getAttributes();
      Iterator itSubElementAttributes = subElementAttributes.values().iterator();

      while (itSubElementAttributes.hasNext()) {
         ParameterSpec attribute = (ParameterSpec) itSubElementAttributes.next();
         if ("id".equals(attribute.getName())) {
            assertEquals("Attribute 'id' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "description: " + attribute.getDescription(),
               "The id of the product.", attribute.getDescription());
            assertTrue("Attribute 'id' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "'is required' value: " , attribute.isRequired());
            assertTrue("Attribute 'id' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "type: ", attribute.getType() instanceof Int64);
         } else if ("price".equals(attribute.getName())) {
            assertEquals("Attribute 'price' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "description: " + attribute.getDescription(),
               "The description of the product.", attribute.getDescription());
            assertFalse("Attribute 'price' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "'is required' value: ", attribute.isRequired());
            assertTrue("Attribute 'price' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "type: ", attribute.getType() instanceof Int32);
         } else {
            fail("The sub-element of the output data section element of the " +
               "function 'DataSection2' contains an attribute" +
               attribute.getName() + " which should not be there.");
         }
      }
   }

   /**
    * Tests that {@link DataSectionElementSpec#isPCDataAllowed() isPCDataAllowed()}
    * returns the correct PC data allowed in a data section for a function of
    * the API.
    */
   public void testDataSectionIsPCDataAllowed() {
      assertTrue("The output data section element for the function 'DataSection'" +
         " has an incorrect 'PC data allowed' value", _userElement.isPCDataAllowed());
   }
}
