/**
 * WallStreetJournalTest.java  v0.8  6 April 2015 12:50:58 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>WallStreetJournal</code>.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.8
 */
public class WallStreetJournalTest extends SourceTest {

  // Date-related URL parameters
  private static final String BASE          = baseURL(WallStreetJournalTest.class);
  private static final String START_DATE    = "&startDate=";
  private static final String END_DATE      = "&endDate=";

  // suffix
  private static final String ROWS          = "&num_rows=32767";

  // Exchange-related constants
  private static final String COUNTRY       = "&country=";

  private static final String US            = "US";
  private static final String CA            = "CA";
  private static final String UK            = "UK";
  private static final String IE            = "IE";
  private static final String DE            = "DE";
  private static final String FR            = "FR";
  private static final String NL            = "NL";
  private static final String BE            = "BE";
  private static final String CH            = "CH";
  private static final String IT            = "IT";
  private static final String ES            = "ES";
  private static final String PT            = "PT";
  private static final String AT            = "AT";
  private static final String GR            = "GR";
  private static final String TR            = "TR";
  private static final String NO            = "NO";
  private static final String SE            = "SE";
  private static final String FI            = "FI";
  private static final String DK            = "DK";
  private static final String IS            = "IS";
  private static final String RU            = "RU";
  private static final String UA            = "UA";
  private static final String LV            = "LV";
  private static final String EE            = "EE";
  private static final String LT            = "LT";
  private static final String PL            = "PL";
  private static final String HU            = "HU";
  private static final String CZ            = "CZ";
  private static final String RO            = "RO";
  private static final String SI            = "SI";
  private static final String SG            = "SG";
  private static final String HK            = "HK";
  private static final String CN            = "CN";
  private static final String JP            = "JP";
  private static final String IN            = "IN";
  private static final String KR            = "KR";
  private static final String TW            = "TW";
  private static final String ID            = "ID";
  private static final String MY            = "MY";
  private static final String TH            = "TH";
  private static final String PH            = "PH";
  private static final String AU            = "AU";
  private static final String NZ            = "NZ";
  private static final String ZA            = "ZA";
  private static final String BR            = "BR";
  private static final String AR            = "AR";
  private static final String CL            = "CL";
  private static final String MX            = "MX";
  private static final String CO            = "CO";
  private static final String PE            = "PE";

  private final DateFormat    urlDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

  public WallStreetJournalTest() throws IOException {
    exchanges.put(NYSE, US);
    exchanges.put(NASDAQ, US);
    exchanges.put(AMEX, US);
    exchanges.put(ARCA, US);
    exchanges.put(TSX, CA);
    exchanges.put(LSE, UK);
    exchanges.put(ISE, IE);
    exchanges.put(FWB, DE);
    exchanges.put(PAR, FR);
    exchanges.put(AEX, NL);
    exchanges.put(BB, BE);
    exchanges.put(SWX, CH);
    exchanges.put(MIB, IT);
    exchanges.put(BM, ES);
    exchanges.put(BVLP, PT);
    exchanges.put(WB, AT);
    exchanges.put(ATHEX, GR);
    exchanges.put(BIST, TR);
    exchanges.put(OSLO, NO);
    exchanges.put(SB, SE);
    exchanges.put(HEX, FI);
    exchanges.put(KFB, DK);
    exchanges.put(ICEX, IS);
    exchanges.put(MOEX, RU);
    exchanges.put(UX, UA);
    exchanges.put(RSE, LV);
    exchanges.put(TALSE, EE);
    exchanges.put(VSE, LT);
    exchanges.put(GPW, PL);
    exchanges.put(BET, HU);
    exchanges.put(PX, CZ);
    exchanges.put(BVB, RO);
    exchanges.put(LJSE, SI);
    exchanges.put(SGX, SG);
    exchanges.put(HKEX, HK);
    exchanges.put(SSE, CN);
    exchanges.put(SZSE, CN);
    exchanges.put(TSE, JP);
    exchanges.put(OSE, JP);
    exchanges.put(BSE, IN);
    exchanges.put(NSE, IN);
    exchanges.put(KRX, KR);
    exchanges.put(TWSE, TW);
    exchanges.put(IDX, ID);
    exchanges.put(MYX, MY);
    exchanges.put(SET, TH);
    exchanges.put(PSE, PH);
    exchanges.put(ASX, AU);
    exchanges.put(NZX, NZ);
    exchanges.put(JSE, ZA);
    exchanges.put(BOVESPA, BR);
    exchanges.put(BCBA, AR);
    exchanges.put(BCS, CL);
    exchanges.put(BMV, MX);
    exchanges.put(BVC, CO);
    exchanges.put(BVL, PE);
    exchanges.put(FX, US);

    originalLines.addAll(Files.readAllLines(new File(DIRECTORY, getClass().getSimpleName() + HTML).toPath()));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.1100,35.0250,34.0000,34.9350,24900000",
                                          "INTC,20151203,34.9700,34.9900,34.0000,34.0400,30130000",
                                          "INTC,20151202,35.0900,35.4100,34.8050,34.8300,18690000000",
                                          "INTC,20151201,35.0000,35.2000,34.7100,35.0900,23560000000",
                                          "INTC,20151130,34.5500,34.9000,34.4300,34.7700,21790000000000",
                                          "INTC,20151127,34.5400,34.6800,34.4000,34.4600,6620000000000",
                                          "INTC,20151125,34.2600,34.7400,34.1400,34.4500,17.94",
                                          "INTC,20151124,34.3300,34.4400,33.9000,34.3600,2141"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()) +
                   END_DATE + urlDateFormat.format(TODAY.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   (exchanges.containsKey(exchange)? COUNTRY + exchanges.get(exchange) : EMPTY) +
                   START_DATE + urlDateFormat.format(DEFAULT_START.getTime()) +
                   END_DATE + urlDateFormat.format(TODAY.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return new URL(BASE + symbol + QUESTION +
                   (exchanges.containsKey(exchange)? COUNTRY + exchanges.get(exchange) : EMPTY) +
                   START_DATE + urlDateFormat.format(start.getTime()) +
                   END_DATE + urlDateFormat.format(end.getTime()) +
                   ROWS);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end);
  }

}
