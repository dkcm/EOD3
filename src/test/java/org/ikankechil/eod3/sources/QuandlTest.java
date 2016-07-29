/**
 * QuandlTest.java	v0.6	5 April, 2015 11:25:06 pm
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Quandl</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class QuandlTest extends SourceTest {

  // https://www.quandl.com/api/v3/datasets/WIKI/FB/data.csv?column_index=4&exclude_column_names=true&rows=3&start_date=2012-11-01&end_date=2013-11-30&order=asc&collapse=quarterly&transform=rdiff

  // v3
  private static final String BASE          = baseURL(QuandlTest.class);
  private static final String START_DATE    = "&start_date=";
  private static final String END_DATE      = "&end_date=";
  private static final String FREQUENCY     = "&collapse=";
  private static final String SUFFIX        = "&order=desc&exclude_column_names=false&transform=none";

  // v1 deprecated
//  private static final String BASE          = "http://www.quandl.com/api/v1/datasets/";
//  private static final String START_DATE    = "&trim_start=";
//  private static final String END_DATE      = "&trim_end=";
//  private static final String FREQUENCY     = "&collapse=";
//  private static final String SUFFIX        = "&sort_order=desc&exclude_headers=false&transformation=none";

  private static final String WIKI          = "WIKI/%s";
  private static final String LSE_          = "LSE/%s";
  private static final String FSE           = "FSE/%s_X";
  private static final String EURONEXT      = "EURONEXT/%s";
  private static final String HKEX          = "HKEX/%s";
  private static final String TSE_          = "TSE/%s";
  private static final String BSE_BOM       = "BSE/BOM%s";
  private static final String NSE_          = "NSE/%s";

  private static final String CSV           = ".csv?";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public QuandlTest() {
    exchanges.put(NYSE, WIKI);
    exchanges.put(NASDAQ, WIKI);
    exchanges.put(AMEX, WIKI);
    exchanges.put(LSE, LSE_);
    exchanges.put(FWB, FSE);
    exchanges.put(PAR, EURONEXT);
    exchanges.put(AMS, EURONEXT);
    exchanges.put(BB, EURONEXT);
    exchanges.put(BVLP, EURONEXT);
    exchanges.put(HKSE, HKEX);
    exchanges.put(TSE, TSE_);
    exchanges.put(BSE, BSE_BOM);
    exchanges.put(NSE, NSE_);

    originalLines.addAll(Arrays.asList("Date,Open,High,Low,Close,Volume,Ex-Dividend,Split Ratio,Adj. Open,Adj. High,Adj. Low,Adj. Close,Adj. Volume",
                                       "2015-12-04,34.11,35.025,34.0,34.935,24261345.0,0.0,1.0,34.11,35.025,34.0,34.935,24261345.0",
                                       "2015-12-03,34.97,34.99,34.0,34.04,29486331.0,0.0,1.0,34.97,34.99,34.0,34.04,29486331.0",
                                       "2015-12-02,35.09,35.41,34.805,34.841,18158381.0,0.0,1.0,35.09,35.41,34.805,34.841,18158381.0",
                                       "2015-12-01,35.0,35.2,34.71,35.09,22957189.0,0.0,1.0,35.0,35.2,34.71,35.09,22957189.0",
                                       "2015-11-30,34.55,34.9,34.43,34.77,19570154.0,0.0,1.0,34.55,34.9,34.43,34.77,19570154.0"));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.025,34.0,34.935,24261345.0",
                                          "INTC,20151203,34.97,34.99,34.0,34.04,29486331.0",
                                          "INTC,20151202,35.09,35.41,34.805,34.841,18158381.0",
                                          "INTC,20151201,35.0,35.2,34.71,35.09,22957189.0",
                                          "INTC,20151130,34.55,34.9,34.43,34.77,19570154.0"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? String.format(exchanges.get(exchange), symbol) : symbol) +
                   CSV + SUFFIX);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, start, end, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, null, start, end, frequency);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? String.format(exchanges.get(exchange), symbol) : symbol) +
                   CSV +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   (frequency != null ? FREQUENCY + frequency.toString().toLowerCase() : EMPTY) +
                   SUFFIX);
  }

}
