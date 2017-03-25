/**
 * AlphaVantageTest.java  v0.1  23 March 2017 5:09:27 pm
 *
 * Copyright © 2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>AlphaVantage</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class AlphaVantageTest extends SourceTest {

  private static final String BASE      = baseURL(AlphaVantageTest.class);
  private static final String FREQUENCY = "&function=TIME_SERIES_";
  private static final String API_KEY   = "&apikey=demo";

  public AlphaVantageTest() throws IOException {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);

    originalLines.addAll(Files.readAllLines(new File(DIRECTORY, getClass().getSimpleName() + JSON).toPath()));

    transformedLines.addAll(Arrays.asList("INTC,20170324,35.65,35.73,35.12,35.16,22030600",
                                          "INTC,20170323,35.49,35.49,35.02,35.27,20529600",
                                          "INTC,20170322,35.22,35.46,35.00,35.37,18704600",
                                          "INTC,20170321,35.59,35.60,35.00,35.04,22513700",
                                          "INTC,20170320,35.37,35.63,35.26,35.43,16686200",
                                          "INTC,20170317,35.31,35.33,35.10,35.27,30446300",
                                          "INTC,20170316,35.17,35.36,35.07,35.14,19377700",
                                          "INTC,20170315,34.94,35.17,34.68,35.10,27964300",
                                          "INTC,20170314,35.06,35.23,34.66,35.18,28538100",
                                          "INTC,20170313,35.85,36.04,34.94,35.16,52088500",
                                          "INTC,20170310,36.12,36.30,35.78,35.91,24626400",
                                          "INTC,20170309,35.60,35.94,35.50,35.82,23543100",
                                          "INTC,20170308,35.67,35.90,35.53,35.62,20899400",
                                          "INTC,20170307,35.54,35.80,35.39,35.80,23337700",
                                          "INTC,20170306,35.79,35.80,35.42,35.57,24280300",
                                          "INTC,20170303,35.94,36.09,35.67,35.90,17538100",
                                          "INTC,20170302,35.93,36.02,35.65,35.91,23591200",
                                          "INTC,20170301,35.85,36.10,35.44,35.93,33969900",
                                          "INTC,20170228,36.42,36.46,36.00,36.20,24793100",
                                          "INTC,20170227,36.42,36.68,36.27,36.51,14063400",
                                          "INTC,20170224,36.14,36.53,35.95,36.53,18515100",
                                          "INTC,20170223,36.24,36.30,35.90,36.18,22509000",
                                          "INTC,20170222,36.34,36.40,35.79,36.07,29868400",
                                          "INTC,20170221,36.53,36.65,36.33,36.52,20542400",
                                          "INTC,20170217,36.32,36.48,36.10,36.48,18712800",
                                          "INTC,20170216,36.18,36.56,36.10,36.41,21181700",
                                          "INTC,20170215,35.98,36.06,35.67,36.05,18364200",
                                          "INTC,20170214,35.84,35.96,35.53,35.93,25725100",
                                          "INTC,20170213,35.57,35.85,35.34,35.80,28180600",
                                          "INTC,20170210,35.26,35.43,34.84,35.34,44106500",
                                          "INTC,20170209,36.50,36.50,35.40,35.46,47802700",
                                          "INTC,20170208,36.34,36.65,36.14,36.38,20807400",
                                          "INTC,20170207,36.45,36.52,36.24,36.35,18581900",
                                          "INTC,20170206,36.51,36.51,36.15,36.27,21427000",
                                          "INTC,20170203,36.69,36.82,36.45,36.52,21038100",
                                          "INTC,20170202,36.26,36.70,36.20,36.68,31916900",
                                          "INTC,20170201,36.82,36.95,36.02,36.52,28380600",
                                          "INTC,20170131,37.22,37.37,36.64,36.82,27059100",
                                          "INTC,20170130,37.74,37.84,37.33,37.42,23954700",
                                          "INTC,20170127,38.00,38.45,37.81,37.98,44368600",
                                          "INTC,20170126,37.82,37.92,37.42,37.56,34144800",
                                          "INTC,20170125,37.87,38.00,37.52,37.80,32276400",
                                          "INTC,20170124,36.84,37.74,36.81,37.62,35140900",
                                          "INTC,20170123,37.07,37.21,36.55,36.77,27752600",
                                          "INTC,20170120,36.76,37.03,36.58,36.94,23950900",
                                          "INTC,20170119,36.51,36.91,36.51,36.57,13654500",
                                          "INTC,20170118,36.89,37.01,36.61,36.76,15382500",
                                          "INTC,20170117,36.67,36.83,36.59,36.80,20195200",
                                          "INTC,20170113,36.71,36.86,36.62,36.79,15072200",
                                          "INTC,20170112,36.83,36.83,36.32,36.71,20391900",
                                          "INTC,20170111,36.51,37.00,36.51,36.95,22398400",
                                          "INTC,20170110,36.55,36.93,36.53,36.54,15918800",
                                          "INTC,20170109,36.48,36.89,36.48,36.61,19449400",
                                          "INTC,20170106,36.59,36.68,36.19,36.48,15114000",
                                          "INTC,20170105,36.45,36.72,36.31,36.35,13986000",
                                          "INTC,20170104,36.71,36.77,36.34,36.41,15915700",
                                          "INTC,20170103,36.61,36.93,36.27,36.60,20196500",
                                          "INTC,20161230,36.79,36.80,36.20,36.27,17468000",
                                          "INTC,20161229,36.51,36.72,36.43,36.66,8447300",
                                          "INTC,20161228,37.10,37.20,36.58,36.63,12868600",
                                          "INTC,20161227,36.91,37.33,36.91,37.07,9033700",
                                          "INTC,20161223,36.93,36.98,36.77,36.97,6287300",
                                          "INTC,20161222,37.23,37.24,36.81,36.93,10793900",
                                          "INTC,20161221,37.25,37.35,36.91,36.98,14323600",
                                          "INTC,20161220,36.95,37.24,36.89,37.21,18551800",
                                          "INTC,20161219,36.34,36.95,36.27,36.89,18744300",
                                          "INTC,20161216,36.72,36.87,36.09,36.31,38212800",
                                          "INTC,20161215,36.74,37.16,36.53,36.79,23721500",
                                          "INTC,20161214,36.70,36.89,36.46,36.55,30819400",
                                          "INTC,20161213,36.01,36.89,35.93,36.80,35773600",
                                          "INTC,20161212,35.78,36.09,35.67,35.97,21985800",
                                          "INTC,20161209,35.75,35.90,35.59,35.76,16326900",
                                          "INTC,20161208,35.48,36.03,35.22,35.70,19966500",
                                          "INTC,20161207,34.67,35.57,34.45,35.50,21710300",
                                          "INTC,20161206,34.39,34.75,34.30,34.72,15297700",
                                          "INTC,20161205,34.31,34.42,33.97,34.39,14462000",
                                          "INTC,20161202,33.72,34.26,33.60,34.16,21492500",
                                          "INTC,20161201,34.86,34.93,33.56,33.76,29618700",
                                          "INTC,20161130,35.20,35.30,34.70,34.70,27016100",
                                          "INTC,20161129,35.64,35.64,35.27,35.31,19581000",
                                          "INTC,20161128,35.43,35.66,35.21,35.51,13549000",
                                          "INTC,20161125,35.11,35.45,35.11,35.44,6372800",
                                          "INTC,20161123,35.48,35.52,35.10,35.20,15843100",
                                          "INTC,20161122,35.18,35.50,35.11,35.48,22327400",
                                          "INTC,20161121,35.10,35.20,34.84,34.98,14259500",
                                          "INTC,20161118,34.90,35.00,34.64,34.95,16806900",
                                          "INTC,20161117,34.81,35.02,34.63,35.02,15748500",
                                          "INTC,20161116,34.90,34.92,34.54,34.84,18565500",
                                          "INTC,20161115,34.64,35.29,34.61,34.91,20676100",
                                          "INTC,20161114,34.56,34.73,34.20,34.48,22558900",
                                          "INTC,20161111,34.57,34.87,34.34,34.61,22665000",
                                          "INTC,20161110,34.95,34.95,34.03,34.50,28249900",
                                          "INTC,20161109,34.13,34.85,33.67,34.75,25999400",
                                          "INTC,20161108,34.60,34.95,34.49,34.74,14833100",
                                          "INTC,20161107,34.24,34.75,34.15,34.69,19296600",
                                          "INTC,20161104,33.53,33.93,33.42,33.61,21914700",
                                          "INTC,20161103,34.45,34.52,33.87,33.93,19400000",
                                          "INTC,20161102,34.60,34.90,34.47,34.60,21416400",
                                          "INTC,20161101,34.90,35.16,34.27,34.52,25368200",
                                          "INTC,20161031,34.92,35.08,34.84,34.87,19188600"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return expectedURL(symbol, exchange, null, null);
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
    return expectedURL(symbol, exchange, start, end, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange, final Calendar start, final Calendar end, final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE +
                   symbol +
                   FREQUENCY + ((frequency == null) ? Frequencies.DAILY : frequency) +
                   API_KEY);
  }

}
