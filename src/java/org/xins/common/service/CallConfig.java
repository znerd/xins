/*
 * $Id: CallConfig.java,v 1.28 2007/09/18 08:45:14 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.io.Serializable;

/**
 * Configuration for a service call. Objects of this type specify certain
 * aspects of <em>how</em> a call is executed. For example, for an HTTP
 * service caller, a <code>CallConfig</code> object could specify what HTTP
 * method (GET, POST, etc.) to use.
 *
 * <p>This base class only specifies the property <em>failOverAllowed</em>,
 * which indicates whether fail-over is unconditionally allowed, even if the
 * request was already received or even processed by the other end.
 *
 * <p>This class is not thread safe</p>
 *
 * @version $Revision: 1.28 $ $Date: 2007/09/18 08:45:14 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.1.0
 *
 * @see ServiceCaller
 * @see CallRequest
 */
public abstract class CallConfig implements Serializable {

   /**
    * Flag that indicates whether fail-over is unconditionally allowed.
    */
   private boolean _failOverAllowed;

   /**
    * Constructs a new <code>CallConfig</code>.
    */
   protected CallConfig() {
      // empty
   }

   /**
    * Describes this configuration.
    *
    * <p>The implementation of this method in class {@link CallConfig} returns
    * a descriptive string that contains the <em>failOverAllowed</em> setting.
    *
    * @return
    *    the description of this configuration, should never be <code>null</code>.
    */
   public String describe() {

      String description = "Call config with fail over ";
      if (!_failOverAllowed) {
         description += "not ";
      }
      description += "allowed.";

      return description;
   }

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
   public final String toString() {
      return describe();
   }

   /**
    * Determines whether fail-over is unconditionally allowed.
    *
    * @return
    *    <code>true</code> if fail-over is unconditionally allowed, even if
    *    the request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    */
   public final boolean isFailOverAllowed() {
     return _failOverAllowed;
   }

   /**
    * Configures whether fail-over is unconditionally allowed.
    *
    * @param allowed
    *    <code>true</code> if fail-over is unconditionally allowed, even if
    *    the request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    */
   public final void setFailOverAllowed(boolean allowed) {
     _failOverAllowed = allowed;
   }
}
