<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.3//EN" "http://www.xins.org/dtd/function_1_3.dtd">

<function name="ParamComboNotAll"
rcsversion="$Revision: 1.1 $" rcsdate="$Date: 2005/06/21 06:42:09 $">

	<description>Test function for a param-combo of type 'not-all'.</description>

	<input>
		<param name="param1" required="false" type="_int32">
			<description>The first parameter.</description>
		</param>
		<param name="param2" required="false" type="_int32">
			<description>The second parameter.</description>
		</param>
		<param name="param3" required="false" type="_int32">
			<description>The third parameter.</description>
		</param>
		<param name="param4" required="false" type="_int32">
			<description>The fourth parameter.</description>
		</param>
		<param-combo type="not-all">
			<param-ref name="param1" />
			<param-ref name="param2" />
			<param-ref name="param3" />
			<param-ref name="param4" />
		</param-combo>
	</input>
</function>
