/*
 * $Id: ResultCode.java,v 1.29 2007/09/18 08:45:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Abstraction of an error code returned by a function. Result codes are
 * either generic or API-specific.
 *
 * <p>Result codes do not automatically apply to all functions of an API if
 * they have been defined for that API. Instead they are associated with each
 * individual function.
 *
 * @version $Revision: 1.29 $ $Date: 2007/09/18 08:45:06 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public final class ResultCode {

   /**
    * The symbolic name of this result code. Can be <code>null</code>.
    */
   private final String _name;

   /**
    * Constructs a new generic <code>ResultCode</code>.
    *
    * @param name
    *    the symbolic name, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public ResultCode(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      _name = name;
   }

   /**
    * Returns the symbolic name of this result code.
    *
    * @return
    *    the symbolic name, can be <code>null</code>.
    */
   public final String getName() {
      return _name;
   }
}
