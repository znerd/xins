<?xml version="1.0" encoding="UTF-8" ?>
<!--
 XSLT that generates the unit tests of the function examples.

 $Id: api_to_test.xslt,v 1.19 2007/09/13 14:25:56 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text"/>

	<xsl:param name="package" />

	<xsl:template match="api">
		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;

import java.io.File;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;

import org.xins.common.servlet.container.HTTPServletHandler;

/**
 * Testcase that includes all the tests for the </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ API.
 *
 * @version $]]><![CDATA[Revision$ $]]>Date$
 */
public class APITests extends TestCase {

    /**
     * The Servlet server running the API. The value is &lt;code&gt;null&lt;/code&gt; if the server is not started.
     */
    private static HTTPServletHandler API_SERVER;

    /**
     * Flag that indicates that the API has been started.
     */
    private static boolean API_STARTED = false;

    /**
     * Constructs a new &lt;code&gt;APITests&lt;/code&gt; test suite with
     * the specified name. The name will be passed to the superconstructor.
     *
     * @param name
     *      the name for this test suite.
     */
    public APITests(String name) {
        super(name);
    }

    /**
     * Returns a test suite with all test cases defined by this class.
     *
     * @return
     *     the test suite, never &lt;code&gt;null&lt;/code&gt;.
     */
    public static Test suite() {

        configureLoggerFallback();

        TestSuite suite = new TestSuite();

        if ("true".equals(System.getProperty("test.start.server"))) {
            suite.addTestSuite(StartServer.class);
        }
        // Add all tests</xsl:text>
		<xsl:for-each select="function">
			<xsl:text>
        suite.addTestSuite(</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>Tests.class);</xsl:text>
		</xsl:for-each>
		<xsl:text>
        if ("true".equals(System.getProperty("test.start.server"))) {
            suite.addTestSuite(StopServer.class);
        }

        return suite;
    }

    /**
     * Initializes the logging subsystem with fallback default settings.
     */
    private static final void configureLoggerFallback() {
        Properties settings = new Properties();
        settings.setProperty("log4j.rootLogger",                                "ALL, console");
        settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
        settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
        settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%6c{1} %-6p %x %m%n");
        settings.setProperty("log4j.logger.org.xins.",                          "DEBUG");
        PropertyConfigurator.configure(settings);
    }

    /**
     * Starts the web server.
     */
    public static class StartServer extends TestCase {

        /**
         * Constructs a new &lt;code&gt;StartServer&lt;/code&gt; test suite with
         * the specified name. The name will be passed to the superconstructor.
         *
         * @param name
         *     the name for this test suite.
         */
        public StartServer(String name) {
            super(name);
        }

        /**
         * Returns a test suite with all test cases defined by this class.
         *
         * @return
         *     the test suite, never &lt;code&gt;null&lt;/code&gt;.
         */
        public static Test suite() {
            return new TestSuite(StartServer.class);
        }

        /**
         * Unit test that is only used to start the Servlet API if the 
         * test.start.server build properties is set to true.
         *
         * @throws Exception
         *    if the internal Servlet container cannot be started for any reasons.
         */
        public void testStartServer() throws Exception {

            String warLocation = "build/webapps/</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>.war".replace('/', File.separatorChar);
            File warFile = new File(System.getProperty("user.dir"), warLocation);
            int port = 8080;
            if (System.getProperty("servlet.port") != null &amp;&amp; !System.getProperty("servlet.port").equals("")) {
                port = Integer.parseInt(System.getProperty("servlet.port"));
            }

            // Start the web server
            API_SERVER = new HTTPServletHandler(warFile, port, true);
            API_STARTED = true;
        }
    }

    /**
     * Stops the web server.
     */
    public static class StopServer extends TestCase {

        /**
         * Constructs a new &lt;code&gt;StopServer&lt;/code&gt; test suite with
         * the specified name. The name will be passed to the superconstructor.
         *
         * @param name
         *     the name for this test suite.
         */
        public StopServer(String name) {
            super(name);
        }

        /**
         * Returns a test suite with all test cases defined by this class.
         *
         * @return
         *     the test suite, never &lt;code&gt;null&lt;/code&gt;.
         */
        public static Test suite() {
            return new TestSuite(StopServer.class);
        }

        /**
         * Unit test that is only used to stop the Servlet API if the servlet
         * is started.
         *
         * @throws Exception
         *    if the internal Servlet container cannot be stopped for any reasons.
         */
        public void testStopServer() throws Exception {

            if (API_STARTED) {
                API_SERVER.close();
            }
        }
    }
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
