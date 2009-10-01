/*
 * $Id: UnsupportedLocaleException.java,v 1.16 2007/03/15 17:08:40 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Exception thrown if a specified locale is not supported by at least one
 * <em>logdoc</em> <code>Log</code> class.
 *
 * @version $Revision: 1.16 $ $Date: 2007/03/15 17:08:40 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class UnsupportedLocaleException extends RuntimeException {

   private static final long serialVersionUID = -991987123777189023L;

   /**
    * The locale that is unsupported. The value of this field cannot be
    * <code>null</code>.
    */
   private final String _locale;

   /**
    * Constructs a new <code>UnsupportedLocaleException</code>.
    *
    * @param locale
    *    the locale, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>locale == null</code>.
    */
   public UnsupportedLocaleException(String locale)
   throws IllegalArgumentException {

      // Call superconstructor first
      super("Locale \"" + locale + "\" is not supported.");

      // Check preconditions
      if (locale == null) {
         throw new IllegalArgumentException("locale == null");
      }

      // Store locale?
      _locale = locale;
   }

   /**
    * Retrieves the unsupported locale.
    *
    * @return
    *    the unsupported locale, never <code>null</code>.
    */
   public String getLocale() {
      return _locale;
   }
}
