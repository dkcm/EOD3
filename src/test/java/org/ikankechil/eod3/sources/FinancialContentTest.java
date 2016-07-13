/**
 * FinancialContentTest.java v0.2 15 January 2016 2:25:20 PM
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.eod3.sources.FinancialContent.DateFormats;
import org.junit.Test;

/**
 * JUnit test for <code>FinancialContent</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class FinancialContentTest extends SourceTest {

  private final String        base;

  private static final String YEAR           = "&Year=";
  private static final String MONTH          = "&Month=";
  private static final String MONTHS         = "&Range=";

  private static final int    MONTHS_IN_YEAR = 12;

  private final String[]      DATE_FORMATS   = { "INPUT", "OUTPUT" };

  public FinancialContentTest() {
    this("http://markets.financialcontent.com/stocks/action/gethistoricaldata?Symbol=");

    originalLines.addAll(Arrays.asList("Symbol,Date,Open,High,Low,Close,Volume,Change,% Change",
                                       "INTC,12/31/15,,133.64,133.64,133.64,0,-2.35,-1.73%",
                                       "INTC,12/30/15,135.40,138.28,134.67,135.99,340482,6.93,5.37%",
                                       "INTC,12/29/15,131.71,131.75,128.50,129.06,388222,-8.16,-5.95%",
                                       "INTC,12/28/15,134.54,137.25,134.17,137.22,265108,8.35,6.48%",
                                       "INTC,12/24/15,,128.87,128.87,128.87,0,-1.54,-1.18%"));
    transformedLines.addAll(Arrays.asList("INTC,20151231,,133.64,133.64,133.64,0",
                                          "INTC,20151230,135.40,138.28,134.67,135.99,340482",
                                          "INTC,20151229,131.71,131.75,128.50,129.06,388222",
                                          "INTC,20151228,134.54,137.25,134.17,137.22,265108",
                                          "INTC,20151224,,128.87,128.87,128.87,0"));
  }

  FinancialContentTest(final String base) {
    this.base = base;

    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(NYSEARCA, EMPTY);
    exchanges.put(TSX, EMPTY);
    exchanges.put(FX, EMPTY);
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return expectedURL(symbol, exchange, DEFAULT_START, TODAY);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return expectedURL(symbol, null, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, null, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    final int startMonth = start.get(Calendar.MONTH) + 1;
    final int startYear = start.get(Calendar.YEAR);
    final int endMonth = end.get(Calendar.MONTH) + 1;
    final int endYear = end.get(Calendar.YEAR);
    final int months = (endYear - startYear) * MONTHS_IN_YEAR + (endMonth - startMonth) + 1;

    return new URL(base   + ((exchange == Exchanges.FX) ? (symbol.substring(0, 3) + HYPHEN + symbol.substring(3))
                                                        : symbol) +
                   MONTH  + endMonth +
                   YEAR   + endYear +
                   MONTHS + months);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end);
  }

  @Test
  public final void dateFormats() throws Exception {
    for (final String df : DATE_FORMATS) {
      assertEquals(df, DateFormats.valueOf(df).name());
    }
    assertEquals(DATE_FORMATS.length, DateFormats.values().length);
  }

  @Test
  public final void dateFormatPatterns() throws Exception {
    for (final DateFormats df : DateFormats.values()) {
      assertTrue(df.dateFormat instanceof SimpleDateFormat);
      final String actual = ((SimpleDateFormat) df.dateFormat).toPattern();

      switch (df) {
        case INPUT:
          assertEquals("MM/dd/yy", actual);
          break;

        case OUTPUT:
          assertEquals("yyyyMMdd", actual);
          break;

        default:
          fail("Unsupported date format: " + df);
          break;
      }
    }
  }

}
