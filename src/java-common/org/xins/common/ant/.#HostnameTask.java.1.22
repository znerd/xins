/*
 * $Id: HostnameTask.java,v 1.22 2007/05/21 15:11:32 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.xins.common.text.TextUtils;
import org.xins.common.net.IPAddressUtils;

/**
 * Apache Ant task that determines the host name.
 *
 * @version $Revision: 1.22 $ $Date: 2007/05/21 15:11:32 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class HostnameTask extends Task {

   /**
    * Default name for the property to set.
    */
   public static final String DEFAULT_PROPERTY_NAME = "hostname";

   /**
    * Name of the property to store the host name in. Default is
    * {@link #DEFAULT_PROPERTY_NAME}.
    */
   private String _propertyName = DEFAULT_PROPERTY_NAME;

   /**
    * Sets the name of the property. If <code>null</code> or <code>""</code>
    * is passed as the argument, then {@link #DEFAULT_PROPERTY_NAME} is
    * assumed.
    *
    * @param newPropertyName
    *    the name of the property to store the host name in, or
    *    <code>null</code> if the {@link #DEFAULT_PROPERTY_NAME} should be
    *    assumed.
    */
   public void setProperty(String newPropertyName) {
      _propertyName = TextUtils.isEmpty(newPropertyName, true)
                    ? DEFAULT_PROPERTY_NAME
                    : newPropertyName;
   }

   /**
    * Called by the project to let the task do its work.
    *
    * @throws BuildException
    *    if something goes wrong with the build.
    */
   public void execute() throws BuildException {

      // Do not override the property value
      if (getProject().getUserProperty(_propertyName) != null) {
         String logMessage = "Override ignored for property \""
                           + _propertyName
                           + "\".";
         log(logMessage, Project.MSG_VERBOSE);
         return;
      }

      // First try using the IPAddressUtils class
      String hostname = IPAddressUtils.getLocalHost((String) null);

      // No hostname, fallback to a default and then log a warning
      if (hostname == null) {
         hostname = "localhost";

         String message = "Determining hostname of localhost failed. "
                        + "Setting property to \""
                        + hostname
                        + "\".";
         log(message, Project.MSG_WARN);
      }

      // Actually set the property
      getProject().setUserProperty(_propertyName, hostname);
   }
}
