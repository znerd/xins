/*
 * $Id: HTTPCaller.java,v 1.14 2007/09/18 11:20:52 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

import org.xins.common.text.ParseException;

/**
 * Utility class for making HTTP requests.
 *
 * @version $Revision: 1.14 $ $Date: 2007/09/18 11:20:52 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class HTTPCaller {

   public static HTTPCallerResult call(String httpVersion, String host, int port, String method, String queryString, Properties inputHeaders)
   throws IOException, ParseException {

      String eol = "\r\n";

      // Prepare a connection
      Socket socket = new Socket(host, port);

      byte[] buffer = new byte[16384];
      int bytesRead;
      try {

         // Get the input and output streams
         OutputStream out = socket.getOutputStream();
         InputStream  in  = socket.getInputStream();

         // Construct the output string
         String toWrite = method + ' ' + queryString + " HTTP/" + httpVersion + eol;
         if ("1.1".equals(httpVersion)) {
            toWrite += "Host: " + host + eol;
         }
         if (inputHeaders != null) {
            Enumeration names = inputHeaders.propertyNames();
            while (names.hasMoreElements()) {
               String key   = (String) names.nextElement();
               String value = inputHeaders.getProperty(key);

               toWrite += key + ": " + value + eol;
            }
         }
         toWrite += eol;

         // Write the output
         out.write(toWrite.getBytes("ISO-8859-1"));

         // Read the input
         bytesRead = in.read(buffer);
      } finally {
         try {
            socket.close();
         } catch (Throwable exception) {
            // ignore
         }
      }

      // Convert the response to a character string
      String response = new String(buffer, 0, bytesRead, "ISO-8859-1");

      // Prepare the result
      HTTPCallerResult result = new HTTPCallerResult();;

      // Get the first line
      int    index = response.indexOf(' ');
      String intro = response.substring(0, index);
      int   index2 = response.indexOf(eol);
      result.setStatus(response.substring(index + 1, index2));

      // Remove the part we processed
      response = response.substring(index2 + 2);

      // Get the headers
      boolean done = false;
      while (! done) {
         int nextEOL = response.indexOf(eol);
         if (nextEOL < 0) {
            return result;
         } else if (nextEOL == 0) {
            response = response.substring(2);
            done = true;
         } else {
            parseHeader(result, response.substring(0, nextEOL));
            response = response.substring(nextEOL + 2);
         }
      }

      // Get the body
      result.setBody(response);

      return result;
   }

   private static void parseHeader(HTTPCallerResult result, String header)
   throws ParseException{
      int index = header.indexOf(':');
      if (index < 1) {
         throw new ParseException();
      }

      // Get key and value
      String key   = header.substring(0, index);
      String value = header.substring(index + 1);

      result.addHeader(key, value);
   }
}

