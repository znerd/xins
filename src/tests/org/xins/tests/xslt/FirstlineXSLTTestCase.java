
package org.xins.tests.xslt;

import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for warning.xslt
 */
public class FirstlineXSLTTestCase extends JuxyTestCase {

    protected void setUp() throws Exception {
        newContext("src/xslt/firstline.xslt");
    }

    public void testEmptyLine() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");
        context().setTemplateParamValue("text", "");

        Node result = callTemplate("firstline");

        assertEquals("", xpath("text()").toString(result).trim());
    }

    public void testSingleLine() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");
        context().setTemplateParamValue("text", "Just one line.");

        Node result = callTemplate("firstline");

        assertEquals("Just one line", xpath("text()").toString(result).trim());
    }

    public void testMultipleLines() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");
        context().setTemplateParamValue("text", "first line. \nSecond line. \n Third line.");

        Node result = callTemplate("firstline");

        assertEquals("first line", xpath("text()").toString(result).trim());
    }
}
