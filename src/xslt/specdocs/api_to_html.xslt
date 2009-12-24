<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the index.html of the specification documentation.

 $Id: api_to_html.xslt,v 1.55 2007/08/14 12:08:39 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="apis_dir"     />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<xsl:include href="header.xslt"       />
	<xsl:include href="footer.xslt"       />
	<xsl:include href="../author.xslt"    />
	<xsl:include href="../types.xslt"     />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="api">

		<xsl:variable name="prevcount" select="count($project_node/api[@name = $api]/preceding::api)" />
		<xsl:variable name="prev"      select="$project_node/api[$prevcount]/@name" />
		<xsl:variable name="prev_url"  select="concat('../', $prev, '/index.html')" />
		<xsl:variable name="next"      select="$project_node/api[@name = $api]/following-sibling::api/@name" />
		<xsl:variable name="next_url"  select="concat('../', $next, '/index.html')" />

		<xsl:variable name="prev_title">
			<xsl:if test="boolean($prev) and not($prev = '')">
				<xsl:value-of select="$prev" />
				<xsl:text> API</xsl:text>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="next_title">
			<xsl:if test="boolean($next) and not($next = '')">
				<xsl:value-of select="$next" />
				<xsl:text> API</xsl:text>
			</xsl:if>
		</xsl:variable>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>API overview: </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Specification of the {@name} API." />

				<link rel="stylesheet" type="text/css" href="style.css" />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<link rel="top"                        href="../index.html" title="API index" />
				<link rel="up"                         href="../index.html" title="API index" />
				<link rel="first">
					<xsl:attribute name="href">
						<xsl:text>../</xsl:text>
						<xsl:value-of select="$project_node/api[1]/@name" />
						<xsl:text>/index.html</xsl:text>
					</xsl:attribute>
				</link>
				<xsl:if test="boolean($prev) and not($prev = '')">
					<link rel="prev">
						<xsl:attribute name="href">
							<xsl:value-of select="$prev_url" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$prev_title" />
						</xsl:attribute>
					</link>
				</xsl:if>
				<xsl:if test="boolean($next) and not($next = '')">
					<link rel="next">
						<xsl:attribute name="href">
							<xsl:value-of select="$next_url" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$next_title" />
						</xsl:attribute>
					</link>
				</xsl:if>
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">api</xsl:with-param>
					<xsl:with-param name="prev" select="$prev_title" />
					<xsl:with-param name="next" select="$next_title" />
					<xsl:with-param name="prev_url" select="$prev_url" />
					<xsl:with-param name="next_url" select="$next_url" />
				</xsl:call-template>

				<h1>
					<xsl:text>API overview </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
					<font size="-1">
					(<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@name" />
								<xsl:text>-client.zip</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="title">
								<xsl:text>Download the client API (Javadoc, jar, sources, specdocs) if available</xsl:text>
							</xsl:attribute>
							<xsl:text>Download</xsl:text>
						</a>)
						</font>
				</h1>

				<xsl:apply-templates select="description" />

				<xsl:if test="category">
					<h2>Categories</h2>
					<table class="functionlist">
						<tr>
							<th>Category</th>
							<th>Description</th>
						</tr>
						<xsl:apply-templates select="category" />
					</table>
				</xsl:if>

				<h2>Functions</h2>
				<xsl:choose>
					<xsl:when test="count(function) = 0">
						<p>
							<em>This API defines no functions.</em>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table class="functionlist">
							<tr>
								<th>Function</th>
								<th>Version</th>
								<th>Status</th>
								<th>Description</th>
							</tr>
							<xsl:apply-templates select="function" />
						</table>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Types</h2>
				<xsl:choose>
					<xsl:when test="count(type) = 0">
						<p>
							<em>This API defines no types.</em>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table class="functionlist">
							<tr>
								<th>Type</th>
								<th>Version</th>
								<th>Status</th>
								<th>Description</th>
							</tr>
							<xsl:apply-templates select="type" />
						</table>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Error codes</h2>
				<xsl:choose>
					<xsl:when test="count(resultcode) = 0">
						<p>
							<em>This API defines no specific error codes.</em>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table class="functionlist">
							<tr>
								<th>Error code</th>
								<th>Version</th>
								<th>Status</th>
								<th>Description</th>
							</tr>
							<xsl:apply-templates select="resultcode" />
						</table>
					</xsl:otherwise>
				</xsl:choose>

				<h2>API Owner</h2>
				<p>
				<xsl:variable name="owner_info">
					<xsl:apply-templates select="current()" mode="owner" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$owner_info != ''">
						<xsl:value-of select="$owner_info" disable-output-escaping="yes"/>
					</xsl:when>
					<xsl:otherwise>
						<em>No API Owner has been assigned to this API.</em>
					</xsl:otherwise>
				</xsl:choose>
				</p>

				<h2>Environments</h2>
				<xsl:choose>
					<xsl:when test="environment">
						<ul>
							<xsl:apply-templates select="environment" />
						</ul>
					</xsl:when>
					<xsl:when test="$project_node/api[@name = $api]/environments">
						<ul>
							<xsl:variable name="env_file" select="concat($apis_dir, '/', $api, '/environments.xml')" />
							<xsl:apply-templates select="document($env_file)/environments/environment" />
						</ul>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<em>No environments have been defined for this API.</em>
						</p>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Properties</h2>
				<xsl:choose>
					<xsl:when test="$project_node/api[@name = $api]/impl">
						<xsl:for-each select="$project_node/api[@name = $api]/impl">
							<xsl:variable name="implName" select="@name" />
							<xsl:variable name="implName2">
								<xsl:if test="@name and string-length($implName) &gt; 0">
									<xsl:value-of select="concat('-', $implName)" />
								</xsl:if>
							</xsl:variable>
							<xsl:variable name="impl_file" select="concat($apis_dir, '/', $api, '/impl', $implName2, '/impl.xml')" />
							<xsl:variable name="impl_node" select="document($impl_file)/impl" />
							<xsl:choose>
								<xsl:when test="$impl_node/runtime-properties/property and @name and string-length($implName) &gt; 0">
									<a href="properties{$implName2}.html" title="Specification of the runtime properties used for the {$implName} implementation.">
										<xsl:text>Runtime properties for </xsl:text>
										<xsl:value-of select="$implName" />
										<xsl:text> implementation.</xsl:text>
									</a>
								</xsl:when>
								<xsl:when test="$impl_node/runtime-properties/property">
									<a href="properties.html" title="Specification of the runtime properties used by this API">Runtime properties</a>
								</xsl:when>
								<xsl:when test="@name and string-length($implName) &gt; 0">
									<p>
										<em>
											<xsl:text>No runtime properties have been defined for the &quot;</xsl:text>
											<xsl:value-of select="$implName" />
											<xsl:text>&quot; implementation.</xsl:text>
										</em>
									</p>
								</xsl:when>
								<xsl:otherwise>
									<p>
										<em>No runtime properties have been defined for the default implementation.</em>
									</p>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<em>No implementation information available for this API.</em>
						</p>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Logdoc</h2>
				<xsl:choose>
					<xsl:when test="$project_node/api[@name = $api]/impl">
						<xsl:for-each select="$project_node/api[@name = $api]/impl">
							<xsl:variable name="implName" select="@name" />
							<xsl:variable name="implName2">
								<xsl:if test="@name and string-length($implName) &gt; 0">
									<xsl:value-of select="concat('-', $implName)" />
								</xsl:if>
							</xsl:variable>
							<xsl:variable name="impl_file" select="concat($apis_dir, '/', $api, '/impl', $implName2, '/impl.xml')" />
							<xsl:variable name="impl_node" select="document($impl_file)/impl" />
							<xsl:choose>
								<xsl:when test="$impl_node/logdoc and @name and string-length($implName) &gt; 0">
									<a href="logdoc{$implName2}/index.html" title="Logdoc used for the {$implName} implementation.">
										<xsl:text>Logdoc for </xsl:text>
										<xsl:value-of select="$implName" />
										<xsl:text> implementation.</xsl:text>
									</a>
								</xsl:when>
								<xsl:when test="$impl_node/logdoc">
									<a href="logdoc/index.html" title="Logdoc used by the default implementation">Logdoc specifications</a>
								</xsl:when>
								<xsl:when test="@name and string-length($implName) &gt; 0">
									<p>
										<em>
											<xsl:text>No Logdoc have been defined for the &quot;</xsl:text>
											<xsl:value-of select="$implName" />
											<xsl:text>&quot; implementation.</xsl:text>
										</em>
									</p>
								</xsl:when>
								<xsl:otherwise>
									<p>
										<em>No logdoc have been defined for the default implementation.</em>
									</p>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<em>No implementation information available for this API.</em>
						</p>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="environment">
		<li>
			<xsl:value-of select="@id" />
			<!-- Generate the ( version statistics settings ) links. -->
			<font size="-1">
				<xsl:text> (</xsl:text>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@url" />
						<xsl:text>?_function=_GetVersion&amp;_convention=_xins-std</xsl:text>
					</xsl:attribute>
					<xsl:text>version</xsl:text>
				</a>
				<xsl:text> </xsl:text>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@url" />
						<xsl:text>?_function=_GetStatistics&amp;_convention=_xins-std&amp;detailed=true</xsl:text>
					</xsl:attribute>
					<xsl:text>statistics</xsl:text>
				</a>
				<xsl:text> </xsl:text>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@url" />
						<xsl:text>?_function=_GetSettings&amp;_convention=_xins-std</xsl:text>
					</xsl:attribute>
					<xsl:text>settings</xsl:text>
				</a>
				<xsl:text>)</xsl:text>
			</font>
		</li>
	</xsl:template>

	<xsl:template match="api/description">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<xsl:template match="function">

		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		<xsl:variable name="function_node" select="document($function_file)/function" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="$function_node/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not($function_node)">
			<xsl:message terminate="yes">
				<xsl:text>Function file '</xsl:text>
				<xsl:value-of select="$function_file" />
				<xsl:text>' not found for the defined function '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="$function_node/@name != @name">
			<xsl:message terminate="yes">
				<xsl:text>The function file '</xsl:text>
				<xsl:value-of select="$function_file" />
				<xsl:text>' does not define the function name '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>' as expected but '</xsl:text>
				<xsl:value-of select="$function_node/@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="@freeze = $version">Frozen</xsl:when>
					<xsl:when test="@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="$function_node/deprecated">
						<span class="broken_freeze" title="{$function_node/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="$function_node/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="type">

		<xsl:variable name="type_file">
			<xsl:call-template name="file_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="type" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="type_node" select="document($type_file)/type" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="$type_node/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not($type_node)">
			<xsl:message terminate="yes">
				<xsl:text>Type file '</xsl:text>
				<xsl:value-of select="$type_file" />
				<xsl:text>' not found for the defined type '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$type_node/@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$type_node/@name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="@freeze = $version">Frozen</xsl:when>
					<xsl:when test="@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="$type_node/deprecated">
						<span class="broken_freeze" title="{$type_node/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="$type_node/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="resultcode">

		<xsl:variable name="resultcode_file">
			<xsl:choose>
				<xsl:when test="contains(@name, '/')">
					<xsl:value-of select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($specsdir, '/', @name, '.rcd')" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="resultcode_node" select="document($resultcode_file)/resultcode" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="$resultcode_node/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not($resultcode_node)">
			<xsl:message terminate="yes">
				<xsl:text>Error code file '</xsl:text>
				<xsl:value-of select="$resultcode_file" />
				<xsl:text>' not found for the defined error code '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$resultcode_node/@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$resultcode_node/@name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="@freeze = $version">Frozen</xsl:when>
					<xsl:when test="@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="$resultcode_node/deprecated">
						<span class="broken_freeze" title="{$resultcode_node/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="$resultcode_node/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="category">

		<xsl:variable name="category_file" select="concat($specsdir, '/', @name, '.cat')" />
		<xsl:variable name="category_node" select="document($category_file)/category" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="$category_node/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not($category_node)">
			<xsl:message terminate="yes">
				<xsl:text>Category file '</xsl:text>
				<xsl:value-of select="$category_file" />
				<xsl:text>' not found for the defined category '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>
			<td>
				<xsl:apply-templates select="$category_node/description" />
			</td>
		</tr>

	</xsl:template>

	<xsl:template match="function/description | type/description | resultcode/description | category/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
