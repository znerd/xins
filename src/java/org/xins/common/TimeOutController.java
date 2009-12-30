/*
 * $Id: TimeOutController.java,v 1.25 2007/03/16 09:54:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

/**
 * Utility class for executing a task with a certain time-out period.
 *
 * @version $Revision: 1.25 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class TimeOutController {

   /**
    * Constructs a new <code>TimeOutController</code> object.
    */
   private TimeOutController() {
      // empty
   }

   /**
    * Runs the specified task with a specific time-out. If the task does
    * not finish within the specified time-out period, then the thread
    * executing that task is interrupted using the {@link Thread#interrupt()}
    * method and a {@link TimeOutException} is thrown.
    *
    * <p>Note that the specified task could be run either in the current
    * thread or in a new thread. In the latter case, no initialization is
    * performed. For example, the <em>Nested Diagnostic Context
    * identifier</em> (NDC) is not copied from the current thread to the new
    * one.
    *
    * @param task
    *    the task to run, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the timeOut in milliseconds, must be &gt; 0.
    *
    * @throws IllegalArgumentException
    *    if <code>task == null || timeOut &lt;= 0</code>.
    *
    * @throws IllegalThreadStateException
    *    if the specified task is a {@link Thread} that is already started.
    *
    * @throws SecurityException
    *    if the thread did not finish within the total time-out period, but
    *    the interruption of the thread was disallowed (see
    *    {@link Thread#interrupt()}); consequently, the thread may still be
    *    running.
    *
    * @throws TimeOutException
    *    if the thread did not finish within the total time-out period and was
    *    interrupted.
    */
   public static void execute(Runnable task, int timeOut)
   throws IllegalArgumentException,
          IllegalThreadStateException,
          SecurityException,
          TimeOutException {

      // Check preconditions
      MandatoryArgumentChecker.check("task", task);
      if (timeOut <= 0) {
         throw new IllegalArgumentException("timeOut (" + timeOut + ") <= 0");
      }

      // We need a Thread instance. If the argument is already a Thread
      // instance, then use it, otherwise construct a new Thread instance.
      Thread thread;
      if (task instanceof Thread) {
         thread = (Thread) task;
      } else {
         // XXX: To improve performance and manageability, we could use a
         //      thread pool like the one that is available in J2SE 5.0.
         thread = new Thread(task);
      }

      // Start the thread. This may throw an IllegalThreadStateException.
      thread.start();

      // Wait for the thread to finish, within limits
      try {
         thread.join(timeOut);
      } catch (InterruptedException exception) {
         Utils.logIgnoredException(exception);
      }

      // If the thread is still running at this point, it should stop
      if (thread.isAlive()) {

         // Interrupt the thread. This may throw a SecurityException
         thread.interrupt();

         throw new TimeOutException();
      }
   }
}
