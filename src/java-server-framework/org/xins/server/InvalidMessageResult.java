/*
 * $Id: InvalidMessageResult.java,v 1.5 2007/09/18 08:45:04 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.Iterator;
import java.util.List;

import org.xins.common.xml.Element;

/**
 * Result code that indicates that a request or a response parameter is either
 * missing or invalid.
 *
 * @version $Revision: 1.5 $ $Date: 2007/09/18 08:45:04 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0
 */
class InvalidMessageResult extends FunctionResult {

   /**
    * Constructs a new <code>InvalidMessageResult</code> object.
    *
    * @param errorCode
    *    the error code to return to the client, never <code>null</code>.
    */
   InvalidMessageResult(String errorCode) {
      super(errorCode);
   }

   /**
    * Adds to the response that a paramater that is missing.
    *
    * @param parameter
    *    the missing parameter.
    */
   public void addMissingParameter(String parameter) {
      Element missingParam = new Element("missing-param");
      missingParam.setAttribute("param", parameter);
      add(missingParam);
   }

   /**
    * Adds to the response a parameter that is missing in an element.
    *
    * @param parameter
    *    the missing parameter.
    *
    * @param element
    *    the element in which the parameter is missing.
    */
   public void addMissingParameter(String parameter, String element) {
      Element missingParam = new Element("missing-param");
      missingParam.setAttribute("param", parameter);
      missingParam.setAttribute("element", element);
      add(missingParam);
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @deprecated since XINS 2.0, use {@link #addInvalidValueForType(String, String, String)}.
    */
   public void addInvalidValueForType(String parameter, String type) {
      Element invalidValue = new Element("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("type", type);
      add(invalidValue);
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    *
    * @param value
    *    the value of the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    */
   public void addInvalidValueForType(String parameter, String value, String type) {
      Element invalidValue = new Element("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("value", value);
      invalidValue.setAttribute("type", type);
      add(invalidValue);
   }

   /**
    * Adds an invalid value for a specified type.
    *
    * @param parameter
    *    the parameter passed by the user.
    *
    * @param value
    *    the value of the parameter passed by the user.
    *
    * @param type
    *    the type which this parameter should be compliant with.
    *
    * @param element
    *    the element in which the parameter is missing.
    */
   public void addInvalidValueForType(String parameter, String value, String type, String element) {
      Element invalidValue = new Element("invalid-value-for-type");
      invalidValue.setAttribute("param", parameter);
      invalidValue.setAttribute("value", value);
      invalidValue.setAttribute("type", type);
      invalidValue.setAttribute("element", element);
      add(invalidValue);
   }

   /**
    * Adds an invalid combination of parameters.
    *
    * @param type
    *    the type of the combination.
    *
    * @param parameters
    *    list of the parameters in the combination passed as a list of
    *    {@link String} objects.
    */
   public void addParamCombo(String type, List parameters) {

      Element paramCombo = new Element("param-combo");
      paramCombo.setAttribute("type", type);

      // Iterate over all parameters
      Iterator itParameters = parameters.iterator();
      while(itParameters.hasNext()) {
         Element param = new Element("param");
         param.setAttribute("name", (String) itParameters.next());
         paramCombo.addChild(param);
      }

      add(paramCombo);
   }

   /**
    * Adds an invalid combination of attributes.
    *
    * @param type
    *    the type of the combination.
    *
    * @param attributes
    *    list of the attributes in the combination passed as a list of
    *    {@link String} objects.
    *
    * @param elementName
    *    the name of the element to which these attributes belong.
    *
    * @since XINS 1.4.0
    */
   public void addAttributeCombo(String type, List attributes, String elementName) {

      Element attributeCombo = new Element("attribute-combo");
      attributeCombo.setAttribute("type", type);

      // Iterate over all attributes
      Iterator itAttributes = attributes.iterator();
      while(itAttributes.hasNext()) {
         Element attribute = new Element("attribute");
         attribute.setAttribute("name", (String) itAttributes.next());
         attributeCombo.addChild(attribute);
      }

      add(attributeCombo);
   }
}
