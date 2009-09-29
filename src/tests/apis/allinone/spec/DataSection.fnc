<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.1//EN" "http://xins.sourceforge.net/dtd/function_1_1.dtd">

<function name="DataSection"
rcsversion="$Revision: 1.2 $" rcsdate="$Date: 2005/01/05 13:29:26 $">

	<description>An example of a data section.</description>

	<input>
		<param name="inputText" required="false" type="_text">
			<description>An example of input for a text.</description>
		</param>
	</input>
	<output>
		<data>
			<contains>
				<contained element="user" />
			</contains>
			<!-- provider has 2 attributes name and address.
			     It also can contain a PCDATA text. -->
			<element name="user">
				<description>A user.</description>
				<contains>
					<pcdata />
				</contains>
				<attribute name="name" required="true" type="_text">
					<description>The name of the user.</description>
				</attribute>
				<attribute name="address" required="true" type="_text">
					<description>The address of the user.</description>
				</attribute>
			</element>
		</data>
	</output>

	<example>
		<description>Example with no input.</description>
		<output-data-example>
			<element-example name="user">
				<attribute-example name="name">superuser</attribute-example>
				<attribute-example name="address">12 Madison Avenue</attribute-example>
				<pcdata-example>This user has the root authorisation.</pcdata-example>
			</element-example>
		</output-data-example>
	</example>
	<example>
		<description>Example with input.</description>
		<input-example name="inputText">Doe</input-example>
		<output-data-example>
			<element-example name="user">
				<attribute-example name="name">superuser</attribute-example>
				<attribute-example name="address">12 Madison Avenue</attribute-example>
				<pcdata-example>This user has the root authorisation.</pcdata-example>
			</element-example>
			<element-example name="user">
				<attribute-example name="name">Doe</attribute-example>
				<attribute-example name="address">Unknown</attribute-example>
			</element-example>
		</output-data-example>
	</example>

</function>