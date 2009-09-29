/*
 * $Id: APITests.java,v 1.29 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.InvalidSpecificationException;

import com.mycompany.allinone.capi.CAPI;

/**
 * API spec TestCase. The testcases use the <i>allinone</i> API to test
 * the API specification.
 *
 * @version $Revision: 1.29 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 */
public class APITests extends TestCase {

   /**
    * The API specification of the <i>allinone</i> API.
    */
   private APISpec _allInOneAPI;

   /**
    * Constructs a new <code>APITests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public APITests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(APITests.class);
   }

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      _allInOneAPI = allInOne.getAPISpecification();
   }


   /**
    * Tests that {@link APISpec#getName() getName()} returns the correct
    * name of the API.
    */
   public void testAPIGetName() {
      assertEquals("The API has an incorrect name: " + _allInOneAPI.getName(),
         "allinone", _allInOneAPI.getName());
   }

   /**
    * Tests that {@link APISpec#getOwner()} returns the correct
    * owner of the API.
    */
   public void testAPIGetOwner() {
      assertEquals("The API has an incorrect owner: " + _allInOneAPI.getOwner(),
         "johnd", _allInOneAPI.getOwner());
   }

   /**
    * Tests that {@link APISpec#getDescription() getDescription()} returns the
    * correct description of the API.
    */
   public void testAPIGetDescription() {
      assertEquals("The API has an incorrect description: "
         + _allInOneAPI.getDescription(),
         "API that uses all the features included in XINS.",
         _allInOneAPI.getDescription());
   }

   /**
    * Tests that {@link APISpec#getFunctions() getFunctions()} and the
    * {@link APISpec#getFunction(String) getFunction(String)} return the
    * correct functions of the API.
    */
   public void testAPIGetFunctions() {
      ArrayList list = new ArrayList();
      list.add("AttributeCombo");
      list.add("DataSection");
      list.add("DataSection2");
      list.add("DataSection3");
      list.add("DataSection4");
      list.add("DefaultValue");
      list.add("DefinedTypes");
      list.add("Echo");
      list.add("Logdoc");
      list.add("ParamCombo");
      list.add("ParamComboNotAll");
      list.add("ParamComboValue");
      list.add("ResultCode");
      list.add("RuntimeProps");
      list.add("SimpleOutput");
      list.add("SimpleTypes");

      Map functions = _allInOneAPI.getFunctions();

      Iterator itFunctionNames = functions.keySet().iterator();
      while (itFunctionNames.hasNext()) {
         String functionName = (String) itFunctionNames.next();
         assertTrue(list.contains(functionName));
      }

      try {
         _allInOneAPI.getFunction("RubbishName");
         fail("Expected getFunction to throw an EntityNotFoundException");
      } catch (EntityNotFoundException e) {
         // consume, this is valid
      }

      assertEquals("The API has an incorrect number of functions: " +
         _allInOneAPI.getFunctions().size(),
         _allInOneAPI.getFunctions().size(), list.size());
      int i = 0;
      try {
         for (i = 0; i < list.size(); i++) {
            String functionName = (String) list.get(i);
            assertEquals("The function in the API has an incorrect name:" +
               _allInOneAPI.getFunction(functionName).getName(),
               _allInOneAPI.getFunction(functionName).getName(), functionName);
         }
      } catch (EntityNotFoundException exc) {
         fail("Could not find the function " + list.get(i) + " in allinone API.");
      }
   }

   /**
    * Tests the backward compatibility with older version of xins.
    * The system is actualy not backward compatible so it throws an exception.
    */
   public void testCompatibility() {

      try {
         TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
         com.mycompany.myproject.capi.CAPI capi =
            new com.mycompany.myproject.capi.CAPI(target);
         APISpec myProjectAPI = capi.getAPISpecification();
         fail("Calling an older version of CAPI should throw an exception.");
      } catch (InvalidSpecificationException e) {

         // Expected exception
      } catch (Exception e) {
         fail("Unexpected exception occurs: " + e.getMessage());
      }
   }

}


