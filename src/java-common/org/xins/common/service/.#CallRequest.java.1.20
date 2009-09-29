/*
 * $Id: CallRequest.java,v 1.20 2007/03/15 17:08:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.io.Serializable;

/**
 * Abstraction of a request for a <code>ServiceCaller</code> call. Specific
 * service callers typically only accept a single type of request, derived
 * from this class.
 *
 * <p>This class is not thread-safe.
 *
 * <h2>Implementations</h2>
 *
 * <p>Implementations of this class should stick to the following rules:
 *
 * <ul>
 *    <li>the {@link #describe()} method must be implemented;
 *    <li>a service caller-specific getter should be added for the associated
 *        call config object, this method should return the caller-specific
 *        call config object (a subclass of class {@link CallConfig}).
 * </ul>
 *
 * @version $Revision: 1.20 $ $Date: 2007/03/15 17:08:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see ServiceCaller
 */
public abstract class CallRequest implements Serializable {

   /**
    * The <code>CallConfig</code> associated with this request, if any. Can be
    * -and initially is- <code>null</code>.
    */
   private CallConfig _callConfig;

   /**
    * Constructs a new <code>CallRequest</code>. This constructor is only
    * available to subclasses, since this class is <code>abstract</code>.
    */
   protected CallRequest() {
      // empty
   }

   /**
    * Describes this request. The description should be trimmed and should fit
    * in a sentence. Good examples include <code>"LDAP request #1592"</code>
    * and <code>"request #12903"</code>.
    *
    * @return
    *    the description of this request, should never be <code>null</code>,
    *    should never be empty and should never start or end with whitespace
    *    characters.
    */
   public abstract String describe();

   /**
    * Returns a textual presentation of this object.
    *
    * <p>The implementation of this method in class {@link CallRequest}
    * returns {@link #describe()}.
    *
    * @return
    *    a textual presentation of this object, should never be
    *    <code>null</code>.
    */
   public String toString() {
      return describe();
   }

   /**
    * Retrieves the associated call configuration, if any.
    *
    * @return
    *    the associated call configuration, or <code>null</code> if none is.
    *
    * @since XINS 1.1.0
    */
   protected final CallConfig getCallConfig() {
      return _callConfig;
   }

   /**
    * Sets the call configuration associated with this request.
    *
    * @param config
    *    the call configuration to associate with this request, or
    *    <code>null</code> if none should be.
    *
    * @since XINS 1.1.0
    */
   protected final void setCallConfig(CallConfig config) {
      _callConfig = config;
   }
}
