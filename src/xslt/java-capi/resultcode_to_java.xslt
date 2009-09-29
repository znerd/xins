<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that converts the definition of an error code to a Java file that
 represents this error code within the CAPI code.

 $Id: resultcode_to_java.xslt,v 1.16 2007/03/09 16:24:03 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<!-- Define parameters -->
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../warning.xslt"    />

	<xsl:variable name="api_node" select="document($api_file)/api" />

	<!-- Handle the <resultcode/> element -->
	<xsl:template match="resultcode">

		<xsl:variable name="resultcode" select="@name" />
		<xsl:variable name="className" select="concat($resultcode, 'Exception')" />

		<!-- Warn if name differs from value -->
		<xsl:if test="(string-length(@value) &gt; 0) and (not(@value = @name))">
			<xsl:call-template name="warn">
				<xsl:with-param name="message">
					<xsl:text>Errorcode name ('</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>') differs from value ('</xsl:text>
					<xsl:value-of select="@value" />
					<xsl:text>'). This may cause confusion and errors.</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

/**
 * Exception that represents the <em>]]></xsl:text>
		   <xsl:value-of select="$resultcode" />
		<xsl:text><![CDATA[</em> error code.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[ extends org.xins.client.AbstractCAPIErrorCodeException {

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    *
    * <p><em>This constructor is internal to XINS. Please do not use it
    * directly. It should only be used by the generated classes. Note that
    * this constructor may be removed at any point in time.</em>
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param resultData
    *    the result data, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0
    *          || resultData  == null
    *          || resultData.{@link org.xins.client.XINSCallResult#getErrorCode() getErrorCode()} == null</code>.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>(org.xins.client.XINSCallRequest            request,
                 org.xins.common.service.TargetDescriptor   target,
                 long                                       duration,
                 org.xins.client.XINSCallResultData         resultData)
   throws IllegalArgumentException {
      super(request, target, duration, resultData);
   }
}
</xsl:text>
	</xsl:template>
</xsl:stylesheet>
