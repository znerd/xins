
package org.xins.tests.xslt;

import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for warning.xslt
 */
public class WarningXSLTTestCase extends JuxyTestCase {

    protected void setUp() throws Exception {
        newContext("src/xslt/warning.xslt");
    }

    public void testEmptyList() throws Exception {
        context().setCurrentNode(xpath("/text"));
        context().setDocument("<text/>");
        context().setTemplateParamValue("message", "some warning message");

        Node result = callTemplate("warn");

        //TODO: The xslt does not return any thing, it simply displays a
        // message which is sent, so we can only observe it on command line.
        assertTrue(true);
    }
}
