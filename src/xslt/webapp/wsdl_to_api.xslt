<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the XINS API from the WSDL.

 $Id: wsdl_to_api.xslt,v 1.27 2007/08/13 13:14:31 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:xt="http://www.jclarck.com/xt"
                xmlns:xalan="http://org.apache.xalan.xslt.extensions.Redirect"
								xmlns:redirect="http://xml.apache.org/xalan/redirect"
								xmlns:xsltc="http://xml.apache.org/xalan/xsltc"
								extension-element-prefixes="saxon xt xalan redirect xsltc"
                exclude-result-prefixes="xs xsd saxon xt xalan redirect xsltc"
                version="2.0">

	<xsl:include href="../types.xslt" />
	<xsl:include href="xsd_to_types.xslt" />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api_name"     />

	<!-- variables used for correct indentation in the generated files -->
	<xsl:variable name="return">
		<xsl:text>
</xsl:text>
	</xsl:variable>
	<xsl:variable name="tab"><xsl:text>	</xsl:text></xsl:variable>
	<xsl:variable name="tab4"><xsl:text>				</xsl:text></xsl:variable>

	<!-- keys are defined to avoid duplicated functions or error codes -->
	<xsl:key name="operationnames" match="operation" use="@name" />
	<xsl:key name="faultnames" match="fault" use="@name" />

	<!-- Creates the different files -->
	<xsl:template match="definitions">

		<!-- The api.xml file -->
		<xsl:call-template name="apifile">
			<xsl:with-param name="api_name" select="$api_name" />
		</xsl:call-template>

		<!-- The function files -->
		<xsl:apply-templates select="portType/operation[generate-id() = generate-id(key('operationnames', @name))]" />

		<!-- The types -->
		<xsl:apply-templates select="types/xsd:schema//xsd:simpleType/xsd:restriction" mode="restriction" />
		<xsl:apply-templates select="types/xsd:schema//xsd:simpleType/xsd:list" mode="restriction" />
		<xsl:apply-templates select="types/xsd:schema//xsd:element[@maxOccurs='unbounded' and not(@type='xsd:string')]" mode="list" />

		<!-- The errors -->
		<xsl:apply-templates select="portType/operation/fault[generate-id() = generate-id(key('faultnames', @name))]">
			<xsl:sort select="@name" />
		</xsl:apply-templates>
	</xsl:template>

	<!-- Creates the api.xml file -->
	<xsl:template name="apifile">
		<xsl:param name="api_name" />

		<xsl:if test="not(element-available('xalan:write'))">
			<xsl:text>
</xsl:text>
		</xsl:if>
		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE api PUBLIC "-//XINS//DTD API 2.0//EN" "http://www.xins.org/dtd/api_2_0.dtd">]]>

</xsl:text>
		<api name="{$api_name}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
			<xsl:value-of select="concat($return, $return, $tab)" />
			<!-- The description of the API -->
			<description>
				<xsl:choose>
					<xsl:when test="service/documentation/text()">
						<xsl:value-of select="service/documentation/text()" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>The </xsl:text>
						<xsl:value-of select="$api_name" />
						<xsl:text> API.</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</description>
			<xsl:value-of select="concat($return, $return, $tab)" />

			<!-- The list of the functions -->
			<xsl:for-each select="portType/operation[generate-id() = generate-id(key('operationnames', @name))]">
				<xsl:variable name="functionName">
					<xsl:call-template name="hungarianUpper">
						<xsl:with-param name="text" select="@name" />
					</xsl:call-template>
				</xsl:variable>
				<function name="{$functionName}" />
				<xsl:if test="position() != last()">
					<xsl:value-of select="concat($return, $tab)" />
				</xsl:if>
			</xsl:for-each>

			<!-- The list of the defined types -->
			<xsl:if test="types/xsd:schema//xsd:simpleType">
				<xsl:value-of select="concat($return, $return, $tab)" />
			</xsl:if>
			<xsl:for-each select="types/xsd:schema//xsd:simpleType">
				<xsl:sort select="@name" />
				<xsl:variable name="typeName">
					<xsl:call-template name="hungarianUpper">
						<xsl:with-param name="text">
							<xsl:choose>
								<xsl:when test="not(@name) and ../../xsd:list">
									<xsl:value-of select="../../@name" />
									<xsl:text>ListItem</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@name" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<type name="{$typeName}" />
				<xsl:if test="position() != last()">
					<xsl:value-of select="concat($return, $tab)" />
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="types/xsd:schema//xsd:element[@maxOccurs='unbounded' and not(@type='xsd:string')]">
				<xsl:variable name="type" select="@type" />
				<xsl:variable name="localNameType">
					<xsl:call-template name="localname">
						<xsl:with-param name="text" select="@type" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:if test="(starts-with(@type, 'xsd:') or key('simpletypenames', $localNameType)) and not(preceding::xsd:element[@maxOccurs='unbounded' and @type = $type])">
					<xsl:variable name="typeName">
						<xsl:call-template name="hungarianUpper">
							<xsl:with-param name="text" select="$localNameType" />
						</xsl:call-template>
						<xsl:text>List</xsl:text>
					</xsl:variable>
					<xsl:value-of select="concat($return, $tab)" />
					<type name="{$typeName}" />
				</xsl:if>
			</xsl:for-each>

			<!-- The list of the possible error codes -->
			<xsl:if test="portType/operation/fault">
				<xsl:value-of select="concat($return, $return, $tab)" />
			</xsl:if>
			<xsl:for-each select="portType/operation/fault[generate-id() = generate-id(key('faultnames', @name))]">
				<xsl:sort select="@name" />
				<xsl:variable name="errorcodeName">
					<xsl:call-template name="hungarianUpper">
						<xsl:with-param name="text" select="@name" />
					</xsl:call-template>
				</xsl:variable>
				<resultcode name="{$errorcodeName}" />
				<xsl:if test="position() != last()">
					<xsl:value-of select="concat($return, $tab)" />
				</xsl:if>
			</xsl:for-each>
			<xsl:value-of select="$return" />
		</api>
	</xsl:template>

	<!-- Creates the function files (.fnc) -->
	<xsl:template match="portType/operation">
		<xsl:variable name="functionName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="functionFile" select="concat($specsdir, '/', $functionName, '.fnc')" />

		<xsltc:output file="{$functionFile}">
			<xsl:call-template name="functionfile">
				<xsl:with-param name="functionName" select="$functionName" />
			</xsl:call-template>
			<xsl:fallback />
		</xsltc:output>
		<xalan:write file="{$functionFile}">
			<xsl:call-template name="functionfile">
				<xsl:with-param name="functionName" select="$functionName" />
			</xsl:call-template>
			<xsl:fallback />
		</xalan:write>
		<saxon:output file="{$functionFile}">
			<xsl:call-template name="functionfile">
				<xsl:with-param name="functionName" select="$functionName" />
			</xsl:call-template>
			<xsl:fallback />
		</saxon:output>
		<xt:document href="{$functionFile}">
			<xsl:call-template name="functionfile">
				<xsl:with-param name="functionName" select="$functionName" />
			</xsl:call-template>
			<xsl:fallback />
		</xt:document>
	</xsl:template>

	<!-- The content for the function files (.fnc) -->
	<xsl:template name="functionfile">
		<xsl:param name="functionName" />

		<xsl:if test="not(element-available('xalan:write'))">
			<xsl:text>
</xsl:text>
		</xsl:if>
		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE function PUBLIC "-//XINS//DTD Function 2.0//EN" "http://www.xins.org/dtd/function_2_0.dtd">]]>

</xsl:text>
		<function rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$" name="{$functionName}">
			<xsl:value-of select="concat($return, $return, $tab)" />
			<description>
				<xsl:choose>
					<xsl:when test="documentation">
						<xsl:value-of select="documentation/text()" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>The </xsl:text>
						<xsl:value-of select="$functionName" />
						<xsl:text> function.</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</description>
			<xsl:value-of select="concat($return, $return, $tab)" />
			<input>
				<xsl:apply-templates select="input" mode="section" />
				<xsl:value-of select="concat($return, $tab)" />
			</input>
			<xsl:value-of select="concat($return, $return, $tab)" />
			<output>
				<xsl:apply-templates select="fault" mode="reference">
					<xsl:sort select="@name" />
				</xsl:apply-templates>
				<xsl:if test="fault">
					<xsl:value-of select="$return" />
				</xsl:if>
				<xsl:apply-templates select="output" mode="section" />
				<xsl:value-of select="concat($return, $tab)" />
			</output>
			<xsl:value-of select="$return" />
		</function>
	</xsl:template>

	<!-- The resultcode-ref section in the functions -->
	<xsl:template match="fault" mode="reference">
		<xsl:variable name="errorcodeName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:value-of select="concat($return, $tab, $tab)" />
		<resultcode-ref name="{$errorcodeName}" />
	</xsl:template>

	<!-- Creates the error code files (.rcd) -->
	<xsl:template match="fault">
		<xsl:variable name="errorcodeName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="errorcodeFile" select="concat($specsdir, '/', $errorcodeName, '.rcd')" />

		<xsltc:output file="{$errorcodeFile}">
			<xsl:call-template name="errorcodefile">
				<xsl:with-param name="errorcodeName" select="$errorcodeName" />
			</xsl:call-template>
			<xsl:fallback />
		</xsltc:output>
		<xalan:write file="{$errorcodeFile}">
			<xsl:call-template name="errorcodefile">
				<xsl:with-param name="errorcodeName" select="$errorcodeName" />
			</xsl:call-template>
			<xsl:fallback />
		</xalan:write>
		<saxon:output file="{$errorcodeFile}">
			<xsl:call-template name="errorcodefile">
				<xsl:with-param name="errorcodeName" select="$errorcodeName" />
			</xsl:call-template>
			<xsl:fallback />
		</saxon:output>
		<xt:document href="{$errorcodeFile}">
			<xsl:call-template name="errorcodefile">
				<xsl:with-param name="errorcodeName" select="$errorcodeName" />
			</xsl:call-template>
			<xsl:fallback />
		</xt:document>
	</xsl:template>

	<!-- The content for the error code file (.rcd) -->
	<xsl:template name="errorcodefile">
		<xsl:param name="errorcodeName" />
		<xsl:if test="not(element-available('xalan:write'))">
			<xsl:text>
</xsl:text>
		</xsl:if>
		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE resultcode PUBLIC "-//XINS//DTD Result Code 2.0//EN" "http://www.xins.org/dtd/resultcode_2_0.dtd">]]>

</xsl:text>
		<resultcode rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$" name="{$errorcodeName}">
			<xsl:value-of select="concat($return, $tab)" />
			<description>
				<xsl:choose>
					<xsl:when test="documentation">
						<xsl:value-of select="documentation/text()" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>The </xsl:text>
						<xsl:value-of select="$errorcodeName" />
						<xsl:text> error code.</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</description>
			<xsl:variable name="output_section">
				<xsl:apply-templates select="." mode="section" />
			</xsl:variable>
			<xsl:if test="string-length($output_section) &gt; 0">
				<xsl:value-of select="concat($return, $return, $tab)" />
				<output>
					<xsl:apply-templates select="." mode="section" />
					<xsl:value-of select="concat($return, $tab)" />
				</output>
			</xsl:if>
			<xsl:value-of select="$return" />
		</resultcode>
	</xsl:template>

	<!-- Fills in input or output section -->
	<xsl:template match="input | output | fault" mode="section">
		<xsl:variable name="section" select="local-name()" />
		<xsl:variable name="message">
			<xsl:call-template name="localname">
				<xsl:with-param name="text" select="@message" />
			</xsl:call-template>
		</xsl:variable>

		<!-- The parameters -->
		<xsl:for-each select="/definitions/message[@name=$message]/part">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:call-template name="printParam">
						<xsl:with-param name="section" select="$section" />
						<xsl:with-param name="elementName" select="'param'" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="@element">
					<xsl:variable name="messageElement">
						<xsl:call-template name="localname">
							<xsl:with-param name="text" select="@element" />
						</xsl:call-template>
					</xsl:variable>
					<!-- unbounded elements that points to complexType are put in the data section -->
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$messageElement]/xsd:complexType//xsd:sequence/xsd:element[not(@maxOccurs='unbounded') or starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':'))]">
						<xsl:with-param name="section" select="$section" />
					</xsl:apply-templates>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$messageElement]//xsd:sequence/xsd:element[not(@maxOccurs='unbounded') or starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':'))]">
						<xsl:with-param name="section" select="$section" />
					</xsl:apply-templates>
				</xsl:when>
				<!-- otherwise no section -->
			</xsl:choose>
		</xsl:for-each>
		
		<!-- data section -->
		<!-- Is there any data section -->
		<xsl:variable name="dataSectionDefined">
			<xsl:for-each select="/definitions/message[@name=$message]/part">
				<xsl:if test="@element">
					<xsl:variable name="messageElement">
						<xsl:call-template name="localname">
							<xsl:with-param name="text" select="@element" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:call-template name="dataSectionDefined">
						<xsl:with-param name="messageElement" select="$messageElement" />
					</xsl:call-template>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="starts-with($dataSectionDefined, 'true')">
			<xsl:value-of select="concat($return, $tab, $tab)" />
			<data>
			<!-- Prints the contains part -->
			<xsl:value-of select="concat($return, $tab, $tab, $tab)" />
			<contains>
				<xsl:for-each select="/definitions/message[@name=$message]/part">
					<xsl:if test="@element">
						<xsl:variable name="messageElement">
							<xsl:call-template name="localname">
								<xsl:with-param name="text" select="@element" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$messageElement]/xsd:complexType//xsd:sequence/xsd:element" mode="contained" />
						<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$messageElement]//xsd:sequence/xsd:element" mode="contained" />
					</xsl:if>
				</xsl:for-each>
			<xsl:value-of select="concat($return, $tab, $tab, $tab)" />
			</contains>
			<!-- Prints the elements -->
			<xsl:for-each select="/definitions/message[@name=$message]/part">
				<xsl:if test="@element">
					<xsl:variable name="messageElement">
						<xsl:call-template name="localname">
							<xsl:with-param name="text" select="@element" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$messageElement]/xsd:complexType//xsd:sequence/xsd:element" mode="dataSectionElement">
						<xsl:with-param name="section" select="$section" />
					</xsl:apply-templates>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$messageElement]//xsd:sequence/xsd:element" mode="dataSectionElement">
						<xsl:with-param name="section" select="$section" />
					</xsl:apply-templates>
				</xsl:if>
			</xsl:for-each>
			<xsl:value-of select="concat($return, $tab, $tab)" />
			</data>
		</xsl:if>
	</xsl:template>

	<!-- Found an element for a parameter or an attribute -->
	<xsl:template match="xsd:element">
		<xsl:param name="section" />
		<xsl:param name="elementName" select="'param'" />
		<xsl:param name="prefix" select="''" />

		<xsl:variable name="localNameType">
			<xsl:call-template name="localname">
				<xsl:with-param name="text" select="@type" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="type">
			<xsl:call-template name="paramType">
				<xsl:with-param name="localNameType" select="$localNameType" />
			</xsl:call-template>
		</xsl:variable>

		<!-- If the type of the element is a complexType, call this method recursively -->
		<!-- otherwise create the parameter or the attribute. -->
    <xsl:choose>
			<xsl:when test="not(starts-with(@type, 'xsd:')) and not(key('simpletypenames', $localNameType))">
				<xsl:variable name="new-prefix">
					<xsl:value-of select="$prefix" />
					<xsl:if test="count(../xsd:element) &gt; 1">
						<xsl:call-template name="smartHungarianLower">
							<xsl:with-param name="text" select="@name" />
						</xsl:call-template>
						<xsl:text>.</xsl:text>
					</xsl:if>
				</xsl:variable>
				<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$localNameType]/xsd:complexType//xsd:sequence/xsd:element[not(@maxOccurs='unbounded') or starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':'))]">
					<xsl:with-param name="section" select="$section" />
					<xsl:with-param name="elementName" select="$elementName" />
					<xsl:with-param name="prefix" select="$new-prefix" />
				</xsl:apply-templates>
				<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$localNameType]//xsd:sequence/xsd:element[not(@maxOccurs='unbounded') or starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':'))]">
					<xsl:with-param name="section" select="$section" />
					<xsl:with-param name="elementName" select="$elementName" />
					<xsl:with-param name="prefix" select="$new-prefix" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="printParam">
					<xsl:with-param name="section" select="$section" />
					<xsl:with-param name="elementName" select="$elementName" />
					<xsl:with-param name="prefix" select="$prefix" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- print the parameter or the attribute -->
	<xsl:template name="printParam">
		<xsl:param name="section" />
		<xsl:param name="elementName" />
		<xsl:param name="prefix" select="''" />

		<xsl:variable name="paramName">
			<xsl:value-of select="$prefix" />
			<xsl:call-template name="smartHungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="@minOccurs = '0' or @nillable = 'true'">false</xsl:when>
				<xsl:otherwise>true</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="localNameType">
			<xsl:call-template name="localname">
				<xsl:with-param name="text" select="@type" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="type">
			<xsl:call-template name="paramType">
				<xsl:with-param name="localNameType" select="$localNameType" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:value-of select="concat($return, $tab, $tab)" />
		<xsl:if test="$elementName = 'attribute'">
			<xsl:value-of select="concat($tab, $tab)" />
		</xsl:if>
		<xsl:element name="{$elementName}">
			<xsl:attribute name="name">
				<xsl:value-of select="$paramName" />
			</xsl:attribute>
			<xsl:attribute name="required">
				<xsl:value-of select="$required" />
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:if test="@default and $required='false'">
				<xsl:attribute name="default">
					<xsl:value-of select="@default" />
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="concat($return, $tab, $tab, $tab)" />
			<xsl:if test="$elementName = 'attribute'">
				<xsl:value-of select="concat($tab, $tab)" />
			</xsl:if>
			<description>
				<xsl:choose>
					<xsl:when test="xsd:annotation/xsd:documenation">
						<xsl:value-of select="xsd:annotation/xsd:documenation/text()" />
					</xsl:when>
					<xsl:when test="not(xsd:annotation/xsd:documenation) and $elementName = 'attribute'">
						<xsl:value-of select="concat($paramName, ' attribute.')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($paramName, ' ', $section, ' parameter.')" />
					</xsl:otherwise>
				</xsl:choose>
			</description>
			<xsl:value-of select="concat($return, $tab, $tab)" />
			<xsl:if test="$elementName = 'attribute'">
				<xsl:value-of select="concat($tab, $tab)" />
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- Determine the xins type to use for the type defined in the WSDL -->
	<xsl:template name="paramType">
		<xsl:param name="localNameType" />

		<xsl:choose>
			<xsl:when test="@maxOccurs='unbounded' and @type='xsd:string'">
				<xsl:text>_list</xsl:text>
			</xsl:when>
			<xsl:when test="@maxOccurs='unbounded' and not(@type='xsd:string') and (starts-with(@type, 'xsd:') or key('simpletypenames', $localNameType))">
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text" select="$localNameType" />
				</xsl:call-template>
				<xsl:text>List</xsl:text>
			</xsl:when>
			<xsl:when test="starts-with(@type, 'xsd:') and not(@maxOccurs='unbounded')">
				<xsl:call-template name="type_for_xsdtype">
					<xsl:with-param name="xsdtype" select="$localNameType" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$localNameType" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Returns a word starting with 'true' if at least on of the elements is unbounded -->
	<xsl:template name="dataSectionDefined">
		<xsl:param name="messageElement" />

		<xsl:choose>
			<xsl:when test="/definitions/types/xsd:schema/xsd:element[@name=$messageElement]/xsd:complexType//xsd:sequence/xsd:element[@maxOccurs='unbounded' and not(starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':')))] or /definitions/types/xsd:schema/xsd:complexType[@name=$messageElement]//xsd:sequence/xsd:element[@maxOccurs='unbounded' and not(starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':')))]">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<!-- TODO comment as it may turn to a loop -->
				<xsl:for-each select="/definitions/types/xsd:schema/xsd:element[@name=$messageElement]/xsd:complexType//xsd:sequence/xsd:element | /definitions/types/xsd:schema/xsd:complexType[@name=$messageElement]//xsd:sequence/xsd:element">
					<xsl:if test="@type and not(starts-with(@type, 'xsd:'))">
						<xsl:variable name="localNameType">
							<xsl:call-template name="localname">
								<xsl:with-param name="text" select="@type" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:if test="not(key('simpletypenames', $localNameType))">
							<xsl:call-template name="dataSectionDefined">
								<xsl:with-param name="messageElement" select="$localNameType" />
							</xsl:call-template>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Prints the contains part for the elements or data section -->
	<xsl:template match="xsd:element" mode="contained">
		<xsl:choose>
			<xsl:when test="@maxOccurs='unbounded' and not(starts-with(@type, 'xsd:'))">
				<xsl:variable name="localNameType">
					<xsl:call-template name="localname">
						<xsl:with-param name="text" select="@type" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="elementName">
					<xsl:call-template name="smartHungarianLower">
						<xsl:with-param name="text" select="@name" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:if test="not(key('simpletypenames', $localNameType))">
					<xsl:value-of select="concat($return, $tab4)" />
					<contained element="{$elementName}" />
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="@type">
					<xsl:variable name="localNameType">
						<xsl:call-template name="localname">
							<xsl:with-param name="text" select="@type" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$localNameType]/xsd:complexType//xsd:sequence/xsd:element" mode="contained" />
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$localNameType]//xsd:sequence/xsd:element" mode="contained" />
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Prints the data section elements -->
	<xsl:template match="xsd:element" mode="dataSectionElement">
		<xsl:param name="section" />

		<xsl:choose>
			<xsl:when test="@maxOccurs='unbounded' and not(starts-with(@type, 'xsd:') or key('simpletypenames', @type) or key('simpletypenames', substring-after(@type, ':')))">
				<xsl:variable name="localNameType">
					<xsl:call-template name="localname">
						<xsl:with-param name="text" select="@type" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="elementName">
					<xsl:call-template name="smartHungarianLower">
						<xsl:with-param name="text" select="@name" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:value-of select="concat($return, $tab, $tab, $tab)" />
				<element name="{$elementName}">
					<xsl:value-of select="concat($return, $tab4)" />
					<description>
						<xsl:choose>
							<xsl:when test="xsd:annotation/xsd:documenation">
								<xsl:value-of select="xsd:annotation/xsd:documenation/text()" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat($elementName, ' ', $section, ' element.')" />
							</xsl:otherwise>
						</xsl:choose>
					</description>
					<xsl:variable name="containsElements">
						<xsl:call-template name="dataSectionDefined">
							<xsl:with-param name="messageElement" select="$localNameType" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="starts-with(containsElements, 'true')">
						<xsl:value-of select="concat($return, $tab4)" />
						<contains>
							<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$localNameType]/xsd:complexType//xsd:sequence/xsd:element" mode="contained" />
							<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$localNameType]//xsd:sequence/xsd:element" mode="contained" />
						<xsl:value-of select="concat($return, $tab4)" />
						</contains>
					</xsl:if>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$localNameType]/xsd:complexType//xsd:sequence/xsd:element">
						<xsl:with-param name="section" select="$section" />
						<xsl:with-param name="elementName" select="'attribute'" />
					</xsl:apply-templates>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$localNameType]//xsd:sequence/xsd:element">
						<xsl:with-param name="section" select="$section" />
						<xsl:with-param name="elementName" select="'attribute'" />
					</xsl:apply-templates>
				<xsl:value-of select="concat($return, $tab, $tab, $tab)" />
				</element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="@type and not(starts-with(@type, 'xsd:'))">
					<xsl:variable name="localNameType">
						<xsl:call-template name="localname">
							<xsl:with-param name="text" select="@type" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:element[@name=$localNameType]/xsd:complexType//xsd:sequence/xsd:element" mode="dataSectionElement">
						<xsl:with-param name="section" select="$section" />
					</xsl:apply-templates>
					<xsl:apply-templates select="/definitions/types/xsd:schema/xsd:complexType[@name=$localNameType]//xsd:sequence/xsd:element" mode="dataSectionElement">
						<xsl:with-param name="section" select="$section" />
					</xsl:apply-templates>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
