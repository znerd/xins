/*
 * $Id: ElementParser.java,v 1.43 2007/12/17 14:22:42 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;

/**
 * XML element parser. XML is parsed to produce {@link Element} objects.
 * Comments and parsing instructions are ignored.
 *
 * <p>Note: This parser is
 * <a href="http://www.w3.org/TR/REC-xml-names/">XML Namespaces</a>-aware.
 *
 * @version $Revision: 1.43 $ $Date: 2007/12/17 14:22:42 $
 *
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 */
public class ElementParser {

   /**
    * Error state for the SAX event handler.
    */
   private static final State ERROR = new State("ERROR");

   /**
    * State for the SAX event handler in the data section (at any depth within
    * the <code>data</code> element).
    */
   private static final State PARSING = new State("PARSING");

   /**
    * State for the SAX event handler for the final state, when parsing is
    * finished.
    */
   private static final State FINISHED = new State("FINISHED");

   /**
    * Constructs a new <code>ElementParser</code>.
    */
   public ElementParser() {
      // empty
   }

   /**
    * Parses the specified String to create an XML <code>Element</code> object.
    *
    * @param text
    *    the XML text to be parsed, not <code>null</code>.
    *
    * @return
    *    the parsed result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>text == null</code>.
    *
    * @throws ParseException
    *    if the content of the character stream is not considered to be valid XML.
    *
    * @since XINS 2.0
    */
   public Element parse(String text)
   throws IllegalArgumentException,
          ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("text", text);

      try {
         return parse(new StringReader(text));
      } catch (IOException ioe) {
         throw Utils.logProgrammingError(ioe);
      }
   }

   /**
    * Parses content of a character stream to create an XML
    * <code>Element</code> object.
    *
    * @param in
    *    the byte stream that is supposed to contain XML to be parsed,
    *    not <code>null</code>.
    *
    * @return
    *    the parsed result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    *
    * @throws ParseException
    *    if the content of the character stream is not considered to be valid
    *    XML.
    *
    * @since XINS 2.0
    */
   public Element parse(InputStream in)
   throws IllegalArgumentException,
          IOException,
          ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      // Wrap the InputStream in a SAX InputSource object
      return parse(new InputSource(in));
   }

   /**
    * Parses content of a character stream to create an XML
    * <code>Element</code> object.
    *
    * @param in
    *    the character stream that is supposed to contain XML to be parsed,
    *    not <code>null</code>.
    *
    * @return
    *    the parsed result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    *
    * @throws ParseException
    *    if the content of the character stream is not considered to be valid
    *    XML.
    */
   public Element parse(Reader in)
   throws IllegalArgumentException,
          IOException,
          ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      // Wrap the Reader in a SAX InputSource object
      return parse(new InputSource(in));
   }

   /**
    * Parses content of a file to create an XML <code>Element</code> object.
    *
    * @param file
    *    the file that is supposed to contain XML to be parsed,
    *    not <code>null</code>.
    *
    * @return
    *    the parsed result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error, e.g. the file does not exist or is actually
    *    a directory.
    *
    * @throws ParseException
    *    if the content of the file is not considered to be valid XML.
    *
    * @since XINS 2.2
    */
   public Element parse(File file)
   throws IllegalArgumentException,
          IOException,
          ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("file", file);

      FileInputStream fis = null;
      try {              
         fis = new FileInputStream(file);
         return parse(fis);

      // Enrich an I/O exception with the file name
      } catch (IOException cause) {
         IOException e = new IOException("Failed to parse file " + TextUtils.quote(file.getAbsolutePath()) + " due to an I/O error.");
         e.initCause(cause);
         throw e;

      // Enrich a parse exception with the file name
      } catch (ParseException cause) {
         throw new ParseException("Failed to parse file " + TextUtils.quote(file.getAbsolutePath()) + '.', cause);

      // Anyway, always attempt to close the input stream
      } finally {
         try {
            if (fis != null) {
               fis.close();
            }
         } catch (IOException ex) {
            // Never mind
         }
      }
   }

   /**
    * Parses content of an <code>InputSource</code> to create an XML
    * <code>Element</code> object.
    *
    * @param source
    *    the input source that is supposed to contain XML to be parsed,
    *    not <code>null</code>.
    *
    * @return
    *    the parsed result, not <code>null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    *
    * @throws ParseException
    *    if the content is not considered to be valid XML.
    */
   private Element parse(InputSource source) throws IOException, ParseException {

      // TODO: Consider using an XMLReader instead of a SAXParser

      // Initialize our SAX event handler
      Handler handler = new Handler();

      try {
         // Let SAX parse the XML, using our handler
         SAXParserProvider.get().parse(source, handler);

      } catch (SAXException exception) {

         // TODO: Log: Parsing failed
         String exMessage = exception.getMessage();

         // Construct complete message
         String message = "Failed to parse XML";
         if (TextUtils.isEmpty(exMessage)) {
            message += '.';
         } else {
            message += ": " + exMessage;
         }

         // Throw exception with message, and register cause exception
         throw new ParseException(message, exception, exMessage);
      }

      Element element = handler.getElement();

      return element;
   }

   /**
    * SAX event handler that will parse XML.
    *
    * @version $Revision: 1.43 $ $Date: 2007/12/17 14:22:42 $
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   private static class Handler extends DefaultHandler {

      /**
       * The current state. Never <code>null</code>.
       */
      private State _state;

      /**
       * The element resulting of the parsing.
       */
      private Element _element;

      /**
       * The stack of child elements within the data section. The top element
       * is always <code>&lt;data/&gt;</code>.
       */
      private Stack _dataElementStack;

      /**
       * The level for the element pointer within the XML document. Initially
       * this field is <code>-1</code>, which indicates the current element
       * pointer is outside the document. The value <code>0</code> is for the
       * root element (<code>result</code>), etc.
       */
      private int _level;

      /**
       * Constructs a new <code>Handler</code> instance.
       */
      private Handler() {

         _state            = PARSING;
         _level            = -1;
         _dataElementStack = new Stack();
      }

      /**
       * Receive notification of the beginning of an element.
       *
       * @param namespaceURI
       *    the namespace URI, can be <code>null</code>.
       *
       * @param localName
       *    the local name (without prefix); cannot be <code>null</code>.
       *
       * @param qName
       *    the qualified name (with prefix), can be <code>null</code> since
       *    <code>namespaceURI</code> and <code>localName</code> are always
       *    used instead.
       *
       * @param atts
       *    the attributes attached to the element; if there are no
       *    attributes, it shall be an empty {@link Attributes} object; cannot
       *    be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>localName == null || atts == null</code>.
       *
       * @throws SAXException
       *    if the parsing failed.
       */
      public void startElement(String     namespaceURI,
                               String     localName,
                               String     qName,
                               Attributes atts)
      throws IllegalArgumentException, SAXException {

         // Temporarily enter ERROR state, on success this state is left
         State currentState = _state;
         _state = ERROR;

         // Make sure namespaceURI is either null or non-empty
         namespaceURI = "".equals(namespaceURI) ? null : namespaceURI;

         // Check preconditions
         MandatoryArgumentChecker.check("localName", localName, "atts", atts);

         // Increase the element depth level
         _level++;

         if (currentState == ERROR) {
            String detail = "Unexpected state "
                          + currentState
                          + " (level=" + _level + ')';
            throw Utils.logProgrammingError(detail);

         } else {

            // Find the namespace prefix
            String prefix = null;

            if (qName != null && qName.indexOf(':') != -1) {
               prefix = qName.substring(0, qName.indexOf(':'));
            }

            // Construct a Element
            Element element = new Element(prefix, namespaceURI, localName);

            // Add all attributes
            for (int i = 0; i < atts.getLength(); i++) {
               String attrNamespaceURI = atts.getURI(i);
               String attrLocalName    = atts.getLocalName(i);
               String attrValue        = atts.getValue(i);
               String attrQName        = atts.getQName(i);
               String attrPrefix = null;
               if (attrQName != null && attrQName.indexOf(':') != -1) {
                  attrPrefix = attrQName.substring(0, attrQName.indexOf(':'));
               }

               element.setAttribute(attrPrefix, attrNamespaceURI, attrLocalName, attrValue);
            }

            // Push the element on the stack
            _dataElementStack.push(element);

            // Reset the state from ERROR back to PARSING
            _state = PARSING;
         }
      }

      /**
       * Receive notification of the end of an element.
       *
       * @param namespaceURI
       *    the namespace URI, can be <code>null</code>.
       *
       * @param localName
       *    the local name (without prefix); cannot be <code>null</code>.
       *
       * @param qName
       *    the qualified name (with prefix), can be <code>null</code> since
       *    <code>namespaceURI</code> and <code>localName</code> are only
       *    used.
       *
       * @throws IllegalArgumentException
       *    if <code>localName == null</code>.
       */
      public void endElement(String namespaceURI,
                             String localName,
                             String qName)
      throws IllegalArgumentException {

         // Temporarily enter ERROR state, on success this state is left
         State currentState = _state;
         _state = ERROR;

         // Check preconditions
         MandatoryArgumentChecker.check("localName", localName);

         if (currentState == ERROR) {
            String detail = "Unexpected state " + currentState + " (level=" + _level + ')';
            throw Utils.logProgrammingError(detail);

         // Within data section
         } else {

            // Get the Element for which we process the end tag
            Element child = (Element) _dataElementStack.pop();

            // Add the child to the parent
            if (_dataElementStack.size() > 0) {
               Element parent = (Element) _dataElementStack.peek();
               parent.add(child);

               // Reset the state back from ERROR to PARSING
               _state = PARSING;
            } else {
               _element = child;
               _state = FINISHED;
            }

         }

         _level--;
      }

      /**
       * Receive notification of character data.
       *
       * @param ch
       *    the <code>char</code> array that contains the characters from the
       *    XML document, cannot be <code>null</code>.
       *
       * @param start
       *    the start index within <code>ch</code>.
       *
       * @param length
       *    the number of characters to take from <code>ch</code>.
       *
       * @throws IndexOutOfBoundsException
       *    if characters outside the allowed range are specified.
       *
       * @throws SAXException
       *    if the parsing failed.
       */
      public void characters(char[] ch, int start, int length)
      throws IndexOutOfBoundsException, SAXException {

         // Temporarily enter ERROR state, on success this state is left
         State currentState = _state;
         _state = ERROR;

         // Get the Element within which we found a text snippet
         Element child = (Element) _dataElementStack.peek();

         // Add the text snippet
         child.add(new String(ch, start, length));

         // Reset _state
         _state = currentState;
      }

      /**
       * Gets the parsed element.
       *
       * @return
       *    the element resulting of the parsing of the XML.
       */
      Element getElement() {

         // Check state
         if (_state != FINISHED) {
            String detail = "State is " + _state + " instead of " + FINISHED;
            throw Utils.logProgrammingError(detail);
         }

         return _element;
      }

      public InputSource resolveEntity(String publicId, String systemId) {
         return new InputSource(new ByteArrayInputStream(new byte[0]));
      }
   }

   /**
    * State of the event handler.
    *
    * @version $Revision: 1.43 $ $Date: 2007/12/17 14:22:42 $
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   private static final class State {

      /**
       * Constructs a new <code>State</code> object.
       *
       * @param name
       *    the name of this state, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      State(String name) throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);

         _name = name;
      }

      /**
       * The name of this state. Cannot be <code>null</code>.
       */
      private final String _name;

      /**
       * Returns the name of this state.
       *
       * @return
       *    the name of this state, cannot be <code>null</code>.
       */
      public String getName() {
         return _name;
      }

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    the name of this state, never <code>null</code>.
       */
      public String toString() {
         return _name;
      }
   }
}
