/*
 * $Id: ManageableTests.java,v 1.5 2007/03/16 10:30:36 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.manageable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.*;
import org.xins.common.manageable.*;

/**
 * Tests for class <code>Manageable</code>.
 *
 * @version $Revision: 1.5 $ $Date: 2007/03/16 10:30:36 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ManageableTests extends TestCase {

   /**
    * Constructs a new <code>ManageableTests</code>
    * test suite with the specified name. The name will be passed to the
    * superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ManageableTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ManageableTests.class);
   }

   /**
    * Tests the <code>Manageable</code> class.
    */
   public void testManageable() throws Exception {
      TestManageable m = new TestManageable();

      // Test initial state
      assertEquals("Initial Manageable state must be UNUSABLE.", m.UNUSABLE, m.getState());
      assertEquals("Initially, afterBootstrap is never called.", 0, m._afterBootstrapCalls);
      assertEquals("Initially, afterInit is never called.",      0, m._afterInitCalls     );
      assertEquals("Initially, afterDeinit is never called.",    0, m._afterDeinitCalls   );

      // Test state after bootstrapping
      m.bootstrap(null);
      assertEquals("After bootstrapping Manageable state must be BOOTSTRAPPED.", m.BOOTSTRAPPED, m.getState());
      assertEquals("During bootstrapping, afterBootstrap should have been called.",   1, m._afterBootstrapCalls);
      assertEquals("During bootstrapping, afterInit should not have been called.",    0, m._afterInitCalls     );
      assertEquals("During bootstrapping, afterDeinit should not have been called.",  0, m._afterDeinitCalls   );

      // Test state after initialization
      m.init(null);
      assertEquals("After initialization Manageable state must be USABLE.", m.USABLE, m.getState());
      assertEquals("During initialization, afterBootstrap should not have been called.", 1, m._afterBootstrapCalls);
      assertEquals("During initialization, afterInit should have been called.",          1, m._afterInitCalls     );
      assertEquals("During initialization, afterDeinit should not have been called.",    0, m._afterDeinitCalls   );

      // Test state after deinitialization
      m.deinit();
      assertEquals("After deinitialization Manageable state must be UNUSABLE.", m.UNUSABLE, m.getState());
      assertEquals("During deinitialization, afterBootstrap should not have been called.", 1, m._afterBootstrapCalls);
      assertEquals("During deinitialization, afterInit should not have been called.",      1, m._afterInitCalls     );
      assertEquals("During deinitialization, afterDeinit should have been called.",        1, m._afterDeinitCalls   );

      // Test state after 2nd-time bootstrapping
      m.bootstrap(null);
      assertEquals("After bootstrapping Manageable state must be BOOTSTRAPPED.", m.BOOTSTRAPPED, m.getState());
      assertEquals("During bootstrapping, afterBootstrap should have been called.",   2, m._afterBootstrapCalls);
      assertEquals("During bootstrapping, afterInit should not have been called.",    1, m._afterInitCalls     );
      assertEquals("During bootstrapping, afterDeinit should not have been called.",  1, m._afterDeinitCalls   );

      // Test state after 2nd-time initialization
      m.init(null);
      assertEquals("After initialization Manageable state must be USABLE.", m.USABLE, m.getState());
      assertEquals("During initialization, afterBootstrap should not have been called.", 2, m._afterBootstrapCalls);
      assertEquals("During initialization, afterInit should have been called.",          2, m._afterInitCalls     );
      assertEquals("During initialization, afterDeinit should not have been called.",    1, m._afterDeinitCalls   );

      // Test state after 2nd-time deinitialization
      m.deinit();
      assertEquals("After deinitialization Manageable state must be UNUSABLE.", m.UNUSABLE, m.getState());
      assertEquals("During deinitialization, afterBootstrap should not have been called.", 2, m._afterBootstrapCalls);
      assertEquals("During deinitialization, afterInit should not have been called.",      2, m._afterInitCalls     );
      assertEquals("During deinitialization, afterDeinit should have been called.",        2, m._afterDeinitCalls   );

      // Test that bootstrap cannot be called twice in a row
      m.bootstrap(null);
      assertEquals("During bootstrapping, afterBootstrap should have been called.",   3, m._afterBootstrapCalls);
      assertEquals("During bootstrapping, afterInit should not have been called.",    2, m._afterInitCalls     );
      assertEquals("During bootstrapping, afterDeinit should not have been called.",  2, m._afterDeinitCalls   );
      try {
         m.bootstrap(null);
         fail("Expected Manageable.bootstrap to throw an IllegalStateException when called while in state BOOTSTRAPPED.");
      } catch (IllegalStateException exception) {
         // as expected
      }
      assertEquals("Expected Manageable state to be BOOTSTRAPPED.", m.BOOTSTRAPPED, m.getState());
      assertEquals("During failed bootstrapping, afterBootstrap should not have been called.",   3, m._afterBootstrapCalls);
      assertEquals("During failed bootstrapping, afterInit should not have been called.",        2, m._afterInitCalls     );
      assertEquals("During failed bootstrapping, afterDeinit should not have been called.",      2, m._afterDeinitCalls   );

      // Test state after init was called before bootstrap
      m.deinit();
      assertEquals("During deinitialization, afterBootstrap should not have been called.", 3, m._afterBootstrapCalls);
      assertEquals("During deinitialization, afterInit should not have been called.",      2, m._afterInitCalls     );
      assertEquals("During deinitialization, afterDeinit should have been called.",        3, m._afterDeinitCalls   );
      try {
         m.init(null);
         fail("Expected Manageable.init to throw IllegalStateException if state is UNUSABLE.");
      } catch (IllegalStateException exception) {
         // as expected
      }
      assertEquals("After Manageable.init failed, state must remain UNUSABLE.", m.UNUSABLE, m.getState());
      assertEquals("During failed initialization, afterBootstrap should not have been called.", 3, m._afterBootstrapCalls);
      assertEquals("During failed initialization, afterInit should not have been called.",      2, m._afterInitCalls     );
      assertEquals("During failed initialization, afterDeinit should not have been called.",    3, m._afterDeinitCalls   );

      // Test state after init failed
      m.bootstrap(null);
      assertEquals("During bootstrapping, afterBootstrap should have been called.",   4, m._afterBootstrapCalls);
      assertEquals("During bootstrapping, afterInit should not have been called.",    2, m._afterInitCalls     );
      assertEquals("During bootstrapping, afterDeinit should not have been called.",  3, m._afterDeinitCalls   );
      m._failInit = true;
      try {
         m.init(null);
         fail("Expected Manageable.init to throw InitializationException if initImpl throws an Error.");
      } catch (InitializationException exception) {
         // as expected
      }
      assertEquals("After Manageable.init failed, state must remain BOOTSTRAPPED.", m.BOOTSTRAPPED, m.getState());
      assertEquals("During failed initialization, afterBootstrap should not have been called.", 4, m._afterBootstrapCalls);
      assertEquals("During failed initialization, afterInit should not have been called.",      2, m._afterInitCalls     );
      assertEquals("During failed initialization, afterDeinit should not have been called.",    3, m._afterDeinitCalls   );

      // Test state after bootstrap failed
      m.deinit();
      assertEquals("During deinitialization, afterBootstrap should not have been called.", 4, m._afterBootstrapCalls);
      assertEquals("During deinitialization, afterInit should not have been called.",      2, m._afterInitCalls     );
      assertEquals("During deinitialization, afterDeinit should have been called.",        4, m._afterDeinitCalls   );
      m._failBootstrap = true;
      try {
         m.bootstrap(null);
         fail("Expected Manageable.bootstrap to throw BootstrapException if bootstrapImpl throws an Error.");
      } catch (BootstrapException exception) {
         // as expected
      }
      assertEquals("After Manageable.bootstrap failed, state must remain UNUSABLE.", m.UNUSABLE, m.getState());
      assertEquals("During failed bootstrapping, afterBootstrap should not have been called.",   4, m._afterBootstrapCalls);
      assertEquals("During failed bootstrapping, afterInit should not have been called.",        2, m._afterInitCalls     );
      assertEquals("During failed bootstrapping, afterDeinit should not have been called.",      4, m._afterDeinitCalls   );
   }

   private static final class TestManageable extends Manageable {

      private boolean _failBootstrap;
      private boolean _failInit;
      private int _afterBootstrapCalls;
      private int _afterInitCalls;
      private int _afterDeinitCalls;

      private void expectState(State expectedState) {
         State actualState = getState();
         if (expectedState != actualState) {
            throw new Error("State is " + actualState + " instead of " + expectedState + '.');
         }
      }

      protected void bootstrapImpl(PropertyReader properties) {
         expectState(BOOTSTRAPPING);
         if (_failBootstrap) {
            _failBootstrap = false;
            throw new Error();
         }
      }

      protected void afterBootstrap(PropertyReader properties) {
         expectState(BOOTSTRAPPED);
         _afterBootstrapCalls++;
      }

      protected void initImpl(PropertyReader properties) {
         expectState(INITIALIZING);
         if (_failInit) {
            _failInit = false;
            throw new Error();
         }
      }

      protected void afterInit(PropertyReader properties) {
         expectState(USABLE);
         if (getState() != USABLE) {
            throw new Error("State is " + getState() + " instead of USABLE.");
         }
         _afterInitCalls++;
      }

      protected void afterDeinit() {
         expectState(UNUSABLE);
         _afterDeinitCalls++;
      }
   }
}
