/*
 * $Id: Utils.java,v 1.49 2007/12/17 13:35:30 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import java.lang.reflect.Method;

import org.xins.common.text.TextUtils;

/**
 * General utility functions.
 *
 * @version $Revision: 1.49 $ $Date: 2007/12/17 13:35:30 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 */
public final class Utils {

   /**
    * Constructs a new <code>Utils</code> object.
    */
   private Utils() {
      // empty
   }

   /**
    * Retrieves the actual (major) Java version.
    *
    * @return
    *    the actual Java version.
    *
    * @since XINS 1.2.0
    *
    * @deprecated
    *    Since XINS 3.0; use {@link org.apache.commons.lang.SystemUtils} instead.
    */
   @Deprecated
   public static double getJavaVersion() {
      String s = System.getProperty("java.version").substring(0, 3);
      return Double.parseDouble(s);
   }

   /**
    * Retrieves the name of the calling class at the specified level. The level
    * <code>0</code> indicates the direct caller, while <code>1</code>
    * indicates the caller of the caller.
    *
    * <p>If it cannot be determined, then <code>"&lt;unknown&gt;"</code> is
    * returned.
    *
    * @param level
    *    the level of the caller, must be &gt;= 0.
    *
    * @param methodMode
    *    <code>true</code> if the information wanted is the method name,
    *    <code>false</code> if the information wanted is the class name.
    *
    * @return
    *    the class name of method name of the caller of the caller of this method, at the
    *    specified level, never an empty string and never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>level &lt; 0</code>.
    *
    * @since XINS 2.0
    */
   private static String getCallingTrace(int level, boolean methodMode)
   throws IllegalArgumentException {

      // Check preconditions
      if (level < 0) {
         throw new IllegalArgumentException("level (" + level + ") < 0");
      }
      int depth = level + 3;

      // Only execute on Java 1.4 and up
      if (getJavaVersion() >= 1.4) {

         // Create an exception in order to have a stack trace
         final Throwable exception = new Throwable();

         // Analyze the stack trace
         StackTraceElement[] trace = exception.getStackTrace();
         if (trace != null) {
            for (int pos = 0, i = 0; i < trace.length; i++) {

               // Skip all non-authentic methods
               String method = trace[pos].getMethodName();
               while (method.startsWith("access$")) {
                  method = trace[++pos].getMethodName();
               }

               // If we are at the right depth, then return the method name
               if (i == depth) {
                  if (methodMode) {
                     return method;
                  } else {
                     return trace[pos].getClassName();
                  }

               // Otherwise go deeper
               } else {
                  pos++;
               }
            }
         }
      }

      // Fallback
      return "<unknown>";
   }

   /**
    * Retrieves the name of the calling class at the specified level. The level
    * <code>0</code> indicates the direct caller, while <code>1</code>
    * indicates the caller of the caller.
    *
    * <p>If it cannot be determined, then <code>"&lt;unknown&gt;"</code> is
    * returned.
    *
    * @param level
    *    the level of the caller, must be &gt;= 0.
    *
    * @return
    *    the class name of the caller of the caller of this method, at the
    *    specified level, never an empty string and never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>level &lt; 0</code>.
    *
    * @since XINS 1.3.0
    */
   public static String getCallingClass(int level)
   throws IllegalArgumentException {
      return getCallingTrace(level, false);
   }

   /**
    * Retrieves the name of the calling class. If it cannot be determined,
    * then a special string (e.g. <code>"&lt;unknown&gt;"</code>) is returned.
    *
    * @return
    *    the class name of the caller of the caller of this method, never an
    *    empty string and never <code>null</code>.
    */
   public static String getCallingClass() {
      if (getJavaVersion() >= 1.4) {
         Throwable exception = new Throwable();
         StackTraceElement[] trace = exception.getStackTrace();
         if (trace != null && trace.length >= 3) {
            StackTraceElement caller = trace[2];
            if (caller != null) {
               String callingClass = caller.getClassName();
               if (! TextUtils.isEmpty(callingClass)) {
                  return callingClass;
               }
            }
         }
      }

      // Fallback
      return "<unknown>";
   }

   /**
    * Retrieves the name of the calling method at the specified level. The
    * level <code>0</code> indicates the direct caller, while <code>1</code>
    * indicates the caller of the caller.
    *
    * <p>If it cannot be determined, then <code>"&lt;unknown&gt;"</code> is
    * returned.
    *
    * @param level
    *    the level of the caller, must be &gt;= 0.
    *
    * @return
    *    the method name of the caller of the caller of this method, at the
    *    specified level, never an empty string and never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>level &lt; 0</code>.
    *
    * @since XINS 1.3.0
    */
   public static String getCallingMethod(int level)
   throws IllegalArgumentException {
      return getCallingTrace(level, true);
   }

   /**
    * Retrieves the name of the calling method. If it cannot be determined,
    * then a special string (e.g. <code>"&lt;unknown&gt;"</code>) is returned.
    *
    * @return
    *    the method name of the caller of the caller of this method, never an
    *    empty string and never <code>null</code>.
    */
   public static String getCallingMethod() {
      if (getJavaVersion() >= 1.4) {
         Throwable exception = new Throwable();
         StackTraceElement[] trace = exception.getStackTrace();
         if (trace != null && trace.length >= 3) {
            StackTraceElement caller = trace[2];
            if (caller != null) {
               String callingMethod = caller.getMethodName();
               if (! TextUtils.isEmpty(callingMethod)) {
                  return callingMethod;
               }
            }
         }
      }

      // Fallback
      return "<unknown>";
   }

   /**
    * Logs an exception that will be ignored, with the specified detail
    * message.
    *
    * @param detectingClass
    *    the name of the class that caught the exception, cannot be
    *    <code>null</code>.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    caught the exception, cannot be <code>null</code>.
    *
    * @param subjectClass
    *    the name of the class which threw the exception, cannot be
    *    <code>null</code>.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    threw the exception, cannot be <code>null</code>.
    *
    * @param detail
    *    detail message, can be <code>null</code>.
    *
    * @param exception
    *    the exception to log, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>detectingClass == null || detectingMethod == null
    *          || subjectClass   == null || subjectMethod   == null
    *          || exception      == null</code>.
    *
    * @since XINS 1.3.0
    */
   public static void logIgnoredException(String    detectingClass,
                                          String    detectingMethod,
                                          String    subjectClass,
                                          String    subjectMethod,
                                          String    detail,
                                          Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("detectingClass",  detectingClass,
                                     "detectingMethod", detectingMethod,
                                     "subjectClass",    subjectClass,
                                     "subjectMethod",   subjectMethod);
      MandatoryArgumentChecker.check("exception",       exception);

      // Perform the actual logging
      Log.log_1051(exception.getClass().getName(), exception.getMessage(),
                   detectingClass, detectingMethod,
                   subjectClass,   subjectMethod,
                   detail);
   }

   /**
    * Logs an exception that will be ignored.
    *
    * @param exception
    *    the exception to log, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    *
    * @since XINS 1.5.0
    */
   public static void logIgnoredException(Throwable exception)
   throws IllegalArgumentException {

      // Determine detecting class and method
      String  detectingClass = getCallingClass();
      String detectingMethod = getCallingMethod();

      String  sourceClass = null;
      String sourceMethod = null;

      try {

         // Determine the source of the exception
         StackTraceElement[] trace  = exception.getStackTrace();

         for (int i = 1; i < trace.length && sourceClass == null; i++) {
            StackTraceElement stackTraceElement = trace[i];
            if (stackTraceElement.getClassName().equals(detectingClass) &&
                  stackTraceElement.getMethodName().equals(detectingMethod)) {

               // Go one level up the stack trace to know which method threw the exception
               StackTraceElement source = trace[i - 1];
               sourceClass  = source.getClassName();
               sourceMethod = source.getMethodName();
            }
         }
         if (sourceClass == null) {
            sourceClass  = "<unknown>";
            sourceMethod = "<unknown>";
         }

      // If there's any exception, then fallback to default values
      } catch (Throwable t) {
         sourceClass  = "<unknown>";
         sourceMethod = "<unknown>";
      }

      // Call alternative method with detail set to null
      logIgnoredException(detectingClass, detectingMethod,
                          sourceClass,    sourceMethod,
                          null,           exception);
   }

   /**
    * Logs an exception that will be ignored.
    *
    * @param detectingClass
    *    the name of the class that caught the exception, cannot be
    *    <code>null</code>.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    caught the exception, cannot be <code>null</code>.
    *
    * @param subjectClass
    *    the name of the class which threw the exception, cannot be
    *    <code>null</code>.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    threw the exception, cannot be <code>null</code>.
    *
    * @param exception
    *    the exception to log, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>detectingClass == null || detectingMethod == null
    *          || subjectClass   == null || subjectMethod   == null
    *          || exception      == null</code>.
    *
    * @since XINS 1.3.0
    */
   public static void logIgnoredException(String    detectingClass,
                                          String    detectingMethod,
                                          String    subjectClass,
                                          String    subjectMethod,
                                          Throwable exception)
   throws IllegalArgumentException {

      // Call alternative method with detail set to null
      logIgnoredException(detectingClass, detectingMethod,
                          subjectClass,   subjectMethod,
                          null,           exception);
   }

   /**
    * Logs a programming error with an optional detail message, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * <p>The calling class/method are considered the detecting class and the
    * caller of those (one level up) is considered the subject class/method
    * for the programming error.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static ProgrammingException logProgrammingError(String detail) {
      return logProgrammingError(getCallingClass(0), getCallingMethod(0),
                                 getCallingClass(1), getCallingMethod(1),
                                 detail);
   }

   /**
    * Logs a programming error with an optional cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static ProgrammingException logProgrammingError(Throwable cause) {
      return logProgrammingError(null, cause);
   }


   /**
    * Logs a programming error with an optional cause exception and an optional
    * message, and returns a <code>ProgrammingException</code> object for it.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    *
    * @since XINS 2.0.
    */
   public static ProgrammingException logProgrammingError(String detail, Throwable cause) {

      // Determine detecting class and method
      String detectingClass  = getCallingClass();
      String detectingMethod = getCallingMethod();

      String sourceClass = null;
      String sourceMethod = null;

      try {

         // Determine the source of the exception
         StackTraceElement[] trace = cause.getStackTrace();

         for (int i = 1; i < trace.length && sourceClass == null; i++) {
            StackTraceElement stackTraceElement = trace[i];
            if (stackTraceElement.getClassName().equals(detectingClass) &&
                  stackTraceElement.getMethodName().equals(detectingMethod)) {

               // Go one level up the stack trace to know which method threw the exception
               StackTraceElement source = trace[i - 1];
               sourceClass  = source.getClassName();
               sourceMethod = source.getMethodName();
            }
         }
         if (sourceClass == null) {
            sourceClass  = "<unknown>";
            sourceMethod = "<unknown>";
         }

      // If there's any exception, then fallback to default values
      } catch (Throwable t) {
         sourceClass  = "<unknown>";
         sourceMethod = "<unknown>";
      }

      // Log the programming error
      return logProgrammingError(detectingClass, detectingMethod,
                                 sourceClass,    sourceMethod,
                                 detail,         cause);
   }

   /**
    * Logs a programming error with an optional cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param detectingClass
    *    the name of the class that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    detected the problem, or <code>null</code> if unknown.
    *
    * @param subjectClass
    *    the name of the class which exposes the programming error, or
    *    <code>null</code> if unknown.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    exposes the programming error, or <code>null</code> if unknown.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static ProgrammingException logProgrammingError(String    detectingClass,
                                                          String    detectingMethod,
                                                          String    subjectClass,
                                                          String    subjectMethod,
                                                          String    detail,
                                                          Throwable cause) {

      // Log programming error (not due to exception)
      if (cause == null) {
         Log.log_1050(detectingClass, detectingMethod,
                      subjectClass,   subjectMethod,
                      detail);

      // Log programming error (due to exception)
      } else {
         Log.log_1052(cause,
                      detectingClass, detectingMethod,
                      subjectClass,   subjectMethod,
                      detail);
      }

      // Construct and return ProgrammingException object
      return new ProgrammingException(detectingClass, detectingMethod,
                                      subjectClass,   subjectMethod,
                                      detail,         cause);
   }

   /**
    * Logs a programming error with no cause exception, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param detectingClass
    *    the name of the class that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    detected the problem, or <code>null</code> if unknown.
    *
    * @param subjectClass
    *    the name of the class which exposes the programming error, or
    *    <code>null</code> if unknown.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    exposes the programming error, or <code>null</code> if unknown.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public static ProgrammingException logProgrammingError(String detectingClass,
                                                          String detectingMethod,
                                                          String subjectClass,
                                                          String subjectMethod,
                                                          String detail) {

      return logProgrammingError(detectingClass, detectingMethod,
                                 subjectClass,   subjectMethod,
                                 detail,         null);
   }

   /**
    * Logs a programming error with no detail message, and returns a
    * <code>ProgrammingException</code> object for it.
    *
    * @param detectingClass
    *    the name of the class that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    detected the problem, or <code>null</code> if unknown.
    *
    * @param subjectClass
    *    the name of the class which exposes the programming error, or
    *    <code>null</code> if unknown.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    exposes the programming error, or <code>null</code> if unknown.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link ProgrammingException} that can be thrown by the
    *    calling method, never <code>null</code>.
    *
    * @since XINS 3.0
    */
   public static ProgrammingException logProgrammingError(String    detectingClass,
                                                          String    detectingMethod,
                                                          String    subjectClass,
                                                          String    subjectMethod,
                                                          Throwable cause) {

      return logProgrammingError(detectingClass, detectingMethod,
                                 subjectClass,   subjectMethod,
                                 null,           cause);
   }

   /**
    * Determines the name of the specified class.
    *
    * @param c
    *    the class to determine the name for, not <code>null</code>.
    *
    * @return
    *    the name of the class, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>c == null</code>.
    *
    * @since XINS 1.2.0
    */
   public static String getNameOfClass(Class c)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("c", c);

      // Handle arrays
      if (c.isArray()) {
         Class comp = c.getComponentType();
         String name = getNameOfClass(comp);
         if (c.getName().charAt(0) == '[') {
            name += "[]";
         }
         return name;

      // Handle non-arrays (primitives and classes)
      } else {
         return c.getName();
      }
   }

   /**
    * Determines the name of the class of the specified object.
    *
    * @param object
    *    the object to determine the name of the class for, not
    *    <code>null</code>.
    *
    * @return
    *    the name of the class, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>object == null</code>.
    *
    * @since XINS 1.2.0
    */
   public static String getClassName(Object object)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("object", object);

      return getNameOfClass(object.getClass());
   }

   /**
    * Logs a DEBUG-level message.
    *
    * <p>This method logs Logdoc message 1053.
    *
    * @param message
    *    the text message to log at DEBUG-level.
    *
    * @since XINS 3.0
    */
   public static void logDebug(String message) {
      logDebug(message, null);
   }

   /**
    * Logs a DEBUG-level message with an optional exception.
    *
    * <p>This method logs Logdoc message 1053.
    *
    * @param message
    *    the text message to log at DEBUG-level.
    *
    * @param exception
    *    the exception to log, or <code>null</code> if none.
    *
    * @since XINS 3.0
    */
   public static void logDebug(String message, Throwable exception) {
      Log.log_1053(exception, message);
   }

   /**
    * Logs an INFO-level message.
    *
    * <p>This method logs Logdoc message 1054.
    *
    * @param message
    *    the text message to log at INFO-level.
    *
    * @since XINS 3.0
    */
   public static void logInfo(String message) {
      logInfo(message, null);
   }

   /**
    * Logs an INFO-level message.
    *
    * <p>This method logs Logdoc message 1054.
    *
    * @param message
    *    the text message to log at INFO-level.
    *
    * @param exception
    *    the exception to log, or <code>null</code> if none.
    *
    * @since XINS 3.0
    */
   public static void logInfo(String message, Throwable exception) {
      Log.log_1054(exception, message);
   }

   /**
    * Logs a NOTICE-level message.
    *
    * <p>This method logs Logdoc message 1055.
    *
    * @param message
    *    the text message to log at NOTICE-level.
    *
    * @since XINS 3.0
    */
   public static void logNotice(String message) {
      logNotice(message, null);
   }

   /**
    * Logs a NOTICE-level message.
    *
    * <p>This method logs Logdoc message 1055.
    *
    * @param message
    *    the text message to log at NOTICE-level.
    *
    * @param exception
    *    the exception to log, or <code>null</code> if none.
    *
    * @since XINS 3.0
    */
   public static void logNotice(String message, Throwable exception) {
      Log.log_1055(exception, message);
   }

   /**
    * Logs a WARN-level message.
    *
    * <p>This method logs Logdoc message 1056.
    *
    * @param message
    *    the text message to log at WARN-level.
    *
    * @since XINS 3.0
    */
   public static void logWarning(String message) {
      logWarning(message, null);
   }

   /**
    * Logs a WARN-level message.
    *
    * <p>This method logs Logdoc message 1056.
    *
    * @param message
    *    the text message to log at WARN-level.
    *
    * @param exception
    *    the exception to log, or <code>null</code> if none.
    *
    * @since XINS 3.0
    */
   public static void logWarning(String message, Throwable exception) {
      Log.log_1056(exception, message);
   }

   /**
    * Logs an ERROR-level message.
    *
    * <p>This method logs Logdoc message 1057.
    *
    * @param message
    *    the text message to log at ERROR-level.
    *
    * @since XINS 3.0
    */
   public static void logError(String message) {
      logError(message, null);
   }

   /**
    * Logs an ERROR-level message.
    *
    * <p>This method logs Logdoc message 1057.
    *
    * @param message
    *    the text message to log at ERROR-level.
    *
    * @param exception
    *    the exception to log, or <code>null</code> if none.
    *
    * @since XINS 3.0
    */
   public static void logError(String message, Throwable exception) {
      Log.log_1057(exception, message);
   }

   /**
    * Logs a FATAL-level message.
    *
    * <p>This method logs Logdoc message 1058.
    *
    * @param message
    *    the text message to log at FATAL-level.
    *
    * @since XINS 3.0
    */
   public static void logFatal(String message) {
      logFatal(message, null);
   }

   /**
    * Logs a FATAL-level message.
    *
    * <p>This method logs Logdoc message 1058.
    *
    * @param message
    *    the text message to log at FATAL-level.
    *
    * @param exception
    *    the exception to log, or <code>null</code> if none.
    *
    * @since XINS 3.0
    */
   public static void logFatal(String message, Throwable exception) {
      Log.log_1058(exception, message);
   }

   /**
    * Determines the context class loader of the current thread if available.
    * If not available the ClassLoader used to load this class is returned.
    *
    * @return
    *    the context ClassLoader of the current thread, never <code>null</code>.
    *
    * @since XINS 2.2
    */
   public static ClassLoader getContextClassLoader() {
      ClassLoader loader  = null;
      Thread thread = Thread.currentThread();
      try {
         Method loaderMethod = thread.getClass().getMethod("getContextClassLoader");
         loader  = (ClassLoader) loaderMethod.invoke(thread);
      } catch (Throwable ex) {
         loader = Utils.class.getClassLoader();
      }
      return loader;
   }
}
