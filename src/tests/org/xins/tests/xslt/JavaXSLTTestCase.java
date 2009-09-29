
package org.xins.tests.xslt;

import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for java.xslt
 */
public class JavaXSLTTestCase extends JuxyTestCase {

    protected void setUp() throws Exception {
        newContext("src/xslt/java.xslt");
    }

    public void testJavaHeader() throws Exception {
        context().setCurrentNode(xpath("/list"));
        context().setDocument("<list/>");
        Node result = callTemplate("java-header");
        assertEquals("// This is a generated file. Please do not edit.", xpath("text()").toString(result).trim());
    }
}
