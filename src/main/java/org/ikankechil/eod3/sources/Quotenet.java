/**
 * Quotenet.java  v0.1  16 July 2016 9:28:39 pm
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.sources.Exchanges.*;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing quotenet.com and finanzen.at.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class Quotenet extends Source {
  // TODO issues:
  // 1. price history rendered in HTML by AJAX after a delay

  private static final String NAS    = "NAS";
  private static final String BTT    = "BTT";
  private static final String FSE    = "FSE";
  private static final String ASX_   = "ASX";

  private static final Logger logger = LoggerFactory.getLogger(Quotenet.class);

  public static void main(final String[] args) throws Exception {
    final TextReader reader = new TextReader();
    final List<String> lines = reader.read(new URL("http://www.quotenet.com/stocks/historical-prices/A/NYSE/1.1.2002_5.12.2016"));
    for (final String line : lines) {
      System.out.println(line);
    }
  }

  public Quotenet() {
    super("http://www.quotenet.com/stocks/historical-prices/");

    // supported markets
    exchanges.put(NYSE, NYSE.toString());
    exchanges.put(NASDAQ, NAS);
    exchanges.put(AMEX, BTT);
    exchanges.put(FWB, FSE);
    exchanges.put(AEX, ASX_);
    exchanges.put(SWX, SWX.toString());

    // http://www.quotenet.com/stocks/historical-prices/A/BTT/24.1.2002_24.6.2016
    // http://www.quotenet.com/stocks/historical-prices/A/NYSE/24.1.2002_24.6.2016
    // http://www.quotenet.com/currencies/historical-prices/EUR-USD
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url, symbol);
    url.append(SLASH);
    appendExchange(url, exchange);
    url.append(SLASH);
  }

  @Override
  void appendStartAndEndDates(final StringBuilder url,
                              final Calendar start,
                              final Calendar end) {
    appendStartDate(url, start);
    url.append(UNDERSCORE);
    appendEndDate(url, end);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(start.get(DATE)).append(DOT)
       .append(start.get(MONTH) + ONE).append(DOT)
       .append(start.get(YEAR));
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(end.get(DATE)).append(DOT)
       .append(end.get(MONTH) + ONE).append(DOT)
       .append(end.get(YEAR));
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
        return line;
      }
    };
  }

}
