<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generated an XML file containing the list of all result codes
 per function.

 $Id: api_to_resultcodes.xslt,v 1.5 2007/03/29 09:03:58 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="specsdir"     />

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="api">

		<xsl:comment>Generated file used to create result code Java files.</xsl:comment>
		<api name="{@name}">
			<xsl:for-each select="function">
				<function name="{@name}">
					<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
					<xsl:variable name="function_node" select="document($function_file)/function" />
					<xsl:for-each select="$function_node/output/resultcode-ref">
						<xsl:choose>
							<xsl:when test="contains(@name, '/')">
								<resultcode name="{substring-after(@name, '/')}" />
							</xsl:when>
							<xsl:otherwise>
								<resultcode name="{@name}" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</function>
			</xsl:for-each>
		</api>

	</xsl:template>

</xsl:stylesheet>
