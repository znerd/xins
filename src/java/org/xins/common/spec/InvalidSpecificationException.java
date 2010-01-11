/*
 * $Id: InvalidSpecificationException.java,v 1.12 2007/09/18 11:20:48 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Thrown when the specification of the API is incorrect or cannot be found.
 *
 * @version $Revision: 1.12 $ $Date: 2007/09/18 11:20:48 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.3.0
 */
public class InvalidSpecificationException extends Exception {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 8346877941172915039L;

	/**
	 * Creates a new <code>InvalidSpecificationException</code> with the reason
	 * of the problem.
	 *
	 * @param message
	 *    the reason why this exception has been thrown, can be <code>null</code>.
	 */
	InvalidSpecificationException(String message) {
		this(message, null);
	}

	/**
	 * Creates a new <code>InvalidSpecificationException</code> with the reason
	 * of the problem.
	 *
	 * @param message
	 *    the reason why this exception has been thrown, can be <code>null</code>.
	 *
	 * @param cause
	 *    the cause of the exception, can be <code>null</code>.
	 */
	InvalidSpecificationException(String message, Throwable cause) {
		super(message);
		if (cause != null) {
			initCause(cause);
		}
	}

}
