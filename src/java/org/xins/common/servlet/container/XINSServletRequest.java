/*
 * $Id: XINSServletRequest.java,v 1.41 2007/09/18 08:45:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.FormatException;
import org.xins.common.text.TextUtils;
import org.xins.common.text.URLEncoding;

/**
 * This class is an implementation of the HTTPServletRequest that can be
 * called localy.
 *
 * @version $Revision: 1.41 $ $Date: 2007/09/18 08:45:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class XINSServletRequest implements HttpServletRequest {

   /**
    * The localhost name.
    */
   private static String LOCALHOST_NAME;

   /**
    * The localhost address.
    */
   private static String LOCALHOST_ADDRESS;

   /**
    * The HTTP sessions of the servlet container.
    */
   private static Map<String,HttpSession> SESSIONS = new HashMap<String,HttpSession>();

   /**
    * The HTTP request method.
    */
   private final String _method;

   /**
    * The requested URL including the optional parameters.
    */
   private final String _url;

   /**
    * The parameters retrieved from the URL. Existing keys are either a
    * <code>String</code> or an <code>ArrayList</code> of strings.
    */
   private HashMap<String,Object> _parameters = new HashMap<String,Object>();

   /**
    * The date when the request was created.
    */
   private long _date = System.currentTimeMillis();

   /**
    * The attributes of the request.
    */
   private Hashtable<String,Object> _attributes = new Hashtable<String,Object>();

   /**
    * The HTTP headers of the request.
    */
   private Hashtable<String,String> _headers = new Hashtable<String,String>();

   /**
    * The URL path.
    */
   private String _pathInfo;

   /**
    * The URL query string.
    */
   private String _queryString;

   /**
    * The content type of the query.
    */
   private String _contentType;

   /**
    * The content of the HTTP POST.
    */
   private String _postData;

   /**
    * The cookies of the request.
    */
   private Cookie[] _cookies;

   /**
    * Flags indicating that the input stream has been used.
    */
   private boolean _inputStreamUsed = false;

   /**
    * Flags indicating that the reader has been used.
    */
   private boolean _readerUsed = false;

   /**
    * Flags indicating that the reader has been used.
    */
   private String _characterEncoding;

   static {
      try {
         LOCALHOST_ADDRESS = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException exception) {
         LOCALHOST_ADDRESS = "127.0.0.1";
      }
      try {
         LOCALHOST_NAME = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException exception) {
         LOCALHOST_NAME = "localhost";
      }
   }

   /**
    * Creates a new Servlet request.
    *
    * @param url
    *    the request URL or the list of the parameters (name=value) separated
    *    with comma's. Cannot be <code>null</code>.
    */
   public XINSServletRequest(String url) {
      this("GET", url, null, null);
   }

   /**
    * Creates a new servlet request with the specified method.
    *
    * @param method
    *    the request method, cannot be <code>null</code>.
    *
    * @param url
    *    the request URL or the list of the parameters (name=value) separated
    *    with ampersands, cannot be <code>null</code>.
    *
    * @param data
    *    the content of the request, can be <code>null</code>.
    *
    * @param headers
    *    the HTTP headers of the request, the keys must all be in upper case;
    *    can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null || url == null</code>
    *
    * @since XINS 1.5.0
    */
   public XINSServletRequest(String method, String url, String data, Map<String,String> headers)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("method", method, "url", url);

      // Initialize the method, URL and post data (if any)
      _method   = method;
      _url      = url;
      _postData = data;

      // Initialize the headers
      if (headers != null) {
         _headers.putAll(headers);
         _contentType = (String) headers.get("CONTENT-TYPE");
      }

      // Fallback to a default content-type
      if (TextUtils.isEmpty(_contentType)) {
         _contentType = "application/x-www-form-urlencoded";
      }

      // Parse the URL
      parseURL(url);
   }

   /**
    * Parses the url to extract the parameters.
    *
    * @param url
    *    the request URL or the list of the parameters (name=value) separated
    *    with ampersands.
    */
   private void parseURL(String url) {

      // Parse the URL
      int doubleSlashPos = url.lastIndexOf("://");
      int startPathPos = url.indexOf("/", doubleSlashPos + 1) + 1;

      int questionMarkPos = url.lastIndexOf('?');
      if (questionMarkPos == url.length() - 1) {
         _queryString = "";
         if (startPathPos < questionMarkPos) {
            _pathInfo = url.substring(startPathPos, questionMarkPos);
         }
      } else if (questionMarkPos != -1) {
         _queryString = url.substring(questionMarkPos + 1);
         if (startPathPos < questionMarkPos) {
             _pathInfo = url.substring(startPathPos, questionMarkPos);
         }
      } else {
         _queryString = null;
         if (startPathPos < url.length()) {
            _pathInfo = url.substring(startPathPos);
         }
         return;
      }

      StringTokenizer paramsParser = new StringTokenizer(_queryString, "&");
      while (paramsParser.hasMoreTokens()) {
         String parameter = paramsParser.nextToken();
         int equalPos = parameter.indexOf('=');
         if (equalPos != -1) {
            try {
               String paramName = URLEncoding.decode(parameter.substring(0, equalPos));
               String paramValue = "";
               if (equalPos != parameter.length()-1) {
                  paramValue = URLEncoding.decode(parameter.substring(equalPos + 1));
               }
               Object currValue = _parameters.get(paramName);
               if (currValue == null) {
                  _parameters.put(paramName, paramValue);
               } else if (currValue instanceof String) {
                  ArrayList<String> values = new ArrayList<String>();
                  values.add((String) currValue);
                  values.add(paramValue);
                  _parameters.put(paramName, values);
               } else {
                  ArrayList<String> values = (ArrayList<String>) currValue;
                  values.add(paramValue);
               }

            // Ignore parameter in case of a malformatted string
            } catch (FormatException e) {
               Utils.logIgnoredException(XINSServletRequest.class.getName(), "parseURL(String)",
                                         XINSServletRequest.class.getName(), "parseURL(String)",
                                         e);

            // Ignore parameter in case of an illegal argument
            } catch (IllegalArgumentException e) {
               Utils.logIgnoredException(XINSServletRequest.class.getName(), "parseURL(String)",
                                         XINSServletRequest.class.getName(), "parseURL(String)",
                                         e);
            }
         }
      }
   }

   public void setCharacterEncoding(String str) {
      _characterEncoding = str;
   }

   public String[] getParameterValues(String str) {
      Object values = _parameters.get(str);
      if (values == null) {
         return null;
      } else if (values instanceof String) {
         return new String[] { (String) values };
      } else {
         ArrayList<String> list = (ArrayList<String>) values;
         return (String[]) list.toArray(new String[list.size()]);
      }
   }

   public String getParameter(String str) {
      String[] values = getParameterValues(str);
      return (values == null) ? null : values[0];
   }

   public int getIntHeader(String str) {
      String value = getHeader(str);
      if (value != null) {
         try {
            return Integer.parseInt(value);
         } catch (NumberFormatException exception) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public Object getAttribute(String str) {
      return _attributes.get(str);
   }

   public long getDateHeader(String str) {
      return _date;
   }

   public String getHeader(String str) {
      return (String) _headers.get(str.toUpperCase());
   }

   public Enumeration getHeaders(String str) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   public String getRealPath(String str) {
      throw new UnsupportedOperationException();
   }

   public RequestDispatcher getRequestDispatcher(String str) {
      throw new UnsupportedOperationException();
   }

   public boolean isUserInRole(String str) {
      return false;
   }

   public void removeAttribute(String str) {
      _attributes.remove(str);
   }

   public void setAttribute(String str, Object obj) {
      _attributes.put(str, obj);
   }

   public String getQueryString() {
      return _queryString;
   }

   public String getProtocol() {
      return "file://";
   }

   public String getPathTranslated() {
      // We consider that the first dir is the servlet name
      if (_pathInfo == null) {
         return null;
      }
      int firstSlashPos = _pathInfo.indexOf("/");
      if (firstSlashPos == -1 || firstSlashPos == _pathInfo.length() - 1) {
         return null;
      }
      return _pathInfo.substring(firstSlashPos + 1);
   }

   public String getPathInfo() {
      return _pathInfo;
   }

   public Enumeration<String> getParameterNames() {
      return Collections.enumeration(_parameters.keySet());
   }

   // TODO: Document that the values in the map are either strings or lists of
   //       strings
   public Map<String,Object> getParameterMap() {
      return _parameters;
   }

   public String getMethod() {
      return _method;
   }

   public Enumeration getLocales() {
      throw new UnsupportedOperationException();
   }

   public Locale getLocale() {
      throw new UnsupportedOperationException();
   }

   public Enumeration<String> getAttributeNames() {
      return _attributes.keys();
   }

   public String getAuthType() {
      return "";
   }

   public String getCharacterEncoding() {
      if (_characterEncoding != null) {
         return _characterEncoding;
      } else if (_contentType == null) {
         return null;
      } else {
         int charsetPos = _contentType.indexOf("charset=");
         if (charsetPos == -1) {
            return "UTF-8";
         } else {
            return _contentType.substring(charsetPos + 8);
         }
      }
   }

   public int getContentLength() {
      return getIntHeader("Content-Length");
   }

   public String getContentType() {
      return _contentType;
   }

   public String getContextPath() {
      throw new UnsupportedOperationException();
   }

   public Cookie[] getCookies() {
      if (_cookies == null && _headers.get("COOKIE") != null) {
         String cookies = (String) _headers.get("COOKIE");
         StringTokenizer stCookies = new StringTokenizer(cookies, ";");
         _cookies = new Cookie[stCookies.countTokens()];
         int counter = 0;
         while (stCookies.hasMoreTokens()) {
            String nextCookie = stCookies.nextToken().trim();
            int equalsPos = nextCookie.indexOf('=');
            String cookieName = nextCookie.substring(0, equalsPos);
            String cookieValue = nextCookie.substring(equalsPos + 1);
            _cookies[counter++] = new Cookie(cookieName, cookieValue);
         }
      } else if (_cookies == null) {
         _cookies = new Cookie[0];
      }
      return _cookies;
   }

   public Enumeration<String> getHeaderNames() {
      return _headers.keys();
   }

   public ServletInputStream getInputStream() {
      if (_readerUsed) {
         throw new IllegalStateException("The method getReader() has already been called on this request.");
      }
      _inputStreamUsed = true;
      return new InputStream(_postData);
   }

   public BufferedReader getReader() {
      if (_inputStreamUsed) {
         throw new IllegalStateException("The method getInputStream() has already been called on this request.");
      }
      _readerUsed = true;
      return new BufferedReader(new StringReader(_postData));
   }

   public String getRemoteAddr() {
      return LOCALHOST_ADDRESS;
   }

   public String getRemoteHost() {
      return LOCALHOST_NAME;
   }

   public String getRemoteUser() {
      return "";
   }

   public String getRequestURI() {
      if (_url.indexOf('?') == -1) {
         return _url;
      } else {
         return _url.substring(0, _url.indexOf('?'));
      }
   }

   public StringBuffer getRequestURL() {
      return new StringBuffer(_url);
   }

   public String getRequestedSessionId() {
      throw new UnsupportedOperationException();
   }

   public String getScheme() {
      int separator = _url.indexOf("://");
      if (separator != -1) {
         return _url.substring(0, separator + 3);
      }
      return "file://";
   }

   public String getServerName() {
      try {
         return InetAddress.getLocalHost().getHostName();
      } catch (Exception ioe) {
         return "localhost";
      }
   }

   public int getServerPort() {
      return 8080;
   }

   public String getServletPath() {
      return "";
   }

   public HttpSession getSession() {
      return SESSIONS.get(getRemoteAddr() + getRemoteUser());
   }

   public HttpSession getSession(boolean create) {
      String sessionKey = getRemoteAddr() + getRemoteUser();
      HttpSession session = SESSIONS.get(sessionKey);
      if (session == null) {
         session = new XINSHttpSession();
         SESSIONS.put(sessionKey, session);
      }
      return session;
   }

   public Principal getUserPrincipal() {
      throw new UnsupportedOperationException();
   }

   public boolean isRequestedSessionIdFromCookie() {
      return false;
   }

   public boolean isRequestedSessionIdFromURL() {
      return false;
   }

   @Deprecated
   public boolean isRequestedSessionIdFromUrl() {
      return false;
   }

   public boolean isRequestedSessionIdValid() {
      return false;
   }

   public boolean isSecure() {
      return false;
   }

   /**
    * Implementation of a <code>ServletInputStream</code> for this request.
    *
    * <p>This implementation is <strong>not thread-safe</strong>.
    *
    * @version $Revision: 1.41 $ $Date: 2007/09/18 08:45:08 $
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    */
   private static class InputStream extends ServletInputStream {
      /**
       * Constructs a new <code>InputStream</code> instance for the specified
       * data.
       *
       * @param data
       *    the data, as a string, can be <code>null</code>.
       */
      private InputStream(String data) {
         String encoding = "ISO-8859-1";
         try {
            byte[] dataAsByte = data.getBytes(encoding);
            _stream = new ByteArrayInputStream(dataAsByte);
         } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException("Failed to convert characters to bytes using encoding \"" + encoding + "\".");
         }
      }

      /**
       * The data. Is <code>null</code> if there is no data.
       */
      private final ByteArrayInputStream _stream;

      public int read() throws IOException {
         return _stream.read();
      }

      public int read(byte[] b) throws IOException {
         return _stream.read(b);
      }

      public int read(byte[] b, int off, int len) throws IOException {
         return _stream.read(b, off, len);
      }

      public boolean markSupported() {
         return _stream.markSupported();
      }

      public void mark(int readlimit) {
         _stream.mark(readlimit);
      }

      public long skip(long n) throws IOException {
         return _stream.skip(n);
      }

      public void reset() throws IOException {
         _stream.reset();
      }

      public void close() throws IOException {
         _stream.close();
      }
   }
}
