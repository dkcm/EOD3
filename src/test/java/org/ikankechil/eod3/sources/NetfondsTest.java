/**
 * NetfondsTest.java	v0.10	6 April 2015 12:55:23 am
 *
 * Copyright � 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Set;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Netfonds</code>.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.10
 */
public class NetfondsTest extends SourceTest {

  private final String                BASE         = baseURL(NetfondsTest.class);

  private static final String         N            = ".N";
  private static final String         O            = ".O";
  private static final String         A            = ".A";
  private static final String         ST           = ".ST";
  private static final String         CPH          = ".CPH";
  private static final String         FXSB         = ".FXSB";
  private static final String         E_L          = "E-%sL.BTSE";
  private static final String         E_I          = "E-%sI.BTSE";
  private static final String         E_D          = "E-%sD.BTSE";
  private static final String         E_P          = "E-%sP.BTSE";
  private static final String         E_A          = "E-%sA.BTSE";
  private static final String         E_B          = "E-%sB.BTSE";
  private static final String         E_Z          = "E-%sZ.BTSE";
  private static final String         E_M          = "E-%sM.BTSE";
  private static final String         E_E          = "E-%sE.BTSE";
  private static final String         E_U          = "E-%sU.BTSE";
  private static final String         E_V          = "E-%sV.BTSE";
  private static final String         E_H          = "E-%sH.BTSE";

  private static final Set<Exchanges> EU_EXCHANGES = EnumSet.of(LSE, ISE, FWB, PAR, AEX, BB, SWX, MIB, BM, BVLP, WB, HEX);

  public NetfondsTest() {
    exchanges.put(NYSE, N);
    exchanges.put(NASDAQ, O);
    exchanges.put(AMEX, A);
    exchanges.put(ARCA, A);
    exchanges.put(OSLO, EMPTY);
    exchanges.put(SB, ST);
    exchanges.put(HEX, E_H);
    exchanges.put(KFB, CPH);
    exchanges.put(ICEX, ICEX.toString());
    exchanges.put(FX, FXSB);
    exchanges.put(LSE, E_L);
    exchanges.put(ISE, E_I);
    exchanges.put(FWB, E_D);
    exchanges.put(PAR, E_P);
    exchanges.put(AEX, E_A);
    exchanges.put(BB, E_B);
    exchanges.put(SWX, E_Z);
    exchanges.put(MIB, E_M);
    exchanges.put(BM, E_E);
    exchanges.put(BVLP, E_U);
    exchanges.put(WB, E_V);

    originalLines.addAll(Arrays.asList("quote_date,paper,exch,open,high,low,close,volume,value",
                                       "20151204,INTC,Nasdaq,34.11,35.02,34.00,34.94,23181911,805557348",
                                       "20151203,INTC,Nasdaq,34.97,34.99,34.00,34.04,23087438,793151371",
                                       "20151202,INTC,Nasdaq,35.09,35.41,34.80,34.84,17197888,602936134",
                                       "20151201,INTC,Nasdaq,35.00,35.20,34.71,35.09,20916788,731813435",
                                       "20151130,INTC,Nasdaq,34.55,34.90,34.43,34.77,18365594,637784565"));

    transformedLines.addAll(Arrays.asList("INTC,20151204,34.11,35.02,34.00,34.94,23181911",
                                          "INTC,20151203,34.97,34.99,34.00,34.04,23087438",
                                          "INTC,20151202,35.09,35.41,34.80,34.84,17197888",
                                          "INTC,20151201,35.00,35.20,34.71,35.09,20916788",
                                          "INTC,20151130,34.55,34.90,34.43,34.77,18365594"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    final StringBuilder url = new StringBuilder(BASE);
    if (exchanges.containsKey(exchange)) {
      if (EU_EXCHANGES.contains(exchange)) {
        url.append(String.format(exchanges.get(exchange), symbol));
      }
      else {
        url.append(symbol).append(exchanges.get(exchange));
      }
    }
    else {
      url.append(symbol);
    }
    return new URL(url.toString());
  }

  @Override
  protected URL expectedURL(final String symbol, final Calendar start, final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end) throws MalformedURLException {
    return expectedURL(symbol, exchange);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency) throws MalformedURLException {
    return expectedURL(symbol, exchange);
  }

}
