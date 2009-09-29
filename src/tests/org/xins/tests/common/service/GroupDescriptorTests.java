/*
 * $Id: GroupDescriptorTests.java,v 1.10 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.service;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.Descriptor;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.TargetDescriptor;

/**
 * Tests for class <code>GroupDescriptor</code>.
 *
 * @version $Revision: 1.10 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class GroupDescriptorTests extends TestCase {

   /**
    * Constructs a new <code>GroupDescriptorTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public GroupDescriptorTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(GroupDescriptorTests.class);
   }

   public void testGroupDescriptor() throws Exception {

      final GroupDescriptor.Type TYP_NULL = null;
      final GroupDescriptor.Type TYP_RAND = GroupDescriptor.RANDOM_TYPE;
      final GroupDescriptor.Type TYP_ORDR = GroupDescriptor.ORDERED_TYPE;

      final Descriptor[] descNull = null;
      final Descriptor[] desc0 = new Descriptor[0];
      final Descriptor[] desc1 = new Descriptor[] {
         new TargetDescriptor("http://www.a.com/")
      };
      final Descriptor[] desc2 = new Descriptor[] {
         new TargetDescriptor("http://www.a.com/"),
         new TargetDescriptor("http://www.b.com/")
      };

      // Pass null to constructor
      try {
         new GroupDescriptor(TYP_NULL, descNull);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }
      try {
         new GroupDescriptor(TYP_RAND, descNull);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }
      try {
         new GroupDescriptor(TYP_ORDR, descNull);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }
      try {
         new GroupDescriptor(TYP_NULL, desc2);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }

      // Pass empty array
      try {
         new GroupDescriptor(TYP_RAND, desc0);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }
      try {
         new GroupDescriptor(TYP_ORDR, desc0);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }

      // Pass array with one element
      try {
         new GroupDescriptor(TYP_RAND, desc1);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }
      try {
         new GroupDescriptor(TYP_ORDR, desc1);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }

      GroupDescriptor gd;

      gd = new GroupDescriptor(TYP_RAND,  desc2);
      assertEquals(2,        gd.getTargetCount());
      assertEquals(true,     gd.isGroup());
      assertEquals(TYP_RAND, gd.getType());

      gd = new GroupDescriptor(TYP_ORDR, desc2);
      assertEquals(2,        gd.getTargetCount());
      assertEquals(true,     gd.isGroup());
      assertEquals(TYP_ORDR, gd.getType());
   }
}
