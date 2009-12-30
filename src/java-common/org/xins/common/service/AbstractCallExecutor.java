/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.util.HashMap;
import java.util.Map;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.logging.LoggingThread;

/**
 * Abstract base class for call executors, used from within service caller
 * implementations.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public abstract class AbstractCallExecutor extends LoggingThread {

   /**
    * The counters for objects, per implementation class. The key in the map
    * is the implementation {@link Class}, the value is an {@link Integer}
    * that represents the current number of objects.
    *
    * <p>The value may be <code>null</code> if there are no current objects of
    * the class.
    */
   private static final Map<Class,Integer> INSTANCE_COUNTERS = new HashMap<Class,Integer>();

   /**
    * Constructs a new <code>AbstractCallExecutor</code> for the specified
    * call to a service.
    *
    * @param request
    *    the call request, cannot be <code>null</code>.
    *
    * @param callConfig
    *    the call configuration, cannot be <code>null</code>.
    *
    * @param target
    *    the service target on which to execute the request,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request    == null
    *          || callConfig == null
    *          || target     == null</code>.
    */
   protected AbstractCallExecutor(CallRequest      request,
                                  CallConfig       callConfig,
                                  TargetDescriptor target) {

      // Check preconditions
      MandatoryArgumentChecker.check("request",    request,
                                     "callConfig", callConfig,
                                     "target",     target);

      // Populate fields
      _state      = State.INITIAL;
      _request    = request;
      _callConfig = callConfig;
      _target     = target;

      // Determine the unique ID of this instance
      int instanceID;
      Class<? extends AbstractCallExecutor> thisClass = getClass();
      synchronized (INSTANCE_COUNTERS) {
         Integer  i = INSTANCE_COUNTERS.get(thisClass);
         instanceID = (i == null) ? 0 : i.intValue();
         INSTANCE_COUNTERS.put(thisClass, new Integer(instanceID + 1));
      }

      // Set the name of this thread
      String name = thisClass.getName() + " #" + instanceID;
      setName(name);
   }

   /**
    * The state of this object. Never <code>null</code>.
    */
   private State _state;

   /**
    * The call request to execute. Never <code>null</code>.
    */
   private CallRequest _request;

   /**
    * The call configuration. Never <code>null</code>.
    */
   private CallConfig _callConfig;

   /**
    * The service target on which to execute the request. Never
    * <code>null</code>.
    */
   private TargetDescriptor _target;

   /**
    * The exception caught while executing the call. If there was no
    * exception, then this field is <code>null</code>.
    */
   private Throwable _exception;

   /**
    * Runs this thread. This method will invoke
    * {@link #execute(CallRequest,CallConfig,TargetDescriptor)}. If that
    * method throws an exception, then that exception is stored in this
    * object, along with some more information.
    *
    * <p>In each case, the duration is captured.
    *
    * <p>At the very end, {@link #cleanupImpl()} is called, even if an
    * exception was thrown.
    */
   @Override
   protected final void runImpl() {

      synchronized (this) {
         _state = State.EXECUTING_CALL;
      }

      // Perform the call
      try {
         execute(_request, _callConfig, _target);

      // If an exception is thrown, store it for processing at later stage
      } catch (Throwable exception) {
         _exception = exception;

      } finally {
         synchronized (this) {
            _state = State.CLEANING_UP_AFTER_CALL;
         }
         cleanup();
         synchronized (this) {
            _state = State.CALL_COMPLETED;
         }
      }
   }

   /**
    * Executes the call. This method is called from {@link #runImpl()}.
    *
    * <p>This method may throw any kind of exception, which will then be
    * stored in this object, see {@link #getException()}.
    *
    * @param request
    *    the call request, never <code>null</code>.
    *
    * @param callConfig
    *    the call configuration, never <code>null</code>.
    *
    * @param target
    *    the service target on which to execute the request,
    *    never <code>null</code>.
    *
    * @throws Throwable
    *    in case of an error.
    */
   protected abstract void execute(CallRequest      request,
                                   CallConfig       callConfig,
                                   TargetDescriptor target)
   throws Throwable;

   /**
    * Makes sure the call is completed. If not, this method throws an
    * {@link IllegalStateException}.
    *
    * @throws IllegalStateException
    *    if the state is not {@link State#CALL_COMPLETED}.
    */
   private final void assertCompleted() {
      if (_state != State.CALL_COMPLETED) {
         throw new IllegalStateException("The state is " + _state + " instead of " + State.CALL_COMPLETED + '.');
      }
   }


   /**
    * Gets the exception if any generated when calling the method.
    *
    * @return
    *    the invocation exception or <code>null</code> if the call was
    *    performed successfully.
    *
    * @throws IllegalStateException
    *    if the state is not {@link State#CALL_COMPLETED}.
    */
   public final Throwable getException() throws IllegalStateException {
      assertCompleted();
      return _exception;
   }

   /**
    * Gets the name of the class that threw the exception.
    *
    * @return
    *    the name of the class that threw the exception or <code>null</code>
    *    if the class name is unkown
    *    or if the call was performed successfully.
    *
    * @throws IllegalStateException
    *    if the state is not {@link State#CALL_COMPLETED}.
    */
   public final String getThrowingClass() throws IllegalStateException {
      assertCompleted();

      String throwingClass = null;

      // Analyze the stack trace
      StackTraceElement[] trace = _exception.getStackTrace();
      if (trace != null && trace.length > 0) {
         StackTraceElement element = trace[0];
         if (element != null) {
            throwingClass = element.getClassName();
         }
      }

      return throwingClass;
   }

   /**
    * Gets the name of the method that threw the exception.
    *
    * @return
    *    the name of the method that threw the exception or <code>null</code>
    *    if the method name is unkown
    *    or if the call was performed successfully.
    *
    * @throws IllegalStateException
    *    if the state is not {@link State#CALL_COMPLETED}.
    */
   public final String getThrowingMethod() throws IllegalStateException {
      assertCompleted();

      String throwingMethod = null;

      // Analyze the stack trace
      StackTraceElement[] trace = _exception.getStackTrace();
      if (trace != null && trace.length > 0) {
         StackTraceElement element = trace[0];
         if (element != null) {
            throwingMethod = element.getMethodName();
         }
      }

      return throwingMethod;
   }


   /**
    * Cleans up, right after the call was completed (wrapper method).
    * This is executed before the results of the call are analyzed.
    *
    * <p>This method delegates to {@link #cleanupImpl()}.
    *
    * <p>To cleanup after all the information is analyzed and the thread can
    * be garbage collected, use {@link #dispose()} instead.
    */
   protected final void cleanup() throws IllegalStateException {

      // Check preconditions
      if (_state != State.CLEANING_UP_AFTER_CALL) {
         throw new IllegalStateException("State is " + _state + " instead of " + State.CLEANING_UP_AFTER_CALL + '.');
      }

      // Delegate to subclass
      try {
         cleanupImpl();
      } catch (Throwable exception) {
         Log.log_1052(exception, AbstractCallExecutor.class.getName(), "runImpl()", getClass().getName(), "cleanupImpl()", null);
      }
   }

   /**
    * Cleans up, right after the call was completed (implementation method).
    * This is executed before the results of the call are analyzed.
    *
    * <p>To cleanup after all the information is analyzed and the thread can
    * be garbage collected, use {@link #dispose()} instead.
    *
    * <p>The implementation of this method in class
    * {@link AbstractCallExecutor} is empty.
    */
   protected void cleanupImpl() {
      // empty
   }

   /**
    * Cleans up when this thread is completely done with
    * (wrapper method). This method delegates to
    * {@link #disposeImpl()}.
    *
    * <p>This method never throws any exceptions.
    */
   public final void dispose() {

      synchronized (this) {
         _state = State.DISPOSING;
      }

      // First allow the subclass to dispose
      try {
         disposeImpl();
      } catch (Throwable exception) {
         Log.log_1052(exception, AbstractCallExecutor.class.getName(), "dispose()", getClass().getName(), "disposeImpl()", null);
      }

      // Then dispose this class self
      _callConfig = null;
      _request    = null;
      _target     = null;

      synchronized (this) {
         _state = State.DISPOSED;
      }
   }

   /**
    * Cleans up when this thread is completely done with
    * (implementation method).
    *
    * <p>Any exceptions this method throws are logged and then dropped.
    *
    * <p>The implementation of this method in class
    * {@link AbstractCallExecutor} is empty.
    */
   protected void disposeImpl() throws Throwable {
      // empty
   }

   /**
    * Finalizes this object. This method calls {@link #dispose()} and then
    * calls <code>super.finalize()</code>.
    *
    * @throws Throwable
    *    in case of an error.
    */
   protected final void finalize() throws Throwable {
      dispose();
      super.finalize();
   }

   /**
    * The state of a call executor.
    *
    * @version $Revision$ $Date$
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    *
    * @since XINS 3.0
    */
   public enum State {
      INITIAL,
      EXECUTING_CALL,
      CLEANING_UP_AFTER_CALL,
      CALL_COMPLETED,
      DISPOSING,
      DISPOSED
   }
}
