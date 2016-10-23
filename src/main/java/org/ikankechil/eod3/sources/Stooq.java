/**
 * Stooq.java  v0.6  27 January 2014 PM 07:12:11 PM
 *
 * Copyright � 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;
import java.util.EnumSet;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Stooq.com, a Polish on-line finance
 * portal.
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class Stooq extends Source {

  // Date-related URL parameters
  private static final String START_DATE = "&d1=";
  private static final String END_DATE   = "&d2=";
  private static final String FREQUENCY  = "&i=";

  // Exchange-related constants
  private static final String US         = ".us";
  private static final String UK         = ".uk";
  private static final String DE         = ".de";
  private static final String HK         = ".hk";
  private static final String JP         = ".jp";
  private static final String HU         = ".hu";
//  private static final String F          = ".f";  // futures

  static final Logger         logger     = LoggerFactory.getLogger(Stooq.class);

  public Stooq() {
    super(Stooq.class);

    // supported markets
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, US);
    }

    // GPW and FX do not require a suffix
    for (final Exchanges exchange : EnumSet.of(GPW, FX)) {
      exchanges.put(exchange, EMPTY);
    }

    exchanges.put(LSE, UK);
    exchanges.put(FWB, DE);
    exchanges.put(HKEX, HK);
    exchanges.put(TSE, JP);
    exchanges.put(BET, HU);

    // Notes:
    // 1. FX data from 1970s / 80s
    // 2. Incoming source data in chronological order
    // 3. Futures data available
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    appendDate(url, START_DATE, start);
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    appendDate(url, END_DATE, end);
    logger.debug("End date: {}", url);
  }

  private static final void appendDate(final StringBuilder url, final String parameter, final Calendar calendar) {
    url.append(parameter).append(calendar.get(YEAR));
    final int month = calendar.get(MONTH) + ONE;
    if (month < TEN) {
      url.append(ZERO);
    }
    url.append(month);
    final int date = calendar.get(DATE);
    if (date < TEN) {
      url.append(ZERO);
    }
    url.append(date);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if ((frequency != null) && (frequency != Frequencies.DAILY)) {
      url.append(FREQUENCY).append(frequency.frequency());
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    // sort in descending / reverse chronological order
    return new TextTransformer(transform, ONE, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Stooq CSV format (in chronological order)
        // Date,Open,High,Low,Close
        // 1971-01-04,0.5353,0.5353,0.5353,0.5353
        // 1971-01-05,0.535,0.535,0.535,0.535
        // 1971-01-06,0.5352,0.5352,0.5352,0.5352

        // MetaStock CSV format (in reverse chronological order)
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[(symbol.length() + line.length()) - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy year
        i = getChars(line, ZERO, FOUR, characters, ++i);
        // copy month
        i = getChars(line, FIVE, SEVEN, characters, i);
        // copy rest of line
        line.getChars(EIGHT, line.length(), characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
