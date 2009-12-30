/*
 * $Id: APIServletSingleThreaded.java,v 1.1 2007/12/17 13:36:43 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import javax.servlet.SingleThreadModel;

/**
 * This class is similar to APIServlet except that it implements the javax.servlet.SingleThreadModel
 * to indique that only 1 thread can handle only 1 request at a time.
 *
 * @version $Revision: 1.1 $ $Date: 2007/12/17 13:36:43 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.2
 */
public class APIServletSingleThreaded extends APIServlet implements SingleThreadModel {
}
