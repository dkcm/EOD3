/**
 * FinamTest.java  v0.1  23 July 2017 3:51:19 pm
 *
 * Copyright © 2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.Frequencies.*;
import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Finam</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class FinamTest extends SourceTest {

  private static final String                    BASE        = baseURL(FinamTest.class);
  private static final String                    START_DATE  = "&df=";
  private static final String                    START_MONTH = "&mf=";
  private static final String                    START_YEAR  = "&yf=";
  private static final String                    END_DATE    = "&dt=";
  private static final String                    END_MONTH   = "&mt=";
  private static final String                    END_YEAR    = "&yt=";
  private static final String                    FREQUENCY   = "&p=";

  private static final Map<Frequencies, Integer> FREQUENCIES = new EnumMap<>(Frequencies.class);

  static {
    // daily = 8, weekly = 9 / none, monthly = 10
    FREQUENCIES.put(DAILY, 8);
    FREQUENCIES.put(WEEKLY, 9);
    FREQUENCIES.put(MONTHLY, 10);
  }

  public FinamTest() {
    exchanges.put(NYSE, "25");
    exchanges.put(NASDAQ, "25");
    exchanges.put(ARCA, "28");
    exchanges.put(MOEX, EMPTY);
    exchanges.put(FX, "5");

    originalLines.addAll(Arrays.asList("<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>",
                                       "20150608,000000,128.9400000,129.2100000,126.8400000,127.7700000,3399563",
                                       "20150609,000000,126.7200000,128.0700000,125.6200000,127.4100000,3998136",
                                       "20150610,000000,127.9800000,129.3400000,127.8500000,128.9100000,2674926",
                                       "20150611,000000,129.1300000,130.1700000,128.4800000,128.5900000,2413383",
                                       "20150612,000000,128.1700000,128.3200000,127.1200000,127.1500000,2375203"));

    transformedLines.addAll(Arrays.asList("INTC,20150612,128.1700000,128.3200000,127.1200000,127.1500000,2375203",
                                          "INTC,20150611,129.1300000,130.1700000,128.4800000,128.5900000,2413383",
                                          "INTC,20150610,127.9800000,129.3400000,127.8500000,128.9100000,2674926",
                                          "INTC,20150609,126.7200000,128.0700000,125.6200000,127.4100000,3998136",
                                          "INTC,20150608,128.9400000,129.2100000,126.8400000,127.7700000,3399563"));
  }

  enum Symbols {
    INTC("&em=19069&market=25"),
    EURUSD("&em=83&market=5");

    private final String idExchange;

    Symbols(final String idExchange) {
      this.idExchange = idExchange;
    }

  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + Symbols.valueOf(symbol).idExchange);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + Symbols.valueOf(symbol).idExchange
                        + START_DATE  + start.get(Calendar.DATE)
                        + START_MONTH + start.get(Calendar.MONTH)
                        + START_YEAR  + start.get(Calendar.YEAR)
                        + END_DATE  + end.get(Calendar.DATE)
                        + END_MONTH + end.get(Calendar.MONTH)
                        + END_YEAR  + end.get(Calendar.YEAR));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + Symbols.valueOf(symbol).idExchange
                        + START_DATE  + start.get(Calendar.DATE)
                        + START_MONTH + start.get(Calendar.MONTH)
                        + START_YEAR  + start.get(Calendar.YEAR)
                        + END_DATE  + end.get(Calendar.DATE)
                        + END_MONTH + end.get(Calendar.MONTH)
                        + END_YEAR  + end.get(Calendar.YEAR)
                        + FREQUENCY + FREQUENCIES.get(frequency));
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
    return expectedURL(symbol, start, end, frequency);
  }

}
