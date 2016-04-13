/**
 * Forexite.java  v0.1  29 March 2014 12:19:57 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.util.StringUtility.*;

import java.net.MalformedURLException;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.ikankechil.io.ZipTextReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Forexite.
 *
 * @author Daniel Kuan
 * @version
 */
class Forexite extends Source {

  static final Logger logger = LoggerFactory.getLogger(Forexite.class);

  public static void main(final String... arguments) throws MalformedURLException {
    final Forexite forexite = new Forexite();
    System.out.println(forexite.url(" "));
  }

  public Forexite() {
    super("http://www.forexite.com/free_forex_quotes/");

    // Forexite API
    // Daily:
    // http://www.forexite.com/free_forex_quotes/<YYYY>/daily<YYYY>.zip
    // http://www.forexite.com/free_forex_quotes/2013/daily2013.zip
    //
    // By minute:
    // http://www.forexite.com/free_forex_quotes/2007/12/281207.zip
    // http://www.forexite.com/free_forex_quotes/forex_history_arhiv.html
    //
    // Note:
    // 1. incoming data is in Zip format
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
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
    url.append(Frequencies.DAILY.toString().toLowerCase()); // TODO unfinished
  }

  @Override
  public TextReader newReader() {
    return new ZipTextReader();
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
        // Forexite CSV format
        // <TICKER>,<PER>,<DTYYYYMMDD>,<OPEN>,<HIGH>,<LOW>,<CLOSE>
        // EURUSD,D,20130101,1.3184,1.3222,1.3180,1.3204
        // EURUSD,D,20130102,1.3203,1.3297,1.3155,1.3187
        // EURUSD,D,20130103,1.3186,1.3186,1.3023,1.3035
        // GBPUSD,D,20130101,1.6232,1.6265,1.6224,1.6253
        // GBPUSD,D,20130102,1.6252,1.6378,1.6223,1.6250
        // GBPUSD,D,20130103,1.6250,1.6251,1.6057,1.6081
        // XAUUSD,D,20130101,1675.3,1675.3,1674.6,1674.6
        // XAUUSD,D,20130102,1675.5,1694.5,1670.0,1686.1
        // XAUUSD,D,20130103,1685.8,1689.6,1660.5,1663.5

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[line.length() - TWO];
        // set row name
        final int i = getChars(symbol, ZERO, SIX, characters, ZERO); // TODO other symbols present!
        // copy rest of line
        line.getChars(EIGHT, line.length(), characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
