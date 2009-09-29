<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an JMeter jmx file to run the examples of the API using JMeter.

 $Id: api_to_jmx.xslt,v 1.2 2007/01/04 10:17:40 agoubard Exp $

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

		<xsl:variable name="apiName" select="@name" />
		<xsl:variable name="specsdir" select="concat($project_home, '/apis/', $apiName, '/spec')" />

		<jmeterTestPlan version="1.1" properties="1.7">
			<hashTree>
				<TestPlan>
					<collectionProp name="TestPlan.thread_groups"/>
					<stringProp name="TestPlan.user_define_classpath"></stringProp>
					<stringProp name="TestPlan.comments"></stringProp>
					<boolProp name="TestPlan.functional_mode">false</boolProp>
					<boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
					<boolProp name="TestElement.enabled">true</boolProp>
					<stringProp name="TestElement.name">
						<xsl:value-of select="$apiName" />
						<xsl:text> API Test Plan</xsl:text>
					</stringProp>
					<elementProp name="TestPlan.user_defined_variables" elementType="org.apache.jmeter.config.Arguments">
						<stringProp name="TestElement.test_class">org.apache.jmeter.config.Arguments</stringProp>
						<boolProp name="TestElement.enabled">true</boolProp>
						<stringProp name="TestElement.gui_class">org.apache.jmeter.config.gui.ArgumentsPanel</stringProp>
						<stringProp name="TestElement.name">User Defined Variables</stringProp>
						<collectionProp name="Arguments.arguments"/>
					</elementProp>
					<stringProp name="TestElement.gui_class">org.apache.jmeter.control.gui.TestPlanGui</stringProp>
					<stringProp name="TestElement.test_class">org.apache.jmeter.testelement.TestPlan</stringProp>
				</TestPlan>
				<hashTree>
					<ThreadGroup>
						<stringProp name="ThreadGroup.ramp_time">0</stringProp>
						<boolProp name="ThreadGroup.scheduler">false</boolProp>
						<stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
						<longProp name="ThreadGroup.start_time">1157705759000</longProp>
						<boolProp name="TestElement.enabled">true</boolProp>
						<elementProp name="ThreadGroup.main_controller" elementType="org.apache.jmeter.control.LoopController">
							<boolProp name="LoopController.continue_forever">false</boolProp>
							<stringProp name="TestElement.test_class">org.apache.jmeter.control.LoopController</stringProp>
							<stringProp name="LoopController.loops">1</stringProp>
							<boolProp name="TestElement.enabled">true</boolProp>
							<stringProp name="TestElement.gui_class">org.apache.jmeter.control.gui.LoopControlPanel</stringProp>
							<stringProp name="TestElement.name">Loop Controller</stringProp>
						</elementProp>
						<stringProp name="ThreadGroup.num_threads">1</stringProp>
						<stringProp name="TestElement.name">Defined examples</stringProp>
						<stringProp name="ThreadGroup.duration"></stringProp>
						<stringProp name="ThreadGroup.delay"></stringProp>
						<longProp name="ThreadGroup.end_time">1157705759000</longProp>
						<stringProp name="TestElement.gui_class">org.apache.jmeter.threads.gui.ThreadGroupGui</stringProp>
						<stringProp name="TestElement.test_class">org.apache.jmeter.threads.ThreadGroup</stringProp>
					</ThreadGroup>
					<hashTree>
						<ConfigTestElement>
							<stringProp name="HTTPSampler.follow_redirects">false</stringProp>
							<stringProp name="HTTPSampler.domain">localhost</stringProp>
							<stringProp name="HTTPSampler.protocol">http</stringProp>
							<elementProp name="HTTPsampler.Arguments" elementType="org.apache.jmeter.config.Arguments">
								<stringProp name="TestElement.test_class">org.apache.jmeter.config.Arguments</stringProp>
								<stringProp name="TestElement.gui_class">org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel</stringProp>
								<boolProp name="TestElement.enabled">true</boolProp>
								<stringProp name="TestElement.name"></stringProp>
								<collectionProp name="Arguments.arguments"/>
							</elementProp>
							<boolProp name="TestElement.enabled">true</boolProp>
							<stringProp name="HTTPSampler.path">
								<xsl:value-of select="concat('/', $apiName)" />
							</stringProp>
							<stringProp name="TestElement.name">Defaults</stringProp>
							<stringProp name="HTTPSampler.port">8080</stringProp>
							<stringProp name="HTTPSampler.method">POST</stringProp>
							<stringProp name="TestElement.gui_class">org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui</stringProp>
							<stringProp name="TestElement.test_class">org.apache.jmeter.config.ConfigTestElement</stringProp>
							<stringProp name="HTTPSampler.use_keepalive">false</stringProp>
						</ConfigTestElement>
						<hashTree/>
						<!-- Creates the HTTP Sampler for each examples -->
						<xsl:for-each select="function">
							<xsl:variable name="functionName" select="@name" />
							<xsl:variable name="functionFile" select="concat($specsdir, '/', $functionName, '.fnc')" />

							<xsl:for-each select="document($functionFile)/function/example">
								<HTTPSampler>
									<stringProp name="HTTPSampler.mimetype"></stringProp>
									<boolProp name="HTTPSampler.follow_redirects">false</boolProp>
									<stringProp name="HTTPSampler.domain"></stringProp>
									<stringProp name="HTTPSampler.protocol">http</stringProp>
									<boolProp name="HTTPSampler.auto_redirects">false</boolProp>
									<stringProp name="HTTPSampler.monitor">false</stringProp>
									<stringProp name="HTTPSampler.FILE_FIELD"></stringProp>
									<boolProp name="TestElement.enabled">true</boolProp>
									<elementProp name="HTTPsampler.Arguments" elementType="org.apache.jmeter.config.Arguments">
										<stringProp name="TestElement.test_class">org.apache.jmeter.config.Arguments</stringProp>
										<stringProp name="TestElement.gui_class">org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel</stringProp>
										<boolProp name="TestElement.enabled">true</boolProp>
										<stringProp name="TestElement.name"></stringProp>
										<collectionProp name="Arguments.arguments">
											<elementProp name="" elementType="org.apache.jmeter.protocol.http.util.HTTPArgument">
												<boolProp name="HTTPArgument.always_encode">false</boolProp>
												<boolProp name="HTTPArgument.use_equals">true</boolProp>
												<stringProp name="Argument.value">_xins-std</stringProp>
												<stringProp name="Argument.name">_convention</stringProp>
												<stringProp name="Argument.metadata">=</stringProp>
											</elementProp>
											<elementProp name="" elementType="org.apache.jmeter.protocol.http.util.HTTPArgument">
												<boolProp name="HTTPArgument.always_encode">false</boolProp>
												<boolProp name="HTTPArgument.use_equals">true</boolProp>
												<stringProp name="Argument.value">
													<xsl:value-of select="$functionName" />
												</stringProp>
												<stringProp name="Argument.name">_function</stringProp>
												<stringProp name="Argument.metadata">=</stringProp>
											</elementProp>
											<xsl:for-each select="input-example">
												<elementProp name="" elementType="org.apache.jmeter.protocol.http.util.HTTPArgument">
													<boolProp name="HTTPArgument.always_encode">true</boolProp>
													<boolProp name="HTTPArgument.use_equals">true</boolProp>
													<stringProp name="Argument.value">
														<xsl:value-of select="text()" />
													</stringProp>
													<stringProp name="Argument.name">
														<xsl:value-of select="@name" />
													</stringProp>
													<stringProp name="Argument.metadata">=</stringProp>
												</elementProp>
											</xsl:for-each>
										</collectionProp>
									</elementProp>
									<stringProp name="HTTPSampler.FILE_NAME"></stringProp>
									<stringProp name="HTTPSampler.path">
										<xsl:value-of select="concat('/', $apiName)" />
									</stringProp>
									<stringProp name="TestElement.name">
										<xsl:value-of select="$functionName" />
										<xsl:text> function - example </xsl:text>
										<xsl:value-of select="position()" />
									</stringProp>
									<stringProp name="HTTPSampler.port"></stringProp>
									<stringProp name="HTTPSampler.method">POST</stringProp>
									<stringProp name="TestElement.gui_class">org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui</stringProp>
									<stringProp name="TestElement.test_class">org.apache.jmeter.protocol.http.sampler.HTTPSampler</stringProp>
									<boolProp name="HTTPSampler.use_keepalive">false</boolProp>
								</HTTPSampler>
								<hashTree>
									<org.apache.jmeter.assertions.XPathAssertion>
										<stringProp name="XPath.xpath">/result</stringProp>
										<boolProp name="TestElement.enabled">true</boolProp>
										<boolProp name="XPath.negate">false</boolProp>
										<stringProp name="TestElement.name">XPath Assertion</stringProp>
										<boolProp name="XPath.validate">false</boolProp>
										<boolProp name="XPath.tolerant">false</boolProp>
										<boolProp name="XPath.namespace">false</boolProp>
										<boolProp name="XPath.whitespace">true</boolProp>
										<stringProp name="TestElement.gui_class">org.apache.jmeter.assertions.gui.XPathAssertionGui</stringProp>
										<stringProp name="TestElement.test_class">org.apache.jmeter.assertions.XPathAssertion</stringProp>
									</org.apache.jmeter.assertions.XPathAssertion>
									<hashTree/>
								</hashTree>
							</xsl:for-each>
						</xsl:for-each>
						<ResultCollector>
							<stringProp name="filename"></stringProp>
							<stringProp name="TestElement.test_class">org.apache.jmeter.reporters.ResultCollector</stringProp>
							<stringProp name="TestElement.gui_class">org.apache.jmeter.visualizers.GraphVisualizer</stringProp>
							<boolProp name="TestElement.enabled">true</boolProp>
							<objProp>
								<value class="org.apache.jmeter.samplers.SampleSaveConfiguration">
									<time>true</time>
									<latency>true</latency>
									<timestamp>true</timestamp>
									<success>true</success>
									<label>true</label>
									<code>true</code>
									<message>true</message>
									<threadName>true</threadName>
									<dataType>true</dataType>
									<encoding>false</encoding>
									<assertions>true</assertions>
									<subresults>true</subresults>
									<responseData>false</responseData>
									<samplerData>false</samplerData>
									<xml>true</xml>
									<fieldNames>false</fieldNames>
									<responseHeaders>false</responseHeaders>
									<requestHeaders>false</requestHeaders>
									<responseDataOnError>false</responseDataOnError>
									<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>
									<assertionsResultsToSave>0</assertionsResultsToSave>
									<delimiter>,</delimiter>
									<printMilliseconds>true</printMilliseconds>
								</value>
								<name>saveConfig</name>
							</objProp>
							<boolProp name="ResultCollector.error_logging">false</boolProp>
							<stringProp name="TestElement.name">Graph result</stringProp>
						</ResultCollector>
						<hashTree/>
						<ResultCollector>
							<stringProp name="filename"></stringProp>
							<stringProp name="TestElement.test_class">org.apache.jmeter.reporters.ResultCollector</stringProp>
							<stringProp name="TestElement.gui_class">org.apache.jmeter.visualizers.TableVisualizer</stringProp>
							<boolProp name="TestElement.enabled">true</boolProp>
							<objProp>
								<value class="org.apache.jmeter.samplers.SampleSaveConfiguration">
									<time>true</time>
									<latency>true</latency>
									<timestamp>true</timestamp>
									<success>true</success>
									<label>true</label>
									<code>true</code>
									<message>true</message>
									<threadName>true</threadName>
									<dataType>true</dataType>
									<encoding>false</encoding>
									<assertions>true</assertions>
									<subresults>true</subresults>
									<responseData>false</responseData>
									<samplerData>false</samplerData>
									<xml>true</xml>
									<fieldNames>false</fieldNames>
									<responseHeaders>false</responseHeaders>
									<requestHeaders>false</requestHeaders>
									<responseDataOnError>false</responseDataOnError>
									<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>
									<assertionsResultsToSave>0</assertionsResultsToSave>
									<delimiter>,</delimiter>
									<printMilliseconds>true</printMilliseconds>
								</value>
								<name>saveConfig</name>
							</objProp>
							<boolProp name="ResultCollector.error_logging">false</boolProp>
							<stringProp name="TestElement.name">View Results in Table</stringProp>
						</ResultCollector>
						<hashTree/>
					</hashTree>
				</hashTree>
			</hashTree>
		</jmeterTestPlan>
	</xsl:template>

</xsl:stylesheet>
