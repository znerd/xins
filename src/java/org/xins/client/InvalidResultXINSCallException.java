/*
 * $Id: InvalidResultXINSCallException.java,v 1.18 2007/03/16 09:54:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.io.UnsupportedEncodingException;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.service.TargetDescriptor;

/**
 * Exception thrown to indicate that the result from a XINS API call was
 * invalid according to the XINS rules for a XINS call result.
 *
 * <p>Note that this exception is <em>only</em> thrown if the result is
 * invalid according to the XINS rules for a result XML document. If the
 * result is only invalid in relation to the applicable API specification,
 * then an {@link UnacceptableResultXINSCallException} is thrown instead.
 *
 * @version $Revision: 1.18 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class InvalidResultXINSCallException extends XINSCallException {

   /**
    * Constructs a new <code>InvalidResultXINSCallException</code>.
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
    * @param detail
    *    a more detailed description of the problem, or <code>null</code> if
    *    none is available.
    *
    * @param cause
    *    the cause exception, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    */
   private InvalidResultXINSCallException(XINSCallRequest  request,
                                          TargetDescriptor target,
                                          long             duration,
                                          String           detail,
                                          Throwable        cause)
   throws IllegalArgumentException {
      super("Invalid XINS call result",
            request, target, duration, detail, cause);
   }

   /**
    * Creates a <code>InvalidResultXINSCallException</code> for the situation
    * where no HTTP data is received.
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
    * @return
    *    the exception indicating that no HTTP data was received.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    */
   static InvalidResultXINSCallException noDataReceived(
      XINSCallRequest  request,
      TargetDescriptor target,
      long             duration)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request",  request,
                                     "target",   target);
      if (duration < 0) {
         throw new IllegalArgumentException(
            "duration (" + duration + ") < 0");
      }

      String    detail = "No HTTP response received.";
      Throwable cause  = null;

      return new InvalidResultXINSCallException(
         request, target, duration, detail, cause);
   }

   /**
    * Creates a <code>InvalidResultXINSCallException</code> for the situation
    * where the received HTTP data cannot be parsed.
    *
    * @param httpData
    *    the HTTP data, cannot be <code>null</code> and the length must be
    *    positive.
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
    * @param cause
    *    the cause exception, or <code>null</code> if there is none.
    *
    * @return
    *    the exception indicating that the HTTP data could not be parsed.
    *
    * @throws IllegalArgumentException
    *    if <code>httpData        ==   null
    *          || request         ==   null
    *          || target          ==   null
    *          || httpData.length &lt; 1
    *          || duration        &lt; 0</code>.
    */
   static InvalidResultXINSCallException parseError(
      byte[]           httpData,
      XINSCallRequest  request,
      TargetDescriptor target,
      long             duration,
      Throwable        cause)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("httpData", httpData,
                                     "request",  request,
                                     "target",   target);
      if (httpData.length < 1) {
         throw new IllegalArgumentException("httpData.length == 0");
      } else if (duration < 0) {
         throw new IllegalArgumentException(
            "duration (" + duration + ") < 0");
      }

      // Determine how much to quote; max is 512 bytes
      int    quoteLength = Math.min(httpData.length, 512);
      String quote;
      try {
         quote = new String(httpData, 0, quoteLength, "US-ASCII");
      } catch (UnsupportedEncodingException exception) {
         throw Utils.logProgrammingError(cause);
      }

      // Construct the detail message
      String detail = "Failed to parse the HTTP response. The first "
                    + quoteLength
                    + " bytes are (in ASCII): \""
                    + quote
                    + "\".";

      return new InvalidResultXINSCallException(
         request, target, duration, detail, cause);
   }
}
