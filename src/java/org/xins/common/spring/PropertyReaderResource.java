/*
 * $Id$
 *
 * Copyright 2011 Ernst de Haan
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spring;

import java.io.InputStream;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.AbstractResource;

import org.xins.common.collections.PropertyReader;

/**
 * Spring <code>Resource</code> implementation that uses a 
 * <code>PropertiesReader</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public class PropertyReaderResource extends AbstractResource {

   public PropertyReaderResource(PropertyReader pr) {
      this(pr, "");
   }

   public PropertyReaderResource(PropertyReader pr, String description) {
      _pr = pr;
      _description = (description == null) ? "" : description;
   }

   private final PropertyReader _pr;
   private final String _description;

   public String getDescription() {
      return _description;
   }

   public InputStream getInputStream() throws IOException {
      throw new IOException("Method getInputStream() not implemented (yet).");
   }
}
