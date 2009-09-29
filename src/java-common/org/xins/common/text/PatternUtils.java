/*
 * $Id: PatternUtils.java,v 1.4 2007/09/18 08:45:07 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingException;
import org.xins.common.Utils;

/**
 * Regular expressions related utility functions.
 *
 * @version $Revision: 1.4 $ $Date: 2007/09/18 08:45:07 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.0
 */
public final class PatternUtils {

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Constructs a new <code>PatternUtils</code> object.
    */
   private PatternUtils() {
      // empty
   }

   /**
    * Compiles the given regular expression to a Perl5 pattern object.
    *
    * @param regexp
    *     the String value of the Perl5 regular expresssion, cannot be <code>null</code>.
    *
    * @return
    *    the Perl5 pattern, never <code>null</code>
    *
    * @throws IllegalArgumentException
    *    if <code>regexp == null</code>.
    *
    * @throws ProgrammingException
    *    if the pattern cannot be complied.
    *
    * @since XINS 2.0.
    */
   public static Pattern createPattern(String regexp) throws IllegalArgumentException, ProgrammingException {
      MandatoryArgumentChecker.check("regexp", regexp);
      try {
         Pattern pattern = PATTERN_COMPILER.compile(regexp,
               Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK);
         return pattern;
      } catch (MalformedPatternException exception) {
         throw Utils.logProgrammingError(exception);
      }
   }
}
