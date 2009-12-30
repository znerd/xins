/*
 * $Id: Viewer.java,v 1.7 2007/09/18 11:21:02 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.xml;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.xins.common.io.IOReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.ParseException;

/**
 * Swing XML Viewer.
 * If the text sent is not XML the content is put in the text pane without
 * syntax highlighting.
 *
 * <p>Note: This parser is
 * <a href="http://www.w3.org/TR/REC-xml-names/">XML Namespaces</a>-aware.
 *
 * @version $Revision: 1.7 $ $Date: 2007/09/18 11:21:02 $
 *
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 2.1
 */
public class Viewer extends JTextPane {

   /**
    * Flag to indicate whether the received XML should be indented or left as is.
    */
   private boolean indentXML;

   /**
    * Constructs a new <code>Viewer</code>.
    */
   public Viewer() {
      indentXML = true;
   }

   /**
    * Parses the specified String to render the XML to the text pane.
    *
    * @param text
    *    the XML text to be parsed, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>text == null</code>.
    */
   public void parse(String text)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("text", text);
      setText("");

      // Write the headers
      int nextDecl = 0;
      int endDecl = 0;
      String decl = text.substring(nextDecl, nextDecl + 2);
      while (decl.equals("<?") || decl.equals("<!")) {
         endDecl = text.indexOf('>', endDecl);
         int middleDecl = text.indexOf('<', endDecl);
         if (middleDecl < endDecl) {
             endDecl = text.indexOf('>', middleDecl) + 1;
             continue;
         }
         appendText(text.substring(nextDecl, endDecl + 1) + "\n", null);
         nextDecl = text.indexOf('<', endDecl);
         endDecl = nextDecl;
         decl = text.substring(nextDecl, nextDecl + 2);
      }

      try {
         parse(new StringReader(text));
      } catch (ParseException pe) {
         setText(text);
      } catch (IOException ioe) {
         setText(text);
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
    * @throws IllegalArgumentException
    *    if <code>in == null</code>.
    *
    * @throws IOException
    *    if there is an I/O error.
    */
   public void parse(InputStream in)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      String text = IOReader.readFully(in);
      parse(text);
   }

   /**
    * Parses content of a character stream to create an XML
    * <code>Element</code> object.
    *
    * @param in
    *    the character stream that is supposed to contain XML to be parsed,
    *    not <code>null</code>.
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
   private void parse(Reader in)
   throws IllegalArgumentException,
          IOException,
          ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("in", in);

      // Wrap the Reader in a SAX InputSource object
      InputSource source = new InputSource(in);

      // Initialize our SAX event handler
      Handler handler = new Handler();

      try {
         // Let SAX parse the XML, using our handler
         SAXParserProvider.get().parse(source, handler);

      } catch (SAXException exception) {

         String exMessage = exception.getMessage();

         // Construct complete message
         String message = "Failed to parse XML";

         // Throw exception with message, and register cause exception
         throw new ParseException(message, exception, exMessage);
      }
   }

   /**
    * Indicates whether to indent the XML or leave it as received.
    *
    * @param indentXML
    *    <code>true</code> if the XML should be indented, <code>false</code> otherwise.
    */
   public void setIndentation(boolean indentXML) {
      this.indentXML = indentXML;
   }

   /**
    * Append text at the end of the document.
    *
    * @param text
    *    the text to append, cannot be <code>null</code>.
    *
    * @param style
    *    the style in which the text should appear, can be <code>null</code>
    */
   public void appendText(String text, Style style) {
      try {
         getDocument().insertString(getDocument().getLength(), text, style);
      } catch (BadLocationException ble) {
      }
   }

   /**
    * SAX event handler that will parse XML.
    *
    * @version $Revision: 1.7 $ $Date: 2007/09/18 11:21:02 $
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   private class Handler extends DefaultHandler {

      /**
       * The styles used for the syntax highlighting.
       */
      private Style elementStyle;
      private Style attrNameStyle;
      private Style attrValueStyle;
      private Style contentStyle;

      /**
       * The level for the element pointer within the XML document. Initially
       * this field is <code>-1</code>, which indicates the current element
       * pointer is outside the document. The value <code>0</code> is for the
       * root element (<code>result</code>), etc.
       */
      private int _level;

      /**
       * Flag indicating whether the current element sub-elements (<code>true</code>)
       * or not (<code>false</code>).
       */
      private boolean _hasSubElement;

      /**
       * Constructs a new <code>Handler</code> instance.
       */
      private Handler() {

         _level = -1;
         _hasSubElement = false;

         // Define the style needed
         elementStyle = addStyle("Element", null);
         StyleConstants.setForeground(elementStyle, Color.BLUE.darker());
         attrNameStyle = addStyle("AttrName", null);
         StyleConstants.setForeground(attrNameStyle, Color.GREEN.darker());
         attrValueStyle = addStyle("AttrValue", null);
         StyleConstants.setForeground(attrValueStyle, Color.RED.darker());
         contentStyle = addStyle("Content", null);
         StyleConstants.setForeground(contentStyle, Color.BLACK);
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

         // Make sure namespaceURI is either null or non-empty
         namespaceURI = "".equals(namespaceURI) ? null : namespaceURI;

         // Increase the element depth level
         _level++;
         _hasSubElement = false;

         indent();
         appendText("<", null);

         appendText(qName, elementStyle);

         // Find the namespace prefix
         String prefixNS = null;

         if (qName != null && qName.indexOf(':') != -1) {
            prefixNS = "xmlns:" + qName.substring(0, qName.indexOf(':'));
         } else {
            prefixNS = "xmlns";
         }

         if (namespaceURI != null) {
            appendText(" " + prefixNS, attrNameStyle);
            appendText("=\"", null);
            appendText(namespaceURI, attrValueStyle);
            appendText("\"", null);
         }

         // Add all attributes
         for (int i = 0; i < atts.getLength(); i++) {
            String attrValue = atts.getValue(i);
            String attrQName = atts.getQName(i);
            appendText(" " + attrQName, attrNameStyle);
            appendText("=\"", null);
            appendText(attrValue, attrValueStyle);
            appendText("\"", null);
         }
         appendText(">", null);
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

         if (_hasSubElement) {
            indent();
         }
         appendText("</", null);
         appendText(qName, elementStyle);
         appendText(">", null);

         _level--;
         _hasSubElement = true;
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

         appendText(new String(ch, start, length), contentStyle);
      }

      public InputSource resolveEntity(String publicId, String systemId) {
         return new InputSource(new ByteArrayInputStream(new byte[0]));
      }

      /**
       * Indent if needed.
       */
      private void indent() {
         if (indentXML) {
            String indentation = "\n";
            for (int i = 0; i < _level; i++) {
               indentation += "\t";
            }
            appendText(indentation, null);
         }
      }
   }
}
