/*
 * $Id: CustomCallingConvention.java,v 1.27 2007/09/18 08:45:05 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class for calling convention implementations that are not part of the
 * core XINS framework.
 *
 * <p>Extend this class to create your own calling conventions.
 *
 * <p>If your custom calling convention takes XML as input, you are advised to
 * use {@link #parseXMLRequest(HttpServletRequest)} to parse the request.
 *
 * <p>Note: Since XINS 3.0, the {@link #matches(HttpServletRequest)} method
 * is abstract.
 *
 * @version $Revision: 1.27 $ $Date: 2007/09/18 08:45:05 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public abstract class CustomCallingConvention extends CallingConvention {

   /**
    * Constructs a new <code>CustomCallingConvention</code>.
    */
   public CustomCallingConvention() {
      // empty
   }
}
