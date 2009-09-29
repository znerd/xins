/*
 * $Id: UnacceptableErrorCodeXINSCallException.java,v 1.11 2007/03/16 09:54:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception that indicates an error code was received from the server-side
 * that is not expected at the client-side.
 *
 * @version $Revision: 1.11 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public class UnacceptableErrorCodeXINSCallException
extends UnacceptableResultXINSCallException {

   /**
    * Constructs a new <code>UnacceptableErrorCodeXINSCallException</code>
    * based on a <code>XINSCallResultData</code> instance.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param resultData
    *    the result data, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0
    *          || resultData  == null
    *          || resultData.{@link XINSCallResult#getErrorCode()
    *             getErrorCode()} == null</code>.
    */
   public UnacceptableErrorCodeXINSCallException(
      XINSCallRequest    request,
      TargetDescriptor   target,
      long               duration,
      XINSCallResultData resultData
   ) throws IllegalArgumentException {

      super(request, target, duration, resultData, getDetail(resultData));
   }

   /**
    * Constructs a detail message for the constructor to pass up to the
    * superconstructor.
    *
    * @param result
    *    the {@link XINSCallResultData} that is has an error code set that is
    *    considered unacceptable, never <code>null</code>.
    *
    * @return
    *    the detail message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null
    *          || result.{@link XINSCallResultData#getErrorCode()
    *             getErrorCode()} == null</code>.
    */
   private static final String getDetail(XINSCallResultData result)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("result", result);
      if (result.getErrorCode() == null) {
         throw new IllegalArgumentException("result.getErrorCode() == null");
      }

      // Generate detail message
      return "Error code \""
           + result.getErrorCode()
           + "\" is not acceptable for this function.";
   }
}
