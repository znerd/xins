/*
 * $Id: ListTests.java,v 1.6 2007/09/18 11:20:30 agoubard Exp $
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
import org.xins.common.types.standard.List;

/**
 * Tests for class <code>List</code>.
 *
 * @version $Revision: 1.6 $ $Date: 2007/09/18 11:20:30 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class ListTests extends TestCase {

   /**
    * Constructs a new <code>ListTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ListTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ListTests.class);
   }

   public void testFromString() throws Throwable {

      // Test the SISO principle (Shit In Shit Out)
      assertNull(List.SINGLETON.fromString(null));

      // Test passing an empty string in
      List.Value emptyList = (List.Value) List.SINGLETON.fromString("");
      assertEquals(0, emptyList.getSize());

      // Test passing some values in
      String name1 = "a0@ %";
      String name2 = "Z&";
      String name3 = "-123^  \t\n";

      String string = URLEncoder.encode(name1)
              + '&' + URLEncoder.encode(name2)
              + '&' + URLEncoder.encode(name3);

      // Create a PropertyReader object from the string
      List.Value myList = (List.Value) List.SINGLETON.fromString(string);

      // Test values of the list
      assertEquals(3, myList.getSize());

      assertEquals(name1, myList.get(0));
      assertEquals(name2, myList.get(1));
      assertEquals(name3, myList.get(2));

      // Test with 2 similar items
      String string2 = string + '&' + URLEncoder.encode(name1);
      List.Value myList2 = (List.Value) List.SINGLETON.fromString(string2);
      assertEquals(4, myList2.getSize());
      assertEquals(name1, myList2.get(3));
   }
}
