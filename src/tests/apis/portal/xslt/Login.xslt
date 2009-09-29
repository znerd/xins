<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: Login.xslt,v 1.2 2006/12/19 15:22:37 agoubard Exp $
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="Commons.xslt" />

	<xsl:template match="commandresult">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
			<xsl:call-template name="header">
				<xsl:with-param name="title" select="'Login'" />
			</xsl:call-template>
			<body>
				<xsl:call-template name="page-header">
					<xsl:with-param name="title" select="'Login'" />
				</xsl:call-template>
				<div id="content">
					<xsl:call-template name="error" />
					<form method="{$form-method}" action="{$application-url}">
						<input type="hidden" name="command" value="{$command}" />
						<input type="hidden" name="action" value="Okay" />
						<table id="content">
							<tr>
								<td id="label">Username:</td>
								<td><input name="username" type="text" id="username" value="{parameter[@name='input.username']}" /></td>
							</tr>
							<tr>
								<td id="label">Password:</td>
								<td><input name="password" type="password" id="password" /></td>
							</tr>
							<tr>
								<td id="label">Salutation:</td>
								<td><input type="radio" name="salutation" value="Mister" />Mister<br />
										<input type="radio" name="salutation" value="Madam" />Madam<br />
										<input type="radio" name="salutation" value="Miss" />Miss
								</td>
							</tr>
							<tr>
								<td colspan="2" id="submit">
									<input id="submit" type="submit" value="Login &gt;" />
								</td>
							</tr>
						</table>
					</form>
					<font size="2" color="lightgray">Suggestion: test@test.com / tester</font>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
