/**
 * Source.java	v1.8  15 December 2013 8:11:20 PM
 *
 * Copyright © 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of a data source.
 *
 * @author Daniel Kuan
 * @version 1.8
 */
public abstract class Source {
  // TODO Other potential sources
  // 1. TradingRoom
  //    http://www.tradingroom.com.au/apps/qt/csv/pricehistory.ac?section=yearly_price_download&code=QAN
  // 2. Ariva.de
  //    http://www.ariva.de/quote/historic/historic.csv?secu=120821123&boerse_id=1&clean_split=1&clean_payout=0&clean_bezug=1&min_time=2.1.2015&max_time=2.1.2016&trenner=;&go=Download
  // 3. Handelsblatt.com
  //    http://finanzen.handelsblatt.com/handelsblatt/kurse_einzelkurs_uebersicht.htn?&view=csvHistory&i=110067&von=01/04/14&bis=&i=110067&ajax=4
  // 4. fxtop.com
  //    http://fxtop.com/de/historische-wechselkurse.php?YA=1&C1=XAU&C2=EUR&A=1&YYYY1=2006&MM1=01&DD1=15&YYYY2=2016&MM2=01&DD2=15&LANG=de

  // TODO Enhancements v2.0
  // 1. support other asset classes (e.g. Futures)
  // 2. allow for multiple base URLs for each source

  private final String                     base;
  private final boolean                    isRFC2396Compliant;
  final Map<Exchanges, String>             exchanges;

  // Properties and constants
  private static final String              PROPERTIES_FILE   = "eod3.properties";
  private static final String              BASE_URL          = ".baseURL";

  private static final String              UTF_8             = "UTF-8";
  static final String                      UNSUPPORTED       = "Unsupported operation";

  // Other constants
  static final String                      EMPTY             = "";
  static final char                        SPACE             = ' ';
  static final char                        DOT               = '.';
  static final char                        COMMA             = ',';
  static final char                        COLON             = ':';
  static final char                        EQUAL             = '=';
  static final char                        LESS_THAN         = '<';
  static final char                        MORE_THAN         = '>';
  static final char                        SLASH             = '/';
  static final char                        QUESTION          = '?';
  static final char                        HYPHEN            = '-';
  static final char                        DOUBLE_QUOTE      = '"';
  static final char                        CLOSE_BRACE       = '}';
  static final char                        DOLLAR            = '$';

  // Numeric constants
  static final int                         ZERO              = 0;
  static final int                         ONE               = 1;
  static final int                         TWO               = 2;
  static final int                         THREE             = 3;
  static final int                         FOUR              = 4;
  static final int                         FIVE              = 5;
  static final int                         SIX               = 6;
  static final int                         SEVEN             = 7;
  static final int                         EIGHT             = 8;
  static final int                         NINE              = 9;
  static final int                         TEN               = 10;
  static final int                         FIFTEEN           = 15;

  static final char                        DEFAULT_FREQUENCY = Frequencies.DAILY.frequency();
  /**
   * Start date defaults to 1 January 1970 00:00:00.000 GMT.
   */
  static final Calendar                    DEFAULT_START     = Calendar.getInstance();

  private static final Map<String, String> BASE_URLS         = new HashMap<>();

  private static final Logger              logger            = LoggerFactory.getLogger(Source.class);

  static {
    DEFAULT_START.setTimeInMillis(ZERO); // 1 January 1970 00:00:00.000 GMT

    // register source base URLs
    try (final InputStream is = new FileInputStream(PROPERTIES_FILE)) {
      final Properties properties = new Properties();
      properties.load(is);

      for (final Class<? extends Source> source : Arrays.asList(FinancialContent.class,
                                                                FXHistoricalData.class,
                                                                GoogleFinance.class,
                                                                Kdb.class,
                                                                MoneyControl.class,
                                                                Morningstar.class,
                                                                MotleyFool.class,
                                                                MSNMoney.class,
                                                                Netfonds.class,
                                                                QuoteMedia.class,
                                                                StockNod.class,
                                                                Stooq.class,
                                                                WallStreetJournal.class,
                                                                YahooFinance.class)) {
        final String sourceName = source.getName();
        final String key = sourceName + BASE_URL;
        final String url = properties.getProperty(key, System.getProperty(key));

        if (url != null && !url.isEmpty()) {
          BASE_URLS.put(sourceName, url);
        }
        else {
          logger.warn("No base URL for: {}", sourceName);
        }
      }
    }
    catch (final IOException ioE) {
      logger.warn("Property file not found / loaded", ioE);
    }
  }

  public Source(final Class<? extends Source> source) {
    this(BASE_URLS.get(source.getName()));
  }

  public Source(final String base) {
    if (base.equals(EMPTY)) {
      throw new IllegalArgumentException("Empty base URL");
    }
    this.base = base;
    isRFC2396Compliant = true;
    exchanges = new EnumMap<>(Exchanges.class);
  }

  /**
   * Builds a <code>URL</code> for the specified symbol.
   *
   * @param symbol
   * @return
   * @throws MalformedURLException if no protocol is specified, or an unknown protocol is found
   */
  public URL url(final String symbol) throws MalformedURLException {
    return url(symbol, null);
  }

  public URL url(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return url(symbol, exchange, null, null, null);
  }

  public URL url(final String symbol,
                 final Calendar start,
                 final Calendar end) throws MalformedURLException {
    return url(symbol, start, end, null);
  }

  public URL url(final String symbol,
                 final Calendar start,
                 final Calendar end,
                 final Frequencies frequency) throws MalformedURLException {
    return url(symbol, null, start, end, frequency);
  }

  public URL url(final String symbol,
                 final Exchanges exchange,
                 final Calendar start,
                 final Calendar end) throws MalformedURLException {
    return url(symbol, exchange, start, end, null);
  }

  public URL url(final String symbol,
                 final Exchanges exchange,
                 final Calendar start,
                 final Calendar end,
                 final Frequencies frequency) throws MalformedURLException {
    if (symbol.isEmpty() ||
        symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Empty symbol");
    }
    final StringBuilder urlBuilder = new StringBuilder(base);
    // symbol may not be RFC 2396-compliant
    appendSymbolAndExchange(urlBuilder, symbol, exchange);

    if (start != null && end != null && start.before(end)) {
      appendStartAndEndDates(urlBuilder, start, end);
    }
    else {
      appendDefaultDates(urlBuilder, start, end);
    }
    appendFrequency(urlBuilder, frequency);
    appendSuffix(urlBuilder);

    final String url = urlBuilder.toString();
    logger.info("URL formed: {}", url);

    return new URL(url);
  }

  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url, symbol);
    appendExchange(url, exchange);
  }

  void appendSymbol(final StringBuilder url, final String symbol) {
    try {
      url.append(isRFC2396Compliant ? URLEncoder.encode(symbol, UTF_8) : symbol);
    }
    catch (final UnsupportedEncodingException ueE) {
      logger.warn("{} encoding not supported", UTF_8, ueE);
      url.append(symbol);
    }
  }

  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    final String value = exchanges.get(exchange);
    // only append non-null and non-empty exchanges
    if (value != null && !value.isEmpty()) {
      url.append(value);
    }
    else {
      logger.debug("Unsupported exchange {} requested for {}", exchange, url);
    }
  }

  void appendStartAndEndDates(final StringBuilder url,
                              final Calendar start,
                              final Calendar end) {
    appendStartDate(url, start);
    appendEndDate(url, end);
  }

  abstract void appendStartDate(final StringBuilder url, final Calendar start);

  abstract void appendEndDate(final StringBuilder url, final Calendar end);

  /**
   * Appends default start and end dates if the data source requires it and none
   * were specified.
   *
   * @param url
   * @param start
   * @param end
   */
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  abstract void appendFrequency(final StringBuilder url, final Frequencies frequency);

  /**
   * Appends a suffix (if required).
   *
   * @param url
   */
  void appendSuffix(final StringBuilder url) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  /**
   * @return supported exchanges
   */
  public Set<Exchanges> exchanges() {
    return Collections.unmodifiableSet(exchanges.keySet());
  }

  public TextReader newReader() {
    return new TextReader();
  }

  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, ONE, false);
  }

  public abstract TextTransform newTransform(final String symbol);

  public String directory() {
    return getClass().getSimpleName();
  }

}
