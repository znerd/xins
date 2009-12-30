/*
 * $Id: ErrorCodeSpec.java,v 1.27 2007/09/18 11:20:46 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of a error code (also known as result code).
 *
 * @version $Revision: 1.27 $ $Date: 2007/09/18 11:20:46 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public final class ErrorCodeSpec {

   /**
    * Type indicating that the error code is functional.
    *
    * @since XINS 1.4.0
    */
   public static final Type FUNCTIONAL = new Type();

   /**
    * Type indicating that the error code is technical.
    *
    * @since XINS 1.4.0
    */
   public static final Type TECHNICAL = new Type();

   /**
    * Name of the function.
    */
   private final String _errorCodeName;

   /**
    * Description of the function.
    */
   private String _description;

   /**
    * The output parameters of the function.
    */
   private Map _outputParameters = new LinkedHashMap();

   /**
    * The output data section elements of the function.
    */
   private Map _outputDataSectionElements;

   /**
    * The type of the error code.
    */
   private Type _type;

   /**
    * Creates a new instance of <code>ErrorCodeSpec</code>.
    *
    * @param name
    *    the name of the error code, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the type of the parameters, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the base URL used to located the specifications, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || reference == null || baseURL == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the result code file cannot be found or is incorrect.
    */
   public ErrorCodeSpec(String name, Class reference, String baseURL)
   throws IllegalArgumentException, InvalidSpecificationException {

      MandatoryArgumentChecker.check("name", name, "reference", reference, "baseURL", baseURL);
      _errorCodeName = name;
      try {
         Reader reader = APISpec.getReader(baseURL, name + ".rcd");
         parseErrorCode(reader, reference);
      } catch (IOException ioe) {
         throw new InvalidSpecificationException("[ErrorCode: " + name + "] Cannot read error code.", ioe);
      }
   }

   /**
    * Gets the name of the error code.
    *
    * @return
    *    The name of the error code, never <code>null</code>.
    */
   public String getName() {

      return _errorCodeName;
   }

   /**
    * Gets the description of the error code.
    *
    * @return
    *    The description of the error code, never <code>null</code>.
    */
   public String getDescription() {

      return _description;
   }

   /**
    * Gets the output parameter for the specified name.
    *
    * @param parameterName
    *    the name of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the error code does not contain any output parameter with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null</code>.
    */
   public ParameterSpec getOutputParameter(String parameterName)
   throws EntityNotFoundException, IllegalArgumentException {

      MandatoryArgumentChecker.check("parameterName", parameterName);
      ParameterSpec parameter = (ParameterSpec) _outputParameters.get(parameterName);

      if (parameter == null) {
         throw new EntityNotFoundException("Output parameter \"" + parameterName
               + "\" not found in the error code \"" + _errorCodeName +"\".");
      }

      return parameter;
   }

   /**
    * Gets the output parameter specifications defined in the error code.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The output parameters specifications, never <code>null</code>.
    */
   public Map getOutputParameters() {

      return _outputParameters;
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
    * @throws EntityNotFoundException
    *    if the error code does not define any output data element with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    */
   public DataSectionElementSpec getOutputDataSectionElement(String elementName)
   throws EntityNotFoundException, IllegalArgumentException {

      MandatoryArgumentChecker.check("elementName", elementName);

      DataSectionElementSpec element = (DataSectionElementSpec) _outputDataSectionElements.get(elementName);

      if (element == null) {
         throw new EntityNotFoundException("Output data section element \"" + elementName
               + "\" not found in the error code \"" + _errorCodeName +"\".");
      }

      return element;
   }

   /**
    * Gets the specification of the elements of the output data section.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *   the specification of the output data section, never <code>null</code>.
    */
   public Map getOutputDataSectionElements() {

      return _outputDataSectionElements;
   }

   /**
    * Gets the type of the error code.
    *
    * @return
    *    the type of the error code.
    *
    * @since XINS 1.4.0
    */
   public Type getType() {
      return _type;
   }

   /**
    * Parses the result code file.
    *
    * @param reader
    *    the reader that contains the content of the result code file, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the type of the parameters, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>reader == null || reference == null</code>.
    *
    * @throws IOException
    *    if the parser cannot read the content.
    *
    * @throws InvalidSpecificationException
    *    if the result code file is incorrect.
    */
   private void parseErrorCode(Reader reader, Class reference)
   throws IllegalArgumentException, IOException, InvalidSpecificationException {

      MandatoryArgumentChecker.check("reader", reader, "reference", reference);
      ElementParser parser = new ElementParser();
      Element errorCode;
      try {
         errorCode = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException("[ErrorCode: " + _errorCodeName + "] Cannot parse error code.",  pe);
      }

      // Get the type of the error code.
      String typeAsString = errorCode.getAttribute("type");
      if (typeAsString == null || typeAsString.equals("technical")) {
         _type = TECHNICAL;
      } else if (typeAsString.equals("functional")) {
         _type = FUNCTIONAL;
      } else {
         throw new InvalidSpecificationException("[ErrorCode: " + _errorCodeName
               + "] Incorrect type specified.");
      }

      // Get the result from the parsed error code specification.
      List descriptionElementList = errorCode.getChildElements("description");
      if (descriptionElementList.isEmpty()) {
         throw new InvalidSpecificationException("[ErrorCode: " + _errorCodeName
               + "] No definition specified.");
      }
      Element descriptionElement = (Element) errorCode.getChildElements("description").get(0);
      _description = descriptionElement.getText();

      List output = errorCode.getChildElements("output");
      if (output.size() > 0) {

         // Output parameters
         Element outputElement = (Element) output.get(0);
         _outputParameters = FunctionSpec.parseParameters(reference, outputElement);

         // Data section
         List dataSections = outputElement.getChildElements("data");
         if (dataSections.size() > 0) {
            Element dataSection = (Element) dataSections.get(0);
            _outputDataSectionElements = FunctionSpec.parseDataSectionElements(reference, dataSection, dataSection);
         }
      }
   }
   /**
    * Type of an error code.
    *
    * @version $Revision: 1.27 $ $Date: 2007/09/18 11:20:46 $
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    *
    * @since XINS 1.4.0
    */
   public static class Type {

      /**
       * Creates a new type.
       */
      private Type() {
      }
   }
}
