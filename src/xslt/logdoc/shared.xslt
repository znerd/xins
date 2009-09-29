<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 $Id: shared.xslt,v 1.20 2007/01/25 15:37:39 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="param" mode="method-argument">
		<xsl:param name="exception" select="false()" />

		<xsl:variable name="nullable" select="not(@nullable) or @nullable = 'true'" />
		<xsl:if test="starts-with(@name, '_')">
			<xsl:message terminate="yes">
				<xsl:text>Parameter name "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>" starts with underscore.</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:if test="$exception or (position() &gt; 1)">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="(@type = 'text') or (string-length(@type) &lt; 1)">
				<xsl:text>String</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'serializable'">
				<xsl:text>org.xins.logdoc.LogdocSerializable</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'object'">
				<xsl:text>Object</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'float64') and $nullable">
				<xsl:text>Double</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'float64'">
				<xsl:text>double</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'float32') and $nullable">
				<xsl:text>Float</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'float32'">
				<xsl:text>float</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int64') and $nullable">
				<xsl:text>Long</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int64'">
				<xsl:text>long</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int32') and $nullable">
				<xsl:text>Integer</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int32'">
				<xsl:text>int</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int16') and $nullable">
				<xsl:text>Short</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int16'">
				<xsl:text>short</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'int8') and $nullable">
				<xsl:text>Byte</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'int8'">
				<xsl:text>byte</xsl:text>
			</xsl:when>
			<xsl:when test="(@type = 'boolean') and $nullable">
				<xsl:text>Boolean</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'boolean'">
				<xsl:text>boolean</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>The type '</xsl:text>
					<xsl:value-of select="@type" />
					<xsl:text>' is for parameter named '</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>' is	unknown.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
	</xsl:template>
</xsl:stylesheet>
