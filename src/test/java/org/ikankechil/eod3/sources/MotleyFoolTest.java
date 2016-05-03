/**
 * MotleyFoolTest.java	v0.4	6 April 2015 12:53:51 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>MotleyFool</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class MotleyFoolTest extends SourceTest {

  private static final String BASE          = "http://www.motleyfool.idmanagedsolutions.com/stocks/historical_quotes.idms?&BLOCKSIZE=20000&OFFSET=0&SYMBOL_";
  private static final String START_DATE    = "&START_DATE=";
  private static final String END_DATE      = "&END_DATE=";
  private static final String US            = "US";
  private static final String UK            = "UK";
  private static final String EQUAL         = "=";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public MotleyFoolTest() throws IOException {
    exchanges.put(NYSE, US);
    exchanges.put(NASDAQ, US);
    exchanges.put(AMEX, US);
    exchanges.put(NYSEARCA, US);
    exchanges.put(LSE, UK);

    originalLines.addAll(Files.readAllLines(new File(DIRECTORY, getClass().getSimpleName() + HTML).toPath()));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.02,34.00,34.94,24900994",
                                          "INTC,20151203,34.97,34.99,34.00,34.04,30131055",
                                          "INTC,20151202,35.09,35.41,34.80,34.83,18688180",
                                          "INTC,20151201,35.00,35.20,34.71,35.09,23560239",
                                          "INTC,20151130,34.55,34.90,34.43,34.77,21785791"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE +
                   EQUAL + symbol +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   EQUAL + symbol +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE +
                   EQUAL + symbol +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
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
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   EQUAL + symbol +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
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
