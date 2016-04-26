/**
 * Netfonds.java  v0.4  5 March 2014 6:05:08 PM
 *
 * Copyright © 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;
import java.util.EnumSet;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Netfonds, a Norwegian on-line broker.
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class Netfonds extends Source {

  // Western European non-Scandinavian exchanges
  private final static EnumSet<Exchanges> EU_EXCHANGES;

  // Exchange-related constants
  private static final String             N      = ".N";
  private static final String             O      = ".O";
  private static final String             A      = ".A";
  private static final String             ST     = ".ST";
  private static final String             CPH    = ".CPH";
  private static final String             FXSX   = ".FXSX";
  private static final String             E_L    = "E-%sL.BTSE";
  private static final String             E_D    = "E-%sD.BTSE";
  private static final String             E_P    = "E-%sP.BTSE";
  private static final String             E_A    = "E-%sA.BTSE";
  private static final String             E_Z    = "E-%sZ.BTSE";
  private static final String             E_M    = "E-%sM.BTSE";
  private static final String             E_E    = "E-%sE.BTSE";

  private static final Logger             logger = LoggerFactory.getLogger(Netfonds.class);

  static {
    EU_EXCHANGES = EnumSet.of(LSE, FWB, PAR, AMS, SWX, MIB, BM);
  }

  public Netfonds() {
    super(Netfonds.class);

    // supported markets
    // Oslo Børs does not require a suffix
    exchanges.put(NYSE, N);
    exchanges.put(NASDAQ, O);
    exchanges.put(AMEX, A);
    exchanges.put(NYSEARCA, A);
    exchanges.put(OSLO, EMPTY); // suffix = OSE?
    exchanges.put(SB, ST);
    exchanges.put(KFB, CPH);
    exchanges.put(ICEX, ICEX.toString());
    exchanges.put(FX, FXSX);

    // string formats for Western European non-Scandinavian exchanges
    exchanges.put(LSE, E_L);
    exchanges.put(FWB, E_D);
    exchanges.put(PAR, E_P);
    exchanges.put(AMS, E_A);
    exchanges.put(SWX, E_Z);
    exchanges.put(MIB, E_M);
    exchanges.put(BM, E_E);

    // Notes:
    // 1. cannot specify a time window
    // 2. only data from 20001121
    // 3. FX data from 2005 (.FXSB) or 2003 (.FXSX)
    // 4. numerous exchanges supported via BATS
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    if (EU_EXCHANGES.contains(exchange)) {
      url.append(String.format(exchanges.get(exchange), symbol));
    }
    else {
      super.appendSymbolAndExchange(url, symbol, exchange);
    }
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Netfonds CSV format
        // quote_date,paper,exch,open,high,low,close,volume,value
        // 20151224,INTC,Nasdaq,35.07,35.26,34.96,34.98,5514034,193552193
        // 20151223,INTC,Nasdaq,34.95,35.05,34.78,35.00,10840917,378964839
        // 20151222,INTC,Nasdaq,34.36,34.77,34.27,34.73,16497996,571655863

        // locate last comma
        final int last = findNthLast(COMMA, line, ONE);
        // locate third comma
        final int third = findNth(COMMA, line, THREE, EIGHT);
        final char[] characters = new char[symbol.length() + EIGHT + (last - third) + ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy date
        i = getChars(line, ZERO, EIGHT, characters, ++i);
        // copy OHLCV
        line.getChars(third, last, characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
