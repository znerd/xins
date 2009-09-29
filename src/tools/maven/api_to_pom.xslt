<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates a Maven pom.xml file for an API.

 $Id: api_to_pom.xslt,v 1.8 2007/09/25 08:24:55 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		omit-xml-declaration="no"
		encoding="UTF-8"
		method="xml"
		indent="yes" />

	<!-- Define parameters -->
	<xsl:param name="api" />
	<xsl:param name="project_home" />
	<xsl:param name="xins_home" />

	<xsl:template match="api">
		<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />
		<xsl:variable name="project_node" select="document($project_file)/project" />

		<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
			<modelVersion>4.0.0</modelVersion>
			<groupId>
				<xsl:value-of select="concat(@domain, '.', $api, '.api')" />
			</groupId>
			<artifactId>
				<xsl:value-of select="$api" />
			</artifactId>
			<packaging>war</packaging>
			<version>1.0</version>
			<name>
				<xsl:value-of select="$api" />
				<xsl:text> API</xsl:text>
			</name>
			<description>
				<xsl:value-of select="description/text()" />
			</description>
			<url>http://maven.apache.org</url>
			<properties>
				<xins.home>
					<xsl:value-of select="$xins_home" />
				</xins.home>
				<project.home>
					<xsl:value-of select="$project_home" />
				</project.home>
				<xsl:comment>
					<xsl:text>Eclipse user should create a build link in the project that links to </xsl:text>
					<xsl:value-of select="concat($project_home, '/build')" />
					<xsl:text> and modify the build.home property to &quot;build&quot;.</xsl:text>
				</xsl:comment>
				<build.home>
					<xsl:value-of select="concat($project_home, '/build')" />
				</build.home>
			</properties>
			<dependencies>
				<xsl:if test="type">
					<dependency>
						<groupId>
							<xsl:value-of select="$api" />
							<xsl:text>-types</xsl:text>
						</groupId>
						<artifactId>
							<xsl:value-of select="$api" />
							<xsl:text>-types</xsl:text>
						</artifactId>
						<version>1.0</version>
						<scope>system</scope>
						<systemPath>
							<xsl:value-of select="concat($project_home, '/build/classes-types/', $api, '/')" />
						</systemPath>
					</dependency>
				</xsl:if>
				<xsl:if test="$project_node/api[@name=$api]/impl">
					<xsl:variable name="dependenciesdir" select="@dependenciesdir" />
					<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
					<xsl:variable name="impl_node" select="document($impl_file)/impl" />
					<xsl:for-each select="dependency">
						<dependency>
							<groupId>dependencies</groupId>
							<artifactId>
								<xsl:text>dependency-</xsl:text>
								<xsl:value-of select="$api" />
							</artifactId>
							<version>1.0</version>
							<scope>system</scope>
							<systemPath>
								<xsl:value-of select="concat($project_home, '/', $dependenciesdir, '/', @dir, '/', @includes)" />
							</systemPath>
						</dependency>
					</xsl:for-each>
				</xsl:if>
				<dependency>
					<groupId>org.xins</groupId>
					<artifactId>xins-common</artifactId>
					<version>2.1</version>
				</dependency>
				<dependency>
					<groupId>org.xins</groupId>
					<artifactId>xins-server</artifactId>
					<version>2.1</version>
				</dependency>
				<dependency>
					<groupId>org.xins</groupId>
					<artifactId>logdoc</artifactId>
					<version>2.1</version>
				</dependency>
				<dependency>
					<groupId>org.xins</groupId>
					<artifactId>xins-client</artifactId>
					<version>2.1</version>
				</dependency>
				<dependency>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
					<version>1.2.15</version>
				</dependency>
				<dependency>
					<groupId>commons-codec</groupId>
					<artifactId>commons-codec</artifactId>
					<version>1.3</version>
				</dependency>
				<dependency>
					<groupId>commons-httpclient</groupId>
					<artifactId>commons-httpclient</artifactId>
					<version>3.1</version>
				</dependency>
				<dependency>
					<groupId>oro</groupId>
					<artifactId>oro</artifactId>
					<version>2.0.8</version>
				</dependency>
				<dependency>
					<groupId>javax.servlet</groupId>
					<artifactId>servlet-api</artifactId>
					<version>2.4</version>
				</dependency>
				<dependency>
					<groupId>xmlenc</groupId>
					<artifactId>xmlenc</artifactId>
					<version>0.52</version>
				</dependency>
				<dependency>
					<groupId>junit</groupId>
					<artifactId>junit</artifactId>
					<version>3.8.1</version>
				</dependency>
			</dependencies>
			<build>
				<!--sourceDirectory>impl</sourceDirectory>
				<outputDirectory>classes</outputDirectory>
				<testSourceDirectory>test</testSourceDirectory>
				<resources>
					<resource>
						<directory>.</directory>
						<excludes>
							<exclude>**/*.java</exclude>
						</excludes>
					</resource>
				</resources-->
				<finalName>
					<xsl:value-of select="$api" />
				</finalName>
				<plugins>
					<plugin>
						<artifactId>maven-antrun-plugin</artifactId>
						<executions>
							<execution>
								<phase>generates-specdocs</phase>
								<configuration>
									<tasks>
										<taskdef resource="org/xins/common/ant/antlib.xml"/>
										<xins target="specdocs" api="{$api}" projectdir="../.."/>
									</tasks>
								</configuration>
								<goals>
									<goal>run</goal>
								</goals>
							</execution>
						</executions>
					</plugin>
					<!--plugin>
						<groupId>org.codehaus.mojo</groupId>
						<artifactId>tomcat-maven-plugin</artifactId>
						<configuration>
							<warSourceDirectory>impl</warSourceDirectory>
							<warFile>
								<xsl:value-of select="concat('${build.home}/webapps/', $api, '/', $api, '.war')" />
							</warFile>
							<xsl:if test="$project_node/api[@name=$api]/environments">
								<xsl:variable name="env_file" select="concat($project_home, '/apis/', $api, '/environments.xml')" />
								<url>
									<xsl:value-of select="document($env_file)/environments/environment[1]/@url" />
								</url>
							</xsl:if>
						</configuration>
					</plugin-->
					<plugin>
						<groupId>org.apache.maven.plugins</groupId>
						<artifactId>maven-war-plugin</artifactId>
						<version>2.0</version>
						<configuration>
							<outputDirectory>
								<xsl:value-of select="concat('${build.home}/webapps/', $api)" />
							</outputDirectory>
							<warName>
								<xsl:value-of select="$api" />
							</warName>
							<warSourceDirectory>impl</warSourceDirectory>
							<webappDirectory>
								<xsl:value-of select="concat('${build.home}/classes-api/', $api)" />
							</webappDirectory>
							<workDirectory>
								<xsl:value-of select="concat('${build.home}/webapps/', $api)" />
							</workDirectory>
							<webXml>
								<xsl:value-of select="concat('${build.home}/webapps/', $api, '/web.xml')" />
							</webXml>
							<webResources>
								<xsl:if test="$project_node/api[@name=$api]/impl">
									<xsl:variable name="dependenciesdir" select="@dependenciesdir" />
									<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')" />
									<xsl:variable name="impl_node" select="document($impl_file)/impl" />
									<xsl:for-each select="content">
										<resource>
											<!-- TODO this is relative to the pom.xml directory -->
											<directory>
												<xsl:value-of select="@dir" />
											</directory>
											<xsl:if test="@web-path">
												<targetPath>
													<xsl:value-of select="@web-path" />
												</targetPath>
											</xsl:if>
											<xsl:if test="@includes">
												<includes>
													<include>
														<xsl:value-of select="@includes" />
													</include>
												</includes>
											</xsl:if>
										</resource>
									</xsl:for-each>
								</xsl:if>
							</webResources>
						</configuration>
					</plugin>
					<plugin>
						<groupId>org.mortbay.jetty</groupId>
						<artifactId>maven-jetty-plugin</artifactId>
					</plugin>
				</plugins>
			</build>
		</project>
	</xsl:template>

</xsl:stylesheet>
