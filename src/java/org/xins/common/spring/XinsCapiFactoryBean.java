/*
 * $Id: XinsCapiFactoryBean.java,v 1.3 2007/09/18 11:21:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spring;

import org.springframework.beans.factory.FactoryBean;

import org.xins.client.AbstractCAPI;

/**
 * FactoryBean for locally defined CAPI references.
 * This class requires the Spring library.
 *
 * @version $Revision: 1.3 $ $Date: 2007/09/18 11:21:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0
 */
public class XinsCapiFactoryBean extends XinsClientInterceptor implements FactoryBean {

   public Object getObject() throws Exception {
      return capi;
   }

   public Class getObjectType() {
      if (capi == null) {
         return AbstractCAPI.class;
      } else {
         return capi.getClass();
      }
   }

   public boolean isSingleton() {
      return true;
   }

}
