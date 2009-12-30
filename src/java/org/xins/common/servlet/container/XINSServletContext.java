/*
 * $Id: XINSServletContext.java,v 1.23 2007/09/18 08:45:09 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.xins.common.Log;
import org.xins.common.Utils;

/**
 * This class is an implementation of the ServletContext that can be
 * called locally.
 *
 * @version $Revision: 1.23 $ $Date: 2007/09/18 08:45:09 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class XINSServletContext implements ServletContext {

   /**
    * The configuration of the servlet.
    */
   private LocalServletConfig _config;

   /**
    * The root URL for the servlet.
    */
   private String _rootURL;

   /**
    * Creates a new <code>XINSServletContext</code> instance.
    */
   public XINSServletContext() {
      // empty
   }

   /**
    * Creates a new <code>XINSServletContext</code> with the specified
    * configuration.
    *
    * @param config
    *    the config of the servlet, can be <code>null</code>.
    */
   XINSServletContext(LocalServletConfig config) {
      _config = config;
      _rootURL = "jar:" + config.getWarFile().toURI().toString() + "!";
   }

   public void removeAttribute(String str) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   public Servlet getServlet(String str) {
      throw new UnsupportedOperationException();
   }

   public Set getResourcePaths(String str) {
      throw new UnsupportedOperationException();
   }

   public Object getAttribute(String str) {
      throw new UnsupportedOperationException();
   }

   public ServletContext getContext(String str) {
      throw new UnsupportedOperationException();
   }

   public String getInitParameter(String str) {
      throw new UnsupportedOperationException();
   }

   public String getMimeType(String str) {
      throw new UnsupportedOperationException();
   }

   public RequestDispatcher getNamedDispatcher(String str) {
      throw new UnsupportedOperationException();
   }

   public String getRealPath(String str) {

      // The WAR file is not unpacked
      return null;
   }

   public RequestDispatcher getRequestDispatcher(String str) {
      throw new UnsupportedOperationException();
   }

   public URL getResource(String str) {
      if (!str.startsWith("/")) {
         str = "/" + str;
      }
      try {
         return new URL(_rootURL + str);
      } catch (MalformedURLException muex) {
         Log.log_1513(_rootURL + str);
         return null;
      }
   }

   public InputStream getResourceAsStream(String str) {
      try {
         JarFile warFile = new JarFile(_config.getWarFile());
         JarEntry entry = warFile.getJarEntry(str);
         if (entry == null) {
            Log.log_1514(str, "No entry.");
            return null;
         } else {
            return warFile.getInputStream(entry);
         }
      } catch (IOException ioe) {
         Log.log_1514(str, ioe.getMessage());
         return null;
      }
   }

   @Deprecated
   public void log(Exception exception, String msg) {
      log(msg, exception);
   }

   public void log(String msg) {
      Log.log_1510(msg);
   }

   public void log(String msg, Throwable throwable) {
      Log.log_1511(throwable, msg);
   }

   public void setAttribute(String str, Object obj) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   public Enumeration getServlets() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   public Enumeration getServletNames() {
      throw new UnsupportedOperationException();
   }

   public String getServletContextName() {
      throw new UnsupportedOperationException();
   }

   public String getServerInfo() {
      String osName    = System.getProperty("os.name"   );
      String osVersion = System.getProperty("os.version");
      String osArch    = System.getProperty("os.arch"   );
      String os = osName + " " + osVersion + "/" + osArch;
      return "XINS Servlet Test Container (" + os + ')';
   }

   public Enumeration getAttributeNames() {
      throw new UnsupportedOperationException();
   }

   public Enumeration getInitParameterNames() {
      throw new UnsupportedOperationException();
   }

   public int getMajorVersion() {
      return 2;
   }

   public int getMinorVersion() {
      return 3;
   }
}
