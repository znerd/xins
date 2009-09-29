/*
 * $Id: SetTests.java,v 1.6 2007/09/18 11:20:25 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.types.standard;

import java.net.URLEncoder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Set;

/**
 * Tests for class <code>Set</code>.
 *
 * @version $Revision: 1.6 $ $Date: 2007/09/18 11:20:25 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class SetTests extends TestCase {

   /**
    * Constructs a new <code>SetTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public SetTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(SetTests.class);
   }

   public void testFromString() throws Throwable {

      // Test the SISO principle (Shit In Shit Out)
      assertNull(Set.SINGLETON.fromString(null));

      // Test passing an empty string in
      Set.Value emptyList = (Set.Value) Set.SINGLETON.fromString("");
      assertEquals(0, emptyList.getSize());

      // Test passing some values in
      String name1 = "a0@ %";
      String name2 = "Z&";
      String name3 = "-123^  \t\n";

      String string = URLEncoder.encode(name1)
              + '&' + URLEncoder.encode(name2)
              + '&' + URLEncoder.encode(name3);

      // Create a PropertyReader object from the string
      Set.Value mySet = (Set.Value) Set.SINGLETON.fromString(string);

      // Test values of the list
      assertEquals(3, mySet.getSize());

      assertEquals(name1, mySet.get(0));
      assertEquals(name2, mySet.get(1));
      assertEquals(name3, mySet.get(2));

      // Test with 2 similar items
      String string2 = string + '&' + URLEncoder.encode(name1);
      Set.Value mySet2 = (Set.Value) Set.SINGLETON.fromString(string2);
      assertEquals(3, mySet2.getSize());
   }
}
