<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.1//EN" "http://xins.sourceforge.net/dtd/function_1_1.dtd">

<function name="DataSection3"
rcsversion="$Revision: 1.1 $" rcsdate="$Date: 2005/01/05 13:29:45 $">

	<description>An example of a data section with multiple root elements.</description>

	<input>
		<param name="inputText" required="false" type="_text">
			<description>An example of input for a text.</description>
		</param>
		<data>
			<!-- The data section for the input. -->
			<contains>
				<contained element="address" />
			</contains>
			<element name="address">
				<description>The addresses to post it.</description>
				<attribute name="company" required="true" type="_text">
					<description>The name of the company.</description>
				</attribute>
				<attribute name="postcode" required="true" type="_text">
					<description>The postcode of the address.</description>
				</attribute>
			</element>
		</data>
	</input>

	<output>
		<data>
			<!-- The data section includes packet or envelope elements. -->
			<contains>
				<contained element="packet" />
				<contained element="envelope" />
			</contains>
			<element name="packet">
				<description>The packet.</description>
				<attribute name="destination" required="true" type="_text">
					<description>The destination of the packet.</description>
				</attribute>
			</element>
			<element name="envelope">
				<description>The envelope.</description>
				<attribute name="destination" required="true" type="_text">
					<description>The destination of the envelope.</description>
				</attribute>
			</element>
		</data>
	</output>

	<example>
		<description>Example for this data section.</description>
		<input-data-example>
			<element-example name="address">
				<attribute-example name="company">McDo</attribute-example>
				<attribute-example name="postcode">1234</attribute-example>
			</element-example>
			<element-example name="address">
				<attribute-example name="company">Drill</attribute-example>
				<attribute-example name="postcode">5467</attribute-example>
			</element-example>
		</input-data-example>
		<output-data-example>
			<element-example name="packet">
				<attribute-example name="destination">20 West Street, New York</attribute-example>
			</element-example>
			<element-example name="envelope">
				<attribute-example name="destination">55 Kennedy lane, Washinton DC</attribute-example>
			</element-example>
		</output-data-example>
	</example>

</function>