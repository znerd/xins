/*
 * $Id: TextUtilsTests.java,v 1.8 2007/09/18 11:21:05 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.TextUtils;

/**
 * Tests for class <code>TextUtils</code>.
 *
 * @version $Revision: 1.8 $ $Date: 2007/09/18 11:21:05 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class TextUtilsTests extends TestCase {

   /**
    * Constructs a new <code>TextUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public TextUtilsTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(TextUtilsTests.class);
   }

   public void testReplace() throws Exception {
      String text = "hello ${world} ${name}";
      Properties replaceMap = new Properties();

      String replace1 = TextUtils.replace(text, replaceMap, "${", "}");
      assertEquals("Nothing should have been replaced", text, replace1);

      replaceMap.put("world", "world");
      String replace2 = TextUtils.replace(text, replaceMap, "${", "}");
      assertEquals("Incorrect text replaced.", "hello world ${name}", replace2);

      replaceMap.put("world", "");
      String replace3 = TextUtils.replace(text, replaceMap, "${", "}");
      assertEquals("Incorrect text replaced.", "hello  ${name}", replace3);

      replaceMap.put("world", "Mr");
      replaceMap.put("name", "Anthony");
      String replace4 = TextUtils.replace(text, replaceMap, "${", "}");
      assertEquals("Incorrect text replaced.", "hello Mr Anthony", replace4);
   }
}
