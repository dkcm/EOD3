/**
 * Quandl.java  v0.3  3 November 2014 4:51:49 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Quandl.
 * <p>
 * https://www.quandl.com/docs/api
 * <p>
 * Authentication token can be specified by the System property:
 * org.ikankechil.eod3.sources.Quandl.apiKey
 * <p>
 * Download volume restrictions apply: Authenticated users have a limit of 2000
 * calls per 10 minutes, and a limit of 50000 calls per day.
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class Quandl extends Source {
  // TODO extend support for asset classes other than Equities

  private final String        authenticationToken;
  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  // Date-related URL parameters
  private static final String START_DATE    = "&start_date=";
  private static final String END_DATE      = "&end_date=";
//  private static final String START_DATE    = "&trim_start="; // v1
//  private static final String END_DATE      = "&trim_end=";   // v1
  private static final String FREQUENCY     = "&collapse=";

  // Authentication token / API key property: org.ikankechil.eod3.sources.Quandl.apiKey
  private static final String API_KEY       = "&api_key=";
//  private static final String API_KEY       = "&auth_token="; // v1
  private static final String AUTH_TOKEN    = System.getProperty(Quandl.class.getName() + ".apiKey");

  // other URL parameters
  private static final String CSV           = ".csv";
  private static final String SUFFIX        = "&order=desc&exclude_column_names=false&transform=none";
//  private static final String SUFFIX        = "&sort_order=desc&exclude_headers=false&transformation=none"; // v1

  // Exchange-related constants
  private static final String WIKI          = "WIKI";   // U.S. Equities
//  private static final String CURRFX        = "CURRFX"; // FX

  private static final Logger logger        = LoggerFactory.getLogger(Quandl.class);

  public Quandl() {
    this(null);
  }

  public Quandl(final String authenticationToken) {
    super("https://www.quandl.com/api/v3/datasets/");
//    super("http://www.quandl.com/api/v1/datasets/"); // v1 soon to be deprecated
//    super("http://quandl.com/api/v1/multisets.csv?sort_order=desc&exclude_headers=false&columns="); // deprecated

    this.authenticationToken = authenticationToken == null || authenticationToken.isEmpty() ?
                               AUTH_TOKEN :
                               authenticationToken;

    // supported markets (see https://www.quandl.com/resources/api-for-stock-data)
    exchanges.put(NYSE, WIKI);
    exchanges.put(NASDAQ, WIKI);
    exchanges.put(AMEX, WIKI);
    exchanges.put(LSE, LSE.toString());
//    exchanges.put(FX, CURRFX);
    // TODO FX has a different format from equities (i.e. Date,Rate,High (est),Low (est))
    // either maintain a table of FX symbols or overload newTransform() with exchange

    // e.g. https://www.quandl.com/api/v3/datasets/WIKI/FB/data.csv?column_index=4&exclude_column_names=true&rows=3&start_date=2012-11-01&end_date=2013-11-30&order=asc&collapse=quarterly&transform=rdiff
    //      http://www.quandl.com/api/v1/datasets/WIKI/AAPL.csv?sort_order=asc&exclude_headers=true&rows=3&trim_start=2012-11-01&trim_end=2013-11-30&column=4&collapse=quarterly&transformation=rdiff
    //      http://www.quandl.com/api/v1/datasets/WIKI/A.csv?&trim_start=1999-01-01&trim_end=2014-12-31&sort_order=desc&exclude_headers=false&transformation=none
    //      [Multi-sets deprecated] https://quandl.com/api/v1/multisets.csv?columns=FRED.GDP.1,DOE.RWTC.1,WIKI.AAPL.4&collapse=annual&transformation=rdiff&rows=10
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    // Quandl code format: <database_code>/<dataset_code>
    appendExchange(url, exchange);
    url.append(SLASH);
    appendSymbol(url, symbol);

    // e.g. https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?
    url.append(CSV).append(QUESTION);
    logger.debug("Symbol and exchange: {}", url);
  }

//  @Deprecated@Override
//  void appendSymbol(final StringBuilder url, final String symbol) {
//    // Multi-sets deprecated
//    url.append(WIKI).append(symbol).append(DOT).append(ONE).append(COMMA)   // open
//       .append(WIKI).append(symbol).append(DOT).append(TWO).append(COMMA)   // high
//       .append(WIKI).append(symbol).append(DOT).append(THREE).append(COMMA) // low
//       .append(WIKI).append(symbol).append(DOT).append(FOUR).append(COMMA)  // close
//       .append(WIKI).append(symbol).append(DOT).append(FIVE);               // volume
//    logger.debug("Columns: {}", url);
//  }
//
//  @Deprecated@Override
//  void appendExchange(final StringBuilder url, final Exchanges exchange) {
//    // do nothing
//    logger.debug(UNSUPPORTED);
//  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(urlDateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(urlDateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null && frequency != Frequencies.DAILY) {
      url.append(FREQUENCY).append(frequency.toString().toLowerCase());
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    url.append(SUFFIX);
    appendApiKey(url);
  }

  private final void appendApiKey(final StringBuilder url) {
    if (authenticationToken != null && !authenticationToken.isEmpty()) {
      url.append(API_KEY).append(authenticationToken);
    }
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Quandl CSV format
        // Date,Open,High,Low,Close,Volume,Ex-Dividend,Split Ratio,Adj. Open,Adj. High,Adj. Low,Adj. Close,Adj. Volume
        // 2014-12-31,41.39,41.79,40.9,40.94,1421100.0,0.0,1.0,41.28820462370881,41.68722085587801,40.799409739301524,40.839311362518444,1421100.0
        // 2014-12-30,41.21,41.59,41.14,41.37,1323500.0,0.0,1.0,41.10864731923266,41.487712739793416,41.03881947860305,41.26825381210034,1323500.0
        // 2014-12-29,41.36,41.57,41.31,41.33,975900.0,0.0,1.0,41.258278406296114,41.46776192818495,41.20840137727497,41.22835218888342,975900.0
        //
        // Multi-set
        // Date(YYYY-MM-DD),WIKI.A - Open,WIKI.A - High,WIKI.A - Low,WIKI.A - Close,WIKI.A - Volume
        // 2014-10-31,55.21,55.41,54.85,55.28,2081774.0

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final int comma = findNth(COMMA, line, SIX, TEN);
        final char[] characters = new char[symbol.length() + comma - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy year
        i = getChars(line, ZERO, FOUR, characters, ++i);
        // copy month
        i = getChars(line, FIVE, SEVEN, characters, i);
        // copy rest of line
        line.getChars(EIGHT, comma, characters, i);
        final String result = String.valueOf(characters);

        return result;
      }
    };
  }

}
