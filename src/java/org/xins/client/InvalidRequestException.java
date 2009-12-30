/*
 * $Id: InvalidRequestException.java,v 1.28 2007/05/15 11:33:15 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.util.List;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;

/**
 * Exception thrown to indicate a standard error code was received that
 * indicates the request from the client-side is considered invalid by the
 * server-side.
 *
 * @version $Revision: 1.28 $ $Date: 2007/05/15 11:33:15 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public class InvalidRequestException extends StandardErrorCodeException {

   /**
    * Constructs a new <code>InvalidRequestException</code>.
    *
    * @param request
    *    the original request, guaranteed not to be <code>null</code>.
    *
    * @param target
    *    the target on which the request was executed, guaranteed not to be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, guaranteed to be &gt;= <code>0L</code>.
    *
    * @param resultData
    *    the data returned from the call, guaranteed to be <code>null</code>
    *    and must have an error code set.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null
    *          || result.{@link XINSCallResultData#getErrorCode() getErrorCode()} == null</code>.
    */
   InvalidRequestException(XINSCallRequest    request,
                           TargetDescriptor   target,
                           long               duration,
                           XINSCallResultData resultData)
   throws IllegalArgumentException {
      super(request, target, duration, resultData,
            determineDetail(resultData));
   }

   /**
    * Delegate for the constructor that determines the detail message based on
    * a <code>XINSCallResultData</code> object.
    *
    * @param result
    *    the {@link XINSCallResultData} instance, should not be
    *    <code>null</code>.
    *
    * @return
    *    the detail message for the constructor to use, never
    *    <code>null</code>.
    */
   private static final String determineDetail(XINSCallResultData result) {

      // Result must be unsuccessful
      String errorCode = result.getErrorCode();
      if (errorCode == null) {
         throw new IllegalArgumentException("result.getErrorCode() == null");
      }

      // Parse the data element
      Element element = result.getDataElement();
      return createMessage(element);
   }

   /**
    * Creates the message containing the details of the error.
    *
    * @param element
    *    the {@link Element} containing the details of the error, can be
    *    <code>null</code>.
    *
    * @return
    *    the message or <code>null</code> if <code>element == null</code>
    *    or empty.
    */
   static String createMessage(Element element) {

      // Parse the data element
      if (element == null) {
         return null;
      }

      StringBuffer detail = new StringBuffer(250);

      // Handle all missing parameters
      List missingParamElements = element.getChildElements("missing-param");
      if (missingParamElements != null) {
         int size = missingParamElements.size();
         for (int i = 0; i < size; i++) {
            Element e = (Element) missingParamElements.get(i);
            String paramName = e.getAttribute("param");
            String elementName = e.getAttribute("element");
            if (elementName == null && paramName != null && paramName.length() >= 1) {
               detail.append("No value given for required parameter \"" + paramName + "\". ");
            } else if (elementName != null &&  elementName.length() >= 1 && paramName != null && paramName.length() >= 1) {
               detail.append("No value given for required attribute \"" +
                     paramName + "\" in the element \"" + elementName + "\". ");
            }
         }
      }

      // Handle all invalid parameter values
      List invalidValueElements = element.getChildElements("invalid-value-for-type");
      if (invalidValueElements != null) {
         int size = invalidValueElements.size();
         for (int i = 0; i < size; i++) {
            Element e = (Element) invalidValueElements.get(i);
            String paramName = e.getAttribute("param");
            String invalidValue = e.getAttribute("value");
            String typeName = e.getAttribute("type");
            String elementName = e.getAttribute("element");
            if (!TextUtils.isEmpty(elementName) && !TextUtils.isEmpty(paramName)) {
               detail.append("The value ");
               if (invalidValue != null) {
                   detail.append("\"" + invalidValue + "\" ");
               }
               detail.append("for the attribute \"" + paramName + "\" in the element \""
                     + elementName + "\" is considered invalid for the type \""
                     + typeName + "\". ");
            } else if (!TextUtils.isEmpty(paramName)) {
               detail.append("The value ");
               if (invalidValue != null) {
                   detail.append("\"" + invalidValue + "\" ");
               }
               detail.append("for the parameter \"" + paramName
                     + "\" is considered invalid for the type \"" + typeName + "\". ");
            }
         }
      }

      // Handle all param-combo values
      List paramComboElements = element.getChildElements("param-combo");
      if (paramComboElements != null) {
         int size = paramComboElements.size();

         // Loop through all param-combo elements
         for (int i = 0; i < size; i++) {
            Element e = (Element) paramComboElements.get(i);

            // There should be a 'type' attribute
            String typeName = e.getAttribute("type");
            if (typeName == null || typeName.trim().length() < 1) {
               // TODO: Log?
               continue;
            }

            // There should be at least 2 'param' elements
            List paramList = e.getChildElements("param");
            if (paramList == null || paramList.size() < 2) {
               // TODO: Log?
               continue;
            }

            // Create detail message
            detail.append("Violated param-combo constraint of type \"");
            detail.append(typeName);
            detail.append("\" on parameters ");
            int paramCount = paramList.size();
            for (int j = 0; j < paramCount; j++) {
               Element e2 = (Element) paramList.get(j);
               String paramName = e2.getAttribute("name");
               if (paramName == null || paramName.trim().length() < 1) {
                  // TODO: Log?
                  continue;
               }

               detail.append("\"");
               detail.append(paramName);
               detail.append("\"");

               if (j == (paramCount - 1)) {
                  detail.append(". ");
               } else if (j == (paramCount - 2)) {
                  detail.append(" and ");
               } else {
                  detail.append(", ");
               }
            }
         }
      }

      // Remove the last space from the string, if there is any
      if (detail.length() >= 1) {
         detail.deleteCharAt(detail.length() - 1);
         return detail.toString();
      } else {
         return null;
      }
   }
}
