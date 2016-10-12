/**
 * FilenameConvention.java  v0.3  5 December 2014 11:58:54 pm
 *
 * Copyright © 2014-2016 Daniel Kuan. All rights reserved.
 */
package org.ikankechil.eod3;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.Frequencies.*;

import java.util.Calendar;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recognises but does not generate filenames without frequencies.
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class FilenameConvention {
  // TODO v0.4 move to new project

  // Accepted filename conventions:
  // 1. <symbol>.csv
  // 2. <symbol>_<frequency>.csv
  // 3. <symbol>_<YYYYMMDD>-<YYYYMMDD>.csv
  // 4. <symbol>_<YYYYMMDD>-<YYYYMMDD>_<frequency>.csv

  private static final Map<Interval, String> SUFFICES                  = new WeakHashMap<>();

  private static final Frequencies           DEFAULT_FREQUENCY         = DAILY;

  private static final char                  UNDERSCORE                = '_';
  private static final char                  DASH                      = '-';
  private static final char                  DOT                       = '.';

  /**
   * System property: org.ikankechil.eod3.FilenameConvention.fileExtension
   * <p>
   * default file extension: .csv
   */
  public static final String                 FILE_EXTENSION            = System.getProperty(FilenameConvention.class.getName() + ".fileExtension", ".csv");

  // regex building blocks
  private static final String                SYMBOL                    = "[A-Z0-9]+";
  private static final String                DATES                     = "\\d{8}";
  private static final String                OPTIONAL_FREQUENCY        = "(_[dwm])?";

  /**
   * regex: [A-Z0-9]+(_[dwm])?
   */
  public static final String                 FILENAME_REGEX            = SYMBOL + OPTIONAL_FREQUENCY + FILE_EXTENSION;
  /**
   * regex: [A-Z0-9]+_\\d{8}-\\d{8}(_[dwm])?
   */
  public static final String                 FILENAME_WITH_DATES_REGEX = SYMBOL + UNDERSCORE +
                                                                         DATES + DASH + DATES +
                                                                         OPTIONAL_FREQUENCY + FILE_EXTENSION;
  // date building blocks
  private static final char                  ZERO                      = '0';
  private static final int                   TEN                       = 10;
  private static final int                   HUNDRED                   = 100;
  private static final int                   THOUSAND                  = 1000;

  private static final int                   DATE_LENGTH               = 8;

  private static final Logger                logger                    = LoggerFactory.getLogger(FilenameConvention.class);

  private FilenameConvention() { /* do not instantiate */ }

  /**
   * Extract symbol from filename.
   *
   * @param filename
   * @return
   * @throws NullPointerException when <code>filename</code> is
   *           <code>null</code>
   * @throws IndexOutOfBoundsException when <code>filename</code> is an empty
   *           string
   */
  public static final String getSymbolFrom(final String filename) {
    final int underscore = filename.indexOf(UNDERSCORE);
    final String symbol = filename.substring(0, (underscore > -1) ?
                                                underscore :
                                                filename.lastIndexOf(DOT));
    logger.debug("Extracted symbol {} from: {}", symbol, filename);
    return symbol;
  }

  /**
   * Extract frequency from filename.
   *
   * @param filename
   * @return the <code>Frequency</code>, or <code>Frequencies.DAILY</code> if
   *         none explicitly specified
   * @throws NullPointerException when <code>filename</code> is
   *           <code>null</code>
   * @throws IndexOutOfBoundsException when <code>filename</code> is an empty
   *           string
   */
  public static final Frequencies getFrequencyFrom(final String filename) {
    final int fileExt = filename.indexOf(FILE_EXTENSION);
    if (fileExt < 0) {
      throw new IndexOutOfBoundsException();
    }

    Frequencies frequency = DEFAULT_FREQUENCY;  // default to daily
    final int penultimate = fileExt - 2;        // _<frequency>
    if ((penultimate > -1) &&
        filename.charAt(penultimate) == UNDERSCORE) {
      final char last = filename.charAt(penultimate + 1);
      for (final Frequencies f : values()) {
        if (last == f.frequency()) {
          frequency = f;
          break;
        }
      }
    }
    logger.debug("Extracted frequency {} from: {}", frequency, filename);

    return frequency;
  }

  /**
   * Extract filename extension from filename.
   *
   * @param filename
   * @return the filename extension
   * @throws NullPointerException when <code>filename</code> is
   *           <code>null</code>
   * @throws IndexOutOfBoundsException when <code>filename</code> is an empty
   *           string
   */
  public static final String getFilenameExtensionFrom(final String filename) {
    final String filenameExtension = filename.substring(filename.lastIndexOf(DOT));
    logger.debug("Extracted filename extension {} from: {}",
                 filenameExtension,
                 filename);
    return filenameExtension;
  }

  /**
   * Build filename suffix from interval.
   *
   * @param interval
   * @return
   */
  public static final String getSuffixFrom(final Interval interval) {
    String suffix;
    synchronized (SUFFICES) {
      if ((suffix = SUFFICES.get(interval)) == null) {
        SUFFICES.put(interval, suffix = buildSuffix(interval));
      }
    }
    return suffix;
  }

  private static final String buildSuffix(final Interval interval) {
    // append start and end dates
    final StringBuilder sb = new StringBuilder(32);

    final Calendar start = interval.start();
    final Calendar end = interval.end();
    if ((start != null) && (end != null)) {
      sb.append(UNDERSCORE);
      appendDate(sb, start);
      sb.append(DASH);
      appendDate(sb, end);
    }

    // append frequency
    // recognise but not generate filenames w/o frequencies
    final Frequencies frequency = interval.frequency();
    sb.append(UNDERSCORE).append((frequency != null) ? frequency.frequency()
                                                     : DEFAULT_FREQUENCY.frequency()); // default to daily

    // append filename extension
    final String suffix = sb.append(FILE_EXTENSION).toString();
    logger.debug("Built suffix {} from: {}", suffix, interval);

    return suffix;
  }

  private static final void appendDate(final StringBuilder sb, final Calendar calendar) {
    // year
    final int year = calendar.get(YEAR);
    if (year < THOUSAND) {
      sb.append(ZERO);
      if (year < HUNDRED) {
        sb.append(ZERO);
        if (year < TEN) {
          sb.append(ZERO);
        }
      }
    }
    sb.append(year);

    // month
    final int month = calendar.get(MONTH) + 1;
    if (month < TEN) {
      sb.append(ZERO);
    }
    sb.append(month);

    // date
    final int date = calendar.get(DATE);
    if (date < TEN) {
      sb.append(ZERO);
    }
    sb.append(date);

    logger.trace("Appended date: {}", sb);
  }

  /**
   * Remove dates, if any, from filename.
   *
   * @param filename
   * @return
   */
  public static final String removeDatesFrom(final String filename) {
    final String filenameWithoutDates;

    // look for the dash ('-') that separates the dates and then remove the
    // 8 or 9 characters on either side
    final int dash = filename.indexOf(DASH);
    if (dash > -1) {
      final int offset = DATE_LENGTH + 1;
      filenameWithoutDates = filename.substring(0, dash - offset) +
                             filename.substring(dash + offset);
      logger.debug("Removed dates from filename: {} -> {}",
                   filename,
                   filenameWithoutDates);
    }
    else {
      filenameWithoutDates = filename;
      logger.debug("No dates to remove from filename: {}", filenameWithoutDates);
    }

    return filenameWithoutDates;
  }

  public static final String getFilename(final String symbol, final Interval interval) {
    final String filename = symbol + getSuffixFrom(interval);
    logger.debug("Built filename {} from: {} and {}",
                 filename,
                 symbol,
                 interval);
    return filename;
  }

  public static final String getFilenameRegex(final boolean hasDates, final Frequencies frequency) {
    final StringBuilder regex = new StringBuilder(SYMBOL);
    if (hasDates) {
      regex.append(UNDERSCORE).append(DATES).append(DASH).append(DATES);
    }
    if (frequency == null) {
      regex.append(OPTIONAL_FREQUENCY);
    }
    else {
      regex.append(UNDERSCORE).append(frequency.frequency());
    }
    return regex.append(FILE_EXTENSION).toString();
  }

}
