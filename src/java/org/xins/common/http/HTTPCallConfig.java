/*
 * $Id: HTTPCallConfig.java,v 1.22 2007/03/15 17:08:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.CallConfig;
import org.xins.common.text.TextUtils;

/**
 * Call configuration for the HTTP service caller. The HTTP method and the
 * <em>User-Agent</em> string can be configured. By default the HTTP method is
 * <em>POST</em> and the no <em>User-Agent</em> string is set.
 *
 * <p>This class is not thread safe.</p>
 *
 * @version $Revision: 1.22 $ $Date: 2007/03/15 17:08:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 */
public final class HTTPCallConfig extends CallConfig {

   /**
    * The HTTP method to use. This field cannot be <code>null</code>.
    */
   private HTTPMethod _method;

   /**
    * The HTTP user agent. This field can be <code>null</code>.
    */
   private String _userAgent;

   /**
    * Constructs a new <code>HTTPCallConfig</code> object.
    */
   public HTTPCallConfig() {

      // Default to the POST method
      _method = HTTPMethod.POST;
   }

   /**
    * Returns the HTTP method associated with this configuration.
    *
    * @return
    *    the HTTP method, never <code>null</code>.
    */
   public HTTPMethod getMethod() {
     return _method;
   }

   /**
    * Sets the HTTP method associated with this configuration.
    *
    * @param method
    *    the HTTP method to be associated with this configuration, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null</code>.
    */
   public void setMethod(HTTPMethod method)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("method", method);

      // Store the new HTTP method
      _method = method;
   }

   /**
    * Sets the user agent associated with the HTTP call.
    *
    * @param agent
    *    the HTTP user agent, or <code>null</code> if no user-agent header
    *    should be sent.
    *
    * @since XINS 1.3.0
    */
   public void setUserAgent(String agent) {
      _userAgent = agent;
   }

   /**
    * Returns the HTTP user agent associated with the HTTP call.
    *
    * @return
    *    the HTTP user agent or <code>null</code> no user agent has been
    *    specified.
    *
    * @since XINS 1.3.0
    */
   public String getUserAgent() {
      return _userAgent;
   }

   /**
    * Describes this configuration.
    *
    * @return
    *    the description of this configuration, should never be
    *    <code>null</code>, should never be empty and should never start or
    *    end with whitespace characters.
    */
   public String describe() {

      String description = "HTTP call config [failOverAllowed=" + isFailOverAllowed() + "; method=" +
            TextUtils.quote(_method.toString()) + "; userAgent=" + TextUtils.quote(_userAgent) + "]";

      return description;
   }
}
