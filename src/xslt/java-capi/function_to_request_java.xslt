<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the Request classes.

 $Id: function_to_request_java.xslt,v 1.47 2007/09/28 13:47:51 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />

	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="../xml_to_java.xslt"  />
	<xsl:include href="../java-server-framework/check_params.xslt"  />
	<xsl:include href="../java-server-framework/result_java.xslt"  />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="function">
		<xsl:variable name="project_node" select="document($project_file)/project" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="//function/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="className" select="concat($functionName,'Request')" />

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Request for a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 * <p>An instance of this class is accepted by the corresponding call method
 * in the CAPI class: {@link CAPI#call]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>)}.
 *
 * @see CAPI
 * @see </xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>Result
 */
public final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[
extends org.xins.client.AbstractCAPICallRequest {

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      super(&quot;</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>&quot;);</xsl:text>
		<xsl:for-each select="input/param[@default]">
			<xsl:text>
      parameterValue(&quot;</xsl:text>
			<xsl:value-of select="concat(@name, '&quot;, &quot;')" />
			<xsl:call-template name="xml_to_java_string">
				<xsl:with-param name="text" select="@default" />
			</xsl:call-template>
			<xsl:text>&quot;);</xsl:text>
		</xsl:for-each>
		<xsl:text>
   }</xsl:text>

		<xsl:apply-templates select="input/param" mode="methods" />

		<xsl:apply-templates select="input/data" mode="methods" />

		<xsl:if test="input/data/@contains">
			<xsl:variable name="elementName" select="input/data/@contains" />
			<xsl:apply-templates select="input/data/element[@name=$elementName]" mode="addMethod" />
		</xsl:if>
		<xsl:for-each select="input/data/contains/contained">
			<xsl:variable name="elementName" select="@element" />
			<xsl:apply-templates select="../../element[@name=$elementName]" mode="addMethod" />
		</xsl:for-each>

		<xsl:text><![CDATA[

   /**
    * Validates whether this request is considered acceptable. If any
    * constraints are violated, then an
    * {@link org.xins.client.UnacceptableRequestException UnacceptableRequestException}
    * is returned.
    *
    * <p>This method is called automatically when this request is executed, so
    * it typically does not need to be called manually in advance.
    *
    * @return
    *    an
    *    {@link org.xins.client.UnacceptableRequestException UnacceptableRequestException}
    *    instance if this request is considered unacceptable, otherwise
    *    <code>null</code>.
    */
   public org.xins.client.UnacceptableRequestException checkParameters() {
]]></xsl:text>
		<xsl:apply-templates select="input" mode="checkParams">
			<xsl:with-param name="side" select="'client'" />
		</xsl:apply-templates>
		<xsl:if test="not(input)">
			<xsl:text>
      return null;</xsl:text>
		</xsl:if>
		<xsl:text>
   }
</xsl:text>
		<xsl:apply-templates select="input/data/element" mode="addElementClass" />
		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="input/param" mode="methods">

		<!-- Determine the Java class or primary data type -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'true'"        />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine if $javatype is a Java primary data type -->
		<xsl:variable name="isJavaDatatype">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javatype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- If $javatype is a primary data type, determine class -->
		<xsl:variable name="javaclass">
			<xsl:choose>
				<xsl:when test="$isJavaDatatype = 'true'">
					<xsl:call-template name="javaclass_for_javatype">
						<xsl:with-param name="javatype" select="$javatype" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$javatype" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java class that represents the type -->
		<xsl:variable name="typeclass">
			<xsl:call-template name="javatypeclass_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- The name of the variable used in code for this parameter -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the method that transform the value to a String -->
		<xsl:variable name="typeToString">
			<xsl:call-template name="javatype_to_string_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"      select="$api" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="@type" />
				<xsl:with-param name="variable" select="$javaVariable" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the method that transform the String to a value -->
		<xsl:variable name="stringToType">
			<xsl:call-template name="javatype_from_string_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api" />
				<xsl:with-param name="specsdir"     select="$specsdir" />
				<xsl:with-param name="required"     select="'false'" />
				<xsl:with-param name="type"         select="@type" />
				<xsl:with-param name="variable"     select="'typeValue'" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the names of the methods -->
		<xsl:variable name="methodTail">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="getMethod">
			<xsl:text>get</xsl:text>
			<xsl:value-of select="$methodTail" />
		</xsl:variable>
		<xsl:variable name="setMethod">
			<xsl:text>set</xsl:text>
			<xsl:value-of select="$methodTail" />
		</xsl:variable>

		<xsl:text><![CDATA[

   /**
    * Gets the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> parameter.
    * If unset, <code>null</code> is returned.
    *
    * @return
    *    the value of the parameter, or <code>null</code> if unset.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$javaclass" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$getMethod" />
		<xsl:text>() {
      String typeValue = getParameter("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>");
      try {
         return </xsl:text>
		<xsl:value-of select="$stringToType" />
		<xsl:text>;
      } catch (org.xins.common.types.TypeValueException tvex) {

         // Should never happens
         return null;
      }
   }

   /**
    * Sets</xsl:text>
		<xsl:if test="$isJavaDatatype = 'false'">
			<xsl:text> or resets</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[ the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> parameter as a]]></xsl:text>
		<xsl:if test="translate(substring($javatype,1,1),'aeiouy','******') = '*'">
			<xsl:text>n</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
    * <code>]]></xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text><![CDATA[</code>.
    *
    * @param ]]></xsl:text>
				<xsl:value-of select="$javaVariable" />
				<xsl:text>
    *    the new value for the parameter</xsl:text>
		<xsl:if test="$isJavaDatatype = 'false'">
			<xsl:text><![CDATA[, or <code>null</code> if it should be
    * reset]]></xsl:text>
		</xsl:if>
		<xsl:text>.
    */
   public void </xsl:text>
		<xsl:value-of select="$setMethod" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text>) {
      parameterValue("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", </xsl:text>
		<xsl:value-of select="$typeToString" />
		<xsl:text>);
   }</xsl:text>

		<xsl:if test="$isJavaDatatype = 'true'">
			<xsl:variable name="wraptype">
				<xsl:call-template name="javaclass_for_javatype">
					<xsl:with-param name="javatype" select="$javatype" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:text><![CDATA[

   /**
    * Sets or resets the <em>]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[</em> parameter as a]]></xsl:text>
			<xsl:if test="translate(substring($wraptype,1,1),'aeiouy','******') = '*'">
				<xsl:text>n</xsl:text>
			</xsl:if>
			<xsl:text><![CDATA[
    * <code>]]></xsl:text>
			<xsl:value-of select="$wraptype" />
			<xsl:text><![CDATA[</code>.
    *
    * @param ]]></xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text><![CDATA[
    *    the new value for the parameter, or <code>null</code> if it should be
    *    reset.
    */
   public void ]]></xsl:text>
			<xsl:value-of select="$setMethod" />
			<xsl:text>(</xsl:text>
			<xsl:value-of select="$wraptype" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="$javaVariable" />
			<xsl:text>) {
      parameterValue("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", </xsl:text>
		<xsl:value-of select="$typeToString" />
		<xsl:text>);
   }</xsl:text>
		</xsl:if>

	</xsl:template>

	<xsl:template match="input/data" mode="methods">

		<xsl:text><![CDATA[

   /**
    * Sets the data section.
    * If the value is <code>null</code> any previous data section set is removed.
    * If a previous value was entered, the value will be overridden by this new
    * value.
    *
    * @param dataSection
    *    The data section.
    */
   public void addDataSection(org.xins.common.xml.Element dataSection) {
      putDataSection(dataSection);
   }]]></xsl:text>

	</xsl:template>
</xsl:stylesheet>

