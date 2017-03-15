/**
 * IntervalTest.java  v0.4  7 April 2014 11:35:15 PM
 *
 * Copyright © 2014-2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.Interval.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>Interval</code>.
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class IntervalTest {

  private Interval                interval;
  private Calendar                start;
  private Calendar                end;

  @Rule
  public final ExpectedException  thrown                 = ExpectedException.none();

  private static final DateFormat DATE_FORMAT            = new SimpleDateFormat("yyyyMMdd", Locale.US);

  private static final String     INTERVAL_FORMAT_STRING = "Interval [%s-%s %s]";
  private static final String     NO_START_DATE          = "No start date";
  private static final String     NO_END_DATE            = "No end date";
  private static final String     NO_FREQUENCY           = "No frequency";

  @Before
  public void setUp() throws Exception {
    start = Calendar.getInstance();
    start.add(YEAR, -1);
    end = Calendar.getInstance();
  }

  @Test
  public void cannotInstantiateWithNullStartEndAndFrequency() {
    thrown.expect(NullPointerException.class);
    interval = new Interval(null, null, null);
  }

  @Test
  public void cannotInstantiateWithNullStartAndFrequency() {
    thrown.expect(NullPointerException.class);
    interval = new Interval(null, end, null);
  }

  @Test
  public void cannotInstantiateWithNullStart() {
    thrown.expect(NullPointerException.class);
    interval = new Interval(null, end, Frequencies.DAILY);
  }

  @Test
  public void cannotInstantiateWhenStartEqualsEnd() {
    final Calendar startEqualsEnd = (Calendar) end.clone();

    thrown.expect(IllegalArgumentException.class);
    interval = new Interval(startEqualsEnd, end, Frequencies.DAILY);
  }

  @Test
  public void cannotInstantiateWhenStartAfterEnd() {
    final Calendar startAfterEnd = (Calendar) end.clone();
    startAfterEnd.add(YEAR, 1);

    thrown.expect(IllegalArgumentException.class);
    interval = new Interval(startAfterEnd, end, Frequencies.DAILY);
  }

  @Test
  public void instantiateWithNullEndAndFrequency() {
    interval = new Interval(start, null, null);

    assertEquals(start, interval.start());
    checkDates(end, interval.end());
    assertNull(interval.frequency());

    checkString(start, end, null, interval);
  }

  @Test
  public void instantiateWithNullEnd() {
    interval = new Interval(start, null, Frequencies.WEEKLY);

    assertEquals(start, interval.start());
    checkDates(end, interval.end());
    assertEquals(Frequencies.WEEKLY, interval.frequency());

    checkString(start, end, Frequencies.WEEKLY, interval);
  }

  @Test
  public void instantiateWithNullFrequency() {
    interval = new Interval(start, end, null);

    assertEquals(start, interval.start());
    assertEquals(end, interval.end());
    assertNull(interval.frequency());

    checkString(start, end, null, interval);
  }

  @Test
  public void instantiateWithMonthlyFrequency() {
    interval = new Interval(start, end, Frequencies.MONTHLY);

    assertEquals(start, interval.start());
    assertEquals(end, interval.end());
    assertEquals(Frequencies.MONTHLY, interval.frequency());

    checkString(start, end, Frequencies.MONTHLY, interval);
  }

  @Test
  public void instantiateWithSmallYearMonthAndDateButNullFrequency() {
    final Calendar smallStart = Calendar.getInstance();
    final Calendar smallEnd = Calendar.getInstance();
    smallStart.set(9, Calendar.JANUARY, 1);
    smallEnd.set(9, Calendar.SEPTEMBER, 9);

    interval = new Interval(smallStart, smallEnd, null);

    assertEquals(smallStart, interval.start());
    assertEquals(smallEnd, interval.end());
    assertNull(interval.frequency());

    checkString(smallStart, smallEnd, null, interval);
  }

  private static final void checkDates(final Calendar expected, final Calendar actual) {
    assertEquals(expected.get(YEAR), actual.get(YEAR));
    assertEquals(expected.get(MONTH), actual.get(MONTH));
    assertEquals(expected.get(DATE), actual.get(DATE));
  }

  @Test
  public void constantSinceInceptionHasDailyFrequencyButNullStartAndEnd() {
    assertNull(SINCE_INCEPTION.start());
    assertNull(SINCE_INCEPTION.end());
    assertEquals(Frequencies.DAILY, SINCE_INCEPTION.frequency());

    checkString(null, null, Frequencies.DAILY, SINCE_INCEPTION);
  }

  private static final void checkString(final Calendar start,
                                        final Calendar end,
                                        final Frequencies frequency,
                                        final Interval actualInterval) {
    final String startDate = (start != null) ? DATE_FORMAT.format(start.getTime()) : NO_START_DATE;
    final String endDate = (end != null) ? DATE_FORMAT.format(end.getTime()) : NO_END_DATE;

    final String expectedInterval = String.format(INTERVAL_FORMAT_STRING,
                                                  startDate,
                                                  endDate,
                                                  (frequency != null) ? frequency : NO_FREQUENCY);

    assertEquals(expectedInterval, actualInterval.toString());
  }

}
