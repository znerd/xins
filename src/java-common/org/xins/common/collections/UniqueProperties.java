/*
 * $Id: UniqueProperties.java,v 1.3 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Properties;
import org.xins.common.Log;

/**
 * Class that indicates whether a property value is overwritten.
 * It also logs which property has been overwritten.
 *
 * @version $Revision: 1.3 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0.
 */
public class UniqueProperties extends Properties {

   /**
    * Flag that indicates that a identical key has been put more than once in
    * this properties object.
    */
   private boolean _unique = true;

   public Object put(Object key, Object value) {
       Object oldValue = super.put(key, value);
       if (oldValue != null &&
             key instanceof String && value instanceof String && oldValue instanceof String) {
           _unique = false;
           Log.log_1351((String) key, (String) oldValue, (String) value);
       }
       return oldValue;
   }

   /**
    * Indicates whether a property has been changed in this collection.
    *
    * @return
    *    <code>true</code> if none of the properties were changed, <code>false</code> otherwise.
    */
   public boolean isUnique() {
      return _unique;
   }
}
