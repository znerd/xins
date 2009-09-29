/*
 * $Id: InitializationExceptionTests.java,v 1.9 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.manageable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.manageable.InitializationException;

/**
 * Tests for class <code>InitializationException</code>.
 *
 * @version $Revision: 1.9 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class InitializationExceptionTests extends TestCase {

   /**
    * Constructs a new <code>InitializationExceptionTests</code>
    * test suite with the specified name. The name will be passed to the
    * superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InitializationExceptionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(InitializationExceptionTests.class);
   }

   /**
    * Tests the <code>InitializationException</code> class.
    */
   public void testInitializationException() throws Exception {
      new InitializationException(null, null);
   }
}
