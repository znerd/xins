<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="DefinedTypes"
rcsversion="$Revision: 1.3 $" rcsdate="$Date: 2006/09/25 09:03:25 $">

	<description>A function to test the types defined in a .typ file.</description>
	<input>
		<param name="inputIP" required="false" type="IPAddress">
			<description>An example of input for a pattern type.</description>
		</param>
		<param name="inputSalutation" required="true" type="Salutation">
			<description>An example of input for an enum type.</description>
		</param>
		<param name="inputAge" required="true" type="Age">
			<description>An example of input for a int8 type with a minimum and maximum.</description>
		</param>
		<param name="inputList" required="false" type="TextList">
			<description>An example of input for a list.</description>
		</param>
		<param name="inputShared" required="false" type="portal/Username">
			<description>An example of input for a shared type.</description>
		</param>
	</input>
	<output>
		<param name="outputIP" required="false" type="IPAddress">
			<description>An example of output for a pattern type.</description>
		</param>
		<param name="outputSalutation" required="true" type="Salutation">
			<description>An example of output for an enum type.</description>
		</param>
		<param name="outputAge" required="false" type="Age">
			<description>An example of output for a int8 type with a minimum and maximum.</description>
		</param>
		<param name="outputList" required="false" type="TextList">
			<description>An example of output for a list.</description>
		</param>
		<param name="outputProperties" required="false" type="AgeNameProperties">
			<description>An example of output for a list.</description>
		</param>
		<param name="outputShared" required="false" type="portal/Password">
			<description>An example of output shared type.</description>
		</param>
	</output>

	<example resultcode="_InvalidRequest">
		<description>Invalid parameter.</description>
		<input-example name="inputIP">8.2</input-example>
		<input-example name="inputSalutation">Sir</input-example>
		<input-example name="inputAge">100</input-example>
		<input-example name="inputList">Hello</input-example>
		<data-example>
			<element-example name="invalid-value-for-type">
				<attribute-example name="type">IPAddress</attribute-example>
				<attribute-example name="param">inputIP</attribute-example>
			</element-example>
			<element-example name="invalid-value-for-type">
				<attribute-example name="type">Salutation</attribute-example>
				<attribute-example name="param">inputSalutation</attribute-example>
			</element-example>
			<element-example name="invalid-value-for-type">
				<attribute-example name="type">Age</attribute-example>
				<attribute-example name="param">inputAge</attribute-example>
			</element-example>
		</data-example>
	</example>
	<example>
		<description>Successful example.</description>
		<input-example name="inputIP">192.200.0.1</input-example>
		<input-example name="inputSalutation">Miss</input-example>
		<input-example name="inputAge">33</input-example>
		<input-example name="inputList">Hello&amp;Bonjour&amp;Hoi&amp;Hola</input-example>
		<output-example name="outputIP">127.0.0.1</output-example>
		<output-example name="outputSalutation">Miss</output-example>
		<output-example name="outputAge">35</output-example>
		<output-example name="outputList">Test1&amp;Test2</output-example>
		<output-example name="outputProperties">Doe=28&amp;Irene=43</output-example>
	</example>
</function>