<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an Eclipse .project file for an API.

 $Id: api_to_project.xslt,v 1.3 2007/05/03 14:59:33 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		omit-xml-declaration="no"
		encoding="UTF-8"
		method="xml"
		indent="yes" />

	<!-- Define parameters -->
	<xsl:param name="project_home" />

	<xsl:template match="api">

		<projectDescription>
			<name>
				<xsl:value-of select="@name" />
			</name>
			<comment>
				<xsl:value-of select="description/text()" />
			</comment>
			<projects>
			</projects>
			<buildSpec>
				<buildCommand>
					<name>org.eclipse.ui.externaltools.ExternalToolBuilder</name>
					<arguments>
						<dictionary>
							<key>LaunchConfigHandle</key>
							<value>
								<xsl:text>&lt;project&gt;/.externalToolBuilders/</xsl:text>
								<xsl:value-of select="@name" />
								<xsl:text> Ant Builder.launch</xsl:text>
							</value>
						</dictionary>
					</arguments>
				</buildCommand>
			</buildSpec>
			<natures>
				<nature>org.eclipse.jdt.core.javanature</nature>
				<nature>com.sysdeo.eclipse.tomcat.tomcatnature</nature>
			</natures>
			<linkedResources>
				<link>
					<name>build</name>
					<type>2</type>
					<location>
						<xsl:value-of select="concat($project_home, '/build')" />
					</location>
				</link>
			</linkedResources>
		</projectDescription>
	</xsl:template>

</xsl:stylesheet>
