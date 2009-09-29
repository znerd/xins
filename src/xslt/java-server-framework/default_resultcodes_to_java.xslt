<?xml version="1.0" encoding="US-ASCII"?>
<!--
 $Id: default_resultcodes_to_java.xslt,v 1.21 2007/03/29 09:03:58 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />

	<xsl:template match="resultcodes">
		<xsl:call-template name="java-header" />
		<xsl:text>package org.xins.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants for the default result codes.
 *
 * @version XML file (</xsl:text>
		<xsl:value-of select="@rcsversion" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@rcsdate" />
		<!-- TODO: Pass down version of .rcd file -->
		<xsl:text><![CDATA[); XSLT file ($Revision: 1.21 $ $Date: 2007/03/29 09:03:58 $)
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public interface DefaultResultCodes {]]></xsl:text>
		<xsl:for-each select="code">
			<xsl:text><![CDATA[
   /**
    * Constant for the <em>]]></xsl:text>
			<xsl:choose>
				<xsl:when test="@value">
					<xsl:value-of select="@value" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@name" />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[</em> result code.
    * The description for this result code is:
    *
    * <blockquote>"]]></xsl:text>
			<xsl:apply-templates select="description" />
			<xsl:text><![CDATA["</blockquote>
    */
   final ResultCode ]]></xsl:text>
			<xsl:call-template name="toupper">
				<xsl:with-param name="text">
					<xsl:call-template name="hungarianWordSplit">
						<xsl:with-param name="text" select="@name" />
						<xsl:with-param name="separator" select="'_'" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text> = new ResultCode("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>");
</xsl:text>
		</xsl:for-each>
		<xsl:text>
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
