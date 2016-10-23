/**
 * Quandl.java  v0.7  3 November 2014 4:51:49 PM
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
 *
 * <p>https://www.quandl.com/docs/api
 *
 * <p>Authentication token can be specified by the System property:
 * org.ikankechil.eod3.sources.Quandl.apiKey
 *
 * <p>Download volume restrictions apply: Authenticated users have a limit of 2000
 * calls per 10 minutes, and a limit of 50000 calls per day.
 *
 * @author Daniel Kuan
 * @version 0.7
 */
public class Quandl extends Source {
  // TODO extend support for asset classes other than Equities

  private final String        authenticationToken;
  private final API           urlQueryKeys;
  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  // Authentication token / API key property: org.ikankechil.eod3.sources.Quandl.apiKey
  private static final String AUTH_TOKEN    = System.getProperty(Quandl.class.getName() + ".apiKey");

  // other URL parameters
  private static final String CSV           = ".csv";

  // Exchange-related constants
  private static final String WIKI          = "WIKI/";   // U.S. Equities
  private static final String FSE           = "FSE/";
  private static final String _X            = "_X";
  private static final String EURONEXT      = "EURONEXT/";
  private static final String BSE_BOM       = "BSE/BOM";
//  private static final String CURRFX        = "CURRFX"; // FX

  private static final Logger logger        = LoggerFactory.getLogger(Quandl.class);

  public Quandl() {
    this(null);
  }

  public Quandl(final String authenticationToken) {
    this(authenticationToken, API.V3);
  }

  public Quandl(final String authenticationToken, final API urlQueryKeys) {
    super(urlQueryKeys.base);

    this.authenticationToken = (authenticationToken == null || authenticationToken.isEmpty()) ?
                               AUTH_TOKEN :
                               authenticationToken;
    this.urlQueryKeys = urlQueryKeys;

    // supported markets (see https://www.quandl.com/blog/api-for-global-stock-data)
    for (final Exchanges exchange : new Exchanges[] { NYSE, NASDAQ, AMEX }) {
      exchanges.put(exchange, WIKI);
    }
    for (final Exchanges exchange : new Exchanges[] { PAR, AEX, BB, BVLP }) {
      exchanges.put(exchange, EURONEXT);
    }
    for (final Exchanges exchange : new Exchanges[] { LSE, TSE, HKEX, NSE }) {
      exchanges.put(exchange, exchange.toString() + SLASH);
    }

    exchanges.put(FWB, FSE);
    exchanges.put(BSE, BSE_BOM);
//    exchanges.put(FX, CURRFX);
    // TODO FX has a different format from equities (i.e. Date,Rate,High (est),Low (est))
    // either maintain a table of FX symbols or overload newTransform() with exchange

    // e.g. https://www.quandl.com/api/v3/datasets/WIKI/FB/data.csv?column_index=4&exclude_column_names=true&rows=3&start_date=2012-11-01&end_date=2013-11-30&order=asc&collapse=quarterly&transform=rdiff
    //      http://www.quandl.com/api/v1/datasets/WIKI/AAPL.csv?sort_order=asc&exclude_headers=true&rows=3&trim_start=2012-11-01&trim_end=2013-11-30&column=4&collapse=quarterly&transformation=rdiff
    //      http://www.quandl.com/api/v1/datasets/WIKI/A.csv?&trim_start=1999-01-01&trim_end=2014-12-31&sort_order=desc&exclude_headers=false&transformation=none
    //      [Multi-sets deprecated] https://quandl.com/api/v1/multisets.csv?columns=FRED.GDP.1,DOE.RWTC.1,WIKI.AAPL.4&collapse=annual&transformation=rdiff&rows=10
  }

  static enum API {
    // https://www.quandl.com/help/api_v1
    V1("http://www.quandl.com/api/v1/datasets/",
       "&trim_start=",
       "&trim_end=",
       "&auth_token=",
       "&sort_order=desc&exclude_headers=false&transformation=none"),
    // https://www.quandl.com/docs/api#customize-your-dataset, API version: 2015-04-09
    V3("https://www.quandl.com/api/v3/datasets/",
       "&start_date=",
       "&end_date=",
       "&api_key=",
       "&order=desc&exclude_column_names=false&transform=none");

    final String base;
    final String startDate;
    final String endDate;
    final String frequency = "&collapse=";
    final String apiKey;
    final String suffix;

    API(final String base, final String startDate, final String endDate, final String apiKey, final String suffix) {
      this.base = base;
      this.startDate = startDate;
      this.endDate = endDate;
      this.apiKey = apiKey;
      this.suffix = suffix;
    }

  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    // Quandl code format: <database_code>/<dataset_code>
    appendExchange(url, exchange);
    appendSymbol(url, symbol);
    if (exchange == FWB) {
      url.append(_X);
    }

    // e.g. https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?
    url.append(CSV).append(QUESTION);
    logger.debug("Symbol and exchange: {}", url);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(urlQueryKeys.startDate).append(urlDateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(urlQueryKeys.endDate).append(urlDateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null && frequency != Frequencies.DAILY) {
      url.append(urlQueryKeys.frequency).append(frequency.toString().toLowerCase());
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    url.append(urlQueryKeys.suffix);
    appendApiKey(url);
  }

  private final void appendApiKey(final StringBuilder url) {
    if (authenticationToken != null && !authenticationToken.isEmpty()) {
      url.append(urlQueryKeys.apiKey).append(authenticationToken);
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
