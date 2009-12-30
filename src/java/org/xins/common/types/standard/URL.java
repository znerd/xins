/*
 * $Id: URL.java,v 1.15 2007/09/18 11:21:04 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.net.MalformedURLException;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_url</em>.
 *
 * @version $Revision: 1.15 $ $Date: 2007/09/18 11:21:04 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.2.0
 */
public final class URL extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final URL SINGLETON = new org.xins.common.types.standard.URL();

   /**
    * Constructs a new <code>Boolean</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private URL() {
      super("_url", String.class);
   }

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>String</code>. This is in fact a no-op, the method will just
    * return the input value. This method exists to be in line with the
    * interfaces of the other standard type classes.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the original {@link String}.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    */
   public static String fromStringForRequired(String string)
   throws TypeValueException, IllegalArgumentException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else if (!SINGLETON.isValidValue(string)) {
         throw new TypeValueException(SINGLETON, string);
      } else {
         return string;
      }
   }

   /**
    * Converts the specified string value to a <code>String</code>
    * value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the original {@link String}, can be <code>null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static String fromStringForOptional(String string)
   throws TypeValueException {
      if (!SINGLETON.isValidValue(string)) {
         throw new TypeValueException(SINGLETON, string);
      } else {
         return string;
      }
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {
      try {
         new TargetDescriptor(value);
      } catch (MalformedURLException cause) {
         throw new TypeValueException(this, value, cause);
      }
   }

   @Override
   protected Object fromStringImpl(String string) {
      return string;
   }

   @Override
   public String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      return (String) value;
   }

   @Override
   public String getDescription() {
      return "Uniform Resource Locator.";
   }
}
