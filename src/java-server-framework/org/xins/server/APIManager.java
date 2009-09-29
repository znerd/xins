/*
 * $Id: APIManager.java,v 1.29 2008/02/27 23:02:19 agoubard Exp $
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.apache.log4j.Logger;
import org.apache.log4j.jmx.HierarchyDynamicMBean;

import org.xins.common.Utils;
import org.xins.common.collections.PropertyReaderConverter;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.text.DateConverter;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;

/**
 * Management bean for the API.
 *
 * @version $Revision: 1.29 $ $Date: 2008/02/27 23:02:19 $
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.5.0
 */
public final class APIManager implements APIManagerMBean {

   /**
    * Formatter to convert {@link String} to {@link java.util.Date}.
    */
   private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.DD HH:MM:ss.SSS");

   /**
    * The API, never <code>null</code>.
    */
   private final API _api;

   /**
    * The IP address runing this class, never <code>null</code>.
    */
   private String _ip;

   /**
    * Ctreates a new API manager MBean.
    *
    * @param api
    *    the APi that is managed by this MBean.
    */
   APIManager(API api) {
      _api = api;
      try {
         _ip = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException uhex) {
         Log.log_3250(uhex);
         _ip = "127.0.0.1";
      }
   }

   /**
    * Gets the version of the API.
    *
    * @return
    *    the version of the API running.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getAPIVersion() throws IOException {
      return _api.getBootstrapProperties().get(API.API_VERSION_PROPERTY);
   }

   /**
    * Gets the version of XINS which is running this API.
    *
    * @return
    *    the version of XINS running the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getXINSVersion() throws IOException {
      return Library.getVersion();
   }

   /**
    * Gets the name of the API.
    *
    * @return
    *    the name the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getAPIName() throws IOException {
      return _api.getName();
   }

   /**
    * Gets the bootstrap properties.
    *
    * @return
    *    the bootstrap properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public CompositeDataSupport getBootstrapProperties() throws IOException {
       Properties bootstrapProps = PropertyReaderConverter.toProperties(_api.getBootstrapProperties());
       return propertiesToCompositeData(bootstrapProps);
   }

   /**
    * Gets the runtime properties.
    *
    * @return
    *    the runtime properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public CompositeDataSupport getRuntimeProperties() throws IOException {
      Properties runtimeProps = PropertyReaderConverter.toProperties(_api.getRuntimeProperties());
      return propertiesToCompositeData(runtimeProps);
   }

   /**
    * Gets the time at which the API was started.
    *
    * @return
    *    the time at which the API was started in the form YYYYMMDDThhmmssSSS+TZ.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getStartupTime() throws IOException {
      return DateConverter.toDateString(_api.getStartupTimestamp());
   }


   /**
    * Gets the list of the API functions.
    *
    * @return
    *    the list of the API function names.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String[] getFunctionNames() throws IOException {
      List functions =  _api.getFunctionList();
      String[] functionNames = new String[functions.size()];
      for (int i = 0; i < functions.size(); i++) {
         Function nextFunction = (Function) functions.get(i);
         functionNames[i] = nextFunction.getName();
      }
      return functionNames;
   }

   /**
    * Gets the statistics of the functions.
    *
    * @return
    *    the statistics of the functions.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public TabularDataSupport getStatistics() throws IOException {
      String[] statsNames = {"Function", "Count", "Error Code", "Average", "Min Date",
            "Min Duration", "Max Date", "Max Duration", "Last Date", "Last Duration"};
      OpenType[] statsTypes = {SimpleType.STRING, SimpleType.LONG, SimpleType.STRING,
            SimpleType.LONG, SimpleType.DATE, SimpleType.LONG, SimpleType.DATE, SimpleType.LONG,
            SimpleType.DATE, SimpleType.LONG};
      try {
         CompositeType statType = new CompositeType("Statistic",
               "A statistic of a function", statsNames, statsNames, statsTypes);
         TabularType tabType = new TabularType("Function statistics",
               "Statistics of the functions", statType, statsNames);
         TabularDataSupport tabularData = new TabularDataSupport(tabType);
         Iterator itFunctions =  _api.getFunctionList().iterator();
         while (itFunctions.hasNext()) {
            Function nextFunction = (Function) itFunctions.next();
            Element success = nextFunction.getStatistics().getSuccessfulElement();
            HashMap statMap = statisticsToMap(success, nextFunction.getName());
            CompositeDataSupport statData = new CompositeDataSupport(statType, statMap);
            tabularData.put(statData);
            Element[] unsuccess = nextFunction.getStatistics().getUnsuccessfulElement(true);
            for (int i = 0; i < unsuccess.length; i++) {
               HashMap statMap2 = statisticsToMap(unsuccess[i], nextFunction.getName());
               CompositeDataSupport statData2 = new CompositeDataSupport(statType, statMap2);
               tabularData.put(statData2);
            }
         }

         return tabularData;
      } catch (OpenDataException odex) {
         Utils.logProgrammingError(odex);
         return null;
      }
   }

   /**
    * Executes the _NoOp meta function.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    *
    * @throws NoSuchFunctionException
    *    if the _noOp meta function is not found.
    *
    * @throws AccessDeniedException
    *    if the JMX client is not in the ACLs to execute the _noOp meta function.
    */
   public void noOp() throws IOException, NoSuchFunctionException, AccessDeniedException {
      FunctionRequest noOpRequest = new FunctionRequest("_NoOp",
            PropertyReaderUtils.EMPTY_PROPERTY_READER, null);
      _api.handleCall(System.currentTimeMillis(), noOpRequest, _ip, null);
   }

   /**
    * Reloads the runtime properties if the file has changed.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    *
    * @throws NoSuchFunctionException
    *    if the _ReloadProperties meta function is not found.
    *
    * @throws AccessDeniedException
    *    if the JMX client is not in the ACLs to execute the _ReloadProperties meta function.
    */
   public void reloadProperties() throws IOException, NoSuchFunctionException, AccessDeniedException {
      FunctionRequest reloadPropertiesRequest = new FunctionRequest("_ReloadProperties",
            PropertyReaderUtils.EMPTY_PROPERTY_READER, null);
      _api.handleCall(System.currentTimeMillis(), reloadPropertiesRequest, _ip, null);
   }

   /**
    * Put the data of a function statistic in a {@link HashMap}.
    *
    * @param statElement
    *    the XML element containing the data about the successful or unsuccessful call,
    *    cannot be <code>null</code>.
    *
    * @param functionName
    *    the name of the function of this statistic, cannot be <code>null</code>.
    *
    * @return
    *    a {@link HashMap} containing the statistics.
    */
   private HashMap statisticsToMap(Element statElement, String functionName) {
      HashMap statMap = new HashMap();
      statMap.put("Function", functionName);
      statMap.put("Count", new Long(statElement.getAttribute("count")));
      if (!TextUtils.isEmpty(statElement.getAttribute("errorcode"))) {
         statMap.put("Error Code", statElement.getAttribute("errorcode"));
      } else if (statElement.getLocalName().equals("unsuccessful")) {
         statMap.put("Error Code", "<unsuccessful>");
      } else if (statElement.getLocalName().equals("successful")) {
         statMap.put("Error Code", "<successful>");
      }
      if (!"N/A".equals(statElement.getAttribute("average"))) {
         statMap.put("Average", new Long(statElement.getAttribute("average")));
      } else {
          statMap.put("Average", null);
      }
      try {
         Element minStat = statElement.getUniqueChildElement("min");
         if (!"N/A".equals(minStat.getAttribute("duration"))) {
            synchronized (DATE_FORMATTER) {
               statMap.put("Min Date", DATE_FORMATTER.parse(minStat.getAttribute("start")));
            }
            statMap.put("Min Duration", new Long(minStat.getAttribute("duration")));
         } else {
            statMap.put("Min Date", null);
            statMap.put("Min Duration", null);
         }
      } catch (Exception ex) {
         Utils.logProgrammingError(ex);
      }
      try {
         Element maxStat = statElement.getUniqueChildElement("max");
         if (!"N/A".equals(maxStat.getAttribute("duration"))) {
            synchronized (DATE_FORMATTER) {
               statMap.put("Max Date", DATE_FORMATTER.parse(maxStat.getAttribute("start")));
            }
            statMap.put("Max Duration", new Long(maxStat.getAttribute("duration")));
         } else {
            statMap.put("Max Date", null);
            statMap.put("Max Duration", null);
         }
      } catch (Exception ex) {
         Utils.logProgrammingError(ex);
      }
      try {
         Element lastStat = statElement.getUniqueChildElement("last");
         if (!"N/A".equals(lastStat.getAttribute("duration"))) {
            synchronized (DATE_FORMATTER) {
               statMap.put("Last Date", DATE_FORMATTER.parse(lastStat.getAttribute("start")));
            }
            statMap.put("Last Duration", new Long(lastStat.getAttribute("duration")));
         } else {
            statMap.put("Last Date", null);
            statMap.put("Last Duration", null);
         }
      } catch (Exception ex) {
         Utils.logProgrammingError(ex);
      }

      return statMap;
   }

   /**
    * Utility method to convert a {@link Properties} to a {@link CompositeDataSupport}.
    *
    * @param properties
    *    the properties to represent to the JMX agent, cannot be <code>null</code>.
    *
    * @return
    *    the {@link CompositeDataSupport} containng the properties, or <code>null</code>
    *    if an error occured.
    */
   private CompositeDataSupport propertiesToCompositeData(Properties properties) {
       try {
          String[] keys = (String[]) properties.keySet().toArray(new String[properties.size()]);
          OpenType[] itemTypes = new OpenType[keys.length];
          Object[] values = new Object[keys.length];
          for (int i = 0; i < itemTypes.length; i++) {
             itemTypes[i] = SimpleType.STRING;
             values[i] = properties.getProperty(keys[i]);
          }
          CompositeType propsType = new CompositeType("Properties type", "properties", keys, keys, itemTypes);
          CompositeDataSupport propsData = new CompositeDataSupport(propsType, keys, values);
          return propsData;
       } catch (OpenDataException odex) {
          Utils.logProgrammingError(odex);
          return null;
       }
   }

   /**
    * Registers the API MBean.
    *
    * @param api
    *    the API, never <code>null</code>.
    *
    * @throws Throwable
    *    if the MBeanServer cannot be found or created or one of the registered MBean fails.
    */
   static void registerMBean(API api) throws Throwable {
      javax.management.MBeanServer mBeanServer;
      try {
         mBeanServer = (javax.management.MBeanServer) Class.forName("java.lang.management.ManagementFactory").getMethod("getPlatformMBeanServer").invoke(null);
      } catch (ClassNotFoundException cnfe) {

         // Try with the JDK 1.4 and 1.3 compatible JMX reference implementation
         mBeanServer = javax.management.MBeanServerFactory.createMBeanServer();
      }
      APIManager mBean = new APIManager(api);
      javax.management.ObjectName objectName = new javax.management.ObjectName("org.xins.server.api." + api.getName() + ":type=APIManager");

      mBeanServer.registerMBean(mBean, objectName);

      // Register also the Log4J loggers

      // Create and Register the top level Log4J MBean
      HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
      javax.management.ObjectName mbo = new javax.management.ObjectName("org.xins.server.api." + api.getName() + ":hiearchy=log4j");
      mBeanServer.registerMBean(hdm, mbo);

      // Add the root logger to the Hierarchy MBean
      Logger rootLogger = Logger.getRootLogger();
      hdm.addLoggerMBean(rootLogger.getName());
   }
}
