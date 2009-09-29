/*
 * $Id: PropertiesTests.java,v 1.17 2007/09/24 12:18:49 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.types.standard;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Properties;
import org.xins.common.collections.PropertyReader;

/**
 * Tests for class <code>Properties</code>.
 *
 * @version $Revision: 1.17 $ $Date: 2007/09/24 12:18:49 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class PropertiesTests extends TestCase {

   /**
    * Constructs a new <code>PropertiesTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public PropertiesTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(PropertiesTests.class);
   }

   public void testFromString() throws Throwable {

      final Properties TYPE = Properties.SINGLETON;

      // Test the SISO principle (Shit In Shit Out)
      assertNull(TYPE.fromString(null));

      // Test passing an empty string in
      PropertyReader pr = (PropertyReader) TYPE.fromString("");
      assertTrue(! pr.getNames().hasNext());

      // Test passing some values in
      String name1 = "a0@ %",          value1 = name1;
      String name2 = "Z&"                            ;
      String name3 = "-123^  \t\n",    value3 = " "  ;

      String string = URLEncoder.encode(name1) + '=' + URLEncoder.encode(value1)
              + '&' + URLEncoder.encode(name2) + '='
              + '&' + URLEncoder.encode(name3) + '=' + URLEncoder.encode(value3);

      // Create a PropertyReader object from the string
      pr = (PropertyReader) TYPE.fromString(string);

      // Loop over all combinations
      Iterator iterator = pr.getNames();
      int count = 0;
      Map m = new HashMap();
      while (iterator.hasNext()) {
         String name = (String) iterator.next();
         m.put(name, pr.get(name));
         count++;
      }
      assertEquals(2, count);

      assertEquals(m.get(name1), value1);
      assertEquals(m.get(name2), null);
      assertEquals(m.get(name3), value3);

      // The conversion should fail, since the last token does not contain an
      // equals sign
      String string2 = string + "&&cd";
      try {
         TYPE.fromString(string2);
         fail("Expected Properties.SINGLETON.fromString(\"" + string2 + "\") to throw a TypeValueException.");
      } catch (TypeValueException exception) { /* as expected */ }
   }
}
