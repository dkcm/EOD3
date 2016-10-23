/**
 * PortfolioTest.java  v0.2  28 July 2016 1:06:23 am
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
 * JUnit test for <code>Portfolio</code>.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class PortfolioTest extends SourceTest {

  private static final String BASE = baseURL(PortfolioTest.class);

  public PortfolioTest() {
    exchanges.put(BET, EMPTY);

    originalLines.addAll(Arrays.asList("Részvény: OTP",
                                       "",
                                       "Dátum\tNyitó\tZáró\tMinimum\tMaximum\tForgalom (db)",
                                       "2016-07-21\t6780.0000\t6849.0000\t6760.0000\t6866.0000\t785786",
                                       "2016-07-22\t6851.0000\t6843.0000\t6736.0000\t6869.0000\t556572",
                                       "2016-07-25\t6840.0000\t6857.0000\t6816.0000\t6900.0000\t408962",
                                       "2016-07-26\t6857.0000\t6836.0000\t6790.0000\t6869.0000\t402995",
                                       "2016-07-27\t6850.0000\t6722.0000\t6711.0000\t6855.0000\t375554"));

    transformedLines.addAll(Arrays.asList("INTC,20160727,6850.0000,6855.0000,6711.0000,6722.0000,375554",
                                          "INTC,20160726,6857.0000,6869.0000,6790.0000,6836.0000,402995",
                                          "INTC,20160725,6840.0000,6900.0000,6816.0000,6857.0000,408962",
                                          "INTC,20160722,6851.0000,6869.0000,6736.0000,6843.0000,556572",
                                          "INTC,20160721,6780.0000,6866.0000,6760.0000,6849.0000,785786"));
  }

  @Override
  protected URL expectedURL(final String symbol)
      throws MalformedURLException {
    return new URL(BASE + symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
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
