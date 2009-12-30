/*
 * $Id: ElementUtils.java 8214 2009-02-11 09:44:23Z ernst $
 */
package org.xins.common.xml;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;

/**
 * Utility functions that help in processing XML <code>Element</code>
 * instances.
 *
 * @version $Revision: 8214 $ $Date: 2009-02-11 10:44:23 +0100 (wo, 11 feb 2009) $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 3.0
 */
public final class ElementUtils {

   /**
    * Constructs a new <code>ElementUtils</code> instance.
    */
   private ElementUtils() {
      // empty
   }

   /**
    * Parses an optional element attribute as a <code>boolean</code>.
    *
    * <p>The attribute value must be unset, <code>"true"</code> or
    * <code>"false"</code>. Otherwise an exception is thrown.
    *
    * @param xml
    *    the XML {@link Element}, cannot be <code>null</code>.
    *
    * @param attributeName
    *    the name of the attribute to parse, cannot be <code>null</code>.
    *
    * @param fallback
    *    the fallback default value, to use when the attribute value is unset
    *    (<code>null</code>).
    *
    * @throws IllegalArgumentException
    *    if <code>xml == null || attributeName == null</code>.
    *
    * @throws ParseException
    *    if the 
    */
   public static final boolean parseBooleanAttribute(Element xml,
                                                     String  attributeName,
                                                     boolean fallback)
   throws IllegalArgumentException,
          ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("xml", xml, "attributeName", attributeName);

      // Get the attribute value (as a character String)
      String attributeValue = xml.getAttribute(attributeName);

      // Interpret
      if (attributeValue == null) {
         return fallback;
      } else if ("true".equals(attributeValue)) {
         return true;
      } else if ("false".equals(attributeValue)) {
         return false;
      }

      throw new ParseException("Attribute \"" + attributeName + "\" on \"" + xml.getLocalName() + "\" element is \"" + attributeValue + "\". Expecting either (null), \"true\" or \"false\".");
   }
}
