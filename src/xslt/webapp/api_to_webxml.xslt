<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the web.xml file that is included in the WAR file.

 $Id: api_to_webxml.xslt,v 1.52 2007/12/18 10:42:43 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="apis_dir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_version"  />
	<xsl:param name="java_version" />
	<xsl:param name="hostname"     />
	<xsl:param name="timestamp"    />

	<xsl:output
	method="xml"
	doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
	doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd"
	indent="yes" />

	<xsl:include href="../package_for_api.xslt" />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="api">
		<xsl:if test="string-length($hostname) &lt; 1">
			<xsl:message terminate="yes">Parameter 'hostname' is not specified.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($timestamp) &lt; 1">
			<xsl:message terminate="yes">Parameter 'timestamp' is not specified.</xsl:message>
		</xsl:if>
		<xsl:variable name="impl_file" select="concat($apis_dir, '/', $api, '/impl/impl.xml')"/>
		<xsl:apply-templates select="document($impl_file)/impl" />
	</xsl:template>

	<xsl:template match="impl">
		<web-app>

			<!-- 
			If you want to apply Servlet filters or filter-mapping, do it here.
			Servlet filters could be used for compression, encryption, authentication, etc...
			-->
      <xsl:apply-templates select="web-app" />

			<servlet>
				<servlet-name>
					<xsl:value-of select="$api" />
				</servlet-name>
				<display-name>
					<xsl:value-of select="$api" />
				</display-name>
				<description>
					<xsl:text>Implementation of '</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:text>' API.</xsl:text>
				</description>
				<servlet-class>
					<xsl:choose>
						<xsl:when test="calling-convention[@class='org.xins.server.frontend.FrontendCallingConvention']">
							<xsl:text>org.xins.server.APIServletSingleThreaded</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>org.xins.server.APIServlet</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</servlet-class>
				<init-param>
					<param-name>org.xins.api.name</param-name>
					<param-value>
						<xsl:value-of select="$api" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.class</param-name>
					<param-value>
						<xsl:call-template name="package_for_server_api">
							<xsl:with-param name="project_node" select="$project_node" />
							<xsl:with-param name="api" select="$api" />
						</xsl:call-template>
						<xsl:text>.APIImpl</xsl:text>
					</param-value>
				</init-param>
				<xsl:for-each select="param">
					<init-param>
						<param-name>
							<xsl:value-of select="@name" />
						</param-name>
						<param-value>
							<xsl:value-of select="text()" />
						</param-value>
					</init-param>
				</xsl:for-each>
				<init-param>
					<param-name>org.xins.api.build.version</param-name>
					<param-value>
						<xsl:value-of select="$xins_version" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.version</param-name>
					<param-value>
						<xsl:value-of select="$api_version" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.build.java.version</param-name>
					<param-value>
						<xsl:value-of select="$java_version" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.build.host</param-name>
					<param-value>
						<xsl:value-of select="$hostname" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.build.time</param-name>
					<param-value>
						<xsl:value-of select="$timestamp" />
					</param-value>
				</init-param>
				<xsl:if test="count(calling-convention[@default='true']) > 1">
					<xsl:message terminate="yes">
						<xsl:text>Only one calling convention can be defined as the default one.</xsl:text>
					</xsl:message>
				</xsl:if>
				<xsl:if test="count(calling-convention) > 1 and count(calling-convention[@default='true']) != 1">
					<xsl:message terminate="yes">
						<xsl:text>More than one calling convention has been defined,
one of them should be defined as the default one.</xsl:text>
					</xsl:message>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="count(calling-convention) = 1">
						<init-param>
							<param-name>org.xins.api.calling.convention</param-name>
							<param-value>
								<xsl:value-of select="calling-convention/@name" />
							</param-value>
						</init-param>
						<xsl:if test="calling-convention/@class">
							<init-param>
								<param-name>org.xins.api.calling.convention.class</param-name>
								<param-value>
									<xsl:value-of select="calling-convention/@class" />
								</param-value>
							</init-param>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<init-param>
							<param-name>org.xins.api.calling.convention</param-name>
							<param-value>
								<xsl:value-of select="calling-convention[@default='true']/@name" />
							</param-value>
						</init-param>
						<xsl:if test="calling-convention[@default='true']/@class">
							<init-param>
								<param-name>org.xins.api.calling.convention.class</param-name>
								<param-value>
									<xsl:value-of select="calling-convention[@default='true']/@class" />
								</param-value>
							</init-param>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:for-each select="calling-convention[@class!='']">
					<init-param>
						<param-name>
							<xsl:text>org.xins.api.calling.convention.</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>.class</xsl:text>
						</param-name>
						<param-value>
							<xsl:value-of select="@class" />
						</param-value>
					</init-param>
				</xsl:for-each>
				<xsl:for-each select="bootstrap-properties/bootstrap-property">
					<xsl:if test="starts-with(@name, 'org.xins.') and @name != 'org.xins.server.config' and @name != 'org.xins.logdoc.stackTraceAtMessageLevel'">
						<xsl:message terminate="yes">
							<xsl:text>Invalid defined bootstrap property &quot;</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>&quot; in impl.xml. The defined bootstrap properties are not allowed to start with &quot;org.xins.&quot;.</xsl:text>
						</xsl:message>
					</xsl:if>
					<init-param>
						<param-name>
							<xsl:value-of select="@name" />
						</param-name>
						<param-value>
							<xsl:value-of select="text()" />
						</param-value>
					</init-param>
				</xsl:for-each>
				<load-on-startup>
					<!-- XXX: Should we be able to configure the load-on-startup setting ? -->
					<xsl:text>0</xsl:text>
				</load-on-startup>
			</servlet>
			<servlet-mapping>
				<servlet-name>
					<xsl:value-of select="$api" />
				</servlet-name>
				<url-pattern>
					<xsl:choose>
						<xsl:when test="@web-path">
							<xsl:text>/</xsl:text>
							<xsl:value-of select="@web-path" />
							<xsl:text>/*</xsl:text>
						</xsl:when>
						<!-- It's authorized to put files in the WEB-INF directory sush as the xins.properties or (Spring) applicationContext.xml -->
						<xsl:when test="not(@web-path) and content[web-path != 'WEB-INF']">
							<xsl:message terminate="yes">
								<xsl:text>The attribute web-path to the impl element is mandatory if a content element is specified.</xsl:text>
							</xsl:message>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>/*</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</url-pattern>
			</servlet-mapping>
		</web-app>
	</xsl:template>

	<xsl:template match="calling-convention">
	</xsl:template>

	<xsl:template match="web-app">
		<xsl:element name="{@element}">
			<xsl:if test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id" />
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="text()" disable-output-escaping="yes" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
