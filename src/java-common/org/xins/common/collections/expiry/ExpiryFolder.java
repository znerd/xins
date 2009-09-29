/*
 * $Id: ExpiryFolder.java,v 1.61 2007/09/18 11:21:11 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections.expiry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.text.TextUtils;

/**
 * Expiry folder. Contains values indexed by key. Entries in this folder will
 * expire after a predefined amount of time, unless their lifetime is extended
 * within that timeframe. This is done using the {@link #get(Object)} method.
 *
 * <p>Listeners are supported. Listeners are added using the
 * {@link #addListener(ExpiryListener)} method and removed using the
 * {@link #removeListener(ExpiryListener)} method. If a listener is registered
 * multiple times, it will receive the events multiple times as well. And it
 * will have to be removed multiple times as well.
 *
 * <p>This class is thread-safe.
 *
 * @version $Revision: 1.61 $ $Date: 2007/09/18 11:21:11 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public final class ExpiryFolder {

   /**
    * The name of this class.
    */
   private static final String CLASSNAME = ExpiryFolder.class.getName();

   /**
    * The initial size for the queue of threads waiting to obtain read or
    * write access to a resource.
    */
   private static final int INITIAL_QUEUE_SIZE = 89;

   /**
    * The number of instances of this class.
    */
   private static int INSTANCE_COUNT;

   /**
    * Lock object for <code>INSTANCE_COUNT</code>.
    */
   private static final Object INSTANCE_COUNT_LOCK = new Object();

   /**
    * Lock object.
    */
   private final Object _lock;

   /**
    * The instance number of this instance.
    */
   private final int _instanceNum;

   /**
    * The name of this expiry folder.
    */
   private final String _name;

   /**
    * The strategy used. This field cannot be <code>null</code>.
    */
   private ExpiryStrategy _strategy;

   /**
    * Flag that indicates whether the associated strategy has already stopped.
    * If it has, then this folder becomes invalid.
    */
   private boolean _strategyStopped;

   /**
    * String representation. Cannot be <code>null</code>.
    */
   private final String _asString;

   /**
    * The most recently accessed entries. This field cannot be
    * <code>null</code>. The entries in this map will expire after
    * {@link ExpiryStrategy#getTimeOut()} milliseconds, plus at maximum
    * {@link ExpiryStrategy#getPrecision()} milliseconds.
    */
   private HashMap _recentlyAccessed;

   /**
    * Number of active slots. Always equals
    * {@link #_slots}<code>.length</code>.
    */
   private final int _slotCount;

   /**
    * The index of the last slot. This is always
    * {@link #_slotCount}<code> - 1</code>.
    */
   private final int _lastSlot;

   /**
    * Slots to contain the maps with entries that are not the most recently
    * accessed. The further back in the array, the sooner the entries will
    * expire.
    */
   private HashMap[] _slots;

   /**
    * The set of listeners. May be empty, but never is <code>null</code>.
    */
   private ArrayList _listeners;

   /**
    * Constructs a new <code>ExpiryFolder</code> with the specified name and
    * strategy. When the strategy is stopped (see
    * {@link ExpiryStrategy#stop()} then this folder becomes invalid and can
    * no longer be used.
    *
    * @param name
    *    description of this folder, to be used in log and exception messages,
    *    not <code>null</code>.
    *
    * @param strategy
    *    the strategy that should be applied, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || strategy == null</code>.
    *
    * @throws IllegalStateException
    *    if the strategy is already stopped.
    *
    * @since XINS 1.0.1
    */
   public ExpiryFolder(final String         name,
                       final ExpiryStrategy strategy)
   throws IllegalArgumentException, IllegalStateException {

      // Determine instance number
      synchronized (INSTANCE_COUNT_LOCK) {
         _instanceNum = INSTANCE_COUNT++;
      }

      String constructorDetail = "#" + _instanceNum + " [name=" + TextUtils.quote(name)
            + "; strategy=" + TextUtils.quote(strategy.toString()) + ']';

      // Check arguments
      MandatoryArgumentChecker.check("name", name, "strategy", strategy);

      // Initialize fields
      _lock             = new Object();
      _name             = name;
      _strategy         = strategy;
      _strategyStopped  = false;
      _asString         = CLASSNAME + ' ' + constructorDetail;
      _recentlyAccessed = new HashMap(INITIAL_QUEUE_SIZE);
      _slotCount        = strategy.getSlotCount();
      _slots            = new HashMap[_slotCount];
      _lastSlot         = _slotCount - 1;
      _listeners        = new ArrayList(5);

      // Initialize all slots to a new HashMap
      for (int i = 0; i < _slotCount; i++) {
         _slots[i] = new HashMap(INITIAL_QUEUE_SIZE);
      }

      // Notify the strategy that we listen to it. If the strategy has already
      // stopped, then this will throw an IllegalStateException
      strategy.folderAdded(this);

      // Constructed ExpiryFolder
      Log.log_1408(_instanceNum, _name);
   }

   /**
    * Checks that the associated expiry strategy was not yet stopped. If it
    * was, then an {@link IllegalStateException} is thrown.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} was stopped.
    */
   private void assertStrategyNotStopped()
   throws IllegalStateException {
      if (_strategyStopped) {
         throw new IllegalStateException(
            "The associated ExpiryStrategy has stopped already.");
      }
   }

   /**
    * Callback method, called by the <code>ExpiryStrategy</code> to indicate
    * it was stopped.
    */
   void strategyStopped() {
      synchronized (_lock) {
         _strategyStopped = true;
         _strategy         = null;
         _recentlyAccessed = null;
         _slots            = null;
         _listeners        = null;
      }
   }

   /**
    * Checks whether this object is considered equal to the argument.
    *
    * @param obj
    *    the object to compare with.
    *
    * @return
    *    <code>true</code> if this object is considered equal to
    *    <code>obj</code>, or <code>false</code> otherwise.
    *
    * @see Object#equals(Object)
    */
   public boolean equals(final Object obj) {

      boolean equal = false;

      if (obj instanceof ExpiryFolder) {
         ExpiryFolder that = (ExpiryFolder) obj;

         // Avoid a potential deadlock by always locking on the instance with
         // the lowest instance number first
         Object firstLock  = (_instanceNum < that._instanceNum) ? _lock : that._lock;
         Object secondLock = (_instanceNum < that._instanceNum) ? that._lock : _lock;
         synchronized (firstLock) {
            synchronized (secondLock) {
               if (_strategy.equals(that._strategy)) {
                  if (_name.equals(that._name)) {
                     if (_recentlyAccessed.equals(that._recentlyAccessed)) {
                        equal = true;
                        for (int i = 0; i < _slotCount && equal; i++) {
                           if (! _slots[i].equals(that._slots[i])) {
                              equal = false;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return equal;
   }


   /**
    * Returns a hash code value for the object.
    *
    * @return
    *    a hash code value for this object.
    *
    * @see Object#hashCode()
    * @see #equals(Object)
    */
   public int hashCode() {
       return _strategy.hashCode() & _name.hashCode();
   }

   /**
    * Returns the name given to this expiry folder.
    *
    * @return
    *    the name assigned to this expiry folder, not <code>null</code>.
    */
   public String getName() {
      return _name;
   }

   /**
    * Returns the unique instance number.
    *
    * @return
    *    the unique instance number, which is <code>0</code> for the first
    *    <code>ExpiryFolder</code> instance, <code>1</code> for the second,
    *    etc.
    */
   int getInstanceNum() {
      return _instanceNum;
   }

   /**
    * Notifies this map that the precision time frame has passed since the
    * last tick.
    *
    * <p>Entries that are expirable may be removed from this folder.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    */
   void tick() throws IllegalStateException {

      // Check state
      assertStrategyNotStopped();

      HashMap toBeExpired;
      HashMap refMap = null;
      synchronized (_lock) {

         // Shift the slots
         toBeExpired = _slots[_lastSlot];
         for (int i = _lastSlot; i > 0; i--) {
            _slots[i] = _slots[i - 1];
         }
         _slots[0] = _recentlyAccessed;

         // Removed the entries expired in the last slot
         if (!_slots[_lastSlot].isEmpty()) {
            Iterator iterator = _slots[_lastSlot].entrySet().iterator();
            while (iterator.hasNext()) {

               // Get the next Map.Entry from the iterator
               Map.Entry me = (Map.Entry) iterator.next();

               // Determine key and entry object
               Object key   = me.getKey();
               Entry  entry = (Entry) me.getValue();
               if (entry.isExpired()) {
                  iterator.remove();
                  if (refMap == null) {
                     refMap = new HashMap();
                  }
                  refMap.put(key, entry.getReference());
               }
            }
         }

         // Copy all references from the wrapping Entry objects
         if (!toBeExpired.isEmpty()) {
            Iterator iterator = toBeExpired.entrySet().iterator();
            while (iterator.hasNext()) {

               // Get the next Map.Entry from the iterator
               Map.Entry me = (Map.Entry) iterator.next();

               // Get the key and the entry
               Object key   = me.getKey();
               Entry  entry = (Entry) me.getValue();

               if (entry.isExpired()) {

                  // Create a map for the object references, if necessary
                  if (refMap == null) {
                     refMap = new HashMap();
                  }

                  // Store the entry that needs expiring in the refMap
                  refMap.put(key, entry.getReference());
               } else {
                  String detail = "Entry marked for expiry should "
                        + "have expired. Key as string is \""
                        + entry.getReference().toString() + "\".";
                  Utils.logProgrammingError(detail);
               }
            }
         }

         // Recycle the old HashMap
         toBeExpired.clear();
         _recentlyAccessed = toBeExpired;
      }

      // Determine how may objects are to be sent to the listeners
      int refMapSize       = refMap == null
                           ? 0
                           : refMap.size();

      // Log this
      Log.log_1400(_instanceNum, _name, refMapSize);

      // If set of objects for listeners is empty, then short-circuit
      if (refMapSize < 1 || _listeners.size() < 1) {
         return;
      }

      // XXX: Should we do this in separate thread(s) ?

      // Get a copy of the list of listeners
      synchronized (_listeners) {

         // If appropriate, notify the listeners
         if (refMap != null && refMap.size() > 0) {
            Map unmodifiableExpired = Collections.unmodifiableMap(refMap);
            int listenerCount = _listeners.size();
            for (int i = 0; i < listenerCount; i++) {
               ExpiryListener listener = (ExpiryListener) _listeners.get(i);
               listener.expired(this, unmodifiableExpired);
            }
         }
      }
   }

   /**
    * Adds the specified object as a listener for expiry events.
    *
    * @param listener
    *    the listener to be registered, cannot be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>listener == null</code>.
    */
   public void addListener(final ExpiryListener listener)
   throws IllegalStateException, IllegalArgumentException {

      // Check state
      assertStrategyNotStopped();

      // Check arguments
      MandatoryArgumentChecker.check("listener", listener);

      synchronized (_listeners) {
         _listeners.add(listener);
      }
   }

   /**
    * Removes the specified object as a listener for expiry events.
    *
    * <p>If the listener cannot be found, then nothing happens.
    *
    * @param listener
    *    the listener to be unregistered, cannot be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>listener == null</code>.
    */
   public void removeListener(final ExpiryListener listener)
   throws IllegalStateException, IllegalArgumentException {

      // Check state
      assertStrategyNotStopped();

      // Check arguments
      MandatoryArgumentChecker.check("listener", listener);

      synchronized (_listeners) {
         _listeners.remove(listener);
      }
   }

   /**
    * Determines the number of non-expired entries in the specified
    * <code>HashMap</code>. If any entries are expired, they will be removed.
    *
    * @param map
    *    the map in which the non-expired entries are counted.
    *
    * @return
    *    the size of the specified map, always &gt;= 0.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    */
   private int sizeOf(final Map map) throws IllegalStateException {

      // Check state
      assertStrategyNotStopped();

      int size = 0;

      synchronized (_lock) {
         Iterator iterator = map.entrySet().iterator();
         while (iterator.hasNext()) {

            // Get the next Map.Entry from the iterator
            Map.Entry me = (Map.Entry) iterator.next();

            // Get the entry
            Entry  entry = (Entry) me.getValue();
            if (!entry.isExpired()) {
               size++;
            }
         }
      }

      return size;
   }

   /**
    * Gets the number of entries.
    *
    * @return
    *    the number of entries in this expiry folder, always &gt;= 0.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    */
   public int size()
   throws IllegalStateException {

      // Check state
      assertStrategyNotStopped();

      synchronized (_lock) {

         int size = sizeOf(_recentlyAccessed);
         for (int i = 0; i < _slotCount; i++) {
            size += sizeOf(_slots[i]);
         }

         return size;
      }
   }

   /**
    * Gets the value associated with a key and extends the lifetime of the
    * matching entry, if there was a match.
    *
    * <p>The more recently the specified entry was accessed, the faster the
    * lookup.
    *
    * @param key
    *    the key to lookup, cannot be <code>null</code>.
    *
    * @return
    *    the value associated with the specified key, or <code>null</code> if
    *    and only if this folder does not contain an entry with the specified
    *    key.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public Object get(final Object key)
   throws IllegalStateException, IllegalArgumentException {

      // Check state
      assertStrategyNotStopped();

      // Check arguments
      MandatoryArgumentChecker.check("key", key);

      // Search in the recently accessed map first
      Entry entry;
      synchronized (_lock) {
         entry = (Entry) _recentlyAccessed.get(key);

         // Entry found in recently accessed
         if (entry != null) {

            // Entry is already expired
            if (entry.isExpired()) {
               return null;

            // Entry is not expired, touch it and return the reference
            } else {
               entry.touch();
               return entry.getReference();
            }

         // Not found in recently accessed, look in slots
         } else {

            // Go through all slots
            for (int i = 0; i < _slotCount; i++) {
               entry = (Entry) _slots[i].remove(key);

               if (entry != null) {

                  // Entry is already expired, update the map and size and
                  // return null
                  if (entry.isExpired()) {
                     return null;

                  // Entry is not expired, touch it, store in the recently
                  // accessed and return the reference
                  } else {
                     entry.touch();
                     _recentlyAccessed.put(key, entry);
                     return entry.getReference();
                  }
               }
            }

            // Nothing found in any of the slots
            return null;
         }
      }
   }

   /**
    * Finds the value associated with a key. The lifetime of the matching
    * entry is not extended.
    *
    * <p>The more recently the specified entry was accessed, the faster the
    * lookup.
    *
    * @param key
    *    the key to lookup, cannot be <code>null</code>.
    *
    * @return
    *    the value associated with the specified key, or <code>null</code> if
    *    and only if this folder does not contain an entry with the specified
    *    key.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public Object find(final Object key)
   throws IllegalStateException, IllegalArgumentException {

      // Check state
      assertStrategyNotStopped();

      // Check arguments
      MandatoryArgumentChecker.check("key", key);

      Object value;

      // Search in the recently accessed map first
      synchronized (_lock) {
         value = _recentlyAccessed.get(key);

         // If not found, then look in the slots
         if (value == null) {
            for (int i = 0; i < _slotCount && value == null; i++) {
               value = _slots[i].get(key);
            }
         }
      }

      if (value == null) {
         return null;
      }

      Entry entry = (Entry) value;
      if (entry.isExpired()) {
         return null;
      } else {
         return entry.getReference();
      }
   }

   /**
    * Associates the specified key with the specified value.
    *
    * @param key
    *    they key for the entry, cannot be <code>null</code>.
    *
    * @param value
    *    they value for the entry, cannot be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null || value == null</code>.
    */
   public void put(final Object key, final Object value)
   throws IllegalStateException, IllegalArgumentException {

      // Check state
      assertStrategyNotStopped();

      // Check arguments
      MandatoryArgumentChecker.check("key", key, "value", value);

      // Store the association in the set of recently accessed entries
      synchronized (_lock) {
         Entry entry = new Entry(value);
         _recentlyAccessed.put(key, entry);
      }
   }

   /**
    * Removes the specified key from this folder.
    *
    * @param key
    *    the key for the entry, cannot be <code>null</code>.
    *
    * @return
    *    the old value associated with the specified key, or <code>null</code>
    *    if and only if this folder does not contain an entry with the
    *    specified key.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public Object remove(final Object key)
   throws IllegalStateException, IllegalArgumentException {

      // Check state
      assertStrategyNotStopped();

      // Check arguments
      MandatoryArgumentChecker.check("key", key);

      Object value;

      // Remove the key in the set of recently accessed entries
      synchronized (_recentlyAccessed) {
         value = _recentlyAccessed.remove(key);
      }

      // If not found, then look in the slots
      if (value == null) {
         synchronized (_slots) {
            for (int i = 0; i < _slotCount && value == null; i++) {
               value = _slots[i].remove(key);
            }
         }
      }

      if (value == null) {
         return null;
      }

      Entry entry = (Entry) value;
      if (entry.isExpired()) {
         return null;
      } else {
         return entry.getReference();
      }
   }

   /**
    * Copies the entries of this <code>ExpiryFolder</code> into another one.
    * This method does not perform a deep copy, so if a key is added or
    * removed, both folders will be modified.
    *
    * @param newFolder
    *    the new folder where the entries should be copied into,
    *    cannot be <code>null</code>, cannot be <code>this</code>.
    *
    * @throws IllegalStateException
    *    if the associated {@link ExpiryStrategy} has stopped already.
    *
    * @throws IllegalArgumentException
    *    if <code>newFolder == null</code> or <code>newFolder == this</code>
    *    or if the precision of <code>newFolder</code> is not the same as for
    *    this <code>ExpiryFolder</code>.
    */
   public void copy(final ExpiryFolder newFolder)
   throws IllegalStateException, IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("newFolder", newFolder);
      if (newFolder == this) {
         String detail = "Folder can not be copied into itself.";
         Utils.logProgrammingError(detail);
         throw new IllegalArgumentException(detail);
      }
      if (newFolder._strategy.getPrecision() != _strategy.getPrecision()) {
         String detail = "Folders must have the same precision.";
         Utils.logProgrammingError(detail);
         throw new IllegalArgumentException(detail);
      }

      // Avoid a potential deadlock by always locking on the instance with the
      // lowest instance number first
      Object firstLock  = (_instanceNum < newFolder._instanceNum) ? _lock : newFolder._lock;
      Object secondLock = (_instanceNum < newFolder._instanceNum) ? newFolder._lock : _lock;
      synchronized (firstLock) {
         synchronized (secondLock) {

            // Copy the recentlyAccessed
            newFolder._recentlyAccessed = new HashMap(_recentlyAccessed);

            // Copy the slots
            for (int i = 0; i < _slotCount && i < newFolder._slotCount; i++) {
               newFolder._slots[i] = new HashMap(_slots[i]);
            }
         }
      }
   }

   /**
    * Returns the strategy associated with this folder.
    *
    * @return
    *    the strategy, never <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the associated strategy has already stopped.
    */
   public ExpiryStrategy getStrategy()
   throws IllegalStateException {

      // Check state
      assertStrategyNotStopped();

      return _strategy;
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    a textual representation of this <code>ExpiryFolder</code>, which
    *    includes the name.
    */
   public String toString() {
      return _asString;
   }

   /**
    * Entry in an expiry folder. Combination of the referenced object and a
    * timestamp. The timestamp indicates when the object should be expired.
    *
    * @version $Revision: 1.61 $ $Date: 2007/09/18 11:21:11 $
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   private class Entry {
      /**
       * Constructs a new <code>Entry</code>.
       *
       * @param reference
       *    reference to the object, should not be <code>null</code> (although
       *    it is not checked).
       */
      private Entry(final Object reference) {
         _reference = reference;
         touch();
      }

      /**
       * Reference to the object. Should not be <code>null</code>.
       */
      private final Object _reference;

      /**
       * The time at which this entry should expire.
       */
      private long _expiryTime;

      /**
       * Returns a hash code value for the object.
       *
       * @return
       *    a hash code value for this object.
       *
       * @see Object#hashCode()
       * @see #equals(Object)
       */
      public int hashCode() {
         return _reference.hashCode();
      }

      /**
       * Checks whether this object is considered equal to the argument.
       *
       * @param obj
       *    the object to compare with.
       *
       * @return
       *    <code>true</code> if this object is considered equal to
       *    <code>obj</code>, or <code>false</code> otherwise.
       *
       * @see Object#equals(Object)
       */
      public boolean equals(final Object obj) {

         boolean equal;

         if (obj instanceof Entry) {
            Entry that = (Entry) obj;
            equal = _reference.equals(that._reference);
         } else {
            equal = false;
         }

         return equal;
      }

      /**
       * Retrieves the reference to the object.
       *
       * @return
       *    the reference to the object, should not be <code>null</code>.
       */
      public Object getReference() {
         return _reference;
      }

      /**
       * Checks if this entry is expired.
       *
       * @return
       *    <code>true</code> if this entry is expired, <code>false</code>
       *    otherwise.
       */
      public boolean isExpired() {
         return System.currentTimeMillis() >= _expiryTime;
      }

      /**
       * Touches this entry, resetting the expiry time.
       */
      public void touch() {
         _expiryTime = System.currentTimeMillis() + _strategy.getTimeOut();
      }
   }
}
