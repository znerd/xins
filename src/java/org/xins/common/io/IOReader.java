/*
 * $Id: IOReader.java,v 1.9 2007/09/18 11:21:09 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Utilities methods to read input streams.
 *
 * @version $Revision: 1.9 $ $Date: 2007/09/18 11:21:09 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0.
 */
public final class IOReader {

   /**
    * Read an InputStream completly and put the content of the input stream in
    * a String.
    *
    * @param inputStream
    *    the input stream to read, cannot be <code>null</code>.
    *
    * @return
    *    the content of the input stream using the default encoding, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>inputStream == null</code>.
    *
    * @throws IOException
    *    if there are some problems reading the input stream.
    */
   public static String readFully(InputStream inputStream) throws IllegalArgumentException, IOException {
      MandatoryArgumentChecker.check("inputStream", inputStream);

      BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
      StringWriter output = new StringWriter();
      char[] buffer = new char[1024];
      while (true) {
         int length = input.read(buffer);
         if (length == -1) break;
         output.write(buffer, 0, length);
      }
      inputStream.close();
      input.close();
      output.close();
      return output.toString();
   }
}
