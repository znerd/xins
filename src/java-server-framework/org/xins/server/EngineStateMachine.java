/*
 * $Id: EngineStateMachine.java,v 1.12 2007/03/15 17:08:41 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * State machine for the XINS server engine.
 *
 * @version $Revision: 1.12 $ $Date: 2007/03/15 17:08:41 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
final class EngineStateMachine {

   /**
    * Lock for the <code>_state</code> field. This object must be locked on
    * before _state may be read or changed.
    */
   private final Object _stateLock;

   /**
    * The current state.
    */
   private EngineState _state;

   /**
    * Constructs a new <code>EngineStateMachine</code> object. Initially the
    * state will be {@link EngineState#INITIAL}.
    */
   EngineStateMachine() {
      _stateLock = new Object();
      _state     = EngineState.INITIAL;
   }

   /**
    * Gets the current state.
    *
    * @return
    *    the current state, cannot be <code>null</code>.
    */
   EngineState getState() {
      synchronized (_stateLock) {
         return _state;
      }
   }

   /**
    * Changes the current state.
    *
    * <p>If the state change is considered invalid, then an
    * {@link IllegalStateException} is thrown.
    *
    * @param newState
    *    the new state, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>newState == null</code>.
    *
    * @throws IllegalStateException
    *    if the state change is considered invalid.
    */
   void setState(EngineState newState)
   throws IllegalArgumentException, IllegalStateException {

      // Check preconditions
      MandatoryArgumentChecker.check("newState", newState);

      synchronized (_stateLock) {

         // Remember the current state
         EngineState oldState = _state;

         // Determine name of current and new state
         String oldStateName = (oldState == null)
                             ? null
                             : oldState.getName();
         String newStateName = newState.getName();

         // Short-circuit if the current equals the new state
         if (oldState == newState) {
            return;

         // Always allow changing state to DISPOSING
         } else if (oldState != EngineState.DISPOSING
                 && newState == EngineState.DISPOSING) {

         // The first state change should be to bootstrap the framework
         } else if (oldState == EngineState.INITIAL
                 && newState == EngineState.BOOTSTRAPPING_FRAMEWORK) {

         // Bootstrapping the framework may fail
         } else if (oldState == EngineState.BOOTSTRAPPING_FRAMEWORK
                 && newState == EngineState.FRAMEWORK_BOOTSTRAP_FAILED) {

         // Bootstrapping the framework can be retried
         } else if (oldState == EngineState.FRAMEWORK_BOOTSTRAP_FAILED
                 && newState == EngineState.BOOTSTRAPPING_FRAMEWORK) {

         // Bootstrapping the framework may succeed, in which case the API
         // will be constructed
         } else if (oldState == EngineState.BOOTSTRAPPING_FRAMEWORK
                 && newState == EngineState.CONSTRUCTING_API) {

         // Construction of API may fail
         } else if (oldState == EngineState.CONSTRUCTING_API
                 && newState == EngineState.API_CONSTRUCTION_FAILED) {

         // API construction can be retried
         } else if (oldState == EngineState.API_CONSTRUCTION_FAILED
                 && newState == EngineState.CONSTRUCTING_API) {

         // Construction of API may succeed, in which case the API is
         // bootstrapped
         } else if (oldState == EngineState.CONSTRUCTING_API
                 && newState == EngineState.BOOTSTRAPPING_API) {

         // Bootstrapping the API may fail
         } else if (oldState == EngineState.BOOTSTRAPPING_API
                 && newState == EngineState.API_BOOTSTRAP_FAILED) {

         // Bootstrapping the API can be retried
         } else if (oldState == EngineState.API_BOOTSTRAP_FAILED
                 && newState == EngineState.BOOTSTRAPPING_API) {

         // If bootstrapping the API succeeds, then the next step is either to
         // determine the watch interval...
         } else if (oldState == EngineState.BOOTSTRAPPING_API
                 && newState == EngineState.INITIALIZING_API) {

         // ...or to skip that and start initializing the API
         } else if (oldState == EngineState.BOOTSTRAPPING_API
                 && newState == EngineState.INITIALIZING_API) {

         // API initialization may fail
         } else if (oldState == EngineState.INITIALIZING_API
                 && newState == EngineState.API_INITIALIZATION_FAILED) {

         // API initialization may be retried, but then the interval is
         // determined first
         } else if (oldState == EngineState.API_INITIALIZATION_FAILED
                 && newState == EngineState.INITIALIZING_API) {

         // API initialization may succeed, in which case the engine is ready
         } else if (oldState == EngineState.INITIALIZING_API
                 && newState == EngineState.READY) {

         // While the servet is ready, the watch interval may be redetermined,
         // which is the first step in reinitialization
         } else if (oldState == EngineState.READY
                 && newState == EngineState.INITIALIZING_API) {

         // After disposal the state changes to the final disposed state
         } else if (oldState == EngineState.DISPOSING
                 && newState == EngineState.DISPOSED) {

         // Otherwise the state change is not allowed, fail!
         } else {

            // Log error
            Log.log_3101(oldStateName, newStateName);

            // Throw exception
            String error = "The state "
                         + oldStateName
                         + " cannot be followed by the state "
                         + newStateName
                         + '.';
            throw new IllegalStateException(error);
         }

         // Perform the state change
         _state = newState;
         Log.log_3100(oldStateName, newStateName);
      }
   }
}
