/*
 * $Id: ServletClassLoader.java,v 1.19 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Class used to get the Servlet Class loader.
 * The class loader returned is a child first class loader.
 *
 * @version $Revision: 1.19 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class ServletClassLoader {

   /**
    * Use the current class loader to load the servlet and the libraries.
    */
   public static final int USE_CURRENT_CLASSPATH = 1;

   /**
    * Load the Servlet code from the WAR file and use the current
    * classpath for the libraries.
    */
   public static final int USE_CLASSPATH_LIB = 2;

   /**
    * Load the servlet code from the WAR file and try to find the libraries
    * in the same directory as this xins-common.jar and &lt:parent&gt;/lib
    * directory.
    */
   public static final int USE_XINS_LIB = 3;

   /**
    * Load the servlet code and the libraries from the WAR file.
    * This may take some time as the libraries need to be extracted from the
    * WAR file.
    */
   public static final int USE_WAR_LIB = 4;

   /**
    * Load the servlet code and the standard libraries from the CLASSPATH.
    * Load the included external libraries from the WAR file.
    */
   public static final int USE_WAR_EXTERNAL_LIB = 5;

   /**
    * Gest the class loader that will loader the servlet.
    *
    * @param warFile
    *    the WAR file containing the Servlet.
    *
    * @param mode
    *    the mode in which the servlet should be loaded. The possible values are
    *    <code>USE_CURRENT_CLASSPATH</code>, <code>USE_CLASSPATH_LIB</code>,
    *    <code>USE_XINS_LIB</code>, <code>USE_WAR_LIB</code>,
    *    <code>USE_WAR_EXTERNAL_LIB</code>.
    *
    * @return
    *    the Class loader to use to load the Servlet.
    *
    * @throws IOException
    *    if the file cannot be read or is incorrect.
    */
   public static ClassLoader getServletClassLoader(File warFile, int mode) throws IOException {
      if (mode == USE_CURRENT_CLASSPATH) {
         return ServletClassLoader.class.getClassLoader();
      }

      List urlList = new ArrayList();

      // Add the WAR file so that it can locate web pages included in the WAR file
      urlList.add(warFile.toURI().toURL());

      if (mode != USE_WAR_EXTERNAL_LIB) {
         URL classesURL = new URL("jar:file:" + warFile.getAbsolutePath().replace(File.separatorChar, '/') + "!/WEB-INF/classes/");
         urlList.add(classesURL);
      }

      List standardLibs = new ArrayList();
      if (mode == USE_XINS_LIB) {
         String classLocation = ServletClassLoader.class.getProtectionDomain().getCodeSource().getLocation().toString();
         String commonJar = classLocation.substring(6).replace('/', File.separatorChar);
         if (!commonJar.endsWith("xins-common.jar")) {
            String xinsHome = System.getenv("XINS_HOME");
            commonJar = xinsHome + File.separator + "build" + File.separator + "xins-common.jar";
         }
         File baseDir = new File(commonJar).getParentFile();
         File[] xinsFiles = baseDir.listFiles();
         for (int i = 0; i < xinsFiles.length; i++) {
            if (xinsFiles[i].getName().endsWith(".jar")) {
               urlList.add(xinsFiles[i].toURI().toURL());
            }
         }
         File libDir = new File(baseDir, ".." + File.separator + "lib");
         File[] libFiles = libDir.listFiles();
         for (int i = 0; i < libFiles.length; i++) {
            if (libFiles[i].getName().endsWith(".jar")) {
               urlList.add(libFiles[i].toURI().toURL());
            }
         }
      }
      if (mode == USE_CLASSPATH_LIB || mode == USE_WAR_EXTERNAL_LIB) {
         String classPath = System.getProperty("java.class.path");
         StringTokenizer stClassPath = new StringTokenizer(classPath, File.pathSeparator);
         while (stClassPath.hasMoreTokens()) {
            String nextPath = stClassPath.nextToken();
            if (nextPath.toLowerCase().endsWith(".jar")) {
               standardLibs.add(nextPath.substring(nextPath.lastIndexOf(File.separatorChar) + 1));
            }
            urlList.add(new File(nextPath).toURI().toURL());
         }
      }
      if (mode == USE_WAR_LIB || mode == USE_WAR_EXTERNAL_LIB) {
         JarInputStream jarStream = new JarInputStream(new FileInputStream(warFile));
         JarEntry entry = jarStream.getNextJarEntry();
         while(entry != null) {
            String entryName = entry.getName();
            if (entryName.startsWith("WEB-INF/lib/") && entryName.endsWith(".jar") && !standardLibs.contains(entryName.substring(12))) {
               File tempJarFile = unpack(jarStream, entryName);
               urlList.add(tempJarFile.toURI().toURL());
            }
            entry = jarStream.getNextJarEntry();
         }
         jarStream.close();
      }
      URL[] urls = new URL[urlList.size()];
      for (int i=0; i<urlList.size(); i++) {
         urls[i] = (URL) urlList.get(i);
      }
      ClassLoader loader = new ChildFirstClassLoader(urls, ServletClassLoader.class.getClassLoader());
      Thread.currentThread().setContextClassLoader(loader);
      return loader;
   }

   /**
    * Unpack the specified entry from the JAR file.
    *
    * @param jarStream
    *    The input stream of the JAR file positioned at the entry.
    * @param entryName
    *    The name of the entry to extract.
    *
    * @return
    *    The extracted file. The created file is a temporary file in the
    *    temporary directory.
    *
    * @throws IOException
    *    if the JAR file cannot be read or is incorrect.
    */
   private static File unpack(JarInputStream jarStream, String entryName) throws IOException {
      String libName = entryName.substring(entryName.lastIndexOf('/') + 1, entryName.length() - 4);
      File tempJarFile = File.createTempFile("tmp_" + libName, ".jar");
      tempJarFile.deleteOnExit();
      FileOutputStream out = new FileOutputStream(tempJarFile);

      // Transfer bytes from the JAR file to the output file
      byte[] buf = new byte[8192];
      int len;
      while ((len = jarStream.read(buf)) > 0) {
         out.write(buf, 0, len);
      }
      out.close();
      return tempJarFile;
   }

   /**
    * An almost trivial no-fuss implementation of a class loader
    * following the child-first delegation model.
    *
    * @author <a href="http://www.qos.ch/log4j/">Ceki Gulcu</a>
    */
   private static class ChildFirstClassLoader extends URLClassLoader {

      public ChildFirstClassLoader(URL[] urls) {
         super(urls);
      }

      public ChildFirstClassLoader(URL[] urls, ClassLoader parent) {
         super(urls, parent);
      }

      public void addURL(URL url) {
         super.addURL(url);
      }

      public Class loadClass(String name) throws ClassNotFoundException {
         return loadClass(name, false);
      }

      /**
       * We override the parent-first behavior established by
       * java.land.Classloader.
       * <p>
       * The implementation is surprisingly straightforward.
       *
       * @param name
       *    the name of the class to load, should not be <code>null</code>.
       *
       * @param resolve
       *    flag that indicates whether the class should be resolved.
       *
       * @return
       *    the loaded class, never <code>null</code>.
       *
       * @throws ClassNotFoundException
       *    if the class could not be loaded.
       */
      protected Class loadClass(String name, boolean resolve)
      throws ClassNotFoundException {

         // First, check if the class has already been loaded
         Class c = findLoadedClass(name);

         // if not loaded, search the local (child) resources
         if (c == null) {
            try {
               c = findClass(name);
            } catch(ClassNotFoundException cnfe) {
               // ignore
            }
         }

         // If we could not find it, delegate to parent
         // Note that we do not attempt to catch any ClassNotFoundException
         if (c == null) {
            if (getParent() != null) {
               c = getParent().loadClass(name);
            } else {
               c = getSystemClassLoader().loadClass(name);
            }
         }

         // Resolve the class, if required
         if (resolve) {
            resolveClass(c);
         }

         return c;
      }

      /**
       * Override the parent-first resource loading model established by
       * java.land.Classloader with child-first behavior.
       *
       * @param name
       *    the name of the resource to load, should not be <code>null</code>.
       *
       * @return
       *    a {@link URL} for the resource, or <code>null</code> if it could
       *    not be found.
       */
      public URL getResource(String name) {

         URL url = findResource(name);

         // If local search failed, delegate to parent
         if (url == null) {
            url = getParent().getResource(name);
         }

         return url;
     }
   }
}
