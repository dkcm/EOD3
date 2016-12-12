/**
 * RavaTest.java  v0.1  29 October 2016 11:09:10 pm
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Rava</code>.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class RavaTest extends SourceTest {

  private static final String BASE = baseURL(RavaTest.class);

  public RavaTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(ARCA, EMPTY);
    exchanges.put(BCBA, EMPTY);

    originalLines.addAll(Arrays.asList("fecha,apertura,maximo,minimo,cierre,volumen,openint",
                                       "\"2005-01-03\",\"4.62693\",\"4.6505\",\"4.47122\",\"4.52050\",\"173362080\",\"0\"",
                                       "\"2005-01-04\",\"4.55622\",\"4.67621\",\"4.49765\",\"4.56693\",\"274528096\",\"0\"",
                                       "\"2005-01-05\",\"4.59621\",\"4.6605\",\"4.57479\",\"4.60693\",\"170218176\",\"0\"",
                                       "\"2005-01-06\",\"4.62014\",\"4.63621\",\"4.52336\",\"4.61050\",\"176477696\",\"0\"",
                                       "\"2005-01-07\",\"4.64264\",\"4.97334\",\"4.62478\",\"4.94620\",\"558958720\",\"0\"",
                                       "\"2005-01-10\",\"4.99263\",\"5.04977\",\"4.84835\",\"4.92549\",\"434279040\",\"0\""));

    transformedLines.addAll(Arrays.asList("INTC,20050110,4.99263,5.04977,4.84835,4.92549,434279040",
                                          "INTC,20050107,4.64264,4.97334,4.62478,4.94620,558958720",
                                          "INTC,20050106,4.62014,4.63621,4.52336,4.61050,176477696",
                                          "INTC,20050105,4.59621,4.6605,4.57479,4.60693,170218176",
                                          "INTC,20050104,4.55622,4.67621,4.49765,4.56693,274528096",
                                          "INTC,20050103,4.62693,4.6505,4.47122,4.52050,173362080"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
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
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol);
  }

}
