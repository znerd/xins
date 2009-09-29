<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generated the APIImpl.java class.

 $Id: api_to_java.xslt,v 1.34 2007/04/25 15:32:43 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="impl_file"    />
	<xsl:param name="package"      />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:output method="text" />

	<!-- Determine name of API -->
	<xsl:variable name="api" select="/api/@name" />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="api">
		<xsl:apply-templates select="document($impl_file)/impl">
			<xsl:with-param name="api_node" select="." />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="impl-java | impl">
		<xsl:param name="api_node" />

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:text><![CDATA[

import org.xins.server.API;

/**
 * Implementation of <code>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</code> API.
 */
public class APIImpl extends API {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final APIImpl SINGLETON = new APIImpl();]]></xsl:text>

		<xsl:for-each select="$api_node/function">
			<xsl:variable name="name"    select="@name" />
			<xsl:variable name="file"    select="concat($specsdir, '/', $name, '.fnc')" />
			<xsl:variable name="fieldname">
				<xsl:call-template name="toupper">
					<xsl:with-param name="text">
						<xsl:call-template name="hungarianWordSplit">
							<xsl:with-param name="text" select="$name" />
							<xsl:with-param name="separator" select="'_'" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>

			<xsl:text><![CDATA[

   /**
    * The <em>]]></xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[</em> function.
    */
   public static final ]]></xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="$fieldname" />
			<xsl:text> = new </xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text>Impl(SINGLETON);</xsl:text>
		</xsl:for-each>

		<xsl:text>

   private final RuntimeProperties _runtimeProperties;</xsl:text>

		<xsl:for-each select="instance">
			<xsl:text>

   private final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;</xsl:text>
		</xsl:for-each>

		<xsl:text><![CDATA[

   /**
    * Constructs a new <code>APIImpl</code> instance.
    */
   private APIImpl() {
      super("]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text>");
      _runtimeProperties = new RuntimeProperties();</xsl:text>
		<xsl:choose>
			<xsl:when test="instance">
				<xsl:for-each select="instance">
					<xsl:text>
      </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> = new </xsl:text>
					<xsl:value-of select="@class" />
					<xsl:text>(this);</xsl:text>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      // empty</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>
   }</xsl:text>

		<xsl:text><![CDATA[

   /**
    * Gets the class used to access the defined runtime properties
    *
    * @return
    *    the runtime properties, never <code>null</code>code>.
    */
   public org.xins.server.RuntimeProperties getProperties() {
      return _runtimeProperties;
   }

   /**
    * Triggers re-initialization of this API.
    */
   void reinitialize() {
      super.reinitializeImpl();
   }
]]></xsl:text>
	<xsl:if test="instance">
		<xsl:text>
   protected void bootstrapImpl2(org.xins.common.collections.PropertyReader properties)
   throws org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException,
          org.xins.common.manageable.BootstrapException {</xsl:text>
		<xsl:for-each select="instance">
			<xsl:text>
      add(</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>);</xsl:text>
		</xsl:for-each>
		<xsl:text>
   }</xsl:text>
	</xsl:if>

		<xsl:for-each select="instance">
			<xsl:text>

   public </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@getter" />
			<xsl:text>() {
      return </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;
   }</xsl:text>
		</xsl:for-each>
		<xsl:text>
}</xsl:text>
	</xsl:template>
</xsl:stylesheet>
