/*
 * $Id: WarnDoubleProperties.java,v 1.8 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Properties;

import org.xins.common.Log;

/**
 * Class that logs a warning message in the log system if a property value
 * is overwritten.
 *
 * @version $Revision: 1.8 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
public class WarnDoubleProperties extends Properties {
   public Object put(Object key, Object value) {
       Object oldValue = super.put(key, value);
       if (oldValue != null &&
             key instanceof String && value instanceof String && oldValue instanceof String) {
           Log.log_1350((String) key, (String) oldValue, (String) value);
       }
       return oldValue;
   }
}
