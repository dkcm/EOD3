/**
 * WallStreetJournalTest.java	v0.2	6 April 2015 12:50:58 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.eod3.sources.WallStreetJournal.Elements;
import org.junit.Test;

/**
 * JUnit test for <code>WallStreetJournal</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class WallStreetJournalTest extends SourceTest {

  private static final String BASE          = "http://quotes.wsj.com/cdssvco/marketwatch/dylan/v1/HistoricalPrices?pageSize=0&sortColumn=Time&sortOrder=DESC&ticker=";
  private static final String START_DATE    = "&startDate=";
  private static final String END_DATE      = "&endDate=";
  private static final String FREQUENCY     = "&duration=P1";
  private static final String COUNTRY_CODE  = "&countrycode=";

  private static final String US            = "US";
  private static final String CA            = "CA";
  private static final String UK            = "UK";
  private static final String DE            = "DE";
  private static final String FR            = "FR";
  private static final String SG            = "SG";
  private static final String HK            = "HK";
  private static final String JP            = "JP";
  private static final String IN            = "IN";
  private static final String AU            = "AU";
  private static final String TW            = "TW";
  private static final String IT            = "IT";
  private static final String PL            = "PL";
  private static final String NO            = "NO";
  private static final String SE            = "SE";
  private static final String DK            = "DK";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

  public WallStreetJournalTest() {
    exchanges.put(NYSE, US);
    exchanges.put(NASDAQ, US);
    exchanges.put(AMEX, US);
    exchanges.put(NYSEARCA, US);
    exchanges.put(LSE, UK);
    exchanges.put(FWB, DE);
    exchanges.put(PAR, FR);
    exchanges.put(MIB, IT);
    exchanges.put(SGX, SG);
    exchanges.put(HKSE, HK);
    exchanges.put(TSE, JP);
    exchanges.put(NSE, IN);
    exchanges.put(TWSE, TW);
    exchanges.put(ASX, AU);
    exchanges.put(TSX, CA);
    exchanges.put(GPW, PL);
    exchanges.put(OSLO, NO);
    exchanges.put(SB, SE);
    exchanges.put(KFB, DK);
    exchanges.put(FX, US);

    originalLines.addAll(Arrays.asList("2015-12-04,34.11,35.03,34.00,34.94,24484400,34.94",
                                       "2015-12-03,34.97,34.99,34.00,34.04,29829200,34.04",
                                       "2015-12-02,35.09,35.41,34.81,34.83,18644100,34.83",
                                       "2015-12-01,35.00,35.20,34.71,35.09,23352200,35.09",
                                       "2015-11-30,34.55,34.90,34.43,34.77,20131700,34.77"));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.03,34.00,34.94,24484400",
                                          "INTC,20151203,34.97,34.99,34.00,34.04,29829200",
                                          "INTC,20151202,35.09,35.41,34.81,34.83,18644100",
                                          "INTC,20151201,35.00,35.20,34.71,35.09,23352200",
                                          "INTC,20151130,34.55,34.90,34.43,34.77,20131700"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol +
                   COUNTRY_CODE +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()) +
                   END_DATE + urlDateFormat.format(TODAY.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   COUNTRY_CODE + exchanges.get(exchange) +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()) +
                   END_DATE + urlDateFormat.format(TODAY.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   COUNTRY_CODE +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   COUNTRY_CODE +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + Character.toUpperCase(frequency.frequency()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   COUNTRY_CODE + exchanges.get(exchange) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   COUNTRY_CODE + exchanges.get(exchange) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   FREQUENCY + Character.toUpperCase(frequency.frequency()));
  }

  @Test
  public void lineElements() throws Exception {
    for (final Elements element : Elements.values()) {
      final String name = element.name().toLowerCase();
      assertEquals(name, element.toString());
      assertEquals(name.length() + 1, element.offset());
    }
  }

}
