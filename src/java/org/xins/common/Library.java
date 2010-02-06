/*
 * $Id: Library.java,v 1.16 2007/03/16 09:54:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * Class that represents the XINS/Java Common Library.
 *
 * @version $Revision: 1.16 $ $Date: 2007/03/16 09:54:58 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class Library {

   /**
    * The version of this library, lazily initialized.
    */
   private static String VERSION;

   /**
    * Initializes this class, loading the version number once.
    */
   static {
      String filePath = "version.txt";
      try {
         InputStream stream = getMetaResourceAsStream(Library.class, filePath);
         VERSION = IOUtils.toString(stream, "UTF-8").trim();
      } catch (IOException cause) {
         System.err.println("I/O error while reading meta resource: " + filePath);
      }
   }

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }
   
   /**
    * Returns the name of this library.
    * 
    * @return
    *    the name of this library, never <code>null</code>;
    *    for example <code>"XINS/Java Common Library"</code>.
    *    
    * @since XINS 3.0
    */
   public static final String getName() {
      return "XINS/Java Common Library";
   }

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"3.0"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return VERSION;
   }
   
   /**
    * Prints the name and version of this library.
    * 
    * @param args
    *    the command line arguments; will be ignored.
    *
    * @since XINS 3.0
    */
   public static final void main(String[] args) {
      System.out.println(getName() + " " + getVersion());
   }
   
   /**
    * Retrieves a meta resource and returns it as a <code>URL</code>.
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
    *    if <code>path == null</code>.
    *    
    * @throws NoSuchResourceException
    *    if the resource could not be found.
    *
    * @since XINS 3.0
    */
   public static final <T> URL getMetaResource(Class<T> clazz, String path)
   throws IllegalArgumentException, NoSuchResourceException {
      
      // Check preconditions
      MandatoryArgumentChecker.check("clazz", clazz, "path", path);
      
      // Load the resource
      String absPath = "/META-INF/" + path;
      URL        url = clazz.getResource(absPath);
      
      // Resource not found - this is fatal
      if (url == null) {
         // TODO: Log.log_XXX(LogLevel.ERROR, "Failed to load resource \"" + absPath + "\".");
         throw new NoSuchResourceException("Failed to load resource \"" + absPath + "\".");
      }
      
      // TODO: Log.log_XXX(LogLevel.DEBUG, "Loaded \"" + absPath + "\".");
      
      return url;
   }
   
   /**
    * Retrieves a meta resource and returns it as an <code>InputStream</code>.
    * 
    * @param clazz
    *    the class that relates to the resource, cannot be <code>null</code>.
    *    
    * @param path
    *    the path to the meta resource, cannot be <code>null</code>.
    *    
    * @return
    *    the resource as an {@link InputStream}.
    *
    * @throws IllegalArgumentException
    *    if <code>path == null</code>.
    *    
    * @throws NoSuchResourceException
    *    if the resource could not be found.
    *    
    * @throws IOException
    *    if the stream could not be opened.
    *
    * @since XINS 3.0
    */
   public static final <T> InputStream getMetaResourceAsStream(Class<T> clazz, String path)
   throws IllegalArgumentException, NoSuchResourceException, IOException {
      return getMetaResource(clazz, path).openStream();
   }
}
