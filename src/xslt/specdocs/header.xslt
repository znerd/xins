<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 XSLT that generates the header of the specification documentation.

 $Id: header.xslt,v 1.8 2007/02/13 14:23:48 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="header">
		<xsl:param name="active" />
		<xsl:param name="prev" />
		<xsl:param name="next" />
		<xsl:param name="prev_url" />
		<xsl:param name="next_url" />

		<table class="headerlinks">
			<tr>
				<td>
					<xsl:call-template name="header_item">
						<xsl:with-param name="item">apilist</xsl:with-param>
						<xsl:with-param name="active" select="$active" />
					</xsl:call-template>
					<xsl:text> | </xsl:text>
					<xsl:call-template name="header_item">
						<xsl:with-param name="item">api</xsl:with-param>
						<xsl:with-param name="active" select="$active" />
					</xsl:call-template>
					<xsl:text> | </xsl:text>
					<xsl:call-template name="header_item">
						<xsl:with-param name="item">function</xsl:with-param>
						<xsl:with-param name="active" select="$active" />
					</xsl:call-template>
					<xsl:text> | </xsl:text>
					<xsl:call-template name="header_item">
						<xsl:with-param name="item">testform</xsl:with-param>
						<xsl:with-param name="active" select="$active" />
					</xsl:call-template>
					<xsl:text> | </xsl:text>
					<xsl:call-template name="header_item">
						<xsl:with-param name="item">type</xsl:with-param>
						<xsl:with-param name="active" select="$active" />
					</xsl:call-template>
					<xsl:text> | </xsl:text>
					<xsl:call-template name="header_item">
						<xsl:with-param name="item">resultcode</xsl:with-param>
						<xsl:with-param name="active" select="$active" />
					</xsl:call-template>
				</td>
				<xsl:if test="boolean($prev) or boolean($next)">
					<td class="prevnext">
						<xsl:choose>
							<xsl:when test="not(boolean($prev)) or $prev=''">
								<span class="disabled">
									<xsl:text>Prev</xsl:text>
								</span>
							</xsl:when>
							<xsl:otherwise>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$prev_url" />
									</xsl:attribute>
									<xsl:attribute name="title">
										<xsl:value-of select="$prev" />
									</xsl:attribute>
									<xsl:text>Prev</xsl:text>
								</a>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:text> | </xsl:text>
						<xsl:choose>
							<xsl:when test="not(boolean($next)) or $next=''">
								<span class="disabled">
									<xsl:text>Next</xsl:text>
								</span>
							</xsl:when>
							<xsl:otherwise>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$next_url" />
									</xsl:attribute>
									<xsl:attribute name="title">
										<xsl:value-of select="$next" />
									</xsl:attribute>
									<xsl:text>Next</xsl:text>
								</a>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</xsl:if>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="header_item">
		<xsl:param name="item" />
		<xsl:param name="active" />
		<xsl:param name="name" />

		<xsl:choose>
			<xsl:when test="$active = $item">
				<span class="active">
					<xsl:call-template name="header_item_caption">
						<xsl:with-param name="item" select="$item" />
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:when test="$item='api' and not($active='category' or $active='function' or $active='testform' or $active='type' or $active='resultcode' or $active='properties')">
				<span class="disabled">
					<xsl:call-template name="header_item_caption">
						<xsl:with-param name="item" select="$item" />
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:when test="$item='type' or ($item='function' and not($active='testform'))">
				<span class="disabled">
					<xsl:call-template name="header_item_caption">
						<xsl:with-param name="item" select="$item" />
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:when test="$item='resultcode' or ($item='function' and not($active='testform'))">
				<span class="disabled">
					<xsl:call-template name="header_item_caption">
						<xsl:with-param name="item" select="$item" />
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:when test="$item='testform'and not($active='function')">
				<span class="disabled">
					<xsl:call-template name="header_item_caption">
						<xsl:with-param name="item" select="$item" />
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="header_item_href">
							<xsl:with-param name="item" select="$item" />
							<xsl:with-param name="active" select="$active" />
							<xsl:with-param name="name" select="$name" />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:attribute name="title">
						<xsl:call-template name="header_item_title">
							<xsl:with-param name="item" select="$item" />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:call-template name="header_item_caption">
						<xsl:with-param name="item" select="$item" />
					</xsl:call-template>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="header_item_caption">
		<xsl:param name="item" />

		<xsl:choose>
			<xsl:when test="$item='apilist'">API Index</xsl:when>
			<xsl:when test="$item='api'">Overview</xsl:when>
			<xsl:when test="$item='function'">Function</xsl:when>
			<xsl:when test="$item='testform'">Test form</xsl:when>
			<xsl:when test="$item='type'">Type</xsl:when>
			<xsl:when test="$item='resultcode'">Result code</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="header_item_title">
		<xsl:param name="item" />

		<xsl:choose>
			<xsl:when test="$item='apilist'">Overview of all API specifications</xsl:when>
			<xsl:when test="$item='api'">Overview of the current API</xsl:when>
			<xsl:when test="$item='function'">Function</xsl:when>
			<xsl:when test="$item='testform'">Test form for this function</xsl:when>
			<xsl:when test="$item='type'">Type</xsl:when>
			<xsl:when test="$item='resultcode'">Result code</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="header_item_href">
		<xsl:param name="item" />
		<xsl:param name="active" />
		<xsl:param name="name" />

		<xsl:choose>
			<xsl:when test="$active='apilist'">
				<xsl:message terminate="yes">
					<xsl:text>Unsupported combination: item is '</xsl:text>
					<xsl:value-of select="$item" />
					<xsl:text>', while '</xsl:text>
					<xsl:value-of select="$active" />
					<xsl:text>' is active.</xsl:text>
				</xsl:message>
			</xsl:when>
			<xsl:when test="$item='apilist'">../index.html</xsl:when>
			<xsl:when test="$item='api'">index.html</xsl:when>
			<xsl:when test="$item='function'">
				<xsl:value-of select="@name" />
				<xsl:text>.html</xsl:text>
			</xsl:when>
			<xsl:when test="$item='testform'">
				<xsl:value-of select="@name" />
				<xsl:text>-testform.html</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Unsupported combination: item is '</xsl:text>
					<xsl:value-of select="$item" />
					<xsl:text>', while '</xsl:text>
					<xsl:value-of select="$active" />
					<xsl:text>' is active.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
