<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the footer of the specification documentation.

 $Id: footer.xslt,v 1.8 2007/01/22 14:56:38 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Include RCS/CVS parsing utilities -->
	<xsl:include href="../rcs.xslt" />

	<!-- Print the footer -->
	<xsl:template name="footer">

		<!-- Define parameters -->
		<xsl:param name="xins_version" />

		<!-- Determine the version of the concerned object -->
		<xsl:variable name="version">
	    	<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the modification timestamp of the concerned object -->
		<xsl:variable name="timestamp">
			<xsl:choose>
				<xsl:when test="not(@rcsdate) or @rcsdate = concat('$', 'Date$')">
					<xsl:text>?/?/? ?:?:?</xsl:text>
				</xsl:when>
				<xsl:when test="@rcsdate and string-length(@rcsdate) &lt; 20">
					<xsl:message>
						<xsl:text>Unable to parse RCS date. It should be specified in the 'rcsdate' attribute of the '</xsl:text>
						<xsl:value-of select="name()" />
						<xsl:text>' element.</xsl:text>
					</xsl:message>
					<xsl:text>?/?/? ?:?:?</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring(@rcsdate, 8, string-length(@rcsdate) - 9)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="date"        select="substring-before($timestamp, ' ')"                  />
		<xsl:variable name="time"        select="substring-after($timestamp, ' ')"                   />
		<xsl:variable name="date_year"   select="substring-before($date, '/')"                       />
		<xsl:variable name="date_month"  select="substring-before(substring-after($date, '/'), '/')" />
		<xsl:variable name="date_day"    select="substring-after(substring-after($date, '/'), '/')"  />
		<xsl:variable name="time_hour"   select="substring-before($time, ':')"                       />
		<xsl:variable name="time_minute" select="substring-before(substring-after($time, ':'), ':')" />
		<xsl:variable name="time_second" select="substring-after(substring-after($time, ':'), ':')"  />

		<!-- Check preconditions -->
		<xsl:if test="not(string-length($xins_version) &gt; 0)">
			<xsl:message terminate="yes">The mandatory parameter 'xins_version' is not set.</xsl:message>
		</xsl:if>

		<div class="footer">
			<xsl:attribute name="title">
				<xsl:text>Version </xsl:text>
				<xsl:value-of select="$version" />
				<xsl:text> (</xsl:text>
				<xsl:value-of select="$date_year" />
				<xsl:text>.</xsl:text>
				<xsl:value-of select="$date_month" />
				<xsl:text>.</xsl:text>
				<xsl:value-of select="$date_day" />
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$time_hour" />
				<xsl:text>:</xsl:text>
				<xsl:value-of select="$time_minute" />
				<xsl:text>)</xsl:text>
			</xsl:attribute>
			<xsl:if test="@rcsversion">
				<xsl:text>Version </xsl:text>
				<xsl:value-of select="$version" />
				<xsl:text>.</xsl:text>
			</xsl:if>
			<xsl:text>Generated using </xsl:text>
			<a href="http://www.xins.org/">XINS</a>
			<xsl:text> </xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text>.</xsl:text>
		</div>
	</xsl:template>
</xsl:stylesheet>
