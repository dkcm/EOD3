/**
 * Interval.java  v0.4  2 April 2014 12:42:15 AM
 *
 * Copyright © 2014-2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A time duration with start, end and (data) frequency.
 *
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class Interval {

  // immutable                               // allowed permutations
  private final Calendar          start;     // 0 1 1 1 1
  private final Calendar          end;       // 0 0 0 1 1
  private final Frequencies       frequency; // 1 0 1 0 1

  public static final Interval    SINCE_INCEPTION = new Interval(null, null, Frequencies.DAILY);

  private static final DateFormat DATE_FORMAT     = new SimpleDateFormat("yyyyMMdd", Locale.US);

  private static final char       SPACE           = ' ';
  private static final char       DASH            = '-';
  private static final char       CLOSE_BRACKET   = ']';

  private static final String     INTERVAL_       = "Interval [";
  private static final String     NO_START_DATE   = "No start date";
  private static final String     NO_END_DATE     = "No end date";
  private static final String     NO_FREQUENCY    = "No frequency";

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
        synchronized (DATE_FORMAT) {
          final String startDate = DATE_FORMAT.format(start.getTime());
          final String endDate = DATE_FORMAT.format(this.end.getTime());
          // display start and end dates
          throw new IllegalArgumentException("Start date (" + startDate + ") not before end date (" + endDate + ")");
        }
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

  @Override
  public String toString() {
    final StringBuilder interval = new StringBuilder(INTERVAL_);
    synchronized (DATE_FORMAT) {
      final String startDate = (start == null) ?
                               NO_START_DATE :
                               DATE_FORMAT.format(start.getTime());
      final String endDate = (end == null) ?
                             NO_END_DATE :
                             DATE_FORMAT.format(end.getTime());
      interval.append(startDate).append(DASH).append(endDate);
    }
    interval.append(SPACE)
            .append((frequency == null) ? NO_FREQUENCY : frequency)
            .append(CLOSE_BRACKET);
    return interval.toString(); // Interval [<yyyyMMdd>-<yyyyMMdd> <frequency>]
  }

}
