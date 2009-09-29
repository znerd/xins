/*
 * $Id: LogFilter.java 9034 2009-04-16 13:53:04Z ernst $
 *
 * Copyright 2009 PensioenPage B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Parameter value log filters. Implementations of this abstract class can be
 * used to modify parameters during logging.
 *
 * <p>A typical use is to filter out passwords so these will not be stored in
 * a logging store.
 *
 * @version $Revision: 9034 $ $Date: 2009-04-16 15:53:04 +0200 (do, 16 apr 2009) $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public abstract class LogFilter {

   /**
    * Constructs a new <code>LogFilter</code>.
    */
   protected LogFilter() {
      // empty
   }

   /**
    * Filters the specified <code>String</code> value.
    *
    * @param logger
    *    the name of the logger,
    *    for example <code>"org.xins.common.lowlevel.1050"</code>,
    *    cannot be <code>null</code>.
    *
    * @param param
    *    the name of the parameter,
    *    for example <code>"queryString"</code>,
    *    cannot be <code>null</code>.
    *
    * @param value
    *    the original parameter value, can be <code>null</code>.
    *
    * @return
    *    the parameter value to use, possibly modified,
    *    can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>logger == null || param == null</code>.
    */
   public abstract String filter(String logger, String param, String value)
   throws IllegalArgumentException;

   /**
    * Filters the specified <code>Object</code> value.
    *
    * @param logger
    *    the name of the logger,
    *    for example <code>"org.xins.common.lowlevel.1050"</code>,
    *    cannot be <code>null</code>.
    *
    * @param param
    *    the name of the parameter,
    *    for example <code>"queryString"</code>,
    *    cannot be <code>null</code>.
    *
    * @param value
    *    the original parameter value, can be <code>null</code>.
    *
    * @return
    *    the parameter value to use, possibly modified,
    *    can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>logger == null || param == null</code>.
    */
   public abstract Object filter(String logger, String param, Object value)
   throws IllegalArgumentException;
}
