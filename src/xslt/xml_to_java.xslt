<?xml version="1.0" encoding="US-ASCII" ?>
<!--
  Utility XSLT that provide a template that return a string with the
 " and \ character escaped.

$Id: xml_to_java.xslt,v 1.14 2006/04/06 09:35:06 agoubard Exp $
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="xml_to_java_string">
		<xsl:param name="text" />

		<xsl:variable name="firstchar" select="substring($text, 1, 1)" />
		<xsl:variable name="rest" select="substring($text, 2)" />

		<xsl:choose>
			<xsl:when test="string-length($text) &lt; 1" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$firstchar='\'">\\</xsl:when>
					<xsl:when test="$firstchar='&quot;'">\"</xsl:when>
					<xsl:when test="$firstchar='&#xDF;'">\u00DF</xsl:when>
					<xsl:when test="$firstchar='&#xE0;'">\u00E0</xsl:when>
					<xsl:when test="$firstchar='&#xE6;'">\u00E6</xsl:when>
					<xsl:when test="$firstchar='&#xE7;'">\u00E7</xsl:when>
					<xsl:when test="$firstchar='&#xE8;'">\u00E8</xsl:when>
					<xsl:when test="$firstchar='&#xE9;'">\u00E9</xsl:when>
					<xsl:when test="$firstchar='&#xEA;'">\u00EA</xsl:when>
					<xsl:when test="$firstchar='&#xEB;'">\u00EB</xsl:when>
					<xsl:when test="$firstchar='&#xEC;'">\u00EC</xsl:when>
					<xsl:when test="$firstchar='&#xEF;'">\u00EF</xsl:when>
					<xsl:when test="$firstchar='&#xF3;'">\u00F3</xsl:when>
					<xsl:when test="$firstchar='&#xF4;'">\u00F4</xsl:when>
					<xsl:when test="$firstchar='&#xF9;'">\u00F9</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text" select="$rest" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Normalize the text. This means that consecutive spaces (including tabs,
	     \r and \n) are merged to one space.
	     On the contrary to the normalize() method this template does not
	     troncate the leading and trailing spaces.
	-->
	<xsl:template name="normalize">
		<xsl:param name="text" />
		<xsl:param name="previouschar" />

		<xsl:variable name="firstchar" select="substring($text, 1, 1)" />

		<xsl:variable name="rest" select="substring($text, 2)" />

		<xsl:choose>
			<xsl:when test="string-length($text) &lt; 1" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="($previouschar=' ' or $previouschar='&#xA;' or $previouschar='&#x9;' or $previouschar='&#xD;') and ($firstchar=' ' or $firstchar='&#xA;' or $firstchar='&#x9;' or $firstchar='&#xD;')" />
					<xsl:when test="$firstchar=' ' or $firstchar='&#xA;' or $firstchar='&#x9;' or $firstchar='&#xD;'">
						<xsl:text> </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="normalize">
					<xsl:with-param name="text" select="$rest" />
					<xsl:with-param name="previouschar" select="$firstchar" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Transform a PCDATA text to a Java string. -->
	<xsl:template name="pcdata_to_java_string">
		<xsl:param name="text" />

		<xsl:variable name="normalized-text">
			<xsl:call-template name="normalize">
				<xsl:with-param name="text" select="$text" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:call-template name="xml_to_java_string">
			<xsl:with-param name="text" select="$normalized-text" />
		</xsl:call-template>
	</xsl:template>

	<!-- Transform the XML to HTML for the Javadoc -->
	<xsl:template name="xml_to_html_string">
		<xsl:param name="text" />

		<xsl:variable name="firstchar" select="substring($text, 1, 1)" />
		<xsl:variable name="rest" select="substring($text, 2)" />

		<xsl:choose>
			<xsl:when test="string-length($text) &lt; 1" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$firstchar='&#xDF;'">&amp;#xDF;</xsl:when>
					<xsl:when test="$firstchar='&#xE0;'">&amp;#xE0;</xsl:when>
					<xsl:when test="$firstchar='&#xE6;'">&amp;#xE6;</xsl:when>
					<xsl:when test="$firstchar='&#xE7;'">&amp;#xE7;</xsl:when>
					<xsl:when test="$firstchar='&#xE8;'">&amp;#xE8;</xsl:when>
					<xsl:when test="$firstchar='&#xE9;'">&amp;#xE9;</xsl:when>
					<xsl:when test="$firstchar='&#xEA;'">&amp;#xEA;</xsl:when>
					<xsl:when test="$firstchar='&#xEB;'">&amp;#xEB;</xsl:when>
					<xsl:when test="$firstchar='&#xEC;'">&amp;#xEC;</xsl:when>
					<xsl:when test="$firstchar='&#xEF;'">&amp;#xEF;</xsl:when>
					<xsl:when test="$firstchar='&#xF3;'">&amp;#xF3;</xsl:when>
					<xsl:when test="$firstchar='&#xF4;'">&amp;#xF4;</xsl:when>
					<xsl:when test="$firstchar='&#xF9;'">&amp;#xF9;</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="xml_to_html_string">
					<xsl:with-param name="text" select="$rest" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
