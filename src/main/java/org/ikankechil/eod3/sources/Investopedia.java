/**
 * Investopedia.java  v0.1  12 January 2016 9:56:23 pm
 *
 * Copyright � 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Investopedia.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class Investopedia extends Source {

  private final DateFormat    dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

  // Date-related URL parameters
  private static final String START_DATE = "&StartDate=";
  private static final String END_DATE   = "&EndDate=";
  private static final String FREQUENCY  = "&HistoryType=";

  // Exchange-related constants
  private static final String STOCKS     = "stocks";
  private static final String ETFS       = "etfs";

  // Other constants
  private static final String HISTORICAL = "historical/?";

  private static final Logger logger     = LoggerFactory.getLogger(Investopedia.class);

  public Investopedia() {
    super("http://www.investopedia.com/markets/");

    // supported markets
    exchanges.put(NYSE, STOCKS);
    exchanges.put(NASDAQ, STOCKS);
    exchanges.put(AMEX, STOCKS);
    exchanges.put(ARCA, ETFS);

    // Investopedia API
    // e.g.
    // http://www.investopedia.com/markets/etfs/sco/historical/?page=16&StartDate=01/01/2010&EndDate=01/12/2016&HistoryType=Daily
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    // prefix exchange
    appendExchange(url, exchange);
    url.append(SLASH);
    appendSymbol(url, symbol);
    url.append(HISTORICAL);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &StartDate=01/01/2010
    url.append(START_DATE).append(dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // &EndDate=01/12/2016
    url.append(END_DATE).append(dateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // &HistoryType=Daily
    url.append(FREQUENCY).append(frequency);
    logger.debug("Frequency: {}", url);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    // TODO Auto-generated method stub
    return null;
  }

}
