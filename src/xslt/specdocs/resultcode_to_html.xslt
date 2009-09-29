<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the resultcode.html files which contains
 the description of the result code.

 $Id: resultcode_to_html.xslt,v 1.26 2007/07/06 13:43:17 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node" select="document($api_file)/api" />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<xsl:include href="broken_freeze.xslt"  />
	<xsl:include href="output_section.xslt" />
	<xsl:include href="header.xslt"         />
	<xsl:include href="footer.xslt"         />
	<xsl:include href="../types.xslt"       />
	<xsl:include href="../urlencode.xslt"   />
	<xsl:include href="../warning.xslt"     />

	<xsl:template match="resultcode">

		<xsl:variable name="resultcode_name" select="@name" />

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>Result code </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Specification of the {@name} error code of the {$api} API." />

				<link rel="stylesheet" type="text/css" href="style.css"                               />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">resultcode</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Result code </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
				</h1>

				<!-- Broken freezes >
				<xsl:variable name="broken_file" select="concat($resultcode_name,'.rcd')" />
				<xsl:call-template name="broken_freeze">
					<xsl:with-param name="project_home" select="$project_home" />
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="api" select="$api" />
					<xsl:with-param name="frozen_version" select="$api_node/resultcode[@name=$resultcode_name]/@freeze" />
					<xsl:with-param name="broken_file" select="$broken_file" />
				</xsl:call-template-->

				<!-- Description -->
				<xsl:apply-templates select="description" />

				<xsl:call-template name="output_section" />

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="output_section">
		<xsl:if test="output">
			<h2>Output section</h2>
			<blockquote>
				<xsl:if test="output">
					<xsl:apply-templates select="output" />
				</xsl:if>
			</blockquote>
		</xsl:if>
	</xsl:template>

	<xsl:template match="resultcode/output">
		<xsl:call-template name="parametertable">
			<xsl:with-param name="title">Output parameters</xsl:with-param>
			<xsl:with-param name="content">output parameters</xsl:with-param>
			<xsl:with-param name="class">outputparameters</xsl:with-param>
		</xsl:call-template>

		<xsl:call-template name="datasection" />
	</xsl:template>

	<xsl:template name="datasection">
		<xsl:choose>
			<xsl:when test="data/contains">
			<h3>Data section</h3>
				<p>Root element(s):
					<ul>
						<xsl:for-each select="data/contains/contained">
							<li><code>
								<xsl:text>&lt;</xsl:text>
								<xsl:value-of select="@element" />
								<xsl:text>/&gt;</xsl:text>
							</code>.</li>
						</xsl:for-each>
					</ul>
				</p>
				<xsl:apply-templates select="data/element" />
			</xsl:when>
			<xsl:when test="data/@contains">
				<h3>Data section</h3>
					<p>
						<xsl:text>Root element: </xsl:text>
						<code>
							<xsl:text>&lt;</xsl:text>
							<xsl:value-of select="data/@contains" />
							<xsl:text>/&gt;</xsl:text>
						</code>
						<xsl:text>.</xsl:text>
					</p>
					<xsl:apply-templates select="data/element" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
