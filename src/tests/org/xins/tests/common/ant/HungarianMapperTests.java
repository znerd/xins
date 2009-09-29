/*
 * $Id: HungarianMapperTests.java,v 1.2 2007/09/18 11:20:58 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.ant;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.xins.common.ant.HostnameTask;
import org.xins.common.ant.HungarianMapper;

import org.xins.tests.AllTests;

/**
 * Tests for class <code>HostnameTask</code>.
 *
 * @version $Revision: 1.2 $ $Date: 2007/09/18 11:20:58 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class HungarianMapperTests extends TestCase {

   /**
    * Constructs a new <code>HostnameTaskTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public HungarianMapperTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(HungarianMapperTests.class);
   }

   public void testHungarianMapping() throws Exception {
      HungarianMapper mapper = new HungarianMapper();
      String[] mapping1 = mapper.mapFileName("helloWorld.txt");
      assertEquals(1, mapping1.length);
      assertEquals("HelloWorld.txt", mapping1[0]);

      String[] mapping2 = mapper.mapFileName("test" + File.separator + "helloWorld2.txt");
      assertEquals(1, mapping2.length);
      assertEquals("test" + File.separator + "HelloWorld2.txt", mapping2[0]);

      String[] mapping3 = mapper.mapFileName("test" + File.separator + "HelloWorld3.txt");
      assertEquals(1, mapping3.length);
      assertEquals("test" + File.separator + "HelloWorld3.txt", mapping3[0]);
   }
}
