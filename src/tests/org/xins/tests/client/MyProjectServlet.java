/*
 * $Id: MyProjectServlet.java,v 1.8 2007/09/18 11:21:05 agoubard Exp $
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
 * @version $Revision: 1.8 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class MyProjectServlet extends HttpServlet {

   /**
    * Creates a new instance of MyProjectServlet
    */
   public MyProjectServlet() {
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
      if (function.equals("MyFunction")) {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/xml;charset=UTF-8");
         Writer writer = response.getWriter();
         writer.write(getResultCodeResult());
         writer.close();
      } else {
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
   }

   /**
    * Returns an invalid result for the ResultCode function.
    *
    * @returns
    *    the invalid result as XML String.
    */
   private String getResultCodeResult() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
             "<result errorcode=\"NoVowel\">"+
             "</result>";
   }
}
