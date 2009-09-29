/*
 * $Id: FunctionRequest.java,v 1.21 2007/09/18 08:45:05 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.xml.Element;

/**
 * Function request. Consists of a function name, a set of parameters and a
 * data section. The function name is mandatory, while there may not be any
 * parameters nor data section.
 *
 * @version $Revision: 1.21 $ $Date: 2007/09/18 08:45:05 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 *
 * @see FunctionResult
 */
public class FunctionRequest {

   /**
    * The name of the function. This field is never <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters of the function. This field is never <code>null</code>
    */
   private final PropertyReader _parameters;

   /**
    * The data section of the function. If there is none, then this field is
    * <code>null</code>.
    */
   private final Element _dataElement;

   /**
    * Flag indicating whether the function should be skipped or not.
    */
   private final boolean _skipFunctionCall;

   /**
    * Creates a new <code>FunctionRequest</code> with just a function name.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @since XINS 3.0
    */
   public FunctionRequest(String functionName)
   throws IllegalArgumentException {
       this(functionName, null, null, false);
   }

   /**
    * Creates a new <code>FunctionRequest</code> with a function name and
    * optional parameters.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters of the function requested, or <code>null</code>.
    *
    * @param dataElement
    *    the data section of the input request, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @since XINS 3.0
    */
   public FunctionRequest(String         functionName,
                          PropertyReader parameters,
                          Element        dataElement)
   throws IllegalArgumentException {
       this(functionName, parameters, dataElement, false);
   }

   /**
    * Creates a new <code>FunctionRequest</code> with a function name,
    * optional parameters and an optional data element.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters of the function requested, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public FunctionRequest(String functionName, PropertyReader parameters)
   throws IllegalArgumentException {
       this(functionName, parameters, null, false);
   }

   /**
    * Creates a new <code>FunctionRequest</code>, optionally indicating that
    * the actual function invocation should be skipped.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters of the function requested, or <code>null</code>.
    *
    * @param dataElement
    *    the data section of the input request, or <code>null</code>.
    *
    * @param skipFunctionCall
    *    <code>true</code> if the function should not be executed;
    *    <code>false</code> if the function should be executed (the latter
    *    being typical)
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @since XINS 2.0
    */
   public FunctionRequest(String         functionName,
                          PropertyReader parameters,
                          Element        dataElement,
                          boolean        skipFunctionCall)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Initialize instance fields
      _functionName     = functionName;
      _parameters       = (parameters == null)
                        ? PropertyReaderUtils.EMPTY_PROPERTY_READER
                        : PropertyReaderUtils.copyUnmodifiable(parameters);
      _dataElement      = dataElement;
      _skipFunctionCall = skipFunctionCall;
   }

   /**
    * Gets the name of the function.
    *
    * @return
    *    the name of the function, never <code>null</code>.
    *
    * @since XINS 2.0
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Gets the parameters of the function. The returned
    * {@link PropertyReader} instance is unmodifiable.
    *
    * @return
    *    the parameters of the function, never <code>null</code>.
    *
    * @since XINS 2.0
    */
   public PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Gets the data section of the request.
    *
    * @return
    *    the data section, or <code>null</code> if there is none.
    *
    * @since XINS 2.0
    */
   public Element getDataElement() {
      return _dataElement;
   }

   /**
    * Gets whether the function should be executed or not.
    *
    * @return
    *    <code>true</code> if the function shouldn't be executed, <code>false</code> otherwise.
    *
    * @since XINS 2.0
    */
   public boolean shouldSkipFunctionCall() {
      return _skipFunctionCall;
   }
}
