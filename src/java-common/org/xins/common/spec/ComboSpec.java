/*
 * $Id: ComboSpec.java,v 1.10 2007/09/18 11:20:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.util.Map;

/**
 * Specification of a combo.
 *
 * @version $Revision: 1.10 $ $Date: 2007/09/18 11:20:49 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
class ComboSpec {

   /**
    * The type of the combo, never <code>null</code>.
    */
   private final String _type;

   /**
    * The parameters of this combo, never <code>null</code>.
    */
   private final Map _parameters;

   /**
    * Creates a new <code>ComboSpec</code>.
    *
    * @param type
    *    the type of the combo, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters this param-combo or attribute-combo refers to, cannot be <code>null</code>.
    */
   ComboSpec(String type, Map parameters) {
      _type = type;
      _parameters = parameters;
   }

   /**
    * Returns whether the combo is a all-or-none type.
    *
    * @return
    *    <code>true</code> if the type is <i>all-or-none</i>, <code>false</code> otherwise.
    */
   public boolean isAllOrNone() {

      return _type.equals("all-or-none");
   }

   /**
    * Returns whether the combo is a not-all type.
    *
    * @return
    *    <code>true</code> if the type is <i>not-all</i>, <code>false</code> otherwise.
    */
   public boolean isNotAll() {

      return _type.equals("not-all");
   }

   /**
    * Returns whether the combo is a exclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is <i>exclusive-or</i>, <code>false</code> otherwise.
    */
   public boolean isExclusiveOr() {

      return _type.equals("exclusive-or");
   }

   /**
    * Returns whether the combo is a inclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is <i>inclusive-or</i>, <code>false</code> otherwise.
    */
   public boolean isInclusiveOr() {

      return _type.equals("inclusive-or");
   }

   /**
    * Gets the parameters or attributes defined in the combo.
    * The key is the name of the parameter or of the attributes,
    * the value is the {@link ParameterSpec} object.
    *
    * @return
    *    the specification of the parameters defined in the combo, never <code>null</code>.
    */
   protected Map getReferences() {

      return _parameters;
   }
}
