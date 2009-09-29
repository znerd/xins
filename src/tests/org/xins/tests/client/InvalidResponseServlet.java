/*
 * $Id: InvalidResponseServlet.java,v 1.12 2007/09/18 11:21:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns invalid XINS results.
 *
 * @version $Revision: 1.12 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class InvalidResponseServlet extends HttpServlet {

   /**
    * Creates a new instance of InvalidResponseServlet
    */
   public InvalidResponseServlet() {
   }

   /**
    * Handles a request to this servlet (wrapper method). If any of the
    * arguments is <code>null</code>, then the behaviour of this method is
    * undefined.
    *
    * @param request
    *    the servlet request, should not be <code>null</code>.
    *
    * @param response
    *    the servlet response, should not be <code>null</code>.
    *
    * @throws IOException
    *    if there is an error error writing to the response output stream.
    */
   public void service(HttpServletRequest request, HttpServletResponse response)
   throws IOException {
      String function = request.getParameter("_function");
      if (function == null) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         throw new IOException("Invalid request, no \"_function\" parameter passed.");
      }
      String xmlResponse = null;

      if (function.equals("SimpleTypes")) {
         xmlResponse = getInvalidSimpleTypesResult();
      } else if (function.equals("DefinedTypes")) {
         xmlResponse = getInvalidDefinedTypesResult();
      } else if (function.equals("ResultCode")) {
         xmlResponse = getInvalidResultCodeResult();
      } else if (function.equals("DataSection")) {
         xmlResponse = getInvalidDataSectionResult();
      } else if (function.equals("DataSection2")) {
         xmlResponse = getInvalidDataSection2Result();
      } else if (function.equals("RuntimeProps")) {
         xmlResponse = getValidRuntimePropsResult();
      }

      if (xmlResponse != null) {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/xml;charset=UTF-8");
         Writer writer = response.getWriter();
         writer.write(xmlResponse);
         writer.close();
      } else {
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
   }

   /**
    * Returns an invalid result for the SimpleTypes function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidSimpleTypesResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result>"+
             "<param name=\"outputInt\">16</param>"+
             "<param name=\"outputShort\">-1</param>"+
             "<param name=\"outputLong\">14</param>"+
             "<param name=\"outputFloat\">3.5</param>"+
             "<param name=\"outputDouble\">3.1415</param>"+
             "<param name=\"outputDate\">20040621</param>"+
             "</result>";
   }

   /**
    * Returns an invalid result for the DefinedTypes function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidDefinedTypesResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result/>";
   }

   /**
    * Returns an invalid result for the ResultCode function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidResultCodeResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result errorcode=\"InvalidNumber\">"+
             "</result>";
   }

   /**
    * Returns an invalid result for the DataSection function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidDataSectionResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result>" +
             "<data>" +
             "<user address=\"12 Madison Avenue\">This user has the root authorisation.</user>" +
             "</data>" +
             "</result>";
   }

   /**
    * Returns an invalid result for the DataSection2 function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getInvalidDataSection2Result() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result>" +
             "<data>" +
             "<packet destination=\"20 West Street, New York\">" +
             "<product id=\"123456\" price=\"12\" />" +
             "<product id=\"321654\" price=\"23\" />" +
             "</packet>" +
             "<packet destination=\"55 Kennedy lane, Washinton DC\">" +
             "<product id=\"123456\" price=\"bla\" />" +
             "</packet>" +
             "</data>" +
             "</result>";
   }

   /**
    * Returns an valid result for the RuntimeProps function.
    *
    * @returns
    *    the valid result as XML String.
    */
   private String getValidRuntimePropsResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result code=\"_InvalidRequest\" />";
   }
}
