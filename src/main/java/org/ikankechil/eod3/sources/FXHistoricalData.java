/**
 * FXHistoricalData.java	v0.4	28 March 2014 12:43:51 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.ikankechil.io.ZipTextReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing FXHistoricalData.
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class FXHistoricalData extends Source {

  private static final String DAY    = "_day.zip";

  static final Logger         logger = LoggerFactory.getLogger(FXHistoricalData.class);

  public FXHistoricalData() {
    super("http://www.fxhistoricaldata.com/download/");

    // supported markets
    // FX does not require a suffix
    exchanges.put(FX, EMPTY);

    // FXHistoricalData API
    // http://www.fxhistoricaldata.com/download/<Symbol>_<Frequency>.zip
    //
    // e.g.
    // http://www.fxhistoricaldata.com/download/EURUSD_day.zip
    // http://www.fxhistoricaldata.com/download/EURUSD_hour.zip
    //
    // Notes:
    // 1. incoming data is in Zip format
    // 2. data ultimately sourced from FXCM
    // 3. FX data from 2001
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
    url.append(DAY);
    logger.debug("Frequency: {}", url);
  }

  @Override
  public TextReader newReader() {
    return new ZipTextReader();
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
        // FXHistoricalData CSV format
        // <TICKER>,<DATE>,<TIME>,<OPEN>,<LOW>,<HIGH>,<CLOSE>
        // EURUSD,20010103,00:00:00,0.9507,0.9262,0.9569,0.9271
        // EURUSD,20010104,00:00:00,0.9271,0.9269,0.9515,0.9507
        // EURUSD,20010105,00:00:00,0.9507,0.9464,0.9591,0.9575

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[line.length() - NINE];
        final int comma = findNth(COMMA, line, TWO, ZERO);
        // copy symbol and date
        line.getChars(ZERO, comma, characters, ZERO);

        // indices
        final int o = findNth(COMMA, line, ONE, comma + ONE);
        final int l = findNth(COMMA, line, ONE, o + ONE);
        final int h = findNth(COMMA, line, ONE, l + ONE);
        final int c = findNth(COMMA, line, ONE, h + ONE);
        // copy open
        int i = getChars(line, o, l, characters, comma);
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
