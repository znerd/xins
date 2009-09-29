
package org.xins.tests.xslt;

import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for warning.xslt
 */
public class RcsXSLTTestCase extends JuxyTestCase {

    protected void setUp() throws Exception {
        newContext("src/xslt/rcs.xslt");
    }

    public void testEmptyRevision() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");
        context().setTemplateParamValue("revision", "");

        Node result = callTemplate("revision2string");

        assertEquals("?.?", xpath("text()").toString(result).trim());
    }

    public void testSmallRevision() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");
        context().setTemplateParamValue("revision", "some");

        Node result = callTemplate("revision2string");

        assertEquals("?.?", xpath("text()").toString(result).trim());
    }

    public void testBigRevision() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");

        //TODO: Send the correct input instead of this one.
        context().setTemplateParamValue("revision", "Revision 1.2.3.5.6");

        Node result = callTemplate("revision2string");

        assertEquals("2.3.5", xpath("text()").toString(result).trim());
    }
}
