/*
 * $Id: FunctionTests.java,v 1.27 2007/03/16 10:30:41 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParamComboSpec;

import com.mycompany.allinone.capi.CAPI;

/**
 * Function spec TestCase. The testcases use the <i>allinone</i> API
 * to test the API specification.
 *
 * @version $Revision: 1.27 $ $Date: 2007/03/16 10:30:41 $
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 */
public class FunctionTests extends TestCase {

   /**
    * The API specification of the <i>allinone</i> API.
    */
   private APISpec _allInOneAPI;

   /**
    * Constructs a new <code>FunctionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public FunctionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(FunctionTests.class);
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
    * Tests that the {@link FunctionSpec#getName() getName()} returns the correct name
    * of a function of the API.
    */
   public void testFunctionsGetName() throws Exception {

      String functionName = "DataSection";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect name: " +
         function.getName(), functionName, function.getName());
   }

   /**
    * Tests that the {@link FunctionSpec#getDescription() getDescription()} returns
    * the correct description of a function of the API.
    */
   public void testFunctionsGetDescription() throws Exception {

      String functionName = "DataSection";
      String functionDescription = "An example of a data section.";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect description: " +
         function.getDescription(),
         functionDescription, function.getDescription());
   }

   /**
    * Tests that {@link FunctionSpec#getInputParameters() getInputParameters()} returns
    * correct input parameters of a function of the API
    */
   public void testFunctionsGetInputParameters() throws Exception {

      String functionName = "SimpleOutput";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'SimpleOutput' has an incorrect number of input" +
         " parameters: " + function.getInputParameters().size(),
         0, function.getInputParameters().size());

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("inputIP");
      parameters.add("inputSalutation");
      parameters.add("inputAge");
      parameters.add("inputList");
      parameters.add("inputShared");

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'DefinedTypes' has an incorrect number of input" +
         " parameters: " + function.getInputParameters().size(),
         parameters.size(), function1.getInputParameters().size());

      Map functionParameters = function1.getInputParameters();
      Iterator itFunctionParameters = functionParameters.keySet().iterator();
      while (itFunctionParameters.hasNext()) {
         String functionParameter = (String) itFunctionParameters.next();
         assertTrue("Function 'DefinedTypes' does not contain the input parameter: " +
            functionParameter,
            parameters.contains(functionParameter));
      }
   }

   /**
    * Tests that getInputParameter(String) returns correct input parameters
    * for a function of the API when given an input parameter name.
    *
    * @see org.xins.common.spec.FunctionSpec#getInputParameter(String)
    */
   public void testFunctionsGetInputParameter() throws Exception {

      String functionName = "SimpleOutput";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      try {
         function.getInputParameter("NoName");
         fail("Function 'SimpleOutput' contains an input parameter 'NoName' " +
            "which was not specified.");
      } catch (EntityNotFoundException e) {

         // Expected exception
      }

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("inputIP");
      parameters.add("inputSalutation");
      parameters.add("inputAge");
      parameters.add("inputList");

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      String parameter = null;
      for (int i = 0; i < parameters.size(); i++) {
         try {
            parameter = (String) parameters.get(i);
            function1.getInputParameter(parameter);
            assertEquals("The input parameter of the function 'DefinedTypes' has " +
               " an incorrect name: " +
               function1.getInputParameter(parameter).getName(),
               parameter, function1.getInputParameter(parameter).getName());

         } catch (IllegalArgumentException e) {
            fail("The input parameter" + parameter + " of the function " +
               "'DefinedTypes' not found.");
         }
      }
   }

   /**
    * Tests that getOutputParameters() returns correct output parameters of a
    * function of the API.
    *
    * @see org.xins.common.spec.FunctionSpec#getOutputParameters()
    */
   public void testFunctionsGetOutputParameters() throws Exception {

      String functionName = "DataSection";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect number of output" +
         " parameters: " + function.getOutputParameters().size(),
         0, function.getOutputParameters().size());

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("outputIP");
      parameters.add("outputSalutation");
      parameters.add("outputAge");
      parameters.add("outputList");
      parameters.add("outputProperties");
      parameters.add("outputShared");

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);

      Map functionParameters = function1.getOutputParameters();
      Iterator itFunctionParameters = functionParameters.keySet().iterator();
      assertEquals("Function 'DefinedTypes' has an incorrect number of output" +
         " parameters: " + functionParameters.size(),
         parameters.size(), functionParameters.size());

      while (itFunctionParameters.hasNext()) {
         String functionParameter = (String) itFunctionParameters.next();
         assertTrue("Function 'DefinedTypes' does not contain the output parameter: " +
            functionParameter,
            parameters.contains(functionParameter));
      }

   }

   /**
    * Tests that getOutputParameter(String) returns correct output parameters
    * for a function of the API when given an output parameter name.
    *
    * @see org.xins.common.spec.FunctionSpec#getOutputParameter(String)
    */
   public void testFunctionsGetOutputParameter() throws Exception {

      String functionName = "DataSection";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      try {
         function.getOutputParameter("NoName");
         fail("Function 'DataSection' contains an output parameter 'NoName' " +
            "which was not specified.");
      } catch (EntityNotFoundException e) {
         // Expected exception
      }

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("outputIP");
      parameters.add("outputSalutation");
      parameters.add("outputAge");
      parameters.add("outputList");
      parameters.add("outputProperties");

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      String parameter = null;
      for (int i = 0; i < parameters.size(); i++) {
         try {
            parameter = (String) parameters.get(i);
            function1.getOutputParameter(parameter);
            assertEquals("The output parameter of the function 'DefinedTypes' has " +
                    " an incorrect name: " +
                    function1.getOutputParameter(parameter).getName(),
                    parameter, function1.getOutputParameter(parameter).getName());

         } catch (EntityNotFoundException e) {
            fail("The output parameter" + parameter + " of the function " +
               "'DefinedTypes' not found.");
         }
      }
   }

   /**
    * Tests that getErrorCodes() return correct errorcodes for a function
    * of the API.
    *
    * @see org.xins.common.spec.FunctionSpec#getErrorCodes()
    */
   public void testFunctionsGetErrorCodes() throws Exception {

      String functionName = "DataSection";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect number of " +
         "error codes: " + function.getErrorCodes().size(),
         0, function.getErrorCodes().size());

      String functionName1 = "ResultCode";
      List errorCodes = new ArrayList();
      errorCodes.add("AlreadySet");
      errorCodes.add("MissingInput");
      //TODO need to check the function with multiple error codes.
      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'AlreadySet' has an incorrect number of error codes: "
         + function1.getErrorCodes().size(), 2, function1.getErrorCodes().size());

      Map functionErrorCodes = function1.getErrorCodes();
      Iterator itFunctionErrorCodes = functionErrorCodes.keySet().iterator();
      while (itFunctionErrorCodes.hasNext()) {
         String errorCodeName = (String) itFunctionErrorCodes.next();
         assertTrue("The error code in function 'AlreadySet' has an incorrect name: "
            + errorCodeName,
            errorCodes.contains(errorCodeName));
      }
   }

   /**
    * Tests that getErrorCode(String) returns correct errorcode for a function
    * of the API when specified with a errorcode name.
    *
    * @see org.xins.common.spec.FunctionSpec#getErrorCodes()
    */
   public void testFunctionsGetErrorCode() throws Exception {

      FunctionSpec function = _allInOneAPI.getFunction("ResultCode");
      try {
         function.getErrorCode("AlreadySet");
      } catch (EntityNotFoundException e) {
         fail("Could not find the errorocode 'AlreadySet' in of 'ResultCode'" +
            " function of allinone API.");
      }

      try {
         function.getErrorCode("RubbishName");
         fail("Expected getErrorCode(String) to throw an EntityNotFoundException" +
         " for an errorcode which does not exist.");
      } catch (EntityNotFoundException e) {

         // Consume, as it was expected.
      }
   }

   /**
    * Tests that getInputDataSectionElements() returns the correct input data
    * section of a funtion of the API.
    *
    * @see org.xins.common.spec.FunctionSpec#getInputDataSectionElements()
    */
   public void testFunctionsGetInputDataSectionElements() throws Exception {

      String functionName = "DataSection";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect number of input " +
         "data section elements: " + function.getInputDataSectionElements().size(),
         0, function.getInputDataSectionElements().size());

      String functionName1 = "DataSection3";
      List inputDataSectionElements = new ArrayList();
      inputDataSectionElements.add("address");
      //TODO need to check the function with multiple data section elements.
      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'DataSection3' has an incorrect number of input " +
         "data section elements: " + function1.getInputDataSectionElements().size(),
         1, function1.getInputDataSectionElements().size());

      Map functionInputDataSectionElements = function1.getInputDataSectionElements();
      Iterator itElementNames = functionInputDataSectionElements.keySet().iterator();
      while (itElementNames.hasNext()) {
         String elementName = (String) itElementNames.next();
         assertTrue("The input data section element of the function 'DataSection3'" +
            " has an incorrect name: " + elementName,
            inputDataSectionElements.contains(elementName));
      }

   }

   /**
    * Tests that getOutputDataSectionElements() returns  the correct output data
    * section for a funtion of the API.
    *
    * @see org.xins.common.spec.FunctionSpec#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSectionElements() throws Exception {

      String functionName = "DefinedTypes";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DefinedTypes' has an incorrect number of " +
         "output data section elements: " +
         function.getOutputDataSectionElements().size(),
         0, function.getOutputDataSectionElements().size());

      String functionName1 = "DataSection3";
      List outputDataSectionElements = new ArrayList();
      outputDataSectionElements.add("packet");
      outputDataSectionElements.add("envelope");

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'DataSection3' has an incorrect number of output " +
         "data section elements: " +
         function1.getOutputDataSectionElements().size(),
         2, function1.getOutputDataSectionElements().size());

      Map functionOutputDataSectionElements = function1.getOutputDataSectionElements();
      Iterator itElementNames = functionOutputDataSectionElements.keySet().iterator();
      while (itElementNames.hasNext()) {
         String elementName = (String) itElementNames.next();
         assertTrue("The output data section element of the function 'DataSection3'" +
            " has an incorrect name: " + elementName,
            outputDataSectionElements.contains(elementName));
      }

   }

   /**
    * Tests that getOutputDataSectionElements() returns the correct output data
    * section for a funtion of the API. This test case tests a function which has
    * one data section element and one sub-element for output.
    *
    * @see org.xins.common.spec.FunctionSpec#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSecElementsWithOneElementAndSubElements() throws Exception {

      String functionName = "DataSection2";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection2' has an incorrect number of output " +
         "data section elements: " + function.getOutputDataSectionElements().size(),
         1, function.getOutputDataSectionElements().size());
   }



   /**
    * Tests that getInputParamCombos() returns the correct input param combo
    * of a function of the API.
    *
    * @see org.xins.common.spec.FunctionSpec#getInputParamCombos()
    */
   public void testFunctionsInputParamCombos() throws Exception {

      String functionName = "DefinedTypes";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DefinedTypes' has an incorrect number of input parameter combos: " + function.getInputParamCombos().size(), 0, function.getInputParamCombos().size());

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'ParamCombo' has an incorrect number of input parameter combos: " + function1.getInputParamCombos().size(), 3, function1.getInputParamCombos().size());

      Iterator itFunctionInputParamCombos = function1.getInputParamCombos().iterator();
      while (itFunctionInputParamCombos.hasNext()) {
         ParamComboSpec combo = (ParamComboSpec) itFunctionInputParamCombos.next();
         if (combo.isExclusiveOr()) {
            exclusiveCount++;
         } else if (combo.isInclusiveOr()) {
            inclusiveCount++;
         } else if (combo.isAllOrNone()) {
            allCount++;
         }
      }
      assertEquals("Function 'ParamCombo' has an incorrect number of exclusive input parameter combos: " + exclusiveCount, 1, exclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of inclusive input parameter combos: " + inclusiveCount, 1, inclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of all input parameter combos: "       + allCount,       1, allCount);
   }

   /**
    * Tests that getOutputParamCombos() returns the correct output param combo
    * of a function of the API.
    *
    * @see org.xins.common.spec.FunctionSpec#getOutputParamCombos()
    */
   public void testFunctionsOutputParamCombos() throws Exception {

      String functionName = "DefinedTypes";
      FunctionSpec function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DefinedTypes' has an incorrect number of output parameter combos: " + function.getOutputParamCombos().size(), 0, function.getOutputParamCombos().size());

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      FunctionSpec function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'ParamCombo' has an incorrect number of output parameter combos: " + function1.getOutputParamCombos().size(), 2, function1.getOutputParamCombos().size());

      Iterator itFunctionOutputParamCombos = function1.getOutputParamCombos().iterator();
      while (itFunctionOutputParamCombos.hasNext()) {
         ParamComboSpec combo = (ParamComboSpec) itFunctionOutputParamCombos.next();
         if (combo.isExclusiveOr()) {
            exclusiveCount++;
         } else if (combo.isInclusiveOr()) {
            inclusiveCount++;
         } else if (combo.isAllOrNone()) {
            allCount++;
         }
      }

      assertEquals("Function 'ParamCombo' has an incorrect number of exclusive output parameter combos: " + exclusiveCount, 1, exclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of inclusive output parameter combos: " + inclusiveCount, 0, inclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of all output parameter combos: " + allCount, 1, allCount);
   }
}


