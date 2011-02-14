<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the java representation of the type.

 $Id: type_to_java.xslt,v 1.40 2007/08/27 11:17:44 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt"    />
	<xsl:include href="../xml_to_java.xslt" />
	<xsl:include href="../java.xslt"          />
	<xsl:include href="../types.xslt"         />

	<xsl:output method="text" />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="type">

		<xsl:variable name="type" select="@name" />
		<xsl:variable name="classname">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text">
					<xsl:value-of select="$type" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="kind">
			<!-- XXX Can this be optimized ? -->
			<xsl:choose>
				<xsl:when test="enum">enum</xsl:when>
				<xsl:when test="pattern">pattern</xsl:when>
				<xsl:when test="properties">properties</xsl:when>
				<xsl:when test="int8">int8</xsl:when>
				<xsl:when test="int16">int16</xsl:when>
				<xsl:when test="int32">int32</xsl:when>
				<xsl:when test="int64">int64</xsl:when>
				<xsl:when test="float32">float32</xsl:when>
				<xsl:when test="float64">float64</xsl:when>
				<xsl:when test="base64">base64</xsl:when>
				<xsl:when test="hex">hex</xsl:when>
				<xsl:when test="list">list</xsl:when>
				<xsl:when test="set">set</xsl:when>
				<xsl:when test="decimal">decimal</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="superclass">
			<xsl:choose>
				<xsl:when test="$kind = 'enum'">org.xins.common.types.EnumType</xsl:when>
				<xsl:when test="$kind = 'pattern'">org.xins.common.types.PatternType</xsl:when>
				<xsl:when test="$kind = 'properties'">org.xins.common.types.standard.Properties</xsl:when>
				<xsl:when test="$kind = 'int8'">org.xins.common.types.standard.Int8</xsl:when>
				<xsl:when test="$kind = 'int16'">org.xins.common.types.standard.Int16</xsl:when>
				<xsl:when test="$kind = 'int32'">org.xins.common.types.standard.Int32</xsl:when>
				<xsl:when test="$kind = 'int64'">org.xins.common.types.standard.Int64</xsl:when>
				<xsl:when test="$kind = 'float32'">org.xins.common.types.standard.Float32</xsl:when>
				<xsl:when test="$kind = 'float64'">org.xins.common.types.standard.Float64</xsl:when>
				<xsl:when test="$kind = 'base64'">org.xins.common.types.standard.Base64</xsl:when>
				<xsl:when test="$kind = 'hex'">org.xins.common.types.standard.Hex</xsl:when>
				<xsl:when test="$kind = 'list'">org.xins.common.types.List</xsl:when>
				<xsl:when test="$kind = 'set'">org.xins.common.types.List</xsl:when>
				<xsl:when test="$kind = 'decimal'">org.xins.common.types.Decimal</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;

/**
 * </xsl:text>
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text">
				<xsl:value-of select="$kind" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ type <em>]]></xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text><![CDATA[</em>.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> extends </xsl:text>
		<xsl:value-of select="$superclass" />
		<xsl:text> {</xsl:text>

		<xsl:if test="$kind = 'enum'">
			<xsl:apply-templates select="enum/item" mode="field" />
		</xsl:if>
		<xsl:text><![CDATA[
   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> SINGLETON = new </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>();</xsl:text>

		<!-- The constructor -->
		<xsl:text><![CDATA[

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>()</xsl:text>
		<xsl:text> {
      super("</xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text>", </xsl:text>
		<xsl:choose>
			<xsl:when test="$kind = 'enum'">
				<xsl:text>new org.xins.common.types.EnumItem[] {</xsl:text>
				<xsl:for-each select="enum/item">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:variable name="itemName">
						<xsl:choose>
							<xsl:when test="@name">
								<xsl:value-of select="@name" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@value" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="name_for_itemfield">
						<xsl:with-param name="itemName" select="$itemName" />
					</xsl:call-template>
					<!--xsl:text>
         new Item("</xsl:text>
					<xsl:choose>
						<xsl:when test="@name">
							<xsl:value-of select="@name" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@value" />
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text>", "</xsl:text>
					<xsl:value-of select="@value" />
					<xsl:text>")</xsl:text-->
				</xsl:for-each>
				<xsl:text>}</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'pattern'">
				<xsl:text>"</xsl:text>
				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text" select="pattern/text()" />
				</xsl:call-template>
				<xsl:text>"</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'properties'">
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="properties/@nameType" />
				</xsl:call-template>
				<xsl:text>.SINGLETON, </xsl:text>
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="properties/@valueType" />
				</xsl:call-template>
				<xsl:text>.SINGLETON</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'int8'">
				<xsl:choose>
					<xsl:when test="int8/@min">
						<xsl:text>(byte)</xsl:text>
						<xsl:value-of select="int8/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Byte.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int8/@max">
						<xsl:text>(byte)</xsl:text>
						<xsl:value-of select="int8/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Byte.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'int16'">
				<xsl:choose>
					<xsl:when test="int16/@min">
						<xsl:text>(short)</xsl:text>
						<xsl:value-of select="int16/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Short.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int16/@max">
						<xsl:text>(short)</xsl:text>
						<xsl:value-of select="int16/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Short.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'int32'">
				<xsl:choose>
					<xsl:when test="int32/@min">
						<xsl:value-of select="int32/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Integer.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int32/@max">
						<xsl:value-of select="int32/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Integer.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'int64'">
				<xsl:choose>
					<xsl:when test="int64/@min">
						<xsl:value-of select="int64/@min" />
						<xsl:text>L</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Long.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int64/@max">
						<xsl:value-of select="int64/@max" />
						<xsl:text>L</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Long.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'float32'">
				<xsl:choose>
					<xsl:when test="float32/@min">
						<xsl:value-of select="float32/@min" />
						<xsl:text>F</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Float.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="float32/@max">
						<xsl:value-of select="float32/@max" />
						<xsl:text>F</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Float.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'float64'">
				<xsl:choose>
					<xsl:when test="float64/@min">
						<xsl:value-of select="float64/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Double.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="float64/@max">
						<xsl:value-of select="float64/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Double.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'decimal'">
				<xsl:choose>
					<xsl:when test="float64/@min">
						<xsl:value-of select="float64/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Double.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="float64/@max">
						<xsl:value-of select="float64/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Double.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'base64'">
				<xsl:choose>
					<xsl:when test="base64/@min">
						<xsl:value-of select="base64/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>0</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="base64/@max">
						<xsl:value-of select="base64/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Integer.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'hex'">
				<xsl:choose>
					<xsl:when test="hex/@min">
						<xsl:value-of select="hex/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>0</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="hex/@max">
						<xsl:value-of select="hex/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Integer.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'list'">
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="list/@type" />
				</xsl:call-template>
				<xsl:text>.SINGLETON</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'set'">
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="set/@type" />
				</xsl:call-template>
				<xsl:text>.SINGLETON</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text>);
   }</xsl:text>

		<!-- Pattern type -->
		<xsl:choose>
			<xsl:when test="$kind = 'pattern'">
				<xsl:text><![CDATA[

   /**
    * Converts the specified character string to a value for this type. The
    * character string cannot be <code>null</code>. If it is, then an
    * exception is thrown.
    *
    * @param string
    *    the character string to convert to a value for this type, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the converted value for this type, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws org.xins.common.types.TypeValueException
    *    if the specified character string is not considered valid for this
    *    type.
    */
   public static String fromStringForRequired(String string)
   throws IllegalArgumentException,
          org.xins.common.types.TypeValueException {

      // Check preconditions
      org.xins.common.MandatoryArgumentChecker.check("string", string);

      return (String) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified character string -or <code>null</code>- to a
    * value for this type. The character string can be <code>null</code> in
    * which case <code>null</code> is also returned.
    *
    * @param string
    *    the character string to convert to a value for this type, can be
    *    <code>null</code>.
    *
    * @return
    *    the converted value for this type, or <code>null</code>.
    *
    * @throws org.xins.common.types.TypeValueException
    *    if the specified character string is not considered valid for this
    *    type.
    */
   public static String fromStringForOptional(String string)
   throws org.xins.common.types.TypeValueException {
      return (String) SINGLETON.fromString(string);
   }
]]></xsl:text>
			</xsl:when>

			<!-- Number type -->
			<xsl:when test="$kind = 'int8' or $kind = 'int16' or $kind = 'int32' or $kind = 'int64' or $kind = 'float32' or $kind = 'float64'">
				<xsl:variable name="required_object">
					<xsl:call-template name="javatype_for_type">
						<xsl:with-param name="project_node"     select="$project_node" />
						<xsl:with-param name="specsdir"     select="$specsdir" />
						<xsl:with-param name="api"     select="$api" />
						<xsl:with-param name="type"     select="concat('_', $kind)" />
						<xsl:with-param name="required" select="'true'" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="optional_object">
					<xsl:call-template name="javatype_for_type">
						<xsl:with-param name="project_node"     select="$project_node" />
						<xsl:with-param name="specsdir"     select="$specsdir" />
						<xsl:with-param name="api"     select="$api" />
						<xsl:with-param name="type"     select="concat('_', $kind)" />
						<xsl:with-param name="required" select="'false'" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:text><![CDATA[

   /**
    * Converts the specified character string to a value for this type. The
    * character string cannot be <code>null</code>. If it is, then an
    * exception is thrown.
    *
    * @param string
    *    the character string to convert to a value for this type, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the converted value for this type.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws org.xins.common.types.TypeValueException
    *    if the specified character string is not considered valid for this
    *    type.
    */
   public static ]]></xsl:text>
				<xsl:value-of select="$required_object"/>
				<xsl:text> fromStringForRequired(String string)
   throws IllegalArgumentException,
          org.xins.common.types.TypeValueException {

      // Check preconditions
      org.xins.common.MandatoryArgumentChecker.check("string", string);

      return ((</xsl:text>
				<xsl:value-of select="$optional_object"/>
				<xsl:text>)SINGLETON.fromString(string)).</xsl:text>
				<xsl:value-of select="$required_object"/>
				<xsl:text>Value();
   }
</xsl:text>
				<xsl:text><![CDATA[

   /**
    * Converts the specified character string -or <code>null</code>- to a
    * value for this type. The character string can be <code>null</code> in
    * which case <code>null</code> is also returned.
    *
    * @param string
    *    the character string to convert to a value for this type, can be
    *    <code>null</code>.
    *
    * @return
    *    the converted value for this type, or <code>null</code>.
    *
    * @throws org.xins.common.types.TypeValueException
    *    if the specified character string is not considered valid for this
    *    type.
    */
   public static ]]></xsl:text>
				<xsl:value-of select="$optional_object"/>
				<xsl:text> fromStringForOptional(String string)
   throws org.xins.common.types.TypeValueException {
      return (</xsl:text>
				<xsl:value-of select="$optional_object"/>
				<xsl:text>) SINGLETON.fromString(string);
   }</xsl:text>

			</xsl:when>
			<!-- XXX: otherwise? -->
		</xsl:choose>

		<xsl:if test="$kind = 'list' or $kind = 'set'">
			<xsl:variable name="innertype">
				<xsl:choose>
					<xsl:when test="$kind = 'list'">
						<xsl:value-of select="list/@type" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="set/@type" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="javasimpletype">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="'true'"        />
					<xsl:with-param name="type"         select="$innertype"    />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="javaoptionaltype">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_node" select="$project_node" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="'false'"       />
					<xsl:with-param name="type"         select="$innertype"    />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="typeIsPrimary">
				<xsl:call-template name="is_java_datatype">
					<xsl:with-param name="text" select="$javasimpletype" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:text><![CDATA[

   public org.xins.common.types.ItemList createList() {
      return new Value();
   }

   public static Value fromStringForRequired(String string)
   throws IllegalArgumentException,
          org.xins.common.types.TypeValueException {

      // Check preconditions
      org.xins.common.MandatoryArgumentChecker.check("string", string);

      return (Value) SINGLETON.fromString(string);
   }

   public static Value fromStringForOptional(String string)
   throws org.xins.common.types.TypeValueException {
      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Inner class that represents a ]]></xsl:text>
			<xsl:value-of select="$kind" />
	 		<xsl:text> of </xsl:text>
			<xsl:value-of select="$javasimpletype" />
			<xsl:text>.
    */
   public static final class Value extends org.xins.common.types.ItemList {</xsl:text>

			<xsl:if test="$kind = 'set'">
	      <xsl:text>
      /**
       * Creates a new set.
       */
      public Value() {
         super(true);
      }
</xsl:text>
			</xsl:if>
			<xsl:text>

      /**
       * Add a new element in the </xsl:text>
			<xsl:value-of select="$kind" />
			<xsl:text>.
       *
       * @param value
       *    the new value to add</xsl:text>
			<xsl:if test="$typeIsPrimary = 'false'">
	      <xsl:text><![CDATA[, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>value == null</code>]]></xsl:text>
			</xsl:if>
			<xsl:text>.
       */
      public void add(</xsl:text>
			<xsl:value-of select="$javasimpletype" />
			<xsl:text> value) {
			</xsl:text>
			<xsl:if test="$typeIsPrimary = 'false'">
         org.xins.common.MandatoryArgumentChecker.check("value", value);
			</xsl:if>
			<xsl:variable name="valueasobject">
				<xsl:choose>
					<xsl:when test="$typeIsPrimary = 'true'">
						<xsl:text>new </xsl:text>
						<xsl:value-of select="$javaoptionaltype" />
						<xsl:text>(value)</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>value</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:text>
         addItem(</xsl:text>
			<xsl:value-of select="$valueasobject" />
      <xsl:text>);
      }

      /**
       * Get an element from the </xsl:text>
			<xsl:value-of select="$kind" />
			<xsl:text>.
       *
       * @param index
       *    The position of the required element.
       *
       * @return
       *    The element at the specified position</xsl:text>
			<xsl:if test="$typeIsPrimary = 'false'">
	      <xsl:text><![CDATA[, cannot be <code>null</code>]]></xsl:text>
			</xsl:if>
      <xsl:text>.
       */
      public </xsl:text>
			<xsl:value-of select="$javasimpletype" />
			<xsl:text> get(int index) {
         return </xsl:text>
			<xsl:choose>
				<xsl:when test="$typeIsPrimary = 'true'">
					<xsl:text>((</xsl:text>
					<xsl:value-of select="$javaoptionaltype" />
					<xsl:text>) getItem(index)).</xsl:text>
					<xsl:value-of select="$javasimpletype" />
					<xsl:text>Value()</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>(</xsl:text>
					<xsl:value-of select="$javasimpletype" />
					<xsl:text>) </xsl:text>
					<xsl:text>getItem(index)</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="$typeIsPrimary = 'false'">
			</xsl:if>
			<xsl:text>;
      }
   }</xsl:text>
		</xsl:if>

		<xsl:if test="$kind = 'enum'">
			<xsl:text><![CDATA[

   public static Item fromStringForRequired(String string)
   throws IllegalArgumentException,
          org.xins.common.types.TypeValueException {

      // Check preconditions
      org.xins.common.MandatoryArgumentChecker.check("string", string);

      return getItemByValue(string);
   }

   public static Item fromStringForOptional(String string)
   throws org.xins.common.types.TypeValueException {
      return getItemByValue(string);
   }

   /**
    * Gets the <code>Item</code> for the specified string value.
    *
    * @param value
    *    the value for which to lookup the matching {@link Item} instance,
    *    can be <code>null</code>, in which case <code>null</code> is also
    *    returned.
    *
    * @return
    *    the matching {@link Item} instance, or <code>null</code> if and only
    *    if <code>value == null</code>.
    *
    * @throws org.xins.common.types.TypeValueException
    *    if the specified value does not denote an existing item.
    */
   public static Item getItemByValue(String value)
   throws org.xins.common.types.TypeValueException {

      if (value != null) {
         Object o = SINGLETON._valuesToItems.get(value);
         if (o != null) {
            return (Item) o;
         } else {
            throw new org.xins.common.types.TypeValueException(SINGLETON, value);
         }
      } else {
         return null;
      }
   }

   /**
    * Gets the <code>Item</code> for the specified string name.
    *
    * @param name
    *    the name for which to lookup the matching {@link Item} instance,
    *    can be <code>null</code>, in which case <code>null</code> is also
    *    returned.
    *
    * @return
    *    the matching {@link Item} instance, or <code>null</code> if and only
    *    if <code>name == null</code>.
    *
    * @throws org.xins.common.types.TypeValueException
    *    if the specified name does not denote an existing item.
    */
   public static Item getItemByName(String name)
   throws org.xins.common.types.TypeValueException {

      if (name != null) {
         Object o = SINGLETON._namesToItems.get(name);
         if (o != null) {
            return (Item) o;
         } else {
            throw new org.xins.common.types.TypeValueException(SINGLETON, name);
         }
      } else {
         return null;
      }
   }

   public Object fromStringImpl(String value)
   throws org.xins.common.types.TypeValueException {
      return getItemByValue(value);
   }

   /**
    * Item of the <em>]]></xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text><![CDATA[</em> enumeration type.
    * The following items are defined in this type:
    *
    * <ul>]]></xsl:text>
		<xsl:for-each select="enum/item">
			<xsl:variable name="itemName">
				<xsl:choose>
					<xsl:when test="@name">
						<xsl:value-of select="@name" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@value" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="fieldName">
				<xsl:call-template name="name_for_itemfield">
					<xsl:with-param name="itemName" select="$itemName" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:text><![CDATA[
    *    <li>{@link #]]></xsl:text>
			<xsl:value-of select="$fieldName" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="$fieldName" />
			<xsl:text>}</xsl:text>
		</xsl:for-each>
		<xsl:text><![CDATA[
    * </ul>
    */
   public static final class Item
   extends org.xins.common.types.EnumItem {

      /**
       * Constructs a new <code>Item</code> with the specified name and value.
       *
       * @param name
       *    the symbolic (friendly) name for the enumeration value, not
       *    <code>null</code>.
       *
       * @param value
       *    the actual value of the enumeration item, not <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null || value == null</code>.
       */
      private Item(String name, String value)
      throws IllegalArgumentException {
         super(name, value);
      }
   }]]></xsl:text>
		</xsl:if>

		<xsl:text>

   public String getDescription() {
      return "</xsl:text>
		<xsl:call-template name="pcdata_to_java_string">
			<xsl:with-param name="text" select="description/text()" />
		</xsl:call-template>
		<xsl:text>";
   }
</xsl:text>

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="enum/item" mode="field">
		<xsl:variable name="itemName">
			<xsl:choose>
				<xsl:when test="@name">
					<xsl:value-of select="@name" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@value" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text><![CDATA[

   /**
    * The <em>]]></xsl:text>
		<xsl:value-of select="$itemName" />
		<xsl:text><![CDATA[</em> item.
    */
   public static final Item ]]></xsl:text>
		<xsl:call-template name="name_for_itemfield">
			<xsl:with-param name="itemName" select="$itemName" />
		</xsl:call-template>
		<xsl:text> = new Item("</xsl:text>
		<xsl:value-of select="$itemName" />
		<xsl:text>", "</xsl:text>
		<xsl:call-template name="xml_to_java_string">
			<xsl:with-param name="text" select="@value" />
		</xsl:call-template>
		<xsl:text>");
</xsl:text>
	</xsl:template>

	<xsl:template name="name_for_itemfield">
		<xsl:param name="itemName" />
		<xsl:call-template name="toupper">
			<xsl:with-param name="text" select="translate($itemName, ' .-/,;:!?*+=%#(){}[]&amp;&quot;&lt;&gt;', '________________________')" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
