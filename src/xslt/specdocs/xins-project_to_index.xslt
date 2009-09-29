<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the main index.html which link to the different APIs.

 $Id: xins-project_to_index.xslt,v 1.25 2007/09/10 11:34:48 lexu Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="apis_dir"     />
	<xsl:param name="specsdir"     />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<xsl:include href="header.xslt"       />
	<xsl:include href="footer.xslt"       />
	<xsl:include href="../firstline.xslt" />

	<xsl:template match="project">
		<html>
			<head>
				<title>API index</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="API list" />

				<link rel="stylesheet" href="style.css" type="text/css" />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">apilist</xsl:with-param>
				</xsl:call-template>

				<h1>API index</h1>
				<xsl:apply-templates select="description" />
				<xsl:choose>
					<xsl:when test="api">
						<p>This project defines the following API specifications:</p>
						<table class="apilist">
							<tr>
								<th>API</th>
								<th>Description</th>
								<th>Function count</th>
							</tr>
							<xsl:apply-templates select="api" />
						</table>
					</xsl:when>
					<xsl:otherwise>
						<em>
							<xsl:text>This project does not define any API specifications.</xsl:text>
						</em>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="api">
		<!-- This test is not guaranteed to work with all XSLT
		     processors. -->
		<xsl:variable name="new_api_file" select="concat($apis_dir, '/', @name, '/spec/api.xml')" />
		<xsl:variable name="api_file">
			<xsl:choose>
				<xsl:when test="impl or environments or document($new_api_file)">
					<xsl:value-of select="$new_api_file" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($specsdir, '/', @name, '/api.xml')" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="api_node" select="document($api_file)/api" />
		<xsl:variable name="functioncount" select="count($api_node/function)" />

		<xsl:if test="not($api_node/@name = @name)">
			<xsl:message terminate="yes">
				<xsl:text>API name specified in xins-project.xml ('</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>') does not match the name specified in the api.xml file ('</xsl:text>
				<xsl:value-of select="$api_file" />
				<xsl:text>'), which is: '</xsl:text>
				<xsl:value-of select="$api_node/@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>
		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>/index.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>

			<td>
				<xsl:apply-templates select="$api_node/description" />
			</td>

			<td>
				<xsl:value-of select="$functioncount" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="project/description">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>
  
	<xsl:template match="api/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
