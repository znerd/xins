<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="SimpleTypes"
rcsversion="$Revision: 1.10 $" rcsdate="$Date: 2006/09/08 11:05:29 $">

	<description>Test all possible types as input and output.</description>

	<input>
		<param name="inputBoolean" required="false" type="_boolean">
			<description>An example of input for a boolean.</description>
		</param>
		<param name="inputByte" required="true" type="_int8">
			<description>An example of input for a byte.</description>
		</param>
		<param name="inputShort" required="false" type="_int16">
			<description>An example of input for a short.</description>
		</param>
		<param name="inputInt" required="true" type="_int32">
			<description>An example of input for an integer.</description>
		</param>
		<param name="inputLong" required="true" type="_int64">
			<description>An example of input for a long.</description>
		</param>
		<param name="inputFloat" required="true" type="_float32">
			<description>An example of input for a float.</description>
		</param>
		<param name="inputDouble" required="false" type="_float64">
			<description>An example of input for a double.</description>
		</param>
		<param name="inputText" required="true" type="_text">
			<description>An example of input for a text.</description>
		</param>
		<param name="inputText2" required="false">
			<description>Another example of input for a text.</description>
		</param>
		<param name="inputProperties" required="false" type="_properties">
			<description>An example of input for properties.</description>
		</param>
		<param name="inputDate" required="false" type="_date">
			<description>An example of input for a date.</description>
		</param>
		<param name="inputTimestamp" required="false" type="_timestamp">
			<description>An example of input for a timestamp.</description>
		</param>
		<param name="inputBinary" required="false" type="_base64">
			<description>An example of input for a byte array.</description>
		</param>
		<param-combo type="all-or-none">
			<param-ref name="inputDate"      />
			<param-ref name="inputTimestamp" />
		</param-combo>
	</input>

	<output>
		<param name="outputBoolean" required="false" type="_boolean">
			<description>An example of output for a boolean.</description>
		</param>
		<param name="outputByte" required="false" type="_int8">
			<description>An example of output for a byte.</description>
		</param>
		<param name="outputShort" required="true" type="_int16">
			<description>An example of output for a short.</description>
		</param>
		<param name="outputInt" required="true" type="_int32">
			<description>An example of output for an integer.</description>
		</param>
		<param name="outputLong" required="true" type="_int64">
			<description>An example of output for a long.</description>
		</param>
		<param name="outputFloat" required="false" type="_float32">
			<description>An example of output for a float.</description>
		</param>
		<param name="outputDouble" required="true" type="_float64">
			<description>An example of output for a double.</description>
		</param>
		<param name="outputText" required="true" type="_text">
			<description>An example of output for a text.</description>
		</param>
		<param name="outputText2" required="false">
			<description>Another example of output for a text.</description>
		</param>
		<param name="outputProperties" required="false" type="_properties">
			<description>An example of output for properties.</description>
		</param>
		<param name="outputDate" required="false" type="_date">
			<description>An example of output for a date.</description>
		</param>
		<param name="outputTimestamp" required="false" type="_timestamp">
			<description>An example of output for a timestamp.</description>
		</param>
		<param name="outputBinary" required="false" type="_base64">
			<description>An example of output for a byte array.</description>
		</param>
	</output>

	<example resultcode="_InvalidRequest">
		<description>Missing parameter.</description>
		<input-example name="inputByte">8</input-example>
		<input-example name="inputShort">100</input-example>
		<input-example name="inputLong">10000</input-example>
		<input-example name="inputText">Hello</input-example>
		<data-example>
			<element-example name="missing-param">
				<attribute-example name="param">inputInt</attribute-example>
			</element-example>
			<element-example name="missing-param">
				<attribute-example name="param">inputFloat</attribute-example>
			</element-example>
		</data-example>
	</example>
	<example resultcode="_InvalidRequest">
		<description>Missing and invalid parameter.</description>
		<input-example name="inputByte">8</input-example>
		<input-example name="inputShort">two</input-example>
		<input-example name="inputLong">10000</input-example>
		<input-example name="inputFloat">35.2</input-example>
		<input-example name="inputText">Hello</input-example>
		<input-example name="inputBinary">aGVsbG8=</input-example>
		<data-example>
			<element-example name="missing-param">
				<attribute-example name="param">inputInt</attribute-example>
			</element-example>
			<element-example name="invalid-value-for-type">
				<attribute-example name="type">_int16</attribute-example>
				<attribute-example name="param">inputShort</attribute-example>
			</element-example>
		</data-example>
	</example>
	<example>
		<description>Successful example.</description>
		<input-example name="inputByte">8</input-example>
		<input-example name="inputInt">20</input-example>
		<input-example name="inputLong">10000</input-example>
		<input-example name="inputFloat">2.8</input-example>
		<input-example name="inputText">Hello</input-example>
		<output-example name="outputInt">16</output-example>
		<output-example name="outputShort">-1</output-example>
		<output-example name="outputLong">14</output-example>
		<output-example name="outputFloat">3.5</output-example>
		<output-example name="outputDouble">3.1415</output-example>
		<output-example name="outputDate">20040621</output-example>
		<output-example name="outputText">hello</output-example>
	</example>

</function>
