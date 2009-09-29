<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.4//EN" "http://xins.sourceforge.net/dtd/function_1_4.dtd">

<function name="AttributeCombo"
rcsversion="$Revision: 1.2 $" rcsdate="$Date: 2006/04/05 07:37:15 $">

	<description>A function to test the attribute-combo.</description>
	<input>
		<data>
			<contains>
				<contained element="person" />
			</contains>
			<element name="person">
				<description>A person</description>
				<attribute name="birthDate" required="false" type="_date">
					<description>The birth date.</description>
				</attribute>
				<attribute name="birthYear" required="false" type="_int32">
					<description>The birth date's year.</description>
				</attribute>
				<attribute name="birthMonth" required="false" type="_int32">
					<description>The birth date's month.</description>
				</attribute>
				<attribute name="birthDay" required="false" type="_int32">
					<description>The birth date's day.</description>
				</attribute>
				<attribute name="birthCountry" required="false" type="_text">
					<description>The country where the person is borned.</description>
				</attribute>
				<attribute name="birth-city" required="false" type="_text">
					<description>The city where the person is borned.</description>
				</attribute>
				<attribute name="age" required="false" type="Age">
					<description>An example of input for a int8 type with a minimum and maximum.</description>
				</attribute>
				<!-- One and only one of the three attributes must be filled -->
				<attribute-combo type="exclusive-or">
					<attribute-ref name="birthDate" />
					<attribute-ref name="birthYear" />
					<attribute-ref name="age"       />
				</attribute-combo>
				<!-- At least one of the two attributes must be filled -->
				<attribute-combo type="inclusive-or">
					<attribute-ref name="birthCountry" />
					<attribute-ref name="birth-city"    />
				</attribute-combo>
				<!-- These attributes must be filled together or not filled at all -->
				<attribute-combo type="all-or-none">
					<attribute-ref name="birthYear"  />
					<attribute-ref name="birthMonth" />
					<attribute-ref name="birthDay"   />
				</attribute-combo>
			</element>
		</data>
	</input>

	<output>
		<data>
			<contains>
				<contained element="registration" />
			</contains>
			<element name="registration">
				<description>Registration of a person.</description>
				<attribute name="registration-date" required="false" type="_date">
					<description>The registration date.</description>
				</attribute>
				<attribute name="registrationYear" required="false" type="_int32">
					<description>The registration year.</description>
				</attribute>
				<attribute name="registrationMonth" required="false" type="_int32">
					<description>The registration month.</description>
				</attribute>
				<!-- One of the two attributes must be filled but not both-->
				<attribute-combo type="exclusive-or">
					<attribute-ref name="registration-date" />
					<attribute-ref name="registrationYear" />
				</attribute-combo>
				<!-- These attributes must be filled together or not filled at all -->
				<attribute-combo type="all-or-none">
					<attribute-ref name="registrationYear" />
					<attribute-ref name="registrationMonth" />
				</attribute-combo>
			</element>
		</data>
	</output>

	<example resultcode="_InvalidRequest">
		<description>Invalid attribute.</description>
		<output-data-example>
			<element-example name="attribute-combo">
				<attribute-example name="type">inclusive-or</attribute-example>
				<element-example name="attribute">
					<attribute-example name="name">birthCountry</attribute-example>
				</element-example>
				<element-example name="attribute">
					<attribute-example name="name">birth-city</attribute-example>
				</element-example>
			</element-example>
			<element-example name="attribute-combo">
				<attribute-example name="type">exclusive-or</attribute-example>
				<element-example name="attribute">
					<attribute-example name="name">birthDate</attribute-example>
				</element-example>
				<element-example name="attribute">
					<attribute-example name="name">birthYear</attribute-example>
				</element-example>
				<element-example name="attribute">
					<attribute-example name="name">age</attribute-example>
				</element-example>
			</element-example>
		</output-data-example>
	</example>

	<example resultcode="_InvalidResponse">
		<description>Invalid result.</description>
		<input-example name="birthYear">2006</input-example>
		<input-example name="birthMonth">8</input-example>
		<input-example name="birthDay">19</input-example>
		<input-example name="birthCountry">France</input-example>
		<output-data-example>
			<element-example name="attribute-combo">
				<attribute-example name="type">exclusive-or</attribute-example>
				<element-example name="attribute">
					<attribute-example name="name">registration-date</attribute-example>
				</element-example>
				<element-example name="attribute">
					<attribute-example name="name">registrationYear</attribute-example>
				</element-example>
			</element-example>
		</output-data-example>
	</example>
</function>
