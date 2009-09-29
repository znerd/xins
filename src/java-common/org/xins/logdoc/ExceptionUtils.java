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
    * Reference to the <code>getCause()</code> method in class
    * <code>Throwable</code>. This reference will be <code>null</code> on Java
    * 1.3.
    */
   private static Method GET_CAUSE;

   /**
    * Reference to the <code>initCause()</code> method in class
    * <code>Throwable</code>. This reference will be <code>null</code> on Java
    * 1.3.
    */
   private static Method SET_CAUSE;

   /**
    * Table that maps from exception to cause. This table will only be used on
    * Java 1.3. On Java 1.4 and up it will be <code>null</code>.
    */
   private static WeakHashMap CAUSE_TABLE;

   /**
    * Placeholder for the <code>null</code> object. This object will be stored
    * in the {@link #CAUSE_TABLE} on Java 1.3 if the cause for an exception is
    * set to <code>null</code>.
    */
   private static final Object NULL = new Object();

   /**
    * Initializes this class.
    */
   static {

      Class[] args = new Class[] { Throwable.class };

      try {
         GET_CAUSE = Throwable.class.getDeclaredMethod("getCause", (Class[]) null);
         SET_CAUSE = Throwable.class.getDeclaredMethod("initCause", args);
         CAUSE_TABLE = null;

      // Method does not exist, this is not Java 1.4
      } catch (NoSuchMethodException exception) {
         GET_CAUSE   = null;
         SET_CAUSE   = null;
         CAUSE_TABLE = new WeakHashMap();

      // Access denied
      } catch (SecurityException exception) {
         throw new RuntimeException("Unable to get getCause() method of class Throwable: Access denied by security manager.");
      }
   }

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
    *    Since XINS 3.0, use {@link Throwable#getCause()} instead.
    *    This method was previously provided for backwards compatibility with
    *    Java 1.3, but that version is no longer supported.
    */
   public static Throwable getCause(Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      if (exception == null) {
         throw new IllegalArgumentException("exception  == null");
      }

      // On Java 1.4 (and up) use the Throwable.getCause() method
      if (GET_CAUSE != null) {
         try {
            return (Throwable) GET_CAUSE.invoke(exception, (Object[]) null);
         } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke Throwable.getCause() method. Caught IllegalAccessException.");
         } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to invoke Throwable.getCause() method. Caught IllegalArgumentException");
         } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to invoke Throwable.getCause() method. Caught InvocationTargetException");
         }

      // On Java 1.3 use the static table
      } else {
         Object cause = CAUSE_TABLE.get(exception);
         return (cause == NULL) ? null : (Throwable) cause;
      }
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
   public static void setCause(Throwable exception, Throwable cause)
   throws IllegalArgumentException, IllegalStateException {

      // Check preconditions
      if (exception == null) {
         throw new IllegalArgumentException("exception  == null");
      }
      if (exception == cause) {
         throw new IllegalArgumentException("exception == cause");
      }

      // On Java 1.4 (and up) use the Throwable.initCause() method
      if (SET_CAUSE != null) {
         try {
            Object[] args = { cause };
            SET_CAUSE.invoke(exception, args);
         } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke Throwable.initCause() method. Caught IllegalAccessException.");
         } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to invoke Throwable.initCause() method. Caught IllegalArgumentException");
         } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
               throw (RuntimeException) targetException;
            } else if (targetException instanceof Error) {
               throw (Error) targetException;
            } else {
               throw new RuntimeException("Unable to invoke Throwable.initCause() method. Throwable.initCause() has thrown an unexpected exception. Exception class is " + targetException.getClass().getName() + ".  Message is: " + targetException.getMessage() + '.');
            }
         }

      // On Java 1.3 use the static table
      } else {
         if (CAUSE_TABLE.get(exception) != null) {
            throw new IllegalStateException("Cause for exception already set.");
         }

         Object value = (cause == null) ? NULL : cause;
         CAUSE_TABLE.put(exception, value);
      }
   }
}
