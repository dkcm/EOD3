/**
 * Portfolio.java  v0.1  30 December 2015 11:27:50 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
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
 * A <code>Source</code> representing Portfolio.hu, a Hungarian financial portal.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class Portfolio extends Source {

  private static final Logger logger = LoggerFactory.getLogger(Portfolio.class);

  public Portfolio() {
    super(Portfolio.class);

    // supported exchanges
    exchanges.put(BET, EMPTY);

    // see http://www.chartoasis.com/free-analysis-software/free-data/portfolio-help.html
    // http://www.portfolio.hu/history/reszveny-adatok.php
    // tipus=1&rstartdate=2006-08-28&renddate=2015-12-29&open=1&max=1&min=1&close=1&forgdb=1&rticker=OTP
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
    // sort in descending / reverse chronological order
    return new TextTransformer(transform, THREE, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Részvény: OTP
        //
        // Dátum Nyitó Záró  Minimum Maximum Forgalom (db)
        // 2016-07-14  6475.0000 6485.0000 6395.0000 6535.0000 1000957
        // 2016-07-15  6479.0000 6460.0000 6425.0000 6521.0000 609300
        // 2016-07-18  6495.0000 6545.0000 6442.0000 6545.0000 442016
        // 2016-07-19  6520.0000 6630.0000 6476.0000 6630.0000 598582
        // 2016-07-20  6630.0000 6762.0000 6600.0000 6762.0000 820090
        // 2016-07-21  6780.0000 6849.0000 6760.0000 6866.0000 785786
        // 2016-07-22  6851.0000 6843.0000 6736.0000 6869.0000 556572
        // 2016-07-25  6840.0000 6857.0000 6816.0000 6900.0000 408962
        // 2016-07-26  6857.0000 6836.0000 6790.0000 6869.0000 402995
        // 2016-07-27  6850.0000 6722.0000 6711.0000 6855.0000 375554

        // Date, Open, Close, Low, High, Volume

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[symbol.length() + line.length() - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;

        // copy YYYYMMDD
        i = getChars(line, ZERO, FOUR, characters, ++i);  // year
        i = getChars(line, FIVE, SEVEN, characters, i);   // month
        i = getChars(line, EIGHT, TEN, characters, i);    // date

        final int close = findNth(TAB, line, ONE, ELEVEN) + ONE;
        final int low = findNth(TAB, line, ONE, close) + ONE;
        final int high = findNth(TAB, line, ONE, low) + ONE;
        final int volume = findNth(TAB, line, ONE, high) + ONE;

        // copy OHLCV
        characters[i] = COMMA;
        i = getChars(line, ELEVEN, close - ONE, characters, ++i); // open
        characters[i] = COMMA;
        i = getChars(line, high, volume - ONE, characters, ++i);  // high
        characters[i] = COMMA;
        i = getChars(line, low, high - ONE, characters, ++i);     // low
        characters[i] = COMMA;
        i = getChars(line, close, low - ONE, characters, ++i);    // close
        characters[i] = COMMA;
        line.getChars(volume, line.length(), characters, ++i);    // volume

        return String.valueOf(characters);
      }
    };
  }

}
