/**
 * Rava.java  v0.1  29 October 2016 10:50:21 pm
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
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
 * A <code>Source</code> representing Rava Bursátil S.A., an Argentinian stock
 * broker.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class Rava extends Source {

  private static final Logger logger = LoggerFactory.getLogger(Rava.class);

  public Rava() {
    super(Rava.class);

    // supported markets
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(ARCA, EMPTY);
    exchanges.put(BCBA, EMPTY);
  }

  @Override
  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    logger.debug(UNSUPPORTED);
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
        // Rava CSV format
        // fecha,apertura,maximo,minimo,cierre,volumen,openint
        // "2005-01-03","2.07791","2.08835","2.03614","2.03614","935662","0"
        // "2005-01-04","2.02570","2.04659","2.01004","2.01004","635008","0"
        // "2005-01-05","1.98394","1.98394","1.93173","1.95261","304675","0"
        // "2005-01-06","1.95783","1.99438","1.95783","1.96305","417970","0"
        // "2005-01-07","1.96827","1.98916","1.94217","1.94217","161044","0"
        // "2005-01-10","1.95261","1.98394","1.94217","1.98394","314497","0"

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final int volumeEnd = findNthLast(COMMA, line, ONE) - ONE;
        final char[] characters = new char[symbol.length() + volumeEnd - TWELVE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;

        // copy date
        i = getChars(line, ONE, FIVE, characters, ++i); // year
        i = getChars(line, SIX, EIGHT, characters, i);  // month
        i = getChars(line, NINE, ELEVEN, characters, i);// date
        characters[i] = COMMA;

        // copy OHLC
        int doubleQuote = THIRTEEN;
        for (int j = ZERO; j < FOUR; ++j) {
          i = getChars(line,
                       ++doubleQuote,
                       doubleQuote = findNth(DOUBLE_QUOTE,
                                             line,
                                             ONE,
                                             doubleQuote),
                       characters,
                       ++i);
          characters[i] = COMMA;
          doubleQuote += TWO;
        }

        // copy volume
        i = getChars(line, ++doubleQuote, volumeEnd, characters, ++i);

        return String.valueOf(characters);
      }
    };
  }

}
