/*
 * $Id: ExpiryStrategy.java,v 1.44 2007/09/11 11:51:51 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections.expiry;

import java.lang.ref.WeakReference;

import java.util.ArrayList;

import org.xins.common.Log;
import org.xins.common.Utils;

/**
 * Expiry strategy. A strategy maintains a time-out and a time-out precision.
 *
 * <p>When an <code>ExpiryStrategy</code> is constructed, then an associated
 * thread is immediately constructed and started. This thread <em>must</em>
 * be stopped manually by calling {@link #stop()} as soon as the strategy is
 * no longer used.
 *
 * @version $Revision: 1.44 $ $Date: 2007/09/11 11:51:51 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class ExpiryStrategy {

   /**
    * The fully-qualified name of this class.
    */
   private static final String CLASSNAME = ExpiryStrategy.class.getName();

   /**
    * The number of instances of this class.
    */
   private static int INSTANCE_COUNT;

   /**
    * Lock object for <code>INSTANCE_COUNT</code>.
    */
   private static final Object INSTANCE_COUNT_LOCK = new Object();

   /**
    * The instance number of this instance.
    */
   private final int _instanceNum;

   /**
    * The time-out, in milliseconds.
    */
   private final long _timeOut;

   /**
    * The time-out precision, in milliseconds.
    */
   private final long _precision;

   /**
    * The number of slots that should be used by expiry collections that use
    * this strategy.
    */
   private final int _slotCount;

   /**
    * A textual presentation of this object. This is returned by
    * {@link #toString()}.
    */
   private final String _asString;

   /**
    * The list of folders associated with this strategy.
    */
   private final ArrayList<WeakReference<ExpiryFolder>> _folders;

   /**
    * The timer thread. Not <code>null</code>.
    */
   private final TimerThread _timerThread;

   /**
    * Hash code for this object. The hash code is a constant.
    */
   private final int _hashCode;

   /**
    * Flag that indicates if the time thread should stop or not. Initially
    * <code>false</code>, ofcourse.
    */
   private boolean _stop;

   /**
    * Constructs a new <code>ExpiryStrategy</code> and starts the
    * corresponding thread.
    *
    * @param timeOut
    *    the time-out, in milliseconds.
    *
    * @param precision
    *    the time-out precision, in milliseconds.
    *
    * @throws IllegalArgumentException
    *    if <code>timeOut   &lt; 1L
    *          || precision &lt; 1L
    *          || timeOut   &lt; precision</code>
    */
   public ExpiryStrategy(final long timeOut,
                         final long precision)
   throws IllegalArgumentException {

      // Determine instance number
      synchronized (INSTANCE_COUNT_LOCK) {
         _instanceNum = INSTANCE_COUNT++;
      }

      // Check preconditions
      if (timeOut < 1) {
         String detail = "timeOut (" + timeOut + "L) < 1L";
         Utils.logProgrammingError(detail);
         throw new IllegalArgumentException(detail);

      } else if (precision < 1) {
         String detail = "precision (" + precision + "L) < 1L";
         Utils.logProgrammingError(detail);
         throw new IllegalArgumentException(detail);

      } else if (timeOut < precision) {
         String detail = "timeOut (" + timeOut + "L) < precision (" + precision + "L)";
         Utils.logProgrammingError(detail);
         throw new IllegalArgumentException(detail);
      }

      // Determine number of slots
      long slotCount = timeOut / precision;
      long remainder = timeOut % precision;
      if (remainder != 0L) {
         slotCount++;
      }

      // Initialize fields
      _timeOut   = timeOut;
      _precision = precision;
      _slotCount = (int) slotCount;
      _folders   = new ArrayList<WeakReference<ExpiryFolder>>();
      String constructorDetail = "#" + _instanceNum + " [timeOut=" + timeOut
            + "L; precision=" + precision + "L]";
      _asString  = CLASSNAME + ' ' + constructorDetail;

      // Compute a hash code
      _hashCode = ("" + _timeOut + ":" + _precision).hashCode();

      // Constructed an ExpiryStrategy instance
      Log.log_1409(_instanceNum, _timeOut, _precision);

      // Create and start the timer thread. If no other threads are active,
      // then neither should this timer thread, so do not mark as a daemon
      // thread.
      _timerThread = new TimerThread();
      _timerThread.setDaemon(false);
      _timerThread.start();
   }

   /**
    * Checks whether this object is considered equal to the argument.
    *
    * <p>Two <code>ExpiryStrategy</code> objects are considered equal if they
    * have the same time-out (see {@link #getTimeOut()} and the same precision
    * (see {@link #getPrecision()}.
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
   public boolean equals(Object obj) {

      boolean equal = false;

      if (obj instanceof ExpiryStrategy) {
         ExpiryStrategy that = (ExpiryStrategy) obj;

         equal = that._timeOut   == _timeOut
              && that._precision == _precision;
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
       return _hashCode;
   }

   /**
    * Returns the time-out.
    *
    * @return
    *    the time-out, in milliseconds.
    */
   public long getTimeOut() {
      return _timeOut;
   }

   /**
    * Returns the time-out precision.
    *
    * @return
    *    the time-out precision, in milliseconds.
    */
   public long getPrecision() {
      return _precision;
   }

   /**
    * Returns the number of slots that should be used by expiry collections
    * that use this strategy.
    *
    * @return
    *    the slot count, always &gt;= 1.
    */
   public int getSlotCount() {
      return _slotCount;
   }

   /**
    * Callback method indicating an <code>ExpiryFolder</code> is now
    * associated with this strategy.
    *
    * @param folder
    *    the {@link ExpiryFolder} that is now associated with this strategy,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this strategy was already stopped.
    */
   void folderAdded(final ExpiryFolder folder) throws IllegalStateException {

      // Check state
      if (_stop) {
         throw new IllegalStateException("Already stopped.");
      }

      // Associating expiry folder with expiry stategy thread
      Log.log_1401(folder.getInstanceNum(), folder.getName(), _instanceNum);

      synchronized (_folders) {
         _folders.add(new WeakReference<ExpiryFolder>(folder));
      }
   }

   /**
    * Stops the thread that generates ticks that are passed to the registered
    * expiry folders.
    *
    * @throws IllegalStateException
    *    if this strategy was already stopped.
    */
   public void stop()
   throws IllegalStateException {

      // Check state
      if (_stop) {
         throw new IllegalStateException("Already stopped.");
      }

      // Set the stop flag
      _stop = true;

      // Notify the timer thread
      _timerThread.interrupt();

      // Notify all the associated ExpiryFolder instances that we are stopping
      for (int i = 0; i < _folders.size(); i++) {
         WeakReference<ExpiryFolder> ref = _folders.get(i);
         ExpiryFolder             folder = ref.get();
         if (folder != null) {
            folder.strategyStopped();
         }
      }
   }

   /**
    * Callback method indicating the next tick has taken place. This method is
    * called from (and on) the timer thread.
    */
   private void doTick() {

      // Do nothing if this strategy was already stopped
      if (_stop) {
         return;
      }

      int emptyRefIndex = -1;

      synchronized (_folders) {
         int count = _folders.size();
         for (int i = 0; i < count; i++) {
            WeakReference<ExpiryFolder> ref = _folders.get(i);
            ExpiryFolder             folder = ref.get();
            if (folder != null) {
               folder.tick();
            } else {
               emptyRefIndex = i;
            }
         }

         // Remove last empty WeakReference
         if (emptyRefIndex >= 0) {
            _folders.remove(emptyRefIndex);
         }
      }
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    a textual representation of this object, never <code>null</code>.
    */
   public String toString() {
      return _asString;
   }

   /**
    * Timer thread for an expiry strategy. It calls back the expiry strategy
    * at each so-called 'tick'. The interval between ticks is the precision of
    * the strategy.
    *
    * @version $Revision: 1.44 $ $Date: 2007/09/11 11:51:51 $
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    *
    * @since XINS 1.0.0
    */
   private final class TimerThread extends Thread {

      /**
       * Constructs a new <code>TimerThread</code>.
       */
      public TimerThread() {
         super(ExpiryStrategy.this.toString() + " timer thread");
      }

      /**
       * Runs this thread. The thread keeps running until the expiry strategy
       * is stopped.
       */
      public void run() {

         Log.log_1402(_instanceNum);

         long now  = System.currentTimeMillis();
         long next = now + _precision;

         while (! _stop) {
            boolean interrupted;
            long sleep = next - now;
            if (sleep > 0) {
               Log.log_1404(_instanceNum, sleep);
               try {
                  Thread.sleep(sleep);
                  interrupted = false;

               // Sleep was interrupted
               } catch (InterruptedException exception) {
                  interrupted = true;
               }

               // Determine how much time we spent since we started sleeping
               long after = System.currentTimeMillis();
               long slept = after - now;
               now        = after;

               // Perform logging
               if (interrupted) {
                  Log.log_1405(_instanceNum, slept);
               } else {
                  Log.log_1406(_instanceNum, slept);
               }
            }

            // If we should stop, then exit the loop
            if (_stop) {
               break;
            }

            while (next <= now) {
               Log.log_1407(_instanceNum);
               doTick();
               now = System.currentTimeMillis();
               next += _precision;
            }
         }

         Log.log_1403(_instanceNum);
      }

      /**
       * Returns a textual representation of this timer thread object.
       *
       * @return
       *    a textual representation of this object, never <code>null</code>.
       */
      public String toString() {
         return getName();
      }
   }
}
