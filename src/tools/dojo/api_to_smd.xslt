<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates a Maven pom.xml file for an API.

 $Id: api_to_smd.xslt,v 1.3 2007/05/14 11:35:59 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		encoding="UTF-8"
		method="text"
		indent="yes" />

	<xsl:param name="project_home" />
	<xsl:param name="endpoint" select="''" />

	<xsl:template match="api">
		<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />
		<xsl:variable name="project_node" select="document($project_file)/project" />
		<xsl:variable name="apiname" select="@name" />
		<xsl:variable name="location">
			<xsl:choose>
				<xsl:when test="string-length($endpoint) > 0">
					<xsl:value-of select="$endpoint" />
				</xsl:when>
				<xsl:when test="$project_node/api[@name=$apiname]/environments">
					<xsl:variable name="env_file" select="concat($project_home, '/apis/', $apiname, '/environments.xml')" />
					<xsl:value-of select="document($env_file)/environments/environment[1]/@url" />
					<xsl:text>/?_convention=_xins-jsonrpc</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>http://localhost:8080/</xsl:text>
					<xsl:value-of select="$apiname" />
					<xsl:text>/?_convention=_xins-jsonrpc</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>{
	"SMDVersion": ".1",
	"objectName": "</xsl:text>
		<xsl:value-of select="$apiname" />
		<xsl:text>",
	"serviceType": "JSON-RPC",
	"serviceURL": "</xsl:text>
		<xsl:value-of select="$location" />
		<xsl:text>",
	"methods":[</xsl:text>
		<xsl:for-each select="function">
			<xsl:text>
		{
			"name": "</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>",
			"parameters":[</xsl:text>
			<xsl:variable name="function_file" select="concat($project_home, '/apis/', $apiname, '/spec/', @name, '.fnc')" />
			<xsl:variable name="function_node" select="document($function_file)/function" />
			<xsl:for-each select="$function_node/input/param">
				<xsl:text>
				{"name": "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>"}</xsl:text>
				<xsl:if test="position() != last()">
					<xsl:text>,</xsl:text>
				</xsl:if>
			</xsl:for-each>
			<xsl:text>
			]
		}</xsl:text>
			<xsl:if test="position() != last()">
				<xsl:text>,</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<xsl:text>
	]
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
