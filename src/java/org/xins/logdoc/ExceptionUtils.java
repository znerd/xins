/*
 * $Id: ExceptionUtils.java,v 1.18 2007/06/07 08:27:52 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.WeakHashMap;

/**
 * Utility functions related to exceptions.
 *
 * @version $Revision: 1.18 $ $Date: 2007/06/07 08:27:52 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public final class ExceptionUtils {

   /**
    * Constructs a new <code>ExceptionUtils</code> object.
    */
   private ExceptionUtils() {
      // empty
   }

   /**
    * Determines the root cause for the specified exception.
    *
    * @param exception
    *    the exception to determine the root cause for, can be
    *    <code>null</code>.
    *
    * @return
    *    the root cause exception, can be <code>null</code>.
    */
   public static Throwable getRootCause(Throwable exception) {

      // Check preconditions
      if (exception == null) {
         return null;
      }

      // Get the root cause of the exception
      Throwable cause = getCause(exception);
      while (cause != null) {
         exception = cause;
         cause = getCause(exception);
      }

      return exception;
   }

   /**
    * Determines the cause for the specified exception.
    *
    * @param exception
    *    the exception to determine the cause for, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    *
    * @deprecated
    *    Since XINS 3.0, use {@link Throwable#getCause()} directly.
    *    This method was previously provided for backwards compatibility with
    *    Java 1.3, but that version is no longer supported.
    */
   @Deprecated
   public static Throwable getCause(Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      if (exception == null) {
         throw new IllegalArgumentException("exception  == null");
      }

      // Use the Throwable.getCause() method, available as of J2SE v1.4
      return exception.getCause();
   }

   /**
    * Sets the cause for the specified exception.
    *
    * @param exception
    *    the exception to set the cause for, cannot be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code> but cannot be the
    *    same as <code>exception</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null || exception == cause</code>.
    *
    * @throws IllegalStateException
    *    if the cause exception was already set.
    *
    * @deprecated
    *    Since XINS 3.0, use {@link Throwable#initCause(Throwable)} instead.
    *    This method was previously provided for backwards compatibility with
    *    Java 1.3, but that version is no longer supported.
    */
   @Deprecated
   public static void setCause(Throwable exception, Throwable cause)
   throws IllegalArgumentException, IllegalStateException {

      // Check preconditions
      if (exception == null) {
         throw new IllegalArgumentException("exception  == null");
      } else if (exception == cause) {
         throw new IllegalArgumentException("exception == cause");

      // Use the Throwable.initCause() method, available as of J2SE v1.4
      } else if (cause != null) {
         exception.initCause(cause);
      }
   }
}
