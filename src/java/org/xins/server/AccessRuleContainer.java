/*
 * $Id: AccessRuleContainer.java,v 1.15 2007/09/18 08:45:03 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.text.ParseException;

/**
 * Collection of one or more access rules.
 *
 * @version $Revision: 1.15 $ $Date: 2007/09/18 08:45:03 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.1.0
 */
public interface AccessRuleContainer {

   /**
    * Determines if the specified IP address is allowed to access the
    * specified function.
    *
    * <p>This method finds the first matching rule and then returns the
    * <em>allow</em> property of that rule (see
    * {@link AccessRule#isAllowRule()}). If there is no matching rule, then
    * <code>null</code> is returned.
    *
    * @param ip
    *    the IP address, cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param conventionName
    *    the name of the calling convention, can be <code>null</code>.
    *
    * @return
    *    {@link Boolean#TRUE} if the specified IP address is allowed to access
    *    the specified function, {@link Boolean#FALSE} if it is disallowed
    *    access or <code>null</code> if there is no match.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null || functionName == null</code>.
    *
    * @throws ParseException
    *    if the specified IP address is malformed.
    *
    * @since XINS 2.1.
    */
   Boolean isAllowed(String ip, String functionName, String conventionName)
   throws IllegalArgumentException, ParseException;

   /**
    * Disposes this access rule. All claimed resources are freed as much as
    * possible.
    *
    * <p>Once disposed, the {@link #isAllowed} method should no longer be
    * called.
    */
   void dispose();
}
