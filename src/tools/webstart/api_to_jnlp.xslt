<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates a JNLP file for an API.

 $Id: api_to_jnlp.xslt,v 1.3 2007/08/30 12:43:08 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:output omit-xml-declaration="no" encoding="UTF-8" method="xml" indent="yes" />
	
	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="xins_version" />
	
	<xsl:template match="api">
		<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />
		<xsl:variable name="project_node" select="document($project_file)/project" />
		
		<xsl:comment>
			<xsl:text>JNLP file for </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> API.</xsl:text>
		</xsl:comment>
		<xsl:comment>
			<xsl:text>Generated by XINS </xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text>.</xsl:text>
		</xsl:comment>
		<xsl:comment>
			<xsl:text>Note that the WAR file need to be signed.</xsl:text>
		</xsl:comment>
		
		<xsl:variable name="homebase">
			<xsl:if test="$project_node/specdocs">
				<xsl:value-of select="$project_node/specdocs/@href" />
			</xsl:if>
		</xsl:variable>
		<jnlp spec="0.2 1.0" codebase="{$homebase}"	href="{@name}.jnlp">
			<xsl:text>
	</xsl:text>
			<information>
				<xsl:text>
		</xsl:text>
				<title>
					<xsl:value-of select="@name" />
					<xsl:text> API</xsl:text>
				</title>
				<xsl:if test="@owner">
					<xsl:variable name="owner" select="@owner" />
					<xsl:variable name="authors_file" select="concat($project_home, '/authors.xml')" />
					<xsl:variable name="owner_name" select="document($authors_file)/authors/author[@id=$owner]/@name" />
					<xsl:text>
		</xsl:text>
					<vendor>
						<xsl:value-of select="$owner_name" />
					</vendor>
				</xsl:if>
				<xsl:text>
		</xsl:text>
				<description>
					<xsl:value-of select="description/text()" />
				</description>
				<xsl:text>
		</xsl:text>
				<description kind="short">
					<xsl:text>The </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> API.</xsl:text>
				</description>
				<xsl:text>
		</xsl:text>
				<icon href="http://www.xins.org/xins.gif"/>
				<xsl:text>
		</xsl:text>
				<offline-allowed/>
				<xsl:text>
	</xsl:text>
			</information>
				<xsl:text>
	</xsl:text>
			<resources>
				<xsl:text>
		</xsl:text>
				<j2se version="1.4+"/>
				<xsl:text>
		</xsl:text>
				<jar href="{@name}.war"/>
				<xsl:text>
		</xsl:text>
				<property value="true" name="apple.laf.useScreenMenuBar"/>
				<xsl:text>
		</xsl:text>
				<xsl:comment>
					<xsl:text>property name="org.xins.server.config" value="TODO"/</xsl:text>
				</xsl:comment>
				<xsl:text>
	</xsl:text>
			</resources>
				<xsl:text>
	</xsl:text>
			<security>
				<xsl:text>
		</xsl:text>
				<all-permissions />
				<xsl:text>
	</xsl:text>
			</security>
				<xsl:text>
	</xsl:text>
			<application-desc main-class="org.xins.common.servlet.container.HTTPServletStarter">
				<xsl:text>
		</xsl:text>
				<argument>-gui</argument>
				<xsl:text>
	</xsl:text>
			</application-desc>
		</jnlp>
	</xsl:template>
	
</xsl:stylesheet>