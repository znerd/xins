/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.TextUtils;
import org.xins.common.types.standard.Hex;

/**
 * Servlet request wrapper to support RFC 1867 multipart form submissions.
 * Note that all file uploads will be stored in memory, inside the request.
 * All metadata, such as the file names, are discarded, only the file contents
 * are available.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
@SuppressWarnings("deprecation")
final class MultipartServletRequestWrapper
extends HttpServletRequestWrapper {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>IOException</code> with the specified cause
    * exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    a correctly initialized {@link IOException}, never <code>null</code>.
    */
   static final IOException newIOException(String message, Throwable cause) {
      IOException e = new IOException(message);
      if (cause != null) {
         e.initCause(cause);
      }
      return e;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MultipartServletRequestWrapper</code>.
    *
    * @param httpRequest
    *    the original {@link HttpServletRequest} to interpret and to wrap
    *    around, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>httpRequest == null</code>.
    *
    * @throws IOException
    *    in case the request could not be analyzed or in case of a different
    *    kind of I/O error.
    */
   MultipartServletRequestWrapper(HttpServletRequest httpRequest)
   throws IllegalArgumentException, IOException {
      
      // Explicitly invoke superclass constructor
      super(httpRequest);

      // Check preconditions
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

      // Prepare the Commons FileUpload library
      FileItemFactory diskStore = new DiskFileItemFactory();
      ServletFileUpload  upload = new ServletFileUpload(diskStore);

      // Parse the request
      List itemList;
      try {
         itemList = upload.parseRequest(httpRequest);
      } catch (FileUploadException cause) {
         throw newIOException("Failed to parse HTTP file upload (RFC 1867) request.", cause);
      }

      // Convert the list to a Map
      _parameters = new HashMap<String,String>();
      for (int i = 0; i < itemList.size(); i++) {
         FileItem item = (FileItem) itemList.get(i);
         String   name = item.getFieldName();

         if (item.isFormField()) {
            String value = item.getString();
            _parameters.put(name, value);
         } else {

            // The file name is not stored and FormState has one value per field
            // String fileName = item.getName();
            // _parameters.put(name + "Name", fileName);
            try {

               // Prepare for reading the input stream
               InputStream   inputContent = item.getInputStream();
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               int         availableBytes = inputContent.available();

               // Read all input
               while (availableBytes > 0) {
                  byte[] buffer = new byte[availableBytes];
                  inputContent.read(buffer);
                  baos.write(buffer);
                  availableBytes = inputContent.available();
               }

               // Convert the file content to a hex string
               byte[] fileContent = baos.toByteArray();
               _parameters.put(name, Hex.toString(fileContent));

               // Close the streams
               inputContent.close();
               baos.close();

            // I/O error
            } catch (IOException ioe) {
               throw newIOException("Failed to read the input file.", ioe);
            }
         }
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The parameter values, indexed by name. Never <code>null</code>,
    * completely and permanently initialized in the constructor.
    */
   private Map<String,String> _parameters;
      

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   @Override
   public Map getParameterMap() {
      return Collections.unmodifiableMap(_parameters);
   }
      
   @Override
   public String getParameter(String name) {
      return _parameters.get(name);
   }
      
   @Override
   public Enumeration getParameterNames() {
      return Collections.enumeration(_parameters.keySet());
   }
      
   @Override
   public String[] getParameterValues(String name) {
      String[] result = new String[1];
      result[0] = _parameters.get(name);
      return result;
   }
}
