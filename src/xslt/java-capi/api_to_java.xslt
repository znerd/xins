<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the CAPI.java class.

 $Id: api_to_java.xslt,v 1.139 2007/07/09 09:20:33 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_file" />
	<xsl:param name="project_home" />
	<xsl:param name="apis_dir"     />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="xins_version" />

	<!-- Output is text/plain -->
	<xsl:output method="text" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />
	<xsl:include href="../xml_to_java.xslt" />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<!-- Determine the location of the online specification docs -->
	<xsl:variable name="specdocsURL" select="$project_node/specdocs/@href" />
	<xsl:variable name="hasSpecdocsURL" select="string-length($specdocsURL) &gt; 0" />

	<!-- ***************************************************************** -->
	<!-- Match the root element: api                                       -->
	<!-- ***************************************************************** -->

	<xsl:template match="api">

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />

		<xsl:text><![CDATA[;

/**
 * Client-side API (CAPI) for the remote <em>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</em> API.
 *
 * <p>An instance of this class can be created as follows:
 *
 * <blockquote><pre>String            url = "http://127.0.0.1:8080/]]></xsl:text>
		<xsl:value-of select="$api" />
 		<xsl:text><![CDATA[/";
int      totalTimeOut = 12000; // 12 seconds
int connectionTimeOut = 4000;  //  4 seconds
int     socketTimeOut = 6000;  //  6 seconds
TargetDescriptor   td = new TargetDescriptor(url, totalTimeOut, connectionTimeOut, socketTimeOut);

CAPI capi = new CAPI(td);</pre></blockquote>]]></xsl:text>

		<!-- Display the specdocs URL if it is specified -->
		<xsl:if test="$hasSpecdocsURL">
			<xsl:text><![CDATA[
 *
 * <p>See the <a href="]]></xsl:text>
			<xsl:value-of select="$specdocsURL" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$api" />
			<xsl:text><![CDATA[/">API specification</a>.]]></xsl:text>
		</xsl:if>

		<xsl:text><![CDATA[
 */
public final class CAPI extends org.xins.client.AbstractCAPI {

   /**
    * Secret key used when creating <code>ProtectedPropertyReader</code>
    * instances.
    */
   private static final Object SECRET_KEY = new Object();

   /**
    * Error codes, per function. This field is never <code>null</code>. All
    * keys are function names, such as <code>"ProcessOrder"</code>, and all
    * values are {@link java.util.ArrayList} lists, which contain the
    * supported error codes for that function, as <code>String</code>s, such
    * as <code>"ProcessingFailed"</code>.
    */
   private final java.util.Map _errorCodesPerFunction;
]]></xsl:text>

		<xsl:call-template name="constructor" />
		<xsl:text><![CDATA[
   /**
    * Initializes the map of error codes per function. This class function is
    * called from the constructors.
    *
    * @return
    *    the set of error codes, per function; never <code>null</code>.
    */
   private static final java.util.HashMap initErrorCodesPerFunction() {
      java.util.HashMap map = new java.util.HashMap();
      java.util.ArrayList list;]]></xsl:text>

      <xsl:for-each select="function">
			<xsl:variable name="functionName" select="@name" />
			<xsl:variable name="functionFile" select="concat($specsdir, '/', $functionName, '.fnc')" />
				<xsl:for-each select="document($functionFile)/function">
					<xsl:if test="output/resultcode-ref">
						<xsl:text>

      // Error codes for the '</xsl:text>
						<xsl:value-of select="$functionName" />
						<xsl:text>' function
      list = new java.util.ArrayList();</xsl:text>
						<xsl:for-each select="output/resultcode-ref">
							<xsl:text>
      list.add("</xsl:text>
							<xsl:choose>
								<xsl:when test="contains(@name, '/')">
									<xsl:value-of select="substring-after(@name, '/')" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@name" />
								</xsl:otherwise>
							</xsl:choose>
								<xsl:text>");</xsl:text>
							</xsl:for-each>
							<xsl:text>map.put("</xsl:text>
							<xsl:value-of select="$functionName" />
							<xsl:text>", list);</xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:for-each>

      <xsl:text><![CDATA[
      return map;
   }]]></xsl:text>

		<!-- Loop through all <function/> elements within the <api/> element
		     and process the corresponding .fnc function definition files. -->
		<xsl:for-each select="function">
			<xsl:variable name="functionName" select="@name" />
			<xsl:variable name="functionFile" select="concat($specsdir, '/', $functionName, '.fnc')" />
			<xsl:apply-templates select="document($functionFile)/function">
				<xsl:with-param name="name" select="$functionName" />
			</xsl:apply-templates>
		</xsl:for-each>

		<xsl:text><![CDATA[

   /**
    * Retrieves the name of the API (implementation method).
    *
    * <p>The implementation of this method in class <code>AbstractCAPI</code>
    * returns <code>"]]></xsl:text>
			<xsl:value-of select="$api" />
      <xsl:text><![CDATA["</code>.
    *
    * @return
    *    the name of the API, or <code>null</code> if unknown.
    */
   protected String getAPINameImpl() {
      return "]]></xsl:text>
		<xsl:value-of select="$api" />
      <xsl:text><![CDATA[";
   }

   protected boolean isFunctionalError(String errorCode) {
      return ]]></xsl:text>
		<xsl:variable name="functionalCode">
			<xsl:for-each select="resultcode">
				<xsl:variable name="resultcodeFile">
					<xsl:choose>
						<xsl:when test="contains(@name, '/')">
							<xsl:value-of select="concat($apis_dir, '/', substring-before(@name, '/'), '/spec/', substring-after(@name, '/'), '.rcd')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat($specsdir, '/', @name, '.rcd')" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="resultcodeNode" select="document($resultcodeFile)/resultcode" />
				<xsl:if test="$resultcodeNode/@type = 'functional'">
					<xsl:text>"</xsl:text>
					<xsl:value-of select="$resultcodeNode/@name" />
					<xsl:text>".equals(errorCode) || </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($functionalCode) &gt; 3">
				<xsl:value-of select="substring($functionalCode, 0, string-length($functionalCode) - 3)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>false</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
      <xsl:text><![CDATA[;
   }

   /**
    * Returns the version of XINS used to generate this CAPI class.
    *
    * @return
    *    the version as a {@link String}, e.g. <code>"]]></xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text><![CDATA["</code>;
    *    never <code>null</code>.
    */
   public String getXINSVersion() {
      return "]]></xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text><![CDATA[";
   }

   /**
    * Creates an <code>AbstractCAPIErrorCodeException</code> for the specified
    * error code. If the specified error code is not recognized, then
    * <code>null</code> is returned.
    *
    * @param request
    *    the original request, should not be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, should not
    *    be <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, should be &gt;= 0.
    *
    * @param resultData
    *    the result data, should not be <code>null</code> and should have an
    *    error code set.
    *
    * @return
    *    if the error code is recognized, then a matching
    *    {@link org.xins.client.AbstractCAPIErrorCodeException} instance,
    *    otherwise <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request                   ==   null
    *          || target                    ==   null
    *          || duration                  &lt; 0
    *          || resultData                ==   null
    *          || resultData.getErrorCode() ==   null</code>.
    *
    * @throws UnacceptableErrorCodeXINSCallException
    *    if the specified error code is unacceptable for the specified
    *    function.
    */
   protected final org.xins.client.AbstractCAPIErrorCodeException
   createErrorCodeException(org.xins.client.XINSCallRequest          request,
                            org.xins.common.service.TargetDescriptor target,
                            long                                     duration,
                            org.xins.client.XINSCallResultData       resultData)
   throws IllegalArgumentException,
          org.xins.client.UnacceptableErrorCodeXINSCallException {

      // Check preconditions
      org.xins.common.MandatoryArgumentChecker.check(
         "request", request, "target",  target, "resultData", resultData);
      if (duration < 0L) {
         throw new IllegalArgumentException("duration ("
                                                     + duration + ") < 0L");
      }

      // Determine the error code
      String errorCode = resultData.getErrorCode();
      if (resultData.getErrorCode() == null) {
         throw new IllegalArgumentException(
            "resultData.getErrorCode() == null");
      }
]]></xsl:text>
      <xsl:choose>
         <xsl:when test="resultcode">
            <xsl:for-each select="resultcode">
							<xsl:variable name="name">
								<xsl:choose>
									<xsl:when test="contains(@name, '/')">
										<xsl:value-of select="substring-after(@name, '/')" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@name" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
               <xsl:text>
      </xsl:text>
               <xsl:choose>
                  <xsl:when test="position() = 1">
                     <xsl:text>
      if ("</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:text>
      } else if ("</xsl:text>
                  </xsl:otherwise>
               </xsl:choose>
               <xsl:value-of select="$name" />
               <xsl:text>".equals(errorCode)) {
         String function  = request.getFunctionName();
         java.util.ArrayList supported = (java.util.ArrayList) _errorCodesPerFunction.get(function);
         if (supported == null || !supported.contains("</xsl:text>
               <xsl:value-of select="$name" />
               <xsl:text>")) {
            throw new org.xins.client.UnacceptableErrorCodeXINSCallException(
               request, target, duration, resultData);
         } else {
            return new </xsl:text>
               <xsl:value-of select="$name" />
               <xsl:text>Exception(request, target, duration, resultData);
         }</xsl:text>
            </xsl:for-each>
            <xsl:text>
      } else {
         return null;
      }</xsl:text>
         </xsl:when>
         <xsl:otherwise>
            <xsl:text>
      // No error codes defined for this API
      return null;</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:text>
   }
}
</xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print the constructors                                            -->
	<!-- ***************************************************************** -->

	<xsl:template name="constructor">
		<xsl:text><![CDATA[

   /**
    * Constructs a new <code>CAPI</code> object for the specified API from a
    * set of properties.
    *
    * @param properties
    *    the properties to create a <code>CAPI</code> object for, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>.
    *
    * @throws org.xins.common.collections.MissingRequiredPropertyException
    *    if a required property is missing in the specified properties set.
    *
    * @throws org.xins.common.collections.InvalidPropertyValueException
    *    if one of the properties in the specified properties set is used to
    *    create a <code>CAPI</code> instance but its value is considered
    *    invalid.
    */
   public CAPI(org.xins.common.collections.PropertyReader properties)
   throws IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {
      super(properties, "]]></xsl:text>
		<xsl:value-of select="//api/@name" />
		<xsl:text><![CDATA[");
      _errorCodesPerFunction = initErrorCodesPerFunction();
   }

   /**
    * Constructs a new <code>CAPI</code> object for the specified API from a
    * set of properties, specifying the API name to assume.
    *
    * @param properties
    *    the properties to create a <code>CAPI</code> object for, cannot be
    *    <code>null</code>.
    *
    * @param apiName
    *    the name of the API, cannot be <code>null</code> and must be a valid
    *    API name.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null || apiName == null</code> or if
    *    <code>apiName</code> is not considered to be a valid API name.
    *
    * @throws org.xins.common.collections.MissingRequiredPropertyException
    *    if a required property is missing in the specified properties set.
    *
    * @throws org.xins.common.collections.InvalidPropertyValueException
    *    if one of the properties in the specified properties set is used to
    *    create a <code>CAPI</code> instance but its value is considered
    *    invalid.
    */
   public CAPI(org.xins.common.collections.PropertyReader properties, String apiName)
   throws IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {
      super(properties, apiName);
      _errorCodesPerFunction = initErrorCodesPerFunction();
   }

   /**
    * Constructs a new <code>CAPI</code> object, using the specified
    * <code>Descriptor</code>.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws org.xins.common.service.UnsupportedProtocolException
    *    if any of the target descriptors specifies an unsupported protocol
    *    (<em>since XINS 1.1.0</em>).
    */
   public CAPI(org.xins.common.service.Descriptor descriptor)
   throws IllegalArgumentException,
          org.xins.common.service.UnsupportedProtocolException {
      super(descriptor);
      _errorCodesPerFunction = initErrorCodesPerFunction();
   }]]></xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print a method to call a single function                          -->
	<!-- ***************************************************************** -->

	<xsl:template match="function">

		<!-- Define parameters -->
		<xsl:param name="name" />

		<!-- Determine the name of the call methods -->
		<xsl:variable name="methodName" select="concat('call', $name)" />

		<!-- Always return a <FunctionName>Result object -->
		<xsl:variable name="returnType" select="concat($name, 'Result')" />

		<!-- Check name set in function definition file -->
		<xsl:if test="string-length(@name) &gt; 0 and not($name = @name)">
			<xsl:message terminate="yes">
				<xsl:value-of select="concat($name, '!=', @name)" />
				<xsl:text>. The name function definition file differs from name defined in API definition file.</xsl:text>
			</xsl:message>
		</xsl:if>

		<!-- Print method that accepts the a request object only -->
		<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em>
    * function using the specified request object.
    *
    * <p>Description: ]]></xsl:text>
		<xsl:value-of select="description/text()" />
    <xsl:text><![CDATA[
    *
    * <p>Generated from function specification version ]]></xsl:text>
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision" select="@rcsversion" />
		</xsl:call-template>
		<xsl:text>.</xsl:text>
		<xsl:if test="$hasSpecdocsURL">
			<xsl:text><![CDATA[
    * See the
    * <a href="]]></xsl:text>
			<xsl:value-of select="$specdocsURL" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$api" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[.html">online function specification</a>.]]></xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
    *
    * @param request
    *    the request, cannot be <code>null</code>.
    *
    * @return
    *    the result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws org.xins.client.UnacceptableRequestException
    *    if the request is considered to be unacceptable; this is determined
    *    by calling
    *    <code>request.</code>{@link org.xins.client.AbstractCAPICallRequest#checkParameters() checkParameters()}.]]></xsl:text>
		<xsl:call-template name="javadoc-exceptions">
			<xsl:with-param name="package" select="$package" />
		</xsl:call-template>
		<xsl:text>
   public </xsl:text>
		<xsl:value-of select="$returnType" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text>Request request)
   throws IllegalArgumentException,
          org.xins.client.UnacceptableRequestException,
          </xsl:text>
		<xsl:call-template name="throws-exceptions">
			<xsl:with-param name="package" select="$package" />
		</xsl:call-template>
		<xsl:text>
      // Execute the call request
      org.xins.client.XINSCallResult result = callImpl(request);

      return new </xsl:text>

		<xsl:value-of select="$returnType" />
		<xsl:text>(result);</xsl:text>
		<xsl:text>
   }</xsl:text>

		<!-- Print method that accepts the individual parameters -->
		<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em>
    * function with the specified parameters.
    *
    * <p>Description: ]]></xsl:text>
		<xsl:value-of select="description/text()" />
    <xsl:text><![CDATA[
    *
    * <p>Generated from function specification version ]]></xsl:text>
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision" select="@rcsversion" />
		</xsl:call-template>
		<xsl:text>.</xsl:text>
		<xsl:if test="$hasSpecdocsURL">
			<xsl:text><![CDATA[
    * See the
    * <a href="]]></xsl:text>
			<xsl:value-of select="$specdocsURL" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$api" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[.html">online function specification</a>.]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates select="input/param" mode="javadoc" />
		<xsl:if test="input/data/element">
			<xsl:text><![CDATA[
    *
    * @param _dataSection
    *    the data section for the request, or <code>null</code> if the data
    *    section should be empty.]]></xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
    *
    * @return
    *    the result, not <code>null</code>.]]></xsl:text>
		<xsl:call-template name="javadoc-exceptions">
			<xsl:with-param name="package" select="$package" />
		</xsl:call-template>
		<xsl:text>
   public </xsl:text>
		<xsl:value-of select="$returnType" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:apply-templates select="input/param" mode="methodSignature" />
		<xsl:if test="input/data/element">
			<xsl:if test="input/param">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:text>org.xins.common.xml.Element _dataSection</xsl:text>
		</xsl:if>
		<xsl:text>)
   throws </xsl:text>
		<xsl:call-template name="throws-exceptions">
			<xsl:with-param name="package" select="$package" />
		</xsl:call-template>
		<xsl:text>
      // Get the XINS service caller
      org.xins.client.XINSServiceCaller caller = getCaller();</xsl:text>
		<xsl:if test="input/param">
			<xsl:text>

      // Store the input parameters in a PropertyReader
      org.xins.common.collections.ProtectedPropertyReader params = new org.xins.common.collections.ProtectedPropertyReader(SECRET_KEY);</xsl:text>
			<xsl:apply-templates select="input/param" mode="store" />
		</xsl:if>

		<xsl:text>

      // Construct a call request
      org.xins.client.XINSCallRequest request = new org.xins.client.XINSCallRequest(</xsl:text>
		<xsl:text>"</xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text>", </xsl:text>
		<xsl:choose>
			<xsl:when test="input/param">
				<xsl:text>params</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>null</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="input/data/element">
			<xsl:text>, _dataSection</xsl:text>
		</xsl:if>
		<xsl:text>);

      // Execute the call request
      org.xins.client.XINSCallResult result = caller.call(request);

      return new </xsl:text>

		<xsl:value-of select="$returnType" />
		<xsl:text>(result);</xsl:text>
		<xsl:text>
   }</xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Prints an @param section for an input parameter.                  -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="javadoc">

		<!-- Determine if the input parameter is mandatory. -->
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java primary data type or class for the input
		     parameter -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine if $javatype is a Java primary data type -->
		<xsl:variable name="typeIsPrimary">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javatype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the description for the input parameter -->
		<xsl:variable name="origDescription" select="normalize-space(description/text())" />
		<xsl:variable name="description">
			<xsl:choose>
				<xsl:when test="string-length($origDescription) = 0">
					<xsl:text><![CDATA[the value of the <em>]]></xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text><![CDATA[</em> input parameter.]]></xsl:text>
				</xsl:when>
				<xsl:when test="substring($origDescription, string-length($origDescription), 1) = '.'">
					<xsl:value-of select="translate(substring($origDescription,1,1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
					<xsl:value-of select="substring($origDescription,2)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="translate(substring($origDescription,1,1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
					<xsl:value-of select="substring($origDescription,2)" />
					<xsl:text>.</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java variable name -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Perform the actual printing -->
		<xsl:text>
    *
    * @param </xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text>
    *    </xsl:text>
		<xsl:value-of select="$description" />
		<xsl:if test="$typeIsPrimary = 'false'">
			<xsl:choose>
				<xsl:when test="$required = 'true'">
					<xsl:text><![CDATA[
    *    Cannot be <code>null</code>.]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[
    *    Can be <code>null</code>.]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Prints an argument definition for a function calling method       -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="methodSignature">

		<!-- Determine if this parameter is required -->
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java class or primary data type -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- The name of the variable used in code for this parameter -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="position() &gt; 1">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$javaVariable" />
	</xsl:template>

	<!-- ***************************************************************** -->
	<!-- Prints the Javadoc @throws section for call methods.              -->
	<!-- ***************************************************************** -->

	<xsl:template name="javadoc-exceptions">
		<xsl:param name="package" />

		<xsl:text>
    *
    * @throws org.xins.common.service.GenericCallException
    *    if the first call attempt failed due to a generic reason and all the
    *    other call attempts (if any) failed as well.
    *
    * @throws org.xins.common.http.HTTPCallException
    *    if the first call attempt failed due to an HTTP-related reason and
    *    all the other call attempts (if any) failed as well.</xsl:text>

        <xsl:for-each select="output/resultcode-ref">
					<xsl:variable name="errorcode">
						<xsl:choose>
							<xsl:when test="contains(@name, '/')">
								<xsl:value-of select="substring-after(@name, '/')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:text>
    *
    * @throws </xsl:text>
            <xsl:value-of select="$package" />
            <xsl:text>.</xsl:text>
            <xsl:value-of select="$errorcode" />
            <xsl:text><![CDATA[Exception
    *    if the first call attempt failed due to the error code
    *    <em>]]></xsl:text>
            <xsl:value-of select="$errorcode" />
            <xsl:text><![CDATA[</em> being returned by the other end; and
    *    all the other call attempts (if any) failed as well;
    *    note that this exception is derived from
    *    {@link org.xins.client.XINSCallException}, so if that one is caught,
    *    then this one is also caught.]]></xsl:text>
        </xsl:for-each>

        <xsl:text>
    *
    * @throws org.xins.client.XINSCallException
    *    if the first call attempt failed due to a XINS-related reason and
    *    all the other call attempts (if any) failed as well.
    */</xsl:text>
	</xsl:template>

	<!-- ***************************************************************** -->
	<!-- Prints the Javadoc @throws section for call methods.              -->
	<!-- ***************************************************************** -->

	<xsl:template name="throws-exceptions">
		<xsl:param name="package" />
		
			<xsl:text>org.xins.common.service.GenericCallException,
          org.xins.common.http.HTTPCallException,</xsl:text>
        <xsl:for-each select="output/resultcode-ref">
            <xsl:text>
          </xsl:text>
            <xsl:value-of select="$package" />
            <xsl:text>.</xsl:text>
						<xsl:choose>
							<xsl:when test="contains(@name, '/')">
								<xsl:value-of select="substring-after(@name, '/')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name" />
							</xsl:otherwise>
						</xsl:choose>
            <xsl:text>Exception,</xsl:text>
        </xsl:for-each>
        <xsl:text>
          org.xins.client.XINSCallException {
</xsl:text>
	</xsl:template>

	<!-- ***************************************************************** -->
	<!-- Print code that will store an input parameter in a variable       -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="store">
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- The name of the variable used in code for this parameter -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="@required = 'false'" >
				<xsl:text>
      if (</xsl:text>
				<xsl:value-of select="$javaVariable" />
				<xsl:text> != null) {
         params.set(SECRET_KEY, "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", </xsl:text>
				<xsl:call-template name="javatype_to_string_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="@type" />
					<xsl:with-param name="variable" select="$javaVariable" />
				</xsl:call-template>
				<xsl:text>);
      }</xsl:text>
				<xsl:if test="@default">
					<xsl:text> else {
         params.set(SECRET_KEY, "</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>", "</xsl:text>
					<xsl:call-template name="xml_to_java_string">
						<xsl:with-param name="text" select="@default" />
					</xsl:call-template>
					<xsl:text>");
      }</xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      params.set(SECRET_KEY, "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", </xsl:text>
				<xsl:call-template name="javatype_to_string_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="@type" />
					<xsl:with-param name="variable" select="$javaVariable" />
				</xsl:call-template>
				<xsl:text>);</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
