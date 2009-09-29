<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an Eclipse .tomcatplugin file for an API.

 $Id: api_to_tomcatserver.xslt,v 1.2 2007/01/04 10:17:39 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output 
		omit-xml-declaration="no" 
		encoding="UTF-8"
		method="xml" 
		indent="yes" />
	
	<!-- Define parameters -->
	<xsl:param name="project_home" />

	<xsl:template match="api">

		<Server port="8005" shutdown="SHUTDOWN">

			<GlobalNamingResources>
				<!-- Used by Manager webapp -->
				<Resource name="UserDatabase" auth="Container"
									type="org.apache.catalina.UserDatabase"
					 description="User database that can be updated and saved"
							 factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
							pathname="conf/tomcat-users.xml" />
			</GlobalNamingResources>

			<Service name="Catalina">
				<Connector port="8080" />

				<!-- This is here for compatibility only, not required -->
				<Connector port="8009" protocol="AJP/1.3" />

				<Engine name="Catalina" defaultHost="localhost">
					<Realm className="org.apache.catalina.realm.UserDatabaseRealm"
								 resourceName="UserDatabase" />
					<Host name="localhost" appBase="{$project_home}/build/webapps/{@name}" />
				</Engine>

			</Service>
		</Server>
	</xsl:template>

</xsl:stylesheet>
