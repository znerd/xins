/*
 * $Id: UniquePropertiesTests.java,v 1.3 2007/09/18 11:20:57 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.io.ByteArrayInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.UniqueProperties;

/**
 * Tests for the <code>UniqueProperties</code> class.
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class UniquePropertiesTests extends TestCase {

   /**
    * Constructs a new <code>UniquePropertiesTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public UniquePropertiesTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(UniquePropertiesTests.class);
   }

   public void testUniqueProperties() {
      UniqueProperties props = new UniqueProperties();
      props.put("hello", "world");
      props.put("hi", "world");
      assertTrue(props.isUnique());
      assertEquals("world", props.get("hello"));

      props.put("hello", "world2");
      assertFalse(props.isUnique());
      assertEquals("world2", props.get("hello"));
   }

   public void testUniquePropertiesStream() throws Exception {
      UniqueProperties props = new UniqueProperties();
      String data = "hello=world\nhi=world\nhello=world2";
      ByteArrayInputStream reader = new ByteArrayInputStream(data.getBytes());
      props.load(reader);
      assertFalse(props.isUnique());
      assertEquals("world2", props.get("hello"));
   }
}
