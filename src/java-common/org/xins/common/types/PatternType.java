/*
 * $Id: PatternType.java,v 1.26 2007/08/27 11:18:21 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.TextUtils;

/**
 * Abstract base class for pattern types. A pattern type only accepts values
 * that match a certain regular expression.
 *
 * @version $Revision: 1.26 $ $Date: 2007/08/27 11:18:21 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class PatternType extends Type {

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Pattern string. This is the uncompiled version of {@link #_pattern}.
    * This field cannot be <code>null</code>.
    */
   private final String _patternString;

   /**
    * Compiled pattern. This is the compiled version of
    * {@link #_patternString}. This field cannot be <code>null</code>.
    */
   private final Pattern _pattern;

   /**
    * Creates a new <code>PatternType</code> instance. The name of the type
    * needs to be specified. The value class (see
    * {@link Type#getValueClass()}) is set to {@link String String.class}.
    *
    * @param name
    *    the name of the type, not <code>null</code>.
    *
    * @param pattern
    *    the regular expression the values must match, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || pattern == null</code>.
    *
    * @throws PatternCompileException
    *    if the specified pattern is considered invalid.
    */
   protected PatternType(String name, String pattern)
   throws IllegalArgumentException, PatternCompileException {
      super(name, String.class);

      if (pattern == null) {
         throw new IllegalArgumentException("pattern == null");
      }

      // Compile the regular expression to a Pattern object
      try {
         synchronized (PATTERN_COMPILER) {
            _pattern = PATTERN_COMPILER.compile(pattern,
                                                Perl5Compiler.READ_ONLY_MASK);
         }

      // Handle pattern compilation error
      } catch (MalformedPatternException cause) {
         PatternCompileException e = new PatternCompileException(pattern);
         e.initCause(cause);
         throw e;
      }

      // Store the original pattern string
      _patternString = pattern;
   }

   @Override
   protected final void checkValueImpl(String value) throws TypeValueException {

      // Determine if the value matches the pattern
      try {
         Perl5Matcher patternMatcher = new Perl5Matcher();
         if (! patternMatcher.matches(value, _pattern)) {
            throw new TypeValueException(this, value, "String does not match pattern.");
         }

      // If the call causes an exception, then log that exception and assume
      // the value does not match the pattern
      } catch (Throwable cause) {
         String detail = "Assuming the value \"" + value + "\" is invalid for the pattern \"" + _patternString + "\".";
         Utils.logProgrammingError(detail, cause);
         throw new TypeValueException(this, value, "Pattern matching caused an exception.", cause);
      }
   }

   @Override
   protected final Object fromStringImpl(String value) {
      return value;
   }

   @Override
   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      String s = (String) value;
      checkValue(s);
      return s;
   }

   @Override
   public String getDescription() {
      return "Text that matches the regular expression " + TextUtils.quote(getPattern()) + '.';
   }

   /**
    * Returns the pattern.
    *
    * @return
    *    the pattern, not <code>null</code>.
    */
   public String getPattern() {
      return _patternString;
   }
}
