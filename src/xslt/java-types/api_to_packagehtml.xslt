<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the package.html used by the Javadoc.

 $Id: api_to_packagehtml.xslt,v 1.12 2007/01/04 10:17:35 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="api" />

	<!-- Output is text/plain -->
	<xsl:output method="html" />

	<!-- ***************************************************************** -->
	<!-- Match the root element: api                                       -->
	<!-- ***************************************************************** -->

	<xsl:template match="api">
		<html>
			<body>
				<xsl:text>Types defined in the </xsl:text>
				<em>
					<xsl:value-of select="$api" />
				</em>
				<xsl:text> API.</xsl:text>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
