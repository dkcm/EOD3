/**
 * QuoteMediaTest.java	v0.10	20 March 2014 12:46:35 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>QuoteMedia</code>.
 *
 * @author Daniel Kuan
 * @version 0.10
 */
public class QuoteMediaTest extends SourceTest {

  // Date-related URL parameters
  private static final String BASE        = baseURL(QuoteMediaTest.class);
  private static final String START_DATE  = "&startDay=";
  private static final String START_MONTH = "&startMonth=";
  private static final String START_YEAR  = "&startYear=";
  private static final String END_DATE    = "&endDay=";
  private static final String END_MONTH   = "&endMonth=";
  private static final String END_YEAR    = "&endYear=";
  private static final String MAX_YEARS   = "&maxDownloadYears=" + Short.MAX_VALUE;

  // Exchange-related constants
  private static final String CA          = ":CA";
  private static final String LN          = ":LN";
  private static final String IE          = ":IE";
  private static final String FF          = ":FF";
  private static final String PA          = ":PA";
  private static final String AS          = ":AS";
  private static final String BR          = ":BR";
  private static final String LU          = ":LU";
  private static final String SM          = ":SM";
  private static final String MI          = ":MI";
  private static final String MA          = ":MA";
  private static final String LS          = ":LS";
  private static final String VN          = ":VN";
  private static final String AT          = ":AT";
  private static final String OS          = ":OS";
  private static final String ST          = ":ST";
  private static final String HI          = ":HI";
  private static final String CO          = ":CO";
  private static final String RU          = ":RU";
  private static final String SI          = ":SI";
  private static final String HK          = ":HK";
  private static final String SH          = ":SH";
  private static final String SZ          = ":SZ";
  private static final String TK          = ":TK";
  private static final String OK          = ":OK";
  private static final String MB          = ":MB";
  private static final String IN          = ":IN";
  private static final String KR          = ":KR";
  private static final String TW          = ":TW";
  private static final String ID          = ":ID";
  private static final String MY          = ":MY";
  private static final String TH          = ":TH";
  private static final String PH          = ":PH";
  private static final String AU          = ":AU";
  private static final String NZ          = ":NZ";
  private static final String BV          = ":BV";
  private static final String AR          = ":AR";
  private static final String CL          = ":CL";
  private static final String MX          = ":MX";

  // QuoteMedia
  // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=501
  //                                                             &symbol=<Stock Symbol>
  //                                                             &startDay=02
  //                                                             &startMonth=02
  //                                                             &startYear=2002
  //                                                             &endDay=02
  //                                                             &endMonth=07
  //                                                             &endYear=2009
  //                                                             &isRanged=false
  // e.g.
  // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=501&symbol=INTC&startDay=02&startMonth=02&startYear=2002&endDay=02&endMonth=07&endYear=2009&isRanged=false

  public QuoteMediaTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);
    exchanges.put(TSX, CA);
    exchanges.put(LSE, LN);
    exchanges.put(ISE, IE);
    exchanges.put(FWB, FF);
    exchanges.put(PAR, PA);
    exchanges.put(AEX, AS);
    exchanges.put(BB, BR);
    exchanges.put(LUX, LU);
    exchanges.put(SWX, SM);
    exchanges.put(MIB, MI);
    exchanges.put(BM, MA);
    exchanges.put(BVLP, LS);
    exchanges.put(WB, VN);
    exchanges.put(ATHEX, AT);
    exchanges.put(OSLO, OS);
    exchanges.put(SB, ST);
    exchanges.put(HEX, HI);
    exchanges.put(KFB, CO);
    exchanges.put(MOEX, RU);
    exchanges.put(SGX, SI);
    exchanges.put(HKEX, HK);
    exchanges.put(SSE, SH);
    exchanges.put(SZSE, SZ);
    exchanges.put(TSE, TK);
    exchanges.put(OSE, OK);
    exchanges.put(BSE, MB);
    exchanges.put(NSE, IN);
    exchanges.put(KRX, KR);
    exchanges.put(TWSE, TW);
    exchanges.put(IDX, ID);
    exchanges.put(MYX, MY);
    exchanges.put(SET, TH);
    exchanges.put(PSE, PH);
    exchanges.put(ASX, AU);
    exchanges.put(NZX, NZ);
    exchanges.put(BOVESPA, BV);
    exchanges.put(BCBA, AR);
    exchanges.put(BCS, CL);
    exchanges.put(BMV, MX);
    exchanges.put(FX, DOLLAR);

    originalLines.addAll(Arrays.asList("date,open,high,low,close,volume,changed,changep,adjclose,tradeval,tradevol",
                                       "2015-12-04,34.11,35.025,34.00,34.935,24900994,0.895,2.63%,34.935,865294800.11,116588",
                                       "2015-12-03,34.97,34.99,34.00,34.04,30131055,-0.79,-2.27%,34.04,1035746850.11,131408",
                                       "2015-12-02,35.09,35.41,34.805,34.83,18688180,-0.26,-0.74%,34.83,655186697.41,86539",
                                       "2015-12-01,35.00,35.20,34.71,35.09,23560239,0.32,0.92%,35.09,824296639.48,112790",
                                       "2015-11-30,34.55,34.90,34.43,34.77,21785791,0.31,0.90%,34.77,756559259.79,85804"));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.025,34.00,34.935,24900994",
                                          "INTC,20151203,34.97,34.99,34.00,34.04,30131055",
                                          "INTC,20151202,35.09,35.41,34.805,34.83,18688180",
                                          "INTC,20151201,35.00,35.20,34.71,35.09,23560239",
                                          "INTC,20151130,34.55,34.90,34.43,34.77,21785791"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol + MAX_YEARS);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return new URL(BASE +
                   ((exchange == Exchanges.FX) ? DOLLAR + symbol
                                               : symbol + (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY)) +
                   MAX_YEARS);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol + START_DATE  + start.get(Calendar.DATE)
                                 + START_MONTH + (start.get(Calendar.MONTH) + 1)
                                 + START_YEAR  + start.get(Calendar.YEAR)
                                 + END_DATE  + end.get(Calendar.DATE)
                                 + END_MONTH + (end.get(Calendar.MONTH) + 1)
                                 + END_YEAR  + end.get(Calendar.YEAR));
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
                            final Calendar end)
      throws MalformedURLException {
    return new URL(BASE +
                   ((exchange == Exchanges.FX) ? DOLLAR + symbol
                                               : symbol + (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY)) +
                   START_DATE  + start.get(Calendar.DATE) +
                   START_MONTH + (start.get(Calendar.MONTH) + 1) +
                   START_YEAR  + start.get(Calendar.YEAR) +
                   END_DATE  + end.get(Calendar.DATE) +
                   END_MONTH + (end.get(Calendar.MONTH) + 1) +
                   END_YEAR  + end.get(Calendar.YEAR));
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
