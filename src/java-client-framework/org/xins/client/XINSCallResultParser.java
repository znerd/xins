/*
 * $Id: XINSCallResultParser.java,v 1.66 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * XINS call result parser. XML is parsed to produce a {@link XINSCallResult}
 * object.
 *
 * <p>The root element in the XML must be of type <code>result</code>. Inside
 * this element, <code>param</code> elements optionally define parameters and
 * an optional <code>data</code> element defines a data section.
 *
 * <p>If the result element contains an <code>errorcode</code> or a
 * <code>code</code> attribute, then the value of the attribute is interpreted
 * as the error code. If both these attributes are set and conflicting, then
 * this is considered a showstopper.
 *
 * <p>TODO: Describe rest of parse process.
 *
 * <p>Note: This parser is
 * <a href="http://www.w3.org/TR/REC-xml-names/">XML Namespaces</a>-aware.
 *
 * @version $Revision: 1.66 $ $Date: 2007/09/18 08:45:07 $
 *
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class XINSCallResultParser {

   /**
    * The parser used to parse the XML.
    */
   private final ElementParser _parser;

   /**
    * Constructs a new <code>XINSCallResultParser</code>.
    */
   public XINSCallResultParser() {
      _parser = new ElementParser();
   }

   /**
    * Parses the given XML string to create a <code>XINSCallResultData</code>
    * object.
    *
    * @param xml
    *    the XML to be parsed, not <code>null</code>.
    *
    * @return
    *    the parsed result of the call, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>xml == null</code>.
    *
    * @throws ParseException
    *    if the specified string is not valid XML or if it is not a valid XINS
    *    API function call result.
    */
   public XINSCallResultData parse(byte[] xml)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("xml", xml);

      ByteArrayInputStream stream = null;
      try {

         // Convert the byte array to an input stream
         stream = new ByteArrayInputStream(xml);

         Element resultElement = _parser.parse(stream);

         return new XINSCallResultDataImpl(resultElement);

      } catch (Throwable exception) {

         // Log: Parsing failed
         String detail = exception.getMessage();
         Log.log_2205(exception, detail);

         // Include the exception message in our error message, if any
         String message = "Unable to convert the specified string to XML.";

         if (detail != null) {
            detail = detail.trim();
            if (detail.length() > 0) {
               message = "Unable to convert the specified string to XML: " + detail;
            }
         }

         // Throw exception with message, and register cause exception
         throw new ParseException(message, exception, detail);

      // Always dispose the ByteArrayInputStream
      } finally {
         if (stream != null) {
            try {
               stream.close();
            } catch (Throwable exception) {
               Utils.logProgrammingError(exception);
            }
         }
      }
   }

   /**
    * SAX event handler that will parse the result from a call to a XINS
    * service.
    *
    * @version $Revision: 1.66 $ $Date: 2007/09/18 08:45:07 $
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   private static class XINSCallResultDataImpl implements XINSCallResultData {

      /**
       * The error code returned by the function or <code>null</code>, if no
       * error code is returned.
       *
       * <p>The value will never return an empty string, so if the result is
       * not <code>null</code>, then it is safe to assume the length of the
       * string is at least 1 character.
       */
      private String _errorCode;

      /**
       * The list of the parameters (name/value) returned by the function.
       * This field is <code>null</code> if there is no output parameters returned.
       */
      private BasicPropertyReader _parameters;

      /**
       * The data section of the result, can be <code>null</code>.
       */
      private Element _dataSection;


      /**
       * Constructs a new <code>XINSCallResultDataImpl</code> instance.
       *
       * @param resultElement
       *    the parsed result, cannot be <code>null</code>.
       *
       * @throws ParseException
       *    if the parse XML does not match the XINS protocol.
       */
      private XINSCallResultDataImpl(Element resultElement) throws ParseException {

         if (!"result".equals(resultElement.getLocalName())) {
            String detail = "Incorrect root element '" + resultElement.getLocalName() + "'. Excpected 'result'.";
            throw new ParseException(detail);
         }
         if (resultElement.getNamespaceURI() != null) {
            String detail = "No namespace is allowed for the 'result' element. The namespace used is '" +
                  resultElement.getNamespaceURI() + "'.";
            throw new ParseException(detail);
         }
         if (resultElement.getText() != null && !resultElement.getText().trim().equals("")) {
            String detail = "No PCDATA is allowed for the 'result' element. The PCDATA returned is '" +
                  resultElement.getText() + "'.";
            throw new ParseException(detail);
         }

         // Get and check the error code if any.
         _errorCode = resultElement.getAttribute("errorcode");
         String oldErrorCode = resultElement.getAttribute("code");
         if (TextUtils.isEmpty(_errorCode) && !TextUtils.isEmpty(oldErrorCode)) {
            _errorCode = oldErrorCode;
         }
         if (!TextUtils.isEmpty(_errorCode) && !TextUtils.isEmpty(oldErrorCode) && !_errorCode.equals(oldErrorCode)) {
               // NOTE: No need to log here. This will be logged already in
               //       Logdoc log message 2205.
               String detail = "Found conflicting duplicate value for the "
                             + "error code, since attribute errorcode=\"" + _errorCode
                             + "\", while attribute code=\"" + oldErrorCode + "\".";
               throw new ParseException(detail);
         }

         // Get and check the parameters, if any.
         Iterator itParamElements = resultElement.getChildElements("param").iterator();
         while (itParamElements.hasNext()) {
            Element nextParam = (Element) itParamElements.next();
            String paramName = nextParam.getAttribute("name");
            if (TextUtils.isEmpty(paramName)) {
               throw new ParseException("No parameter name specified for a parameter: " + nextParam.toString());
            }
            String paramValue = nextParam.getText();
            if (_parameters != null && _parameters.get(paramName) != null &&
                  !_parameters.get(paramName).equals(paramValue)) {
               String detail = "Duplicate output parameter '" + paramName +
                     "'with different values: '" + _parameters.get(paramName) +
                     "' and '" + paramValue + "'.";
               throw new ParseException(detail);
            }
            if (!TextUtils.isEmpty(paramValue) && nextParam.getNamespaceURI() == null) {
               if (_parameters == null) {
                  _parameters = new BasicPropertyReader();
               }
               _parameters.set(paramName, paramValue);
            }
         }

         // Get the data section, if any.
         if (resultElement.getChildElements("data").size() > 0) {
             _dataSection = resultElement.getUniqueChildElement("data");
         }
      }

      /**
       * Returns the error code. If <code>null</code> is returned the call was
       * successful and thus no error code was returned. Otherwise the call
       * was unsuccessful.
       *
       * <p>This method will never return an empty string, so if the result is
       * not <code>null</code>, then it is safe to assume the length of the
       * string is at least 1 character.
       *
       * @return
       *    the returned error code, or <code>null</code> if the call was
       *    successful.
       */
      public String getErrorCode() {

         return _errorCode;
      }

      /**
       * Get the parameters returned by the function.
       *
       * @return
       *    the parameters (name/value) or <code>null</code> if the function
       *    does not have any parameters.
       */
      public PropertyReader getParameters() {

         return _parameters;
      }

      /**
       * Get the data element returned by the function if any.
       *
       * @return
       *    the data element, or <code>null</code> if the function did not
       *    return any data element.
       */
      public Element getDataElement() {

         return _dataSection;
      }
   }
}
