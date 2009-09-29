<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://xins.sourceforge.net/dtd/function_1_5.dtd">

<function name="ParamComboValue"
rcsversion="$Revision: 1.3 $" rcsdate="$Date: 2006/10/19 12:54:01 $">

	<description>A function to test the param-combo based on a parameter value.</description>
	<input>
		<param name="salutation" required="true" type="Salutation">
			<description>The gender of the person.</description>
		</param>
		<param name="maidenName" required="false" type="_text">
			<description>The maiden name.</description>
		</param>
		<param name="surname" required="false" type="_text">
			<description>The name of the person.</description>
		</param>
		<param name="age" required="false" type="_int32">
			<description>The age of the person.</description>
		</param>
		<param name="country" required="false" type="_text">
			<description>The country from which this person comes from.</description>
		</param>
		<param name="nationality" required="false" type="_text">
			<description>The nationality of the person.</description>
		</param>
		<param name="passportNumber" required="false" type="_text">
			<description>The passport number of the person.</description>
		</param>
		<param name="passportValidityDate" required="false" type="_int32">
			<description>The expiration year of the passport.</description>
		</param>

		<!-- If the salutation is Madam, the maiden name is required -->
		<param-combo type="inclusive-or">
			<param-ref name="salutation" value="Madam" />
			<param-ref name="maidenName" />
		</param-combo>

		<!-- If the country is Canada, the nationality should not be filled, otherwise the nationality should be filled -->
		<param-combo type="exclusive-or">
			<param-ref name="country" value="Canada" />
			<param-ref name="nationality" />
		</param-combo>

		<!-- Passport number and validity date are required if country is Other. If country is not Other, the passport number and the validity date should not be set. -->
		<param-combo type="all-or-none">
			<param-ref name="country" value="Other" />
			<param-ref name="passportNumber" />
			<param-ref name="passportValidityDate" />
		</param-combo>
		
		<!-- If the salutation is Miss, the age should not be set -->
		<param-combo type="not-all">
			<param-ref name="salutation" value="Miss" />
			<param-ref name="age" />
		</param-combo>
	</input>
	<output>
	</output>
</function>
