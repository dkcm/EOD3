/**
 * GlobalViewTest.java  v0.1  11 October 2016 11:20:47 pm
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
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
 * JUnit test for <code>GlobalView</code>.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class GlobalViewTest extends SourceTest {

  private static final String      BASE              = baseURL(GlobalViewTest.class);
  private static final String      START_DATE        = "&start_date=";
  private static final String      END_DATE          = "&stop_date=";
  private static final String      FREQUENCY         = "&Submit=Get %s Stats";

  private static final Frequencies DEFAULT_FREQUENCY = Frequencies.DAILY;
  private static final String      EURUSD            = "CLOSE_1=ON&HIGH_1=ON&LOW_1=ON";

  private final DateFormat         urlDateFormat     = new SimpleDateFormat("M/d/yyyy", Locale.US);

  public GlobalViewTest() {
    exchanges.put(FX, EMPTY);

    originalLines.addAll(Arrays.asList("Results,NZD/USD Close,NZD/USD High,NZD/USD Low",
                                       "Average,  0.429,  0.431,  0.426",
                                       "Minimum,   0.000,   0.000,   0.000",
                                       "Maximum,   0.882,   0.884,   0.879",
                                       "Std,   0.371,   0.373,   0.369",
                                       "# In Calc, 4638, 4638, 4638",
                                       "",
                                       "Date,NZD/USD Close,NZD/USD High,NZD/USD Low",
                                       "2006-07-03,0,0,0",
                                       "2006-07-04,0.61,0.6115,0.6056",
                                       "2006-07-05,0.6041,0.6119,0.6028",
                                       "2006-07-06,0.60470,0.60640,0.60210",
                                       "2006-07-13,0.6184,0.62,0.6145",
                                       "2006-07-14,0.6206,0.6214,0.6146",
                                       "2006-07-17,0.621,0.6226,0.6185"));

    transformedLines.addAll(Arrays.asList("INTC,20060717,,0.6226,0.6185,0.621",
                                          "INTC,20060714,,0.6214,0.6146,0.6206",
                                          "INTC,20060713,,0.62,0.6145,0.6184",
                                          "INTC,20060706,,0.60640,0.60210,0.60470",
                                          "INTC,20060705,,0.6119,0.6028,0.6041",
                                          "INTC,20060704,,0.6115,0.6056,0.61",
                                          "INTC,20060703,,0,0,0"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol, DEFAULT_START, TODAY);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, start, end, DEFAULT_FREQUENCY);
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
    return expectedURL(symbol, exchange, start, end, DEFAULT_FREQUENCY);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    final StringBuilder f = new StringBuilder(frequency.toString().toLowerCase());
    f.setCharAt(0, Character.toUpperCase(frequency.frequency()));

    return new URL(BASE + ((FX_SYMBOL == symbol) ? EURUSD : EMPTY) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   String.format(FREQUENCY, f));
  }

}
