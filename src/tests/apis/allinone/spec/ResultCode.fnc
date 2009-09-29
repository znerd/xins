<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.1//EN" "http://xins.sourceforge.net/dtd/function_1_1.dtd">

<function name="ResultCode"
rcsversion="$Revision: 1.3 $" rcsdate="$Date: 2006/01/25 15:57:22 $">

	<description>A function that may return different result codes.</description>

	<input>
		<param name="useDefault" required="true" type="_boolean">
			<description>An example of input for a boolean.</description>
		</param>
		<param name="inputText" required="false" type="_text">
			<description>An example of input for a text.</description>
		</param>
	</input>
	
	<output>
		<resultcode-ref name="AlreadySet" />
		<resultcode-ref name="MissingInput" />
		<param name="outputText" required="true" type="_text">
			<description>An example of output for a text.</description>
		</param>
	</output>

	<example resultcode="_InvalidRequest">
		<description>Missing parameter.</description>
		<data-example>
			<element-example name="missing-param">
				<attribute-example name="param">inputBoolean</attribute-example>
			</element-example>
		</data-example>
	</example>
	
	<example resultcode="AlreadySet">
		<description>The text has already been set.</description>
		<input-example name="useDefault">false</input-example>
		<input-example name="inputText">hello</input-example>
		<output-example name="count">1</output-example>
	</example>
	
	<example resultcode="MissingInput">
		<description>The default is not enabled and no value is passed.</description>
		<input-example name="useDefault">false</input-example>
		<output-data-example>
			<element-example name="inputParameter">
				<attribute-example name="name">inputText</attribute-example>
				<attribute-example name="details">If the default is not enabled, a value should be passed.</attribute-example>
			</element-example>
		</output-data-example>
	</example>
	
	<example>
		<description>A new text was sent.</description>
		<input-example name="useDefault">false</input-example>
		<input-example name="inputText">hello you!</input-example>
		<output-example name="outputText">hello you! added.</output-example>
	</example>

</function>