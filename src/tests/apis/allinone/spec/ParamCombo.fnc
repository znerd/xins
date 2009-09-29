<?xml version="1.0" encoding="US-ASCII"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.2//EN" "http://xins.sourceforge.net/dtd/function_1_2.dtd">

<function name="ParamCombo"
rcsversion="$Revision: 1.6 $" rcsdate="$Date: 2006/09/22 12:24:46 $">

	<description>A function to test the param-combo.</description>
	<input>
		<param name="birthDate" required="false" type="_date">
			<description>The birth date.</description>
		</param>
		<param name="birthYear" required="false" type="_int32">
			<description>The birth date's year.</description>
		</param>
		<param name="birthMonth" required="false" type="_int32">
			<description>The birth date's month.</description>
		</param>
		<param name="birthDay" required="false" type="_int32">
			<description>The birth date's day.</description>
		</param>
		<param name="birthCountry" required="false" type="_text">
			<description>The country where the person is borned.</description>
		</param>
		<param name="birth-city" required="false" type="_text">
			<description>The city where the person is borned.</description>
		</param>
		<param name="age" required="false" type="Age">
			<description>The age of the person.</description>
		</param>
		<!-- One and only one of the three parameters must be filled -->
		<param-combo type="exclusive-or">
			<param-ref name="birthDate" />
			<param-ref name="birthYear" />
			<param-ref name="age"       />
		</param-combo>
		<!-- At least one of the two parameters must be filled -->
		<param-combo type="inclusive-or">
			<param-ref name="birthCountry" />
			<param-ref name="birth-city"    />
		</param-combo>
		<!-- These parameters must be filled together or not filled at all -->
		<param-combo type="all-or-none">
			<param-ref name="birthYear"  />
			<param-ref name="birthMonth" />
			<param-ref name="birthDay"   />
		</param-combo>
	</input>
	<output>
		<param name="registration-date" required="false" type="_date">
			<description>The registration date.</description>
		</param>
		<param name="registrationYear" required="false" type="_int32">
			<description>The registration year.</description>
		</param>
		<param name="registrationMonth" required="false" type="_int32">
			<description>The registration month.</description>
		</param>
		<!-- One of the two parameters must be filled but not both-->
		<param-combo type="exclusive-or">
			<param-ref name="registration-date" />
			<param-ref name="registrationYear" />
		</param-combo>
		<!-- These parameters must be filled together or not filled at all -->
		<param-combo type="all-or-none">
			<param-ref name="registrationYear" />
			<param-ref name="registrationMonth" />
		</param-combo>
	</output>

	<example resultcode="_InvalidRequest">
		<description>Invalid parameter.</description>
		<output-data-example>
			<element-example name="param-combo">
				<attribute-example name="type">inclusive-or</attribute-example>
				<element-example name="param">
					<attribute-example name="name">birthCountry</attribute-example>
				</element-example>
				<element-example name="param">
					<attribute-example name="name">birth-city</attribute-example>
				</element-example>
			</element-example>
			<element-example name="param-combo">
				<attribute-example name="type">exclusive-or</attribute-example>
				<element-example name="param">
					<attribute-example name="name">birthDate</attribute-example>
				</element-example>
				<element-example name="param">
					<attribute-example name="name">birthYear</attribute-example>
				</element-example>
				<element-example name="param">
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
			<element-example name="param-combo">
				<attribute-example name="type">exclusive-or</attribute-example>
				<element-example name="param">
					<attribute-example name="name">registration-date</attribute-example>
				</element-example>
				<element-example name="param">
					<attribute-example name="name">registrationYear</attribute-example>
				</element-example>
			</element-example>
		</output-data-example>
	</example>
	<example>
		<description>Correct combination.</description>
		<input-example name="birthYear">1973</input-example>
		<input-example name="birthMonth">8</input-example>
		<input-example name="birthDay">19</input-example>
		<input-example name="birthCountry">France</input-example>
		<output-example name="registration-date">19740801</output-example>
	</example>
</function>
