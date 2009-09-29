<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: MainPage.xslt,v 1.2 2007/05/08 10:41:44 agoubard Exp $
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
					<xsl:with-param name="title" select="'Search pet'" />
				</xsl:call-template>
				<xsl:if test="parameter[@name='session.username'] = 'superhuman'">
					<xsl:message terminate="yes">Only human allowed!</xsl:message>
				</xsl:if>
				<div id="content">
					<xsl:call-template name="error" />
					<form name="form1" method="{$form-method}" action="{$application-url}">
						<input type="hidden" name="command" value="{$command}" />
						<input type="hidden" name="action" value="Okay" />
						<table id="content">
							<tr>
								<td id="label">Pet name:</td>
								<td><input name="petName" type="text" onkeyup="petSuggest();" autocomplete="off" id="petName" /></td>
							</tr>
							<tr>
								<td colspan="2">
									<font size="2" color="lightgray">
										<span id="suggestion">
											<xsl:text> </xsl:text>
										</span>
									</font>
								</td>
							</tr>
							<tr>
								<td id="submit" colspan="2"><input id="submit" type="submit" value="Search" /></td>
							</tr>
						</table>
					</form>
					<xsl:if test="data/Pet">
						<table id="content">
							<tr><th>Pet name</th><th>Age</th><th>Price</th><th>Quantity</th></tr>
							<xsl:for-each select="data/Pet">
								<tr>
									<td id="label"><xsl:value-of select="@petName" /></td>
									<td id="label"><xsl:value-of select="@age" /></td>
									<td id="label">$ <xsl:value-of select="@price" /></td>
									<td>
										<form method="{$form-method}" action="{$application-url}">
											<input type="hidden" name="command" value="OrderPet" />
											<input type="hidden" name="petName" value="{@petName}" />
											<input type="hidden" name="petID" value="{@petID}" />
											<input name="quantity" type="text" size="3" value="1" id="quantity" />
											<input id="submit" type="submit" value="Order" />
										</form>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</xsl:if>
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
