/*
 * $Id: FunctionNotSpecifiedException.java,v 1.9 2007/03/12 10:40:30 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Exception that indicates that an incoming request does not specify the
 * function to execute.
 *
 * @version $Revision: 1.9 $ $Date: 2007/03/12 10:40:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class FunctionNotSpecifiedException
extends Exception {

   /**
    * Constructs a new <code>FunctionNotSpecifiedException</code>.
    */
   public FunctionNotSpecifiedException() {
      super("Function not specified in incoming request.");
   }
}
