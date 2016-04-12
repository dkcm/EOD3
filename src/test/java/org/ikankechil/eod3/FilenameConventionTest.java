/**
 * FilenameConventionTest.java	v0.2	18 November 2015 12:23:46 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static org.ikankechil.eod3.FilenameConvention.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>FilenameConvention</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class FilenameConventionTest {

  @Rule
  public ExpectedException         thrown                                    = ExpectedException.none();

  private static final DateFormat  DATE_FORMAT                               = new SimpleDateFormat("yyyyMMdd", Locale.US);

  private static final String      EXPECTED_SYMBOL                           = "A"; // keep this to one character to test limits
  private static final String      EXPECTED_START_DATE                       = "20110103";
  private static final String      EXPECTED_END_DATE                         = "20141231";
  private static final Frequencies EXPECTED_FREQUENCY                        = Frequencies.MONTHLY;
  private static final Frequencies DEFAULT_FREQUENCY                         = Frequencies.DAILY;
  private static final char        ILLEGAL_FREQUENCY                         = 'Z';

  private static final char        UNDERSCORE                                = '_';
  private static final char        DASH                                      = '-';
  private static final String      EMPTY                                     = "";

  private static final String      FILE_EXTENSION                            = ".csv";
  private static final String      FREQUENCY_WITH_FILE_EXTENSION             = UNDERSCORE +
                                                                               (EXPECTED_FREQUENCY.frequency() +
                                                                               FILE_EXTENSION);
  private static final String      DEFAULT_FREQUENCY_WITH_FILE_EXTENSION     = UNDERSCORE +
                                                                               (DEFAULT_FREQUENCY.frequency() +
                                                                               FILE_EXTENSION);

  private static final String      FILENAME                                  = EXPECTED_SYMBOL +
                                                                               FILE_EXTENSION;
  private static final String      FILENAME_WITH_DEFAULT_FREQUENCY           = EXPECTED_SYMBOL + UNDERSCORE +
                                                                               DEFAULT_FREQUENCY.frequency() +
                                                                               FILE_EXTENSION;
  private static final String      FILENAME_WITH_FREQUENCY                   = EXPECTED_SYMBOL + UNDERSCORE +
                                                                               EXPECTED_FREQUENCY.frequency() +
                                                                               FILE_EXTENSION;
  private static final String      FILENAME_WITH_DATES                       = EXPECTED_SYMBOL + UNDERSCORE +
                                                                               EXPECTED_START_DATE + DASH +
                                                                               EXPECTED_END_DATE +
                                                                               FILE_EXTENSION;
  private static final String      FILENAME_WITH_DATES_AND_DEFAULT_FREQUENCY = EXPECTED_SYMBOL + UNDERSCORE +
                                                                               EXPECTED_START_DATE + DASH +
                                                                               EXPECTED_END_DATE + UNDERSCORE +
                                                                               DEFAULT_FREQUENCY.frequency() +
                                                                               FILE_EXTENSION;
  private static final String      FILENAME_WITH_DATES_AND_FREQUENCY         = EXPECTED_SYMBOL + UNDERSCORE +
                                                                               EXPECTED_START_DATE + DASH +
                                                                               EXPECTED_END_DATE + UNDERSCORE +
                                                                               EXPECTED_FREQUENCY.frequency() +
                                                                               FILE_EXTENSION;
  private static final String      FILENAME_WITH_ILLEGAL_FREQUENCY           = EXPECTED_SYMBOL + UNDERSCORE +
                                                                               ILLEGAL_FREQUENCY +
                                                                               FILE_EXTENSION;

  private static final String      FILENAME_REGEX                            = "[A-Z0-9]+(_[dwm])?" + FILE_EXTENSION;
  private static final String      FILENAME_WITH_DATES_REGEX                 = "[A-Z0-9]+_\\d{8}-\\d{8}(_[dwm])?" + FILE_EXTENSION;

  @Test
  public void cannotGetSymbolFromNullFilename() {
    thrown.expect(NullPointerException.class);
    getSymbolFrom(null);
  }

  @Test
  public void cannotGetFrequencyFromNullFilename() {
    thrown.expect(NullPointerException.class);
    getFrequencyFrom(null);
  }

  @Test
  public void cannotGetFilenameExtensionFromNullFilename() {
    thrown.expect(NullPointerException.class);
    getFilenameExtensionFrom(null);
  }

  @Test
  public void cannotGetSymbolFromEmptyFilename() {
    thrown.expect(IndexOutOfBoundsException.class);
    getSymbolFrom(EMPTY);
  }

  @Test
  public void cannotGetFrequencyFromEmptyFilename() {
    thrown.expect(IndexOutOfBoundsException.class);
    getFrequencyFrom(EMPTY);
  }

  @Test
  public void cannotGetFilenameExtensionFromEmptyFilename() {
    thrown.expect(IndexOutOfBoundsException.class);
    getFilenameExtensionFrom(EMPTY);
  }

  @Test
  public void cannotGetSuffixFromNullInterval() {
    thrown.expect(NullPointerException.class);
    getSuffixFrom(null);
  }

  @Test
  public void cannotRemoveDatesFromNullFilename() {
    thrown.expect(NullPointerException.class);
    removeDatesFrom(null);
  }

  @Ignore@Test
  public void cannotRemoveDatesFromEmptyFilename() {
    thrown.expect(IndexOutOfBoundsException.class);
    removeDatesFrom(EMPTY);
  }

  @Ignore@Test
  public void cannotGetFilenameFromNullSymbol() {
    thrown.expect(NullPointerException.class);
    getFilename(null, Interval.SINCE_INCEPTION);
  }

  @Test
  public void cannotGetFilenameFromNullInterval() {
    thrown.expect(NullPointerException.class);
    getFilename(EXPECTED_SYMBOL, null);
  }

  @Test
  public void symbolFromFilename() {
    assertEquals(EXPECTED_SYMBOL, getSymbolFrom(FILENAME));
  }

  @Test
  public void symbolFromFilenameWithFrequency() {
    assertEquals(EXPECTED_SYMBOL, getSymbolFrom(FILENAME_WITH_FREQUENCY));
  }

  @Test
  public void symbolFromFilenameWithDates() {
    assertEquals(EXPECTED_SYMBOL, getSymbolFrom(FILENAME_WITH_DATES));
  }

  @Test
  public void symbolFromFilenameWithDatesAndFrequency() {
    assertEquals(EXPECTED_SYMBOL, getSymbolFrom(FILENAME_WITH_DATES_AND_FREQUENCY));
  }

  @Test
  public void frequencyFromFilename() {
    assertEquals(DEFAULT_FREQUENCY, getFrequencyFrom(FILENAME));
  }

  @Test
  public void frequencyFromFilenameWithFrequency() {
    assertEquals(EXPECTED_FREQUENCY, getFrequencyFrom(FILENAME_WITH_FREQUENCY));
  }

  @Test
  public void frequencyFromFilenameWithDates() {
    assertEquals(DEFAULT_FREQUENCY, getFrequencyFrom(FILENAME_WITH_DATES));
  }

  @Test
  public void frequencyFromFilenameWithDatesAndFrequency() {
    assertEquals(EXPECTED_FREQUENCY, getFrequencyFrom(FILENAME_WITH_DATES_AND_FREQUENCY));
  }

  @Test
  public void illegalFrequencyDefaultsToDaily() {
    assertEquals(DEFAULT_FREQUENCY, getFrequencyFrom(FILENAME_WITH_ILLEGAL_FREQUENCY));
  }

  @Test
  public void filenameExtensionFromFilename() {
    assertEquals(FILE_EXTENSION, getFilenameExtensionFrom(FILENAME));
  }

  @Test
  public void filenameExtensionFromFilenameWithFrequency() {
    assertEquals(FILE_EXTENSION, getFilenameExtensionFrom(FILENAME_WITH_FREQUENCY));
  }

  @Test
  public void filenameExtensionFromFilenameWithDates() {
    assertEquals(FILE_EXTENSION, getFilenameExtensionFrom(FILENAME_WITH_DATES));
  }

  @Test
  public void filenameExtensionFromFilenameWithDatesAndFrequency() {
    assertEquals(FILE_EXTENSION, getFilenameExtensionFrom(FILENAME_WITH_DATES_AND_FREQUENCY));
  }

  @Test
  public void defaultFilenameExtension() {
    assertEquals(FILE_EXTENSION, FilenameConvention.FILE_EXTENSION);
  }

  @Test
  public void suffixFromIntervalWithoutDates() {
    assertEquals(FREQUENCY_WITH_FILE_EXTENSION, getSuffixFrom(new Interval(null, null, EXPECTED_FREQUENCY)));
  }

  @Test
  public void suffixFromIntervalWithoutDatesWithDefaultFrequency() {
    assertEquals(DEFAULT_FREQUENCY_WITH_FILE_EXTENSION, getSuffixFrom(new Interval(null, null, DEFAULT_FREQUENCY)));
  }

  @Test
  public void suffixFromIntervalWithFrequencyAndSmallYearMonthDate() {
    final Calendar[] smallStarts = new Calendar[2];
    final Calendar[] smallEnds = new Calendar[2];
    final Calendar smallStart = Calendar.getInstance();
    final Calendar smallEnd = Calendar.getInstance();

    smallStart.set(9, Calendar.SEPTEMBER, 9);
    smallEnd.set(10, Calendar.OCTOBER, 10);
    smallStarts[0] = (Calendar) smallStart.clone();
    smallEnds[0] = (Calendar) smallEnd.clone();

    smallStart.set(100, Calendar.SEPTEMBER, 9);
    smallEnd.set(1000, Calendar.OCTOBER, 10);
    smallStarts[1] = smallStart;
    smallEnds[1] = smallEnd;

    for (int i = 0; i < smallStarts.length; ++i) {
      final Calendar start = smallStarts[i];
      final Calendar end = smallEnds[i];
      final String expected = UNDERSCORE + DATE_FORMAT.format(start.getTime()) +
                              DASH + DATE_FORMAT.format(end.getTime()) +
                              UNDERSCORE + EXPECTED_FREQUENCY.frequency() +
                              FILE_EXTENSION;
      assertEquals(expected, getSuffixFrom(new Interval(start,
                                                        end,
                                                        EXPECTED_FREQUENCY)));
    }
  }

  @Test
  public void removeDatesFromFilename() {
    assertEquals(FILENAME, removeDatesFrom(FILENAME));
  }

  @Test
  public void removeDatesFromFilenameWithFrequency() {
    assertEquals(FILENAME_WITH_FREQUENCY, removeDatesFrom(FILENAME_WITH_FREQUENCY));
  }

  @Test
  public void removeDatesFromFilenameWithDates() {
    assertEquals(FILENAME, removeDatesFrom(FILENAME_WITH_DATES));
  }

  @Test
  public void removeDatesFromFilenameWithDatesAndFrequency() {
    assertEquals(FILENAME_WITH_FREQUENCY, removeDatesFrom(FILENAME_WITH_DATES_AND_FREQUENCY));
  }

  @Test
  public void filenameFromSymbolAndIntervalWithoutDates() throws Exception {
    // without dates
    assertEquals(FILENAME_WITH_DEFAULT_FREQUENCY,
                 getFilename(EXPECTED_SYMBOL, new Interval(null,
                                                           null,
                                                           DEFAULT_FREQUENCY)));
    assertEquals(FILENAME_WITH_FREQUENCY,
                 getFilename(EXPECTED_SYMBOL, new Interval(null,
                                                           null,
                                                           EXPECTED_FREQUENCY)));
  }

  @Test
  public void filenameFromSymbolAndIntervalWithoutEndDate() throws Exception {
    // create start and end dates
    final Calendar start = Calendar.getInstance();
    start.setTime(DATE_FORMAT.parse(EXPECTED_START_DATE));
    final String end = DATE_FORMAT.format(Calendar.getInstance().getTime());

    final String filenameWithDatesAndDefaultFrequency = FILENAME_WITH_DATES_AND_DEFAULT_FREQUENCY.replace(EXPECTED_END_DATE, end);
    assertEquals(filenameWithDatesAndDefaultFrequency,
                 getFilename(EXPECTED_SYMBOL, new Interval(start,
                                                           null,
                                                           null)));
    assertEquals(filenameWithDatesAndDefaultFrequency,
                 getFilename(EXPECTED_SYMBOL, new Interval(start,
                                                           null,
                                                           DEFAULT_FREQUENCY)));
    assertEquals(FILENAME_WITH_DATES_AND_FREQUENCY.replace(EXPECTED_END_DATE, end),
                 getFilename(EXPECTED_SYMBOL, new Interval(start,
                                                           null,
                                                           EXPECTED_FREQUENCY)));
  }

  @Test
  public void filenameFromSymbolAndIntervalWithDates() throws Exception {
    // create start and end dates
    final Calendar start = Calendar.getInstance();
    start.setTime(DATE_FORMAT.parse(EXPECTED_START_DATE));
    final Calendar end = Calendar.getInstance();
    end.setTime(DATE_FORMAT.parse(EXPECTED_END_DATE));

    // with dates
    assertEquals(FILENAME_WITH_DATES_AND_DEFAULT_FREQUENCY,
                 getFilename(EXPECTED_SYMBOL, new Interval(start,
                                                           end,
                                                           null)));
    assertEquals(FILENAME_WITH_DATES_AND_DEFAULT_FREQUENCY,
                 getFilename(EXPECTED_SYMBOL, new Interval(start,
                                                           end,
                                                           DEFAULT_FREQUENCY)));
    assertEquals(FILENAME_WITH_DATES_AND_FREQUENCY,
                 getFilename(EXPECTED_SYMBOL, new Interval(start,
                                                           end,
                                                           EXPECTED_FREQUENCY)));
  }

  @Test
  public void regex() {
    assertEquals(FILENAME_REGEX, FilenameConvention.FILENAME_REGEX);
  }

  @Test
  public void regexWithDates() {
    assertEquals(FILENAME_WITH_DATES_REGEX, FilenameConvention.FILENAME_WITH_DATES_REGEX);
  }

}
