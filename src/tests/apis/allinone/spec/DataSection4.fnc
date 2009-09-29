<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="DataSection4"
rcsversion="$Revision: 1.4 $" rcsdate="$Date: 2006/09/25 09:03:25 $">

	<description>An example for the input data section with multiple root elements.</description>

	<input>
		<data>
			<!-- The data section includes packet or envelope elements. -->
			<contains>
				<contained element="person" />
				<contained element="address" />
			</contains>
			<element name="person">
				<description>The person data.</description>
				<attribute name="gender" required="true" type="Salutation">
					<description>The gender of the person.</description>
				</attribute>
				<attribute name="name" required="true" type="portal/Username">
					<description>The name of the person.</description>
				</attribute>
				<attribute name="age" required="false" type="Age">
					<description>The age of the person.</description>
				</attribute>
				<attribute name="size" required="false" type="_int16">
					<description>The size of the person in centimeters.</description>
				</attribute>
				<attribute name="birthdate" required="true" type="_date">
					<description>The birth date of the person.</description>
				</attribute>
				<attribute name="deathdate" required="false" type="_date">
					<description>The death date of the person.</description>
				</attribute>
			</element>
			<element name="address">
				<description>The address.</description>
				<contains>
					<pcdata />
				</contains>
			</element>
		</data>
	</input>

	<example>
		<description>Example for this data section.</description>
		<input-data-example>
			<element-example name="person">
				<attribute-example name="gender">Mister</attribute-example>
				<attribute-example name="name">Doe</attribute-example>
				<attribute-example name="age">55</attribute-example>
				<attribute-example name="size">168</attribute-example>
				<attribute-example name="birthdate">19551205</attribute-example>
			</element-example>
			<element-example name="person">
				<attribute-example name="gender">Miss</attribute-example>
				<attribute-example name="name">Doe</attribute-example>
				<attribute-example name="age">54</attribute-example>
				<attribute-example name="size">158</attribute-example>
				<attribute-example name="birthdate">19561105</attribute-example>
			</element-example>
			<element-example name="address">
				<pcdata-example>22 Washinton square, 54632 London, UK</pcdata-example>
			</element-example>
		</input-data-example>
	</example>

</function>