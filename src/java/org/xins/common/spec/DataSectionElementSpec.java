/*
 * $Id: DataSectionElementSpec.java,v 1.16 2007/09/18 11:20:47 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.List;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Specification of a data section element.
 *
 * @version $Revision: 1.16 $ $Date: 2007/09/18 11:20:47 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public final class DataSectionElementSpec {

   /**
    * Name of the element, cannot be <code>null</code>.
    */
   private final String _name;

   /**
    * Description of the element, cannot be <code>null</code>.
    */
   private final String _description;

   /**
    * Flag indicating that the element can have PCDATA.
    */
   private final boolean _isPCDataAllowed;

   /**
    * The sub elements of the element, cannot be <code>null</code>.
    */
   private final Map _subElements;

   /**
    * The attributes of the element, cannot be <code>null</code>.
    */
   private final Map _attributes;

   /**
    * The attribute combos of the element, cannot be <code>null</code>.
    */
   private final List _attributeCombos;

   /**
    * Creates a new instance of <code>DataSectionElementSpec</code>.
    *
    * @param name
    *    the name of the data section element, cannot be <code>null</code>.
    *
    * @param description
    *    the description of the data section element, cannot be <code>null</code>.
    *
    * @param isPCDataAllowed
    *    <code>true</code> if the element can contain text, <code>false</code> otherwise.
    *
    * @param subElements
    *    the sub elements that can contain this element, cannot be <code>null</code>.
    *
    * @param attributes
    *    the possible attributes for this element, cannot be <code>null</code>.
    *
    * @param attributeCombos
    *    the attribute combos for this element, cannot be <code>null</code>.
    */
   DataSectionElementSpec(String name, String description, boolean isPCDataAllowed,
         Map subElements, Map attributes, List attributeCombos)
   {
      _name = name;
      _description = description;
      _isPCDataAllowed = isPCDataAllowed;
      _attributes = attributes;
      _subElements = subElements;
      _attributeCombos = attributeCombos;
   }

   /**
    * Gets the name of the data element.
    *
    * @return
    *    The name of the data element, never <code>null</code>.
    */
   public String getName() {

      return _name;
   }

   /**
    * Gets the description of the data element.
    *
    * @return
    *    The description of the data element, never <code>null</code>.
    */
   public String getDescription() {

      return _description;
   }

   /**
    * Gets the specified sub element that are included in this element.
    *
    * @param elementName
    *    the name of the element, cannot be <code>null</code>.
    *
    * @return
    *    The specification of the sub element, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    *
    * @throws EntityNotFoundException
    *    if the element does not have any sub element with the specified name.
    */
   public DataSectionElementSpec getSubElement(String elementName)
   throws IllegalArgumentException, EntityNotFoundException {

      MandatoryArgumentChecker.check("elementName", elementName);

      DataSectionElementSpec element = (DataSectionElementSpec) _subElements.get(elementName);

      if (element == null) {
         throw new EntityNotFoundException("Sub element \"" + elementName
                 + "\" not found in the element \"" + _name +"\".");
      }

      return element;
   }

   /**
    * Gets the specification of the sub elements that are included in this element.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *    the specification of the sub elements, never <code>null</code>.
    */
   public Map getSubElements() {

      return _subElements;
   }

   /**
    * Gets the specification of the specified attribute of the element.
    *
    * @param attributeName
    *    the name of the attribute, cannot be <code>null</code>.
    *
    * @return
    *    The specification of the attribute, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the element does not have any attribute with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>attributeName == null</code>.
    */
   public ParameterSpec getAttribute(String attributeName)
   throws EntityNotFoundException, IllegalArgumentException {

      MandatoryArgumentChecker.check("attributeName", attributeName);

      ParameterSpec attribute = (ParameterSpec) _attributes.get(attributeName);

      if (attribute == null) {
         throw new EntityNotFoundException("Attribute \"" + attributeName
                 + "\" not found in the element \"" + _name +"\".");
      }

      return attribute;
   }

   /**
    * Gets the attributes of the element.
    * The key is the name of the attribute, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The specification of the attributes, never <code>null</code>.
    */
   public Map getAttributes() {

      return _attributes;
   }

   /**
    * Returns whether the element can contain a PCDATA text.
    *
    * @return
    *    <code>true</code> if the element can contain text, <code>false</code> otherwise.
    */
   public boolean isPCDataAllowed() {

      return _isPCDataAllowed;
   }

   /**
    * Gets the attribute combos defined for the element.
    * A list of {@link AttributeComboSpec} object.
    *
    * @return
    *    the list of the attribute combos define for the element, never <code>null</code>.
    *
    * @since XINS 1.4.0
    */
   public List getAttributeCombos() {

      return _attributeCombos;
   }
}
