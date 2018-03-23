/**
 * TiingoTest.java  v0.2  15 July 2017 10:37:11 pm
 *
 * Copyright © 2017-2018 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit test for <code>Tiingo</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class TiingoTest extends SourceTest {

  private static final String BASE          = baseURL(TiingoTest.class);
  private static final String PRICES        = "/prices";
  private static final String START_DATE    = "?startDate=";
  private static final String END_DATE      = "&endDate=";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public TiingoTest() throws IOException {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);
    exchanges.put(SSE, EMPTY);
    exchanges.put(SZSE, EMPTY);

    readOriginalLinesFromJsonFile();

    transformedLines.addAll(Arrays.asList("INTC,19800319,63.5,64.5,63.5,63.5,96400",
                                          "INTC,19800318,62.5,63.0,62.0,62.0,88900",
                                          "INTC,19800317,62.5,63.5,62.5,62.5,56900"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + PRICES);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol + PRICES +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
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
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Ignore
  @Override
  @Test
  public void connectivity() throws Exception {
    // do nothing
  }

}
