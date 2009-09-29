/*
 * $Id: HTTPCallConfigTests.java,v 1.7 2007/09/18 11:20:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.http;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import org.xins.common.http.HTTPCallConfig;
import org.xins.common.http.HTTPMethod;

/**
 * Tests for class <code>HTTPCallConfig</code>.
 *
 * @version $Revision: 1.7 $ $Date: 2007/09/18 11:20:49 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class HTTPCallConfigTests extends TestCase {

   /**
    * Constructs a new <code>HTTPCallConfigTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    *
    * @param name
    *    the name for this test suite.
    */
   public HTTPCallConfigTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(HTTPCallConfigTests.class);
   }

   public void testHTTPCallConfig() throws Exception {

      // Test first constructor
      HTTPCallConfig config = new HTTPCallConfig();
      assertEquals("Incorrect HTTP method.", HTTPMethod.POST, config.getMethod());
      config.setMethod(HTTPMethod.GET);
      assertEquals("Incorrect HTTP method.", HTTPMethod.GET, config.getMethod());
      config.setUserAgent("Anthony");
      assertEquals("Incorrect HTTP agent.", "Anthony", config.getUserAgent());
      config.describe();
   }
}
