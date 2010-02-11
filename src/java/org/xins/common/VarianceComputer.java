/*
 * $Id$
 *
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common;

/**
 * Continuous computation of mean and variance in a set of positive long
 * numbers.
 *
 * <p>Disclaimer: This class should only be used by XINS self.
 * This class can be removed from XINS at any time.
 *
 * <p>This class is thread-safe.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 2.3
 */
public final class VarianceComputer {

   /**
    * Constructs a new <code>VarianceComputer</code> object.
    */
   public VarianceComputer() {
      // empty
   }

   /**
    * The number of values added so far.
    */
   private int _count;

   /**
    * The mean (average) up until now.
    */
   private double _mean = Double.NaN;

   private double _m2;

   /**
    * The variance (deviation) up until now.
    */
   private double _variance;

   /**
    * Adds another value.
    *
    * @param value
    *    the value to add, must be &gt;= 0L.
    *
    * @throws IllegalArgumentException
    *    if <code>value &lt; 0L</code>.
    */
   public synchronized void add(long value) throws IllegalArgumentException {

      double n = (double) ++_count;

      if (n == 1) {
         _mean     = value;
         _m2       = 0.0;
         _variance = 0.0;
         return;
      }


      double x    = (double) value;
      double m2   = _m2;
      double mean = _mean;
      if (Double.isNaN(_mean)) {
         mean = 0.0;
      }

      double delta = x - mean;
      mean         = mean + delta/n;
      m2           = m2 + delta * (x - mean);

      // Store the results
      _variance = m2 / (n - 1);
      _mean     = mean;
      _m2       = m2;
   }

   /**
    * Retrieves the total number of times a value was added.
    *
    * @return
    *    the count.
    */
   public synchronized int getCount() {
      return _count;
   }

   /**
    * Retrieves the mean.
    *
    * @return
    *    the average, initially {@link Double#NaN}.
    */
   public synchronized double getMean() {
      return _mean;
   }

   /**
    * Retrieves the variance.
    *
    * @return
    *    the deviation, initially 0.0.
    */
   public synchronized double getVariance() {
      return _variance;
   }
}
