/**
 * FXHistoricalData.java  v0.6  28 March 2014 12:43:51 AM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing FXHistoricalData.
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class FXHistoricalData extends Source {

  // Date-related URL parameters
  private static final String FREQUENCY = "&timeframe=";
  private static final String DAY       = "day";
  private static final String WEEK      = "week";

  private static final Logger logger    = LoggerFactory.getLogger(FXHistoricalData.class);

  public FXHistoricalData() {
    super(FXHistoricalData.class);

    // supported markets
    // FX does not require a suffix
    exchanges.put(FX, EMPTY);

    // Notes:
    // 1. incoming data is in CSV format (no longer Zip format)
    // 2. data ultimately sourced from FXCM
    // 3. FX data from 2001
    // 4. frequencies supported: hourly, daily and weekly
  }

  @Override
  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // default to daily
    url.append(FREQUENCY).append((frequency == Frequencies.WEEKLY) ? WEEK : DAY);
    logger.debug("Frequency: {}", url);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    // already sorted in descending / reverse chronological order
    return new TextTransformer(transform);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // FXHistoricalData CSV format
        // Current:
        // EURUSD,2016-11-25 00:00:00,1.0562,1.0542,1.0614,1.0594
        // EURUSD,2016-11-24 00:00:00,1.0544,1.0524,1.0581,1.0562
        // EURUSD,2016-11-23 00:00:00,1.0634,1.0531,1.0643,1.0546
        // EURUSD,2016-11-22 00:00:00,1.0639,1.0585,1.0658,1.0633
        // EURUSD,2016-11-21 00:00:00,1.0584,1.0584,1.0649,1.0638

        //
        // Old:
        // <TICKER>,<DATE>,<TIME>,<OPEN>,<LOW>,<HIGH>,<CLOSE>
        // EURUSD,20010103,00:00:00,0.9507,0.9262,0.9569,0.9271
        // EURUSD,20010104,00:00:00,0.9271,0.9269,0.9515,0.9507
        // EURUSD,20010105,00:00:00,0.9507,0.9464,0.9591,0.9575

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[line.length() - ELEVEN];

        // copy symbol and year
        final int dash = findNth(HYPHEN, line, ONE, ZERO);
        line.getChars(ZERO, dash, characters, ZERO);
        // copy month and date
        getChars(line, dash + ONE, dash + THREE, characters, dash);
        getChars(line, dash + FOUR, dash + SIX, characters, dash + TWO);

        // indices
        final int o = findNth(COMMA, line, ONE, dash + SIX);
        final int l = findNth(COMMA, line, ONE, o + ONE);
        final int h = findNth(COMMA, line, ONE, l + ONE);
        final int c = findNth(COMMA, line, ONE, h + ONE);
        // copy open
        int i = getChars(line, o, l, characters, dash + FOUR);
        // copy high
        i = getChars(line, h, c, characters, i);
        // copy low
        i = getChars(line, l, h, characters, i);
        // copy close
        getChars(line, c, line.length(), characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
