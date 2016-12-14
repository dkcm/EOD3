/**
 * ExchangesTest.java  v0.10  6 April 2015 2:44:12 PM
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
 *
 *
 * @author Daniel Kuan
 * @version 0.10
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
                                               "IDX",  "MYX",     "SET",  "PSE",  "HOSE",
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
    final Exchanges[] exchanges = Exchanges.toExchanges(EXCHANGES);
    for (int i = 0; i < EXCHANGES.length; ++i) {
      final String exchange = EXCHANGES[i];
      assertEquals(Exchanges.toExchange(exchange), Exchanges.valueOf(exchange));
      assertEquals(exchange, exchanges[i].toString());
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
  public final void emptyArrayIfOnlyInvalidExchanges() {
    // empty array
    assertEquals(0, Exchanges.toExchanges(PARA, SWXA, AMSA).length);
  }

  @Test
  public final void invalidExchangesFilteredOff() {
    // mix of valid and invalid exchanges
    final String exchange = EXCHANGES[0];
    final Exchanges[] exchanges = Exchanges.toExchanges(PARA, exchange, SWXA);

    assertEquals(exchange, exchanges[0].toString());
    assertEquals(1, exchanges.length);
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
