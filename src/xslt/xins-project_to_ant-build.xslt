<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the build.xml used to compile the different APIs.

 $Id: xins-project_to_ant-build.xslt,v 1.362 2007/12/17 15:41:08 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="builddir"     />
	<xsl:param name="xins_version" />

	<!-- Perform includes -->
	<xsl:include href="hungarian.xslt"       />
	<xsl:include href="package_for_api.xslt" />

	<xsl:output indent="yes" />

	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')"  />
	<xsl:variable name="project_node" select="document($project_file)/project"             />
	<xsl:variable name="specsdir">
		<xsl:value-of select="$project_home" />
		<xsl:text>/</xsl:text>
		<xsl:choose>
			<xsl:when test="//project/@specsdir">
				<xsl:value-of select="//project/@specsdir" />
			</xsl:when>
			<xsl:otherwise>src/specs</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="apis_dir">
		<xsl:value-of select="$project_home" />
		<xsl:text>/</xsl:text>
		<xsl:choose>
			<xsl:when test="$project_node/@apisDir">
				<xsl:value-of select="$project_node/@apisDir" />
			</xsl:when>
			<xsl:otherwise>apis</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="dependenciesDir">
		<xsl:value-of select="$project_home" />
		<xsl:if test="//project/@dependenciesdir">
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//project/@dependenciesdir" />
		</xsl:if>
	</xsl:variable>

	<xsl:template match="project">
		<project name="{//project/@name}" default="help" basedir="..">

			<import file="{$xins_home}/src/ant/build-apis.xml" optional="true" />
			<import file="{$xins_home}/src/ant/build-create.xml" optional="true" />
			<import file="{$xins_home}/src/ant/build-tools.xml" optional="true" />

			<target name="-load-xins-properties">
				<property name="xins_home"    value="{$xins_home}"    />
				<property name="apis_dir"     value="{$apis_dir}"     />
				<property name="project_home" value="{$project_home}" />
				<property name="builddir"     value="{$builddir}"     />
				<property name="xins_version" value="{$xins_version}" />
				<property name="cvsweb"       value="{cvsweb/@href}"  />
				<xsl:if test="@domain">
					<property name="domain" value="{@domain}" />
				</xsl:if>
				<property name="apis.list">
					<xsl:attribute name="value">
						<xsl:for-each select="//project/api/impl">
							<xsl:if test="position() &gt; 1">,</xsl:if>
							<xsl:value-of select="../@name" />
							<xsl:if test="@name">
								<xsl:value-of select="concat('-', @name)" />
							</xsl:if>
						</xsl:for-each>
					</xsl:attribute>
				</property>
			</target>

			<target name="specdocs" description="Generates all specification docs">
				<xsl:attribute name="depends">
					<xsl:text>index-specdocs</xsl:text>
					<xsl:for-each select="api">
						<xsl:text>,specdocs-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="classes" description="Compiles all Java classes">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>classes-api-</xsl:text>
						<xsl:value-of select="../@name" />
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="clients" description="Generates all CAPI JAR files, corresponding Javadoc and the specdocs">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>client-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="javadoc-capis" description="Generates all CAPI Javadoc">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>javadoc-capi-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="capis" description="Generates all CAPI JAR files">
				<xsl:attribute name="depends">
					<xsl:for-each select="api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>jar-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="javadoc-apis" description="Creates the Javadoc for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>javadoc-api-</xsl:text>
						<xsl:value-of select="../@name" />
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="wars" description="Creates the WARs for all APIs">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/impl">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>war-</xsl:text>
						<xsl:value-of select="../@name" />
						<xsl:if test="@name">
							<xsl:value-of select="concat('-', @name)" />
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="tests" description="Tests all APIs that have tests.">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api/test">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>test-</xsl:text>
						<xsl:value-of select="../@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<target name="all" description="Generates everything">
				<xsl:attribute name="depends">
					<xsl:for-each select="//project/api">
						<xsl:if test="position() &gt; 1">,</xsl:if>
						<xsl:text>all-</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:attribute>
			</target>

			<xsl:apply-templates select="api" />
		</project>
	</xsl:template>

	<xsl:template match="api">
		<xsl:variable name="api" select="@name" />
		<xsl:variable name="api_specsdir" select="concat($apis_dir, '/', $api, '/spec')" />
		<xsl:variable name="api_file" select="concat($api_specsdir, '/api.xml')" />
		<xsl:variable name="api_node" select="document($api_file)/api" />
		<xsl:variable name="typeClassesDir"    select="concat($builddir, '/classes-types/', $api)" />
		<xsl:variable name="functionIncludes">
			<xsl:for-each select="$api_node/function">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.fnc</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="typeIncludes">
			<xsl:for-each select="$api_node/type">
				<xsl:if test="not(contains(@name, '/'))">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text>.typ</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="typeIncludesAll">
			<xsl:for-each select="$api_node/type">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:choose>
					<xsl:when test="contains(@name, '/')">
						<xsl:value-of select="concat(substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.typ')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($api, '/spec/', @name, '.typ')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="resultcodeIncludes">
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="not(contains(@name, '/'))">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text>.rcd</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="resultcodeIncludesAll">
			<xsl:for-each select="$api_node/resultcode">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:choose>
					<xsl:when test="contains(@name, '/')">
						<xsl:value-of select="concat(substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($api, '/spec/', @name, '.rcd')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="categoryIncludes">
			<xsl:for-each select="$api_node/category">
				<xsl:if test="position() &gt; 1">,</xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text>.cat</xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="clientPackage">
			<xsl:call-template name="package_for_client_api">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api" select="$api" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="clientPackageAsDir" select="translate($clientPackage, '.','/')" />
		<xsl:variable name="apiHasTypes" select="boolean($api_node/type)" />
		<xsl:variable name="package">
			<xsl:call-template name="package_for_server_api">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api" select="$api" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="packageAsDir" select="translate($package, '.', '/')" />

		<target name="-load-properties-{$api}" depends="-load-properties">
			<property name="{$api}.api" value="{@name}" />
			<property name="{$api}.api_specsdir" value="${{apis_dir}}/${{{$api}.api}}/spec" />
			<property name="{$api}.api_file" value="${{{$api}.api_specsdir}}/api.xml" />
			<property name="{$api}.functionIncludes" value="{$functionIncludes}" />
			<xsl:if test="string-length($typeIncludes) &gt; 0">
				<property name="{$api}.typeIncludes" value="{$typeIncludes}" />
			</xsl:if>
			<xsl:if test="string-length($typeIncludesAll) &gt; 0">
				<property name="{$api}.typeIncludesAll" value="{$typeIncludesAll}" />
			</xsl:if>
			<xsl:if test="string-length($resultcodeIncludes) &gt; 0">
				<property name="{$api}.resultcodeIncludes" value="{$resultcodeIncludes}" />
			</xsl:if>
			<xsl:if test="string-length($resultcodeIncludesAll) &gt; 0">
				<property name="{$api}.resultcodeIncludesAll" value="{$resultcodeIncludesAll}" />
			</xsl:if>
			<xsl:if test="string-length($categoryIncludes) &gt; 0">
				<property name="{$api}.categoryIncludes" value="{$categoryIncludes}" />
			</xsl:if>
			<property name="{$api}.clientPackage" value="{$clientPackage}" />
			<property name="{$api}.clientPackageAsDir" value="{$clientPackageAsDir}" />
			<property name="{$api}.package" value="{$package}" />
			<property name="{$api}.packageAsDir" value="{$packageAsDir}" />
			<xsl:if test="apiHasTypes">
				<property name="{$api}.apiHasTypes" value="true" />
			</xsl:if>
			<propertyset id="{$api}.properties">
				<propertyref prefix="{$api}." />
				<mapper type="glob" from="{$api}.*" to="*"/>
			</propertyset>
		</target>

		<target name="-dependset-file-{$api}">
			<dirname property="dependset.directory" file="{$builddir}/${{dependset.destination}}" />
			<mkdir dir="${{dependset.directory}}" />
			<dependset>
				<srcfilelist dir="{$api_specsdir}" files="${{functionIncludes}}" />
				<xsl:if test="string-length($typeIncludesAll) &gt; 0">
					<srcfilelist dir="{$apis_dir}" files="${{typeIncludesAll}}" />
				</xsl:if>
				<xsl:if test="string-length($resultcodeIncludesAll) &gt; 0">
					<srcfilelist dir="{$apis_dir}" files="${{resultcodeIncludesAll}}" />
				</xsl:if>
				<xsl:if test="string-length($categoryIncludes) &gt; 0">
					<srcfilelist dir="{$api_specsdir}" files="${{categoryIncludes}}" />
				</xsl:if>
				<xsl:for-each select="impl">
					<xsl:variable name="implName" select="@name" />
					<xsl:variable name="implName2">
						<xsl:if test="@name and string-length($implName) &gt; 0">
							<xsl:value-of select="concat('-', $implName)" />
						</xsl:if>
					</xsl:variable>
					<srcfilelist dir="{$apis_dir}/{$api}/impl{$implName2}" files="impl.xml" />
				</xsl:for-each>
				<targetfileset dir="{$builddir}" includes="${{dependset.destination}}" />
			</dependset>
		</target>

		<target name="specdocs-{$api}" depends="-load-properties-{$api}, index-specdocs" description="Generates all specification docs for the '{$api}' API">
			<xsl:if test="environments">
				<xsl:variable name="env_dir" select="concat($apis_dir, '/', $api)" />
				<dependset>
					<srcfilelist dir="{$env_dir}" files="environments.xml" />
					<targetfileset dir="{$builddir}/specdocs/{$api}" includes="*.html" />
				</dependset>
				<xsl:variable name="env_file" select="concat($apis_dir, '/', $api, '/environments.xml')" />
				<xmlvalidate file="{$env_file}" warn="false">
					<xmlcatalog refid="all-dtds" />
				</xmlvalidate>
			</xsl:if>
			<antcall target="-specdocs">
				<propertyset refid="{$api}.properties" />
				<reference refid="all-dtds" />
			</antcall>
			<xsl:for-each select="impl">
				<xsl:variable name="implName" select="@name" />
				<xsl:variable name="implName2">
					<xsl:if test="@name and string-length($implName) &gt; 0">
						<xsl:value-of select="concat('-', $implName)" />
					</xsl:if>
				</xsl:variable>
				<xsl:variable name="impl_dir" select="concat($apis_dir, '/', $api, '/impl', $implName2)" />
				<xsl:variable name="impl_file" select="concat($impl_dir, '/impl.xml')" />
				<xsl:variable name="impl_node" select="document($impl_file)/impl" />
				<xsl:if test="$impl_node/property">
					<xsl:message terminate="yes">Missing runtime-properties element.</xsl:message>
				</xsl:if>
				<xsl:if test="$impl_node/runtime-properties">
					<antcall target="-specdocs-impl-runtime">
						<propertyset refid="{$api}.properties" />
						<param name="implName2" value="{$implName2}" />
						<reference refid="all-dtds" />
					</antcall>
				</xsl:if>
				<xsl:if test="$impl_node/logdoc">
					<antcall target="-specdocs-impl-logdoc">
						<propertyset refid="{$api}.properties" />
						<param name="implName2" value="{$implName2}" />
						<reference refid="all-dtds" />
					</antcall>
				</xsl:if>
			</xsl:for-each>
		</target>

		<xsl:if test="$apiHasTypes">
			<target name="-classes-types-{$api}" depends="-load-properties-{$api}">
				<xsl:variable name="typePackage">
					<xsl:call-template name="package_for_type_classes">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api" select="$api" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="typePackageAsDir" select="translate($typePackage, '.','/')" />
				<property name="{$api}.typePackage" value="{$typePackage}" />
				<property name="{$api}.typePackageAsDir" value="{$typePackageAsDir}" />
				<antcall target="-classes-types">
					<propertyset refid="{$api}.properties" />
					<reference refid="all-dtds" />
				</antcall>
			</target>
		</xsl:if>

		<target name="wsdl-{$api}" depends="-load-properties-{$api}" description="Generates the WSDL specification of the '{$api}' API">
			<antcall target="-wsdl">
				<propertyset refid="{$api}.properties" />
				<reference refid="all-dtds" />
			</antcall>
		</target>

		<target name="opendoc-{$api}" depends="-load-properties-{$api}" description="Generates the specification document for the '{$api}' API">
			<antcall target="-opendoc">
				<propertyset refid="{$api}.properties" />
				<reference refid="all-dtds" />
			</antcall>
		</target>

		<xsl:for-each select="impl">
			<xsl:variable name="implName" select="@name" />
			<xsl:variable name="implName2">
				<xsl:if test="@name and string-length($implName) &gt; 0">
					<xsl:value-of select="concat('-', $implName)" />
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="javaImplDir" select="concat($apis_dir, '/', $api, '/impl', $implName2)" />
			<xsl:variable name="javaDestDir"     select="concat($builddir, '/java-fundament/', $api, $implName2)" />
			<xsl:variable name="classesDestDir"  select="concat($builddir, '/classes-api/',    $api, $implName2)" />
			<xsl:variable name="javaDestFileDir" select="concat($javaDestDir, '/', $packageAsDir)" />
			<xsl:variable name="impl_dir"     select="concat($apis_dir, '/', $api, '/impl', $implName2)" />
			<xsl:variable name="impl_file"    select="concat($impl_dir, '/impl.xml')" />
			<xsl:variable name="impl_node"    select="document($impl_file)/impl" />

			<xsl:if test="$impl_node/dependency">
				<target name="-load-dependencies-{$api}{$implName2}">
					<path id="impl.dependencies">
						<xsl:apply-templates select="$impl_node/dependency" />
					</path>
				</target>
			</xsl:if>

			<target name="classes-api-{$api}{$implName2}" description="Compiles the Java classes for the '{$api}{$implName2}' API implementation">
				<xsl:attribute name="depends">
					<xsl:text>-load-properties-</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:text>,-prepare-classes</xsl:text>
					<xsl:if test="$apiHasTypes">
						<xsl:text>,-classes-types-</xsl:text>
						<xsl:value-of select="$api" />
					</xsl:if>
					<xsl:if test="$impl_node/dependency">
						<xsl:value-of select="concat(',-load-dependencies-', $api, $implName2)" />
					</xsl:if>
				</xsl:attribute>

				<mkdir dir="{$javaDestDir}/{$packageAsDir}" />
				<dependset>
					<srcfilelist dir="{$api_specsdir}/../impl{$implName2}" files="impl.xml" />
					<srcfileset dir="{$api_specsdir}">
						<include name="${{{$api}.functionIncludes}} ${{{$api}.typeIncludes}} ${{{$api}.resultcodeIncludes}}" />
					</srcfileset>
					<targetfileset dir="{$javaDestDir}/{$packageAsDir}" includes="*.java" />
					<xsl:if test="$api_node/resultcode">
						<targetfileset dir="{$javaDestDir}" includes="resultcodes.xml" />
					</xsl:if>
				</dependset>
				<xsl:if test="$impl_node/bootstrap-property">
					<xsl:message terminate="yes">Missing bootstrap-properties element.</xsl:message>
				</xsl:if>
				<path id="classes.api.classpath">
					<xsl:if test="$apiHasTypes">
						<pathelement path="{$typeClassesDir}" />
					</xsl:if>
					<xsl:if test="$impl_node/dependency">
						<path refid="impl.dependencies" />
					</xsl:if>
				</path>
				<antcall target="-classes-api">
					<reference refid="classes.api.classpath" />
					<reference refid="all-dtds" />
					<propertyset refid="{$api}.properties" />
					<param name="implName2" value="{$implName2}" />
					<xsl:if test="$impl_node/logdoc">
						<xsl:variable name="accesslevel" select="$impl_node/logdoc/@accesslevel" />
						<param name="accesslevel" value="{$accesslevel}" />
						<param name="logdoc_file" value="{$impl_dir}/log.xml" />
					</xsl:if>
				</antcall>
			</target>

			<target name="war-{$api}{$implName2}" depends="-load-properties-{$api}, classes-api-{$api}{$implName2}, wsdl-{$api}" description="Creates the WAR for the '{$api}{$implName2}' API" unless="no-war-{$api}{$implName2}">
				<tstamp>
					<format property="timestamp" pattern="yyyy.MM.dd HH:mm:ss.SS" />
				</tstamp>
				<antcall target="-war">
					<propertyset refid="{$api}.properties" />
					<param name="implName2" value="{$implName2}" />
				</antcall>
				<property name="classes.api.dir" value="{$classesDestDir}" />
				<war
				webxml="{$builddir}/webapps/{$api}{$implName2}/web.xml"
				destfile="{$builddir}/webapps/{$api}{$implName2}/{$api}{$implName2}.war"
				manifest="{$builddir}/webapps/{$api}{$implName2}/MANIFEST.MF"
				duplicate="fail">
					<lib dir="{$xins_home}/build" includes="xins-common.jar xins-server.jar xins-client.jar" />
					<lib dir="{$xins_home}/lib"   includes="commons-codec.jar commons-httpclient.jar commons-logging.jar jakarta-oro.jar log4j.jar logdoc-base.jar xmlenc.jar json.jar" />
					<xsl:apply-templates select="$impl_node/dependency" mode="lib" />
					<classes dir="${{classes.api.dir}}" includes="**/*.class" />
					<xsl:if test="$apiHasTypes">
						<classes dir="{$typeClassesDir}" includes="**/*.class" />
					</xsl:if>
					<classes dir="{$javaImplDir}" excludes="**/*.java,**/*.class,impl.xml" />
					<zipfileset dir="{$builddir}/webapps/{$api}{$implName2}" includes="org/xins/common/servlet/container/*.class org/xins/common/servlet/container/xins.gif" /> 
					<xsl:apply-templates select="$impl_node/content" />
					<zipfileset dir="{$builddir}/wsdl" includes="{$api}.wsdl" prefix="WEB-INF" />
					<zipfileset dir="{$api_specsdir}" includes="api.xml ${{{$api}.functionIncludes}} ${{{$api}.typeIncludes}} ${{{$api}.resultcodeIncludes}} ${{{$api}.categoryIncludes}}" prefix="WEB-INF/specs" />
					<xsl:for-each select="$api_node/type">
						<xsl:if test="contains(@name, '/')">
							<xsl:variable name="type_dir"
							select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec')" />
							<xsl:variable name="type_filename"
							select="concat(substring-after(@name, '/'), '.typ')" />
							<zipfileset dir="{$type_dir}" includes="{$type_filename}" prefix="WEB-INF/specs" />
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="$api_node/resultcode">
						<xsl:if test="contains(@name, '/')">
							<xsl:variable name="resultcode_dir"
							select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec')" />
							<xsl:variable name="resultcode_filename"
							select="concat(substring-after(@name, '/'), '.rcd')" />
							<zipfileset dir="{$resultcode_dir}" includes="{$resultcode_filename}" prefix="WEB-INF/specs" />
						</xsl:if>
					</xsl:for-each>
					<zipgroupfileset dir="{$xins_home}/lib" includes="servlet.jar" />
				</war>
				<checksum file="{$builddir}/webapps/{$api}{$implName2}/{$api}{$implName2}.war" property="war.md5"/>
				<echo message="MD5: ${{war.md5}}" />
				<echo message="Build time: ${{timestamp}}" />
			</target>

			<target name="run-{$api}{$implName2}" description="Runs the '{$api}{$implName2}' API">
				<xsl:attribute name="depends">
					<xsl:if test="$impl_node/dependency">
						<xsl:value-of select="concat('-load-dependencies-', $api, $implName2, ',')" />
					</xsl:if>
					<xsl:value-of select="concat('war-', $api, $implName2)" />
				</xsl:attribute>
				<path id="run.classpath">
					<path location="{$builddir}/classes-api/{$api}{$implName2}" />
					<xsl:if test="$apiHasTypes">
						<path location="{$builddir}/classes-types/{$api}" />
					</xsl:if>
					<xsl:if test="$impl_node/dependency">
						<path refid="impl.dependencies" />
					</xsl:if>
				</path>
				<antcall target="-run">
					<reference refid="run.classpath" />
					<reference refid="xins.classpath" />
					<propertyset refid="{$api}.properties" />
					<param name="implName2" value="{$implName2}" />
				</antcall>
			</target>

			<target name="javadoc-api-{$api}{$implName2}" depends="-load-properties, classes-api-{$api}{$implName2}" description="Generates Javadoc API docs for the '{$api}{$implName2}' API">
				<!--path id="javadoc.api.{$api}{$implName2}.packageset"-->
				<path id="javadoc.api.packageset">
					<dirset dir="{$javaDestDir}" />
					<dirset dir="{$javaImplDir}" />
					<xsl:if test="$apiHasTypes">
						<dirset dir="{$builddir}/java-types/{$api}" />
					</xsl:if>
				</path>
				<antcall target="-javadoc-api">
					<reference refid="javadoc.api.packageset" />
					<reference refid="classes.api.classpath"  />
					<reference refid="xins.classpath"  />
					<param name="api" value="{$api}" />
					<param name="implName2" value="{$implName2}" />
				</antcall>
			</target>

			<target name="stub-{$api}{$implName2}" depends="-load-properties-{$api}" description="Generates an Stub API using the defined examples">
				<antcall target="-stub">
					<propertyset refid="{$api}.properties" />
					<reference refid="all-dtds" />
					<param name="implName2" value="{$implName2}" />
				</antcall>
			</target>

			<target name="server-{$api}{$implName2}"
							depends="specdocs-{$api}, javadoc-api-{$api}{$implName2}, war-{$api}{$implName2}"
							description="Generates the war file, the Javadoc API docs for the server side and the specdocs for the '{$api}{$implName2}' API.">
			</target>
		</xsl:for-each>

		<xsl:if test="test">
			<xsl:variable name="packageTests">
				<xsl:call-template name="package_for_tests">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api" select="$api" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="packageTestsAsDir" select="translate($packageTests, '.','/')" />

			<target name="test-{$api}" description="Generates (if needed) and run the tests for the {$api} API.">
				<xsl:variable name="impl_file" select="concat($apis_dir, '/', $api, '/impl/impl.xml')" />
				<xsl:variable name="impl_node" select="document($impl_file)/impl" />
				<xsl:attribute name="depends">
					<xsl:value-of select="concat('-load-properties-', $api)" />
					<xsl:if test="$impl_node/dependency">
						<xsl:value-of select="concat(',-load-dependencies-', $api)" />
					</xsl:if>
				</xsl:attribute>
				<property name="packageTests" value="{$packageTests}" />
				<path id="test.classpath">
					<pathelement path="{$builddir}/capis/{$api}-capi.jar" />
					<xsl:if test="$apiHasTypes">
						<pathelement path="{$builddir}/classes-types/{$api}" />
					</xsl:if>
					<xsl:if test="$impl_node/dependency">
						<path refid="impl.dependencies" />
					</xsl:if>
				</path>
				<antcall target="-test">
					<reference refid="test.classpath" />
					<propertyset refid="{$api}.properties" />
				</antcall>
			</target>

			<target name="generatetests-{$api}" depends="-load-properties-{$api}" unless="test.generated">
				<property name="{$api}.packageTests" value="{$packageTests}" />
				<property name="{$api}.packageTestsAsDir" value="{$packageTestsAsDir}" />
				<antcall target="-generatetests">
					<propertyset refid="{$api}.properties" />
					<reference refid="all-dtds" />
				</antcall>
			</target>

			<target name="javadoc-test-{$api}" description="Generates the Javadoc of the unit tests of the {$api} API.">
				<xsl:variable name="impl_file" select="concat($apis_dir, '/', $api, '/impl/impl.xml')" />
				<xsl:variable name="impl_node" select="document($impl_file)/impl" />
				<xsl:if test="$impl_node/dependency">
					<xsl:attribute name="depends">
						<xsl:value-of select="concat('-load-dependencies-', $api)" />
					</xsl:attribute>
				</xsl:if>
				<path id="javadoc.test.classpath">
					<pathelement path="{$builddir}/classes-tests/{$api}" />
					<xsl:if test="$apiHasTypes">
						<pathelement path="{$builddir}/classes-types/{$api}" />
					</xsl:if>
					<xsl:if test="$impl_node/dependency">
						<path refid="impl.dependencies" />
					</xsl:if>
				</path>
				<antcall target="-javadoc-test">
					<reference refid="javadoc.test.classpath" />
					<param name="api" value="{$api}" />
				</antcall>
			</target>
		</xsl:if>

		<target name="jar-{$api}" description="Generates and compiles the Java classes for the client-side '{$api}' API stubs">
			<xsl:attribute name="depends">
				<xsl:text>-prepare-classes, -load-properties-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:if test="$apiHasTypes">
					<xsl:text>, </xsl:text>
					<xsl:text>-classes-types-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:if>
			</xsl:attribute>
			<path id="jar.classpath">
				<xsl:if test="$apiHasTypes">
					<pathelement path="{$typeClassesDir}"  />
				</xsl:if>
			</path>
			<antcall target="-jar">
				<propertyset refid="{$api}.properties" />
				<reference refid="jar.classpath" />
			</antcall>
			<xsl:if test="$apiHasTypes">
				<copy todir="{$builddir}/classes-capi/{$api}">
					<fileset dir="{$typeClassesDir}" includes="**/*.class" />
				</copy>
			</xsl:if>
			<jar
			destfile="{$builddir}/capis/{$api}-capi.jar"
			manifest="{$builddir}/capis/{$api}-MANIFEST.MF">
				<fileset dir="{$builddir}/classes-capi/{$api}" includes="**/*.class" />
				<zipfileset dir="{$api_specsdir}" includes="api.xml ${{{$api}.functionIncludes}} ${{{$api}.typeIncludes}} ${{{$api}.resultcodeIncludes}}" prefix="specs" />
				<xsl:for-each select="type">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="type_dir"
						select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec')" />
						<xsl:variable name="type_filename"
						select="concat(substring-after(@name, '/'), '.typ')" />
						<zipfileset dir="{$type_dir}" includes="{$type_filename}" prefix="specs" />
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="resultcode">
					<xsl:if test="contains(@name, '/')">
						<xsl:variable name="resultcode_dir"
						select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec')" />
						<xsl:variable name="resultcode_filename"
						select="concat(substring-after(@name, '/'), '.rcd')" />
						<zipfileset dir="{$resultcode_dir}" includes="{$resultcode_filename}" prefix="specs" />
					</xsl:if>
				</xsl:for-each>
			</jar>
		</target>
		<target name="capi-{$api}" depends="jar-{$api}" />

		<target name="javadoc-capi-{$api}" description="Generates Javadoc API docs for the client-side '{$api}' API stubs">
			<xsl:attribute name="depends">
				<xsl:text>-load-properties-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:if test="$apiHasTypes">
					<xsl:value-of select="concat(', -classes-types-', $api)" />
				</xsl:if>
			</xsl:attribute>
			<path id="javadoc.capi.packages">
				<dirset dir="{$builddir}/java-capi/{$api}" />
				<xsl:if test="$apiHasTypes">
					<dirset dir="{$builddir}/java-types/{$api}" />
				</xsl:if>
			</path>
			<antcall target="-javadoc-capi">
				<reference refid="javadoc.capi.packages" />
				<propertyset refid="{$api}.properties" />
			</antcall>
		</target>

		<target name="client-{$api}"
						depends="jar-{$api}, javadoc-capi-{$api}, specdocs-{$api}, wsdl-{$api}, opendoc-{$api}"
						description="Generates the Javadoc API docs for the client side and the client JAR file for the '{$api}' API stubs and zip the result.">
			<antcall target="-client">
				<param name="api" value="{$api}" />
			</antcall>
		</target>

		<target name="all-{$api}"
						description="Generates everything for the '{$api}' API stubs.">
			<xsl:attribute name="depends">
				<xsl:text>client-</xsl:text>
				<xsl:value-of select="$api" />
				<xsl:if test="impl">
					<xsl:text>, server-</xsl:text>
					<xsl:value-of select="$api" />
				</xsl:if>
			</xsl:attribute>
		</target>

		<target name="clean-{$api}" description="Deletes everything for the '{$api}' API stubs.">
			<antcall target="-clean">
				<param name="api" value="{$api}" />
			</antcall>
			<xsl:for-each select="impl/@name">
				<xsl:variable name="impl" select="." />
				<antcall target="-clean-impl">
					<param name="api" value="{$api}" />
					<param name="impl" value="{$impl}" />
				</antcall>
			</xsl:for-each>
		</target>

		<target name="rebuild-{$api}" depends="clean-{$api}, all-{$api}"
						description="Regenerates everything for the '{$api}' API stubs." />
	</xsl:template>

	<xsl:template match="content">
		<zipfileset dir="{$dependenciesDir}/{@dir}">
			<xsl:attribute name="includes">
				<xsl:choose>
					<xsl:when test="@includes">
						<xsl:value-of select="@includes" />
					</xsl:when>
					<xsl:otherwise>**/*</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@web-path">
				<xsl:attribute name="prefix">
					<xsl:value-of select="@web-path" />
				</xsl:attribute>
			</xsl:if>
		</zipfileset>
	</xsl:template>

	<xsl:template match="dependency">
		<fileset dir="{$dependenciesDir}/{@dir}">
			<xsl:attribute name="includes">
				<xsl:choose>
					<xsl:when test="@includes">
						<xsl:value-of select="@includes" />
					</xsl:when>
					<xsl:otherwise>**/*.jar</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</fileset>
	</xsl:template>

	<xsl:template match="dependency[not(@deploy = 'false')]" mode="lib">
		<lib dir="{$dependenciesDir}/{@dir}">
			<xsl:attribute name="includes">
				<xsl:choose>
					<xsl:when test="@includes">
						<xsl:value-of select="@includes" />
					</xsl:when>
					<xsl:otherwise>**/*.jar</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</lib>
	</xsl:template>
</xsl:stylesheet>
