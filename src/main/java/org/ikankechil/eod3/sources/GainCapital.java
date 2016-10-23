/**
 * GainCapital.java  v0.1  12 January 2016 10:46:37 pm
 *
 * Copyright � 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
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
 * A <code>Source</code> representing GAIN Capital, a U.S. provider of online
 * trading services.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class GainCapital extends Source {

  private static final String ZIP    = ".zip";

  private static final Logger logger = LoggerFactory.getLogger(GainCapital.class);

  public GainCapital() {
    super("http://ratedata.gaincapital.com/");

    exchanges.put(FX, EMPTY);

    // e.g.
    // http://ratedata.gaincapital.com/2015/04%20April/XAU_CHF_Week1.zip
    // http://ratedata.gaincapital.com/2015/10%20October/EUR_USD_Week4.zip
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    if (exchange == FX) {
      appendSymbol(url, symbol);
      url.insert(url.length() - THREE, UNDERSCORE);
    }
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(start.get(YEAR)).append(SLASH);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    url.append(UNDERSCORE);
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    url.append(ZIP);
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
        // GainCapital CSV format
        // lTid,cDealable,CurrencyPair,RateDateTime,RateBid,RateAsk
        // 4447393639,D,EUR/USD,2015-10-25 17:00:02.800000000,1.099940,1.100440
        // 4447393668,D,EUR/USD,2015-10-25 17:00:07.800000000,1.099960,1.100460
        // 4447393683,D,EUR/USD,2015-10-25 17:00:11.550000000,1.100010,1.100500
        // 4447393696,D,EUR/USD,2015-10-25 17:00:13.300000000,1.100010,1.100450
        // 4447393744,D,EUR/USD,2015-10-25 17:00:20.300000000,1.100320,1.100500

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final int comma = findNthLast(COMMA, line, THREE);
        final char[] characters = new char[symbol.length()];
        // set row name
        final int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;

        return String.valueOf(characters);
      }
    };
  }

}
