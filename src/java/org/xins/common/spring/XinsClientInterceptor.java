/*
 * $Id: XinsClientInterceptor.java,v 1.3 2007/09/18 11:21:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xins.common.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import org.xins.client.AbstractCAPI;
import org.xins.client.XINSCallException;
import org.xins.client.XINSServiceCaller;

import org.xins.common.collections.PropertiesPropertyReader;
import org.xins.common.service.Descriptor;
import org.xins.common.service.DescriptorBuilder;
import org.xins.common.service.GenericCallException;
import org.xins.common.service.TargetDescriptor;

/**
 * Interceptor for accessing a specific XINS API.
 * This class requires the Spring library.
 *
 * @version $Revision: 1.3 $ $Date: 2007/09/18 11:21:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0
 */
public class XinsClientInterceptor extends UrlBasedRemoteAccessor implements MethodInterceptor {

   /**
    * Properties containing the location of the API.
    */
   private Properties descriptorProperties;

   /**
    * time-out in milliseconds for a call to the API when a single target is specified.
    */
   private int timeout = -1;

   /**
    * The CAPI used to call the web service.
    */
   protected AbstractCAPI capi;

   /**
    * The name of the API to call.
    */
   private String serviceName;

   public void afterPropertiesSet() {
      super.afterPropertiesSet();
      prepare();
   }

   public void prepare() {
      try {
         capi = createCapi();
      } catch (MalformedURLException murlex) {
         throw new RemoteLookupFailureException("Service URL [" + getServiceUrl() + "] is invalid", murlex);
      } catch (Exception ex) {
         throw new BeanCreationException(ex.getMessage(), ex);
      }
   }


   /**
    * Sets the location of the API to call.
    * If this method is call, it will invalidate the previous call to
    * {@link #setServiceUrl} and {@link #setTimeout} methods.
    *
    * @param descriptorProperties
    *    the different destination as explained in
    *    <a href="http://xins.sourceforge.net/javadoc/1.5.2/org/xins/common/service/DescriptorBuilder.html">DescriptorBuilder</a>.
    */
   public void setServiceProperties(Properties descriptorProperties) {
      setServiceUrl(null);
      this.descriptorProperties = descriptorProperties;
   }

   /**
    * Sets the time-out for the call of the API.
    * This method requires that you also call {@link #setServiceUrl}.
    *
    * @param timeout
    *    the time-out for the call in milliseconds. This parameter will be used
    *    for the connection time-out, socket time-out and total time-out.
    */
   public void setTimeout(int timeout) {
      this.timeout = timeout;
   }

   /**
    * Sets the name of the API to call.
    * The name is used to detect the capi.&lt;api name&gt; in the service properties set.
    *
    * @param serviceName
    *    the name of the API to call.
    */
   public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
   }

   /**
    * Gets the name of the API to call.
    *
    * @return
    *    the name of the API to call.
    */
   public String getServiceName() {
      return serviceName;
   }

   /**
    * Creates the {@link Descriptor} containing the location of the API.
    *
    * @return
    *    the specified descriptor or <code>null</code> no descriptorProperties or serviceURL is set.
    *
    * @throws Exception
    *    if the descriptorProperties or serviceURL is incorrect.
    */
   public Descriptor createDescriptor() throws Exception {
      if (descriptorProperties != null) {
         PropertiesPropertyReader reader = new PropertiesPropertyReader(descriptorProperties);
         return DescriptorBuilder.build(reader, "capi." + getServiceName());
      } else if (getServiceUrl() != null) {
         if (timeout > -1) {
            return new TargetDescriptor(getServiceUrl(), timeout);
         } else {
            return new TargetDescriptor(getServiceUrl());
         }
      }
      return null;
   }

   /**
    * Creates the CAPI to call the API.
    *
    * @return
    *    the created CAPI.
    *
    * @throws Exception
    *    if the CAPI class is not found or the descriptor cannot be created.
    */
   public AbstractCAPI createCapi() throws Exception {
      Descriptor descriptor = createDescriptor();
      // Creates the CAPI (Client API) based on the class provided to the service interface.
      Constructor constCAPI = getServiceInterface().getConstructor(new Class[] {Descriptor.class});
      AbstractCAPI capi = (AbstractCAPI) constCAPI.newInstance(new Object[]{descriptor});
      return capi;
   }

   /**
    * Creates the XINSServiceCaller to call the API.
    *
    * @return
    *    the service caller to call the API.
    *
    * @throws Exception
    *    if the descriptor cannot be created or is incorrect.
    */
   public XINSServiceCaller createXinsServiceCaller() throws Exception {
      XINSServiceCaller caller = new XINSServiceCaller(createDescriptor());
      return caller;
   }

   public Object invoke(MethodInvocation invocation) throws Throwable {
      if (this.capi == null) {
         throw new IllegalStateException("XinsClientInterceptor is not properly initialized - " +
               "invoke 'prepare' before attempting any operations");
      }

      try {
         return invocation.getMethod().invoke(this.capi, invocation.getArguments());
      } catch (InvocationTargetException ex) {
         if (ex.getTargetException() instanceof XINSCallException) {
            XINSCallException callEx = (XINSCallException) ex.getTargetException();
            throw convertXinsAccessException(callEx);
         }
         throw ex.getTargetException();
      } catch (Throwable ex) {
         throw new RemoteProxyFailureException(
               "Failed to invoke XINS API for remote service [" + getServiceUrl() + "]", ex);
      }
   }

   /**
    * Convert the given XINS exception to an appropriate Spring RemoteAccessException.
    *
    * @param ex
    *    the exception to convert.
    *
    * @return
    *    the RemoteAccessException to throw.
    */
   protected RemoteAccessException convertXinsAccessException(Throwable ex) {
      if (ex instanceof GenericCallException) {
         throw new RemoteConnectFailureException(
               "Cannot connect to Burlap remote service at [" + getServiceUrl() + "]", ex);
      } else {
         throw new RemoteAccessException(
               "Cannot access Burlap remote service at [" + getServiceUrl() + "]", ex);
      }
   }
}
