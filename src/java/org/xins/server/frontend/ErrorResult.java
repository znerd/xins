/*
 * $Id: ErrorResult.java,v 1.4 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server.frontend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xins.common.text.TextUtils;
import org.xins.server.FunctionResult;

/**
 * Result for an error.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
class ErrorResult extends FunctionResult {

    private static SimpleDateFormat ERROR_DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.dd',' HH:mm:ss ");

   /**
    * Creates the result for a transformation error.
    *
    * @param exception
    *    the exception during the transformation, cannot be <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    */
   ErrorResult(Exception exception, HttpServletRequest httpRequest) {

      param("error.type", "TechnicalError");
      param("error.message", exception.getMessage());
      StringWriter stWriter = new StringWriter(360);
      PrintWriter printWriter = new PrintWriter(stWriter);
      exception.printStackTrace(printWriter);
      String stackTrace = stWriter.toString();
      param("error.stacktrace", stackTrace);
      String timeOfFailure = ERROR_DATE_FORMATTER.format(new Date());
      param("error.time", timeOfFailure);
      if (exception instanceof TransformerException) {
         TransformerException tex = (TransformerException) exception;
         SourceLocator locator = tex.getLocator();
         if (locator != null) {
            int line = locator.getLineNumber();
            int col = locator.getColumnNumber();
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            String detail = "line: " + line + "; col: " + col +
                  "; public ID: " + publicId + "; system ID: " + systemId;
            param("error.location", detail);
         }
      }

      String command = httpRequest.getParameter("command");
      if (!TextUtils.isEmpty(command)) {
         param("error.command", command);
      }
      String action = httpRequest.getParameter("action");
      if (!TextUtils.isEmpty(action)) {
         param("error.action", action);
      }
      String query = httpRequest.getQueryString();
      if (!TextUtils.isEmpty(query)) {
         param("error.query", query);
      }
   }

   /**
    * Return the XSLT to use to display the data of the Control command.
    *
    * @return
    *    the XSLT to display the Control result, never <code>null</code>.
    */
   static String getDefaultErrorTemplate() {
      String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
          "<xsl:output method=\"html\" indent=\"yes\" encoding=\"US-ASCII\"\n" +
          "doctype-public=\"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
          "doctype-system=\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"\n" +
          "omit-xml-declaration=\"yes\" />\n" +
          "<xsl:template match=\"commandresult\">\n" +
          "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n" +
            "<body>\n" +
              "<h2>\n" +
              "A technical error occured\n" +
              "</h2>\n" +
              "<h3>Command</h3>\n" +
              "<xsl:value-of select=\"parameter[@name='error.command']/text()\" />\n" +
              "<xsl:if test=\"parameter[@name='error.action']\">\n" +
                "<xsl:text> with the </xsl:text>\n" +
                "<xsl:value-of select=\"parameter[@name='error.action']/text()\" />\n" +
                "<xsl:text> action.</xsl:text>\n" +
              "</xsl:if>\n" +
              "<h3>Request</h3>\n" +
              "<xsl:value-of select=\"parameter[@name='error.query']/text()\" />\n" +
              "<h3>Error message</h3>\n" +
              "<xsl:value-of select=\"parameter[@name='error.message']/text()\" />\n" +
              "<xsl:if test=\"parameter[@name='error.location']\">\n" +
                "<h3>Error location</h3>\n" +
                "<xsl:value-of select=\"parameter[@name='error.location']/text()\" />\n" +
              "</xsl:if>\n" +
              "<h3>Error details</h3>\n" +
              "<pre>\n" +
              "<xsl:value-of select=\"parameter[@name='error.stacktrace']/text()\" />\n" +
              "</pre>\n" +
            "</body>\n" +
          "</html>\n" +
        "</xsl:template>\n" +
        "</xsl:stylesheet>";
      return result;
   }
}