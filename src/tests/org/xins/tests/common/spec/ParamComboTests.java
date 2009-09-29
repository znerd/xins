/*
 * $Id: ParamComboTests.java,v 1.22 2007/03/16 10:30:41 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParamComboSpec;

import com.mycompany.allinone.capi.CAPI;

/**
 * ParamCombo spec TestCase. The testcases use the <i>allinone</i> API
 * to test the API specification.
 *
 * @version $Revision: 1.22 $ $Date: 2007/03/16 10:30:41 $
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:tauseef.rehman@orange-ftgroup.com">Tauseef Rehman</a>
 */
public class ParamComboTests extends TestCase {

   /**
    * The exclusive input param combo specification of the
    * <i>ParamCombo</i> function.
    */
   private ParamComboSpec _exclusiveCombo;

   /**
    * The inclusive input param combo specification of the
    * <i>ParamCombo</i> function.
    */
   private ParamComboSpec _inclusiveCombo;

   /**
    * The all-or-none input param combo specification of the
    * <i>ParamCombo</i> function.
    */
   private ParamComboSpec _allOrNoneCombo;

   /**
    * The not-all input param combo specification of the
    * <i>ParamComboNotAll</i> function.
    */
   private ParamComboSpec _notAll;

   /**
    * Constructs a new <code>ParamComboTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ParamComboTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ParamComboTests.class);
   }

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      APISpec allInOneAPI = allInOne.getAPISpecification();

      String functionName = "ParamCombo";
      FunctionSpec function = allInOneAPI.getFunction(functionName);
      Iterator itParamCombo = function.getInputParamCombos().iterator();
      while (itParamCombo.hasNext()) {
         ParamComboSpec combo = (ParamComboSpec) itParamCombo.next();
         if (combo.isExclusiveOr()) {
            _exclusiveCombo = combo;
         } else if (combo.isInclusiveOr()) {
            _inclusiveCombo = combo;
         } else if (combo.isAllOrNone()) {
            _allOrNoneCombo = combo;
         }
      }

      _notAll = (ParamComboSpec) allInOneAPI.getFunction("ParamComboNotAll").getInputParamCombos().get(0);
   }

   /**
    * Tests that {@link ParamComboSpec#isExclusiveOr() isExclusiveOr()} returns
    * the correct exclusive flag for a param combo.
    */
   public void testErrorCodeIsExclusiveOr() {
      assertTrue("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _exclusiveCombo.isExclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _inclusiveCombo.isExclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _allOrNoneCombo.isExclusiveOr());
   }

   /**
    * Tests that {@link ParamComboSpec#isInclusiveOr() isInclusiveOr()} returns
    * the correct inclusive flag for a param combo.
    */
   public void testErrorCodeIsInclusiveOr() {
      assertTrue("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _inclusiveCombo.isInclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _exclusiveCombo.isInclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _allOrNoneCombo.isInclusiveOr());
   }

   /**
    * Tests that {@link ParamComboSpec#isNotAll()} returns the correct not-all
    * flag for a param combo.
    */
   public void testErrorCodeNotAll() {
      assertTrue("Function 'ParamComboNotAll' has an incorrect not-all param combo: ",
         _notAll.isNotAll());
      assertFalse("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _inclusiveCombo.isNotAll());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _exclusiveCombo.isNotAll());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _allOrNoneCombo.isNotAll());
   }

   /**
    * Tests that {@link ParamComboSpec#isAllOrNone() isAllOrNone()} returns
    * the correct all-or-none flag for a param combo.
    */
   public void testErrorCodeIsAllOrNode() {
      assertTrue("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _allOrNoneCombo.isAllOrNone());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _inclusiveCombo.isAllOrNone());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _exclusiveCombo.isAllOrNone());
   }

   /**
    * Tests that {@link ParamComboSpec#getParameters() getParameters()} returns
    * the correct parameters for a param combo.
    */
   public void testErrorCodeGetParameters() {
      assertEquals(3, _exclusiveCombo.getParameters().size());
      Set paramNames = _exclusiveCombo.getParameters().keySet();

      assertTrue("The exclusive input param combo of the function 'ParamCombo' does not contain the parameter 'birthDate'", paramNames.contains("birthDate"));
      assertTrue("The exclusive input param combo of the function 'ParamCombo' does not contain the paramter 'birthYear'", paramNames.contains("birthYear"));
      assertTrue("The exclusive input param combo of the function 'ParamCombo' does not contain the paramter 'age'", paramNames.contains("age"));

      assertEquals("The inclusive input param combo of the function 'ParamCombo' has an incorrect number of parameters.", 2, _inclusiveCombo.getParameters().size());
      assertEquals("The all-or-none input param combo of the function 'ParamCombo' has an incorrect number of parameters.", 3, _allOrNoneCombo.getParameters().size());
      assertEquals("The not-all input param combo of the function 'ParamComboNotAll' has an incorrect number of parameters.", 4, _notAll.getParameters().size());
   }
}
