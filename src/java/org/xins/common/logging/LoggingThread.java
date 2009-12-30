/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.logging;

import org.apache.log4j.NDC;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Thread that may perform some Log4J-based logging. The Log4J logging context
 * (see class {@link NDC} is automatically passed from the invoking thread to
 * this one.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public abstract class LoggingThread extends Thread {

   /**
    * Constructs a new <code>LoggingThread</code>.
    */
   protected LoggingThread() {
      // empty
   }

   /**
    * Flag that indicates whether this thread is already started.
    */
   private boolean _started;

   /**
    * The logging context, or <code>null</code> if there is none.
    * This field is initialized in {@link #start()}.
    */
   private String _loggingContext;


   @Override
   public final void start() {

      // Check preconditions
      if (_started) {
         throw new IllegalThreadStateException("This thread is already started.");
      }

      // Now it's started
      _started = true;
      
      // Get the current logging context, if any
      _loggingContext = NDC.peek();

      // Actually start the thread
      super.start();
   }

   @Override
   public final void run() {

      // Make sure this is the current thread
      if (this != currentThread()) {
         throw new IllegalThreadStateException("The run() method should only be called when this object is the current Thread. Invoke this thread via start().");
      }

      // Set the Log4J logging context, if appropriate
      if (_loggingContext != null) {
         NDC.push(_loggingContext);
      }

      // Delegate to the subclass
      try {
         runImpl();

      // Unset the Log4J logging context
      } finally {
         if (_loggingContext != null) {
            NDC.pop();
         }
         NDC.remove();
      }
   }

   /**
    * Runs this thread (implementation method). The logging context will have
    * been set, if one was passed to the constructor.
    */
   protected abstract void runImpl();
}
