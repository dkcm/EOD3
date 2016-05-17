/**
 * KdbTest.java	v0.4	29 December 2015 4:36:07 pm
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Kdb</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class KdbTest extends SourceTest {

  private static final String BASE       = "http://k-db.com/stocks/";
  private static final String START_YEAR = "&year=";
  private static final String DOWNLOAD   = "?download=csv";

  private static final String TOKYO      = "-T";
  private static final String OSAKA      = "-O";

  public KdbTest() {
    exchanges.put(TSE, TOKYO);
    exchanges.put(OSE, OSAKA);

    originalLines.addAll(Arrays.asList("6502-T,東証1部,東芝,日足",
                                       "日付,始値,高値,安値,終値,出来高,売買代金",
                                       "2015-12-29,229.0,234.8,223.1,232.1,73080000,16789443700",
                                       "2015-12-28,215.0,229.1,214.3,226.0,81249000,18182179200",
                                       "2015-12-25,222.0,225.6,216.0,216.6,93109000,20398613400",
                                       "2015-12-24,221.0,230.9,220.0,220.6,122009000,27276628700",
                                       "2015-12-22,251.9,253.5,221.0,223.5,213954000,49717132700"));

    transformedLines.addAll(Arrays.asList("INTC,20151229,229.0,234.8,223.1,232.1,73080000",
                                          "INTC,20151228,215.0,229.1,214.3,226.0,81249000",
                                          "INTC,20151225,222.0,225.6,216.0,216.6,93109000",
                                          "INTC,20151224,221.0,230.9,220.0,220.6,122009000",
                                          "INTC,20151222,251.9,253.5,221.0,223.5,213954000"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + DOWNLOAD);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return new URL(BASE + symbol +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   DOWNLOAD);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol + DOWNLOAD +
                   START_YEAR + start.get(Calendar.YEAR));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   DOWNLOAD +
                   START_YEAR + start.get(Calendar.YEAR));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end);
  }

}
