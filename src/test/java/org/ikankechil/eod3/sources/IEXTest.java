/**
 * IEXTest.java  v0.1  25 February 2018 9:55:10 PM
 *
 * Copyright © 2018 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>IEX</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class IEXTest extends SourceTest {

  private static final String BASE   = baseURL(IEXTest.class);
  private static final String SUFFIX = "/chart/5y?filter=date,open,high,low,close,volume&format=csv";

  public IEXTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);

    originalLines.addAll(Arrays.asList("date,open,high,low,close,volume",
                                       "2013-02-25,64.8356,65.0171,63.2242,63.2571,92899597",
                                       "2013-02-26,63.4028,64.5056,62.5228,64.1385,125096657",
                                       "2013-02-27,64.0614,64.6342,62.9499,63.5099,146674682",
                                       "2013-02-28,63.4357,63.9814,63.0571,63.0571,80532382",
                                       "2013-03-01,62.5714,62.5971,61.4257,61.4957,137899041",
                                       "2013-03-04,61.1142,61.1714,59.8571,60.0071,145406366",
                                       "2013-03-05,60.2114,62.1699,60.1071,61.5919,159298020",
                                       "2013-03-06,62.0728,62.1785,60.6328,60.8088,114903180",
                                       "2013-03-07,60.6428,61.7157,60.1514,61.5117,116992841",
                                       "2013-03-08,61.3999,62.2042,61.2299,61.6742,97854442"));

    transformedLines.addAll(Arrays.asList("INTC,20130308,61.3999,62.2042,61.2299,61.6742,97854442",
                                          "INTC,20130307,60.6428,61.7157,60.1514,61.5117,116992841",
                                          "INTC,20130306,62.0728,62.1785,60.6328,60.8088,114903180",
                                          "INTC,20130305,60.2114,62.1699,60.1071,61.5919,159298020",
                                          "INTC,20130304,61.1142,61.1714,59.8571,60.0071,145406366",
                                          "INTC,20130301,62.5714,62.5971,61.4257,61.4957,137899041",
                                          "INTC,20130228,63.4357,63.9814,63.0571,63.0571,80532382",
                                          "INTC,20130227,64.0614,64.6342,62.9499,63.5099,146674682",
                                          "INTC,20130226,63.4028,64.5056,62.5228,64.1385,125096657",
                                          "INTC,20130225,64.8356,65.0171,63.2242,63.2571,92899597"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, null, null, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, null, null, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, null, null, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + symbol + SUFFIX);
  }

}
