<?xml version="1.0" encoding="US-ASCII"?>
<!--
 Utility XSLT that displays a warning message to the console.

 $Id: warning.xslt,v 1.5 2007/01/04 10:17:30 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="warn">
		<xsl:param name="message" />
		<xsl:message terminate="no">
			<xsl:text>
 *** WARNING: </xsl:text>
			<xsl:value-of select="$message" />
			<xsl:text> ***</xsl:text>
		</xsl:message>
	</xsl:template>
</xsl:stylesheet>
