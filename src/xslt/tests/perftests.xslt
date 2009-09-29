<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT stylesheet that converts a JUnit testresult to HTML.

 $Id: perftests.xslt,v 1.6 2007/01/04 10:17:40 agoubard Exp $

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output
	method="xml"
	indent="no"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

	<xsl:template match="testsuite">
		<html>
			<head>
				<title>Performance test results</title>
				<link rel="stylesheet" type="text/css" href="stylesheet.css" />
			</head>
			<body>
				<h1>Performance test results</h1>
				<p class="summaryLine">
					<xsl:variable name="testcaseCount" select="count(testcase)" />
					<xsl:variable name="failedTestcaseCount" select="count(testcase/failure)" />
					<xsl:variable name="erroneousTestcaseCount" select="count(testcase/error)" />

					<xsl:text>Performed </xsl:text>
					<xsl:if test="$testcaseCount &gt; 1">
						<xsl:text>all </xsl:text>
					</xsl:if>
					<xsl:value-of select="$testcaseCount" />
					<xsl:text> test</xsl:text>
					<xsl:if test="$testcaseCount &gt; 1">
						<xsl:text>s</xsl:text>
					</xsl:if>
					<xsl:text> in </xsl:text>
					<xsl:value-of select="/testsuite/@time" />
					<xsl:text> second</xsl:text>
					<xsl:if test="not(/testsuite/@time = 1)">
						<xsl:text>s</xsl:text>
					</xsl:if>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="$failedTestcaseCount" />
					<xsl:text> failed and </xsl:text>
					<xsl:value-of select="$erroneousTestcaseCount" />
					<xsl:text> had errors.</xsl:text>
				</p>

				<xsl:if test="count(testcase[failure or error]) &gt; 0">
					<h2>Errors and failures</h2>
					<xsl:apply-templates select="testcase[failure or error]" />
				</xsl:if>

				<h2>Details</h2>
				<table class="summary">
					<tr>
						<th>#</th>
						<th>Name</th>
						<th>Time</th>
						<th>Result</th>
					</tr>
					<xsl:apply-templates select="testcase" mode="summary" />
				</table>

				<h2>Standard output</h2>
				<pre>
					<xsl:apply-templates select="system-out" />
				</pre>

				<h2>Error output</h2>
				<pre>
					<xsl:apply-templates select="system-err" />
				</pre>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="testcase" mode="summary">
		<xsl:variable name="testnumber" select="position()" />
		<xsl:variable name="name_orig"  select="substring-after(@name, 'test')" />
		<xsl:variable name="name_start" select="translate(substring($name_orig, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
		<xsl:variable name="name_end"   select="substring($name_orig, 2, string-length($name_orig) - 1)" />

		<xsl:variable name="name" select="concat($name_start, $name_end)" />

		<tr>
			<td class="testnumber">
				<xsl:value-of select="$testnumber" />
			</td>
			<td class="testname">
				<xsl:choose>
					<xsl:when test="count(error) &gt; 0 or count(failure) &gt; 0">
						<a>
							<xsl:attribute name="href">
								<xsl:text>#test-</xsl:text>
								<xsl:value-of select="$name" />
							</xsl:attribute>
							<xsl:value-of select="$name" />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$name" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td>
				<xsl:value-of select="@time" />
			</td>
			<xsl:choose>
				<xsl:when test="failure">
					<td class="failure">
						<acronym>
							<xsl:attribute name="title">
								<xsl:value-of select="failure/@type" />
							</xsl:attribute>
							<xsl:value-of select="substring-after(substring-after(failure/@type, '.'), '.')" />
						</acronym>
					</td>
				</xsl:when>
				<xsl:when test="error">
					<td class="testresult_error">Error</td>
				</xsl:when>
				<xsl:otherwise>
					<td class="okay">OK</td>
				</xsl:otherwise>
			</xsl:choose>
		</tr>
	</xsl:template>

	<xsl:template match="testcase[not(failure) and not(error)]">
		<xsl:variable name="name_orig"  select="substring-after(@name, 'test')" />
		<xsl:variable name="name_start" select="translate(substring($name_orig, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
		<xsl:variable name="name_end"   select="substring($name_orig, 2, string-length($name_orig) - 1)" />

		<xsl:variable name="name" select="concat($name_start, $name_end)" />

		<a>
			<xsl:attribute name="name">
				<xsl:text>test-</xsl:text>
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</a>
		<h3>
			<xsl:text>Test </xsl:text>
			<xsl:value-of select="$name" />
		</h3>
		<table class="testcase_details">
			<tr>
				<th>Name:</th>
				<td>
					<xsl:value-of select="$name" />
				</td>
			</tr>
			<tr>
				<th>Time:</th>
				<td>
					<xsl:value-of select="@time" />
					<xsl:text> second</xsl:text>
					<xsl:if test="not(@time = 1)">
						<xsl:text>s</xsl:text>
					</xsl:if>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="testcase[failure]">
		<xsl:variable name="name_orig"  select="substring-after(@name, 'test')" />
		<xsl:variable name="name_start" select="translate(substring($name_orig, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
		<xsl:variable name="name_end"   select="substring($name_orig, 2, string-length($name_orig) - 1)" />

		<xsl:variable name="name"    select="concat($name_start, $name_end)" />

		<a>
			<xsl:attribute name="name">
				<xsl:text>test-</xsl:text>
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</a>
		<h3>
			<xsl:text>Test </xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text> (</xsl:text>
			<span class="failure">Failure</span>
			<xsl:text>)</xsl:text>
		</h3>
		<table class="testcase_details">
			<tr>
				<th>Name:</th>
				<td>
					<xsl:value-of select="$name" />
				</td>
			</tr>
			<tr>
				<th>Time:</th>
				<td>
					<xsl:value-of select="@time" />
					<xsl:text> second</xsl:text>
					<xsl:if test="not(@time = 1)">
						<xsl:text>s</xsl:text>
					</xsl:if>
				</td>
			</tr>
			<tr>
				<th>Failure type:</th>
				<td>
					<xsl:value-of select="failure/@type" />
				</td>
			</tr>
			<tr>
				<th>Message:</th>
				<td class="testdetails_message">
					<xsl:value-of select="normalize-space(failure/@message)" />
				</td>
			</tr>
			<tr>
				<th>Details:</th>
				<td class="testdetails_details">
					<pre><xsl:value-of select="failure/text()" /></pre>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="testcase[error]">
		<xsl:variable name="name_orig"  select="substring-after(@name, 'test')" />
		<xsl:variable name="name_start" select="translate(substring($name_orig, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" />
		<xsl:variable name="name_end"   select="substring($name_orig, 2, string-length($name_orig) - 1)" />

		<xsl:variable name="name"    select="concat($name_start, $name_end)" />

		<a>
			<xsl:attribute name="name">
				<xsl:text>test-</xsl:text>
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</a>
		<h3>
			<xsl:text>Test </xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text> (</xsl:text>
			<span class="error">Error</span>
			<xsl:text>)</xsl:text>
		</h3>
		<table class="testcase_details">
			<tr>
				<th>Name:</th>
				<td>
					<xsl:value-of select="$name" />
				</td>
			</tr>
			<tr>
				<th>Time:</th>
				<td>
					<xsl:value-of select="@time" />
					<xsl:text> second</xsl:text>
					<xsl:if test="not(@time = 1)">
						<xsl:text>s</xsl:text>
					</xsl:if>
				</td>
			</tr>
			<tr>
				<th>Error type:</th>
				<td>
					<xsl:value-of select="error/@type" />
				</td>
			</tr>
			<tr>
				<th>Message:</th>
				<td class="testdetails_message">
					<xsl:value-of select="normalize-space(error/@message)" />
				</td>
			</tr>
			<tr>
				<th>Details:</th>
				<td class="testdetails_details">
					<pre><xsl:value-of select="error/text()" /></pre>
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
