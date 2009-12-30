/*
 * $Id: AttributeComboSpec.java,v 1.8 2007/09/18 11:20:47 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.Map;

/**
 * Specification of a attribute combo.
 *
 * @version $Revision: 1.8 $ $Date: 2007/09/18 11:20:47 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
public final class AttributeComboSpec extends ComboSpec {

   /**
    * Creates a new <code>AttributeComboSpec</code>.
    *
    * @param type
    *    the type of the attribute-combo, cannot be <code>null</code>.
    *
    * @param attributes
    *    the attributes this attribute-combo refers to, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>type == null || attributes == null</code>.
    */
   AttributeComboSpec(String type, Map attributes) throws IllegalArgumentException {
      super(type, attributes);
   }

   /**
    * Gets the attributes defined in the attribute combo.
    * The key is the name of the attribute, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The specification of the attributes defined in the attribute combo, never <code>null</code>.
    */
   public Map getAttributes() {

      return getReferences();
   }
}
