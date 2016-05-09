/**
 * WallStreetJournalTest.java	v0.4	6 April 2015 12:50:58 am
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
 * JUnit test for <code>WallStreetJournal</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class WallStreetJournalTest extends SourceTest {

  // Date-related URL parameters
  private static final String BASE          = baseURL(WallStreetJournalTest.class);
  private static final String START_DATE    = "&startDate=";
  private static final String END_DATE      = "&endDate=";

  // suffix
  private static final String ROWS          = "&num_rows=32767";

  // Exchange-related constants
  private static final String EXCHANGE      = "&exchange=";
  private static final String XNYS          = "XNYS";
  private static final String XNAS          = "XNAS";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

  public WallStreetJournalTest() throws IOException {
    exchanges.put(NYSE, XNYS);
    exchanges.put(NASDAQ, XNAS);

    originalLines.addAll(Files.readAllLines(new File(DIRECTORY, getClass().getSimpleName() + HTML).toPath()));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.1100,35.0250,34.0000,34.9350,24900000",
                                          "INTC,20151203,34.9700,34.9900,34.0000,34.0400,30130000",
                                          "INTC,20151202,35.0900,35.4100,34.8050,34.8300,18690000000",
                                          "INTC,20151201,35.0000,35.2000,34.7100,35.0900,23560000000",
                                          "INTC,20151130,34.5500,34.9000,34.4300,34.7700,21790000000000",
                                          "INTC,20151127,34.5400,34.6800,34.4000,34.4600,6620000000000"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()) +
                   END_DATE + urlDateFormat.format(TODAY.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   (exchanges.containsKey(exchange)? EXCHANGE + exchanges.get(exchange) : EMPTY) +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()) +
                   END_DATE + urlDateFormat.format(TODAY.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   (exchanges.containsKey(exchange)? EXCHANGE + exchanges.get(exchange) : EMPTY) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end);
  }

}
