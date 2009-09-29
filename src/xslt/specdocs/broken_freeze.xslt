<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the function.html files that contains
 the input description, the output description and the examples.

 $Id: broken_freeze.xslt,v 1.9 2007/01/04 10:17:34 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="broken_freeze">
		<!-- Define parameters -->
		<xsl:param name="project_home"   />
		<xsl:param name="project_node"   />
		<xsl:param name="apis_dir"     />
		<xsl:param name="specsdir"       />
		<xsl:param name="api"            />
		<xsl:param name="frozen_version" />
		<xsl:param name="broken_file"    />

		<xsl:variable name="cvsweb_url" select="$project_node/cvsweb/@href" />

		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="string-length($frozen_version) &gt; 0">
			<xsl:if test="not($frozen_version = $version)">
				<div class="broken_freeze">
					<h3>Broken freeze!</h3>
					<p>
						<xsl:text>Version </xsl:text>
						<xsl:value-of select="$frozen_version" />
						<xsl:text> is marked as frozen.</xsl:text>
						<xsl:if test="string-length($cvsweb_url) &gt; 0">
							<br />
							<xsl:text>View differences between this version and the frozen version:</xsl:text>
							<br />
							<xsl:variable name="api_path">
								<xsl:choose>
									<xsl:when test="$specsdir = concat($apis_dir, '/', $api, '/spec')">
										<xsl:value-of select="concat($apis_dir, '/', $api, '/spec')" />
									</xsl:when>
									<xsl:otherwise> <!-- XXX: Review -->
										<xsl:value-of select="concat('src/apis/', $api)" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<a href="{$cvsweb_url}/{$api_path}/{$broken_file}.diff?r1={$frozen_version}&amp;r2={$version}">
								<xsl:text>diff </xsl:text>
								<xsl:value-of select="$frozen_version" />
								<xsl:text> and </xsl:text>
								<xsl:value-of select="$version" />
							</a>
							<xsl:text> (</xsl:text>
							<a href="{$cvsweb_url}/{$api_path}/{$broken_file}.diff?r1={$frozen_version}&amp;r2={$version}&amp;f=h">
								<xsl:text>colored</xsl:text>
							</a>
							<xsl:text>)</xsl:text>
						</xsl:if>
					</p>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
