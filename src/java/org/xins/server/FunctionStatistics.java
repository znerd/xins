/*
 * $Id: FunctionStatistics.java,v 1.23 2008/07/04 10:22:51 agoubard Exp $
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.log4j.NDC;

import org.xins.common.VarianceComputer;
import org.xins.common.text.DateConverter;
import org.xins.common.xml.Element;

/**
 * Statistics of a function.
 *
 * <p>The implementation of this class is thread-safe.
 *
 * @version $Revision: 1.23 $ $Date: 2008/07/04 10:22:51 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
class FunctionStatistics {

   /**
    * String to insert instead of a figure when the figure is unavailable.
    */
   private static final String NOT_AVAILABLE = "N/A";

   /**
    * The time zone used when generating dates for output.
    */
   private static final TimeZone TIME_ZONE = TimeZone.getDefault();

   /**
    * Constructs a new <code>FunctionStatistics</code> instance.
    */
   FunctionStatistics() {
      _successful          = new Statistic();
      _unsuccessful        = new Statistic();
      _errorCodeStatistics = new TreeMap<String,Statistic>();
   }

   /**
    * Statistics for the successful calls. Never <code>null</code>.
    */
   private final Statistic _successful;

   /**
    * Statistic over the unsuccessful calls. Never <code>null</code>.
    */
   private final Statistic _unsuccessful;

   /**
    * Statistics over the unsuccessful calls sorted by error code.
    * The key of the map is the error code and the Statistic object
    * corresponding to the error code. Never <code>null</code>.
    */
   private final TreeMap<String,Statistic> _errorCodeStatistics;

   /**
    * Callback method that may be called after a call to this function. This
    * method will store statistics-related information.
    *
    * <p />This method does not <em>have</em> to be called. If statistics
    * gathering is disabled, then this method should not be called.
    *
    * @param start
    *    the start time, in milliseconds since the UNIX Epoch.
    *
    * @param success
    *    indication if the call was successful.
    *
    * @param errorCode
    *    the error code returned by the function if a result is unsuccessful;
    *    this value is <code>null</code> only when <code>success</code>
    *    is <code>true</code>.
    *
    * @return
    *    returns the duration in milliseconds of the call of the function;
    *    the duration is computed as the difference in between
    *    the start time and the time that this method has been invoked.
    */
   final synchronized long recordCall(long    start,
                                      boolean success,
                                      String  errorCode) {

      long duration = System.currentTimeMillis() - start;

      // Call succeeded
      if (success) {

         _successful.recordCall(start, duration);

      // Call failed
      } else {

         _unsuccessful.recordCall(start, duration);

         Statistic errorCodeStat = _errorCodeStatistics.get(errorCode);
         if (errorCodeStat == null) {
            errorCodeStat = new Statistic();
            _errorCodeStatistics.put(errorCode, errorCodeStat);
         }
         errorCodeStat.recordCall(start, duration);
      }
      return duration;
   }

   /**
    * Resets the statistics for this function.
    */
   final synchronized void resetStatistics() {
      _successful.reset();
      _unsuccessful.reset();
      _errorCodeStatistics.clear();
   }

   /**
    * Get the successful statistic as an {@link Element}.
    *
    * @return
    *    the successful element, cannot be <code>null</code>
    */
   public synchronized Element getSuccessfulElement() {
      return _successful.getElement(true, null);
   }


   /**
    * Get the unsuccessful statistics as an array of {@link org.xins.common.xml.Element}.
    *
    * @param detailed
    *    If <code>true</code>, the unsuccessful results will be returned
    *    per error code. Otherwise only one unsuccessful containing all
    *    unsuccessful result will be returned.
    *
    * @return
    *    the successful element, cannot be empty.
    */
   public synchronized Element[] getUnsuccessfulElement(boolean detailed) {
      if (!detailed || _errorCodeStatistics.size() == 0) {
         Element[] result = new Element[1];
         result[0] = _unsuccessful.getElement(false, null);
         return result;
      } else {
         Element[] result = new Element[_errorCodeStatistics.size()];
         int i = 0;
         Iterator itErrorCodeStats = _errorCodeStatistics.keySet().iterator();
         while (itErrorCodeStats.hasNext()) {
            String nextErrorCode = (String) itErrorCodeStats.next();
            Statistic nextStat = (Statistic) _errorCodeStatistics.get(nextErrorCode);
            result[i] = nextStat.getElement(false, nextErrorCode);
            i++;
         }
         return result;
      }
   }

   /**
    * Group of statistics data.
    *
    * <p>The implementation of this class is thread-safe.
    *
    * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
    * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
    */
   private static final class Statistic {

	  /**
	   * The variance computer.
	   */
	  private VarianceComputer _varianceComputer = new VarianceComputer();

      /**
       * The start time of the most recent call. Initially <code>0L</code>.
       */
      private long _lastStart;

      /**
       * The transaction ID of the most recent call.
       * Initially null.
       */
      private String _lastTx;

      /**
       * The duration of the most recent call. Initially <code>0L</code>.
       */
      private long _lastDuration;

      /**
       * The total duration of all calls up until now. Initially
       * <code>0L</code>.
       */
      private long _duration;

      /**
       * The minimum time a call took. Initially set to
       * {@link Long#MAX_VALUE}.
       */
      private long _min = Long.MAX_VALUE;

      /**
       * The start time of the call that took the shortest. Initially
       * <code>0L</code>.
       */
      private long _minStart;

      /**
       * The transaction ID of the call that took the shortest.
       * Initially null.
       */
      private String _minTx;

      /**
       * The duration of the call that took the longest. Initially
       * <code>0L</code>.
       */
      private long _max;

      /**
       * The start time of the call that took the longest. Initially
       * <code>0L</code>.
       */
      private long _maxStart;

      /**
       * The transaction ID of the call that took the longest.
       * Initially null.
       */
      private String _maxTx;

      /**
       * Constructs a new <code>Statistic</code> object.
       */
      private Statistic() {
         _min = Long.MAX_VALUE;
      }

      /**
       * Records a call.
       *
       * @param start
       *    the start time, in milliseconds since the UNIX Epoch, not
       *    <code>null</code>.
       *
       * @param duration
       *    duration of the call, in milliseconds since the
       *    <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
       */
      public synchronized void recordCall(long start, long duration) {
         _lastStart    = start;
         _lastDuration = duration;
         _lastTx       = getTx();

         _varianceComputer.add(duration);
         _duration += duration;
         _min      = _min > duration ? duration : _min;
         _max      = _max < duration ? duration : _max;
         _minStart = (_min == duration) ? start : _minStart;
         _maxStart = (_max == duration) ? start : _maxStart;
         _minTx    = (_min == duration) ? _lastTx : _minTx;
         _maxTx    = (_max == duration) ? _lastTx : _maxTx;
      }

      private final String getTx() {
         return NDC.peek();
      }

      /**
       * Get this statistic as an {@link Element}.
       *
       * @param successful
       *    true if the result is successful, false otherwise.
       * @param errorCode
       *    the errorCode of the unsuccessful result, if you want it also
       *    specified in the returned element.
       *
       * @return
       *    the statistics as an XML {@link Element}, cannot be <code>null</code>.
       */
      public synchronized Element getElement(boolean successful, String errorCode) {

         String average;
         String min;
         String minStart;
         String max;
         String maxStart;
         String lastStart;
         String lastDuration;
         int       calls = _varianceComputer.getCount();
         double     mean = _varianceComputer.getMean();
         String variance = String.valueOf(_varianceComputer.getVariance());
         if (calls == 0) {
            average      = NOT_AVAILABLE;
            min          = NOT_AVAILABLE;
            minStart     = NOT_AVAILABLE;
            max          = NOT_AVAILABLE;
            maxStart     = NOT_AVAILABLE;
            lastStart    = NOT_AVAILABLE;
            lastDuration = NOT_AVAILABLE;
         } else {
            average      = String.valueOf(mean);
            min          = String.valueOf(_min);
            minStart     = DateConverter.toDateString(TIME_ZONE, _minStart);
            max          = String.valueOf(_max);
            maxStart     = DateConverter.toDateString(TIME_ZONE, _maxStart);
            lastStart    = DateConverter.toDateString(TIME_ZONE, _lastStart);
            lastDuration = String.valueOf(_lastDuration);
         }
         Element element = new Element(successful ? "successful" : "unsuccessful");
         element.setAttribute("count",    String.valueOf(calls));
         element.setAttribute("average",  average);
         element.setAttribute("variance", variance);
         if (errorCode != null) {
            element.setAttribute("errorcode", errorCode);
         }
         Element minElem = new Element("min");
         minElem.setAttribute("start",    minStart);
         minElem.setAttribute("duration", min);
         minElem.setAttribute("tx",       _minTx);
         element.add(minElem);

         Element maxElem = new Element("max");
         maxElem.setAttribute("start",    maxStart);
         maxElem.setAttribute("duration", max);
         maxElem.setAttribute("tx",       _maxTx);
         element.add(maxElem);

         Element lastElem = new Element("last");
         lastElem.setAttribute("start",    lastStart);
         lastElem.setAttribute("duration", lastDuration);
         lastElem.setAttribute("tx",       _lastTx);
         element.add(lastElem);
         return element;
      }

      /**
       * Resets this statistic.
       */
      public synchronized void reset() {
         _varianceComputer = new VarianceComputer();
         _lastStart    = 0L;
         _lastDuration = 0L;
         _duration     = 0L;
         _min          = Long.MAX_VALUE;
         _minStart     = 0L;
         _max          = 0L;
         _maxStart     = 0L;
      }
   }
}
