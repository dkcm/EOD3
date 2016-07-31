/**
 * ExchangesTest.java v0.8 6 April 2015 2:44:12 PM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>Exchanges</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.8
 */
public class ExchangesTest {

  @Rule
  public final ExpectedException thrown    = ExpectedException.none();

  private static final String[]  EXCHANGES = { "NYSE", "NASDAQ",  "AMEX", "ARCA", "TSX",
                                               "LSE",  "ISE",     "FWB",  "PAR",  "AEX",
                                               "BB",   "SWX",     "MIB",  "BM",   "BVLP",
                                               "WB",   "ATHEX",   "BIST", "LUX",
                                               "MOEX", "TALSE",   "RSE",  "VSE",  "UX",
                                               "GPW",  "BET",     "PX",   "BVB",  "LJSE",
                                               "OSLO", "SB",      "HEX",  "KFB",  "ICEX",
                                               "SGX",  "HKEX",    "SSE",  "SZSE", "TSE",
                                               "OSE",  "BSE",     "NSE",  "KRX",  "TWSE",
                                               "IDX",  "MYX",     "SET",  "PSE",
                                               "ASX",  "NZX",
                                               "TASE", "TADAWUL", "QSE",  "ADX",  "DFM",
                                               "MSM",  "ASE",     "BHB",
                                               "JSE",  "EGX",     "NGSE", "BC",
                                               "BCBA", "BOVESPA", "BCS",  "BMV",  "BVC",
                                               "BVCA", "BVL",
                                               "FX" };
  private static final String    PARA      = "PARA";
  private static final String    SWXA      = "SWXA";
  private static final String    AMSA      = "AMSA";

  private static final String    EMPTY     = "";

  @Test
  public final void validExchanges() {
    for (final String exchange : EXCHANGES) {
      assertEquals(Exchanges.toExchange(exchange), Exchanges.valueOf(exchange));
    }
    assertEquals(EXCHANGES.length, Exchanges.values().length);
  }

  @Test
  public final void invalidExchanges() {
    assertNull(Exchanges.toExchange(PARA));
    assertNull(Exchanges.toExchange(SWXA));
    assertNull(Exchanges.toExchange(AMSA));
  }

  @Test
  public final void cannotInstantiateEmptyExchange() {
    assertNull(Exchanges.toExchange(EMPTY));
  }

  @Test
  public final void cannotInstantiateNullExchange() {
    thrown.expect(NullPointerException.class);
    assertNull(Exchanges.toExchange(null));
  }

}
