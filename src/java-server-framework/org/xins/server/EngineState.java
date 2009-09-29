/*
 * $Id: EngineState.java,v 1.13 2007/03/15 17:08:41 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * State of an <code>Engine</code>.
 *
 * @version $Revision: 1.13 $ $Date: 2007/03/15 17:08:41 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
final class EngineState {
   static final Type INTERMEDIATE_STATE = new Type();

   static final Type USABLE_STATE = new Type();

   static final Type ERROR_STATE = new Type();

   /**
    * The <em>INITIAL</em> state.
    */
   static final EngineState INITIAL =
      new EngineState("INITIAL", INTERMEDIATE_STATE);

   /**
    * The <em>BOOTSTRAPPING_FRAMEWORK</em> state.
    */
   static final EngineState BOOTSTRAPPING_FRAMEWORK =
      new EngineState("BOOTSTRAPPING_FRAMEWORK", INTERMEDIATE_STATE);

   /**
    * The <em>FRAMEWORK_BOOTSTRAP_FAILED</em> state.
    */
   static final EngineState FRAMEWORK_BOOTSTRAP_FAILED =
      new EngineState("FRAMEWORK_BOOTSTRAP_FAILED", ERROR_STATE);

   /**
    * The <em>CONSTRUCTING_API</em> state.
    */
   static final EngineState CONSTRUCTING_API =
      new EngineState("CONSTRUCTING_API", INTERMEDIATE_STATE);

   /**
    * The <em>API_CONSTRUCTION_FAILED</em> state.
    */
   static final EngineState API_CONSTRUCTION_FAILED =
      new EngineState("API_CONSTRUCTION_FAILED", ERROR_STATE);

   /**
    * The <em>BOOTSTRAPPING_API</em> state.
    */
   static final EngineState BOOTSTRAPPING_API =
      new EngineState("BOOTSTRAPPING_API", INTERMEDIATE_STATE);

   /**
    * The <em>API_BOOTSTRAP_FAILED</em> state.
    */
   static final EngineState API_BOOTSTRAP_FAILED =
      new EngineState("API_BOOTSTRAP_FAILED", ERROR_STATE);

   /**
    * The <em>INITIALIZING_API</em> state.
    */
   static final EngineState INITIALIZING_API =
      new EngineState("INITIALIZING_API", INTERMEDIATE_STATE);

   /**
    * The <em>API_INITIALIZATION_FAILED</em> state.
    */
   static final EngineState API_INITIALIZATION_FAILED =
      new EngineState("API_INITIALIZATION_FAILED", ERROR_STATE);

   /**
    * The <em>READY</em> state.
    */
   static final EngineState READY =
      new EngineState("READY", USABLE_STATE);

   /**
    * The <em>DISPOSING</em> state.
    */
   static final EngineState DISPOSING =
      new EngineState("DISPOSING", INTERMEDIATE_STATE);

   /**
    * The <em>DISPOSED</em> state.
    */
   static final EngineState DISPOSED
      = new EngineState("DISPOSED", INTERMEDIATE_STATE);

   /**
    * The name of this state. Cannot be <code>null</code>.
    */
   private final String _name;

   /**
    * The type of this state. Never <code>null</code>.
    */
   private final Type _type;

   /**
    * Constructs a new <code>EngineState</code> object.
    *
    * @param name
    *    the name of this state, cannot be <code>null</code>.
    *
    * @param type
    *    the type of this state, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || type == null</code>.
    */
   EngineState(String name, Type type)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name, "type", type);

      // Initialize fields
      _name = name;
      _type = type;
   }

   /**
    * Returns the name of this state.
    *
    * @return
    *    the name of this state, cannot be <code>null</code>.
    */
   public String getName() {
      return _name;
   }

   /**
    * Checks if this state is an error state.
    *
    * @return
    *    <code>true</code> if this is an error state, <code>false</code>
    *    otherwise.
    */
   public boolean isError() {
      return _type == ERROR_STATE;
   }

   /**
    * Checks if this state allows function invocations.
    *
    * @return
    *    <code>true</code> if this state allows function invocations,
    *    <code>false</code> otherwise.
    */
   public boolean allowsInvocations() {
      return _type == USABLE_STATE;
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    the name of this state, never <code>null</code>.
    */
   public String toString() {
      return _name;
   }

   /**
    * Categorization of an engine state.
    *
    * @version $Revision: 1.13 $ $Date: 2007/03/15 17:08:41 $
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   static class Type {

      /**
       * Constructs a new instance.
       */
      private Type() {
         // empty
      }
   }
}
