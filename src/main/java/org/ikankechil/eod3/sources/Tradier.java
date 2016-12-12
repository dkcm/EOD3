/**
 * Tradier.java  v0.1  31 October 2016 12:34:39 am
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Tradier.
 *
 * <p>https://developer.tradier.com/documentation/markets/get-history
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class Tradier extends Source {
  // TODO issues:
  // 1. authorisation key required as an URL connection header
  // 2. incoming data in XML or JSON formats

  private final DateFormat    dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  private static final String START_DATE = "&start=";
  private static final String END_DATE   = "&end=";
  private static final String FREQUENCY  = "&interval=";

  private static final Logger logger     = LoggerFactory.getLogger(Tradier.class);

  public static void main(final String[] args) throws IOException {
    final TextReader reader = new TextReader();
    final List<String> lines = reader.read(new URL("https://api.tradier.com/v1/markets/history?symbol=AAPL&start=2016-10-01&end=2016-10-10"));
    for (final String line : lines) {
      System.out.println(line);
    }
  }

  public Tradier() {
    super("https://api.tradier.com/v1/markets/history?symbol=");

    // Tradier API (https://developer.tradier.com/documentation/markets/get-history)
    // https://api.tradier.com/v1/markets/quotes?symbols=spy
    // https://api.tradier.com/v1/markets/history?symbol=SPY&interval=monthly
    // https://api.tradier.com/v1/markets/history?symbol=AAPL&start=2016-10-01&end=2016-10-10

    // curl -X GET "https://api.tradier.com/v1/markets/quotes?symbols=spy" \
    //      -H "Accept: application/json" \
    //      -H "Authorization: Bearer pAc18LC1QteFqGxrwX1FvqmMwTKO" \
    //      -m 30 \
    //      -v

    // supported markets (https://developer.tradier.com/documentation/reference/exchanges)
    exchanges.put(NYSE, "N");
    exchanges.put(NASDAQ, "Q");
    exchanges.put(AMEX, "A");
    exchanges.put(ARCA, "P");
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(dateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null) {
      url.append(FREQUENCY).append(frequency);
    }
  }

  @Override
  public TextReader newReader() {
    return new TextReader();
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // TODO Auto-generated method stub
        return line;
      }
    };
  }

}
