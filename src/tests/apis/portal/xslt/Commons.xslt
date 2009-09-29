<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: Commons.xslt,v 1.1 2006/09/19 07:35:46 agoubard Exp $
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output
	method="html"
	indent="yes"
	media-type="text/html"
	doctype-public="-//W3C//DTD XHTML 1.1//EN"
	doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
	encoding="UTF-8"
	/>

	<!-- Variable declarations -->
	<xsl:variable name="command" select="//commandresult/@command" />
	<xsl:variable name="action" select="//commandresult/@action" />
	<xsl:variable name="form-method" select="'get'" />
	<xsl:variable name="application-url" select="'#'" />

	<xsl:template name="header">
		<xsl:param name="title" />
		<head>
			<title>
				<xsl:value-of select="$title" />
			</title>
			<link rel="stylesheet" type="text/css" href="../style.css" />
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			<script type="text/javascript" src="../utils.js">
				<xsl:text> </xsl:text>
			</script>
			<xsl:if test="//data/errorlist/fielderror or
				parameter[@name = 'error.code' and contains(., 'Password')] or
				parameter[@name = 'error.code' and contains(., 'Login')]">
				<style type="text/css">
					<xsl:for-each select="//data/errorlist/fielderror">
						<xsl:text />select#<xsl:value-of select="@field" />,
						<xsl:text />input#<xsl:value-of select="@field" />
						<xsl:if test="position() != last()">,</xsl:if>
					</xsl:for-each>
					<xsl:if test="parameter[@name = 'error.code' and contains(., 'Password')]">
						<xsl:text />input#password,
					</xsl:if>
					<xsl:if test="parameter[@name = 'error.code' and contains(., 'Login')]">
						<xsl:text />input#email
					</xsl:if>
					{ color:red; border-color: red; } 
				</style>
			</xsl:if>
		</head>
	</xsl:template>

	<xsl:template name="page-header">
		<xsl:param name="title" />
		<table id="header">
			<tr>
				<td>XINS Petstore Demo :: <xsl:value-of select="$title" /></td>
			</tr>
		</table>
		<div id="logo"><xsl:text /></div>
		<br />
		<br />
	</xsl:template>

	<xsl:template name="error">
		<xsl:choose>
			<xsl:when test="//data/errorlist/fielderror">
				<span style="color:red;font-weight:bold">Fields marked red are invalid</span>
			</xsl:when>
			<xsl:when test="parameter[@name='error.code'] = 'AlreadyRegistered'">
				<span style="color:red;font-weight:bold">You are already registered</span>
			</xsl:when>
			<xsl:when test="parameter[@name='error.code'] = 'ProductNotAvailable'">
				<span style="color:red;font-weight:bold">The selected product is not available</span>
			</xsl:when>
			<xsl:when test="parameter[@name='error.code'] = 'IncorrectPassword'">
				<span style="color:red;font-weight:bold">The password you supplied is incorrect</span>
			</xsl:when>
			<xsl:when test="parameter[@name='error.code'] = 'UnknownLogin'">
				<span style="color:red;font-weight:bold">The login you supplied is incorrect</span>
			</xsl:when>
			<xsl:when test="parameter[starts-with(@name,'error.')]">
				<span style="color:red;font-weight:bold">An unknown error has occurred</span>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

