/*
 * $Id: HostnameTaskTests.java,v 1.6 2007/09/18 11:20:57 agoubard Exp $
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

import org.xins.tests.AllTests;

/**
 * Tests for class <code>HostnameTask</code>.
 *
 * @version $Revision: 1.6 $ $Date: 2007/09/18 11:20:57 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class HostnameTaskTests extends TestCase {

   /**
    * Constructs a new <code>HostnameTaskTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public HostnameTaskTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(HostnameTaskTests.class);
   }

   public void testHostname() throws Exception {
      HostnameTask hostTask = createHostnameTask();
      hostTask.setProperty("myHostname");
      hostTask.execute();
      String hostname = hostTask.getProject().getProperty("myHostname");
      assertNotNull(hostname);
      assertTrue(hostname.length() > 0);
   }

   /**
    * Creates the Ant CallXINSTask without any parameters set.
    *
    * @return
    *    The CallXINSTask, never <code>null</code>.
    */
   private HostnameTask createHostnameTask() {
      Project project = new Project();
      project.init();
      HostnameTask hostTask = new HostnameTask();
      hostTask.setProject(project);
      return hostTask;
   }
}
