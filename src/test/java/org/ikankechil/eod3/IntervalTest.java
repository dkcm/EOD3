/**
 * IntervalTest.java  v0.3  7 April 2014 11:35:15 PM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.Interval.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>Interval</code>.
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class IntervalTest {

  private Interval               interval;
  private Calendar               start;
  private Calendar               end;

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    start = Calendar.getInstance();
    start.add(YEAR, -1);
    end = Calendar.getInstance();
  }

  @Test
  public void cannotInstantiateWithNullStartEndAndFrequency() throws Exception {
    thrown.expect(NullPointerException.class);
    interval = new Interval(null, null, null);
  }

  @Test
  public void cannotInstantiateWithNullStartAndFrequency() throws Exception {
    thrown.expect(NullPointerException.class);
    interval = new Interval(null, end, null);
  }

  @Test
  public void cannotInstantiateWithNullStart() throws Exception {
    thrown.expect(NullPointerException.class);
    interval = new Interval(null, end, Frequencies.DAILY);
  }

  @Test
  public void cannotInstantiateWhenStartAfterEnd() throws Exception {
    final Calendar startAfterEnd = (Calendar) end.clone();
    startAfterEnd.add(YEAR, 1);

    thrown.expect(IllegalArgumentException.class);
    interval = new Interval(startAfterEnd, end, Frequencies.DAILY);
  }

  @Test
  public void instantiateWithNullEndAndFrequency() throws Exception {
    interval = new Interval(start, null, null);

    assertEquals(start, interval.start());
    checkDates(end, interval.end());
    assertNull(interval.frequency());
  }

  @Test
  public void instantiateWithNullEnd() throws Exception {
    interval = new Interval(start, null, Frequencies.WEEKLY);

    assertEquals(start, interval.start());
    checkDates(end, interval.end());
    assertEquals(Frequencies.WEEKLY, interval.frequency());
  }

  @Test
  public void instantiateWithNullFrequency() throws Exception {
    interval = new Interval(start, end, null);

    assertEquals(start, interval.start());
    assertEquals(end, interval.end());
    assertNull(interval.frequency());
  }

  @Test
  public void instantiateWithMonthlyFrequency() throws Exception {
    interval = new Interval(start, end, Frequencies.MONTHLY);

    assertEquals(start, interval.start());
    assertEquals(end, interval.end());
    assertEquals(Frequencies.MONTHLY, interval.frequency());
  }

  @Test
  public void instantiateWithSmallYearMonthAndDateButNullFrequency() throws Exception {
    final Calendar smallStart = Calendar.getInstance();
    final Calendar smallEnd = Calendar.getInstance();
    smallStart.set(9, Calendar.JANUARY, 1);
    smallEnd.set(9, Calendar.SEPTEMBER, 9);

    interval = new Interval(smallStart, smallEnd, null);

    assertEquals(smallStart, interval.start());
    assertEquals(smallEnd, interval.end());
    assertNull(interval.frequency());
  }

  private static final void checkDates(final Calendar expected, final Calendar actual) {
    assertEquals(expected.get(YEAR), actual.get(YEAR));
    assertEquals(expected.get(MONTH), actual.get(MONTH));
    assertEquals(expected.get(DATE), actual.get(DATE));
  }

  @Test
  public void constantSinceInceptionHasDailyFrequencyButNullStartAndEnd() throws Exception {
    assertNull(SINCE_INCEPTION.start());
    assertNull(SINCE_INCEPTION.end());
    assertEquals(Frequencies.DAILY, SINCE_INCEPTION.frequency());
  }

}
