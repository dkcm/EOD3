/**
 * AlphaVantage.java  v0.1  23 March 2017 4:58:12 pm
 *
 * Copyright © 2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.AlphaVantage.Elements.*;
import static org.ikankechil.eod3.sources.Exchanges.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Alpha Vantage.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class AlphaVantage extends Source {

  private final String        apiKey;

  // URL parameters
  private static final String FREQUENCY = "&function=TIME_SERIES_";
  private static final String API_KEY_  = "&apikey=";

  // API key property: org.ikankechil.eod3.sources.AlphaVantage.apiKey
  private static final String API_KEY   = System.getProperty(AlphaVantage.class.getName() + ".apiKey", "demo");

  private static final Logger logger    = LoggerFactory.getLogger(AlphaVantage.class);

  public AlphaVantage() {
    this(null);
  }

  public AlphaVantage(final String apiKey) {
    super(AlphaVantage.class);

    this.apiKey = (apiKey == null || apiKey.isEmpty()) ? API_KEY : apiKey;

    // supported markets
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }

    // Alpha Vantage API
    // http://www.alphavantage.co/documentation/
    //
    // Notes:
    // 1. incoming data is in JSON format
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
    url.append(FREQUENCY).append((frequency != null) ? frequency
                                                     : DEFAULT_FREQUENCY); // default to daily
    logger.debug("Frequency: {}", url);
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    appendApiKey(url);
  }

  private final void appendApiKey(final StringBuilder url) {
    if (apiKey != null && !apiKey.isEmpty()) {
      url.append(API_KEY_).append(apiKey);
    }
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {

      private final String dataStartTag = OPEN.toString();

      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>(lines.size() >> ONE);

        final ListIterator<String> iterator = lines.listIterator();
        while (iterator.hasNext()) {
          if (iterator.next().contains(dataStartTag)) {
            iterator.previous();
            final StringBuilder newLine = new StringBuilder(iterator.previous()); // date
            iterator.next();
            newLine.append(iterator.next()); // open
            newLine.append(iterator.next()); // high
            newLine.append(iterator.next()); // low
            newLine.append(iterator.next()); // close
            newLine.append(iterator.next()); // volume

            newLines.add(transform.transform(newLine.toString()));
          }
        }

        lines.clear();
        lines.addAll(newLines);
        logger.info("Transformation complete");

        return lines;
      }
    };
  }

  enum Elements {
    OPEN, HIGH, LOW, CLOSE, VOLUME;

    private final String name;
    private final int    offset;

    Elements() {
      final StringBuilder nameBuilder = new StringBuilder();
      nameBuilder.append(ordinal() + ONE).append(DOT).append(SPACE).append(name().toLowerCase());

      name = nameBuilder.toString();
      offset = name.length() + FOUR;
    }

    @Override
    public String toString() {
      return name;
    }

    public int offset() {
      return offset;
    }
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Alpha Vantage JSON format (after pre-processing)
        //        "2017-03-24": {            "1. open": "35.65",            "2. high": "35.73",            "3. low": "35.12",            "4. close": "35.16",            "5. volume": "22030600"
        //        "2017-03-23": {            "1. open": "35.49",            "2. high": "35.49",            "3. low": "35.02",            "4. close": "35.27",            "5. volume": "20529600"
        //        "2017-03-22": {            "1. open": "35.22",            "2. high": "35.46",            "3. low": "35.00",            "4. close": "35.37",            "5. volume": "18704600"

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
        // INTC,20170324,35.65,35.73,35.12,35.16,22030600
        // INTC,20170323,35.49,35.49,35.02,35.27,20529600
        // INTC,20170322,35.22,35.46,35.00,35.37,18704600

        // indices
        final int ds = line.indexOf(DOUBLE_QUOTE) + ONE;
        final int de = line.indexOf(DOUBLE_QUOTE, ds);
        final int os = line.indexOf(OPEN.toString())             + OPEN.offset();
        final int oe = line.indexOf(DOUBLE_QUOTE, os);
        final int hs = line.indexOf(HIGH.toString(),   oe + TWO) + HIGH.offset();
        final int he = line.indexOf(DOUBLE_QUOTE, hs);
        final int ls = line.indexOf(LOW.toString(),    he + TWO) + LOW.offset();
        final int le = line.indexOf(DOUBLE_QUOTE, ls);
        final int cs = line.indexOf(CLOSE.toString(),  le + TWO) + CLOSE.offset();
        final int ce = line.indexOf(DOUBLE_QUOTE, cs);
        final int vs = line.indexOf(VOLUME.toString(), ce + TWO) + VOLUME.offset();
        final int ve = line.indexOf(DOUBLE_QUOTE, vs);

        // concatenate segments
        final StringBuilder builder = new StringBuilder(symbol).append(COMMA);
        final String date = line.substring(ds, de);
        builder.append(date.substring(ZERO, FOUR))  // year
               .append(date.substring(FIVE, SEVEN)) // month
               .append(date.substring(EIGHT, TEN))  // date
               .append(COMMA);
        builder.append(line.substring(os, oe))      // open
               .append(COMMA);
        builder.append(line.substring(hs, he))      // high
               .append(COMMA);
        builder.append(line.substring(ls, le))      // low
               .append(COMMA);
        builder.append(line.substring(cs, ce))      // close
               .append(COMMA);
        builder.append(line.substring(vs, ve));     // volume

        return builder.toString();
      }
    };
  }

}
