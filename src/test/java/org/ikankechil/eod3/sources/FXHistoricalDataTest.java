/**
 * FXHistoricalDataTest.java  v0.5  17 December 2014 7:17:30 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.ZipTextReader;
import org.junit.Test;

/**
 * JUnit test for <code>FXHistoricalData</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.5
 */
public class FXHistoricalDataTest extends SourceTest {

  private static final String BASE      = baseURL(FXHistoricalDataTest.class);
  private static final String FREQUENCY = "_day.zip";

  public FXHistoricalDataTest() {
    exchanges.put(FX, EMPTY);

    originalLines.addAll(Arrays.asList("<TICKER>,<DATE>,<TIME>,<OPEN>,<LOW>,<HIGH>,<CLOSE>",
                                       "EURUSD,20010103,00:00:00,0.9507,0.9262,0.9569,0.9271",
                                       "EURUSD,20010104,00:00:00,0.9271,0.9269,0.9515,0.9507",
                                       "EURUSD,20010105,00:00:00,0.9507,0.9464,0.9591,0.9575",
                                       "EURUSD,20010108,00:00:00,0.9583,0.9462,0.9588,0.9467",
                                       "EURUSD,20010109,00:00:00,0.9467,0.9384,0.9477,0.9437"));

    transformedLines.addAll(Arrays.asList("EURUSD,20010109,0.9467,0.9477,0.9384,0.9437",
                                          "EURUSD,20010108,0.9583,0.9588,0.9462,0.9467",
                                          "EURUSD,20010105,0.9507,0.9591,0.9464,0.9575",
                                          "EURUSD,20010104,0.9271,0.9515,0.9269,0.9507",
                                          "EURUSD,20010103,0.9507,0.9569,0.9262,0.9271"));
  }

  @Override@Test
  public void reader() {
    assertTrue(source.newReader() instanceof ZipTextReader);
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + FREQUENCY);
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
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol);
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
