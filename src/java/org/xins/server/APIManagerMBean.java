/*
 * $Id: APIManagerMBean.java,v 1.10 2007/09/18 08:45:06 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

/**
 * Management bean for the API.
 *
 * @version $Revision: 1.10 $ $Date: 2007/09/18 08:45:06 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.5.0
 */
public interface APIManagerMBean {

   /**
    * Gets the version of the API.
    *
    * @return
    *    the version of the API running.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getAPIVersion() throws IOException;

   /**
    * Gets the version of XINS which is running this API.
    *
    * @return
    *    the version of XINS running the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getXINSVersion() throws IOException;

   /**
    * Gets the name of the API.
    *
    * @return
    *    the name the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getAPIName() throws IOException;

   /**
    * Gets the bootstrap properties.
    *
    * @return
    *    the bootstrap properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   CompositeDataSupport getBootstrapProperties() throws IOException;

   /**
    * Gets the runtime properties.
    *
    * @return
    *    the runtime properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   CompositeDataSupport getRuntimeProperties() throws IOException;

   /**
    * Gets the time at which the API was started.
    *
    * @return
    *    the time at which the API was started in the form YYYYMMDDThhmmssSSS+TZ.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getStartupTime() throws IOException;

   /**
    * Gets the list of the API functions.
    *
    * @return
    *    the list of the API function names.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String[] getFunctionNames() throws IOException;

   /**
    * Gets the statistics of the functions.
    *
    * @return
    *    the statistics of the functions.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public TabularDataSupport getStatistics() throws IOException;

   /**
    * Executes the _NoOp meta function.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    *
    * @throws NoSuchFunctionException
    *    if the _noOp meta function is not found.
    *
    * @throws AccessDeniedException
    *    if the JMX client is not in the ACLs to execute the _noOp meta function.
    */
   void noOp() throws IOException, NoSuchFunctionException, AccessDeniedException;

   /**
    * Reloads the runtime properties if the file has changed.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    *
    * @throws NoSuchFunctionException
    *    if the _ReloadProperties meta function is not found.
    *
    * @throws AccessDeniedException
    *    if the JMX client is not in the ACLs to execute the _ReloadProperties meta function.
    */
   void reloadProperties() throws IOException, NoSuchFunctionException, AccessDeniedException;
}
