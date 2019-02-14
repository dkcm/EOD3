/**
 * InvestopediaTest.java  v0.1  10 February 2019 9:28:11 PM
 *
 * Copyright © 2019 Daniel Kuan.  All rights reserved.
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
 * JUnit test for <code>Investopedia</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class InvestopediaTest extends SourceTest {

  private final DateFormat      dateFormat         = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

  // Date-related URL parameters
  private static final String   BASE               = baseURL(InvestopediaTest.class);
  private static final String   START_DATE         = "&StartDate=";
  private static final String   END_DATE           = "&EndDate=";
  private static final String   FREQUENCY          = "&Timeframe=";

  private static final Calendar DEFAULT_START_DATE = Calendar.getInstance();
  
  static {
    DEFAULT_START_DATE.set(2010, Calendar.SEPTEMBER, 1);
  }

  public InvestopediaTest() throws IOException {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);

    originalLines.addAll(Files.readAllLines(new File(DIRECTORY, getClass().getSimpleName() + HTML).toPath()));

    transformedLines.addAll(Arrays.asList("INTC,20190208,48.77,49.02,48.01,48.84,18851208",
                                          "INTC,20190207,49.50,49.85,48.68,49.23,27825875",
                                          "INTC,20190206,48.77,49.02,48.14,48.21,6655944",
                                          "INTC,20190205,48.87,50.40,48.87,49.70,31641570",
                                          "INTC,20190204,48.31,48.93,47.94,48.91,20905010"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, DEFAULT_START_DATE, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end) throws MalformedURLException {
    return expectedURL(symbol, start, end, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end, final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, null, start, end, frequency);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange, final Calendar start, final Calendar end, final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   START_DATE + dateFormat.format(start.getTime()) +
                   (end != null ? END_DATE + dateFormat.format(end.getTime()) : EMPTY) +
                   (frequency != null ? FREQUENCY + timeframe(frequency) : EMPTY));
  }

  private static String timeframe(final Frequencies frequency) {
    final String f = frequency.toString();
    return f.charAt(0) + f.substring(1).toLowerCase();
  }

}
