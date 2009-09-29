/*
 * $Id: UppercaseTaskTests.java,v 1.2 2007/09/18 11:20:57 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.ant;

import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.xins.common.ant.HostnameTask;
import org.xins.common.ant.UppercaseTask;

import org.xins.tests.AllTests;

/**
 * Tests for class <code>HostnameTask</code>.
 *
 * @version $Revision: 1.2 $ $Date: 2007/09/18 11:20:57 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class UppercaseTaskTests extends TestCase {

   /**
    * Constructs a new <code>HostnameTaskTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public UppercaseTaskTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(UppercaseTaskTests.class);
   }

   public void testHostname() throws Exception {
      UppercaseTask uppercaseTask = createUppercaseTask();
      uppercaseTask.setProperty("myProp");
      uppercaseTask.setText("helloWorld.2_9");
      uppercaseTask.execute();
      String upper = uppercaseTask.getProject().getProperty("myProp");
      assertNotNull(upper);
      assertTrue(upper.length() > 0);
      assertEquals("HELLOWORLD_2_9", upper);
   }

   /**
    * Creates the Ant CallXINSTask without any parameters set.
    *
    * @return
    *    The CallXINSTask, never <code>null</code>.
    */
   private UppercaseTask createUppercaseTask() {
      Project project = new Project();
      project.init();
      UppercaseTask uppercaseTask = new UppercaseTask();
      uppercaseTask.setProject(project);
      return uppercaseTask;
   }
}
