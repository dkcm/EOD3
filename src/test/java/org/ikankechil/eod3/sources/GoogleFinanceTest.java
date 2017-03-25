/**
 * GoogleFinanceTest.java  v0.11  6 March 2014 2:32:24 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
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
import org.ikankechil.eod3.sources.GoogleFinance.DateFormats;
import org.junit.Test;

/**
 * JUnit test for <code>GoogleFinance</code>.
 *
 * @author Daniel Kuan
 * @version 0.11
 */
public class GoogleFinanceTest extends SourceTest {

  private static final String BASE         = baseURL(GoogleFinanceTest.class);
  private static final String START_DATE   = "&startdate=";
  private static final String END_DATE     = "&enddate=";
//  private static final String FREQUENCY    = "&histperiod=";

  private final DateFormat    dateFormat   = new SimpleDateFormat("dd+MMM+yyyy", Locale.US);

  private final String[]      DATE_FORMATS = { "URL", "INPUT", "OUTPUT" };

  private static final String LON          = "LON:";
  private static final String HKG          = "HKG:";
  private static final String SHA          = "SHA:";
  private static final String SHE          = "SHE:";
  private static final String TYO          = "TYO:";
  private static final String BOM          = "BOM:";
  private static final String TSE_         = "TSE:";
  private static final String FRA          = "FRA:";
  private static final String VIE          = "VIE:";
  private static final String IST          = "IST:";
  private static final String EPA          = "EPA:";
  private static final String AMS          = "AMS:";
  private static final String EBR          = "EBR:";
  private static final String BME          = "BME:";
  private static final String ELI          = "ELI:";
  private static final String WSE          = "WSE:";
  private static final String BIT          = "BIT:";
  private static final String TPE          = "TPE:";
  private static final String KLSE         = "KLSE:";
  private static final String BKK          = "BKK:";
  private static final String NZE          = "NZE:";
  private static final String STO          = "STO:";
  private static final String HEL          = "HEL:";
  private static final String CPH          = "CPH:";
  private static final String ICE          = "ICE:";
  private static final String MCX          = "MCX:";
  private static final String TAL          = "TAL:";
  private static final String TLV          = "TLV:";
  private static final String BVMF         = "BVMF:";

  public GoogleFinanceTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);
    exchanges.put(TSX, TSE_);
    exchanges.put(LSE, LON);
    exchanges.put(FWB, FRA);
    exchanges.put(PAR, EPA);
    exchanges.put(AEX, AMS);
    exchanges.put(BB, EBR);
    exchanges.put(SWX, SWX.toString() + COLON);
    exchanges.put(MIB, BIT);
    exchanges.put(BM, BME);
    exchanges.put(BVLP, ELI);
    exchanges.put(WB, VIE);
    exchanges.put(BIST, IST);
    exchanges.put(SB, STO);
    exchanges.put(HEX, HEL);
    exchanges.put(KFB, CPH);
    exchanges.put(ICEX, ICE);
    exchanges.put(GPW, WSE);
    exchanges.put(SGX, SGX.toString() + COLON);
    exchanges.put(HKEX, HKG);
    exchanges.put(SSE, SHA);
    exchanges.put(SZSE, SHE);
    exchanges.put(TSE, TYO);
    exchanges.put(BSE, BOM);
    exchanges.put(NSE, NSE.toString() + COLON);
    exchanges.put(KRX, KRX.toString() + COLON);
    exchanges.put(TWSE, TPE);
    exchanges.put(IDX, IDX.toString() + COLON);
    exchanges.put(MYX, KLSE);
    exchanges.put(SET, BKK);
    exchanges.put(ASX, ASX.toString() + COLON);
    exchanges.put(NZX, NZE);
    exchanges.put(MOEX, MCX);
    exchanges.put(RSE, RSE.toString() + COLON);
    exchanges.put(TALSE, TAL);
    exchanges.put(VSE, VSE.toString() + COLON);
    exchanges.put(TASE, TLV);
    exchanges.put(TADAWUL, TADAWUL.toString() + COLON);
    exchanges.put(JSE, JSE.toString() + COLON);
    exchanges.put(BOVESPA, BVMF);
    exchanges.put(BCBA, BCBA.toString() + COLON);
    exchanges.put(BMV, BMV.toString() + COLON);

    originalLines.addAll(Arrays.asList("Date,Open,High,Low,Close,Volume",
                                       "4-Dec-15,34.11,35.02,34.00,34.94,24900994",
                                       "3-Dec-15,34.97,34.99,34.00,34.04,30131055",
                                       "2-Dec-15,35.09,35.41,34.80,34.83,18688180",
                                       "1-Dec-15,35.00,35.20,34.71,35.09,23560239",
                                       "30-Nov-15,34.55,34.90,34.43,34.77,21785791",
                                       "INTC,InvalidDate,",
                                       ""));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.02,34.00,34.94,24900994",
                                          "INTC,20151203,34.97,34.99,34.00,34.04,30131055",
                                          "INTC,20151202,35.09,35.41,34.80,34.83,18688180",
                                          "INTC,20151201,35.00,35.20,34.71,35.09,23560239",
                                          "INTC,20151130,34.55,34.90,34.43,34.77,21785791",
                                          "",
                                          ""));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol +
                   START_DATE + dateFormat.format(DEFAULT_START.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   symbol +
                   START_DATE + dateFormat.format(DEFAULT_START.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol +
                   START_DATE + dateFormat.format(start.getTime()) +
                   END_DATE + dateFormat.format(end.getTime()));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, start, end);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   symbol +
                   START_DATE + dateFormat.format(start.getTime()) +
                   END_DATE + dateFormat.format(end.getTime()));
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
        case URL:
          assertEquals("dd+MMM+yyyy", actual);
          break;

        case INPUT:
          assertEquals("d-MMM-yy", actual);
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
