/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.io.File;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;

/**
 * Standard type <em>_dir</em>. A value of this type represents a directory on
 * a file system. The directory must exist, otherwise the path string is
 * considered invalid.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 2.2
 */
public class Dir extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Dir SINGLETON = new Dir();

   /**
    * Constructs a new <code>Dir</code> instance.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Dir() {
      super("_dir", File.class);
   }

   /**
    * Constructs a <code>File</code> from the specified
    * non-<code>null</code> string.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link File} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static File fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (File) SINGLETON.fromString(string);
   }

   /**
    * Constructs an <code>File</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link File}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static File fromStringForOptional(String string)
   throws TypeValueException {
      return (string == null) ? null : fromStringForRequired(string);
   }

   /**
    * Converts the specified <code>File</code> object to a string.
    *
    * @param dir
    *    the {@link File} object to convert to a string,
    *    can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value;
    *    or <code>null</code> if and only if <code>dir == null</code>.
    */
   public static String toString(File dir) {

      // Short-circuit if the argument is null
      if (dir == null) {
         return null;
      }

      return dir.getPath();
   }

   @Override
   protected final void checkValueImpl(String value)
   throws TypeValueException {

      File file = new File(value);
      if (! file.exists()) {
         throw new TypeValueException(this, value, "Path does not exist. Absolute path is \"" + file.getAbsolutePath() + "\".");
      } else if (! file.isDirectory()) {
         throw new TypeValueException(this, value, "Path does not denote a directory. Absolute path is \"" + file.getAbsolutePath() + "\".");
      } else if (! file.canRead()) {
         throw new TypeValueException(this, value, "Path cannot be read. Absolute path is \"" + file.getAbsolutePath() + "\".");
      }
   }

   @Override
   protected final Object fromStringImpl(String string) {
      return new File(string);
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      File f = (File) value;

      // Convert the object to a String
      return f.getPath();
   }

   @Override
   public String getDescription() {
      return "Directory on a file system.";
   }
}
