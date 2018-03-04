/**
 * Macrotrends.java  v0.1  2 March 2018 11:44:00 PM
 *
 * Copyright © 2018 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

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
 * A <code>Source</code> representing Macrotrends.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class Macrotrends extends Source {

  private static final Logger logger = LoggerFactory.getLogger(Macrotrends.class);

  public Macrotrends() {
    super(Macrotrends.class);

    // supported markets
    // NYSE, NASDAQ, AMEX and ARCA do not require suffices
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }

    // Notes:
    // 1. export limit of 100 files
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
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, ELEVEN, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Macrotrends CSV format
        // date,open,high,low,close,volume
        // 1986-05-29,2.6343,2.6584,2.6343,2.6526,15736000
        // 1986-05-30,2.6409,2.6708,2.6409,2.6708,6500800
        // 1986-06-02,2.6735,2.6798,2.6493,2.6614,314400
        // 1986-06-03,2.6559,2.6859,2.6496,2.6801,154400
        // 1986-06-04,2.6611,2.6853,2.6432,2.6432,192000

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[symbol.length() + line.length() - ONE];
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
