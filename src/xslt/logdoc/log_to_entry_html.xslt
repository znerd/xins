<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 $Id: log_to_entry_html.xslt,v 1.40 2007/01/04 10:17:33 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />
	<xsl:param name="sourcedir" />
	<xsl:param name="entry"     />

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
				<title>
					<xsl:text>Log entry </xsl:text>
					<xsl:value-of select="$entry" />
				</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<xsl:apply-templates select="group/entry[@id = $entry]" />

				<h2>Message sets</h2>
				<xsl:choose>
					<xsl:when test="translation-bundle">
						<table>
							<xsl:apply-templates select="translation-bundle">
								<!--
								<xsl:with-param name=""></xsl:with-param>
								-->
							</xsl:apply-templates>
						</table>
					</xsl:when>
					<xsl:otherwise>
						<em>No message sets defined.</em>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="group/entry">
		<xsl:variable name="category" select="concat($package_name, '.', ../@id)" />

		<table class="headerlinks">
			<tr>
				<td>
					<a href="index.html">Logdoc index</a>
					<xsl:text> | </xsl:text>
					<a href="entry-list.html">Logdoc entry list</a>
					<xsl:text> | </xsl:text>
					<a>
						<xsl:attribute name="href">
							<xsl:text>group-</xsl:text>
							<xsl:value-of select="../@id" />
							<xsl:text>.html</xsl:text>
						</xsl:attribute>
						<xsl:text>Log entry group</xsl:text>
					</a>
					<xsl:text> | </xsl:text>
					<span class="active">Log entry</span>
				</td>
			</tr>
		</table>

		<h1>
			<xsl:text>Log entry </xsl:text>
			<xsl:value-of select="$entry" />
		</h1>

		<h2>Details for this entry</h2>
		<table class="entry">
			<tr>
				<th>Description</th>
				<td>
					<xsl:apply-templates select="description" />
				</td>
			</tr>
			<tr>
				<th>Group</th>
				<td>
					<a>
						<xsl:attribute name="href">
							<xsl:text>group-</xsl:text>
							<xsl:value-of select="../@id" />
							<xsl:text>.html</xsl:text>
						</xsl:attribute>
						<xsl:value-of select="../@name" />
					</a>
				</td>
			</tr>
			<tr>
				<th>Log entry ID</th>
				<td>
					<xsl:value-of select="@id" />
				</td>
			</tr>
			<tr>
				<th>Log category</th>
				<td>
					<xsl:value-of select="$category" />
					<xsl:text>.</xsl:text>
					<xsl:value-of select="@id" />
				</td>
			</tr>
			<tr>
				<th>Log level</th>
				<td>
					<xsl:value-of select="@level" />
				</td>
			</tr>
			<tr>
				<th>With exception</th>
				<td>
					<xsl:choose>
						<xsl:when test="@exception = 'true'">Yes</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="(string-length(@exception) = 0) or (@exception = 'false')">No</xsl:when>
					</xsl:choose>
				</td>
			</tr>
		</table>

		<h2>Parameters</h2>
		<xsl:choose>
			<xsl:when test="count(param) &lt; 1">
				<em>No parameters are defined for this entry.</em>
			</xsl:when>
			<xsl:otherwise>
				<table>
					<tr>
						<th>Name</th>
						<th>Type</th>
						<th>Nullable</th>
					</tr>
					<xsl:for-each select="param">
						<xsl:variable name="nullable">
							<xsl:choose>
								<xsl:when test="string-length(@nullable) &lt; 1">true</xsl:when>
								<xsl:when test="@nullable = 'true'">true</xsl:when>
								<xsl:when test="@nullable = 'false'">false</xsl:when>
							</xsl:choose>
						</xsl:variable>
						<tr>
							<td>
								<xsl:value-of select="@name" />
							</td>
							<td>
								<xsl:choose>
									<xsl:when test="(string-length(@type) &lt; 1)">
										<xsl:text>text</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@type" />
									</xsl:otherwise>
								</xsl:choose>
							</td>
							<td>
								<xsl:value-of select="$nullable" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="translation-bundle">
		<xsl:variable name="translation-bundle-doc" select="concat($sourcedir, '/translation-bundle-', @locale, '.xml')" />

		<tr>
			<th>
				<xsl:value-of select="@locale" />
			</th>
			<td>
				<xsl:apply-templates select="document($translation-bundle-doc)/translation-bundle/translation[@entry=$entry]" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="translation/value-of-param[not(@format)]">
		<span class="value-of-param">
			<xsl:value-of select="@name" />
		</span>
	</xsl:template>

	<xsl:template match="translation/value-of-param[@format='quoted']">
		<span class="value-of-param">
			<xsl:text>"</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>"</xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="translation/value-of-param[@format='hex']">
		<span class="value-of-param">
			<xsl:text>hex(</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>)</xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="translation/exception-property[not(@format)]">
		<span class="value-of-param">
			<xsl:text>exception.</xsl:text>
			<xsl:value-of select="@name" />
		</span>
	</xsl:template>

	<xsl:template match="translation/exception-property[@format='quoted']">
		<span class="value-of-param">
			<xsl:text>"</xsl:text>
			<xsl:text>exception.</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>"</xsl:text>
		</span>
	</xsl:template>
</xsl:stylesheet>
