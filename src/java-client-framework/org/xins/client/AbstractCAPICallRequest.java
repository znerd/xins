/*
 * $Id: AbstractCAPICallRequest.java,v 1.40 2007/08/15 13:53:56 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.xml.Element;

/**
 * Base class for generated CAPI function request classes.
 *
 * <p>This class should not be subclassed manually. It is only intended to be
 * subclassed by classes generated by XINS.
 *
 * @version $Revision: 1.40 $ $Date: 2007/08/15 13:53:56 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public abstract class AbstractCAPICallRequest implements Serializable {

   /**
    * The name the function to call, never <code>null</code>.
    */
   private final String _functionName;

   /**
    * The call configuration. Initially <code>null</code>.
    */
   private XINSCallConfig _callConfig;

   /**
    * Mapping from parameter names to either their associated string values or
    * to an exception if the conversion to a string failed. This field is
    * lazily initialized and initially <code>null</code>.
    */
   private Map _parameterValues;

   /**
    * The data section of the function if any, can be <code>null</code>.
    */
   private Element _dataSection;

   /**
    * The data element builder, can be <code>null</code>.
    */
   private Element _dataElement;

   /**
    * Creates a new <code>AbstractCAPICallRequest</code>.
    *
    * @param functionName
    *    the name of the function used with this request.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   protected AbstractCAPICallRequest(String functionName)
   throws IllegalArgumentException{
      MandatoryArgumentChecker.check("functionName", functionName);
      _functionName = functionName;
   }

   /**
    * Sets the specified parameter to the specified value.
    *
    * @param name
    *    the name of the parameter to set, cannot be <code>null</code>.
    *
    * @param value
    *    the character string representation of the value of the parameter,
    *    can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   protected final void parameterValue(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name",  name);

      // If there is no value, then remove the entry from the map
      if (value == null) {
         if (_parameterValues != null) {
            _parameterValues.remove(name);
         }

      // Otherwise just store
      } else {

         if (_parameterValues == null) {
            _parameterValues = new HashMap();
         }
         _parameterValues.put(name, value);
      }
   }

   /**
    * Returns an appropriate <code>XINSCallRequest</code> object.
    *
    * @return
    *    a {@link XINSCallRequest}, never <code>null</code>.
    */
   final XINSCallRequest xinsCallRequest() {

      // Construct a XINSCallRequest object
      XINSCallRequest request = new XINSCallRequest(_functionName);

      // Set all parameters on the request, if any
      if (_parameterValues != null && _parameterValues.size() > 0) {

         // Loop over all parameters in the map containing the types
         Iterator iterator = _parameterValues.keySet().iterator();
         while (iterator.hasNext()) {

            // Determine parameter name, type and value
            String name  = (String) iterator.next();
            String value = (String) _parameterValues.get(name);

            // Set the parameter on the request
            request.setParameter(name, value);
         }
      }

      Element dataSection = getDataElement();
      if (dataSection != null) {
         request.setDataSection(dataSection);
      }

      if (_callConfig != null) {
         request.setXINSCallConfig(_callConfig);
      }

      return request;
   }

   /**
    * Assigns the specified call configuration to this request.
    *
    * @param config
    *    the call configuration to apply when executing this request, or
    *    <code>null</code> if no specific call configuration should be
    *    associated with this request.
    */
   public final void configure(XINSCallConfig config) {
      _callConfig = config;
   }

   /**
    * Retrieves the call configuration currently associated with this request.
    *
    * @return
    *    the call configuration currently associated with this request, which
    *    will be applied when executing this request, or <code>null</code> if
    *    no specific call configuration is associated with this request.
    */
   public final XINSCallConfig configuration() {
      return _callConfig;
   }

   /**
    * Gets the value of a parameter or <code>null</code> if this parameter
    * is not set.
    *
    * @param parameterName
    *    the name of the parameter, can be <code>null</code>.
    *
    * @return
    *    the value of a parameter or <code>null</code> if this parameter
    *    is not set.
    */
   protected final String getParameter(String parameterName) {
      if (_parameterValues == null) {
         return null;
      } else {
         return (String) _parameterValues.get(parameterName);
      }
   }

   /**
    * Sets the data section.
    * If the value is <code>null</code> any previous data section set is removed.
    * If a previous value was entered, the value will be overridden by this new
    * value.
    *
    * @param dataSection
    *    the data section.
    */
   protected final void putDataSection(Element dataSection) {
      _dataSection = dataSection;
      _dataElement = null;
   }

   /**
    * Gets the name of the function to call.
    *
    * @return
    *    the name of the function to call, never <code>null</code>.
    *
    * @since XINS 1.4.0
    */
   public final String functionName() {
      return _functionName;
   }

   /**
    * Add a new Element to the data element.
    * Any previous value was entered with the method {@link #putDataSection},
    * will be removed.
    *
    * @param element
    *    the new element to add to the result, cannot be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   protected void add(Element element) {
      if (_dataElement == null) {
         _dataElement = new Element("data");
         _dataSection = null;
      }
      _dataElement.addChild(element);
   }

   /**
    * Gets the data section.
    *
    * @return
    *    the data section or <code>null</code> if there is no data section.
    */
   protected Element getDataElement() {
      if (_dataElement != null) {
         return _dataElement;
      }
      return _dataSection;
   }

   /**
    * Validates whether this request is considered acceptable. If any
    * constraints are violated, then an {@link UnacceptableRequestException}
    * is returned.
    *
    * <p>This method is called automatically when this request is executed, so
    * it typically does not need to be called manually in advance.
    *
    * @return
    *    an {@link UnacceptableRequestException} instance if this request is
    *    considered unacceptable, otherwise <code>null</code>.
    */
   public abstract UnacceptableRequestException checkParameters();

   public int hashCode() {
      int hashCode = _functionName.hashCode();
      if (_parameterValues != null) {
         hashCode += _parameterValues.hashCode();
      }
      Element dataElement = getDataElement();
      if (dataElement != null) {
         hashCode += dataElement.hashCode();
      }
      return hashCode;
   }

   public boolean equals(Object obj) {
      if (obj == null || !obj.getClass().isInstance(this)) {
         return false;
      }
      AbstractCAPICallRequest otherRequest = (AbstractCAPICallRequest) obj;
      if ((_parameterValues != null && !_parameterValues.equals(otherRequest._parameterValues)) ||
            (_parameterValues == null && otherRequest._parameterValues != null)) {
         return false;
      }
      Element dataElement = getDataElement();
      Element otherDataElement = otherRequest.getDataElement();
      if ((dataElement != null && !dataElement.equals(otherDataElement)) ||
            (dataElement == null && otherDataElement != null)) {
         return false;
      }
      return true;
   }
}
