<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the result code used in the functions of the api.

 $Id: resultcode_to_java.xslt,v 1.32 2007/03/29 09:03:44 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="check_params.xslt"  />
	<xsl:include href="result_java.xslt"   />
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../types.xslt"      />
	<xsl:include href="../warning.xslt"    />
	<xsl:include href="../xml_to_java.xslt"    />

	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node" select="document($api_file)/api" />

	<xsl:template match="resultcode">

		<xsl:variable name="resultcode" select="@name" />
		<xsl:variable name="className" select="concat($resultcode, 'Result')" />

		<xsl:variable name="resultcodeIncludes">
			<xsl:variable name="resultcodes_file" select="concat($project_home, '/build/java-fundament/', $api, '/resultcodes.xml')" />
			<xsl:variable name="resultcodes_node" select="document($resultcodes_file)/api" />
			<xsl:for-each select="$resultcodes_node/function/resultcode[@name=$resultcode]">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="../@name" />
				<xsl:text>.UnsuccessfulResult</xsl:text>
			</xsl:for-each>
		</xsl:variable>

		<!-- Truncate the first ", " -->
		<xsl:variable name="resultcodeIncludes2"    select="concat('implements ', substring($resultcodeIncludes, 2))" />

		<!-- Warn if name differs from value -->
		<xsl:if test="(string-length(@value) &gt; 0) and (not(@value = @name))">
			<xsl:call-template name="warn">
				<xsl:with-param name="message">
					<xsl:text>Errorcode name ('</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>') differs from value ('</xsl:text>
					<xsl:value-of select="@value" />
					<xsl:text>'). This may cause confusion and errors.</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Warn if no function uses this ResultCode -->
		<xsl:if test="$resultcodeIncludes = ''">
			<xsl:call-template name="warn">
				<xsl:with-param name="message">
					<xsl:text>Errorcode '</xsl:text>
					<xsl:value-of select="$resultcode" />
					<xsl:text>'is not used in any function.</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text>;

/**
 * UnsuccessfulResult due to a </xsl:text>
		<xsl:value-of select="$resultcode" />
		<xsl:if test="$resultcodeIncludes = ''">
			<xsl:call-template name="warn">
            <xsl:with-param name="message">
               <xsl:text>This errorcode is not used in any function.</xsl:text>
            </xsl:with-param>
         </xsl:call-template>
		</xsl:if>
		<xsl:text>.
 */
public final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> extends org.xins.server.FunctionResult </xsl:text>
		<!-- This class should implements the UnsuccessfulResult from all the functions
		     that reference to this result code. -->
		<xsl:if test="not($resultcodeIncludes = '')">
			<xsl:value-of select="$resultcodeIncludes2" />
		</xsl:if>
		<xsl:text> {
</xsl:text>
		<xsl:call-template name="constructor">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>

		<!-- Generate the set methods, the inner classes and the add methods -->
		<xsl:apply-templates select="output" />

		<xsl:apply-templates select="output/data/element" mode="addElementClass" />
		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template name="constructor">
		<xsl:param name="className" />
		<xsl:text><![CDATA[
   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      super("</xsl:text>
		<xsl:choose>
			<xsl:when test="@value">
				<xsl:value-of select="@value" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@name" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>");</xsl:text>
		<xsl:for-each select="param[@default]">
			<xsl:text>
      param(&quot;</xsl:text>
			<xsl:value-of select="concat(@name, '&quot;, &quot;')" />
			<xsl:call-template name="xml_to_java_string">
				<xsl:with-param name="text" select="@default" />
			</xsl:call-template>
			<xsl:text>");</xsl:text>
		</xsl:for-each>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<xsl:template name="search-matching-resultcode">
		<!-- Define parameters -->
		<xsl:param name="functionName" />
		<xsl:param name="resultcode" />

		<!-- Determine file that defines type -->
		<xsl:variable name="functionFile"    select="concat($specsdir, '/', $functionName, '.fnc')" />

		<xsl:for-each select="document($functionFile)/function/output/resultcode-ref">
			<xsl:if test="@name = $resultcode">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text>.UnsuccessfulResult</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
