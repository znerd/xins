<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the function-testform-environment HTML form.
 This form is use to test an API on a given environment.

 $Id: function_to_html.xslt,v 1.60 2007/09/17 09:30:37 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="apis_dir"     />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../specdocs/footer.xslt" />
	<xsl:include href="../specdocs/header.xslt" />
	<xsl:include href="../types.xslt" />

	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node"     select="document($api_file)/api" />

	<xsl:output
	method="html"
	indent="yes"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:template match="function">

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="env_file">
			<xsl:if test="$project_node/api[@name=$api]/environments">
				<xsl:value-of select="concat($apis_dir, '/', $api, '/environments.xml')" />
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="init_environment">
			<xsl:choose>
				<xsl:when test="string-length($env_file) > 0">
					<xsl:value-of select="document($env_file)/environments/environment[1]/@url" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('http://localhost:8080/', $api, '/')" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="$functionName" />
					<xsl:text> test form</xsl:text>
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Test form for the {$functionName} function of the {$api} API." />

				<link rel="stylesheet" type="text/css" href="style.css" />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<script type="text/javascript" src="testforms.js"><xsl:text> </xsl:text></script>
				<link rel="top" href="../index.html" title="API index" />
			</head>
			<body onload="selectEnv();">
				<xsl:call-template name="header">
					<xsl:with-param name="active">testform</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Function </xsl:text>
					<em>
						<xsl:value-of select="$functionName" />
					</em>
					<xsl:text> test form</xsl:text>
				</h1>

				<form method="GET" action="{$init_environment}" target="xmlOutputFrame"
				onsubmit="this.action=this._environment.value;setEnvCookie(this);return doRequest(this)">
					<p>
						<input name="_function" type="hidden">
							<xsl:attribute name="value">
								<xsl:value-of select="$functionName" />
							</xsl:attribute>
						</input>
						<input name="_convention" value="_xins-std" type="hidden" />
						<xsl:call-template name="environment_section">
							<xsl:with-param name="env_file" select="$env_file" />
						</xsl:call-template>
					</p>
					<xsl:call-template name="autofill_section" />

					<xsl:call-template name="input_section">
						<xsl:with-param name="functionName" select="$functionName" />
					</xsl:call-template>
				</form>
				<div class="url" style="padding-bottom: 5pt" id="query">
					<xsl:text> </xsl:text>
				</div>
				<iframe height="500" id="xmlOutputFrame" name="xmlOutputFrame" src="about:blank" width="100%"></iframe>
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="em">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="strong">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="list">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="list/item">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template name="environment_section">
		<xsl:param name="env_file" />
		<xsl:text>Execution environment: </xsl:text>
		<xsl:choose>
			<xsl:when test="string-length($env_file) > 0">
				<select name="_environment" class="required">
					<xsl:for-each select="document($env_file)/environments/environment">
						<option value="{@url}">
							<xsl:value-of select="@id" />
						</option>
					</xsl:for-each>
				</select>
			</xsl:when>
			<xsl:otherwise>
				<input type="text" name="_environment" class="required" value="http://localhost:8080/{$api}/" size="60" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="autofill_section">
		<xsl:if test="example">
			<script type="text/javascript">
				<xsl:text>
function fillExample(selectedExample) {
  if (selectedExample == '') {
    var currentEnv = document.forms[0]._environment.value;
    document.forms[0].reset();
    document.forms[0]._environment.value = currentEnv;</xsl:text>
				<xsl:for-each select="example">
					<xsl:text>
  } else if (selectedExample == </xsl:text>
					<xsl:value-of select="position()" />
					<xsl:text>) {</xsl:text>
					<xsl:for-each select="input-example">
						<xsl:text>
    document.forms[0].</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>.value = '</xsl:text>
						<xsl:value-of select="text()" />
						<xsl:text>';</xsl:text>
					</xsl:for-each>
					<xsl:if test="input-data-example">
    document.forms[0]._data.value = <xsl:text>'&lt;data&gt;\n' +</xsl:text>
						<xsl:apply-templates select="input-data-example/element-example" />
						<xsl:text>
      '&lt;/data&gt;';</xsl:text>
					</xsl:if>
				</xsl:for-each>
				<xsl:text>
  }
}
				</xsl:text>
			</script>
			<p>
				<xsl:text>Auto-fill the form with: </xsl:text>
				<select name="_autofill" class="optional" onchange="fillExample(this.value)">
					<option value="">
						<xsl:text>-- blank --</xsl:text>
					</option>
					<xsl:for-each select="example">
						<option value="{position()}">
							<xsl:value-of select="concat('Example ', position())" />
						</option>
					</xsl:for-each>
				</select>
			</p>
		</xsl:if>
	</xsl:template>

	<xsl:template name="input_section">
		<xsl:param name="functionName" />

		<h2>Test form</h2>
		<xsl:choose>
			<xsl:when test="input/param or input/data">
				<table>
					<xsl:apply-templates select="input/param" />
					<xsl:apply-templates select="input/data" />
					<tr>
						<td align="right" colspan="2">
							<input class="submit" type="submit" value="Submit" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<hr />
						</td>
					</tr>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<em>This function supports no input parameters.</em>
				<p>
					<input type="submit" value="Submit" />
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="param">

		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:value-of select="@type" />
				</xsl:when>
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="type_file">
			<xsl:call-template name="file_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="type" select="$type" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="isenum" select="not(starts-with($type, '_')) and boolean(document($type_file)/type/enum)" />

		<xsl:variable name="input_form_name">
			<xsl:if test="@name = 'action' or @name = 'target' or @name = 'method'">
				<xsl:text>_</xsl:text>
			</xsl:if>
			<xsl:value-of select="@name" />
		</xsl:variable>
		<!-- TODO: Deprecated parameters -->

		<tr>
			<td class="name">
				<span>
					<xsl:if test="boolean(description/text())">
						<xsl:attribute name="title">
							<xsl:call-template name="firstline">
								<xsl:with-param name="text" select="description/text()" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@name" />
				</span>
				<xsl:text> (</xsl:text>
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type"     select="$type" />
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</td>
			<td class="value">
				<xsl:choose>
					<xsl:when test="$isenum">
						<select name="{$input_form_name}">
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<option></option>
							<xsl:for-each select="document($type_file)/type/enum/item">
								<xsl:variable name="label">
									<xsl:choose>
										<xsl:when test="string-length(@name) &gt; 0">
											<xsl:value-of select="@name" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="@value" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<option value="{@value}">
									<xsl:value-of select="$label" />
								</option>
							</xsl:for-each>
						</select>
					</xsl:when>
					<xsl:when test="$type = '_boolean'">
						<select name="{$input_form_name}">
							<option></option>
							<option value="true">true</option>
							<option value="false">false</option>
						</select>
					</xsl:when>
					<xsl:otherwise>
						<input type="text" name="{$input_form_name}">
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:if test="@default">
								<xsl:attribute name="value">
									<xsl:value-of select="@default" />
								</xsl:attribute>
							</xsl:if>
						</input>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="@required = 'true'"> *</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<!--
		Writes the row for the data section.
	-->
	<xsl:template match="data">
		<xsl:if test="element">
			<tr>
				<td class="name">
					<span title="data section of the request">
						Data section
					</span>
				</td>
				<td class="value">
					<textarea name="_data" rows="6" cols="40" class="optional" />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!--
		Writes the input data section with auto-fill.
	-->
	<xsl:template match="element-example">

		<xsl:variable name="text" select="pcdata-example/text()" />

		<xsl:text>
  '&lt;</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="attribute-example" />
		<xsl:if test="not(element-example) and not(boolean($text) and not($text = ''))">
			<xsl:text> /</xsl:text>
		</xsl:if>
		<xsl:text>&gt;\n' +</xsl:text>

		<xsl:if test="boolean($text) and not($text = '')">
			<xsl:value-of select="$text" />
		</xsl:if>

		<xsl:apply-templates select="element-example" />

		<xsl:if test="boolean(element-example)">
			<xsl:text>
  '</xsl:text>
		</xsl:if>

		<xsl:if test="boolean(element-example) or (boolean($text) and not($text=''))">
			<xsl:text>&lt;/</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>&gt;\n' +</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="attribute-example">
		<xsl:variable name="name" select="@name" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>=</xsl:text>
		<xsl:text>"</xsl:text>
		<xsl:value-of select="text()" />
		<xsl:text>"</xsl:text>
	</xsl:template>
</xsl:stylesheet>
