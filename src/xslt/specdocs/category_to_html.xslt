<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the categories files that contain
 the input description, the output description and the examples.

 $Id: category_to_html.xslt,v 1.12 2007/07/06 13:43:18 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />

	<!-- Perform includes -->
	<xsl:include href="header.xslt"       />
	<xsl:include href="footer.xslt"       />
	<xsl:include href="../firstline.xslt" />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<!-- Default indentation setting -->
	<xsl:variable name="indentation" select="'&amp;nbsp;&amp;nbsp;&amp;nbsp;'" />

	<xsl:template match="category">

		<xsl:variable name="category_name" select="@name" />
		<xsl:variable name="category_file" select="concat($specsdir, '/', $category_name, '.cat')" />

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Specification of the {$category_name} category of the {$api} API." />

				<link rel="stylesheet" type="text/css" href="style.css"                                  />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">category</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Category </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
				</h1>

				<!-- Description -->
				<xsl:call-template name="description" />

		      <!-- <xsl:for-each select="function"> -->
				<h2>Functions in this category:</h2>

				<xsl:choose>
					<xsl:when test="function-ref">
						<table class="functionlist">
						<tr>
							<th>Function</th>
							<th>Version</th>
							<th>Status</th>
							<th>Description</th>
						</tr>
						<xsl:apply-templates select="function-ref" />
						</table>
					</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>The category "</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>" should at least have one function defined.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="function-ref">
		<xsl:variable name="function_name" select="@name" />
		<xsl:variable name="function_file" select="concat($specsdir, '/', $function_name, '.fnc')" />
		<xsl:variable name="function_node" select="document($function_file)/function" />
		<xsl:variable name="api_file"      select="concat($specsdir, '/api.xml')" />
		<xsl:variable name="api_node"      select="document($api_file)/api/function[@name=$function_name]" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="$function_node/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not($function_node)">
			<xsl:message terminate="yes">
				<xsl:text>Function file '</xsl:text>
				<xsl:value-of select="$function_file" />
				<xsl:text>' not found for the defined function '</xsl:text>
				<xsl:value-of select="$function_name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="not($api_node)">
			<xsl:message terminate="yes">
				<xsl:text>Function '</xsl:text>
				<xsl:value-of select="$function_name" />
				<xsl:text>' not found in '</xsl:text>
				<xsl:value-of select="$api_file" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$function_name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="$api_node/@freeze = $version">Frozen</xsl:when>
					<xsl:when test="$api_node/@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="$api_node/@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="$function_node/deprecated">
						<span class="broken_freeze" title="{$function_node/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="$function_node/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="function/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="description">
		<xsl:if test="description">
			<xsl:apply-templates select="description" />
			<xsl:if test="deprecated">
				<br />
			</xsl:if>
		</xsl:if>
		<xsl:if test="deprecated">
			<em>
				<strong>Deprecated: </strong>
				<xsl:apply-templates select="deprecated" />
			</em>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
