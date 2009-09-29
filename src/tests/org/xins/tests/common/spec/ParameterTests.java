/*
 * $Id: ParameterTests.java,v 1.23 2007/03/16 10:30:41 agoubard Exp $
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
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.types.standard.Text;

import com.mycompany.allinone.capi.CAPI;
import com.mycompany.allinone.types.Age;
import com.mycompany.allinone.types.IPAddress;
import com.mycompany.allinone.types.Salutation;
import com.mycompany.allinone.types.TextList;
import com.mycompany.allinone.types.Username;

/**
 * Parameter spec TestCase. The testcases use the <i>allinone</i> API
 * to test the API specification.
 *
 * @version $Revision: 1.23 $ $Date: 2007/03/16 10:30:41 $
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 */
public class ParameterTests extends TestCase {

   /**
    * The input parameter specification of the <i>DataSection</i> function.
    */
   private ParameterSpec _parameter;

   /**
    * The user defined input parameters specification of
    * the <i>DefinedTypes</i> function.
    */
   private Map _userDefinedParams;

   /**
    * Constructs a new <code>ParameterTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ParameterTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ParameterTests.class);
   }

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      APISpec allInOneAPI = allInOne.getAPISpecification();

      String functionName = "DataSection";
      FunctionSpec function = allInOneAPI.getFunction(functionName);
      _parameter = function.getInputParameter("inputText");
      _userDefinedParams =
         allInOneAPI.getFunction("DefinedTypes").getInputParameters();
   }

   /**
    * Tests that {@link ParameterSpec#getName() getName()} returns the correct
    * name of the parameter of a function of the API
    */
   public void testParameterGetName() {
      assertEquals("Function 'DataSection' has an incorrect parameter name: " +
         _parameter.getName(), "inputText", _parameter.getName());
   }

   /**
    * Tests that the {@link ParameterSpec#getDescription() getDescription()} returns
    * the correct description of the parameter of a function of the API.
    */
   public void testParameterGetDescription() {
      assertEquals("Function 'DataSection' has an incorrect parameter description: " +
         _parameter.getDescription(),
         "An example of input for a text.", _parameter.getDescription());
   }

   /**
    * Tests that {@link ParameterSpec#isRequired() isRequired()} returns the correct
    * flag for the parameter of a function of the API.
    */
   public void testParameterIsRequired() {
      assertFalse("Function 'DataSection' has an incorrect 'is required' value."
         , _parameter.isRequired());
   }

   /**
    * Tests that {@link ParameterSpec#getType() getType()} returns the correct type of
    * the parameter of a function of the API.
    */
   public void testParameterGetType() {
      assertTrue("Function 'DataSection' has an incorrect parameter type: " +
         _parameter.getType(), _parameter.getType() instanceof Text);
   }

   /**
    * Tests that {@link ParameterSpec#getType() getType()} returns the correct
    * user defined type of the parameter of a function of the API.
    */
   public void testParameterGetTypeUserDefined() {
      Iterator itUserDefainedParameters = _userDefinedParams.values().iterator();

      while (itUserDefainedParameters.hasNext()) {
         ParameterSpec userDefinedParameter = (ParameterSpec) itUserDefainedParameters.next();
         if ("inputIP".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for a pattern type.",
               userDefinedParameter.getDescription());

            assertEquals("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect type name: " +
               userDefinedParameter.getType().getName(),
               "IPAddress", userDefinedParameter.getType().getName());

            assertTrue("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof IPAddress);

            assertFalse("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value.",
               userDefinedParameter.isRequired());

         } else if ("inputSalutation".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for an enum type.",
               userDefinedParameter.getDescription());

            assertEquals("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect type name: " +
               userDefinedParameter.getType().getName(),
               "Salutation", userDefinedParameter.getType().getName());

            assertTrue("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Salutation);

            assertTrue("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: ",
               userDefinedParameter.isRequired());

         } else if ("inputAge".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for a int8 type with a minimum and maximum.",
               userDefinedParameter.getDescription());

            assertEquals("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect type name: " +
               userDefinedParameter.getType().getName(),
               "Age", userDefinedParameter.getType().getName());

            assertTrue("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Age);

            assertTrue("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: ",
               userDefinedParameter.isRequired());

         } else if ("inputList".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for a list.",
               userDefinedParameter.getDescription());

            assertEquals("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect type name:" +
               userDefinedParameter.getType().getName(),
               "TextList", userDefinedParameter.getType().getName());

            assertTrue("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof TextList);

            assertFalse("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: ",
               userDefinedParameter.isRequired());
         } else if ("inputShared".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputShared' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for a shared type.",
               userDefinedParameter.getDescription());

            assertEquals("User defined type 'inputShared' of the function " +
               "'DefinedTypes' has an incorrect type name:" +
               userDefinedParameter.getType().getName(),
               "Username", userDefinedParameter.getType().getName());

            assertTrue("User defined type 'inputShared' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Username);

            assertFalse("User defined type 'inputShared' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: ",
               userDefinedParameter.isRequired());
         } else {
            fail("Function 'DefinedTypes' contains a user defined type : "
               + userDefinedParameter.getName() + " which is not specified.");
         }
      }
   }
}
