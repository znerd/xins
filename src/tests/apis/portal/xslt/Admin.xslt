<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: Admin.xslt,v 1.1 2006/10/13 14:44:48 agoubard Exp $
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="Commons.xslt" />

	<xsl:template match="commandresult">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
			<xsl:call-template name="header">
				<xsl:with-param name="title" select="'Search pet'" />
			</xsl:call-template>
			<body>
				<xsl:call-template name="page-header">
					<xsl:with-param name="title" select="'Admin'" />
				</xsl:call-template>
				<div id="content">
					<xsl:call-template name="error" />
					<h2>Admin page.</h2>
					<table style="border:none">
						<tr>
							<td><a href="?command=Logout">Logout</a></td>
						</tr>
					</table>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
