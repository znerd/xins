/*
 * $Id: Descriptor.java,v 1.18 2007/03/12 10:40:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Descriptor for a service or group of services.
 *
 * <p>Once constructed, a <code>Descriptor</code> instance is unmodifiable, it
 * will never change anymore.
 *
 * <p>Since XINS 3.0, this class adds a {@link #targets()} method that returns
 * a {@link Collection}. To iterate over the contained
 * {@link TargetDescriptor} instances, use the following approach:
 *
 * <blockquote id="examplecode"><pre>for ({@linkplain TargetDescriptor} target : descriptor.{@linkplain #targets()}) {
   // do something with 'target'
}</pre></blockquote>
 *
 * @version $Revision: 1.18 $ $Date: 2007/03/12 10:40:49 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class Descriptor
implements Serializable {

   /**
    * Constructs a new <code>Descriptor</code>.
    */
   Descriptor() {
      // empty
   }

   /**
    * Checks if this descriptor denotes a group of descriptor of descriptorss.
    *
    * @return
    *    <code>true</code> if this descriptor denotes a group,
    *    <code>false</code> otherwise.
    */
   public abstract boolean isGroup();

   /**
    * Iterates over all leaves, the target descriptors.
    *
    * <p>The returned {@link Iterator} will not support
    * {@link Iterator#remove()}. The iterator will only return
    * {@link TargetDescriptor} instances, no instances of other classes and
    * no <code>null</code> values.
    *
    * <p>Also, this iterator is guaranteed to return {@link #getTargetCount()}
    * instances of class {@link TargetDescriptor}.
    *
    * @return
    *    iterator over the leaves, the target descriptors, in this
    *    descriptor, in the correct order, never <code>null</code>.
    *
    * @deprecated
    *    Since XINS 3.0. Use {@link #targets()} instead, see the 
    *    <a href="#examplecode">example code</a> in the class description.
    */
   @Deprecated
   public final Iterator<TargetDescriptor> iterateTargets() {
      return targets().iterator();
   }

   /**
    * Returns all leaves, the target descriptors.
    *
    * @return
    *    all the leaves, the {@link TargetDescriptor} instances,
    *    never <code>null</code>.
    *
    * @since XINS 3.0
    */
   public abstract Collection<TargetDescriptor> targets();

   /**
    * Counts the total number of target descriptors in/under this descriptor.
    *
    * @return
    *    the total number of target descriptors, always &gt;= 1.
    */
   public abstract int getTargetCount();

   /**
    * Returns the <code>TargetDescriptor</code> that matches the specified
    * CRC-32 checksum.
    *
    * @param crc
    *    the CRC-32 checksum.
    *
    * @return
    *    the {@link TargetDescriptor} that matches the specified checksum, or
    *    <code>null</code>, if none could be found in this descriptor.
    */
   public abstract TargetDescriptor getTargetByCRC(int crc);
}
