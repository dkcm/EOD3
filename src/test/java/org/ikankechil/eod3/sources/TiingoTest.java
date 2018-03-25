/**
 * TiingoTest.java  v0.3  15 July 2017 10:37:11 pm
 *
 * Copyright © 2017-2018 Daniel Kuan.  All rights reserved.
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
import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit test for <code>Tiingo</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class TiingoTest extends SourceTest {

  private static final String BASE          = baseURL(TiingoTest.class);
  private static final String PRICES        = "/prices";
  private static final String START_DATE    = "?startDate=";
  private static final String END_DATE      = "&endDate=";
  private static final String FREQUENCY     = "&resampleFreq=";
  private static final String FORMAT        = "&format=csv";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public TiingoTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);
    exchanges.put(SSE, EMPTY);
    exchanges.put(SZSE, EMPTY);

//    readOriginalLinesFromJsonFile();
    originalLines.addAll(Arrays.asList("date,close,high,low,open,volume,adjClose,adjHigh,adjLow,adjOpen,adjVolume,divCash,splitFactor",
                                       "2017-12-01,44.68,44.84,43.53,44.73,26640551,44.383516921,44.542455209,43.2411479761,44.433185136,26640551,0.0,1.0",
                                       "2017-12-04,44.49,45.3,44.33,45.02,26829460,44.194777704,44.999402787,44.0358394161,44.721260783,26829460,0.0,1.0",
                                       "2017-12-05,43.44,44.9,43.23,44.6,30112131,43.1517451891,44.602057067,42.9431386861,44.304047777,30112131,0.0,1.0",
                                       "2017-12-06,43.45,43.72,42.67,43.14,24497550,43.1616788321,43.4298871931,42.3868546782,42.8537358991,24497550,0.0,1.0",
                                       "2017-12-07,43.08,43.6,42.78,43.46,31193561,42.7941340411,43.3106834771,42.4961247512,43.1716124751,31193561,0.0,1.0"));

    transformedLines.addAll(Arrays.asList("INTC,20171207,43.46,43.6,42.78,43.08,31193561",
                                          "INTC,20171206,43.14,43.72,42.67,43.45,24497550",
                                          "INTC,20171205,44.6,44.9,43.23,43.44,30112131",
                                          "INTC,20171204,45.02,45.3,44.33,44.49,26829460",
                                          "INTC,20171201,44.73,44.84,43.53,44.68,26640551"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + PRICES +
                   FREQUENCY + Frequencies.DAILY +
                   FORMAT);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol);
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
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, null, start, end, frequency);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + symbol + PRICES +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + ((frequency != null) ? frequency : Frequencies.DAILY) +
                   FORMAT);
  }

  @Ignore
  @Override
  @Test
  public void connectivity() throws Exception {
    // do nothing
  }

}
