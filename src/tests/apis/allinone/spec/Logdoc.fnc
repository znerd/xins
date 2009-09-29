<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.1//EN" "http://xins.sourceforge.net/dtd/function_1_1.dtd">

<function name="Logdoc"
rcsversion="$Revision: 1.2 $" rcsdate="$Date: 2005/01/05 13:29:45 $">

	<description>A function that uses logdoc as logging system.</description>

	<input>
		<param name="inputText" required="true" type="_text">
			<description>An example of input for a text.</description>
		</param>
	</input>
	<output>
		<resultcode-ref name="InvalidNumber" />
	</output>

	<example resultcode="InvalidNumber">
		<description>The entered input is not a number.</description>
		<input-example name="inputText">foo</input-example>
	</example>
	<example>
		<description>The entered text is a number</description>
		<input-example name="inputText">12000</input-example>
	</example>
</function>