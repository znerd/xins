/*
 * $Id: AsynchronousCallTests.java,v 1.11 2007/09/18 11:21:09 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client.async;

import com.mycompany.allinone.capi.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.InvalidRequestException;
import org.xins.client.UnacceptableRequestException;
import org.xins.client.async.AsynchronousCall;
import org.xins.client.async.CallCAPIThread;
import org.xins.client.async.CallFailedEvent;
import org.xins.client.async.CallListener;
import org.xins.client.async.CallSucceededEvent;
import org.xins.common.service.TargetDescriptor;

import org.xins.tests.AllTests;

/**
 * Tests the <code>AsynchronousCall</code>.
 *
 * @version $Revision: 1.11 $ $Date: 2007/09/18 11:21:09 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 */
public class AsynchronousCallTests extends TestCase {

   private Boolean successfulTestOkay = null;
   private Throwable assertionSuccessfulTestException = null;

   private Boolean unsuccessfulTestOkay = null;
   private Throwable assertionUnsuccessfulTestException = null;

   /**
    * Constructs a new <code>AsynchronousCallTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    *
    * @param name
    *    the name for this test suite.
    */
   public AsynchronousCallTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(AsynchronousCallTests.class);
   }

   public void testSuccessfulAsynchronousCall() throws Throwable {

      TargetDescriptor target = new TargetDescriptor(AllTests.url(), 5000, 1000, 4000);
      CAPI capi = new CAPI(target);
      RuntimePropsRequest request = new RuntimePropsRequest();
      request.setPrice(100);
      AsynchronousCall capiCall = new AsynchronousCall();
      capiCall.addCallListener(new SuccessfulCallListener());
      capiCall.call(capi, request);
      while (successfulTestOkay == null) {
         Thread.currentThread().sleep(200);
      }
      if (assertionSuccessfulTestException != null) {
         throw assertionSuccessfulTestException;
      }
   }

   public void testUnsuccessfulAsynchronousCall() throws Throwable {

      TargetDescriptor target = new TargetDescriptor(AllTests.url(), 5000, 1000, 4000);
      CAPI capi = new CAPI(target);
      RuntimePropsRequest request = new RuntimePropsRequest();
      AsynchronousCall capiCall = new AsynchronousCall();
      capiCall.addCallListener(new UnsuccessfulCallListener());
      capiCall.call(capi, request);
      while (unsuccessfulTestOkay == null) {
         Thread.currentThread().sleep(200);
      }
      if (assertionUnsuccessfulTestException != null) {
         throw assertionUnsuccessfulTestException;
      }
   }

   private class SuccessfulCallListener implements CallListener {
      public void callSucceeded(CallSucceededEvent event) {
         try {
            RuntimePropsResult result = (RuntimePropsResult) event.getResult();
            assertNotNull(result);
            assertEquals(20.6f, result.getTaxes(), 0.01f);
            assertEquals("Euros", result.getCurrency());
            assertTrue(result.duration() >= 0L);
            successfulTestOkay = Boolean.TRUE;
         } catch (Throwable ex) {
            assertionSuccessfulTestException = ex;
            successfulTestOkay = Boolean.FALSE;
         }
      }

      public void callFailed(CallFailedEvent event) {
         try {
            fail("This call should have succeeded. Reason: " + event.getException().getMessage());
            successfulTestOkay = Boolean.FALSE;
         } catch (Throwable ex) {
            assertionSuccessfulTestException = ex;
            successfulTestOkay = Boolean.FALSE;
         }
      }
   }

   private class UnsuccessfulCallListener implements CallListener {
      public void callSucceeded(CallSucceededEvent event) {
         try {
            fail("This call should have failed.");
            unsuccessfulTestOkay = Boolean.FALSE;
         } catch (Throwable ex) {
            assertionUnsuccessfulTestException = ex;
            unsuccessfulTestOkay = Boolean.FALSE;
         }
      }

      public void callFailed(CallFailedEvent event) {
         try {
            assertNotNull(event.getException());
            //event.getException().printStackTrace();
            assertTrue(event.getException() instanceof UnacceptableRequestException);
            unsuccessfulTestOkay = Boolean.TRUE;
         } catch (Throwable ex) {
            assertionUnsuccessfulTestException = ex;
            unsuccessfulTestOkay = Boolean.FALSE;
         }
      }
   }
}
