/*
 * $Id: HTTPMethod.java,v 1.13 2007/03/15 17:08:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import java.io.Serializable;

/**
 * HTTP method. Possible values for variable of this class:
 *
 * <ul>
 *    <li>{@link #GET}
 *    <li>{@link #POST}
 *    <li>{@link #HEAD}
 * </ul>
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/15 17:08:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class HTTPMethod implements Serializable {

   /**
    * The GET method.
    */
   public static final HTTPMethod GET = new HTTPMethod("GET");

   /**
    * The POST method.
    */
   public static final HTTPMethod POST = new HTTPMethod("POST");

   /**
    * The HEAD method.
    *
    * @since XINS 2.2
    */
   public static final HTTPMethod HEAD = new HTTPMethod("HEAD");

   /**
    * The name of this method. For example <code>"GET"</code> or
    * <code>"POST"</code>. This field should never be <code>null</code>.
    */
   private final String _name;

   /**
    * Constructs a new <code>HTTPMethod</code> object with the specified name.
    *
    * @param name
    *    the name of the method, for example <code>"GET"</code> or
    *    <code>"POST"</code>; should not be <code>null</code>.
    */
   private HTTPMethod(String name) {
      _name = name;
   }

   /**
    * Returns a textual representation of this object. The implementation
    * of this method returns the name of this HTTP method, like
    * <code>"GET"</code> or <code>"POST"</code>.
    *
    * @return
    *    the name of this method, e.g. <code>"GET"</code> or
    *    <code>"POST"</code>; never <code>null</code>.
    */
   public String toString() {
      return _name;
   }
}
