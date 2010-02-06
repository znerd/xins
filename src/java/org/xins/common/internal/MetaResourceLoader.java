/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Meta resource loader.
 *
 * @version $Revision: 1.16 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public final class MetaResourceLoader {

   /**
    * Constructs a new <code>MetaResourceLoader</code> object.
    */
   private MetaResourceLoader() {
      // empty
   }
   
   /**
    * Retrieves a meta resource and returns it as a <code>URL</code>.
    * 
    * <p>To get an <code>InputStream</code>, just called
    * {@link URL#openStream() openStream} on the {@link URL} object returned.
    *
    * @param clazz
    *    the class that relates to the resource, cannot be <code>null</code>.
    *    
    * @param path
    *    the path to the meta resource, cannot be <code>null</code>.
    *    
    * @return
    *    the resource as a {@link URL}, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>clazz == null || path == null</code>.
    *    
    * @throws NoSuchResourceException
    *    if the resource could not be found.
    */
   public static final <T> URL getMetaResource(Class<T> clazz, String path)
   throws IllegalArgumentException, NoSuchResourceException {
      
      // Check preconditions
      MandatoryArgumentChecker.check("clazz", clazz, "path", path);
      
      // Load the resource
      String absPath = "/META-INF/" + path;
      URL        url = clazz.getResource(absPath);
      
      // Resource not found
      if (url == null) {
         // TODO: Log.log_XXX(LogLevel.ERROR, "Failed to load resource \"" + absPath + "\".");
         throw new NoSuchResourceException("Failed to load resource \"" + absPath + "\".");
      }
      
      // Resource found
      // TODO: Log.log_XXX(LogLevel.DEBUG, "Loaded \"" + absPath + "\".");
      
      return url;
   }

   /**
    * Finds the version for the specified class, by reading a meta resource.
    *
    * @param clazz
    *    the class to load the version for, cannot be <code>null</code>.
    *
    * @return
    *    the version, or <code>null</code> if it could not be found.
    *
    * @throws IllegalArgumentException
    *    if <code>clazz == null</code>.
    */
   public static final <T> String findVersion(Class clazz)
   throws IllegalArgumentException {

      // TODO: Review the exceptions/return

      // Check preconditions
      MandatoryArgumentChecker.check("clazz", clazz);

      // Determine the exact location for the file
      String packageName = clazz.getPackage().getName();
      String    filePath = packageName.replace('.', '/') + "/version.txt";

      String version = null;
      try {
         InputStream stream = getMetaResource(clazz, filePath).openStream();
         version = IOUtils.toString(stream, "UTF-8").trim();
      } catch (IOException cause) {
         System.err.println("I/O error while reading meta resource: " + filePath);
         cause.printStackTrace();
      } catch (NoSuchResourceException cause) {
         System.err.println("Failed to load version meta data for package " + packageName + '.');
         cause.printStackTrace();
      }

      return version;
   }
}
