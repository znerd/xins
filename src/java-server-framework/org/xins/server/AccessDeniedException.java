/*
 * $Id: AccessDeniedException.java,v 1.17 2007/09/11 12:39:57 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Exception that indicates that there is no function matching the request.
 *
 * @version $Revision: 1.17 $ $Date: 2007/09/11 12:39:57 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class AccessDeniedException extends Exception {

   /**
    * The IP address which is denied for the given function. This field may be
    * <code>null</code>.
    */
   private final String _ip;

   /**
    * The name of the function which does not grant the access. This field may
    * be <code>null</code>.
    */
   private final String _functionName;

   /**
    * The name of the calling convention which does not grant the access.
    * This field may be <code>null</code>.
    */
   private final String _conventionName;

   /**
    * Constructs a new <code>AccessDeniedException</code> for the specified
    * IP address and function name.
    *
    * @param ip
    *    the IP address, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function, or <code>null</code>.
    *
    * @param conventionName
    *    the name of the calling convention, can be <code>null</code>.
    */
   AccessDeniedException(String ip, String functionName, String conventionName) {
      super(createMessage(ip, functionName, conventionName));
      _ip = ip;
      _functionName = functionName;
      _conventionName = conventionName;
   }

   /**
    * Creates the error message for this exception.
    *
    * @param ip
    *    the IP address, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function, or <code>null</code>.
    *
    * @param conventionName
    *    the name of the calling convention, can be <code>null</code>.
    *
    * @return
    *    the error message to be used by the constructor, never
    *    <code>null</code>.
    */
   private static String createMessage(String ip, String functionName, 
         String conventionName) {

      String message = null;

      // Function name and IP address given
      if (functionName != null && ip != null) {
         message = "The function \"" + functionName + "\" cannot be accessed from IP address " + ip;

      // Only IP address given
      } else if (ip != null) {
         message = "An unspecified function cannot be accessed from IP address " + ip;

      // Only function name given
      } else if (functionName != null) {
         message = "The function \"" + functionName + "\" cannot be accessed";

      // Neither function name nor IP address given
      } else {
         message = "An unspecified function cannot be accessed";
      }
      if (conventionName == null || conventionName.equals("*")) {
         message += ".";
      } else {
         message += " using the calling convention " + conventionName + ".";
      }
      return message;
   }

   /**
    * Gets the IP address which is denied for the given function.
    *
    * @return
    *    the IP address, or <code>null</code> if no IP address was provided.
    */
   public String getIP() {
      return _ip;
   }

   /**
    * Gets the name of the function which does not grant the access.
    *
    * @return
    *    the name of the function, or <code>null</code> if no function name
    *    was provided.
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Gets the name of the calling convention which does not grant the access.
    *
    * @return
    *    the name of the calling convention, or <code>null</code> 
    *    if no calling convention name was provided.
    *
    * @since XINS 2.1
    */
   public String getConventionName() {
      return _conventionName;
   }
}
