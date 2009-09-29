/*
 * $Id: CallException.java,v 1.33 2007/03/15 17:08:27 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Root class for all exceptions that indicate a <code>ServiceCaller</code>
 * call failed. This exception is typically only thrown by class
 * {@link ServiceCaller} and subclasses.
 *
 * <p>Call exceptions can be linked. The first exception is then actually
 * thrown to the caller. The caller can get the linked exceptions using
 * {@link #getNext()}.
 *
 * @version $Revision: 1.33 $ $Date: 2007/03/15 17:08:27 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class CallException extends Exception {

   /**
    * Short description of the reason. Cannot be <code>null</code>.
    */
   private final String _shortReason;

   /**
    * The original request. Cannot be <code>null</code>.
    */
   private final CallRequest _request;

   /**
    * Descriptor for the target that was attempted to be called. Cannot be
    * <code>null</code>.
    */
   private final TargetDescriptor _target;

   /**
    * The time elapsed between the time the call attempt was started and the
    * time the call returned. The duration is in milliseconds and is always
    * &gt;= 0.
    */
   private final long _duration;

   /**
    * A detailed description of the problem. Can be <code>null</code>.
    */
   private String _detail;

   /**
    * The next linked <code>CallException</code>. Can be <code>null</code> if
    * there is none or if it has not been set yet.
    */
   private CallException _next;

   /**
    * The exception message. Is <code>null</code> if unset.
    */
   private String _message;

   /**
    * Constructs a new <code>CallException</code> based on a short reason, the
    * original request, target called, call duration, detail message and cause
    * exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, can be <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request == null
    *          || duration &lt; 0</code>.
    */
   protected CallException(String           shortReason,
                           CallRequest      request,
                           TargetDescriptor target,
                           long             duration,
                           String           detail,
                           Throwable        cause)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("shortReason", shortReason, "request", request);
      if (duration < 0) {
         throw new IllegalArgumentException(
            "duration (" + duration + ") < 0");
      }

      // Associate this exception with the root cause
      if (cause != null) {
        initCause(cause);
      }

      // Store information in fields
      _shortReason = shortReason;
      _request     = request;
      _target      = target;
      _duration    = duration;
      _detail      = detail;
   }

   /**
    * Returns the detail message string of this exception.
    *
    * @return
    *    the detail message string of this exception, never <code>null</code>.
    */
   public String getMessage() {

      // Initialize the message if necessary
      if (_message == null) {

         StringBuffer buffer = new StringBuffer(495);
         buffer.append(_shortReason);
         buffer.append(" in ");
         buffer.append(_duration);
         buffer.append(" ms while executing ");
         buffer.append(_request.describe());
         buffer.append(" at ");
         buffer.append(_target.getURL());

         if (_detail == null) {
            buffer.append('.');
         } else {
            buffer.append(": ");
            buffer.append(_detail);
         }

         _message = buffer.toString();
      }

      if (_next != null) {
         if (_message.endsWith(".")) {
            return _message + " Followed by: " + _next.getMessage();
         } else {
            return _message + ". Followed by: " + _next.getMessage();
         }
      } else {
         return _message;
      }
   }

   /**
    * Returns the original request.
    *
    * @return
    *    the original request, never <code>null</code>.
    */
   public final CallRequest getRequest() {
      return _request;
   }

   /**
    * Returns the descriptor for the target that was attempted to be called.
    *
    * @return
    *    the target descriptor, can be <code>null</code>.
    */
   public final TargetDescriptor getTarget() {
      return _target;
   }

   /**
    * Returns the call duration. This is defined as the time elapsed between
    * the time the call attempt was started and the time the call returned.
    * The duration is in milliseconds and is always &gt;= 0.
    *
    * @return
    *    the call duration in milliseconds, always &gt;= 0.
    */
   public final long getDuration() {
      return _duration;
   }

   /**
    * Sets the next linked <code>CallException</code>. This method should be
    * called either never or once during the lifetime of a
    * <code>CallException</code> object.
    *
    * @param next
    *    the next linked <code>CallException</code>, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the next linked <code>CallException</code> has already been set.
    *
    * @throws IllegalArgumentException
    *    if <code>next == null</code>.
    */
   final void setNext(CallException next)
   throws IllegalStateException, IllegalArgumentException {

      // Check preconditions
      if (_next != null) {
         throw new IllegalStateException(
            "Next linked CallException already set.");
      }
      MandatoryArgumentChecker.check("next", next);

      // Store the reference
      _next = next;
   }

   /**
    * Gets the next linked <code>CallException</code>, if there is any.
    *
    * @return
    *    the next linked <code>CallException</code>, or <code>null</code> if
    *    there is none.
    */
   public final CallException getNext() {
      return _next;
   }

   /**
    * Returns a detailed description of problem, if any.
    *
    * @return
    *    a detailed description, if available, otherwise <code>null</code>.
    */
   public String getDetail() {
      return _detail;
   }

   /**
    * Determines whether the call has possibly (or definitely) been processed 
    * by the target service.
    *
    * <p>The implementation of this method in class {@link CallException}
    * returns <code>true</code>.
    *
    * @return
    *    <code>true</code> if the call has possibly or definitely been 
    *    processed, <code>false</code> if the call was definitely not 
    *    processed yet (so fail-over should be allowable).
    *
    * @since XINS 2.2
    */
   public boolean isCallPossiblyProcessed() {
      return true;
   }
}
