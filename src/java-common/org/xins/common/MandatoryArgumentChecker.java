/*
 * $Id: MandatoryArgumentChecker.java,v 1.22 2007/03/16 09:54:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

/**
 * Utility class used to check mandatory method arguments.
 *
 * @version $Revision: 1.22 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class MandatoryArgumentChecker {

   /**
    * Constructs a new <code>MandatoryArgumentChecker</code>. This constructor
    * is private since this no instances of this class should be created.
    */
   private MandatoryArgumentChecker() {
      // empty
   }

   /**
    * Checks if the specified argument value is <code>null</code>. If it is
    * <code>null</code>, then an {@link IllegalArgumentException} is thrown.
    *
    * @param argName
    *    the name of the argument, cannot be <code>null</code>.
    *
    * @param argValue
    *    the value of the argument.
    *
    * @throws IllegalArgumentException
    *    if <code>argValue == null</code>.
    */
   public static void check(String argName, Object argValue)
   throws IllegalArgumentException {

      // If both are non-null everything is okay, just short-circuit
      if (argName != null && argValue != null) {
         return;
      }

      // Check if the name is null
      if (argName == null) {
         throw Utils.logProgrammingError("argName == null");
      }

      // Otherwise the value is null
      if (argValue == null) {
         throw new IllegalArgumentException(argName + " == null");
      }
   }

   /**
    * Checks if any of the two specified argument values is <code>null</code>.
    * If at least one value is <code>null</code>, then an
    * {@link IllegalArgumentException} is thrown.
    *
    * @param argName1
    *    the name of the first argument, cannot be <code>null</code>.
    *
    * @param argValue1
    *    the value of the first argument.
    *
    * @param argName2
    *    the name of the second argument, cannot be <code>null</code>.
    *
    * @param argValue2
    *    the value of the second argument.
    *
    * @throws IllegalArgumentException
    *    if <code>argValue1 == null || argValue2 == null</code>.
    */
   public static void check(String argName1, Object argValue1,
                            String argName2, Object argValue2)
   throws IllegalArgumentException {

      // If all are non-null everything is okay, just short-circuit
      if (argName1 != null && argValue1 != null &&
          argName2 != null && argValue2 != null) {
         return;
      }

      String message = "";

      // Check if any of the names is null
      if (argName1 == null && argName2 == null) {
         message = "argName1 == null && argName2 == null";
         throw Utils.logProgrammingError(message);
      }

      // Otherwise (at least) one of the values must be null
      if (argValue1 == null && argValue2 == null) {
         message = argName1 + " == null && "
                 + argName2 + " == null";
      } else {
         check(argName1, argValue1);
         check(argName2, argValue2);
      }
      throw new IllegalArgumentException(message);
   }

   /**
    * Checks if any of the three specified argument values is
    * <code>null</code>. If at least one value is <code>null</code>, then an
    * {@link IllegalArgumentException} is thrown.
    *
    * @param argName1
    *    the name of the first argument, cannot be <code>null</code>.
    *
    * @param argValue1
    *    the value of the first argument.
    *
    * @param argName2
    *    the name of the second argument, cannot be <code>null</code>.
    *
    * @param argValue2
    *    the value of the second argument.
    *
    * @param argName3
    *    the name of the third argument, cannot be <code>null</code>.
    *
    * @param argValue3
    *    the value of the third argument.
    *
    * @throws IllegalArgumentException
    *    if <code>argValue1 == null
    *          || argValue2 == null
    *          || argValue3 == null</code>.
    */
   public static void check(String argName1, Object argValue1,
                            String argName2, Object argValue2,
                            String argName3, Object argValue3)
   throws IllegalArgumentException {

      // If all are non-null everything is okay, just short-circuit
      if (argName1 != null && argValue1 != null &&
          argName2 != null && argValue2 != null &&
          argName3 != null && argValue3 != null) {
         return;
      }

      // Check if any of the names is null
      String message = "";
      if (argName1 == null && argName2 == null && argName3 == null) {
         message = "argName1 == null && "
                 + "argName2 == null && "
                 + "argName3 == null";
         throw Utils.logProgrammingError(message);
      }

      // Otherwise (at least) one of the values must be null
      if (argValue1 == null && argValue2 == null && argValue3 == null) {
         message = argName1 + " == null && "
                 + argName2 + " == null && "
                 + argName3 + " == null";
      } else {
         check(argName1, argValue1, argName2, argValue2);
         check(argName1, argValue1, argName3, argValue3);
         check(argName2, argValue2, argName3, argValue3);
      }
      throw new IllegalArgumentException(message);
   }

   /**
    * Checks if any of the four specified argument values is
    * <code>null</code>. If at least one value is <code>null</code>, then an
    * {@link IllegalArgumentException} is thrown.
    *
    * @param argName1
    *    the name of the first argument, cannot be <code>null</code>.
    *
    * @param argValue1
    *    the value of the first argument.
    *
    * @param argName2
    *    the name of the second argument, cannot be <code>null</code>.
    *
    * @param argValue2
    *    the value of the second argument.
    *
    * @param argName3
    *    the name of the third argument, cannot be <code>null</code>.
    *
    * @param argValue3
    *    the value of the third argument.
    *
    * @param argName4
    *    the name of the fourth argument, cannot be <code>null</code>.
    *
    * @param argValue4
    *    the value of the fourth argument.
    *
    * @throws IllegalArgumentException
    *    if <code>argValue1 == null || argValue2 == null
    *          || argValue3 == null || argValue4 == null</code>.
    */
   public static void check(String argName1, Object argValue1,
                            String argName2, Object argValue2,
                            String argName3, Object argValue3,
                            String argName4, Object argValue4)
   throws IllegalArgumentException {

      // If all are non-null everything is okay, just short-circuit
      if (argName1 != null && argValue1 != null &&
          argName2 != null && argValue2 != null &&
          argName3 != null && argValue3 != null &&
          argName4 != null && argValue4 != null) {
         return;
      }

      // Check if any of the names is null
      String message = "";
      if (argName1 == null && argName2 == null &&
          argName3 == null && argName4 == null) {
         message = "argName1 == null && argName2 == null && "
                 + "argName3 == null && argName4 == null";
         throw Utils.logProgrammingError(message);
      }

      // Otherwise (at least) one of the values must be null
      if (argValue1 == null && argValue2 == null && argValue3 == null && argValue4 == null) {
         message = argName1 + " == null && "
                 + argName2 + " == null && "
                 + argName3 + " == null && "
                 + argName4 + " == null";
      } else {
         check(argName1, argValue1, argName2, argValue2, argName3, argValue3);
         check(argName1, argValue1, argName2, argValue2, argName4, argValue4);
         check(argName2, argValue2, argName3, argValue3, argName4, argValue4);
      }
      throw new IllegalArgumentException(message);
   }
}
