/*
 * $Id: XINSServletRequestTests.java,v 1.7 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.servlet.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReaderConverter;
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.servlet.container.XINSServletRequest;
import org.xins.common.text.ParseException;

import org.xins.tests.AllTests;

/**
 * Tests for class <code>ServletRequestPropertyReader</code>.
 *
 * @version $Revision: 1.7 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class XINSServletRequestTests extends TestCase {

   /**
    * Constructs a new <code>XINSServletRequestTests</code> test
    * suite with the specified name. The name will be passed to the
    * superconstructor.
    *
    *
    * @param name
    *    the name for this test suite.
    */
   public XINSServletRequestTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(XINSServletRequestTests.class);
   }

   public void testEmptyValues() {
      XINSServletRequest request = new XINSServletRequest(AllTests.url() + "?test1=bla&test2=&test3=");

      Enumeration enuParams = request.getParameterNames();
      boolean test1Done = false;
      boolean test2Done = false;
      boolean test3Done = false;
      while (enuParams.hasMoreElements()) {
         String nextParam = (String) enuParams.nextElement();
         if (nextParam.equals("test1") && !test1Done) {
            test1Done = true;
         } else if (nextParam.equals("test2") && !test2Done) {
            test2Done = true;
         } else if (nextParam.equals("test3") && !test3Done) {
            test3Done = true;
         } else {
            fail("Incorrect parameter or parameter already read: " + nextParam);
         }
      }
      if (!(test1Done && test2Done && test3Done)) {
         fail("At least one of the parameter was not read correctly.");
      }
   }

   public void testMultipleValues() {
      XINSServletRequest request = new XINSServletRequest(AllTests.url() + "?test1=bla&test1=&test1=bla2");

      String[] test1Values = request.getParameterValues("test1");
      List test1ValuesList = Arrays.asList(test1Values);
      assertEquals("Not all values found.", test1Values.length, 3);
      assertTrue("Specific value not found.", test1ValuesList.contains("bla"));
      assertTrue("Specific value not found.", test1ValuesList.contains(""));
      assertTrue("Specific value not found.", test1ValuesList.contains("bla2"));
      String[] test2Values = request.getParameterValues("test2");
      assertNull("Values found for an unset parameter.", test2Values);
   }
}
