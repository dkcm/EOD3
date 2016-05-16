/**
 * MorningstarTest.java	v0.6	27 December 2015 9:04:44 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Morningstar</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class MorningstarTest extends SourceTest {

  // Date-related URL parameters
  private static final String      BASE              = baseURL(MorningstarTest.class);
  private static final String      START_DATE        = "&sd=";
  private static final String      END_DATE          = "&ed=";
  private static final String      FREQUENCY         = "&freq=";

  private static final Frequencies DEFAULT_FREQUENCY = Frequencies.DAILY;

  // Exchange-related constants
  private static final String      XNYS              = "XNYS:";
  private static final String      XNAS              = "XNAS:";
  private static final String      XASE              = "XASE:";
  private static final String      XTSE              = "XTSE:";
  private static final String      XLON              = "XLON:";
  private static final String      XDUB              = "XDUB";
  private static final String      XFRA              = "XFRA:";
  private static final String      XPAR              = "XPAR:";
  private static final String      XAMS              = "XAMS:";
  private static final String      XBRU              = "XBRU";
  private static final String      XSWX              = "XSWX:";
  private static final String      XMIL              = "XMIL:";
  private static final String      XMAD              = "XMAD:";
  private static final String      XLIS              = "XLIS";
  private static final String      XWBO              = "XWBO:";
  private static final String      XATH              = "XATH:";
  private static final String      XIST              = "XIST:";
  private static final String      XOSL              = "XOSL:";
  private static final String      XSTO              = "XSTO:";
  private static final String      XHEL              = "XHEL";
  private static final String      XCSE              = "XCSE:";
  private static final String      XICE              = "XICE:";
  private static final String      XMIC              = "XMIC:";
  private static final String      XWAR              = "XWAR:";
  private static final String      XBUD              = "XBUD:";
  private static final String      XPRA              = "XPRA:";
  private static final String      XBSE              = "XBSE:";
  private static final String      XSES              = "XSES:";
  private static final String      XHKG              = "XHKG:";
  private static final String      XSHG              = "XSHG:";
  private static final String      XSHE              = "XSHE:";
  private static final String      XTKS              = "XTKS:";
  private static final String      XBOM              = "XBOM:";
  private static final String      XNSE              = "XNSE:";
  private static final String      XKRX              = "XKRX:";
  private static final String      XTAI              = "XTAI:";
  private static final String      XIDX              = "XIDX:";
  private static final String      XKLS              = "XKLS:";
  private static final String      XBKK              = "XBKK:";
  private static final String      XASX              = "XASX:";
  private static final String      XNZE              = "XNZE:";
  private static final String      XTAE              = "XTAE:";
  private static final String      XJSE              = "XJSE";
  private static final String      XCAI              = "XCAI";
  private static final String      XBSP              = "XBSP:";
  private static final String      XBUE              = "XBUE:";
  private static final String      XSGO              = "XSGO:";
  private static final String      XMEX              = "XMEX:";

  private final DateFormat         dateFormat        = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

  public MorningstarTest() {
    exchanges.put(NYSE, XNYS);
    exchanges.put(NASDAQ, XNAS);
    exchanges.put(AMEX, XASE);
    exchanges.put(NYSEARCA, EMPTY);
    exchanges.put(TSX, XTSE);
    exchanges.put(LSE, XLON);
    exchanges.put(ISE, XDUB);
    exchanges.put(FWB, XFRA);
    exchanges.put(PAR, XPAR);
    exchanges.put(AMS, XAMS);
    exchanges.put(BB, XBRU);
    exchanges.put(SWX, XSWX);
    exchanges.put(MIB, XMIL);
    exchanges.put(BM, XMAD);
    exchanges.put(BVLP, XLIS);
    exchanges.put(WB, XWBO);
    exchanges.put(ATHEX, XATH);
    exchanges.put(BIST, XIST);
    exchanges.put(OSLO, XOSL);
    exchanges.put(SB, XSTO);
    exchanges.put(HEX, XHEL);
    exchanges.put(KFB, XCSE);
    exchanges.put(ICEX, XICE);
    exchanges.put(MOEX, XMIC);
    exchanges.put(GPW, XWAR);
    exchanges.put(BET, XBUD);
    exchanges.put(PX, XPRA);
    exchanges.put(BVB, XBSE);
    exchanges.put(SGX, XSES);
    exchanges.put(HKSE, XHKG);
    exchanges.put(SSE, XSHG);
    exchanges.put(SZSE, XSHE);
    exchanges.put(TSE, XTKS);
    exchanges.put(BSE, XBOM);
    exchanges.put(NSE, XNSE);
    exchanges.put(KRX, XKRX);
    exchanges.put(TWSE, XTAI);
    exchanges.put(IDX, XIDX);
    exchanges.put(MYX, XKLS);
    exchanges.put(SET, XBKK);
    exchanges.put(ASX, XASX);
    exchanges.put(NZX, XNZE);
    exchanges.put(TASE, XTAE);
    exchanges.put(JSE, XJSE);
    exchanges.put(EGX, XCAI);
    exchanges.put(BOVESPA, XBSP);
    exchanges.put(BCBA, XBUE);
    exchanges.put(BCS, XSGO);
    exchanges.put(BMV, XMEX);

    originalLines.addAll(Arrays.asList("Intel Corp (INTC) Historical Prices",
                                       "Date,Open,High,Low,Close,Volume",
                                       "12/07/2015,54.77,54.88,53.91,54.40,\"15,077,326\"",
                                       "12/04/2015,53.66,55.33,53.52,55.09,\"21,463,021\"",
                                       "12/03/2015,54.42,54.60,53.35,53.51,???",  // volume is several non-digit characters
                                       "12/02/2015,54.99,55.02,54.03,54.14,",     // empty volume
                                       "12/01/2015,54.40,54.91,54.25,54.88,�",    // volume is a single non-digit character
                                       "11/30/2015,54.34,54.46,53.97,54.09,\"14,603,058\""));

    transformedLines.addAll(Arrays.asList("INTC,20151207,54.77,54.88,53.91,54.40,15077326",
                                          "INTC,20151204,53.66,55.33,53.52,55.09,21463021",
                                          "INTC,20151203,54.42,54.60,53.35,53.51,0",
                                          "INTC,20151202,54.99,55.02,54.03,54.14,0",
                                          "INTC,20151201,54.40,54.91,54.25,54.88,0",
                                          "INTC,20151130,54.34,54.46,53.97,54.09,14603058"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange) throws MalformedURLException {
    return expectedURL(symbol, exchange, DEFAULT_START, Calendar.getInstance());
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
    return expectedURL(symbol, null, start, end, frequency);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return expectedURL(symbol, exchange, start, end, DEFAULT_FREQUENCY);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return new URL(BASE +
                   (exchanges.containsKey(exchange) ? exchanges.get(exchange) : EMPTY) +
                   symbol +
                   START_DATE + dateFormat.format(start.getTime()) +
                   END_DATE + dateFormat.format(end.getTime()) +
                   FREQUENCY + frequency.frequency());
  }

}
