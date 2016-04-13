/**
 * QuandlTest.java	v0.2	5 April, 2015 11:25:06 pm
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
 * @version 0.2
 */
public class QuandlTest extends SourceTest {

  private static final String BASE          = "http://www.quandl.com/api/v1/datasets/";
  private static final String START_DATE    = "&trim_start=";
  private static final String END_DATE      = "&trim_end=";
  private static final String FREQUENCY     = "&collapse=";
  private static final String SUFFIX        = "&sort_order=desc&exclude_headers=false&transformation=none";

  private static final String WIKI          = "WIKI";
  private static final char   SLASH         = '/';
  private static final String CSV           = ".csv?";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public QuandlTest() {
    exchanges.put(NYSE, WIKI);
    exchanges.put(NASDAQ, WIKI);
    exchanges.put(AMEX, WIKI);
    exchanges.put(LSE, LSE.toString());

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
    return new URL(BASE + SLASH + symbol + CSV + SUFFIX);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   SLASH + symbol + CSV + SUFFIX);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + SLASH + symbol + CSV +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   SUFFIX);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE + SLASH + symbol + CSV +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + frequency.toString().toLowerCase() +
                   SUFFIX);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   SLASH + symbol + CSV +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   SUFFIX);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   SLASH + symbol + CSV +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + frequency.toString().toLowerCase() +
                   SUFFIX);
  }

}
