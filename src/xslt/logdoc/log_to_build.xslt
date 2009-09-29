<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the build.xml that will create the logdoc java or html files.

 $Id: log_to_build.xslt,v 1.43 2007/12/17 15:16:07 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_home"       />
	<xsl:param name="logdoc_xslt_dir" />
	<xsl:param name="logdoc_dtd_dir"  />
	<xsl:param name="sourcedir"       />
	<xsl:param name="html_destdir"    />
	<xsl:param name="java_destdir"    />
	<xsl:param name="package_name"    />

	<xsl:output indent="yes" />

	<xsl:template match="log">
		<project default="all" basedir="..">

			<target name="catalog">
				<xmlcatalog id="log-dtds">
					<classpath>
						<pathelement path="{$xins_home}/src/dtd"/>
					</classpath>
					<dtd location="log_1_0.dtd"                publicId="-//XINS//DTD XINS Logdoc 1.0//EN" />
					<dtd location="translation-bundle_1_0.dtd" publicId="-//XINS//DTD XINS Translation Bundle 1.0//EN" />

					<dtd location="log_1_1.dtd"                publicId="-//XINS//DTD XINS Logdoc 1.1//EN" />
					<dtd location="translation-bundle_1_1.dtd" publicId="-//XINS//DTD XINS Translation Bundle 1.1//EN" />

					<dtd location="log_1_2.dtd"                publicId="-//XINS//DTD XINS Logdoc 1.2//EN" />
					<dtd location="translation-bundle_1_2.dtd" publicId="-//XINS//DTD XINS Translation Bundle 1.2//EN" />

					<dtd location="log_1_3.dtd"                publicId="-//XINS//DTD XINS Logdoc 1.3//EN" />
					<dtd location="translation-bundle_1_3.dtd" publicId="-//XINS//DTD XINS Translation Bundle 1.3//EN" />

					<dtd location="log_1_4.dtd"                publicId="-//XINS//DTD XINS Logdoc 1.4//EN" />
					<dtd location="translation-bundle_1_4.dtd" publicId="-//XINS//DTD XINS Translation Bundle 1.4//EN" />

					<dtd location="log_1_5.dtd"                publicId="-//XINS//DTD XINS Logdoc 1.5//EN" />
					<dtd location="translation-bundle_1_5.dtd" publicId="-//XINS//DTD XINS Translation Bundle 1.5//EN" />

					<dtd location="log_2_0.dtd"                publicId="-//XINS//DTD XINS Logdoc 2.0//EN" />
					<dtd location="translation-bundle_2_0.dtd" publicId="-//XINS//DTD XINS Translation Bundle 2.0//EN" />

					<dtd location="log_2_1.dtd"                publicId="-//XINS//DTD XINS Logdoc 2.1//EN" />
					<dtd location="translation-bundle_2_1.dtd" publicId="-//XINS//DTD XINS Translation Bundle 2.1//EN" />

					<dtd location="log_2_2.dtd"                publicId="-//XINS//DTD XINS Logdoc 2.2//EN" />
					<dtd location="translation-bundle_2_2.dtd" publicId="-//XINS//DTD XINS Translation Bundle 2.2//EN" />

					<dtd location="log_3_0.dtd"                publicId="-//XINS//DTD XINS Logdoc 3.0//EN" />
					<dtd location="translation-bundle_3_0.dtd" publicId="-//XINS//DTD XINS Translation Bundle 3.0//EN" />
				</xmlcatalog>
				<xmlvalidate warn="false" file="{$sourcedir}/log.xml">
					<xmlcatalog refid="log-dtds" />
				</xmlvalidate>
			</target>

			<target name="html" depends="catalog" description="Generates HTML documentation">
				<mkdir dir="{$html_destdir}" />
				<xslt
				in="{$sourcedir}/log.xml"
				out="{$html_destdir}/index.html"
				style="{$logdoc_xslt_dir}/log_to_html.xslt">
					<xmlcatalog refid="log-dtds" />
					<param name="package_name" expression="{$package_name}" />
				</xslt>
				<xslt
				in="{$sourcedir}/log.xml"
				out="{$html_destdir}/entry-list.html"
				style="{$logdoc_xslt_dir}/log_to_list_html.xslt">
					<xmlcatalog refid="log-dtds" />
				</xslt>
				<xsl:for-each select="group">
					<xslt
					in="{$sourcedir}/log.xml"
					out="{$html_destdir}/group-{@id}.html"
					style="{$logdoc_xslt_dir}/log_to_group_html.xslt">
						<xmlcatalog refid="log-dtds" />
						<param name="package_name" expression="{$package_name}" />
						<param name="group"     expression="{@id}"              />
					</xslt>
				</xsl:for-each>
				<xsl:for-each select="group/entry">
					<xslt
					in="{$sourcedir}/log.xml"
					out="{$html_destdir}/entry-{@id}.html"
					style="{$logdoc_xslt_dir}/log_to_entry_html.xslt">
						<xmlcatalog refid="log-dtds" />
						<param name="package_name" expression="{$package_name}" />
						<param name="sourcedir" expression="{$sourcedir}" />
						<param name="entry"     expression="{@id}"              />
					</xslt>
				</xsl:for-each>
			</target>

			<target name="java" depends="catalog" description="Generates Java code">
				<mkdir dir="{$java_destdir}" />
				<xslt
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/Log.java"
				style="{$logdoc_xslt_dir}/log_to_Log_java.xslt">
					<xmlcatalog refid="log-dtds" />
					<param name="package_name" expression="{$package_name}" />
					<param name="accesslevel" expression="${{accesslevel}}" />
				</xslt>
				<xslt
				in="{$sourcedir}/log.xml"
				out="{$java_destdir}/TranslationBundle.java"
				style="{$logdoc_xslt_dir}/log_to_TranslationBundle_java.xslt">
					<xmlcatalog refid="log-dtds" />
					<param name="package_name" expression="{$package_name}" />
					<param name="accesslevel"  expression="${{accesslevel}}" />
				</xslt>
				<xsl:for-each select="translation-bundle">
					<xmlvalidate warn="false" file="{$sourcedir}/translation-bundle-{@locale}.xml">
						<xmlcatalog refid="log-dtds" />
					</xmlvalidate>
					<xslt
					in="{$sourcedir}/translation-bundle-{@locale}.xml"
					out="{$java_destdir}/TranslationBundle_{@locale}.java"
					style="{$logdoc_xslt_dir}/translation-bundle_to_java.xslt">
						<xmlcatalog refid="log-dtds" />
						<param name="locale"       expression="{@locale}" />
						<param name="package_name" expression="{$package_name}" />
						<param name="log_file"     expression="{$sourcedir}/log.xml" />
						<param name="accesslevel" expression="${{accesslevel}}" />
					</xslt>
				</xsl:for-each>
			</target>

			<target name="all" depends="html, java" />
		</project>
	</xsl:template>
</xsl:stylesheet>
