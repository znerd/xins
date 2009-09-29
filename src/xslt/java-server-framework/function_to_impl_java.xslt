<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the skeleton for the implementation of the function.

 $Id: function_to_impl_java.xslt,v 1.31 2007/03/21 10:53:13 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../types.xslt"  />
	<xsl:include href="../author.xslt" />

	<!-- Convert the file locations to nodes -->
	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node" select="document($api_file)/api" />

	<xsl:template match="function">

		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

]]></xsl:text>
		<xsl:call-template name="imports">
			<xsl:with-param name="types" select="input/param | output/param" />
			<xsl:with-param name="imports-set" select="''" />
		</xsl:call-template>
		<xsl:text><![CDATA[
/**
 * Implementation of the <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function.
 *
 * <p>Description: ]]></xsl:text>
		<xsl:value-of select="description/text()" />
		<xsl:text><![CDATA[
 *
 * @version $]]><![CDATA[Revision$ $]]><![CDATA[Date$
 * @author ]]></xsl:text>
				<xsl:variable name="owner_info">
					<xsl:apply-templates select="$api_node" mode="owner" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$owner_info != ''">
						<xsl:value-of select="$owner_info" disable-output-escaping="yes"/>
					</xsl:when>
					<!-- Split the text up, so it does not match when searched for -->
					<xsl:otherwise>
						<xsl:text>TO</xsl:text>
						<xsl:text>DO</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
		<xsl:text><![CDATA[
 */
public final class ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>Impl extends </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ {

    /**
     * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[Impl</code> instance.
     *
     * @param api
     *    the API to which this function belongs, guaranteed to be not
     *    <code>null</code>.
     */
    public ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[Impl(APIImpl api) {
        super(api);
    }

    /**
     * Calls this function. If the function fails, it may throw any kind of
     * exception. All exceptions will be handled by the caller.
     *
     * @param request
     *    the request, never <code>null</code>.
     *
     * @return
     *    the result of the function call, should never be <code>null</code>.
     *
     * @throws Throwable
     *    if anything went wrong.
     */
    public Result call(Request request) throws Throwable {]]></xsl:text>
		<xsl:call-template name="callcontent" />
		<xsl:text>
    }</xsl:text>
		<xsl:call-template name="aftercall" />
		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<!-- Writes the import statements for the function -->
	<xsl:template name="imports">
		<xsl:param name="types" />
		<xsl:param name="imports-set" />

		<xsl:if test="$types">
			<xsl:variable name="import">
				<xsl:call-template name="javaimport_for_type">
					<xsl:with-param name="project_node" select="$project_node"   />
					<xsl:with-param name="specsdir"     select="$specsdir"       />
					<xsl:with-param name="api"          select="$api"            />
					<xsl:with-param name="type"         select="$types[1]/@type" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:if test="not(contains($imports-set, $import)) and not(starts-with($import, '')) and not($import = 'byte[]')">
				<xsl:text>import </xsl:text>
				<xsl:value-of select="$import" />
				<xsl:text>;
</xsl:text>
			</xsl:if>
			<xsl:call-template name="imports">
				<xsl:with-param name="types" select="$types[position()!=1]" />
				<xsl:with-param name="imports-set" select="concat($imports-set, ';', $import)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Write the content of the call method -->
	<xsl:template name="callcontent">
		<xsl:text>
        SuccessfulResult result = new SuccessfulResult();
        // TO</xsl:text>
		<!-- Split this text up so it does not match when searched for -->
		<xsl:text>DO
        return result;</xsl:text>
	</xsl:template>

	<!-- Write the text that should be generated after the call method -->
	<xsl:template name="aftercall" />
</xsl:stylesheet>
