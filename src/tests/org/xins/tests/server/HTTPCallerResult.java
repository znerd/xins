/*
 * $Id: HTTPCallerResult.java,v 1.6 2007/09/18 11:20:50 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Result returned by executing a HTTP request using the HTTPCaller.
 *
 * @version $Revision: 1.6 $ $Date: 2007/09/18 11:20:50 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class HTTPCallerResult {

   private String _status;
   private String _body;
   private HashMap<String, List> _headers = new HashMap<String, List>();

   HTTPCallerResult() {
   }

   void setStatus(String status) {
      _status = status;
   }

   public String getStatus() {
      return _status;
   }

   void setBody(String body) {
      _body = body;
   }

   public String getBody() {
      return (_body == null) ? "" : _body;
   }

   void addHeader(String key, String value) {

      // Always convert the key to upper case
      key = key.toUpperCase();

      // Always trim the value
      value = value.trim();

      // Store the value in the list associated by key
      List<String> list = _headers.get(key);
      if (list == null) {
         list = new ArrayList<String>();
         _headers.put(key, list);
      }
      list.add(value);
   }

   public List getHeaderValues(String key) {
      Object value = _headers.get(key.toUpperCase());
      return (value == null) ? new ArrayList() : (List) value;
   }
}

