/**
 * Interval.java  v0.3  2 April 2014 12:42:15 AM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import java.util.Calendar;

/**
 * Type description goes here.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class Interval {

  // immutable                            // allowed permutations
  private final Calendar       start;     // 0 1 1 1 1
  private final Calendar       end;       // 0 0 0 1 1
  private final Frequencies    frequency; // 1 0 1 0 1

  public static final Interval SINCE_INCEPTION = new Interval(null, null, Frequencies.DAILY);

  /**
   * Creates a new interval.
   *
   * @param start start of the interval
   * @param end end of the interval
   * @param frequency interval frequency
   * @throws IllegalArgumentException when start date is not before end date
   */
  public Interval(final Calendar start,
                  final Calendar end,
                  final Frequencies frequency) {
    if (start != null) {
      this.end = (end == null) ? Calendar.getInstance() : end;
      if (!start.before(this.end)) {
        throw new IllegalArgumentException("Start date after end date"); // TODO display start and end dates
      }
    }
    else if ((end == null) && (frequency != null)) {
      this.end = end;
    }
    else {
      throw new NullPointerException("Null start date");
    }
    this.start = start;
    this.frequency = frequency;
  }

  public Calendar start() {
    return start;
  }

  public Calendar end() {
    return end;
  }

  public Frequencies frequency() {
    return frequency;
  }

}
