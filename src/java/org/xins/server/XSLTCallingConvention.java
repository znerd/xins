/*
 * $Id: XSLTCallingConvention.java,v 1.55 2007/09/18 08:45:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.Utils;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.manageable.InitializationException;
import org.xins.common.text.TextUtils;

/**
 * XSLT calling convention.
 * The XSLT calling convention input is the same as for the standard calling
 * convention. The XSLT calling convention output is the result of the XML
 * normally returned by the standard calling convention and the specified
 * XSLT.
 * The Mime type of the return data can be specified in the XSLT using the
 * media-type or method attribute of the XSL output element.
 * More information about the XSLT calling convention can be found in the
 * <a href="http://www.xins.org/docs/index.html">user guide</a>.
 *
 * @version $Revision: 1.55 $ $Date: 2007/09/18 08:45:06 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class XSLTCallingConvention extends StandardCallingConvention {

   /**
    * The name of the runtime property that defines if the templates should be
    * cached. Should be either <code>"true"</code> or <code>"false"</code>.
    * By default the cache is enabled.
    */
   protected static final String TEMPLATES_CACHE_PROPERTY = "templates.cache";

   /**
    * The name of the input parameter that specifies the location of the XSLT
    * template to use.
    */
   protected static final String TEMPLATE_PARAMETER = "_template";

   /**
    * The name of the input parameter used to clear the template cache.
    */
   protected static final String CLEAR_TEMPLATE_CACHE_PARAMETER = "_cleartemplatecache";

   /**
    * The XSLT transformer. Never <code>null</code>.
    */
   private final TransformerFactory _factory;

   /**
    * Flag that indicates whether the templates should be cached. This field
    * is set during initialization.
    */
   private boolean _cacheTemplates;

   /**
    * The prefix to use for the _template parameter.
    * This field is set during initialization.
    * If the value is <code>null</code> the _template parameter is not allowed.
    */
   private String _templatesPrefix;

   /**
    * Location of the XSLT templates. This field is initially
    * <code>null</code> and set during initialization.
    */
   private String _location;

   /**
    * Cache for the XSLT templates. Never <code>null</code>.
    */
   private Map<String, Templates> _templateCache;

   /**
    * Constructs a new <code>XSLTCallingConvention</code> object.
    */
   public XSLTCallingConvention() {

      // Create the transformer factory
      _factory = TransformerFactory.newInstance();

      // Initialize the template cache
      _templateCache = new HashMap<String, Templates>(89);
   }

   @Override
   protected void initImpl(PropertyReader runtimeProperties)
   throws MissingRequiredPropertyException,
          InvalidPropertyValueException,
          InitializationException {

      // Determine if the template cache should be enabled
      String cacheEnabled = runtimeProperties.get(TEMPLATES_CACHE_PROPERTY);
      initCacheEnabled(cacheEnabled);

      // Get the base directory of the style sheets.
      _location = getXSLTLocation(runtimeProperties, "source");

      // Determine whether template location can be passed as parameter
      _templatesPrefix = getXSLTLocation(runtimeProperties, "parameter.prefix");
   }

   /**
    * Determines if the template cache should be enabled. If no value is
    * passed, then by default the cache is enabled. An invalid value, however,
    * will trigger an {@link InvalidPropertyValueException}.
    *
    * @param cacheEnabled
    *    the value of the runtime property that specifies whether the cache
    *    should be enabled, can be <code>null</code>.
    *
    * @throws InvalidPropertyValueException
    *    if the value is incorrect.
    */
   private void initCacheEnabled(String cacheEnabled)
   throws InvalidPropertyValueException {

      // By default, the template cache is enabled
      if (TextUtils.isEmpty(cacheEnabled)) {
         _cacheTemplates = true;

      // Trim before comparing with 'true' and 'false'
      } else {
         cacheEnabled = cacheEnabled.trim();
         if ("true".equals(cacheEnabled)) {
            _cacheTemplates = true;
         } else if ("false".equals(cacheEnabled)) {
            _cacheTemplates = false;
         } else {
            throw new InvalidPropertyValueException(TEMPLATES_CACHE_PROPERTY,
               cacheEnabled, "Expected either \"true\" or \"false\".");
         }
      }

      // Log whether the cache is enabled or not
      if (_cacheTemplates) {
         Log.log_3440();
      } else {
         Log.log_3441();
      }
   }

   /**
    * Initializes the location for the XSLT templates.
    *
    * The name of the runtime property that defines the location of the XSLT
    * templates should indicate a directory, either locally or remotely.
    * Local locations will be interpreted as relative to the user home
    * directory. The value should be a URL or a relative directory.
    *
    * <p>Examples of valid locations include:
    *
    * <ul>
    *    <li><code>projects/dubey/xslt/</code></li>
    *    <li><code>file:///home/john.doe/projects/dubey/xslt/</code></li>
    *    <li><code>http://johndoe.com/projects/dubey/xslt/</code></li>
    *    <li><code>https://xslt.johndoe.com/</code></li>
    *    <li><code>http://xslt.mycompany.com/myapi/</code></li>
    *    <li><code>file:///c:/home/</code></li>
    * </ul>
    *
    * <p>XSLT template files must match the names of the corresponding
    * functions.
    *
    * @param runtimeProperties
    *    the runtime properties, cannot be <code>null</code>.
    *
    * @param propertySuffix
    *    the suffix of the runtime property we're looking for, cannot be <code>null</code>.
    *
    * @return
    *    the path location where to find the XSLT style sheet files or <code>null</code>
    *    if no location is specified.
    */
   private String getXSLTLocation(PropertyReader runtimeProperties, String propertySuffix) {

      // Get the value of the property
      String templatesProperty = "templates." + getAPI().getName() + ".xins-xslt." + propertySuffix;
      String location = runtimeProperties.get(templatesProperty);

      if (TextUtils.isEmpty(location)) {
         return null;
      }

      // If the value is not a URL, it's considered as a relative path.
      // Relative URLs use the user directory as base dir.
      if (location.indexOf("://") == -1) {

         // Attempt to convert the home directory to a URL
         String home    = System.getProperty("user.dir");
         String homeURL = "";
         try {
            homeURL = new File(home).toURI().toURL().toString();

         // If the conversion to a URL failed, then just use the original
         } catch (IOException exception) {
            Utils.logIgnoredException(exception);
         }

         // Prepend the home directory URL
         location = homeURL + location;
      }

      // Log the base directory for XSLT templates
      Log.log_3442(getAPI().getName(), propertySuffix, location);
      return location;
   }

   @Override
   protected void convertResultImpl(HttpServletRequest  httpRequest,
                                    FunctionRequest     xinsRequest,
                                    HttpServletResponse httpResponse,
                                    FunctionResult      xinsResult)
   throws IOException {

      // If the request is to clear the cache, just clear the cache.
      if ("true".equals(httpRequest.getParameter(CLEAR_TEMPLATE_CACHE_PARAMETER))) {
         _templateCache.clear();
         PrintWriter out = httpResponse.getWriter();
         out.write("Done.");
         out.close();
         return;
      }

      // Get the XML output similar to the standard calling convention.
      StringWriter xmlOutput = new StringWriter(1024);
      CallResultOutputter.output(xmlOutput, xinsResult);
      xmlOutput.close();

      // Get the location of the XSLT file.
      String xsltLocation = null;
      String templatesSuffix = httpRequest.getParameter(TEMPLATE_PARAMETER);
      if (_templatesPrefix != null && templatesSuffix != null) {
         if (templatesSuffix.indexOf("..") != -1) {
            throw new IOException("Incorrect " + TEMPLATE_PARAMETER + " parameter: " + templatesSuffix);
         }
         xsltLocation = _templatesPrefix + templatesSuffix;
      }
      if (_templatesPrefix == null && templatesSuffix != null) {
         throw new IOException(TEMPLATE_PARAMETER + " parameter not allowed.");
      }
      if (xsltLocation == null) {
         if (_location == null) {
            throw new IOException("No location specified for the XSLT stylesheets.");
         }
         xsltLocation = _location + httpRequest.getParameter("_function") + ".xslt";
      }

      try {

         // Load the template or get it from the cache.
         Templates templates;
         if (_cacheTemplates && _templateCache.containsKey(xsltLocation)) {
            templates = _templateCache.get(xsltLocation);
         } else {
            Log.log_3443(xsltLocation);
            templates = _factory.newTemplates(new StreamSource(xsltLocation));
            if (_cacheTemplates) {
               _templateCache.put(xsltLocation, templates);
            }
         }

         // Proceed to the transformation.
         Transformer xformer = templates.newTransformer();
         Source source = new StreamSource(new StringReader(xmlOutput.toString()));
         Writer buffer = new StringWriter(4096);
         Result result = new StreamResult(buffer);
         xformer.transform(source, result);

         // Determine the MIME type for the output.
         String mimeType = getContentType(templates.getOutputProperties());
         if (mimeType != null) {
            httpResponse.setContentType(mimeType);
         }

         httpResponse.setStatus(HttpServletResponse.SC_OK);
         PrintWriter out = httpResponse.getWriter();
         out.print(buffer.toString());
         out.close();
      } catch (Exception exception) {
         if (exception instanceof IOException) {
            throw (IOException) exception;
         } else {
            String message = "Cannot transform the result with the XSLT "
                           + "located at \"" + xsltLocation + "\".";
            IOException ioe = new IOException(message);
            ioe.initCause(exception);
            throw ioe;
         }
      }
   }

   /**
    * Gets the MIME type and the character encoding to return for the HTTP response.
    *
    * @param outputProperties
    *    the output properties defined in the XSLT, never <code>null</code>.
    *
    * @return
    *    the content type, never <code>null</code>.
    */
   private String getContentType(Properties outputProperties) {
      String mimeType = outputProperties.getProperty("media-type");
      if (mimeType == null) {
         String method = outputProperties.getProperty("method");
         if ("xml".equals(method)) {
            mimeType = "text/xml";
         } else if ("html".equals(method)) {
            mimeType = "text/html";
         } else if ("text".equals(method)) {
            mimeType = "text/plain";
         }
      }
      String encoding = outputProperties.getProperty("encoding");
      if (mimeType != null && encoding != null) {
         mimeType += "; charset=" + encoding;
      }
      return mimeType;
   }
}
