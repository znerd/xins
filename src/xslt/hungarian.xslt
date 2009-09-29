<?xml version="1.0" encoding="US-ASCII"?>
<!--
 Utility XSLT that converts a word to the hungarian notation.

 $Id: hungarian.xslt,v 1.17 2007/03/19 14:32:35 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!--
	- Transform a property name in a hungarian-formatted string starting with an
	- uppercase.
	-->
	<xsl:template name="hungarianUpper">
		<xsl:param name="text" />
		<xsl:param name="startWithUpperCase" select="true()" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />

		<xsl:if test="string-length($firstChar) &gt; 0">
			<xsl:variable name="skipChar" select="$firstChar = '.' or $firstChar = '-'" />
			<xsl:if test="not($skipChar)">
				<xsl:choose>
					<xsl:when test="$startWithUpperCase">
						<xsl:value-of select="translate($firstChar,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstChar" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:variable name="rest" select="substring($text, 2)" />
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text"               select="$rest"  />
				<xsl:with-param name="startWithUpperCase" select="$skipChar" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
	- Transform a property name in a hungarian-formatted string starting with an
	- lowercase.
	-->
	<xsl:template name="hungarianLower">
		<xsl:param name="text" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />
		<xsl:variable name="rest" select="substring($text, 2)" />

		<xsl:value-of select="translate($firstChar,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text"               select="$rest"  />
			<xsl:with-param name="startWithUpperCase" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!--
	- Transform a property name in a hungarian-formatted string starting with an
	- lowercase and with the floowing rules:
	- If the word is in uppercase, it creates a all lowercase word.
	- If the word starts with several uppercases, it sets them expect the last one to lower (e.g. HTTPInfo -> httpInfo)
	-->
	<xsl:template name="smartHungarianLower">
		<xsl:param name="text" />

		<xsl:variable name="upperText" select="translate($text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
		<xsl:choose>
			<xsl:when test="$upperText = $text">
				<xsl:value-of select="translate($text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
			</xsl:when>
			<xsl:when test="string-length($text) &lt; 3">">
				<xsl:call-template name="hungarianLower">
					<xsl:with-param name="text" select="$text" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="firstChar" select="substring($text, 1, 1)" />
				<xsl:variable name="rest" select="substring($text, 2)" />
				<xsl:variable name="secondChar" select="substring($text, 2, 1)" />
				<xsl:variable name="lowerSecondChar" select="translate($secondChar,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
				<xsl:variable name="thirdChar" select="substring($text, 3, 1)" />
				<xsl:variable name="lowerThirdChar" select="translate($thirdChar,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
				<xsl:choose>
					<xsl:when test="$secondChar != $lowerSecondChar and $thirdChar != $lowerThirdChar">
						<xsl:value-of select="translate($firstChar,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
						<xsl:call-template name="smartHungarianLower">
							<xsl:with-param name="text" select="$rest" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="hungarianLower">
							<xsl:with-param name="text" select="$text" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
	- Splits a hungarian-formatted string into words using the specified
	- separator.
	-->
	<xsl:template name="hungarianWordSplit">
		<xsl:param name="text" />
		<xsl:param name="separator" select="' '" />
		<xsl:param name="lastWasLowercase" select="false()" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />

		<xsl:if test="string-length($firstChar) &gt; 0">
			<xsl:variable name="rest" select="substring($text, 2)" />
			<xsl:variable name="isLowercase" select="string-length(translate($firstChar, 'abcdefghijklmnopqrstuvwxyz', '')) = 0" />

			<!-- Print a separator if the previous character was lowercase and
		     	this one is uppercase -->
			<xsl:if test="$lastWasLowercase and not($isLowercase)">
				<xsl:value-of select="$separator" />
			</xsl:if>

			<xsl:value-of select="$firstChar" />

			<xsl:call-template name="hungarianWordSplit">
				<xsl:with-param name="text"             select="$rest"        />
				<xsl:with-param name="separator"        select="$separator"   />
				<xsl:with-param name="lastWasLowercase" select="$isLowercase" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
