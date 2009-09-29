/*
 * $Id: ServletConfigPropertyReader.java,v 1.19 2007/08/13 08:40:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.EnumerationIterator;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

/**
 * Implementation of a <code>PropertyReader</code> that returns the
 * initialization properties from a <code>ServletConfig</code> object.
 *
 * @version $Revision: 1.19 $ $Date: 2007/08/13 08:40:08 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class ServletConfigPropertyReader implements PropertyReader {

   /**
    * The servlet configuration object.
    */
   private final ServletConfig _servletConfig;

   /**
    * The number of properties. This field is lazily initialized by
    * {@link #size()}.
    */
   private int _size;

   /**
    * Constructs a new <code>ServletConfigPropertyReader</code>.
    *
    * @param servletConfig
    *    the {@link ServletConfig} object, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>servletConfig == null</code>.
    */
   public ServletConfigPropertyReader(ServletConfig servletConfig)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("servletConfig", servletConfig);

      _servletConfig = servletConfig;
   }

   /**
    * Retrieves the value of the property with the specified name.
    *
    * @param name
    *    the name of the property, cannot be <code>null</code>.
    *
    * @return
    *    the value of the property, possibly <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      return _servletConfig.getInitParameter(name);
   }

   /**
    * Gets an iterator that iterates over all the property names. The
    * {@link Iterator} will return only {@link String} instances.
    *
    * @return
    *    the {@link Iterator} that will iterate over all the names, never
    *    <code>null</code>.
    *
    * @deprecated
    *    Since XINS 3.0. Use {@link #names()} instead, which is type-safe
    *    and supports the
    *    <a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/foreach.html">Java 5 foreach-operator</a>.
    */
   public Iterator getNames() {
      return names().iterator();
   }

   /**
    * Gets all property names, as a <code>Collection</code> of
    * <code>String</code> objects.
    *
    * @return
    *    all property names, never <code>null</code>
    *    (but may be an empty {@link Collection}).
    *
    * @since XINS 3.0
    */
   public Collection<String> names() {
      ArrayList<String> names = new ArrayList<String>();
      Enumeration e = _servletConfig.getInitParameterNames();
      while (e.hasMoreElements()) {
         names.add((String) e.nextElement());
      }
      return names;
   }

   /**
    * Determines the number of properties.
    *
    * @return
    *    the size, always &gt;= 0.
    */
   public int size() {
      if (_size < 0) {
         int size = 0;
         Enumeration e = _servletConfig.getInitParameterNames();
         while (e.hasMoreElements()) {
            e.nextElement();
            size++;
         }
         _size = size;
      }

      return _size;
   }

   /**
    * Compares this object with the specified argument for equality.
    *
    * @param obj
    *    the object to compare with, can be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the objects <code>a</code> and <code>b</code>
    *    are considered to be equal, <code>false</code> if they are considered
    *    different.
    */
   public boolean equals(Object obj) {
      return PropertyReaderUtils.equals(this, obj);
   }

   /**
    * Returns a hash code value for this object.
    *
    * @return
    *    a hash code value for this object.
    */
   public int hashCode() {
      // XXX: This is compute-intensive.
      //      Possible optimization is to store the hash code in a field.
      return PropertyReaderUtils.hashCode(this);
   }
}
