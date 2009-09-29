<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the runtimeProperties.html files that contains
 the list and description of the properties used by the implementation of
 the API.

 $Id: impl_to_html.xslt,v 1.10 2007/07/06 13:43:18 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="xins_version" />
	<xsl:param name="api"          />

	<!-- Perform includes -->
	<xsl:include href="output_section.xslt" />
	<xsl:include href="header.xslt"         />
	<xsl:include href="footer.xslt"         />
	<xsl:include href="../types.xslt"       />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<!-- Default indentation setting -->
	<xsl:variable name="indentation" select="'&amp;nbsp;&amp;nbsp;&amp;nbsp;'" />

	<xsl:template match="impl">

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="$api" />
					<xsl:text> properties.</xsl:text>
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Specification of the runtime properties defined for the {$api} API." />

				<link rel="stylesheet" type="text/css" href="style.css"                               />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">properties</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Runtime properties of </xsl:text>
					<em>
						<xsl:value-of select="$api" />
					</em>.
				</h1>

				<blockquote>
					<xsl:choose>
						<xsl:when test="runtime-properties/property">
							<xsl:apply-templates select="runtime-properties" mode="table" />
						</xsl:when>
						<xsl:otherwise>
							<em>This API implementation does not have any properties defined.</em>
						</xsl:otherwise>
					</xsl:choose>
				</blockquote>
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="runtime-properties" mode="table">
		<xsl:call-template name="parametertable">
			<xsl:with-param name="title"></xsl:with-param>
			<xsl:with-param name="content">property</xsl:with-param>
			<xsl:with-param name="class">inputparameters</xsl:with-param>
			<xsl:with-param name="type">Property</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
