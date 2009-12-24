<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the function.html files that contain
 the input description, the output description and the examples.

 $Id: function_to_html.xslt,v 1.117 2007/08/14 12:08:40 agoubard Exp $

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

	<!-- Perform includes -->
	<xsl:include href="broken_freeze.xslt"  />
	<xsl:include href="output_section.xslt" />
	<xsl:include href="header.xslt"         />
	<xsl:include href="footer.xslt"         />
	<xsl:include href="../types.xslt"       />
	<xsl:include href="../urlencode.xslt"   />

	<xsl:output
	method="html"
	indent="yes"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="no" />

	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node" select="document($api_file)/api" />
	<xsl:variable name="resultcodes_node" select="document('../../xml/default_resultcodes.xml')/resultcodes" />

	<!-- Default indentation setting -->
	<xsl:variable name="indentation" select="'&amp;nbsp;&amp;nbsp;&amp;nbsp;'" />

	<xsl:preserve-space elements="examples" />

	<xsl:template match="function">

		<xsl:variable name="project_node" select="document($project_file)/project" />
		<xsl:variable name="api_node" select="document($api_file)/api" />
		<xsl:variable name="resultcodes_node" select="document('../../xml/default_resultcodes.xml')/resultcodes" />
		<xsl:variable name="function_name"    select="@name" />

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="$function_name" />
				</title>

				<meta name="generator" content="XINS" />
				<meta name="description" content="Specification of the {$function_name} function of the {$api} API." />

				<link rel="stylesheet" type="text/css" href="style.css"                                  />
				<link rel="icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">function</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Function </xsl:text>
					<em>
						<xsl:value-of select="$function_name" />
					</em>
				</h1>

				<!-- Broken freezes -->
				<xsl:call-template name="broken_freeze">
					<xsl:with-param name="project_home" select="$project_home" />
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="api" select="$api" />
					<xsl:with-param name="frozen_version" select="$api_node/function[@name=$function_name]/@freeze" />
					<xsl:with-param name="broken_file" select="concat($function_name, '.fnc')" />
				</xsl:call-template>

				<!-- Description -->
				<xsl:call-template name="description" />

				<!-- References to other functions -->
				<xsl:if test="see">
					<table class="metadata">
						<tr>
							<td class="key">See also:</td>
							<td class="value">
								<xsl:apply-templates select="see" />
							</td>
						</tr>
					</table>
				</xsl:if>

				<xsl:call-template name="input_section" />
				<xsl:call-template name="output_section" />
				<xsl:call-template name="examples_section">
					<xsl:with-param name="function_name" select="$function_name" />
				</xsl:call-template>
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="input_section">
		<h2>Input section</h2>
		<blockquote>
			<xsl:choose>
				<xsl:when test="input">
					<xsl:apply-templates select="input" />
				</xsl:when>
				<xsl:otherwise>
					<em>This function supports no input parameters.</em>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
	</xsl:template>

	<xsl:template name="output_section">
		<h2>Output section</h2>
		<blockquote>
			<xsl:call-template name="resultcodes" />
			<xsl:choose>
				<xsl:when test="output">
					<xsl:apply-templates select="output" />
				</xsl:when>
				<xsl:otherwise>
					<em>This function supports no output parameters and no data section.</em>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
	</xsl:template>

	<xsl:template name="examples_section">
		<xsl:param name="function_name" />

		<h2>Examples section</h2>
		<blockquote>
			<xsl:choose>
				<xsl:when test="example">
					<table class="example">
						<xsl:apply-templates select="example">
							<xsl:with-param name="function_name" select="$function_name" />
						</xsl:apply-templates>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<em>No examples available.</em>
				</xsl:otherwise>
			</xsl:choose>
		</blockquote>
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

	<xsl:template match="function/input">
		<xsl:call-template name="parametertable">
			<xsl:with-param name="title">Input parameters</xsl:with-param>
			<xsl:with-param name="content">input parameters</xsl:with-param>
			<xsl:with-param name="class">inputparameters</xsl:with-param>
		</xsl:call-template>
		<xsl:apply-templates select="note" />
		<xsl:call-template name="additional-constraints" />
		<xsl:call-template name="datasection">
			<xsl:with-param name="side" select="'input'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="function/example">
		<xsl:param name="function_name" />

		<xsl:variable name="examplenum">
			<xsl:choose>
				<xsl:when test="@num">
					<xsl:value-of select="@num" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="position()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="example-inputparams"  select="input-example" />
		<xsl:variable name="example-inputdata">
			<xsl:if test="input-data-example/element-example">
				<xsl:text>&lt;data&gt;</xsl:text>
				<xsl:apply-templates select="input-data-example/element-example" mode="input" />
				<xsl:text>&lt;/data&gt;</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="resultcode" select="@resultcode" />
		<!-- TODO: Check that the error code is not defined in 2 places? -->
		<!-- TODO: Check that the error code exists? -->

		<xsl:if test="string-length($resultcode) &lt; 1">
			<xsl:variable name="examplenode" select="current()" />
			<!--
			If this is an example of a successful case, then all required
			input parameters need to be set.
			-->
			<xsl:for-each select="parent::function/input/param[@required='true']">
				<xsl:variable name="required_attr" select="@name" />
				<xsl:if test="not(/function/input/param[@name=$required_attr]/example-value[@example=$examplenum]) and not($examplenode/input-example[@name=$required_attr])">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> is marked as successful, but it does not specify a value for the required input parameter '</xsl:text>
						<xsl:value-of select="$required_attr" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>
			<!--
			Same applies to output parameters.
			-->
			<xsl:for-each select="parent::function/output/param[@required='true']">
				<xsl:variable name="required_attr" select="@name" />
				<xsl:if test="not(/function/output/param[@name=$required_attr]/example-value[@example=$examplenum]) and not($examplenode/output-example[@name=$required_attr])">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> is marked as successful, but it does not specify a value for the required output parameter '</xsl:text>
						<xsl:value-of select="$required_attr" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>

			<!--
			Checks that the names set in the example element match the name
			of the input section.
			-->
			<xsl:for-each select="input-example">
				<xsl:variable name="examplename" select="@name" />
				<xsl:if test="not(boolean(/function/input/param[@name=$examplename]))">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> has a parameter named '</xsl:text>
						<xsl:value-of select="$examplename" />
						<xsl:text>' which is not defined in the input section.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>
			<!--
			Same applies to output parameters.
			-->
			<xsl:for-each select="output-example">
				<xsl:variable name="examplename" select="@name" />
				<xsl:if test="not(boolean(/function/output/param[@name=$examplename]))">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> has a parameter named '</xsl:text>
						<xsl:value-of select="$examplename" />
						<xsl:text>' which is not defined in the output section.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>

			<!--
			Same applies to required attributes.
			-->
			<xsl:for-each select="parent::function/input/data/element/attribute[@required='true']">
				<xsl:variable name="required_element" select="../@name" />
				<xsl:variable name="required_attr" select="@name" />
				<xsl:for-each select="$examplenode/input-data-example/element-example[@name=$required_element]">
					<xsl:if test="not(attribute-example[@name=$required_attr])">
						<xsl:message terminate="yes">
							<xsl:text>Example </xsl:text>
							<xsl:value-of select="$examplenum" />
							<xsl:text> is marked as successful, but it does not specify a value for the required input attribute '</xsl:text>
							<xsl:value-of select="$required_attr" />
							<xsl:text>' of the element '</xsl:text>
							<xsl:value-of select="$required_element" />
							<xsl:text>'.</xsl:text>
						</xsl:message>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>
			<!--
			Same applies to output attributes.
			-->
			<xsl:for-each select="parent::function/output/data/element/attribute[@required='true']">
				<xsl:variable name="required_element" select="../@name" />
				<xsl:variable name="required_attr" select="@name" />
				<xsl:for-each select="$examplenode/data-example/element-example[@name=$required_element] | $examplenode/output-data-example/element-example[@name=$required_element]">
					<xsl:if test="not(attribute-example[@name=$required_attr])">
						<xsl:message terminate="yes">
							<xsl:text>Example </xsl:text>
							<xsl:value-of select="$examplenum" />
							<xsl:text> is marked as successful, but it does not specify a value for the required output attribute '</xsl:text>
							<xsl:value-of select="$required_attr" />
							<xsl:text>' of the element '</xsl:text>
							<xsl:value-of select="$required_element" />
							<xsl:text>'.</xsl:text>
						</xsl:message>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>

			<!--
			Checks that the names set in the example element match the name
			of the input section.
			-->
			<xsl:for-each select="input-data-example/element-example/attribute-example">
				<xsl:variable name="element_name" select="../@name" />
				<xsl:variable name="attr_name" select="@name" />
				<xsl:if test="not(/function/input/data/element[@name=$element_name]/attribute[@name=$attr_name])">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> has a attribute named '</xsl:text>
						<xsl:value-of select="$attr_name" />
						<xsl:text>' in the element '</xsl:text>
						<xsl:value-of select="$element_name" />
						<xsl:text>' which is not defined in the input section.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>
			<!--
			Same applies to output parameters.
			-->
			<xsl:for-each select="data-example/element-example/attribute-example | output-data-example/element-example/attribute-example">
				<xsl:variable name="element_name" select="../@name" />
				<xsl:variable name="attr_name" select="@name" />
				<xsl:if test="not(/function/output/data/element[@name=$element_name]/attribute[@name=$attr_name])">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> has a attribute named '</xsl:text>
						<xsl:value-of select="$attr_name" />
						<xsl:text>' in the element '</xsl:text>
						<xsl:value-of select="$element_name" />
						<xsl:text>' which is not defined in the output section.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>

		<!--
		Same applies to error code with required output parameters.
		-->
		<xsl:if test="string-length($resultcode) &gt; 0 and not(starts-with($resultcode, '_'))">
			<xsl:variable name="rcd_file">
				<xsl:choose>
					<xsl:when test="contains(@resultcode, '/')">
						<xsl:value-of select="concat($specsdir, '/../../', substring-before(@resultcode, '/'), '/spec/', substring-after(@resultcode, '/'), '.rcd')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($specsdir, '/', $resultcode, '.rcd')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:for-each select="document($rcd_file)/output/param[@required='true']">
				<xsl:variable name="required_attr" select="@name" />
				<xsl:if test="not(boolean(/function/example[@num=$examplenum]/output-example[@name=$required_attr]))">
					<xsl:message terminate="yes">
						<xsl:text>Example </xsl:text>
						<xsl:value-of select="$examplenum" />
						<xsl:text> is marked with the error code '</xsl:text>
						<xsl:value-of select="$resultcode" />
						<xsl:text>', but it does not specify a value for the required output parameter '</xsl:text>
						<xsl:value-of select="$required_attr" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:if>
			</xsl:for-each>

			<!-- Tests also that the error code is defined correctly -->
			<xsl:if test="not($api_node/resultcode[@name=$resultcode])">
				<xsl:message terminate="yes">
					<xsl:text>The error code '</xsl:text>
					<xsl:value-of select="$resultcode" />
					<xsl:text>' defined in the example </xsl:text>
					<xsl:value-of select="$examplenum" />
					<xsl:text> is not defined in the api.xml as an error code.</xsl:text>
				</xsl:message>
			</xsl:if>
			<xsl:if test="not(/function/output/resultcode-ref[@name=$resultcode])">
				<xsl:message terminate="yes">
					<xsl:text>The error code '</xsl:text>
					<xsl:value-of select="$resultcode" />
					<xsl:text>' defined in the example </xsl:text>
					<xsl:value-of select="$examplenum" />
					<xsl:text> is not defined in the 'resultcode-ref' section.</xsl:text>
				</xsl:message>
			</xsl:if>
		</xsl:if>

		<!--
		Tests that the attributes have the correct name.
		-->

		<tr>
			<td colspan="2" class="header">
				<h3>
					<xsl:text>Example </xsl:text>
					<xsl:value-of select="$examplenum" />

					<xsl:if test="description">
						<xsl:text>: </xsl:text>
						<xsl:apply-templates select="description" />
					</xsl:if>
				</h3>
			</td>
		</tr>
		<tr>
			<th>Request:</th>
			<td>
				<span class="url">
					<span class="ellipsis">&#8230;</span>
					<xsl:text>?</xsl:text>
					<span class="functionparam">
						<span class="name">_function</span>
						<xsl:text>=</xsl:text>
						<span class="value">
							<xsl:value-of select="$function_name" />
						</span>
					</span>
					<xsl:text>&amp;</xsl:text>
					<span class="param">
						<span class="name">_convention</span>
						<xsl:text>=</xsl:text>
						<span class="value">_xins-std</span>
					</span>
					<xsl:for-each select="input-example">
						<xsl:text>&amp;</xsl:text>
						<span class="param">
							<xsl:attribute name="title">
								<xsl:value-of select="@name" />
								<xsl:text>: </xsl:text>
								<xsl:value-of select="text()" />
							</xsl:attribute>
							<span class="name">
								<xsl:value-of select="@name" />
							</span>
							<xsl:text>=</xsl:text>
							<span class="value">
								<xsl:call-template name="urlencode">
									<xsl:with-param name="text" select="text()" />
								</xsl:call-template>
							</span>
						</span>
					</xsl:for-each>
					<xsl:if test="$example-inputdata != ''">
						<xsl:text>&amp;</xsl:text>
						<span class="param">
							<xsl:attribute name="title">
								<xsl:text>Data section: </xsl:text>
								<xsl:value-of select="$example-inputdata" />
							</xsl:attribute>
							<span class="name">_data</span>
							<xsl:text>=</xsl:text>
							<span class="value">
								<xsl:call-template name="urlencode">
									<xsl:with-param name="text" select="$example-inputdata" />
								</xsl:call-template>
							</span>
						</span>
					</xsl:if>
				</span>
			</td>
		</tr>
		<tr>
			<th>Response:</th>
			<td>
				<span class="xml">
					<span class="decl">
						<xsl:text>&lt;?</xsl:text>
						<span class="elem">
							<span class="name">xml</span>
						</span>
						<xsl:text> </xsl:text>
						<span class="attr">
							<span class="name">version</span>
							<xsl:text>=</xsl:text>
							<span class="value">"1.0"</span>
						</span>
						<xsl:text> </xsl:text>
						<span class="attr">
							<span class="name">encoding</span>
							<xsl:text>=</xsl:text>
							<span class="value">"UTF-8"</span>
						</span>
						<xsl:text>?&gt;
</xsl:text>
					</span>
					<!-- The <result/> element -->
					<span class="elem">
						<xsl:text>&lt;</xsl:text>
						<span class="name">result</span>
						<xsl:if test="string-length($resultcode) &gt; 0">
							<xsl:text> </xsl:text>
							<span class="attr">
								<!-- TODO: Get error code description for referenced error codes as well -->
								<xsl:attribute name="title">
									<xsl:call-template name="firstline">
										<xsl:with-param name="text" select="parent::function/output/resultcode[@value=$resultcode]/description/text()" />
									</xsl:call-template>
								</xsl:attribute>
								<span class="name">errorcode</span>
								<xsl:text>=</xsl:text>
								<span class="value">
									<xsl:text>"</xsl:text>
									<xsl:if test="not(contains($resultcode, '/'))">
										<xsl:value-of select="$resultcode" />
									</xsl:if>
									<xsl:if test="contains($resultcode, '/')">
										<xsl:value-of select="substring-after($resultcode, '/')" />
									</xsl:if>
									<xsl:text>"</xsl:text>
								</span>
							</span>
						</xsl:if>
						<xsl:choose>
							<xsl:when test="output-example or output-data-example or data-example">
								<xsl:text>&gt;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> /&gt;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</span>
					<xsl:choose>
						<xsl:when test="output-example or output-data-example or data-example">
							<xsl:for-each select="output-example">
								<xsl:text>
</xsl:text>
								<xsl:value-of disable-output-escaping="yes" select="$indentation" />
								<span class="elem">
									<xsl:text>&lt;</xsl:text>
									<span class="name">param</span>
									<xsl:text> </xsl:text>
									<span class="attr">
										<span class="name">name</span>
										<xsl:text>=</xsl:text>
										<span class="value">
											<xsl:text>"</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>"</xsl:text>
										</span>
									</span>
									<xsl:text>&gt;</xsl:text>
								</span>
								<span class="pcdata">
									<xsl:apply-templates select="." />
								</span>
								<span class="elem">
									<xsl:text>&lt;/</xsl:text>
									<span class="name">param</span>
									<xsl:text>&gt;</xsl:text>
								</span>
							</xsl:for-each>
							<xsl:if test="output-data-example or data-example">
								<xsl:text>
</xsl:text>
								<xsl:value-of disable-output-escaping="yes" select="$indentation" />
								<span class="elem">
									<xsl:text>&lt;</xsl:text>
									<span class="name">data</span>
									<xsl:text>&gt;</xsl:text>
								</span>
								<!-- First call, use $indent to set the start value of the indent param -->
								<xsl:apply-templates select="output-data-example/element-example | data-example/element-example">
									<!-- Insert the indentation -->
									<xsl:with-param name="indent" select="concat($indentation,$indentation)" />
								</xsl:apply-templates>
								<xsl:text>
</xsl:text>
								<xsl:value-of disable-output-escaping="yes" select="$indentation" />
								<span class="elem">
									<xsl:text>&lt;/</xsl:text>
									<span class="name">data</span>
									<xsl:text>&gt;</xsl:text>
								</span>
							</xsl:if>
							<span class="elem">
								<xsl:text>
&lt;/</xsl:text>
								<span class="name">result</span>
								<xsl:text>&gt;</xsl:text>
							</span>
						</xsl:when>
					</xsl:choose>
				</span>
			</td>
		</tr>
		<xsl:if test="$project_node/api[@name = $api]/environments">
			<tr>
				<th>Test on:</th>
				<td>
					<xsl:if test="$project_node/api[@name = $api]/environments">
						<xsl:variable name="env_file" select="concat($apis_dir, '/', $api, '/environments.xml')" />
						<xsl:for-each select="document($env_file)/environments/environment">
							<a>
								<xsl:attribute name="href">
									<xsl:value-of select="@url" />
									<xsl:text>?_function=</xsl:text>
									<xsl:value-of select="$function_name" />
									<xsl:text>&amp;amp;_convention=_xins-std</xsl:text>
									<xsl:for-each select="$example-inputparams">
										<xsl:text>&amp;amp;</xsl:text>
										<xsl:value-of select="@name" />
										<xsl:text>=</xsl:text>
										<xsl:call-template name="urlencode">
											<xsl:with-param name="text" select="text()" />
										</xsl:call-template>
									</xsl:for-each>
									<xsl:if test="$example-inputdata != ''">
										<xsl:text>&amp;amp;_data=</xsl:text>
										<xsl:call-template name="urlencode">
											<xsl:with-param name="text" select="$example-inputdata" />
										</xsl:call-template>
									</xsl:if>
								</xsl:attribute>

								<xsl:value-of select="@id" />
							</a>
							<xsl:text> </xsl:text>
						</xsl:for-each>
					</xsl:if>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

<!-- element examples -->

	<xsl:template match="element-example">
		<xsl:param name="indent" />

		<xsl:variable name="text" select="pcdata-example/text()" />

		<xsl:text>
</xsl:text>
		<xsl:value-of disable-output-escaping="yes" select="$indent" />
		<span class="elem">
			<xsl:text>&lt;</xsl:text>
			<span class="name">
				<xsl:value-of select="@name" />
			</span>
			<xsl:apply-templates select="attribute-example" />
			<xsl:if test="not(element-example) and not(boolean($text) and not($text = ''))">
				<xsl:text> /</xsl:text>
			</xsl:if>
			<xsl:text>&gt;</xsl:text>
		</span>

		<xsl:if test="boolean(element-example) and boolean($text) and not($text = '')">
			<xsl:message terminate="yes">
				<xsl:text>Mixed content of sub-elements and PCDATA is currently not supported in element-examples.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="boolean($text) and not($text = '')">
			<xsl:value-of select="$text" />
		</xsl:if>

		<xsl:apply-templates select="element-example">
			<xsl:with-param name="indent" select="concat($indentation,$indent)" />
		</xsl:apply-templates>

		<xsl:if test="boolean(element-example)">
			<xsl:text>
</xsl:text>
			<xsl:value-of disable-output-escaping="yes" select="$indent" />
		</xsl:if>

		<xsl:if test="boolean(element-example) or (boolean($text) and not($text=''))">
			<span class="elem">
				<xsl:text>&lt;/</xsl:text>
				<span class="name">
					<xsl:value-of select="@name" />
				</span>
				<xsl:text>&gt;</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="attribute-example">
		<xsl:variable name="name" select="@name" />
		<xsl:if test="not(count(parent::*/attribute-example[@name = $name]) = 1)">
			<xsl:message terminate="yes">
				<xsl:text>There are </xsl:text>
				<xsl:value-of select="count(parent::*/attribute-example[@name = $name])" />
				<xsl:text> attribute-example tags for the element '</xsl:text>
				<xsl:value-of select="parent::*/@name" />
				<xsl:text>' that have the same attribute name '</xsl:text>
				<xsl:value-of select="$name" />
				<xsl:text>' while there can be only one.</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:text> </xsl:text>
		<span class="attr">
			<span class="name">
				<xsl:value-of select="@name" />
			</span>
			<xsl:text>=</xsl:text>
			<span class="value">
				<xsl:text>"</xsl:text>
				<xsl:value-of select="text()" />
				<xsl:text>"</xsl:text>
			</span>
		</span>
	</xsl:template>

	<xsl:template match="element-example" mode="input">

		<xsl:variable name="text" select="pcdata-example/text()" />

		<xsl:text>&lt;</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:apply-templates select="attribute-example" mode="input" />
		<xsl:if test="not(element-example) and not(boolean($text) and not($text = ''))">
			<xsl:text> /</xsl:text>
		</xsl:if>
		<xsl:text>&gt;</xsl:text>

		<xsl:if test="boolean(element-example) and boolean($text) and not($text = '')">
			<xsl:message terminate="yes">
				<xsl:text>Mixed content of sub-elements and PCDATA is currently not supported in element-examples.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:if test="boolean($text) and not($text = '')">
			<xsl:value-of select="$text" />
		</xsl:if>

		<xsl:apply-templates select="element-example" mode="input" />

		<xsl:if test="boolean(element-example) or (boolean($text) and not($text=''))">
			<span class="elem">
				<xsl:text>&lt;/</xsl:text>
				<span class="name">
					<xsl:value-of select="@name" />
				</span>
				<xsl:text>&gt;</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="attribute-example" mode="input">
		<xsl:variable name="name" select="@name" />
		<xsl:if test="not(count(parent::*/attribute-example[@name = $name]) = 1)">
			<xsl:message terminate="yes">
				<xsl:text>There are </xsl:text>
				<xsl:value-of select="count(parent::*/attribute-example[@name = $name])" />
				<xsl:text> attribute-example tags for the element '</xsl:text>
				<xsl:value-of select="parent::*/@name" />
				<xsl:text>' that have the same attribute name '</xsl:text>
				<xsl:value-of select="$name" />
				<xsl:text>' while there can be only one.</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>="</xsl:text>
		<xsl:value-of select="text()" />
		<xsl:text>"</xsl:text>
	</xsl:template>
<!-- end -->

	<xsl:template match="function/output">
		<xsl:call-template name="parametertable">
			<xsl:with-param name="title">Output parameters</xsl:with-param>
			<xsl:with-param name="content">output parameters</xsl:with-param>
			<xsl:with-param name="class">outputparameters</xsl:with-param>
		</xsl:call-template>

		<xsl:call-template name="additional-constraints" />
		<xsl:call-template name="datasection">
			<xsl:with-param name="side" select="'output'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="datasection">
		<xsl:param name="side" />

		<h3>Data section</h3>
		<xsl:choose>
			<xsl:when test="count(data/contains/contained) &gt; 1">
				<p>
					<em>The data section may contain the elements </em>
					<xsl:for-each select="data/contains/contained">
						<xsl:choose>
							<xsl:when test="position() = last()">
								<em> and </em>
							</xsl:when>
							<xsl:when test="position() != 1">
								<xsl:text>, </xsl:text>
							</xsl:when>
						</xsl:choose>
						<code>
							<xsl:text>&lt;</xsl:text>
							<xsl:value-of select="@element" />
							<xsl:text>/&gt;</xsl:text>
						</code>
					</xsl:for-each>
					<xsl:text>.</xsl:text>
				</p>
				<xsl:apply-templates select="data/element" />
			</xsl:when>
			<xsl:when test="count(data/contains/contained) = 1">
				<p>
					<em>The data section may only contain the element </em>
					<code>
						<xsl:text>&lt;</xsl:text>
						<xsl:value-of select="data/contains/contained/@element" />
						<xsl:text>/&gt;</xsl:text>
					</code>
					<xsl:text>.</xsl:text>
				</p>
				<xsl:apply-templates select="data/element" />
			</xsl:when>
			<xsl:when test="data/@contains">
				<p>
					<em>The data section may only contain the element </em>
					<code>
						<xsl:text>&lt;</xsl:text>
						<xsl:value-of select="data/@contains" />
						<xsl:text>/&gt;</xsl:text>
					</code>
					<xsl:text>.</xsl:text>
				</p>
				<xsl:apply-templates select="data/element" />
			</xsl:when>
			<xsl:otherwise>
				<p>
					<em>
						<xsl:text>This function defines no </xsl:text>
						<xsl:value-of select="$side" />
						<xsl:text> data section.</xsl:text>
					</em>
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="resultcodes">
		<h3>Error codes</h3>
		<em>An error code is returned when an error occurs during the execution of the implementation.</em>
		<table class="resultcodes">
			<tr>
				<th>Name</th>
				<th>Description</th>
			</tr>
			<xsl:call-template name="default_resultcodes" />
			<xsl:call-template name="referenced_resultcodes" />
		</table>
	</xsl:template>

	<xsl:template name="referenced_resultcodes">
		<xsl:for-each select="//function/output/resultcode-ref">
			<xsl:variable name="resultcode" select="@name" />
			<xsl:variable name="rcd_file">
				<xsl:choose>
					<xsl:when test="contains(@name, '/')">
						<xsl:value-of select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($specsdir, '/', @name, '.rcd')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="rcd_node" select="document($rcd_file)/resultcode" />

			<xsl:if test="not($api_node/resultcode[@name=$resultcode])">
				<xsl:message terminate="yes">
					<xsl:text>The error code '</xsl:text>
					<xsl:value-of select="$resultcode" />
					<xsl:text>' referenced in the </xsl:text>
					<xsl:value-of select="//function/@name" />
					<xsl:text> is not defined in api.xml.</xsl:text>
				</xsl:message>
			</xsl:if>

			<tr>
				<td class="value">
					<a href="{$rcd_node/@name}.html">
						<xsl:value-of select="$rcd_node/@name" />
					</a>
				</td>
				<td class="description">
					<xsl:apply-templates select="$rcd_node/description" />
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="default_resultcodes">
		<xsl:variable name="haveParams" select="boolean(//function/input/param)" />

		<xsl:for-each select="$resultcodes_node/code">
			<xsl:choose>
				<xsl:when test="@value = 'MissingFunctionName'" />
				<xsl:when test="@value = 'NoSuchFunction'"      />
				<xsl:when test="not($haveParams) and @onlyIfInputParameters = 'true'" />
				<xsl:otherwise>
					<xsl:call-template name="default_resultcode">
						<xsl:with-param name="value"       select="@value" />
						<xsl:with-param name="description" select="description/text()" />
					</xsl:call-template>
			   </xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="default_resultcode">
		<xsl:param name="value" />
		<xsl:param name="description" />

		<tr class="default">
			<td class="value">
				<span title="This error code is generic, not specific to this API">
					<xsl:value-of select="$value" />
				</span>
			</td>
			<td class="description">
				<xsl:value-of select="$description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="resultcode">
		<tr>
			<td class="value">
				<xsl:value-of select="@value" />
			</td>
			<td class="description">
				<xsl:apply-templates select="description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="function-ref[@name]">
		<xsl:variable name="reffunction" select="@name" />
		<xsl:variable name="reffunction_file" select="concat($specsdir, '/', $reffunction, '.fnc')" />
		<a href="{$reffunction}.html">
			<xsl:attribute name="title">
				<xsl:call-template name="firstline">
					<xsl:with-param name="text" select="document($reffunction_file)/function/description/text()" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:value-of select="$reffunction" />
		</a>
	</xsl:template>

</xsl:stylesheet>
