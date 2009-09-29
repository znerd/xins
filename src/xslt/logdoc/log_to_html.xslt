<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 $Id: log_to_html.xslt,v 1.35 2007/01/04 10:17:34 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />

	<!-- Define variables -->

	<!-- Configure output method -->
	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:template match="log">
		<html>
			<head>
				<title>Log documentation</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<table class="headerlinks">
					<tr>
						<td>
							<span class="active">Logdoc index</span>
							<xsl:text> | </xsl:text>
							<a href="entry-list.html">Logdoc entry list</a>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry group</span>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry</span>
						</td>
					</tr>
				</table>

				<h1>Log documentation</h1>

				<h2>Log entry groups</h2>
				<xsl:text>The following groups of log entries are defined:</xsl:text>
				<table type="groups">
					<tr>
						<th title="The name of the group">Name</th>
						<th title="The logging category for the group">Category</th>
						<th title="The number of log entries in this group">Entries</th>
					</tr>
					<xsl:for-each select="group[entry]">
						<xsl:variable name="category" select="concat($package_name, '.', @id)" />
						<xsl:variable name="group_link" select="concat('group-', @id, '.html')" />
						<tr>
							<td>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$group_link" />
									</xsl:attribute>
									<xsl:value-of select="@name" />
								</a>
							</td>
							<td>
								<xsl:value-of select="$category" />
							</td>
							<td>
								<xsl:value-of select="count(entry)" />
							</td>
						</tr>
					</xsl:for-each>
				</table>

				<h2>Translation bundles</h2>
				<p>The following translation bundles are available:</p>
				<ul>
					<xsl:for-each select="translation-bundle">
						<li>
							<xsl:value-of select="@locale" />
						</li>
					</xsl:for-each>
				</ul>

				<h2>Log levels</h2>
				<p>The following log levels can be used:</p>
				<table class="loglevels">
					<tr>
						<th title="Name of the log level">ID</th>
						<th title="Description of the log level">Description</th>
					</tr>
					<tr>
						<td>DEBUG</td>
						<td>Debugging messages. Only useful for programmers. This is the only level that may contain implementation details that are not exposed outside individual functions.</td>
					</tr>
					<tr>
						<td>INFO</td>
						<td>Informational messages. Typically not important to operational people, except in cases where a problem is being traced or if behaviour is investigated.</td>
					</tr>
					<tr>
						<td>NOTICE</td>
						<td>Informational messages that should typically be noticed by operational people.</td>
					</tr>
					<tr>
						<td>WARNING</td>
						<td>Warning messages. Should be noticed, but typically require no immediate action, although they may indicate a problem that should be fixed.</td>
					</tr>
					<tr>
						<td>ERROR</td>
						<td>Error messages. Indicates an error that should be fixed. However, it does not keep the whole application from functioning.</td>
					</tr>
					<tr>
						<td>FATAL</td>
						<td>Fatal error messages. Indicates an error that keeps the whole application from functioning.</td>
					</tr>
				</table>

				<p>Note that it is a fatal condition to start with an unexpected condition of any crucial aspect of the application. If the configuration of all crucial aspects of the application could not be correctly and fully processed it is effectively in an unexpected state. Falling back to defaults does not imply returning to an expected condition because clearly some specific configuration was intended and expected.</p>
				<p>This rule should be applied to determine whether a message should be logged at the FATAL level or not.</p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
