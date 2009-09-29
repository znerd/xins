<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that gets and display the information about the API author.

 $Id: author.xslt,v 1.12 2007/01/04 10:17:31 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="api" mode="owner">
		<xsl:variable name="owner">
			<xsl:if test="boolean(@owner) and not(owner = '')">
				<xsl:variable name="new_authors_file" select="concat($project_home, '/authors.xml')" />
				<xsl:variable name="authors_file">
					<xsl:choose>
						<xsl:when test="document($new_authors_file)">
							<xsl:value-of select="$new_authors_file" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat($project_home, '/src/authors/authors.xml')" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="document($authors_file)/authors/author[@id=current()/@owner]">
						<xsl:value-of select="@owner" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>Unable to find API owner '</xsl:text>
							<xsl:value-of select="@owner" />
							<xsl:text>' in </xsl:text>
							<xsl:value-of select="$authors_file" />
							<xsl:text>.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:variable>

		<xsl:if test="$owner != ''">
			<xsl:variable name="new_authors_file" select="concat($project_home, '/authors.xml')" />
			<xsl:variable name="authors_file">
				<xsl:choose>
					<xsl:when test="document($new_authors_file)">
						<xsl:value-of select="$new_authors_file" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($project_home, '/src/authors/authors.xml')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="owner_name" select="document($authors_file)/authors/author[@id=$owner]/@name" />
			<xsl:variable name="owner_email" select="document($authors_file)/authors/author[@id=$owner]/@email" />

			<xsl:value-of select="$owner_name" />
			<xsl:text> (&lt;a href="mailto:</xsl:text>
			<xsl:value-of select="$owner_email" />
			<xsl:text>"&gt;</xsl:text>
			<xsl:value-of select="$owner_email" />
			<xsl:text>&lt;/a&gt;)</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
