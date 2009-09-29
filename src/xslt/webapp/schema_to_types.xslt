<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the typ files from a XSD file.

 $Id: schema_to_types.xslt,v 1.5 2007/03/19 14:26:46 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:xt="http://www.jclarck.com/xt"
                xmlns:xalan="http://org.apache.xalan.xslt.extensions.Redirect"
								extension-element-prefixes="saxon xalan xt"
                exclude-result-prefixes="xs saxon xt xalan"
                version="2.0">

	<xsl:include href="../hungarian.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="xsd_to_types.xslt"  />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />

	<!-- TODO test with XML special characters in xsd -->
	<xsl:template match="xs:schema | xsd:schema">
		<xsl:apply-templates select="//xs:simpleType/xs:restriction" mode="restriction" />
		<xsl:apply-templates select="//xsd:simpleType/xsd:restriction" mode="restriction" />
		<xsl:apply-templates select="//xs:simpleType/xs:list" mode="restriction" />
		<xsl:apply-templates select="//xsd:simpleType/xsd:list" mode="restriction" />
		<xsl:apply-templates select="//xs:element[@maxOccurs='unbounded' and not(@type='xs:string')]" mode="list" />
		<xsl:apply-templates select="//xsd:element[@maxOccurs='unbounded' and not(@type='xsd:string')]" mode="list" />
	</xsl:template>
</xsl:stylesheet>
