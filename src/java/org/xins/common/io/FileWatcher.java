/*
 * $Id: FileWatcher.java,v 1.48 2007/09/18 11:21:09 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.io;

import java.io.File;
import java.util.HashMap;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

/**
 * File watcher thread.
 *
 * <p>This thread monitors one or more files, checking them at preset
 * intervals. A listener is notified of the findings.
 *
 * <p>The check is performed every <em>n</em> seconds,
 * where the interval <em>n</em> can be configured
 * (see {@link #setInterval(int)} and {@link #getInterval()}).
 *
 * <p>Initially this thread will be a daemon thread. This can be changed by
 * calling {@link #setDaemon(boolean)}.
 *
 * @version $Revision: 1.48 $ $Date: 2007/09/18 11:21:09 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class FileWatcher extends Thread {

   /**
    * Instance counter, by class name. The keys in this map are
    * <code>String</code> objects, while the values are <code>Integer</code>
    * objects.
    * Used to generate a unique ID for each instance.
    */
   private static HashMap<String,Integer> INSTANCE_COUNTERS = new HashMap<String,Integer>();

   /**
    * State in which this file watcher thread is not running.
    */
   private static final int NOT_RUNNING = 1;

   /**
    * State in which this file watcher thread is currently running and has not
    * been told to stop.
    */
   private static final int RUNNING = 2;

   /**
    * State in which this file watcher thread is currently running, but has
    * been told to stop.
    */
   private static final int SHOULD_STOP = 3;

   /**
    * Fully-qualified name of this class.
    */
   private final String _className;

   /**
    * Unique instance identifier.
    */
   private final int _instanceID;

   /**
    * The number of checks completely executed, including the listener call.
    * Initially 0 and becomes 1 after the listener is called for the first 
    * time.
    */
   private int _checkCount;

   /**
    * Lock object for <code>_checkCount</code>.
    */
   private Object _checkCountLock;

   /**
    * The files to watch. Not <code>null</code>.
    */
   private File[] _files;

   /**
    * The string representation of the files to watch. Not <code>null</code>.
    */
   protected String _filePaths;

   /**
    * Delay in seconds, at least 1. When the interval is uninitialized, the
    * value of this field is less than 1.
    */
   private int _interval;

   /**
    * The listener. Not <code>null</code>
    */
   private final Listener _listener;

   /**
    * Timestamp of the last modification of the file. The value
    * <code>-1L</code> indicates that the file could not be found the last
    * time this was checked.
    *
    * <p>Initially this field is <code>-1L</code>.
    */
   protected long _lastModified = -1L;

   /**
    * Current state. Never <code>null</code>. Value is one of the following
    * values:
    *
    * <ul>
    *    <li>{@link #NOT_RUNNING}
    *    <li>{@link #RUNNING}
    *    <li>{@link #SHOULD_STOP}
    * </ul>
    *
    * <p>Once the thread is stopped, the state will be changed to
    * {@link #NOT_RUNNING} again.
    */
   private int _state;

   /**
    * Indication of when the first check should be performed.
    * Either at construction ({@link InitialCheckPolicy#AT_CONSTRUCTION}) 
    * or when the thread is started
    * ({@link InitialCheckPolicy#AT_THREAD_START}).
    * Never <code>null</code>.
    */
   private final InitialCheckPolicy _initialCheckPolicy;

   /**
    * Creates a new <code>FileWatcher</code> for the specified file.
    *
    * <p>The interval must be set before the thread can be started.
    *
    * <p>The initial check will be performed at construction
    * (see {@link InitialCheckPolicy#AT_CONSTRUCTION}), which is compatible 
    * with XINS 2.1 and before.
    *
    * @param file
    *    the name of the file to watch, cannot be <code>null</code>.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file == null || listener == null</code>
    *
    * @since XINS 1.2.0
    */
   public FileWatcher(String file, Listener listener)
   throws IllegalArgumentException {
      this(file, listener, InitialCheckPolicy.AT_CONSTRUCTION);
   }

   /**
    * Creates a new <code>FileWatcher</code> for the specified file, with the 
    * specified initial check policy.
    *
    * <p>The interval must be set before the thread can be started.
    *
    * @param file
    *    the name of the file to watch, cannot be <code>null</code>.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @param initialCheckPolicy
    *    indication of when the first check should be performed,
    *    either at construction ({@link InitialCheckPolicy#AT_CONSTRUCTION}) 
    *    or when the thread is started
    *    ({@link InitialCheckPolicy#AT_THREAD_START}).
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file               == null
    *          || listener           == null
    *          || initialCheckPolicy == null</code>
    *
    * @since XINS 2.2
    */
   public FileWatcher(String             file,
                      Listener           listener,
                      InitialCheckPolicy initialCheckPolicy)
   throws IllegalArgumentException {
      this(file, 0, listener, initialCheckPolicy);
   }

   /**
    * Creates a new <code>FileWatcher</code> for the specified file, with the
    * specified interval.
    *
    * <p>The initial check will be performed at construction
    * (see {@link InitialCheckPolicy#AT_CONSTRUCTION}), which is compatible 
    * with XINS 2.1 and before.
    *
    * @param file
    *    the name of the file to watch, cannot be <code>null</code>.
    *
    * @param interval
    *    the interval in seconds, must be greater than or equal to 0.
    *    if the interval is 0 the interval must be set before the thread can
    *    be started.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file     == null
    *          || interval &lt; 0
    *          || listener == null</code>.
    */
   public FileWatcher(String file, int interval, Listener listener)
   throws IllegalArgumentException {
      this(new String[]{file}, interval, listener);
   }

   /**
    * Creates a new <code>FileWatcher</code> for the specified file, with the
    * specified interval and initial check policy.
    *
    * @param file
    *    the name of the file to watch, cannot be <code>null</code>.
    *
    * @param interval
    *    the interval in seconds, must be greater than or equal to 0.
    *    if the interval is 0 the interval must be set before the thread can
    *    be started.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @param initialCheckPolicy
    *    indication of when the first check should be performed,
    *    either at construction ({@link InitialCheckPolicy#AT_CONSTRUCTION}) 
    *    or when the thread is started
    *    ({@link InitialCheckPolicy#AT_THREAD_START}).
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file               == null
    *          || interval           &lt; 0
    *          || listener           == null
    *          || initialCheckPolicy == null</code>
    *
    * @since XINS 2.2
    */
   public FileWatcher(String             file,
                      int                interval,
                      Listener           listener,
                      InitialCheckPolicy initialCheckPolicy)
   throws IllegalArgumentException {
      this(new String[]{file}, interval, listener, initialCheckPolicy);
   }

   /**
    * Creates a new <code>FileWatcher</code> for the specified set of files,
    * with the specified interval.
    *
    * <p>The initial check will be performed at construction
    * (see {@link InitialCheckPolicy#AT_CONSTRUCTION}), which is compatible 
    * with XINS 2.1 and before.
    *
    * @param files
    *    the name of the files to watch, cannot be <code>null</code>.
    *    It should also have at least one file and none of the file should be <code>null</code>.
    *
    * @param interval
    *    the interval in seconds, must be greater than or equal to 0.
    *    if the interval is 0 the interval must be set before the thread can
    *    be started.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>files             == null
    *          || files.length      &lt; 1
    *          || files[<em>n</em>] == null
    *          || interval          &lt; 0
    *          || listener          == null</code>
    *    (where <code>0 &lt;= <em>n</em> &lt; files.length</code>).
    *
    * @since XINS 2.1
    */
   public FileWatcher(String[] files, int interval, Listener listener)
   throws IllegalArgumentException {
      this(files, interval, listener, InitialCheckPolicy.AT_CONSTRUCTION);
   }

   /**
    * Creates a new <code>FileWatcher</code> for the specified set of files,
    * with the specified interval.
    *
    * @param files
    *    the name of the files to watch, cannot be <code>null</code>.
    *    It should also have at least one file and none of the file should be <code>null</code>.
    *
    * @param interval
    *    the interval in seconds, must be greater than or equal to 0.
    *    if the interval is 0 the interval must be set before the thread can
    *    be started.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @param initialCheckPolicy
    *    indication of when the first check should be performed,
    *    either at construction ({@link InitialCheckPolicy#AT_CONSTRUCTION}) 
    *    or when the thread is started
    *    ({@link InitialCheckPolicy#AT_THREAD_START}).
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>files              == null
    *          || files.length       &lt; 1
    *          || files[<em>n</em>]  == null
    *          || interval           &lt; 0
    *          || listener           == null
    *          || initialCheckPolicy == null</code>
    *    (where <code>0 &lt;= <em>n</em> &lt; files.length</code>).
    *
    * @since XINS 2.2
    */
   public FileWatcher(String[]           files,
                      int                interval,
                      Listener           listener,
                      InitialCheckPolicy initialCheckPolicy)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("files", files, "listener", listener);
      if (interval < 0) {
         throw new IllegalArgumentException("interval (" + interval + ") < 0");
      } else if (files.length < 1) {
         throw new IllegalArgumentException("At least one file should be specified.");
      }
      for (int i = 0; i < files.length; i++) {
         if (files[i] == null) {
            throw new IllegalArgumentException("The file specified at index " + i + " is null.");
         }
      }

      _className = getClass().getName();

      // Determine the unique instance ID
      int instanceID;
      synchronized (INSTANCE_COUNTERS) {
         Integer instanceCounter = (Integer) INSTANCE_COUNTERS.get(_className);
         instanceID = (instanceCounter == null) ? 0 : instanceCounter.intValue() + 1;
         INSTANCE_COUNTERS.put(_className, new Integer(instanceID));
      }

      // Initialize the fields
      _instanceID         = instanceID;
      _checkCountLock     = new Object();
      storeFiles(files);
      _interval           = interval;
      _listener           = listener;
      _state              = NOT_RUNNING;
      _initialCheckPolicy = initialCheckPolicy;

      // Configure thread as daemon
      setDaemon(true);

      // Set the name of this thread
      configureThreadName();

      // Immediately check if the file can be read from,
      // if that is the correct policy
      if (initialCheckPolicy == InitialCheckPolicy.AT_CONSTRUCTION) {
         firstCheck();
      }
   }

   /**
    * Stores the files in an instance field.
    *
    * @param fileNames
    *    the files to check, as {@link String}s,
    *    cannot be <code>null</code>.
    *
    * @since XINS 2.1
    */
   protected void storeFiles(String[] fileNames) {

      // Initialize the field that will store all File objects
      _files = new File[fileNames.length];

      // Set the first element in that field
      _files[0] = new File(fileNames[0]);

      // Determine the base dir for the first file
      File baseDir = _files[0].getParentFile();

      // Prepare the text string with the conatenated file paths
      _filePaths = _files[0].getPath();

      // Process all successive file names, using the base directory of the
      // first one
      for (int i = 1; i < fileNames.length; i++) {
         _files[i] = new File(baseDir, fileNames[i]);
         _filePaths += ";" + _files[i].getPath();
      }
   }

   /**
    * Configures the name of this thread.
    */
   private synchronized void configureThreadName() {
      String name = _className
                  + " #" + _instanceID
                  + " [file(s)=\"" + _filePaths
                  + "\"; interval=" + _interval + " second(s)]";
      setName(name);
   }

   /**
    * Performs the first check on the file to determine the date the file was
    * last modified. This method is called from the constructors.
    */
   protected void firstCheck() {

      for (int i = 0; i < _files.length; i++) {
         File file = _files[i];
         try {
            if (file.canRead()) {
               _lastModified = Math.max(_lastModified, file.lastModified());
               // XXX no event should be fired at start-up _listener.fileFound();
            } else {
               // XXX no event should be fired at start-up _listener.fileNotFound();
            }

         // Ignore a SecurityException
         } catch (SecurityException exception) {
            _listener.securityException(exception);
         }
      }

      // Update the check counter (becomes 1)
      synchronized (_checkCountLock) {
         _checkCount = 1;
      }
   }

   /**
    * Runs this thread. This method should not be called directly, call
    * {@link #start()} instead. That method will call this method.
    *
    * @throws IllegalStateException
    *    if <code>{@link Thread#currentThread()} != this</code>, if the thread
    *    is already running or should stop, or if the interval was not set
    *    yet.
    */
   public void run() throws IllegalStateException {

      int interval;
      int state;

      synchronized (this) {
         interval = _interval;
         state    = _state;
      }

      // Check preconditions
      if (Thread.currentThread() != this) {
         throw new IllegalStateException("Thread.currentThread() != this");
      } else if (state == RUNNING) {
         throw new IllegalStateException("Thread already running.");
      } else if (state == SHOULD_STOP) {
         throw new IllegalStateException("Thread should stop running.");
      } else if (interval < 1) {
         throw new IllegalStateException("Interval has not been set yet.");
      }

      Log.log_1200(_instanceID, _filePaths, interval);

      // Move to the RUNNING state
      synchronized (this) {
         _state = RUNNING;
      }

      // Check if the file can be read from,
      // if that was not done during construction
      if (_initialCheckPolicy == InitialCheckPolicy.AT_THREAD_START) {
         firstCheck();
      }

      // Loop while we should keep running
      boolean shouldStop = false;
      while (! shouldStop) {

         synchronized (this) {
            try {

               // Wait for the designated amount of time
               wait(((long) interval) * 1000L);

            } catch (InterruptedException exception) {
               // The thread has been notified
            }

            // Should we stop?
            shouldStop = (_state != RUNNING);
         }

         // If we do not have to stop yet, check if the file changed
         if (! shouldStop) {
            check();

            // Update the check counter
            synchronized (_checkCountLock) {
               _checkCount++;
            }
         }
      }

      // Thread stopped
      Log.log_1203(_instanceID, _filePaths);
   }

   /**
    * Returns the current interval.
    *
    * @return interval
    *    the current interval in seconds, always greater than or equal to 1,
    *    except if the interval is not initialized yet, in which case 0 is
    *    returned.
    */
   public synchronized int getInterval() {
      return _interval;
   }

   /**
    * Changes the file check interval.
    *
    * @param newInterval
    *    the new interval in seconds, must be greater than or equal to 1.
    *
    * @throws IllegalArgumentException
    *    if <code>interval &lt; 1</code>
    */
   public synchronized void setInterval(int newInterval)
   throws IllegalArgumentException {

      // Check preconditions
      if (newInterval < 1) {
         throw new IllegalArgumentException(
            "newInterval (" + newInterval + ") < 1");
      }

      // Change the interval
      if (newInterval != _interval) {
         Log.log_1201(_instanceID, _filePaths, _interval, newInterval);
         _interval = newInterval;
      }

      // Update the thread name
      configureThreadName();

      // TODO: Interrupt the thread (see #HERE#)
   }

   /**
    * Stops this thread.
    *
    * @throws IllegalStateException
    *    if the thread is currently not running or already stopping.
    */
   public synchronized void end() throws IllegalStateException {

      // Check state
      if (_state == NOT_RUNNING) {
         throw new IllegalStateException("Thread currently not running.");
      } else if (_state == SHOULD_STOP) {
         throw new IllegalStateException("Thread already stopping.");
      }

      Log.log_1202(_instanceID, _filePaths);

      // Change the state and interrupt the thread
      _state = SHOULD_STOP;
      this.interrupt();
   }

   /**
    * Checks if the file changed. The following algorithm is used:
    *
    * <ul>
    *    <li>check if the file is readable;
    *    <li>if so, then determine when the file was last modified;
    *    <li>if either the file existence check or the file modification check
    *        causes a {@link SecurityException} to be thrown, then
    *        {@link Listener#securityException(SecurityException)} is called
    *        and the method returns;
    *    <li>otherwise if the file is not readable (it may not exist), then
    *        {@link Listener#fileNotFound()} is called and the method returns;
    *    <li>otherwise if the file is readable, but previously was not,
    *        then {@link Listener#fileFound()} is called and the method
    *        returns;
    *    <li>otherwise if the file was modified, then
    *        {@link Listener#fileModified()} is called and the method returns;
    *    <li>otherwise the file was not modified, then
    *        {@link Listener#fileNotModified()} is called and the method
    *        returns.
    * </ul>
    *
    * @since XINS 1.2.0
    */
   public synchronized void check() {

      // Variable to store the file modification timestamp in. The value -1
      // indicates the file does not exist.
      long lastModified = 0L;

      // Check if the file can be read from and if so, when it was last
      // modified
      try {
         lastModified = getLastModified();

      // Authorisation problem; our code is not allowed to call canRead()
      // and/or lastModified() on the File object
      } catch (SecurityException securityException) {

         // Notify the listener
         try {
            _listener.securityException(securityException);

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }

         // Short-circuit
         return;
      }

      // A least one file can not be found
      if (lastModified == -1L) {

         // Set _lastModified to -1, which indicates the file did not exist
         // last time it was checked.
         _lastModified = -1L;

         // Notify the listener
         try {
            _listener.fileNotFound();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }

      // Previously a file could not be found, but now it can
      } else if (_lastModified == -1L) {

         // Update the field that stores the last known modification date
         _lastModified = lastModified;

         // Notify the listener
         try {
            _listener.fileFound();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }

      // At least one file has been modified
      } else if (lastModified != _lastModified) {

         // Update the field that stores the last known modification date
         _lastModified = lastModified;

         // Notify listener
         try {
            _listener.fileModified();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }

      // None of the files has not been modified
      } else {

         // Notify listener
         try {
            _listener.fileNotModified();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable exception) {
            Utils.logIgnoredException(exception);
         }
      }
   }

   /**
    * Gets the time at which the last file was modified.
    * If, for any reason, at least one of the files could not be read then
    * <code>-1L</code> is returned.
    *
    * @return
    *    the time of the last modified file, or <code>-1L</code>.
    *
    * @throws SecurityException
    *    if one of the files could not be read because of a security
    *    restriction.
    */
   protected long getLastModified() throws SecurityException {
      long lastModified = 0L;
      for (int i = 0; i < _files.length; i++) {
         File file = _files[i];
         if (file.canRead()) {
            lastModified = Math.max(lastModified, file.lastModified());
         } else {
            return -1L;
         }
      }
      return lastModified;
   }

   /**
    * Determines how often the file was checked. Initially the file is checked
    * 0 times, which becomes 1 <em>after</em> the listener has been notified
    * for the very first time.
    *
    * @return
    *    the number of times the file(s) has/have been checked, initially 0.
    *
    * @since XINS 2.2
    */
   public int getCheckCount() {
      synchronized (_checkCountLock) {
         return _checkCount;
      }
   }

   /**
    * Interface for file watcher listeners.
    *
    * <p>Note that exceptions thrown by these callback methods will be ignored
    * by the <code>FileWatcher</code>.
    *
    * @version $Revision: 1.48 $ $Date: 2007/09/18 11:21:09 $
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    *
    * @since XINS 1.0.0
    */
   public interface Listener {

      /**
       * Callback method, called if the file is checked but cannot be found.
       * This method is called the first time the file is determined not to
       * exist, but also each consecutive time the file is still determined
       * not to be found.
       */
      void fileNotFound();

      /**
       * Callback method, called if the file is found for the first time since
       * the <code>FileWatcher</code> was started. Each consecutive time the
       * file still exists (and is readable), either
       * {@link #fileModified()} or {@link #fileNotModified()} is called.
       */
      void fileFound();

      /**
       * Callback method, called if an authorisation error prevents that the
       * file is checked for existence and last modification date.
       *
       * @param exception
       *    the caught exception, not <code>null</code>.
       */
      void securityException(SecurityException exception);

      /**
       * Callback method, called if the file was checked and found to be
       * modified.
       */
      void fileModified();

      /**
       * Callback method, called if the file was checked but found not to be
       * modified.
       */
      void fileNotModified();
   }

   /**
    * The policy for executing the initial check, either immediately or when 
    * the thread is started.
    *
    * <p>When the XINS framework starts supporting Java 5+ features, then this 
    * class will be replaced by an enum in a manner that only a recompilation 
    * is needed.
    *
    * @version $Revision$ $Date$
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    *
    * @since XINS 2.2
    */
   public static class InitialCheckPolicy {

      /**
       * The constant that indicates that the first check should be performed 
       * immediately, when the <code>FileWatcher</code> is constructed.
       */
      public static final InitialCheckPolicy AT_CONSTRUCTION = new InitialCheckPolicy("AT_CONSTRUCTION");

      /**
       * The constant that indicates that the first check should be performed 
       * when the <code>FileWatcher</code> thread is started.
       */
      public static final InitialCheckPolicy AT_THREAD_START = new InitialCheckPolicy("AT_THREAD_START");

      /**
       * The name of this constant. Never <code>null</code>.
       */
      private final String _name;

      /**
       * Constructs a new <code>InitialCheckPolicy</code> with the specified 
       * name.
       *
       * @param name
       *    the name of the constant, cannot be <code>null</code>.
       */
      private InitialCheckPolicy(String name)
      throws IllegalArgumentException {
         MandatoryArgumentChecker.check("name", name);
         _name = name;
      }

      /**
       * Returns the name of this constant.
       *
       * @return
       *    the name of the constant, never <code>null</code>.
       */
      public String name() {
         return _name;
      }

      /**
       * Returns the name of this constant.
       *
       * @return
       *    the name of the constant, never <code>null</code>.
       */
      public String toString() {
         return _name;
      }

      /**
       * Returns a hash code for this constant.
       *
       * @return
       *    the hash code.
       */
      public int hashCode() {
         return _name.hashCode();
      }

      /**
       * Returns true if the specified object is equal to this constant.
       *
       * @return
       *    <code>true</code> if this object and the specified object are 
       *    considered equal, <code>false</code> if they are considered 
       *    different.
       */
      public boolean equals(Object o) {
         if (o == null || (! (o instanceof InitialCheckPolicy))) {
            return false;
         }

         InitialCheckPolicy that = (InitialCheckPolicy) o;
         return _name.equals(that._name);
      }
   }
}
