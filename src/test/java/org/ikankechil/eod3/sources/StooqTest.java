/**
 * StooqTest.java	v0.6	5 April 2015 9:45:19 pm
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
 * JUnit test for <code>Stooq</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class StooqTest extends SourceTest {

  private static final String BASE          = baseURL(StooqTest.class);
  private static final String START_DATE    = "&d1=";
  private static final String END_DATE      = "&d2=";
  private static final String FREQUENCY     = "&i=";

  private static final String US            = ".us";
  private static final String UK            = ".uk";
  private static final String DE            = ".de";
  private static final String HK            = ".hk";
  private static final String JP            = ".jp";
  private static final String HU            = ".hu";
//  private static final String IT            = ".it";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);

  public StooqTest() {
    exchanges.put(NYSE, US);
    exchanges.put(NASDAQ, US);
    exchanges.put(AMEX, US);
    exchanges.put(NYSEARCA, US);
    exchanges.put(LSE, UK);
    exchanges.put(FWB, DE);
//    exchanges.put(MIB, IT);
    exchanges.put(HKSE, HK);
    exchanges.put(TSE, JP);
    exchanges.put(BET, HU);
    exchanges.put(GPW, EMPTY);
    exchanges.put(FX, EMPTY);

    originalLines.addAll(Arrays.asList("Date,Open,High,Low,Close,Volume",
                                       "2015-11-30,34.55,34.9,34.43,34.77,21785700",
                                       "2015-12-01,35,35.2,34.71,35.09,23560241",
                                       "2015-12-02,35.09,35.41,34.81,34.83,18687639",
                                       "2015-12-03,34.97,34.99,34,34.04,30131031",
                                       "2015-12-04,34.11,35.03,34,34.94,24900900"));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.03,34,34.94,24900900",
                                          "INTC,20151203,34.97,34.99,34,34.04,30131031",
                                          "INTC,20151202,35.09,35.41,34.81,34.83,18687639",
                                          "INTC,20151201,35,35.2,34.71,35.09,23560241",
                                          "INTC,20151130,34.55,34.9,34.43,34.77,21785700"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE + symbol + (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY));
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE + symbol +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + frequency.frequency());
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE + symbol +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + frequency.frequency());
  }

}
