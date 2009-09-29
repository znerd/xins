/*
 * $Id: HTTPQueryHandler.java,v 1.4 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.Socket;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.PropertyReader;
import org.xins.common.text.ParseException;

/**
 * HTTP query received to be handled by the servlet.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
class HTTPQueryHandler extends Thread {

   /**
    * The encoding of the request.
    */
   private static final String REQUEST_ENCODING = "ISO-8859-1";

   /**
    * The map containing the MIME type information. Never <code>null</code>
    */
   private static final FileNameMap MIME_TYPES_MAP = URLConnection.getFileNameMap();

   /**
    * The line separator used by the HTTP protocol.
    */
   private static final String CRLF = "\r\n";

   /**
    * The instance number for this created query.
    */
   private static int _instanceNumber = 0;

   /**
    * The socket of the HTTP query.
    */
   private final Socket _client;

   /**
    * Mapping between the path and the servlet.
    */
   private final Map _servlets;

   /**
    * Creates a new HTTPQueryHandler to handle the HTTP query sent by the client.
    *
    * @param client
    *    the connection with the client, cannot be <code>null</code>.
    *
    * @param servlets
    *    the mapping between the path and the servlets, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>client == null || servlets == null</code>.
    */
   public HTTPQueryHandler(Socket client, Map servlets) throws IllegalArgumentException {

      // Check argument
      MandatoryArgumentChecker.check("client", client, "servlets", servlets);

      _client = client;
      _servlets = servlets;
      synchronized (servlets) {
         _instanceNumber++;
      }
      setName("XINS Query handler #" + _instanceNumber);
   }

   public void run() {
      try {
         serviceClient(_client);
      } catch (Exception ex) {

         // If anything goes wrong still continue accepting clients
         Utils.logIgnoredException(ex);
      } finally {
         try {
            _client.close();
         } catch (Throwable exception) {
            // ignore
         }
      }
   }

   /**
    * This method is invoked when a client connects to the server.
    *
    * @param client
    *    the connection with the client, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>client == null</code>.
    *
    * @throws IOException
    *    if the query is not handled correctly.
    */
   public void serviceClient(Socket client)
   throws IllegalArgumentException, IOException {

      // Check argument
      MandatoryArgumentChecker.check("client", client);

      InputStream  inbound  = client.getInputStream();
      OutputStream outbound = client.getOutputStream();

      // Delegate to httpQuery in a way it does not have to bother with
      // closing the streams
      try {
         httpQuery(inbound, outbound);

      // Clean up for httpQuery, if necessary
      } finally{
         if (inbound != null) {
            try {
               inbound.close();
            } catch (Throwable exception) {
               Utils.logIgnoredException(exception);
            }
         }
         if (outbound != null) {
            try {
               outbound.close();
            } catch (Throwable exception) {
               Utils.logIgnoredException(exception);
            }
         }
      }
   }

   /**
    * This method parses the data sent from the client to get the input
    * parameters and format the result as a compatible HTTP result.
    * This method will used the servlet associated with the passed virtual
    * path. If no servlet is associated with the virtual path, the servlet with
    * the virtual path "/" is used as default. If there is no servlet then with
    * the virtual path "/" is found then HTTP 404 is returned.
    *
    * @param in
    *    the input byte stream that contains the request sent by the client.
    *
    * @param out
    *    the output byte stream that must be fed the response towards the
    *    client.
    *
    * @throws IOException
    *    if the query is not handled correctly.
    *
    * @since XINS 1.5.0.
    */
   public void httpQuery(InputStream  in, OutputStream out)
   throws IOException {

      // Read the input
      // XXX: Buffer size determines maximum request size
      char[] buffer = new char[16384];
      BufferedReader inReader = new BufferedReader(new InputStreamReader(in, REQUEST_ENCODING));
      int lengthRead = inReader.read(buffer);
      if (lengthRead < 0) {
         sendBadRequest(out);
         return;
      }
      String request = new String(buffer, 0, lengthRead);
      //byte[] requestBytes = IOReader.readFullyAsBytes(in);
      //String request = new String(requestBytes, 0, requestBytes.length, REQUEST_ENCODING);

      // Read the first line
      int eolIndex = request.indexOf(CRLF);
      if (eolIndex < 0) {
         sendBadRequest(out);
         return;
      }

      // The first line must end with "HTTP/1.0" or "HTTP/1.1"
      String line = request.substring(0, eolIndex);
      request = request.substring(eolIndex + 2);
      if (! (line.endsWith(" HTTP/1.1") || line.endsWith(" HTTP/1.0"))) {
         sendBadRequest(out);
         return;
      }

      // Cut off the last part
      line = line.substring(0, line.length() - 9);

      // Find the space
      int spaceIndex = line.indexOf(' ');
      if (spaceIndex < 1) {
         sendBadRequest(out);
         return;
      }

      // Determine the method
      String method = line.substring(0, spaceIndex);

      // Determine the query string
      String url = line.substring(spaceIndex + 1);
      if ("".equals(url)) {
         sendBadRequest(out);
         return;
      } else if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
         url = url.replace(',', '&');
      }

      // Normalize the query string
      if ("GET".equals(method) && url.endsWith("/") && getClass().getResource(url + "index.html") != null) {
         url += "index.html";
      }

      // Read the headers
      HashMap inHeaders = new HashMap();
      boolean done = false;
      while (! done) {
         int nextEOL = request.indexOf(CRLF);
         if (nextEOL <= 0) {
            done = true;
         } else {
            try {
               parseHeader(inHeaders, request.substring(0, nextEOL));
            } catch (ParseException exception) {
               sendBadRequest(out);
               return;
            }
            request = request.substring(nextEOL + 2);
         }
      }

      // Determine the body contents
      String body = "".equals(request)
                  ? ""
                  : request.substring(2);


      // Response encoding defaults to request encoding
      String responseEncoding = REQUEST_ENCODING;

      // Handle the case that a web page is requested
      boolean getMethod = method.equals("GET") || method.equals("HEAD");
      String httpResult;
      if (getMethod && url.indexOf('?') == -1 && !url.endsWith("/") && !"*".equals(url)) {
         httpResult = readWebPage(url);

      // No web page requested
      } else {

         // Determine the content type
         String inContentType = getHeader(inHeaders, "Content-Type");

         // If www-form encoded, then append the body to the query string
         if ((inContentType == null || inContentType.startsWith("application/x-www-form-urlencoded")) &&
               body.length() > 0) {
            // XXX: What if the URL already contains a question mark?
            url += '?' + body;
            body = null;
         }

         // Locate the path of the URL
         String virtualPath = url;
         if (virtualPath.indexOf('?') != -1) {
            virtualPath = virtualPath.substring(0, url.indexOf('?'));
         }
         if (virtualPath.endsWith("/") && virtualPath.length() > 1) {
            virtualPath = virtualPath.substring(0, virtualPath.length() - 1);
         }

         // Get the Servlet according to the path
         LocalServletHandler servlet = findServlet(virtualPath);

         // If no servlet is found return 404
         if (servlet == null) {
            sendError(out, "404 Not Found");
            return;
         } else {

            // Query the Servlet
            XINSServletResponse response = servlet.query(method, url, body, inHeaders);

            // Create the HTTP answer
            StringBuffer sbHttpResult = new StringBuffer();
            sbHttpResult.append("HTTP/1.1 " + response.getStatus() + " " +
                  HttpStatus.getStatusText(response.getStatus()) + CRLF);
            PropertyReader outHeaders = response.getHeaders();
            for (String nextHeader : outHeaders.names()) {
               String headerValue = outHeaders.get(nextHeader);
               if (headerValue != null) {
                  sbHttpResult.append(nextHeader + ": " + headerValue + "\r\n");
               }
            }

            String result = response.getResult();
            if (result != null) {
               responseEncoding = response.getCharacterEncoding();
               int length = response.getContentLength();
               if (length < 0) {
                  length = result.getBytes(responseEncoding).length;
               }
               sbHttpResult.append("Content-Length: " + length + "\r\n");
               sbHttpResult.append("Connection: close\r\n");
               sbHttpResult.append("\r\n");
               sbHttpResult.append(result);
            }
            httpResult = sbHttpResult.toString();
         }
      }

      byte[] bytes = httpResult.getBytes(responseEncoding);
      out.write(bytes, 0, bytes.length);
      out.flush();
   }


   /**
    * Finds the servlet that should handle a request at the specified virtual
    * path.
    *
    * @param path
    *    the virtual path, cannot be <code>null</code>.
    *
    * @return
    *    the servlet that was found, or <code>null</code> if none was found.
    *
    * @throws NullPointerException
    *    if <code>path == null</code>.
    */
   private LocalServletHandler findServlet(String path)
   throws NullPointerException {

      // Special case is path "*"
      if ("*".equals(path)) {
         path = "/";
      }

      // If the path does not end with a slash, then add one,
      // to avoid checking that option
      if (path.charAt(path.length() - 1) != '/') {
         path += '/';
      }

      LocalServletHandler servlet;
      do {

         // Find a servlet at this path
         servlet = (LocalServletHandler) _servlets.get(path);

         // If not found, then strip off the last part of the path
         // E.g. "/objects/boats/Cherry"  becomes "/objects/boats/"
         // and  "/objects/boats/Cherry/" becomes "/objects/boats/"
         if (servlet == null) {

            // Remove the trailing slash, if any
            int lastPos = path.length() - 1;
            if (path.charAt(lastPos) == '/') {
               path = path.substring(0, lastPos);
            }

            // Cut up until and including the last slash, if appropriate
            if (path.length() > 0) {
               int i = path.lastIndexOf('/');
               path = path.substring(0, i + 1);
            }
         }

      } while (servlet == null && path.length() > 0);

      return servlet;
   }

   /**
    * Sends an HTTP error back to the client.
    *
    * @param out
    *    the output stream to contact the client.
    *
    * @param status
    *    the HTTP error code status.
    *
    * @throws IOException
    *    if the error cannot be sent.
    */
   private void sendError(OutputStream out, String status)
   throws IOException {
      String httpResult = "HTTP/1.1 " + status + CRLF + CRLF;
      byte[] bytes = httpResult.getBytes(REQUEST_ENCODING);
      out.write(bytes, 0, bytes.length);
      out.flush();
   }

   /**
    * Sends an HTTP bad request back to the client.
    *
    * @param out
    *    the output stream to contact the client.
    *
    * @throws IOException
    *    if the error cannot be sent.
    */
   private void sendBadRequest(OutputStream out)
   throws IOException {
      sendError(out, "400 Bad Request");
   }

   /**
    * Parses an HTTP header.
    *
    * @param headers
    *    the headers already collected.
    *
    * @param header
    *    the line of the header to be parsed.
    *
    * @throws ParseException
    *    if the header is incorrect
    */
   private static void parseHeader(HashMap headers, String header)
   throws ParseException{
      int index = header.indexOf(':');
      if (index < 1) {
         throw new ParseException();
      }

      // Get key and value
      String key   = header.substring(0, index);
      String value = header.substring(index + 1);

      // Always convert the key to upper case
      key = key.toUpperCase();

      // Always trim the value
      value = value.trim();

      // XXX: Only one header supported
      if (headers.get(key) != null) {
         throw new ParseException();
      }

      // Store the key-value combo
      headers.put(key, value);
   }

   /**
    * Gets a HTTP header from the request.
    *
    * @param headers
    *    the list of the headers.
    *
    * @param key
    *    the name of the header.
    *
    * @return
    *    the header value for the specified key or <code>null</code> if the
    *    key is not in the haeders.
    */
   String getHeader(HashMap headers, String key) {
      return (String) headers.get(key.toUpperCase());
   }

   /**
    * Reads the content of a web page.
    *
    * @param url
    *    the location of the content, cannot be <code>null</code>.
    *
    * @return
    *    the HTTP response to return, never <code>null</code>.
    *
    * @throws IOException
    *    if an error occcurs when reading the URL.
    */
   private String readWebPage(String url) throws IOException {
      String httpResult;
      if (getClass().getResource(url) != null) {
         InputStream urlInputStream = getClass().getResourceAsStream(url);
         ByteArrayOutputStream contentOutputStream = new ByteArrayOutputStream();
         byte[] buf = new byte[8192];
         int len;
         while ((len = urlInputStream.read(buf)) > 0) {
            contentOutputStream.write(buf, 0, len);
         }
         contentOutputStream.close();
         urlInputStream.close();
         String content = contentOutputStream.toString("ISO-8859-1");

         httpResult = "HTTP/1.1 200 OK\r\n";
         String fileName = url.substring(url.lastIndexOf('/') + 1);
         httpResult += "Content-Type: " + MIME_TYPES_MAP.getContentTypeFor(fileName) + "\r\n";
         int length = content.getBytes("ISO-8859-1").length;
         httpResult += "Content-Length: " + length + "\r\n";
         httpResult += "Connection: close\r\n";
         httpResult += "\r\n";
         httpResult += content;
      } else {
         httpResult = "HTTP/1.1 404 Not Found\r\n";
      }
      return httpResult;
   }
}
