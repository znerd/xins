<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the stub implementation from the examples in the function.

 $Id: function_to_stub.xslt,v 1.18 2007/06/22 11:32:01 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<!-- Perform import -->
	<xsl:import href="function_to_impl_java.xslt" />
	<xsl:import href="../xml_to_java.xslt" />

	<!-- Write the content of the call method -->
	<xsl:template name="callcontent">
		<xsl:apply-templates select="example" mode="if" />
		<xsl:text>
        throw new Exception("Not implemented in stub.");</xsl:text>
	</xsl:template>

	<!-- Write the text that should be generated after the call method -->
	<xsl:template name="aftercall">
		<xsl:apply-templates select="example" mode="method">
			<xsl:with-param name="api" select="$api" />
			<xsl:with-param name="specsdir" select="$specsdir" />
		</xsl:apply-templates>
	</xsl:template>

	<!-- Write the if statements -->
	<xsl:template match="example" mode="if">
		<xsl:choose>
			<xsl:when test="@resultcode and starts-with(@resultcode, '_')" />
			<xsl:otherwise>
				<xsl:text>
        if (</xsl:text>
				<xsl:if test="not(input-example)">
					<xsl:text>true</xsl:text>
				</xsl:if>
				<xsl:for-each select="input-example">
					<!-- Get the name of the get method. -->
					<xsl:variable name="inputName" select="@name" />
					<xsl:variable name="hungarianName">
						<xsl:call-template name="hungarianUpper">
							<xsl:with-param name="text" select="@name" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="position() &gt; 1">
						<xsl:text> &amp;&amp;
                </xsl:text>
					</xsl:if>
					<xsl:if test="not(/function/input/param[@name=$inputName]/@required = 'true') and not(/function/input/param[@name=$inputName]/@default)">
						<xsl:text>request.isSet</xsl:text>
						<xsl:value-of select="$hungarianName" />
						<xsl:text>() &amp;&amp; </xsl:text>
					</xsl:if>
					<xsl:text>String.valueOf(request.get</xsl:text>
					<xsl:value-of select="$hungarianName" />
					<xsl:text>()).equals("</xsl:text>
						<xsl:call-template name="pcdata_to_java_string">
							<xsl:with-param name="text" select="text()" />
						</xsl:call-template>
					<xsl:text>")</xsl:text>
				</xsl:for-each>
				<xsl:text>) {
            return example</xsl:text>
				<xsl:value-of select="position()" />
				<xsl:text>();
        }</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Write the methods -->
	<xsl:template match="example" mode="method">
		<xsl:param name="api" />
		<xsl:param name="specsdir" />

		<xsl:choose>
			<xsl:when test="@resultcode and starts-with(@resultcode, '_')" />
			<xsl:otherwise>

				<xsl:variable name="resultclass">
					<xsl:choose>
						<xsl:when test="@resultcode and contains(@resultcode, '/')">
							<xsl:value-of select="substring-after(@resultcode, '/')" />
							<xsl:text>Result</xsl:text>
						</xsl:when>
						<xsl:when test="@resultcode and not(contains(@resultcode, '/'))">
							<xsl:value-of select="@resultcode" />
							<xsl:text>Result</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>SuccessfulResult</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:text>

    public Result example</xsl:text>
				<xsl:value-of select="position()" />
				<xsl:text>() throws Exception {
        </xsl:text>
				<xsl:value-of select="$resultclass" />
				<xsl:text> result = new </xsl:text>
				<xsl:value-of select="$resultclass" />
				<xsl:text>();</xsl:text>
				<xsl:for-each select="output-example">

					<xsl:variable name="parametername" select="@name" />
					<xsl:variable name="hungarianName">
						<xsl:call-template name="hungarianUpper">
							<xsl:with-param name="text" select="@name" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="type">
						<xsl:choose>
							<xsl:when test="../@resultcode and not(contains(../@resultcode, '/'))">
								<xsl:variable name="rcd_file" select="concat($specsdir, '/', ../@resultcode, '.rcd')" />
								<xsl:value-of select="document($rcd_file)/resultcode/output/param[@name=$parametername]/@type" />
							</xsl:when>
							<xsl:when test="../@resultcode and contains(../@resultcode, '/')">
								<xsl:variable name="rcd_file" select="concat($specsdir, '/../../', substring-before(../@resultcode, '/'), '/spec/', substring-after(../@resultcode, '/'), '.rcd')" />
								<xsl:value-of select="document($rcd_file)/resultcode/output/param[@name=$parametername]/@type" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="/function/output/param[@name=$parametername]/@type" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="resultText">
						<xsl:call-template name="pcdata_to_java_string">
							<xsl:with-param name="text" select="text()" />
						</xsl:call-template>
					</xsl:variable>

					<xsl:text>
        result.set</xsl:text>
					<xsl:value-of select="$hungarianName" />
					<xsl:text>(</xsl:text>
					<xsl:call-template name="javatype_from_string_for_type">
						<xsl:with-param name="project_node" select="$project_node" />
						<xsl:with-param name="api"        select="$api"        />
						<xsl:with-param name="required" select="'true'" />
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="type"      select="$type"      />
						<xsl:with-param name="variable" select="concat('&quot;', $resultText, '&quot;')" />
					</xsl:call-template>
					<xsl:text>);</xsl:text>
				</xsl:for-each>
				<xsl:apply-templates select="output-data-example/element-example | data-example/element-example">
					<xsl:with-param name="parent" select="'result'" />
					<xsl:with-param name="api" select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="errorcode" select="@resultcode" />
				</xsl:apply-templates>
				<xsl:text>
        return result;
    }</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Examples with output data section -->
	<xsl:template match="output-data-example//element-example | data-example//element-example">
		<xsl:param name="parent" />
		<xsl:param name="api" />
		<xsl:param name="specsdir" />
		<xsl:param name="errorcode" />

		<xsl:variable name="elementName" select="@name" />
		<xsl:variable name="elementObject">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="$elementName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="elementVariable">
			<xsl:choose>
				<xsl:when test="../../data-example or ../../output-data-example">
					<xsl:value-of select="concat(translate(@name, '-', '_'), position())" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($parent, translate(@name, '-', '_'), position())" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>
        </xsl:text>
		<xsl:value-of select="$elementObject" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$elementVariable" />
		<xsl:text> = new </xsl:text>
		<xsl:value-of select="$elementObject" />
		<xsl:text>();</xsl:text>
		<xsl:for-each select="attribute-example">
			<xsl:variable name="attributeName" select="@name" />
			<xsl:variable name="attributeObject">
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text" select="@name" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="type">
				<xsl:choose>
					<xsl:when test="$errorcode and not(contains($errorcode, '/'))">
						<xsl:variable name="rcd_file" select="concat($specsdir, '/', $errorcode, '.rcd')" />
						<xsl:value-of select="document($rcd_file)/resultcode/output/data/element[@name=$elementName]/attribute[@name=$attributeName]/@type" />
					</xsl:when>
					<xsl:when test="$errorcode and contains($errorcode, '/')">
						<xsl:variable name="rcd_file" select="concat($specsdir, '/../../', substring-before($errorcode, '/'), '/spec/', substring-after($errorcode, '/'), '.rcd')" />
						<xsl:value-of select="document($rcd_file)/resultcode/output/data/element[@name=$elementName]/attribute[@name=$attributeName]/@type" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="/function/output/data/element[@name=$elementName]/attribute[@name=$attributeName]/@type" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="resultText">
				<xsl:call-template name="pcdata_to_java_string">
					<xsl:with-param name="text" select="text()" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:text>
        </xsl:text>
			<xsl:value-of select="$elementVariable" />
			<xsl:text>.set</xsl:text>
			<xsl:value-of select="$attributeObject" />
			<xsl:text>(</xsl:text>
			<xsl:call-template name="javatype_from_string_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"      select="$api"   />
				<xsl:with-param name="required" select="'true'" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="type"     select="$type"     />
				<xsl:with-param name="variable" select="concat('&quot;', $resultText, '&quot;')" />
			</xsl:call-template>
			<xsl:text>);</xsl:text>
		</xsl:for-each>

		<xsl:if test="pcdata-example">
			<xsl:text>
        </xsl:text>
			<xsl:value-of select="$elementVariable" />
			<xsl:text>.pcdata("</xsl:text>
			<xsl:call-template name="pcdata_to_java_string">
				<xsl:with-param name="text" select="pcdata-example/text()" />
			</xsl:call-template>
			<xsl:text>");</xsl:text>
		</xsl:if>

		<xsl:apply-templates select="element-example">
			<xsl:with-param name="parent" select="$elementVariable" />
			<xsl:with-param name="api" select="$api" />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="errorcode" select="$errorcode" />
		</xsl:apply-templates>
		<xsl:text>
        </xsl:text>
		<xsl:value-of select="$parent" />
		<xsl:text>.add</xsl:text>
		<xsl:value-of select="$elementObject" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$elementVariable" />
		<xsl:text>);</xsl:text>
	</xsl:template>

</xsl:stylesheet>
