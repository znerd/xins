<?xml version="1.0" encoding="UTF-8" ?>
<!--
 XSLT that transforms the combination of the XML request and the result
 returned from a XINS API to the specification of an example.

 $Id: create_example.xslt,v 1.5 2007/01/04 10:17:30 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />

	<xsl:strip-space elements="*" />

	<xsl:variable name="indentation" select="'&#x9;'" />

	<xsl:template match="/combined">
		<xsl:text>	</xsl:text>
		<example>
			<xsl:if test="result/@errorcode">
				<xsl:attribute name="resultcode">
					<xsl:value-of select="result/@errorcode" />
				</xsl:attribute>
			</xsl:if>
			<xsl:text>
		</xsl:text>
			<description>TODO</description>
			<xsl:apply-templates select="request/*" />
			<xsl:apply-templates select="result/*" />
		<xsl:text>
	</xsl:text>
		</example>
	</xsl:template>

	<!--
	  You could also use this template just to transform an XML result to an example.
	-->
	<xsl:template match="/result">
			<xsl:text>
	</xsl:text>
		<example>
			<xsl:if test="@errorcode">
				<xsl:attribute name="resultcode">
					<xsl:value-of select="@errorcode" />
				</xsl:attribute>
			</xsl:if>
			<xsl:text>
		</xsl:text>
			<description>TODO</description>
			<xsl:apply-templates select="/result/*" />
		<xsl:text>
	</xsl:text>
		</example>
	</xsl:template>

	<xsl:template match="request/param">
		<xsl:text>
		</xsl:text>
		<input-example name="{@name}">
			<xsl:value-of select="text()" />
		</input-example>
	</xsl:template>

	<xsl:template match="result/param">
		<xsl:text>
		</xsl:text>
		<output-example name="{@name}">
			<xsl:value-of select="text()" />
		</output-example>
	</xsl:template>

	<xsl:template match="request/data">
			<xsl:text>
		</xsl:text>
		<input-data-example>
			<xsl:apply-templates />
		<xsl:text>
		</xsl:text>
		</input-data-example>
	</xsl:template>

	<xsl:template match="result/data">
			<xsl:text>
		</xsl:text>
		<output-data-example>
			<xsl:apply-templates />
		<xsl:text>
		</xsl:text>
		</output-data-example>
	</xsl:template>

	<xsl:template match="*">
		<xsl:variable name="indent">
			<xsl:call-template name="indent">
				<xsl:with-param name="count" select="count(ancestor::*)" />
				<xsl:with-param name="indent" select="$indentation" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text>
</xsl:text>
		<xsl:value-of select="$indent" />
		<element-example name="{name()}">
			<xsl:apply-templates select="@*">
				<xsl:with-param name="indent" select="$indent" />
			</xsl:apply-templates>
			<xsl:apply-templates />
			<xsl:text>
</xsl:text>
			<xsl:value-of select="$indent" />
		</element-example>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:param name="indent" />

		<xsl:text>
</xsl:text>
		<xsl:value-of select="concat($indent, $indentation)" />
		<attribute-example name="{name()}">
			<xsl:value-of select="." />
		</attribute-example>
	</xsl:template>

	<xsl:template name="indent">
		<xsl:param name="count" />
		<xsl:param name="indent" />
		<xsl:choose>
			<xsl:when test="$count = 1">
				<xsl:value-of select="$indent" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="indent">
					<xsl:with-param name="count" select="$count - 1" />
					<xsl:with-param name="indent" select="concat($indent, $indentation)" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
