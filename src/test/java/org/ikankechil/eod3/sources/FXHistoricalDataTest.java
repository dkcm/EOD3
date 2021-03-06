/**
 * FXHistoricalDataTest.java  0.7  17 December 2014 7:17:30 PM
 *
 * Copyright © 2014-2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>FXHistoricalData</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.7
 */
public class FXHistoricalDataTest extends SourceTest {

  private static final String BASE                     = baseURL(FXHistoricalDataTest.class);
  private static final String LOOKBACK_PERIODS         = "&item_count=";
  private static final int    DEFAULT_LOOKBACK_PERIODS = 20000;
  private static final String FREQUENCY                = "&timeframe=";
  private static final String DAY                      = "day";
  private static final String WEEK                     = "week";

  private static final int    DAYS_IN_WEEK             = 7;
  private static final double WEEKDAY_PROPORTION       = (double) 5 / DAYS_IN_WEEK;

  public FXHistoricalDataTest() {
    exchanges.put(FX, EMPTY);

    originalLines.addAll(Arrays.asList("EURUSD,2016-11-25 00:00:00,1.0562,1.0542,1.0614,1.0594",
                                       "EURUSD,2016-11-24 00:00:00,1.0544,1.0524,1.0581,1.0562",
                                       "EURUSD,2016-11-23 00:00:00,1.0634,1.0531,1.0643,1.0546",
                                       "EURUSD,2016-11-22 00:00:00,1.0639,1.0585,1.0658,1.0633",
                                       "EURUSD,2016-11-21 00:00:00,1.0584,1.0584,1.0649,1.0638"));

    transformedLines.addAll(Arrays.asList("EURUSD,20161125,1.0562,1.0614,1.0542,1.0594",
                                          "EURUSD,20161124,1.0544,1.0581,1.0524,1.0562",
                                          "EURUSD,20161123,1.0634,1.0643,1.0531,1.0546",
                                          "EURUSD,20161122,1.0639,1.0658,1.0585,1.0633",
                                          "EURUSD,20161121,1.0584,1.0649,1.0584,1.0638"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null, null, Frequencies.DAILY);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, start, end, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE +
                   symbol +
                   LOOKBACK_PERIODS + computeLookbackPeriods(start, end) +
                   FREQUENCY + ((frequency == Frequencies.WEEKLY) ? WEEK : DAY));
  }

  private static final long computeLookbackPeriods(final Calendar start, final Calendar end) {
    final long lookbackPeriods;

    if (start != null && end != null) {
      final long days = TimeUnit.DAYS.convert((end.getTimeInMillis() - start.getTimeInMillis()),
                                              TimeUnit.MILLISECONDS);
      final long weekdays = (days <= DAYS_IN_WEEK) ? days
                                                   : (long) (days * WEEKDAY_PROPORTION);
      lookbackPeriods = weekdays;
    }
    else {
      lookbackPeriods = DEFAULT_LOOKBACK_PERIODS;
    }

    return lookbackPeriods;
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, start, end, frequency);
  }

}
