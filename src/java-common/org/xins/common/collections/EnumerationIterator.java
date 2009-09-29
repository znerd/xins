/*
 * $Id: EnumerationIterator.java,v 1.14 2007/03/15 17:08:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Iterator implementation that reads from an <code>Enumeration</code>.
 *
 * @version $Revision: 1.14 $ $Date: 2007/03/15 17:08:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class EnumerationIterator implements Iterator {

   /**
    * The underlying <code>Enumeration</code> object.
    */
   private final Enumeration _enumeration;

   /**
    * Constructs a new <code>EnumerationIterator</code> on top of the
    * specified <code>Enumeration</code>.
    *
    * @param enumeration
    *    the {@link Enumeration} object, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>enumeration == null</code>.
    */
   public EnumerationIterator(Enumeration enumeration)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("enumeration", enumeration);

      _enumeration = enumeration;
   }

   /**
    * Checks if the iteration has more elements.
    *
    * @return
    *    <code>true</code> if the iteration has more elements,
    *    <code>false</code> otherwise.
    */
   public boolean hasNext() {
      return _enumeration.hasMoreElements();
   }

   /**
    * Returns the next element in the iteration.
    *
    * @return
    *    the next element.
    *
    * @throws NoSuchElementException
    *    if the iteration has no more elements.
    */
   public Object next() throws NoSuchElementException {
      return _enumeration.nextElement();
   }

   /**
    * Removes the last element returned by the iterator (unsupported
    * operation).
    *
    * <p>The implementation of this method in class
    * {@link EnumerationIterator} always throws an
    * {@link UnsupportedOperationException}.
    *
    * @throws UnsupportedOperationException
    *    if this operation is not supported, which is the case for this
    *    implementation, so always.
    */
   public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
