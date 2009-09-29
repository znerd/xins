/*
 * $Id: ProtectedPropertyReader.java,v 1.23 2007/09/24 12:18:48 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.HashMap;
import java.util.Iterator;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Modifiable <code>PropertyReader</code> implementaton that can be protected
 * from unauthorized changes.
 *
 * <p>A secret key must be passed when constructing a
 * <code>ProtectedPropertyReader</code> instance. All modification methods on
 * this object then require this same secret key to be passed, otherwise they
 * fail with an {@link IncorrectSecretKeyException}.
 *
 * <p>Note that the secret key equality is always checked before the other
 * preconditions. This means that if the secret key is incorrect, then the
 * other preconditions will not even be checked. For example, if <code>
 * {@link #set(Object,String,String) set}(null, null)</code> is called, then
 * an {@link IncorrectSecretKeyException} is thrown for the mismatching secret
 * key, and not an instance of the superclass
 * {@link IllegalArgumentException}, for the missing <code>name</code>
 * argument.
 *
 * @version $Revision: 1.23 $ $Date: 2007/09/24 12:18:48 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class ProtectedPropertyReader
extends AbstractPropertyReader {

   /**
    * The secret key. This field is initialized by the constructor and can
    * never be <code>null</code>.
    */
   private final Object _secretKey;

   /**
    * Constructs a new <code>ProtectedPropertyReader</code>.
    *
    * @param secretKey
    *    the secret key that must be passed to the modification methods in
    *    order to be authorized to modify this collection.
    *
    * @throws IllegalArgumentException
    *    if <code>secretKey == null</code>.
    */
   public ProtectedPropertyReader(Object secretKey)
   throws IllegalArgumentException {
      super(new HashMap(89));

      // Check preconditions
      MandatoryArgumentChecker.check("secretKey", secretKey);

      _secretKey = secretKey;
   }

   /**
    * Verifies that the specified object matches the secret key. If not, an
    * exception is thrown.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @throws IncorrectSecretKeyException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    */
   private void checkSecretKey(Object secretKey)
   throws IncorrectSecretKeyException {
      if (secretKey != _secretKey) {
         throw new IncorrectSecretKeyException();
      }
   }

   /**
    * Sets the specified property to the specified value.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IncorrectSecretKeyException} is thrown. Note that an identity
    * check is done, <em>not</em> an equality check. So
    * the {@link Object#equals(Object)} method is not used, but the
    * <code>==</code> operator is.
    *
    * <p>If <code>value == null</code>, then the property is removed (assuming
    * that the secret key is correct).
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the property to set or reset, cannot be
    *    <code>null</code>.
    *
    * @param value
    *    the value for the property, can be <code>null</code>.
    *
    * @throws IncorrectSecretKeyException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public void set(Object secretKey, String name, String value)
   throws IncorrectSecretKeyException, IllegalArgumentException {

      // Check preconditions
      checkSecretKey(secretKey);
      MandatoryArgumentChecker.check("name", name);

      // Remove the current value
      if (value == null) {
         getPropertiesMap().remove(name);

      // Store a new value
      } else {
         getPropertiesMap().put(name, value);
      }
   }

   /**
    * Removes the specified property. If the property is not found, then
    * nothing happens.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IncorrectSecretKeyException} is thrown. Note that an identity
    * check is done, <em>not</em> an equality check. So
    * the {@link Object#equals(Object)} method is not used, but the
    * <code>==</code> operator is.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the property to remove, cannot be <code>null</code>.
    *
    * @throws IncorrectSecretKeyException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public void remove(Object secretKey, String name)
   throws IncorrectSecretKeyException, IllegalArgumentException {

      // Check preconditions
      checkSecretKey(secretKey);
      MandatoryArgumentChecker.check("name", name);

      // Remove the property
      getPropertiesMap().remove(name);
   }

   /**
    * Removes all properties.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IncorrectSecretKeyException} is thrown. Note that an identity
    * check is done, <em>not</em> an equality check. So
    * the {@link Object#equals(Object)} method is not used, but the
    * <code>==</code> operator is.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @throws IncorrectSecretKeyException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    *
    * @since XINS 1.2.0
    */
   public void clear(Object secretKey)
   throws IncorrectSecretKeyException {
      checkSecretKey(secretKey);
      getPropertiesMap().clear();
   }

   /**
    * Copies all entries from the specified property reader into this one.
    *
    * <p>The correct secret key must be passed. If it is incorrect, then an
    * {@link IncorrectSecretKeyException} is thrown. Note that an identity
    * check is done, <em>not</em> an equality check. So
    * the {@link Object#equals(Object)} method is not used, but the
    * <code>==</code> operator is.
    *
    * @param secretKey
    *    the secret key, must be identity-equal to the secret key passed to
    *    the constructor, cannot be <code>null</code>.
    *
    * @param source
    *    the {@link PropertyReader} containing the key/value pairs to copy to
    *    this object, cannot be <code>null</code>.
    *
    * @throws IncorrectSecretKeyException
    *    if <code>secretKey</code> does not match the secret key passed to the
    *    constructor.
    *
    * @throws IllegalArgumentException
    *    if <code>source == null || source == this</code> or if the specified
    *    {@link PropertyReader} contains an entry which has the name equal to
    *    <code>null</code>.
    *
    * @since XINS 1.2.0
    */
   public void copyFrom(Object secretKey, PropertyReader source)
   throws IncorrectSecretKeyException, IllegalArgumentException {

      // Check preconditions
      checkSecretKey(secretKey);
      MandatoryArgumentChecker.check("source", source);
      if (source == this) {
          throw new IllegalArgumentException("source == this");
      }

      // Copy all entries
      for (String name : source.names()) {
         set(secretKey, name, source.get(name));
      }
   }
}
