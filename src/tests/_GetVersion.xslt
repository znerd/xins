<?xml version="1.0" encoding="UTF-8" ?>

<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that transform the _GetVersion XML to an HTML page.

 $Id: _GetVersion.xslt,v 1.4 2007/01/04 10:17:40 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" />

    <xsl:template match="/">
        <html>
            <head>
                <title>_GetVersion.xslt</title>
            </head>
            <body>
							XINS version :
							<xsl:value-of select="/result/param[@name='xins.version']" />
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
