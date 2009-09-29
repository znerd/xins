/*
 * $Id: TargetDescriptorTests.java,v 1.15 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.service;

import java.net.MalformedURLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;

/**
 * Tests for class <code>TargetDescriptor</code>.
 *
 * @version $Revision: 1.15 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class TargetDescriptorTests extends TestCase {

   /**
    * Constructs a new <code>TargetDescriptorTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public TargetDescriptorTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(TargetDescriptorTests.class);
   }

   public void testConstructor() throws Exception {

      // Pass null to constructor
      try {
         new TargetDescriptor(null);
         fail("TargetDescriptor(String) should throw an IllegalArgumentException if the argument is null.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }
   }

   public void testInvalidURLs() throws Exception {

      String[] invalidURLs = new String[] {
         "", " ", "\n", "http:", "http:8", "http:/8", "blablabla",
         "http%3A%2F%2Fwww.example.com",
         "/usr/local/something", "C:\\xins\\src",
         "file://C:\\xins\\src",
         "http://www.example.com /index.html",
         "http://admin%40admins.com:MyPaS%4@www.admins.com/suffix"
      };
      for (int i = 0; i < invalidURLs.length; i++) {
         String url = invalidURLs[i];
         try {
            new TargetDescriptor(url);
            fail("TargetDescriptor(String) should throw a MalformedURLException if the argument is \"" + url + "\".");
         } catch (MalformedURLException ex) {
            // as expected
         }
      }
   }

   public void testValidURLs() throws Exception {

      String[] validURLs = new String[] {
         "file://home/ernst/something.xml",
         "file://C/Documents%20and%20Settings/",
         "file://home/janwb/../ernst/something.xml",
         "ftp://someserver.co.au/",
         "ftp://someserver.co.au/pub/content/",
         "ftp://someserver.co.au/pub/content/a.ico",
         "http://abc123.com/something",
         "http://127.0.0.1/",
         "http://10.2.3.4",
         "https://1.2.3.4/",
         "jdbc:odbc://dataserv:80/mydomain",
         "http://www.example.com/some%20file",
         "http://www.example.com:8080/",
         "http://www.example.com/somedir/../index.html",
         "file:///index.html",
         "jdbc:odbc:DubyBrothers",
         "http://admin@www.admins.com/suffix/",
         "http://admin:MyPaSS@www.admins.com/suffix",
         "http://admin:MyPaSS1@www.admins.com/suffix",
         "http://admin%40admins.com:My.aSS1@www.admins.com/suffix",
         "http://admin%40admins.com:MyPaS%401@www.admins.com/suffix",
         "http://admin%40admins.com:MyPaS%401@www.admins.com:80/suffix",
         "http://www.test.com/suffix/?a=b",
         "http://www.test.com/suffix/?a=b#15",
         "http://www.test.com/suffix/?a=b&b=c#15",
         "http://www.test.com/suffix/?a=b&b=c#15",
         "smtp://mail.server.com"
      };
      for (int i = 0; i < validURLs.length; i++) {
         String url = validURLs[i];
         new TargetDescriptor(url);
      }
   }

   public void testProtocols() throws Exception {

      // Test protocols
      doTestProtocol("file",      "home/ernst/something.xml");
      doTestProtocol("FILE",      "C/Documents%20and%20Settings/");
      doTestProtocol("ftp",       "someserver.co.au/");
      doTestProtocol("jdbc:odbc", "dataserv:80/mydomain");

      TargetDescriptor td = new TargetDescriptor("http://xins.sf.net");
      assertTrue("Incorrect description.", td.toString().indexOf("xins.sf.net") != -1);
   }

   private void doTestProtocol(String protocol, String rest)
   throws Exception {
      String url = protocol + "://" + rest;
      TargetDescriptor td = new TargetDescriptor(url);
      assertEquals(protocol, td.getProtocol());
   }
}
