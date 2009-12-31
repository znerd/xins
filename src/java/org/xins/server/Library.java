/*
 * $Id: Library.java,v 1.43 2007/03/16 09:55:00 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.text.PatternUtils;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision: 1.43 $ $Date: 2007/03/16 09:55:00 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class Library {

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"3.0"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return Library.class.getPackage().getImplementationVersion();
   }

   /**
    * Checks if the specified version indicates a production release of XINS.
    *
    * @param version
    *    the XINS version to check, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> is the specified XINS version identifies a
    *    production release of XINS, <code>false</code> if it does not.
    *
    * @throws NullPointerException
    *    if <code>version == null</code>.
    */
   static final boolean isProductionRelease(String version) throws NullPointerException {
      return version.matches("[1-9][0-9]*\\.[0-9]+(\\.[0-9]+)?");
      // TODO: Review (the location of) this method
   }
}
