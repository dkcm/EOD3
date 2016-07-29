/**
 * YahooFinanceTest.java  v0.8  4 March 2014 5:59:06 PM
 *
 * Copyright © 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>YahooFinance</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.8
 */
public class YahooFinanceTest extends SourceTest {

  private static final String BASE        = baseURL(YahooFinanceTest.class);
  private static final String START_MONTH = "&a=";
  private static final String START_DATE  = "&b=";
  private static final String START_YEAR  = "&c=";
  private static final String END_MONTH   = "&d=";
  private static final String END_DATE    = "&e=";
  private static final String END_YEAR    = "&f=";
  private static final String FREQUENCY   = "&g=";

  public YahooFinanceTest() {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(NYSEARCA, EMPTY);
    exchanges.put(TSX, ".TO");
    exchanges.put(LSE, ".L");
    exchanges.put(ISE, ".IR");
    exchanges.put(FWB, ".F");
    exchanges.put(PAR, ".PA");
    exchanges.put(AMS, ".AS");
    exchanges.put(BB, ".BR");
    exchanges.put(SWX, ".SW");
    exchanges.put(MIB, ".MI");
    exchanges.put(BM, ".MA");
    exchanges.put(BVLP, ".LS");
    exchanges.put(WB, ".VI");
    exchanges.put(ATHEX, ".AT");
    exchanges.put(BIST, ".IS");
    exchanges.put(OSLO, ".OL");
    exchanges.put(SB, ".ST");
    exchanges.put(HEX, ".HE");
    exchanges.put(KFB, ".CO");
    exchanges.put(ICEX, ".IC");
    exchanges.put(MOEX, ".ME");
    exchanges.put(RSE, ".RG");
    exchanges.put(TALSE, ".TL");
    exchanges.put(VSE, ".VS");
    exchanges.put(PX, ".PR");
    exchanges.put(SGX, ".SI");
    exchanges.put(HKSE, ".HK");
    exchanges.put(SSE, ".SS");
    exchanges.put(SZSE, ".SZ");
    exchanges.put(BSE, ".BO");
    exchanges.put(NSE, ".NS");
    exchanges.put(KRX, ".KS");
    exchanges.put(TWSE, ".TW");
    exchanges.put(IDX, ".JK");
    exchanges.put(MYX, ".KL");
    exchanges.put(SET, ".BK");
    exchanges.put(ASX, ".AX");
    exchanges.put(NZX, ".NZ");
    exchanges.put(TASE, ".TA");
    exchanges.put(EGX, ".CA");
    exchanges.put(QSE, ".QA");
    exchanges.put(BOVESPA, ".SA");
    exchanges.put(BCBA, ".BA");
    exchanges.put(BCS, ".SN");
    exchanges.put(BMV, ".MX");
    exchanges.put(BVCA, ".CR");

    originalLines.addAll(Arrays.asList("Date,Open,High,Low,Close,Volume,Adj Close",
                                       "2015-12-04,34.11,35.03,34.00,34.94,24484400,34.94",
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
    return new URL(BASE + symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return new URL(BASE + symbol + (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol + START_MONTH + start.get(Calendar.MONTH)
                                 + START_DATE  + start.get(Calendar.DATE)
                                 + START_YEAR  + start.get(Calendar.YEAR)
                                 + END_MONTH + end.get(Calendar.MONTH)
                                 + END_DATE  + end.get(Calendar.DATE)
                                 + END_YEAR  + end.get(Calendar.YEAR));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE + symbol + START_MONTH + start.get(Calendar.MONTH)
                                 + START_DATE  + start.get(Calendar.DATE)
                                 + START_YEAR  + start.get(Calendar.YEAR)
                                 + END_MONTH + end.get(Calendar.MONTH)
                                 + END_DATE  + end.get(Calendar.DATE)
                                 + END_YEAR  + end.get(Calendar.YEAR)
                                 + FREQUENCY + frequency.frequency());
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return new URL(BASE + symbol + (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY)
                                 + START_MONTH + start.get(Calendar.MONTH)
                                 + START_DATE  + start.get(Calendar.DATE)
                                 + START_YEAR  + start.get(Calendar.YEAR)
                                 + END_MONTH + end.get(Calendar.MONTH)
                                 + END_DATE  + end.get(Calendar.DATE)
                                 + END_YEAR  + end.get(Calendar.YEAR));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE + symbol + (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY)
                                 + START_MONTH + start.get(Calendar.MONTH)
                                 + START_DATE  + start.get(Calendar.DATE)
                                 + START_YEAR  + start.get(Calendar.YEAR)
                                 + END_MONTH + end.get(Calendar.MONTH)
                                 + END_DATE  + end.get(Calendar.DATE)
                                 + END_YEAR  + end.get(Calendar.YEAR)
                                 + FREQUENCY + frequency.frequency());
  }

}
