/*
 * $Id: FunctionSpec.java,v 1.24 2007/09/18 11:20:47 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of a function.
 *
 * @version $Revision: 1.24 $ $Date: 2007/09/18 11:20:47 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public final class FunctionSpec {

   /**
    * Name of the function, cannot be <code>null</code>.
    */
   private final String _functionName;

   /**
    * Description of the function, cannot be <code>null</code>.
    */
   private String _description;

   /**
    * The input parameters of the function.
    */
   private Map<String,ParameterSpec> _inputParameters = new LinkedHashMap<String,ParameterSpec>();

   /**
    * The input param combos of the function.
    */
   private List<ParamComboSpec> _inputParamCombos = new ArrayList<ParamComboSpec>();

   /**
    * The input data section elements of the function.
    */
   private Map<String,DataSectionElementSpec> _inputDataSectionElements = new LinkedHashMap<String,DataSectionElementSpec>();

   /**
    * The defined error code that the function can return.
    */
   private Map<String,ErrorCodeSpec> _errorCodes = new LinkedHashMap<String,ErrorCodeSpec>();

   /**
    * The output parameters of the function.
    */
   private Map<String,ParameterSpec> _outputParameters = new LinkedHashMap<String,ParameterSpec>();

   /**
    * The output param combos of the function.
    */
   private List<ParamComboSpec> _outputParamCombos = new ArrayList<ParamComboSpec>();

   /**
    * The output data section elements of the function.
    */
   private Map<String,DataSectionElementSpec> _outputDataSectionElements = new LinkedHashMap<String,DataSectionElementSpec>();

   /**
    * Creates a new <code>Function</code> by parsing the function specification file.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the defined type class, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the base URL path where are located the specifications, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null || reference == null || baseURL == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect or cannot be found.
    */
   FunctionSpec(String functionName, Class  reference, String baseURL)
   throws IllegalArgumentException, InvalidSpecificationException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName, "reference", reference, "baseURL", baseURL);

      // Store the function name
      _functionName = functionName;

      // Attempt to parse the function .fnc file
      try {
         Reader reader = APISpec.getReader(baseURL, functionName + ".fnc");
         parseFunction(reader, reference, baseURL);
      } catch (IOException ioe) {
         throw new InvalidSpecificationException("[Function: " + functionName + "] Cannot read function.", ioe);
      }
   }

   /**
    * Gets the name of the function.
    *
    * @return
    *    the name of the function, never <code>null</code>.
    */
   public String getName() {
      return _functionName;
   }

   /**
    * Gets the description of the function.
    *
    * @return
    *    the description of the function, never <code>null</code>.
    */
   public String getDescription() {
      return _description;
   }

   /**
    * Gets the input parameter for the specified name.
    *
    * @param parameterName
    *    the name of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not contain any input parameter with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null</code>.
    */
   public ParameterSpec getInputParameter(String parameterName)
   throws EntityNotFoundException, IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("parameterName", parameterName);

      // Find the (required) input parameter
      ParameterSpec parameter = (ParameterSpec) _inputParameters.get(parameterName);
      if (parameter == null) {
         throw new EntityNotFoundException("Input parameter \"" + parameterName + "\" not found.");
      }

      return parameter;
   }

   /**
    * Gets the input parameter specifications defined in the function.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    the input parameters, never <code>null</code>.
    */
   public Map<String,ParameterSpec> getInputParameters() {
      return Collections.unmodifiableMap(_inputParameters);
   }

   /**
    * Gets the output parameter of the specified name.
    *
    * @param parameterName
    *    the name of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not contain any output parameter with the specified name.
    */
   public ParameterSpec getOutputParameter(String parameterName)
   throws IllegalArgumentException, EntityNotFoundException {

      // Check preconditions
      MandatoryArgumentChecker.check("parameterName", parameterName);

      // Find the (required) parameter
      ParameterSpec parameter = (ParameterSpec) _outputParameters.get(parameterName);
      if (parameter == null) {
         throw new EntityNotFoundException("Output parameter \"" + parameterName + "\" not found.");
      }

      return parameter;
   }

   /**
    * Gets the output parameter specifications defined in the function.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    the output parameters, never <code>null</code>.
    */
   public Map<String,ParameterSpec> getOutputParameters() {
      return Collections.unmodifiableMap(_outputParameters);
   }

   /**
    * Gets the error code specification for the specified error code.
    *
    * @param errorCodeName
    *    the name of the error code, cannot be <code>null</code>.
    *
    * @return
    *    the error code specifications, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>errorCodeName == null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not define any error code with the specified name.
    */
   public ErrorCodeSpec getErrorCode(String errorCodeName)
   throws IllegalArgumentException, EntityNotFoundException {

      // Check preconditions
      MandatoryArgumentChecker.check("errorCodeName", errorCodeName);

      // Find the (required) error code
      ErrorCodeSpec errorCode = _errorCodes.get(errorCodeName);
      if (errorCode == null) {
         throw new EntityNotFoundException("Error code \"" + errorCodeName + "\" not found.");
      }

      return errorCode;
   }

   /**
    * Gets the error code specifications defined in the function.
    * The standard error codes are not included.
    * The key is the name of the error code, the value is the {@link ErrorCodeSpec} object.
    *
    * @return
    *    the error code specifications, never <code>null</code>.
    */
   public Map<String,ErrorCodeSpec> getErrorCodes() {
      return Collections.unmodifiableMap(_errorCodes);
   }

   /**
    * Gets the specification of the element of the input data section with the
    * specified name.
    *
    * @param elementName
    *    the name of the element, cannot be <code>null</code>.
    *
    * @return
    *    the specification of the input data section element, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not define any input data element with the specified name.
    */
   public DataSectionElementSpec getInputDataSectionElement(String elementName)
   throws IllegalArgumentException, EntityNotFoundException {

      // Check preconditions
      MandatoryArgumentChecker.check("elementName", elementName);

      // Find the (required) input data section element
      DataSectionElementSpec element = (DataSectionElementSpec) _inputDataSectionElements.get(elementName);
      if (element == null) {
         throw new EntityNotFoundException("Input data section element \"" + elementName + "\" not found.");
      }

      return element;
   }

   /**
    * Gets the specification of the elements of the input data section.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *    the input data section elements, never <code>null</code>.
    */
   public Map getInputDataSectionElements() {
      return _inputDataSectionElements;
   }

   /**
    * Gets the specification of the element of the output data section with the
    * specified name.
    *
    * @param elementName
    *    the name of the element, cannot be <code>null</code>.
    *
    * @return
    *   The specification of the output data section element, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not define any output data element with the specified name.
    */
   public DataSectionElementSpec getOutputDataSectionElement(String elementName)
   throws IllegalArgumentException, EntityNotFoundException {

      // Check preconditions
      MandatoryArgumentChecker.check("elementName", elementName);

      // Find the (required) output data section element
      DataSectionElementSpec element = (DataSectionElementSpec) _outputDataSectionElements.get(elementName);
      if (element == null) {
         throw new EntityNotFoundException("Output data section element \"" + elementName + "\" not found.");
      }

      return element;
   }

   /**
    * Gets the specification of the elements of the output data section.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *    the output data section elements, never <code>null</code>.
    */
   public Map<String,DataSectionElementSpec> getOutputDataSectionElements() {
      return _outputDataSectionElements;
   }

   /**
    * Gets the input param combo specifications.
    *
    * @return
    *    the list of the input param combos specification
    *    ({@link ParamComboSpec}), never <code>null</code>.
    */
   public List<ParamComboSpec> getInputParamCombos() {
      return Collections.unmodifiableList(_inputParamCombos);
   }

   /**
    * Gets the output param combo specifications.
    *
    * @return
    *    the list of the output param combos specification
    *    ({@link ParamComboSpec}), never <code>null</code>.
    */
   public List<ParamComboSpec> getOutputParamCombos() {
      return Collections.unmodifiableList(_outputParamCombos);
   }

   /**
    * Parses the function specification file.
    *
    * @param reader
    *    the reader that contains the content of the result code file, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the defined type class, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the base URL path where are located the specifications, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>reader == null || reference == null || baseURL == null</code>.
    *
    * @throws IOException
    *    if the parser cannot read the content.
    *
    * @throws InvalidSpecificationException
    *    if the result code file is incorrect.
    */
   private void parseFunction(Reader reader, Class reference, String baseURL)
   throws IllegalArgumentException, IOException, InvalidSpecificationException {

      MandatoryArgumentChecker.check("reader", reader, "reference", reference, "baseURL", baseURL);

      ElementParser parser = new ElementParser();
      Element function;
      try {
         function = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException("[Function: " + _functionName + "] Cannot parse function.", pe);
      }
      List descriptionElementList = function.getChildElements("description");
      if (descriptionElementList.isEmpty()) {
         throw new InvalidSpecificationException("[Function: " + _functionName + "] No definition specified.");
      }
      Element descriptionElement = (Element) descriptionElementList.get(0);
      _description = descriptionElement.getText();
      List input = function.getChildElements("input");
      if (input.size() > 0) {

         // Input parameters
         Element inputElement = (Element) input.get(0);
         _inputParameters = parseParameters(reference, inputElement);

         // Param combos
         _inputParamCombos = parseParamCombos(inputElement, _inputParameters);

         // Data section
         List dataSections = inputElement.getChildElements("data");
         if (dataSections.size() > 0) {
            Element dataSection = (Element) dataSections.get(0);
            _inputDataSectionElements = parseDataSectionElements(reference, dataSection, dataSection);
         }
      }

      List output = function.getChildElements("output");
      if (output.size() > 0) {
         Element outputElement = (Element) output.get(0);

         // Error codes
         List errorCodesList = outputElement.getChildElements("resultcode-ref");
         Iterator itErrorCodes = errorCodesList.iterator();
         while (itErrorCodes.hasNext()) {
            Element nextErrorCode = (Element) itErrorCodes.next();
            String errorCodeName = nextErrorCode.getAttribute("name");
            if (errorCodeName == null) {
               throw new InvalidSpecificationException("[Function: " + _functionName + "] Missing name attribute for a error code.");
            }
            if (errorCodeName.indexOf('/') != -1) {
               errorCodeName = errorCodeName.substring(errorCodeName.indexOf('/') + 1);
            }
            ErrorCodeSpec errorCodeSpec = new ErrorCodeSpec(errorCodeName, reference, baseURL);
            _errorCodes.put(errorCodeName, errorCodeSpec);
         }

         // Output parameters
         _outputParameters = parseParameters(reference, outputElement);

         // Param combos
         _outputParamCombos = parseParamCombos(outputElement, _outputParameters);

         // Data section
         List dataSections = outputElement.getChildElements("data");
         if (dataSections.size() > 0) {
            Element dataSection = (Element) dataSections.get(0);
            _outputDataSectionElements = parseDataSectionElements(reference, dataSection, dataSection);
         }
      }
   }

   /**
    * Parse an element in the data section.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param topElement
    *    the element to parse, cannot be <code>null</code>.
    *
    * @param dataSection
    *    the data section, cannot be <code>null</code>.
    *
    * @return
    *    the top-level elements of the data section,
    *    or an empty map if there is no data section.
    *
    * @throws IllegalArgumentException
    *    if <code>reference == null || topElement == null || dataSection == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static Map<String,DataSectionElementSpec> parseDataSectionElements(Class reference, Element topElement, Element dataSection)
   throws IllegalArgumentException, InvalidSpecificationException {

      // Check preconditions
      MandatoryArgumentChecker.check("reference", reference, "topElement", topElement, "dataSection", dataSection);

      Map<String,DataSectionElementSpec> dataSectionElements = new LinkedHashMap<String,DataSectionElementSpec>();

      // The <data> may have a "contains" attribute.
      String dataContainsAttr = topElement.getAttribute("contains");
      if (dataContainsAttr != null) {
         DataSectionElementSpec dataSectionElement = getDataSectionElement(reference, dataContainsAttr, dataSection);
         dataSectionElements.put(dataContainsAttr, dataSectionElement);
      }

      // Gets the sub elements of this element
      List<Element> dataSectionContains = topElement.getChildElements("contains");
      if (! dataSectionContains.isEmpty()) {
         Element containsElement = dataSectionContains.get(0);
         List<Element> contained = containsElement.getChildElements("contained");
         for (Element containedElement : contained) {
            String name = containedElement.getAttribute("element");
            DataSectionElementSpec dataSectionElement = getDataSectionElement(reference, name, dataSection);
            dataSectionElements.put(name, dataSectionElement);
         }
      }
      return dataSectionElements;
   }

   /**
    * Gets the specified element in the data section.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the element to retreive, cannot be <code>null</code>.
    *
    * @param dataSection
    *    the data section, cannot be <code>null</code>.
    *
    * @return
    *    the data section element or <code>null</code> if there is no element
    *    with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>reference == null || name == null || dataSection == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static DataSectionElementSpec getDataSectionElement(Class reference, String name, Element dataSection)
   throws IllegalArgumentException, InvalidSpecificationException {
      MandatoryArgumentChecker.check("reference", reference, "name", name, "dataSection", dataSection);
      Iterator itElements = dataSection.getChildElements("element").iterator();
      while (itElements.hasNext()) {
         Element nextElement = (Element) itElements.next();
         String nextName = nextElement.getAttribute("name");
         if (name.equals(nextName)) {

            String description = ((Element) nextElement.getChildElements("description").get(0)).getText();

            Map subElements = parseDataSectionElements(reference, nextElement, dataSection);

            boolean isPcdataEnable = false;
            List dataSectionContains = nextElement.getChildElements("contains");
            if (!dataSectionContains.isEmpty()) {
               Element containsElement = (Element) dataSectionContains.get(0);
               List pcdata = containsElement.getChildElements("pcdata");
               if (!pcdata.isEmpty()) {
                  isPcdataEnable = true;
               }
            }

            Map<String,ParameterSpec> attributes = new LinkedHashMap<String,ParameterSpec>();
            for (Element attributeElem : nextElement.getChildElements("attribute")) {
               ParameterSpec attribute = parseParameter(reference, attributeElem);
               attributes.put(attribute.getName(), attribute);
            }

            List<AttributeComboSpec> attributeCombos = parseAttributeCombos(nextElement, attributes);
            DataSectionElementSpec            result = new DataSectionElementSpec(nextName, description, isPcdataEnable, subElements, attributes, attributeCombos);

            return result;
         }
      }
      return null;
   }

   /**
    * Parses a function parameter or an attribute of a data section element.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param paramElement
    *    the element that contains the specification of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>reference == null || paramElement == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static ParameterSpec parseParameter(Class reference, Element paramElement)
   throws IllegalArgumentException, InvalidSpecificationException {
      MandatoryArgumentChecker.check("reference", reference, "paramElement", paramElement);
      String parameterName = paramElement.getAttribute("name");
      if (parameterName == null) {
         throw new InvalidSpecificationException("Missing name for a parameter.");
      }
      String parameterTypeName = paramElement.getAttribute("type");
      boolean requiredParameter = "true".equals(paramElement.getAttribute("required"));
      List descriptionElementList = paramElement.getChildElements("description");
      String parameterDefaultValue = paramElement.getAttribute("default");
      if (descriptionElementList.isEmpty()) {
         throw new InvalidSpecificationException("No definition specified for a parameter.");
      }
      String parameterDescription = ((Element) descriptionElementList.get(0)).getText();
      ParameterSpec parameter = new ParameterSpec(reference ,parameterName,
            parameterTypeName, requiredParameter, parameterDescription, parameterDefaultValue);
      return parameter;
   }

   /**
    * Parses the input or output parameters.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param topElement
    *    the input or output element, cannot be <code>null</code>.
    *
    * @return
    *    a map containing the parameter names as keys, and the
    *    <code>Parameter</code> objects as value, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>reference == null || topElement == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static Map<String,ParameterSpec> parseParameters(Class reference, Element topElement)
   throws IllegalArgumentException, InvalidSpecificationException {

      // Check preconditions
      MandatoryArgumentChecker.check("reference", reference, "topElement", topElement);

      Map<String,ParameterSpec> parameters = new LinkedHashMap<String,ParameterSpec>();
      for (Element paramElem : topElement.getChildElements("param")) {
         ParameterSpec parameter = parseParameter(reference, paramElem);
         parameters.put(parameter.getName(), parameter);
      }

      return parameters;
   }

   /**
    * Parses a param-combo element.
    *
    * @param topElement
    *    the param-combo {@link Element}, cannot be <code>null</code>.
    *
    * @param parameters
    *    the list of the (input or output) parameters, cannot be <code>null</code>.
    *
    * @return
    *    the list of the param-combo elements or an empty list if no
    *    param-combo is defined, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>topElement == null || parameters == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the format of the param-combo is incorrect.
    */
   static List<ParamComboSpec> parseParamCombos(Element topElement, Map<String,ParameterSpec> parameters)
   throws IllegalArgumentException, InvalidSpecificationException {

      // Check preconditions
      MandatoryArgumentChecker.check("topElement", topElement, "parameters", parameters);

      String             comboTag = "param-combo";
      String               refTag = "param-ref";
      List<ParamComboSpec> combos = new ArrayList<ParamComboSpec>();

      // Loop over the appropriate child elements 
      for (Element comboElem : topElement.getChildElements(comboTag)) {

         String type = comboElem.getAttribute("type");
         if (type == null) {
            throw new InvalidSpecificationException("No type defined for " + comboTag + ".");
         }

         Map<String,ParameterSpec> comboParams = new LinkedHashMap<String,ParameterSpec>();

         for (Element refElem : comboElem.getChildElements(refTag)) {

            String refName = refElem.getAttribute("name");
            if (refName == null) {
               throw new InvalidSpecificationException("Missing name in " + comboTag + ".");
            }

            ParameterSpec paramSpec = (ParameterSpec) parameters.get(refName);
            if (paramSpec == null) {
               throw new InvalidSpecificationException("Invalid name \"" + refName + "\" in " + comboTag + ".");
            }
            comboParams.put(refName, paramSpec);
         }

         ParamComboSpec paramComboSpec = new ParamComboSpec(type, comboParams);

         combos.add(paramComboSpec);
      }

      return combos;
   }

   /**
    * Parses an attribute-combo element.
    *
    * @param topElement
    *    the attribute-combo {@link Element}, cannot be <code>null</code>.
    *
    * @param attributes
    *    the list of the attributes, cannot be <code>null</code>.
    *
    * @return
    *    the list of the attribute-combo elements or an empty list if no
    *    attribute-combo is defined, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>topElement == null || attributes == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the format of the param-combo is incorrect.
    */
   static List<AttributeComboSpec> parseAttributeCombos(Element topElement, Map<String,ParameterSpec> attributes)
   throws IllegalArgumentException, InvalidSpecificationException {

      // Check preconditions
      MandatoryArgumentChecker.check("topElement", topElement, "attributes", attributes);

      String                 comboTag = "attribute-combo";
      String                   refTag = "attribute-ref";
      List<AttributeComboSpec> combos = new ArrayList<AttributeComboSpec>();

      // Loop over the appropriate child elements 
      for (Element comboElem : topElement.getChildElements(comboTag)) {

         String type = comboElem.getAttribute("type");
         if (type == null) {
            throw new InvalidSpecificationException("No type defined for " + comboTag + ".");
         }

         Map<String,ParameterSpec> comboParams = new LinkedHashMap<String,ParameterSpec>();

         for (Element refElem : comboElem.getChildElements(refTag)) {

            String refName = refElem.getAttribute("name");
            if (refName == null) {
               throw new InvalidSpecificationException("Missing name in " + comboTag + ".");
            }

            ParameterSpec paramSpec = (ParameterSpec) attributes.get(refName);
            if (paramSpec == null) {
               throw new InvalidSpecificationException("Invalid name \"" + refName + "\" in " + comboTag + ".");
            }
            comboParams.put(refName, paramSpec);
         }

         AttributeComboSpec paramComboSpec = new AttributeComboSpec(type, comboParams);

         combos.add(paramComboSpec);
      }

      return combos;
   }
}
