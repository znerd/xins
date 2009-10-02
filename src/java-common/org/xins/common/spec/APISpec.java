/*
 * $Id: APISpec.java,v 1.20 2007/09/18 11:20:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of an API.
 * This class gets the specification of the API as defined in the api.xml file.
 *
 * @version $Revision: 1.20 $ $Date: 2007/09/18 11:20:49 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public final class APISpec {

   /**
    * Name of the API, cannot be <code>null</code>.
    */
   private String _apiName;

   /**
    * Owner of the API, can be <code>null</code>.
    */
   private String _owner;

   /**
    * Description of the API, cannot be <code>null</code>.
    */
   private String _description;

   /**
    * The functions of the API, cannot be <code>null</code>.
    */
   private Map _functions = new LinkedHashMap();

   /**
    * Creates a new instance of <code>APISpec</code>.
    *
    * @param reference
    *    the reference class used to get the type of the parameters, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the base URL path where are located the specifications, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>reference == null || baseURL == null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the result code file cannot be found or is incorrect.
    */
   public APISpec(Class reference, String baseURL)
   throws IllegalArgumentException, InvalidSpecificationException {
      MandatoryArgumentChecker.check("reference", reference, "baseURL", baseURL);
      try {
         Reader reader = getReader(baseURL, "api.xml");
         parseApi(reader, reference, baseURL);
      } catch (IOException ioe) {
         throw new InvalidSpecificationException("Cannot read API specification files.", ioe);
      }
   }

   /**
    * Gets the content of the file without the DTD declaration.
    *
    * @param baseURL
    *    the base URL used to located the specifications, cannot be <code>null</code>.
    *
    * @param fileName
    *    the name of the file that contains the specifications, cannot be <code>null</code>.
    *
    * @return
    *    the content of the file, never <code>null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specified file cannot be found.
    *
    * @throws IOException
    *    if the specification cannot be read.
    */
   static Reader getReader(String baseURL, String fileName)
   throws InvalidSpecificationException, IOException {

      URL fileURL = new URL(baseURL + fileName);
      InputStream in = fileURL.openStream();
      if (in == null) {
         throw new InvalidSpecificationException("File \"" + fileName +"\" not found in the specifications.");
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      return reader;
   }

   /**
    * Gets the name of the API.
    *
    * @return
    *    the name of the API, never <code>null</code>.
    */
   public String getName() {

      return _apiName;
   }

   /**
    * Gets the owner of the API.
    *
    * @return
    *    the owner of the API or <code>null</code> if no owner is defined.
    */
   public String getOwner() {

      return _owner;
   }

   /**
    * Gets the description of the API. The description will be the text
    * specified in the <i>description</i> element of the API specification file.
    *
    * @return
    *    the description of the API, never <code>null</code>.
    */
   public String getDescription() {

      return _description;
   }

   /**
    * Gets the function specifications defined in the API.
    * The key of the returned {@link Map} is the name of the function and the
    * value is the {@link FunctionSpec} object. The values in the {@link Map}
    * are never <code>null</code>.
    *
    * @return
    *    the function specifications, never <code>null</code>.
    */
   public Map getFunctions() {

      return _functions;
   }

   /**
    * Gets the specification of the given function.
    *
    * @param functionName
    *    The name of the function, can not be <code>null</code>
    *
    * @return
    *    The function specification, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @throws EntityNotFoundException
    *    If the API does not define any function for the given name.
    */
   public FunctionSpec getFunction(String functionName)
   throws IllegalArgumentException, EntityNotFoundException {

      MandatoryArgumentChecker.check("functionName", functionName);

      FunctionSpec function = (FunctionSpec) _functions.get(functionName);

      if (function == null) {
         throw new EntityNotFoundException("Function \"" + functionName + "\" not found.");
      }

      return function;
   }

   /**
    * Parses the API specification file.
    *
    * @param reader
    *    the reader that contains the content of the API specification file, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the type of the parameters, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the base URL path where are located the specifications, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>reader == null || reference == null || baseURL == null</code>.
    *
    * @throws IOException
    *    if one of the specification files cannot be read correctly.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   private void parseApi(Reader reader, Class reference, String baseURL)
   throws IllegalArgumentException, IOException, InvalidSpecificationException {

      MandatoryArgumentChecker.check("reader", reader, "reference", reference, "baseURL", baseURL);
      ElementParser parser = new ElementParser();
      Element api;
      try {
         api = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException("[API] Cannot parse.", pe);
      }

      // Get the result from the parsed API specification.
      _apiName = api.getAttribute("name");
      if (_apiName == null) {
         throw new InvalidSpecificationException("[API] No name defined.");
      }
      _owner = api.getAttribute("owner");
      List descriptionElementList = api.getChildElements("description");
      if (descriptionElementList.isEmpty()) {
         throw new InvalidSpecificationException("[API] No definition specified.");
      }
      Element descriptionElement = (Element) descriptionElementList.get(0);
      _description = descriptionElement.getText();

      // Get the specification of the functions.
      Iterator functions = api.getChildElements("function").iterator();
      while (functions.hasNext()) {
         Element nextFunction = (Element) functions.next();
         String functionName = nextFunction.getAttribute("name");
         FunctionSpec function = new FunctionSpec(functionName, reference, baseURL);
         _functions.put(functionName, function);
      }
   }
}
