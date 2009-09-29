<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 $Id: log_to_list_html.xslt,v 1.9 2007/01/04 10:17:33 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
							<a href="index.html">Logdoc index</a>
							<xsl:text> | </xsl:text>
							<span class="active">Logdoc entry list</span>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry group</span>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry</span>
						</td>
					</tr>
				</table>

				<h1>Log documentation</h1>

				<h2>Log entry list</h2>
				<xsl:text>The following entries are defined:</xsl:text>
				<table type="entries">
					<tr>
						<th title="The unique identifier of the entry">ID</th>
						<th title="A description of the message entry, in US English">Description</th>
						<th title="The log level for the message, ranging from DEBUG to FATAL">Level</th>
						<th title="Number of parameters the message accepts">Parameters</th>
					</tr>
					<xsl:for-each select="group">
						<xsl:for-each select="entry">
							<xsl:variable name="entry_link" select="concat('entry-', @id, '.html')" />
							<tr>
								<td>
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="$entry_link" />
										</xsl:attribute>
										<xsl:value-of select="@id" />
									</a>
								</td>
								<td>
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="$entry_link" />
										</xsl:attribute>
										<xsl:apply-templates select="description" />
									</a>
								</td>
								<td>
									<xsl:value-of select="@level" />
								</td>
								<td>
									<xsl:value-of select="count(param)" />
								</td>
							</tr>
						</xsl:for-each>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
