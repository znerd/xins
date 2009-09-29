<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an Eclipse .classpath file for an API.

 $Id: api_to_classpath.xslt,v 1.3 2007/05/03 14:59:33 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		omit-xml-declaration="no"
		encoding="UTF-8"
		method="xml"
		indent="yes" />

	<xsl:template match="api">
		<xsl:variable name="api" select="@name" />

		<classpath>
			<classpathentry including="**/{$api}/**" excluding="classes-api/{$api}/|classes-types/{$api}/|java-fundament/{$api}/|java-types/{$api}/" kind="src" path="build"/>
			<classpathentry kind="src" path="build/java-fundament/{$api}"/>
			<classpathentry kind="src" path="build/java-types/{$api}"/>
			<classpathentry kind="src" path="impl"/>
			<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
			<classpathentry kind="lib" path="build/classes-api/{$api}"/>
			<classpathentry kind="lib" path="build/classes-types/{$api}"/>
			<classpathentry kind="con" path="org.eclipse.jdt.USER_LIBRARY/xins"/>
			<classpathentry kind="output" path="build/classes-api/{$api}"/>
		</classpath>
	</xsl:template>

</xsl:stylesheet>
