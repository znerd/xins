/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.BasicPropertyReader;

/**
 * Utility class for servlet-related functionality.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public final class ServletUtils {

   /**
    * Constructs a new <code>ServletUtils</code> object.
    */
   private ServletUtils() {
      // empty, never used
   }

   /**
    * Constructs a new <code>PropertyReader</code> containing all request
    * headers (and their values) for the specified
    * <code>HttpServletRequest</code>.
    *
    * <p>If there are multiple headers with the same name, then the first
    * value found is used and all subsequent values are ignored.
    *
    * @param request
    *    the {@link HttpServletRequest} object, cannot be <code>null</code>.
    *
    * @return
    *    a modifiable {@link BasicPropertyReader}, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    */
   public static BasicPropertyReader convertHeadersToPropertyReader(HttpServletRequest request)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // Construct an empty BasicPropertyReader object
      BasicPropertyReader properties = new BasicPropertyReader();

      // Loop over all headers
      Enumeration e = request.getHeaderNames();
      if (e != null) {
         while (e.hasMoreElements()) {
            String  name = (String) e.nextElement();
            String value = request.getHeader(name);

            properties.set(name, value);
         }
      }

      return properties;
   }

   /**
    * Constructs a new <code>PropertyReader</code> containing all request
    * parameters (and their values) for the specified
    * <code>HttpServletRequest</code>.
    *
    * <p>If there are multiple parameters with the same name, then the first
    * value found is used and all subsequent values are ignored.
    *
    * @param request
    *    the {@link HttpServletRequest} object, cannot be <code>null</code>.
    *
    * @return
    *    a modifiable {@link BasicPropertyReader}, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    */
   public static BasicPropertyReader convertParametersToPropertyReader(HttpServletRequest request)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // Construct an empty BasicPropertyReader object
      BasicPropertyReader properties = new BasicPropertyReader();

      // Loop over all parameters
      Enumeration e = request.getParameterNames();
      if (e != null) {
         while (e.hasMoreElements()) {
            String  name = (String) e.nextElement();
            String value = request.getParameter(name);

            properties.set(name, value);
         }
      }

      return properties;
   }
}
