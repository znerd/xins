<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the typ files from a XSD file.

 $Id: xsd_to_types.xslt,v 1.15 2007/08/13 13:14:31 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:xt="http://www.jclarck.com/xt"
                xmlns:xalan="http://org.apache.xalan.xslt.extensions.Redirect"
								xmlns:xsltc="http://xml.apache.org/xalan/xsltc"
								extension-element-prefixes="saxon xalan xt xsltc"
                exclude-result-prefixes="xs saxon xt xalan xsltc"
                version="2.0">

	<xsl:key name="simpletypenames" match="xs:simpleType | xsd:simpleType" use="@name" />

	<!--
	Creates the file
	-->
	<xsl:template match="xs:restriction | xsd:restriction | xs:list | xsd:list" mode="restriction">
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="../@name">
					<xsl:value-of select="../@name" />
				</xsl:when>
				<xsl:when test="not(../@name) and ../../@name">
					<xsl:value-of select="../../@name" />
				</xsl:when>
				<xsl:when test="not(../@name) and not(../../@name) and local-name(../..) = 'list' and ../../../@name">
					<xsl:value-of select="../../../@name" />
					<xsl:text>ListItem</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>Type</xsl:text>
					<xsl:value-of select="position()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="typeName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="$elementName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="create-type">
			<xsl:with-param name="elementName" select="$elementName" />
			<xsl:with-param name="typeName" select="$typeName" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="create-type">
		<xsl:param name="elementName" />
		<xsl:param name="typeName" />

		<xsl:variable name="typeFile" select="concat($specsdir, '/', $typeName, '.typ')" />
		<xalan:write file="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</xalan:write>
		<xsltc:output file="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</xsltc:output>
		<saxon:output file="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</saxon:output>
		<xt:document href="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</xt:document>
	</xsl:template>

	<xsl:template name="xml_type">
		<xsl:param name="typeName" />
		<xsl:param name="elementName" />

		<xsl:if test="not(element-available('xalan:write'))">
			<xsl:text>
</xsl:text>
		</xsl:if>
		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE type PUBLIC "-//XINS//DTD Type 2.0//EN" "http://www.xins.org/dtd/type_2_0.dtd">]]>

</xsl:text>
<type rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$" name="{$typeName}">
	<xsl:text>
	</xsl:text>
	<description>
		<xsl:choose>
			<xsl:when test="../xs:annotation/xs:documentation">
				<xsl:value-of select="../xs:annotation/xs:documentation/text()" />
			</xsl:when>
			<xsl:when test="../xsd:annotation/xsd:documentation">
				<xsl:value-of select="../xsd:annotation/xsd:documentation/text()" />
			</xsl:when>
			<xsl:when test="../../xs:annotation/xs:documentation and string-length(../../xs:annotation/xs:documentation/text()) &gt; 1">
				<xsl:text>Type for </xsl:text>
				<xsl:variable name="text" select="../../xs:annotation/xs:documentation/text()" />
				<xsl:value-of select="translate(substring($text, 1, 1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
				<xsl:value-of select="substring($text, 2)" />
			</xsl:when>
			<xsl:when test="../../xsd:annotation/xsd:documentation and string-length(../../xsd:annotation/xsd:documentation/text()) &gt; 1">
				<xsl:text>Type for </xsl:text>
				<xsl:variable name="text" select="../../xsd:annotation/xsd:documentation/text()" />
				<xsl:value-of select="translate(substring($text, 1, 1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
				<xsl:value-of select="substring($text, 2)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>The </xsl:text>
				<xsl:value-of select="$typeName" />
				<xsl:text> type.</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</description>
	<xsl:text>
	</xsl:text>
	<xsl:choose>
		<xsl:when test="xs:pattern or xsd:pattern">
			<pattern>
				<xsl:value-of select="xs:pattern/@value" />
				<xsl:value-of select="xsd:pattern/@value" />
			</pattern>
		</xsl:when>
		<xsl:when test="xs:enumeration or xsd:enumeration">
			<enum>
				<xsl:for-each select="xs:enumeration | xsd:enumeration">
					<xsl:text>
		</xsl:text>
					<item value="{@value}" />
				</xsl:for-each>
					<xsl:text>
	</xsl:text>
			</enum>
		</xsl:when>
		<xsl:when test="xs:maxLength or xs:minLength or xs:length or xsd:maxLength or xsd:minLength or xsd:length">
			<pattern>
				<xsl:text>.{</xsl:text>
				<xsl:if test="not(xs:minLength) and not(xs:length) and not(xsd:minLength) and not(xsd:length)">
					<xsl:text>0</xsl:text>
				</xsl:if>
				<xsl:value-of select="xs:minLength/@value | xsd:minLength/@value" />
				<xsl:value-of select="xs:length/@value | xsd:length/@value" />
				<xsl:if test="xs:minLength or xs:maxLength or xsd:minLength or xsd:maxLength">
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:value-of select="xs:maxLength/@value | xsd:maxLength/@value" />
				<xsl:text>}</xsl:text>
			</pattern>
		</xsl:when>
		<xsl:when test="xs:minInclusive or xs:minExclusive or xsd:minInclusive or xsd:minExclusive or xs:maxInclusive or xs:maxExclusive or xsd:maxInclusive or xsd:maxExclusive">
			<int32>
				<xsl:if test="xs:minInclusive or xs:minExclusive or xsd:minInclusive or xsd:minExclusive">
					<xsl:attribute name="min">
						<xsl:value-of select="xs:minInclusive/@value | xsd:minInclusive/@value" />
						<xsl:value-of select="xs:minExclusive/@value | xsd:minExclusive/@value" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="xs:maxInclusive or xs:maxExclusive or xsd:maxInclusive or xsd:maxExclusive">
					<xsl:attribute name="max">
						<xsl:value-of select="xs:maxInclusive/@value | xsd:maxInclusive/@value" />
						<xsl:value-of select="xs:maxExclusive/@value | xsd:maxExclusive/@value" />
					</xsl:attribute>
				</xsl:if>
			</int32>
		</xsl:when>
		<xsl:when test="local-name() = 'list'">
			<xsl:variable name="itemList">
				<xsl:choose>
					<xsl:when test="@itemType">
						<xsl:choose>
							<xsl:when test="starts-with(@itemType, 'xs:') or starts-with(@itemType, 'xsd:')">
								<xsl:call-template name="type_for_xsdtype">
									<xsl:with-param name="xsdtype" select="substring-after(@itemType, ':')" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@itemType" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="xs:simpleType/@name | xsd:simpleType/@name">
						<xsl:value-of select="xs:simpleType/@name | xsd:simpleType/@name" />
					</xsl:when>
					<xsl:when test="not(@itemType) and not(xs:simpleType/@name | xsd:simpleType/@name) and ../@name">
						<xsl:value-of select="../@name" />
						<xsl:text>ListItem</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>_text</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<list type="{$itemList}" />
		</xsl:when>
		<xsl:when test="@maxOccurs = 'unbounded'">
			<xsl:variable name="localNameType">
				<xsl:call-template name="localname">
					<xsl:with-param name="text" select="@type" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="itemList">
				<xsl:choose>
					<xsl:when test="starts-with(@type, 'xs:') or starts-with(@type, 'xsd:')">
						<xsl:call-template name="type_for_xsdtype">
							<xsl:with-param name="xsdtype" select="$localNameType" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="hungarianUpper">
							<xsl:with-param name="text" select="$localNameType" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<list type="{$itemList}" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:message terminate="no">
				<xsl:text>No XINS type created for XSD type </xsl:text>
				<xsl:value-of select="$elementName" />
			</xsl:message>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text>
</xsl:text>
</type>
	</xsl:template>

	<xsl:template match="xs:element | xsd:element" mode="list">
		<xsl:variable name="localNameType">
			<xsl:call-template name="localname">
				<xsl:with-param name="text" select="@type" />
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:if test="starts-with(@type, 'xs:') or starts-with(@type, 'xsd:') or key('simpletypenames', $localNameType)">
			<xsl:variable name="typeName">
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text" select="$localNameType" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="create-type">
				<xsl:with-param name="elementName" select="@name" />
				<xsl:with-param name="typeName" select="concat($typeName, 'List')" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Removes any namespace prefix to the text if any -->
	<xsl:template name="localname">
		<xsl:param name="text" />

		<xsl:choose>
			<xsl:when test="contains($text, ':')">
				<xsl:value-of select="substring-after($text, ':')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
