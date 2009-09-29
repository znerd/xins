<?xml version="1.0"?>
<!--
 XSLT that generates the header of the generated java files.

 $Id: java.xslt,v 1.8 2007/01/04 10:17:30 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="java-header">
		<xsl:text>// This is a generated file. Please do not edit.&#10;&#10;</xsl:text>
	</xsl:template>
</xsl:stylesheet>
