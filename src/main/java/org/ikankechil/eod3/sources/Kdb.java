/**
 * Kdb.java	v0.3	29 December 2015 4:25:33 pm
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;

/**
 * A <code>Source</code> representing k-db.com.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class Kdb extends Source {

  // Date-related URL parameters
  private static final String START_YEAR = "&year=";

  // Exchange-related constants
  private static final String TOKYO      = "-T";
//  private static final String OSAKA      = "-O";

  // Other URL constants
  private static final String DOWNLOAD   = "?download=csv";

  public Kdb() {
    super(Kdb.class);

    // supported exchanges
    exchanges.put(TSE, TOKYO);
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    super.appendSymbolAndExchange(url, symbol, exchange);
    url.append(DOWNLOAD);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_YEAR).append(start.get(YEAR));
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // do nothing
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, TWO, false);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // 6502-T,xxx,yyy
        // YYYY-MM-DD,Open,High,Low,Close,Volume,Value
        // 2015-12-29,229.0,234.8,223.1,232.1,73080000,16789443700
        // 2015-12-28,215.0,229.1,214.3,226.0,81249000,18182179200
        // 2015-12-25,222.0,225.6,216.0,216.6,93109000,20398613400
        // 2015-12-24,221.0,230.9,220.0,220.6,122009000,27276628700
        // 2015-12-22,251.9,253.5,221.0,223.5,213954000,49717132700

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final int comma = findNth(COMMA, line, SIX, TEN);
        final char[] characters = new char[(symbol.length() + comma) - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy year
        i = getChars(line, ZERO, FOUR, characters, ++i);
        // copy month
        i = getChars(line, FIVE, SEVEN, characters, i);
        // copy rest of line
        line.getChars(EIGHT, comma, characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
