<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the output section for a function or for a result code.

 $Id: output_section.xslt,v 1.18 2007/08/14 11:15:25 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="parametertable">
		<xsl:param name="title" />
		<xsl:param name="content" />
		<xsl:param name="class" />
		<xsl:param name="type" select="'Parameter'"/>

		<h3>
			<xsl:value-of select="$title" />
		</h3>

		<xsl:choose>
			<xsl:when test="param | property">
				<table class="parameters">
					<xsl:attribute name="class">
						<xsl:value-of select="$class" />
					</xsl:attribute>
					<tr>
						<th>
							<xsl:value-of select="$type" />
						</th>
						<th>Type</th>
						<th>Description</th>
						<th>Required</th>
					</tr>
					<xsl:for-each select="param[not(@required='true') and not(@required='false')] | property[not(@required='true') and not(@required='false')]">
						<xsl:message terminate="yes">
							<xsl:text>Parameter '</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>' in </xsl:text>
							<xsl:value-of select="$content" />
							<xsl:text> has required attribute set to '</xsl:text>
							<xsl:value-of select="@required" />
							<xsl:text>', while only 'true' and 'false' are allowed values.</xsl:text>
						</xsl:message>
					</xsl:for-each>
					<xsl:apply-templates select="param" />
					<xsl:apply-templates select="property" />
				</table>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<em>
						<xsl:text>This function defines no </xsl:text>
						<xsl:value-of select="$content" />
						<xsl:text>.</xsl:text>
					</em>
				</p>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="param | property">

		<xsl:if test="boolean(deprecated) and (@required = 'true')">
			<xsl:message terminate="yes">
				<xsl:text>Parameter '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>' is both deprecated and required!</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td class="name">
				<a>
					<xsl:attribute name="name">
						<xsl:value-of select="name(..)" />
						<xsl:text>_</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:attribute>
				</a>
				<xsl:value-of select="@name" />
			</td>
			<td class="type">
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="boolean(@type)">
								<xsl:value-of select="@type" />
							</xsl:when>
							<xsl:otherwise>_text</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</td>
			<td class="description">
				<xsl:call-template name="description" />
			</td>
			<td class="required">
				<xsl:if test="@required = 'true'">
					<xsl:text>yes</xsl:text>
				</xsl:if>
				<xsl:if test="@required = 'false'">
					<xsl:text>no</xsl:text>
					<xsl:if test="string-length(@default) &gt; 0">
						<xsl:text> (Default is </xsl:text>
						<xsl:value-of select="concat(@default, ')')" />
					</xsl:if>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="data/element">
		<xsl:variable name="elementName" select="@name" />
		<xsl:if test="preceding-sibling::element[@name=$elementName]">
			<xsl:message terminate="yes">
				<xsl:text>Element '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>' is already defined in the data section.</xsl:text>
			</xsl:message>
		</xsl:if>
		<h4>
			<xsl:text>Element </xsl:text>
			<em>
				<xsl:text>&lt;</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>/&gt;</xsl:text>
			</em>
		</h4>

		<table class="element_details">
			<tr>
				<th>Description:</th>
				<td>
					<xsl:choose>
						<xsl:when test="description">
							<xsl:apply-templates select="description" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:text> </xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<th>Contains:</th>
				<td>
					<xsl:choose>
						<xsl:when test="contains/contained">
							<xsl:for-each select="contains/contained">
								<xsl:if test="position() != 1">
									<xsl:text>, </xsl:text>
								</xsl:if>
								<code>
									<xsl:text>&lt;</xsl:text>
									<xsl:value-of select="@element" />
									<xsl:text>/&gt;</xsl:text>
								</code>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="contains/pcdata">
							<xsl:text>Character data.</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<em>Nothing.</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<th>Attributes:</th>
				<td>
					<xsl:choose>
						<xsl:when test="attribute">
							<table class="parameters">
								<tr>
									<th>Name</th>
									<th>Type</th>
									<th>Description</th>
									<th>Required</th>
								</tr>
								<xsl:apply-templates select="attribute" />
							</table>
						</xsl:when>
						<xsl:otherwise>
							<em>none</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</table>
		<xsl:call-template name="additional-constraints" />
	</xsl:template>

	<xsl:template match="data/element/attribute">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:value-of select="@type" />
				</xsl:when>
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<tr>
			<td class="value">
				<a>
					<xsl:attribute name="name">
						<xsl:value-of select="../@name" />
						<xsl:text>_</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:attribute>
				</a>
				<xsl:value-of select="@name" />
			</td>
			<td>
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api"      />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="boolean(@type)">
								<xsl:value-of select="@type" />
							</xsl:when>
							<xsl:otherwise>_text</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="description" />
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="@required = 'true'">
						<xsl:text>yes</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>no</xsl:text>
						<xsl:if test="string-length(@default) &gt; 0">
							<xsl:text> (Default is </xsl:text>
							<xsl:value-of select="concat(@default, ')')" />
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="description">
		<xsl:if test="description">
			<xsl:apply-templates select="description" />
			<xsl:if test="deprecated">
				<br />
			</xsl:if>
		</xsl:if>
		<xsl:if test="deprecated">
			<em>
				<strong>Deprecated: </strong>
				<xsl:apply-templates select="deprecated" />
			</em>
		</xsl:if>
	</xsl:template>

	<xsl:template name="additional-constraints">
		<xsl:variable name="part">
			<xsl:choose>
				<xsl:when test="param-combo">
					<xsl:text> parameters</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> attributes</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="param-combo | attribute-combo">
			<h4>Additional constraints</h4>
			<xsl:text>The following constraint</xsl:text>
			<xsl:choose>
				<xsl:when test="count(param-combo | attribute-combo) &lt; 2">
					<xsl:text> applies to the </xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>s apply to the </xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="local-name()" />
			<xsl:value-of select="$part" />
			<xsl:text>, additional to the </xsl:text>
			<xsl:value-of select="local-name()" />
			<xsl:value-of select="$part" />
			<xsl:text> marked as required. A violation of </xsl:text>
			<xsl:choose>
				<xsl:when test="count(param-combo | attribute-combo) &lt; 2">
					<xsl:text>this constraint</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>any of these constraints</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> will result in an unsuccessful result with the error code </xsl:text>
			<xsl:choose>
				<xsl:when test="local-name() = 'input' or local-name(../..) = 'input'">
					<em>_InvalidRequest</em>
				</xsl:when>
				<xsl:when test="local-name() = 'output' or local-name(../..) = 'output'">
					<em>_InvalidResponse</em>
				</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>Invalid node: </xsl:text>
						<xsl:value-of select="local-name()" />
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>.</xsl:text>
			<ul>
				<xsl:apply-templates select="param-combo | attribute-combo">
					<xsl:with-param name="part" select="$part" />
				</xsl:apply-templates>
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="param-combo[@type='exclusive-or'] | attribute-combo[@type='exclusive-or']">
		<xsl:param name="part" />

		<li>
			<em>Exactly</em>
			<xsl:text> one of these</xsl:text>
			<xsl:value-of select="$part" />
			<xsl:text> must be set: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="param-combo[@type='inclusive-or'] | attribute-combo[@type='inclusive-or']">
		<xsl:param name="part" />

		<li>
			<xsl:text>At </xsl:text>
			<em>least</em>
			<xsl:text> one of these</xsl:text>
			<xsl:value-of select="$part" />
			<xsl:text> must be set: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="param-combo[@type='all-or-none'] | attribute-combo[@type='all-or-none']">
		<xsl:param name="part" />
		<li>
			<xsl:text>Either </xsl:text>
			<em>all</em>
			<xsl:text> of these</xsl:text>
			<xsl:value-of select="$part" />
			<xsl:text> must be set, or </xsl:text>
			<em>none</em>
			<xsl:text> of them can be set: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="param-combo[@type='not-all'] | attribute-combo[@type='not-all']">
		<xsl:param name="part" />
		<li>
			<xsl:text>The following</xsl:text>
			<xsl:value-of select="$part" />
			<xsl:text> cannot all be set at the same time: </xsl:text>
			<xsl:apply-templates select="." mode="textlist" />
			<xsl:text>.</xsl:text>
		</li>
	</xsl:template>

	<xsl:template match="param-combo | attribute-combo" priority="-1">
		<xsl:message terminate="yes">
			<xsl:text>Unrecognized type of </xsl:text>
			<xsl:value-of select="local-name()" />
			<xsl:text>.</xsl:text>
		</xsl:message>
	</xsl:template>

	<xsl:template match="param-combo | attribute-combo" mode="textlist">
		<xsl:variable name="count" select="count(param-ref)" />
		<xsl:variable name="type" select="@type" />
		<xsl:for-each select="param-ref | attribute-ref">
			<xsl:choose>
				<xsl:when test="position() = $count and ($type='inclusive-or' or $type='exclusive-or')">
					<xsl:text> or </xsl:text>
				</xsl:when>
				<xsl:when test="position() = $count and not($type='inclusive-or' or $type='exclusive-or')">
					<xsl:text> and </xsl:text>
				</xsl:when>
				<xsl:when test="position() &gt; 1">
					<xsl:text>, </xsl:text>
				</xsl:when>
			</xsl:choose>
			<em>
				<xsl:value-of select="@name" />
			</em>
			<xsl:if test="@value">
				<xsl:text> with the value '</xsl:text>
				<xsl:value-of select="@value" />
				<xsl:text>'</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
