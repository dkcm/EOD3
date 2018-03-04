/**
 * MacrotrendsTest.java  v0.1  4 March 2018 3:25:11 PM
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
 * JUnit test for <code>Macrotrends</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class MacrotrendsTest extends SourceTest {

  private static final String BASE = baseURL(MacrotrendsTest.class);

  public MacrotrendsTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);

    originalLines.addAll(Arrays.asList("\"MacroTrends Data Download\"",
                                       "\"BAC - Historical Price and Volume Data\"",
                                       "\"NOTE: Historical prices are adjusted for splits AND dividends\"",
                                       "",
                                       "\"Disclaimer and Terms of Use: Historical stock data is provided 'as is' and solely for informational purposes, not for trading purposes or advice.\"",
                                       "\"MacroTrends LLC expressly disclaims the accuracy, adequacy, or completeness of any data and shall not be liable for any errors, omissions or other defects in, \"",
                                       "\"delays or interruptions in such data, or for any actions taken in reliance thereon.  Neither MacroTrends LLC nor any of our information providers will be liable\"",
                                       "\"for any damages relating to your use of the data provided.\"",
                                       "",
                                       "",
                                       "date,open,high,low,close,volume",
                                       "1986-05-29,2.6343,2.6584,2.6343,2.6526,15736000",
                                       "1986-05-30,2.6409,2.6708,2.6409,2.6708,6500800",
                                       "1986-06-02,2.6735,2.6798,2.6493,2.6614,314400",
                                       "1986-06-03,2.6559,2.6859,2.6496,2.6801,154400",
                                       "1986-06-04,2.6611,2.6853,2.6432,2.6432,192000",
                                       "1986-06-05,2.6314,2.6614,2.6251,2.6614,84000",
                                       "1986-06-06,2.6735,2.6798,2.6493,2.6614,367200",
                                       "1986-06-09,2.6659,2.6717,2.5991,2.6054,497600",
                                       "1986-06-10,2.6175,2.6175,2.5870,2.6054,141600",
                                       "1986-06-11,2.6087,2.6087,2.5782,2.5966,318400"));

    transformedLines.addAll(Arrays.asList("INTC,19860611,2.6087,2.6087,2.5782,2.5966,318400",
                                          "INTC,19860610,2.6175,2.6175,2.5870,2.6054,141600",
                                          "INTC,19860609,2.6659,2.6717,2.5991,2.6054,497600",
                                          "INTC,19860606,2.6735,2.6798,2.6493,2.6614,367200",
                                          "INTC,19860605,2.6314,2.6614,2.6251,2.6614,84000",
                                          "INTC,19860604,2.6611,2.6853,2.6432,2.6432,192000",
                                          "INTC,19860603,2.6559,2.6859,2.6496,2.6801,154400",
                                          "INTC,19860602,2.6735,2.6798,2.6493,2.6614,314400",
                                          "INTC,19860530,2.6409,2.6708,2.6409,2.6708,6500800",
                                          "INTC,19860529,2.6343,2.6584,2.6343,2.6526,15736000"));
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
    return new URL(BASE + symbol);
  }

}
