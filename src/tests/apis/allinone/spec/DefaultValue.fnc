<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="DefaultValue"
rcsversion="$Revision: 1.5 $" rcsdate="$Date: 2006/10/18 11:50:28 $">

	<description>An example for default values as input as output parameters.</description>

	<input>
		<param name="inputBoolean" required="false" type="_boolean" default="true">
			<description>An example of input for a boolean with a default value.</description>
		</param>
		<param name="inputInt" required="false" type="_int32" default="33">
			<description>An example of input for an integer with a default value.</description>
		</param>
		<param name="inputText" required="false" type="_text" default="Test of input default &amp; &quot; { é">
			<description>An example of input for a text with a default value.</description>
		</param>
		<data>
			<!-- The data section includes persons. -->
			<contains>
				<contained element="person" />
			</contains>
			<element name="person">
				<description>The person data.</description>
				<attribute name="gender" required="false" type="Salutation" default="Mister">
					<description>The gender of the person.</description>
				</attribute>
				<attribute name="age" required="false" type="Age" default="35">
					<description>The age of the person.</description>
				</attribute>
				<attribute name="size" required="false" type="_int16" default="170">
					<description>The size of the person in centimeters.</description>
				</attribute>
			</element>
		</data>
	</input>
	<output>
		<param name="outputText" required="false" type="_text" default="Test of output default &amp; &quot; { é">
			<description>An example of output for a text with a default value.</description>
		</param>
		<param name="copyAge" required="false" type="Age">
			<description>The first input value sent as age, if any.</description>
		</param>
		<data>
			<!-- The data section includes persons. -->
			<contains>
				<contained element="outputElement" />
			</contains>
			<element name="outputElement">
				<description>An output element.</description>
				<attribute name="outputAttribute" required="false" default="This is a test.">
					<description>An output attribute with a default.</description>
				</attribute>
			</element>
		</data>
	</output>

	<example>
		<description>Example for this data section.</description>
		<input-data-example>
			<element-example name="person">
			</element-example>
			<element-example name="person">
				<attribute-example name="gender">Miss</attribute-example>
				<attribute-example name="age">54</attribute-example>
				<attribute-example name="size">158</attribute-example>
			</element-example>
		</input-data-example>
		<output-example name="outputText">Test of default</output-example>
	</example>

</function>