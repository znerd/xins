<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an Eclipse .tomcatplugin file for an API.

 $Id: api_to_tomcatplugin.xslt,v 1.3 2007/05/03 14:59:33 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		omit-xml-declaration="no"
		encoding="UTF-8"
		method="xml"
		indent="yes" />

	<!-- Define parameters -->
	<xsl:param name="project_home" />

	<xsl:template match="api">

		<tomcatProjectProperties>
			<rootDir>/</rootDir>
			<exportSource>false</exportSource>
			<reloadable>true</reloadable>
			<redirectLogger>false</redirectLogger>
			<updateXml>true</updateXml>
			<warLocation>
				<xsl:value-of select="concat($project_home, '/build/webapps/', @name, '/', @name, '.war')" />
			</warLocation>
			<extraInfo></extraInfo>
			<webPath></webPath>
		</tomcatProjectProperties>
	</xsl:template>

</xsl:stylesheet>
