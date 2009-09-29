<?xml version="1.0" encoding="UTF-8" ?>
<!--
 XSLT that generates the index.html of the specification documentation.

 $Id: api_to_content.xslt,v 1.9 2007/07/12 09:48:59 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" office:version="1.0">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />

	<xsl:output
	method="xml"
	indent="yes"
	encoding="UTF-8" />

	<xsl:include href="../types.xslt" />
	<xsl:include href="../urlencode.xslt"   />
	<xsl:include href="function_to_content.xslt" />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="api">

		<office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" office:version="1.0">
			<xsl:call-template name="automatic-styles" />
			<office:body>
				<office:text>
					<xsl:apply-templates select="." mode="title" />
					<!-- Overview chapter -->
					<text:h text:style-name="Heading1">Overview</text:h>
					<text:h text:style-name="Heading2">Description</text:h>
					<text:p text:style-name="P1">
						<xsl:apply-templates select="description" />
					</text:p>
					<xsl:if test="category">
						<xsl:apply-templates select="." mode="functions-table">
							<xsl:with-param name="type-name" select="'Category'" />
							<xsl:with-param name="type-node" select="category" />
							<xsl:with-param name="extension" select="'cat'" />
						</xsl:apply-templates>
					</xsl:if>
					<xsl:apply-templates select="." mode="functions-table">
						<xsl:with-param name="type-name" select="'Function'" />
						<xsl:with-param name="type-node" select="function" />
						<xsl:with-param name="extension" select="'fnc'" />
					</xsl:apply-templates>
					<xsl:apply-templates select="function" mode="function-chapter">
						<xsl:with-param name="project_home" select="$project_home" />
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="specsdir"     select="$specsdir" />
						<xsl:with-param name="api"          select="$api" />
					</xsl:apply-templates>
				</office:text>
			</office:body>
		</office:document-content>
	</xsl:template>

	<xsl:template match="api" mode="title">
		<text:p text:style-name="Title">
			<xsl:value-of select="@name" />
			<xsl:text> API</xsl:text>
		</text:p>
		<text:p text:style-name="Subtitle">- Technical Guideline -</text:p>
	</xsl:template>

	<xsl:template match="api" mode="functions-table">
		<xsl:param name="type-node" />
		<xsl:param name="type-name" />
		<xsl:param name="extension" />

		<text:h text:style-name="Heading2">
			<xsl:choose>
				<xsl:when test="count($type-node) &gt; 1 and $type-name = 'Category'">
					<xsl:text>Categories</xsl:text>
				</xsl:when>
				<xsl:when test="count($type-node) &gt; 1 and not($type-name = 'Category')">
					<xsl:value-of select="$type-name" />
					<xsl:text>s</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$type-name" />
				</xsl:otherwise>
			</xsl:choose>
		</text:h>
		<xsl:choose>
			<xsl:when test="count($type-node) = 0">
				<text:p text:style-name="Note">
					<xsl:text>This API defines no </xsl:text>
					<xsl:value-of select="$type-name" />
					<xsl:text>.</xsl:text>
				</text:p>
			</xsl:when>
			<xsl:otherwise>
				<table:table table:name="FunctionsTable" table:style-name="FunctionsTable">
					<table:table-column table:style-name="FunctionsTable.A"/>
					<table:table-column table:style-name="FunctionsTable.B"/>
					<table:table-row>
						<table:table-cell table:style-name="FunctionsTable.A1" office:value-type="string">
							<text:p text:style-name="P2">
								<xsl:value-of select="$type-name" />
							</text:p>
						</table:table-cell>
						<table:table-cell table:style-name="FunctionsTable.A1" office:value-type="string">
							<text:p text:style-name="P2">Description</text:p>
						</table:table-cell>
					</table:table-row>
					<xsl:apply-templates select="$type-node" mode="functions-table">
						<xsl:with-param name="type-node" select="$type-node" />
						<xsl:with-param name="type-name" select="$type-name" />
						<xsl:with-param name="extension" select="$extension" />
					</xsl:apply-templates>
				</table:table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="function | type | resultcode | category" mode="functions-table">
		<xsl:param name="type-node" />
		<xsl:param name="type-name" />
		<xsl:param name="extension" />

		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.', $extension)" />
		<xsl:variable name="function_node" select="document($function_file)/node()" />

		<xsl:if test="not($function_node)">
			<xsl:message terminate="yes">
				<xsl:text>Function file '</xsl:text>
				<xsl:value-of select="$function_file" />
				<xsl:text>' not found for the defined function '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<table:table-row>
			<table:table-cell table:style-name="FunctionsTable.A2" office:value-type="string">
				<text:p text:style-name="FunctionName">
					<xsl:value-of select="@name" />
				</text:p>
			</table:table-cell>
			<table:table-cell table:style-name="FunctionsTable.B2" office:value-type="string">
				<text:p text:style-name="P1">
					<xsl:apply-templates select="$function_node/description" mode="firstline" />
				</text:p>
			</table:table-cell>
		</table:table-row>

	</xsl:template>

	<xsl:template match="function/description | type/description | resultcode/description | category/description" mode="firstline">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="function/description | type/description | resultcode/description | category/description">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="em">
		<text:span text:style-name="Em">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<!-- The automatic styles need to be in the content.xml, if set in styles.xml it doesn't work. -->
	<xsl:template name="automatic-styles">
		<office:automatic-styles>
			<style:style style:name="FunctionsTable" style:family="table">
				<style:table-properties style:width="6in" table:align="margins" style:writing-mode="lr-tb"/>
			</style:style>
			<style:style style:name="FunctionsTable.A" style:family="table-column">
				<style:table-column-properties style:column-width="2in" style:rel-column-width="2159*"/>
			</style:style>
			<style:style style:name="FunctionsTable.B" style:family="table-column">
				<style:table-column-properties style:column-width="4in" style:rel-column-width="4601*"/>
			</style:style>
			<style:style style:name="FunctionsTable.A1" style:family="table-cell">
				<style:table-cell-properties fo:background-color="#e6e6ff" fo:border-left="0.0069in solid #e6e6e6" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="FunctionsTable.B1" style:family="table-cell">
				<style:table-cell-properties fo:background-color="#e6e6ff" fo:border-left="none" fo:border-right="0.0069in solid #e6e6e6" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="FunctionsTable.A2" style:family="table-cell">
				<style:table-cell-properties fo:border-left="0.0069in solid #e6e6e6" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="FunctionsTable.B2" style:family="table-cell">
				<style:table-cell-properties fo:border-left="none" fo:border-right="0.0069in solid #e6e6e6" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable" style:family="table">
				<style:table-properties style:width="7in" table:align="margins" style:writing-mode="lr-tb"/>
			</style:style>
			<style:style style:name="ParametersTable.A" style:family="table-column">
				<style:table-column-properties style:column-width="1.5in" style:rel-column-width="2159*"/>
			</style:style>
			<style:style style:name="ParametersTable.B" style:family="table-column">
				<style:table-column-properties style:column-width="1.5in" style:rel-column-width="2947*"/>
			</style:style>
			<style:style style:name="ParametersTable.C" style:family="table-column">
				<style:table-column-properties style:column-width="2.5in" style:rel-column-width="3930*"/>
			</style:style>
			<style:style style:name="ParametersTable.D" style:family="table-column">
				<style:table-column-properties style:column-width="0.7in" style:rel-column-width="601*"/>
			</style:style>
			<style:style style:name="ParametersTable.A1" style:family="table-cell">
				<style:table-cell-properties fo:background-color="#e6e6ff" fo:border-left="0.0069in solid #e6e6e6" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.B1" style:family="table-cell">
				<style:table-cell-properties fo:background-color="#e6e6ff" fo:border-left="none" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.C1" style:family="table-cell">
				<style:table-cell-properties fo:background-color="#e6e6ff" fo:border-left="none" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.D1" style:family="table-cell">
				<style:table-cell-properties fo:background-color="#e6e6ff" fo:border-left="none" fo:border-right="0.0069in solid #e6e6e6" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.A2" style:family="table-cell">
				<style:table-cell-properties fo:border-left="0.0069in solid #e6e6e6" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.B2" style:family="table-cell">
				<style:table-cell-properties fo:border-left="none" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.C2" style:family="table-cell">
				<style:table-cell-properties fo:border-left="none" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ParametersTable.D2" style:family="table-cell">
				<style:table-cell-properties fo:border-left="none" fo:border-right="0.0069in solid #e6e6e6" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ExampleTable" style:family="table">
				<style:table-properties style:width="8in" table:align="margins" fo:margin-right="-0.5in" style:writing-mode="lr-tb"/>
			</style:style>
			<style:style style:name="ExampleTable.A" style:family="table-column">
				<style:table-column-properties style:column-width="1.5in" style:rel-column-width="1500*"/>
			</style:style>
			<style:style style:name="ExampleTable.B" style:family="table-column">
				<style:table-column-properties style:column-width="6.5in" style:rel-column-width="6500*"/>
			</style:style>
			<style:style style:name="ExampleTable.A1" style:family="table-cell">
				<style:table-cell-properties style:vertical-align="top" fo:background-color="#e6e6ff" fo:border-left="0.0069in solid #e6e6e6" fo:border-right="none" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ExampleTable.B1" style:family="table-cell">
				<style:table-cell-properties style:vertical-align="top" fo:border-left="none" fo:border-right="0.0069in solid #e6e6e6" fo:border-top="0.0069in solid #e6e6e6" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ExampleTable.A2" style:family="table-cell">
				<style:table-cell-properties style:vertical-align="top" fo:background-color="#e6e6ff" fo:border-left="0.0069in solid #e6e6e6" fo:border-right="none" fo:border-top="none" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
			<style:style style:name="ExampleTable.B2" style:family="table-cell">
				<style:table-cell-properties style:vertical-align="top" fo:border-left="none" fo:border-right="0.0069in solid #e6e6e6" fo:border-top="none" fo:border-bottom="0.0069in solid #e6e6e6"/>
			</style:style>
		</office:automatic-styles>
	</xsl:template>
</xsl:stylesheet>
