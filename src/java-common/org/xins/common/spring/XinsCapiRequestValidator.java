/*
 * $Id: XinsCapiRequestValidator.java,v 1.4 2007/09/18 11:21:08 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spring;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import org.xins.client.AbstractCAPICallRequest;
import org.xins.client.UnacceptableRequestException;

/**
 * Validator for the request object that should be sent to the CAPI call.
 * This class requires the Spring library.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 11:21:08 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0
 */
public class XinsCapiRequestValidator implements Validator {

   /**
    * Creates a new instance of XinsCapiRequestValidator.
    */
   public XinsCapiRequestValidator() {
   }

   public boolean supports(Class beanClass) {

      // Only support XINS CAPI generated beans
      return beanClass.isInstance(AbstractCAPICallRequest.class);
   }

   public void validate(Object bean, Errors errors) {
      AbstractCAPICallRequest request = (AbstractCAPICallRequest) bean;
      UnacceptableRequestException validationError = request.checkParameters();
      if (validationError != null) {
         errors.reject(validationError.getMessage());
      }
   }
}
