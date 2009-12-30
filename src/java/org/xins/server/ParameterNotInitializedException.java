/*
 * $Id: ParameterNotInitializedException.java,v 1.15 2007/09/18 08:45:05 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that it is attempted to get the value of a
 * parameter that has not been set.
 *
 * @version $Revision: 1.15 $ $Date: 2007/09/18 08:45:05 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class ParameterNotInitializedException extends RuntimeException {

   /**
    * Constructs a new <code>ParameterNotInitializedException</code> for the
    * specified parameter.
    *
    * @param paramName
    *    the name of the parameter that is attempted to be retrieved, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>paramName == null</code>.
    */
   public ParameterNotInitializedException(String paramName)
   throws IllegalArgumentException {
      super(paramName);

      // Check argument
      MandatoryArgumentChecker.check("paramName", paramName);
   }
}
