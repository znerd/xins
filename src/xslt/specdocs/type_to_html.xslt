<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the type.html files that contains
 the description of the type.

 $Id: type_to_html.xslt,v 1.46 2007/08/15 12:40:07 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<xsl:include href="broken_freeze.xslt" />
	<xsl:include href="header.xslt"        />
	<xsl:include href="footer.xslt"        />
	<xsl:include href="../types.xslt"      />
	<xsl:include href="../urlencode.xslt"  />

	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node" select="document($api_file)/api" />

	<xsl:template match="type">

		<xsl:variable name="project_node" select="document($project_file)/project" />
		<xsl:variable name="api_node" select="document($api_file)/api" />
		<xsl:variable name="type_name"    select="@name" />

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>Type </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Specification of the {@name} type of the {$api} API." />

				<link rel="stylesheet" type="text/css" href="style.css" />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<link rel="top"  href="../index.html" title="API index"            />
				<link rel="up"   href="index.html"    title="Overview of this API" />
				<xsl:if test="pattern">
					<script type="text/javascript">
						function testPattern(pattern, input) {
							var result = document.getElementById('result');
							var resultMessage = '';
							try {
								resultMessage = 'The string <b style="color:blue">' + input + '</b> ';
								resultMessage += input.match(pattern) ? '<b style="color:blue">matches</b>' : '<b style="color:red">does not match</b>';
								resultMessage += ' the pattern.';
						  } catch (ex) {
								resultMessage = 'The pattern <b style="color:blue">' + pattern + '</b> ';
								resultMessage += 'is <b style="color:red">invalid</b>.';
							}
							result.innerHTML = resultMessage;
							return false;
						}
					</script>
				</xsl:if>
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">type</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Type </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
				</h1>

				<br />

				<!-- Broken freezes -->
				<xsl:call-template name="broken_freeze">
					<xsl:with-param name="project_home" select="$project_home" />
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="api" select="$api" />
					<xsl:with-param name="frozen_version" select="$api_node/type[@name=$type_name]/@freeze" />
					<xsl:with-param name="broken_file" select="concat($type_name, '.typ')" />
				</xsl:call-template>

				<xsl:apply-templates select="description" />

				<xsl:if test="boolean(see)">
					<table class="metadata">
						<tr>
							<td class="key">See also:</td>
							<td class="value">
								<xsl:apply-templates select="see" />
							</td>
						</tr>
					</table>
				</xsl:if>

				<xsl:apply-templates select="enum"       />
				<xsl:apply-templates select="pattern"    />
				<xsl:apply-templates select="properties" />
				<xsl:apply-templates select="int8"       />
				<xsl:apply-templates select="int16"      />
				<xsl:apply-templates select="int32"      />
				<xsl:apply-templates select="int64"      />
				<xsl:apply-templates select="float32"    />
				<xsl:apply-templates select="float64"    />
				<xsl:apply-templates select="base64"     />
				<xsl:apply-templates select="hex"        />
				<xsl:apply-templates select="list"       />
				<xsl:apply-templates select="set"        />

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="enum">
		<xsl:if test="item">
			<p />
			<xsl:text>This is an enumeration type. Acceptable values are limited to the following list:</xsl:text>
			<table class="typelist">
				<tr>
					<th>Name</th>
					<th>Value</th>
				</tr>
				<xsl:apply-templates select="item" />
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="enum/item">
		<tr>
			<td>
				<xsl:choose>
					<xsl:when test="@name">
						<xsl:value-of select="@name" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@value" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td>
				<xsl:value-of select="@value" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="pattern">
		<p />
		<xsl:text>This is a </xsl:text>
		<em>pattern type</em>
		<xsl:text>. Allowed values must match the following pattern:</xsl:text>
		<xsl:variable name="pattern" select="text()" />
		<blockquote>
			<code id="pattern">
				<xsl:value-of select="$pattern" />
			</code>
		</blockquote>
		<p />
		<form onsubmit="return testPattern(elements['pattern'].value, elements['input'].value)">
		  <input name="pattern" type="text" size="50" value="^{$pattern}$" />
				<xsl:text> </xsl:text>
		  <input type="button" value="&lt;- Original pattern" onclick="javascript:elements['pattern'].value='^' + document.getElementById('pattern').innerHTML + '$'"/>
			<br />
		  <input name="input" type="text" size="50" />
				<xsl:text> </xsl:text>
		  <input type="submit" value="Test this pattern" />
		  <div id="result">
				<xsl:text> </xsl:text>
			</div>
		</form>
	</xsl:template>

	<xsl:template match="properties">
		<p />
		<xsl:text>This is a </xsl:text>
		<em>properties type</em>
		<xsl:text>. Property names must conform to the </xsl:text>
		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"      />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="type"     select="@nameType" />
		</xsl:call-template>
		<xsl:text> type. Property values must conform to the </xsl:text>
		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"       />
			<xsl:with-param name="specsdir" select="$specsdir"  />
			<xsl:with-param name="type"     select="@valueType" />
		</xsl:call-template>
		<xsl:text> type.</xsl:text>
	</xsl:template>

	<xsl:template match="int8 | int16 | int32 | int64 | float32 | float64">
		<p />
		This is a <em>
		<xsl:value-of select="name()" />
		type</em>.<br/>
		<xsl:if test="@min">
			<xsl:text>The minimum value is </xsl:text>
			<xsl:value-of select="@min" />
			<xsl:text>.</xsl:text><br />
		</xsl:if>
		<xsl:if test="@max">
			<xsl:text>The maximum value is </xsl:text>
			<xsl:value-of select="@max" />
			<xsl:text>.</xsl:text><br />
		</xsl:if>
	</xsl:template>

	<xsl:template match="base64 | hex">
		<p />
		This is a <em>
		<xsl:value-of select="name()" />
		type</em>.<br/>
		<xsl:if test="@min">
			<xsl:text>The minimum size is </xsl:text>
			<xsl:value-of select="@min" />
			<xsl:text> bytes.</xsl:text><br />
		</xsl:if>
		<xsl:if test="@max">
			<xsl:text>The maximum size is </xsl:text>
			<xsl:value-of select="@max" />
			<xsl:text> bytes.</xsl:text><br />
		</xsl:if>
	</xsl:template>

	<xsl:template match="list | set">
		<p />
		<xsl:text>This is a </xsl:text>
		<em>
		<xsl:value-of select="name()" />
		type</em>
		<xsl:text>. The elements must conform to the type </xsl:text>
		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"      />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="type"     select="@type" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
