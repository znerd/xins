/*
 * $Id: AttributeComboTests.java,v 1.10 2007/09/18 11:21:11 agoubard Exp $
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
import org.xins.common.spec.DataSectionElementSpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.AttributeComboSpec;

import com.mycompany.allinone.capi.CAPI;

/**
 * AttributeCombo spec TestCase. The testcases use the <i>allinone</i> API
 * to test the API specification.
 *
 * @version $Revision: 1.10 $ $Date: 2007/09/18 11:21:11 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class AttributeComboTests extends TestCase {

   /**
    * The exclusive input attribute combo specification of the
    * <i>AttributeCombo</i> function.
    */
   private AttributeComboSpec _exclusiveCombo;

   /**
    * The inclusive input attribute combo specification of the
    * <i>AttributeCombo</i> function.
    */
   private AttributeComboSpec _inclusiveCombo;

   /**
    * The all-or-none input attribute combo specification of the
    * <i>AttributeCombo</i> function.
    */
   private AttributeComboSpec _allOrNoneCombo;

   /**
    * Constructs a new <code>AttributeComboTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public AttributeComboTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(AttributeComboTests.class);
   }

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      APISpec allInOneAPI = allInOne.getAPISpecification();

      String functionName = "AttributeCombo";
      FunctionSpec function = allInOneAPI.getFunction(functionName);
      DataSectionElementSpec element = function.getInputDataSectionElement("person");
      Iterator itAttributeCombo = element.getAttributeCombos().iterator();
      while (itAttributeCombo.hasNext()) {
         AttributeComboSpec combo = (AttributeComboSpec) itAttributeCombo.next();
         if (combo.isExclusiveOr()) {
            _exclusiveCombo = combo;
         } else if (combo.isInclusiveOr()) {
            _inclusiveCombo = combo;
         } else if (combo.isAllOrNone()) {
            _allOrNoneCombo = combo;
         }
      }
   }

   /**
    * Tests that {@link AttributeComboSpec#isExclusiveOr() isExclusiveOr()} returns
    * the correct exclusive flag for a attribute combo.
    */
   public void testErrorCodeIsExclusiveOr() {
      assertTrue("Function 'AttributeCombo' has an incorrect exclusive attribute combo: ",
         _exclusiveCombo.isExclusiveOr());
      assertFalse("Function 'AttributeCombo' has an incorrect inclusive attribute combo: ",
         _inclusiveCombo.isExclusiveOr());
      assertFalse("Function 'AttributeCombo' has an incorrect all-or-none attribute combo: ",
         _allOrNoneCombo.isExclusiveOr());
   }

   /**
    * Tests that {@link AttributeComboSpec#isInclusiveOr() isInclusiveOr()} returns
    * the correct inclusive flag for a attribute combo.
    */
   public void testErrorCodeIsInclusiveOr() {
      assertTrue("Function 'AttributeCombo' has an incorrect exclusive attribute combo: ",
         _inclusiveCombo.isInclusiveOr());
      assertFalse("Function 'AttributeCombo' has an incorrect inclusive attribute combo: ",
         _exclusiveCombo.isInclusiveOr());
      assertFalse("Function 'AttributeCombo' has an incorrect all-or-none attribute combo: ",
         _allOrNoneCombo.isInclusiveOr());
   }

   /**
    * Tests that {@link AttributeComboSpec#isNotAll()} returns the correct not-all
    * flag for a attribute combo.
    */
   public void testErrorCodeNotAll() {
      assertFalse("Function 'AttributeCombo' has an incorrect exclusive attribute combo: ",
         _inclusiveCombo.isNotAll());
      assertFalse("Function 'AttributeCombo' has an incorrect inclusive attribute combo: ",
         _exclusiveCombo.isNotAll());
      assertFalse("Function 'AttributeCombo' has an incorrect all-or-none attribute combo: ",
         _allOrNoneCombo.isNotAll());
   }

   /**
    * Tests that {@link AttributeComboSpec#isAllOrNone() isAllOrNone()} returns
    * the correct all-or-none flag for a attribute combo.
    */
   public void testErrorCodeIsAllOrNode() {
      assertTrue("Function 'AttributeCombo' has an incorrect exclusive attribute combo: ",
         _allOrNoneCombo.isAllOrNone());
      assertFalse("Function 'AttributeCombo' has an incorrect inclusive attribute combo: ",
         _inclusiveCombo.isAllOrNone());
      assertFalse("Function 'AttributeCombo' has an incorrect all-or-none attribute combo: ",
         _exclusiveCombo.isAllOrNone());
   }

   /**
    * Tests that {@link AttributeComboSpec#getAttributes() getAttributes()} returns
    * the correct attributeeters for a attribute combo.
    */
   public void testErrorCodeGetAttributes() {
      assertEquals(3, _exclusiveCombo.getAttributes().size());
      Set attributeNames = _exclusiveCombo.getAttributes().keySet();

      assertTrue("The exclusive input attribute combo of the function 'AttributeCombo'" +
         " does not contain the attributeter 'birthDate'",
         attributeNames.contains("birthDate"));
      assertTrue("The exclusive input attribute combo of the function 'AttributeCombo'" +
         " does not contain the attributeter 'birthYear'",
         attributeNames.contains("birthYear"));
      assertTrue("The exclusive input attribute combo of the function 'AttributeCombo'" +
         " does not contain the attributeter 'age'",
         attributeNames.contains("age"));

      assertEquals("The inclusive input attribute combo of the function " +
         "'AttributeCombo' has an incorrect number of attributeeters.",
         2, _inclusiveCombo.getAttributes().size());
      assertEquals("The all-or-none input attribute combo of the function " +
         "'AttributeCombo' has an incorrect number of attributeeters.",
         3, _allOrNoneCombo.getAttributes().size());
   }

}


