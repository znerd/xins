<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.5//EN" "http://www.xins.org/dtd/function_1_5.dtd">

<function name="LoginOkay"
rcsversion="$Revision: 1.4 $" rcsdate="$Date: 2006/12/19 15:22:37 $">

	<description>Log a user in.</description>

	<input>
		<param name="userName" required="true" type="Username">
			<description>the name of the user.</description>
		</param>
		<param name="password" required="true" type="Password">
			<description>the password of the user.</description>
		</param>
		<param name="salutation" required="false" type="allinone/Salutation">
			<description>the gender of the user.</description>
		</param>
	</input>
	<output>
	</output>
</function>
