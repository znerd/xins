<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 $Id: log_to_TranslationBundle_java.xslt,v 1.33 2007/03/16 10:58:30 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />
	<xsl:param name="accesslevel"  />

	<xsl:include href="shared.xslt" />
	<xsl:include href="../xml_to_java.xslt" />

	<!-- Set output method -->
	<xsl:output method="text" />

	<xsl:template match="log">
		<xsl:variable name="accessmodifier">
			<xsl:choose>
				<xsl:when test="(string-length($accesslevel) = 0) or $accesslevel = 'package'" />
				<xsl:when test="$accesslevel = 'public'">public </xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text>;

/**
 * Translation bundle for log messages.
 *
 * @see Log</xsl:text>
      <xsl:if test="string-length(@since) &gt; 0">
         <xsl:text>
 *
 * @since </xsl:text>
         <xsl:value-of select="@since" />
      </xsl:if>
      <xsl:text>
 */
</xsl:text>
		<xsl:value-of select="$accessmodifier" />
		<xsl:text>abstract class TranslationBundle {</xsl:text>

		<xsl:text><![CDATA[

   /**
    * The name of this translation bundle.
    */
   private final String _name;

   /**
    * Constructs a new <code>TranslationBundle</code> subclass instance.
    *
    * @param name
    *    the name of this translation bundle, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   protected TranslationBundle(String name)
   throws IllegalArgumentException {

      // Check preconditions
      if (name == null) {
         throw new IllegalArgumentException("name == null");
      }

      // Store information
      _name = name;
   }

   /**
    * Retrieves the name of this translation bundle.
    *
    * @return
    *    the name of this translation bundle.
    */
   public final String getName() {
      return _name;
   }]]></xsl:text>

		<xsl:apply-templates select="group/entry" />

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="group/entry">
		<xsl:variable name="description" select="description/text()" />
		<xsl:variable name="exception" select="@exception = 'true'" />

		<xsl:text>

   /**
    * Get the translation for the log entry with ID </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text><![CDATA[, in the log entry group <em>]]></xsl:text>
		<xsl:value-of select="../@name" />
		<xsl:text><![CDATA[</em>.
    * The description for this log entry is:
    * <blockquote><em>]]></xsl:text>
		<xsl:apply-templates select="description" />
		<xsl:text><![CDATA[</em></blockquote>
    */
   public String translation_]]></xsl:text>
		<!-- TODO: Generate @param tags for parameters -->
		<xsl:value-of select="@id" />
		<xsl:text>(</xsl:text>
		<xsl:if test="$exception">
			<xsl:text>Throwable _exception</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="param" mode="method-argument">
			<xsl:with-param name="exception" select="$exception" />
		</xsl:apply-templates>
		<xsl:text>) {
      return "</xsl:text>
		<xsl:call-template name="xml_to_java_string">
			<xsl:with-param name="text" select="$description" />
		</xsl:call-template>
		<xsl:text>";
   }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
