/*
 * $Id: ElementSerializer.java,v 1.33 2007/09/18 11:21:02 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.znerd.xmlenc.XMLOutputter;

/**
 * Serializer that takes an <code>Element</code> and converts it to an XML
 * string.
 *
 * <p>This class is not thread-safe. It should only be used on one thread at a
 * time.
 *
 * <p>Since XINS 3.0, the encoding is configurable. The default encoding is
 * UTF-8.
 *
 * @version $Revision: 1.33 $ $Date: 2007/09/18 11:21:02 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 */
public final class ElementSerializer {

   /**
    * Constructs a new <code>ElementSerializer</code>. The encoding defaults
    * to <code>"UTF-8"</code>.
    */
   public ElementSerializer() {
      _encoding = "UTF-8";
   }

   /**
    * The encoding to use. The default is <code>"UTF-8"</code>.
    */
   private String _encoding;

   /**
    * Retrieves the encoding. The default is <code>"UTF-8"</code>.
    *
    * @return
    *    the encoding, never <code>null</code>.
    *
    * @since XINS 3.0
    */
   public String getEncoding() {
      return _encoding;
   }

   /**
    * Sets the encoding to use when serializing. Setting an unsupported
    * encoding will cause {@link #serialize(Element)} to fail with a
    * {@link ElementSerializationException}.
    *
    * @param newEncoding
    *    the new encoding, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>newEncoding == null</code>.
    *
    * @since XINS 3.0
    */
   public void setEncoding(String newEncoding) {
      MandatoryArgumentChecker.check("newEncoding", newEncoding);
      _encoding = newEncoding;
   }

   /**
    * Serializes the element to XML. This method is not reentrant. Hence, it
    * should only be used from a single thread.
    *
    * @param element
    *    the element to serialize, cannot be <code>null</code>.
    *
    * @return
    *    an XML document that represents <code>element</code>, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>element == null</code>.
    *
    * @throws ElementSerializationException
    *    if the element serialization failed, for example because of an
    *    unsupported encoding (since XINS 3.0).
    */
   public String serialize(Element element)
   throws IllegalArgumentException, ElementSerializationException {

      // Check argument
      MandatoryArgumentChecker.check("element", element);

      // Create an XMLOutputter
      Writer fsw = new StringWriter(512);
      XMLOutputter out;
      try {
         out = new XMLOutputter(fsw, _encoding);
      } catch (UnsupportedEncodingException cause) {
         throw new ElementSerializationException("Unsupported encoding \"" + _encoding + "\".", cause);
      }

      // XXX: Allow output of declaration to be configured?

      // Output the XML that represents the Element
      try {
         output(out, element);

      // I/O errors should not happen on a StringWriter
      } catch (IOException exception) {
         throw Utils.logProgrammingError(exception);
      }

      return fsw.toString();
   }

   /**
    * Generates XML for the specified <code>Element</code>.
    *
    * @param out
    *    the {@link XMLOutputter} to use, cannot be <code>null</code>.
    *
    * @param element
    *    the {@link Element} object to convert to XML, cannot be
    *    <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>out == null || element == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    */
   public void output(XMLOutputter out, Element element)
   throws NullPointerException, IOException {

      String namespacePrefix = element.getNamespacePrefix();
      String namespaceURI    = element.getNamespaceURI();
      String localName       = element.getLocalName();
      Map namespaces         = new HashMap();

      // Write an element with namespace
      if (namespacePrefix != null) {
         out.startTag(namespacePrefix + ':' + localName);

      // Write an element without namespace
      } else {
         out.startTag(localName);
      }

      if (namespaceURI != null) {

         // Associate the namespace with the prefix in the result XML
         if (namespacePrefix == null) {
            out.attribute("xmlns", namespaceURI);
            namespaces.put("", namespaceURI);
         } else {
            out.attribute("xmlns:" + namespacePrefix, namespaceURI);
            namespaces.put(namespacePrefix, namespaceURI);
         }
      }

      // Loop through all attributes
      Map attributes = element.getAttributeMap();
      Iterator entries = attributes.entrySet().iterator();
      while (entries.hasNext()) {

         // Get the next Map.Entry from the iterator
         Map.Entry entry = (Map.Entry) entries.next();

         // Get the namespace, local name and value
         Element.QualifiedName qn   = (Element.QualifiedName) entry.getKey();
         String attrNamespaceURI    = qn.getNamespaceURI();
         String attrLocalName       = qn.getLocalName();
         String attrNamespacePrefix = qn.getNamespacePrefix();
         String attrValue           = (String) entry.getValue();

         // Do not write the attribute if no value or it is the namespace URI.
         if (attrValue != null &&
               (!"xmlns".equals(attrNamespacePrefix) || !attrLocalName.equals(namespacePrefix))) {

            // Write the attribute with prefix
            if (attrNamespacePrefix != null) {
               out.attribute(attrNamespacePrefix + ':' + attrLocalName, attrValue);

            // Write an attribute without prefix
            } else {
               out.attribute(attrLocalName, attrValue);
            }

            // Write the attribute namespace
            if (attrNamespaceURI != null) {

               // Associate the namespace with the prefix in the result XML
               if (attrNamespacePrefix == null && !namespaces.containsKey("")) {
                  out.attribute("xmlns", attrNamespaceURI);
                  namespaces.put("", namespaceURI);
               } else if (!namespaces.containsKey(attrNamespacePrefix)) {
                  out.attribute("xmlns:" + attrNamespacePrefix, attrNamespaceURI);
                  namespaces.put(attrNamespacePrefix, namespaceURI);
               }
            }
         }
      }

      // Process all contained elements and text snippets
      List content = element.getChildren();
      int count = content.size();
      for (int i = 0; i < count; i++) {
         Object o = content.get(i);
         if (o instanceof Element) {
            output(out, (Element) o);
         } else {
            out.pcdata((String) o);
         }
      }

      // End the tag
      out.endTag();
   }
}
