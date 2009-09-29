<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.3//EN" "http://xins.sourceforge.net/dtd/function_1_3.dtd">

<function name="Echo"
rcsversion="$Revision: 1.1 $" rcsdate="$Date: 2005/11/21 16:04:23 $">

	<description>Copies the input to the output.</description>

	<input>
		<param name="in" required="false">
			<description>The input.</description>
		</param>
	</input>

	<output>
		<param name="out" required="false">
			<description>An exact copy of the input.</description>
		</param>
	</output>

	<example>
		<description>Output equals input</description>
		<input-example name="in">Hello there.</input-example>
		<output-example name="out">Hello there.</output-example>
	</example>
</function>
