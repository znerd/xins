<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE function PUBLIC "-//XINS//DTD Function 1.1//EN" "http://www.xins.org/dtd/function_1_1.dtd">

<function name="RuntimeProps"
rcsversion="$Revision: 1.1 $" rcsdate="$Date: 2005/03/02 11:37:57 $">

	<description>Gets and returns properties defined in the runtime properties file.</description>

	<input>
		<param name="price" required="true" type="_float32">
			<description>The price of the product</description>
		</param>
	</input>
	<output>
		<param name="taxes" required="true" type="_float32">
			<description>The taxes to pay for the product.</description>
		</param>
		<param name="currency" required="false">
			<description>The currency to pay the tax in.</description>
		</param>
	</output>
</function>
