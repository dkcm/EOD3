/**
 * IEX.java  v0.1  25 February 2018 9:15:39 PM
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
 * A <code>Source</code> representing IEX.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class IEX extends Source {

  private static final String SUFFIX = "/chart/5y?filter=date,open,high,low,close,volume&format=csv";

  private static final Logger logger = LoggerFactory.getLogger(IEX.class);

  public IEX() {
    super(IEX.class);

    // supported markets (see https://api.iextrading.com/1.0/ref-data/symbols)
    // NYSE, NASDAQ, AMEX and ARCA do not require suffices
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }

    // IEX API
    // https://iextrading.com/developer/
    //
    // Notes:
    // 1. incoming data in ascending chronological order
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url, symbol);
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
  void appendSuffix(final StringBuilder url) {
    url.append(SUFFIX);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, ONE, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // IEX CSV format
        // date,open,high,low,close,volume
        // 2013-02-25,64.8356,65.0171,63.2242,63.2571,92899597
        // 2013-02-26,63.4028,64.5056,62.5228,64.1385,125096657
        // 2013-02-27,64.0614,64.6342,62.9499,63.5099,146674682
        // 2013-02-28,63.4357,63.9814,63.0571,63.0571,80532382
        // 2013-03-01,62.5714,62.5971,61.4257,61.4957,137899041

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
