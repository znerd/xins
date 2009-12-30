/*
 * $Id: LocalServletConfig.java,v 1.19 2007/09/18 08:45:09 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.xins.common.Log;
import org.xins.common.xml.SAXParserProvider;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is an implementation of the ServletConfig that can be
 * called locally.
 *
 * @version $Revision: 1.19 $ $Date: 2007/09/18 08:45:09 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class LocalServletConfig implements ServletConfig {

   /**
    * The name of the servlet.
    */
   private String _servletName;

   /**
    * The class of the servlet.
    */
   private String _servletClass;

   /**
    * The properties of the servlet.
    */
   private Properties _initParameters;

   /**
    * The servlet context.
    */
   private ServletContext _context;

   /**
    * The WAR file.
    */
   private File _warFile;

   /**
    * Creates a new Servlet configuration.
    *
    * @param warFileLocation
    *    the war file containing the servlet to deploy,
    *    cannot be <code>null</code>.
    */
   public LocalServletConfig(File warFileLocation) {
      _warFile = warFileLocation;
      _initParameters = new Properties();
      _context = new XINSServletContext(this);

      try {
         JarFile warFile = new JarFile(warFileLocation);
         JarEntry webxmlEntry = warFile.getJarEntry("WEB-INF/web.xml");
         InputStream webxmlInputStream = warFile.getInputStream(webxmlEntry);
         parseWebXML(webxmlInputStream);
      } catch (Exception ex) {

         Log.log_1512(ex);
      }
   }

   /**
    * Parses the web.xml file.
    *
    * @param webxmlInputStream
    *    the web.xml file input stream.
    *
    * @throws Exception
    *    if the file cannot be parsed for any reason.
    */
   private void parseWebXML(InputStream webxmlInputStream) throws Exception {
      DefaultHandler handler = new WebInfoParser();
      SAXParserProvider.get().parse(webxmlInputStream, handler);
      webxmlInputStream.close();
   }

   public String getInitParameter(String param) {
      return _initParameters.getProperty(param);
   }

   public String getServletName() {
      return _servletName;
   }

   /**
    * Gets the class name of the Servlet.
    *
    * @return
    *    the class name of the servlet, cannot be <code>null</code>.
    */
   public String getServletClass() {
      return _servletClass;
   }

   public ServletContext getServletContext() {
      return _context;
   }

   public Enumeration getInitParameterNames() {
      return _initParameters.keys();
   }

   /**
    * Gets the WAR file location.
    *
    * @return
    *    the WAR file, never <code>null</code>
    */
   File getWarFile() {
      return _warFile;
   }

   /**
    * Parser for the web.xml containing the information about the Servlet.
    */
   private class WebInfoParser extends DefaultHandler {
      /**
       * The PCDATA element of the tag that is actually parsed.
       */
      private StringBuffer _pcdata;

      /**
       * The name of the property that is currently parsed.
       */
      private String _paramName;

      public void startElement(String     namespaceURI,
                               String     localName,
                               String     qName,
                               Attributes atts)
      throws IllegalArgumentException, SAXException {
         _pcdata = new StringBuffer(80);
      }

      public void endElement(String namespaceURI,
                             String localName,
                             String qName)
      throws IllegalArgumentException, SAXException {
         if (qName.equals("param-name")) {
            _paramName = _pcdata.toString();
         } else if (qName.equals("param-value")) {
            _initParameters.setProperty(_paramName, _pcdata.toString());
         } else if (qName.equals("servlet-name")) {
            _servletName = _pcdata.toString();
         } else if (qName.equals("servlet-class")) {
            _servletClass = _pcdata.toString();
         }
         _pcdata = null;
      }

      public void characters(char[] ch, int start, int length)
      throws IndexOutOfBoundsException {

         if (_pcdata != null) {
            _pcdata.append(ch, start, length);
         }
      }

      public InputSource resolveEntity(String publicId, String systemId) {
         return new InputSource(new ByteArrayInputStream(new byte[0]));
      }
   }
}
