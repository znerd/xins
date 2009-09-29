/*
 * $Id: ErrorCodeTests.java,v 1.23 2007/03/16 10:30:38 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.ErrorCodeSpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.types.standard.Int32;

import com.mycompany.allinone.capi.CAPI;

/**
 * ErrorCode spec TestCase. The testcases use the <i>allinone</i> API
 * to test the API specification.
 *
 * @version $Revision: 1.23 $ $Date: 2007/03/16 10:30:38 $
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 */
public class ErrorCodeTests extends TestCase {

   /**
    * The Error Code specification of the <i>ResultCode</i> function.
    */
   private ErrorCodeSpec _errorCode;

   /**
    * Constructs a new <code>ErrorCodeTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ErrorCodeTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ErrorCodeTests.class);
   }

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      APISpec allInOneAPI = allInOne.getAPISpecification();
      String functionName = "ResultCode";
      FunctionSpec function = allInOneAPI.getFunction(functionName);
      _errorCode = function.getErrorCode("AlreadySet");
   }

   /**
    * Tests that the {@link ErrorCodeSpec#getName() getName()} returns the correct
    * name of the error code of a function of the API.
    */
   public void testErrorCodeGetName() {
      assertEquals("Function 'ResultCode' has an incorrect error code name: " +
         _errorCode.getName(), "AlreadySet", _errorCode.getName());
   }

   /**
    * Tests that the {@link ErrorCodeSpec#getDescription() getDescription()} returns
    * the correct description of the error code of a function of the API.
    */
   public void testErrorCodeGetDescription() {
      assertEquals("Function 'ResultCode' has an incorrect error code description: "
         + _errorCode.getDescription(), "The parameter has already been given.",
         _errorCode.getDescription());
   }

   /**
    * Tests that the {@link ErrorCodeSpec#getOutputParameters() getOutputParameters()}
    * returns the correct output parameters of the error code of a function of
    * the API.
    */
   public void testErrorCodeGetOutputParameters() throws Exception {
      Map outputParams = _errorCode.getOutputParameters();
      ParameterSpec outputParam = _errorCode.getOutputParameter("count");

      assertEquals("The error code in the function 'ResultCode' has an incorrect " +
         "number of the parameters: " + outputParams.size(), 1, outputParams.size());
      assertEquals("The output parameter of the error code in the function " +
         "'ResultCode' has an incorrect name: " + outputParam.getName(),
         "count", outputParam.getName());
      assertEquals("The output parameter of the error code in the function " +
         "'ResultCode' has an incorrect description: " + outputParam.getDescription(),
         "The number of times that the parameter was already passed.",
         outputParam.getDescription());
      assertTrue("The output parameter of the error code in the function " +
         "'ResultCode' has an 'is required' value: ",
         outputParam.isRequired());
      assertTrue("The output parameter of the error code in the function " +
         "'ResultCode' has an incorrect type: ",
         outputParam.getType() instanceof Int32);
   }

   /**
    * Tests that the {@link ErrorCodeSpec#getType() getType()}
    * returns the correct type of the error code of a function of the API.
    */
   public void testErrorCodeGetType() throws Exception {
      assertTrue("The type of the error code is not functional as expected.",
         _errorCode.getType() == ErrorCodeSpec.FUNCTIONAL);
   }

   /**
    * Tests that the  {@link ErrorCodeSpec#getOutputParameter(String) getOutputParameter(String)}
    * returns the correct output parameters of the error code of a function of
    * the API when given an output parameter name.
    */
   public void testErrorCodeGetOutputParameter() {
      try {
         _errorCode.getOutputParameter("NoName");
         fail("The error code in the function 'ResultCode' contains an output parameter which is not specified.");
      } catch (EntityNotFoundException e) {
         //expecting exception
      }
      try {
         ParameterSpec outputParam = _errorCode.getOutputParameter("count");
         assertEquals("The ouput parameter of the error code in the function " +
            "'ResultCode', has an incorrect name: " + outputParam.getName(),
            "count", outputParam.getName());
      } catch (EntityNotFoundException e) {
        fail("The error code in the function 'ResultCode' does not contain an output parameter 'count' which was specified.");
      }
   }

   /**
    * Tests the {@link ErrorCode#getOutputDataSectionElements() getOutputDataSection()}
    * return the correct output datasection of the error code of a function of
    * the API.
    */
/*
   public void testErrorCodeGetOutputDataSectionElements() {
      //TODO an example has to be added in allinone, then the test shall be written
   }
*/

   /**
    * Tests the {@link ErrorCodeSpec#getOutputDataSectionElement(String) getOutputDataSection()}
    * return the correct output datasection of the error code of a function of
    * the API when specified with a name.
    */
/*
   public void testErrorCodeGetOutputDataSectionElements() {
      //TODO an example has to be added in allinone, then the test shall be written
   }
*/

}


