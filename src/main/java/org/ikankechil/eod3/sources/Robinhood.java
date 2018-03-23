/**
 * Robinhood.java  v0.1  3 March 2018 10:40:52 PM
 *
 * Copyright © 2018 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.Frequencies.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.eod3.sources.Robinhood.JsonElements.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Robinhood.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class Robinhood extends Source {

  private static final String                   FREQUENCY   = "/?interval=";
  private static final String                   SUFFIX      = "&bounds=regular";

  private static final Map<Frequencies, String> FREQUENCIES = new EnumMap<>(Frequencies.class);

  private static final Logger                   logger      = LoggerFactory.getLogger(Robinhood.class);

  static {
    FREQUENCIES.put(DAILY, "day&span=year");
    FREQUENCIES.put(WEEKLY, "week&span=5year");
  }

  public Robinhood() {
    super(Robinhood.class);

    // supported markets (see https://api.robinhood.com/markets/)
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }

    // Robinhood API
    // https://api.robinhood.com/
    //
    // Notes:
    // 1. incoming data is in JSON format
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url, symbol);
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
    url.append(FREQUENCY)
       .append(FREQUENCIES.get(FREQUENCIES.containsKey(frequency) ? frequency
                                                                  : DEFAULT_FREQUENCY));
    logger.debug("Frequency: {}", url);
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    url.append(SUFFIX);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {
      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>();

        for (final String line : lines) {
          for (int i = ONE; i < line.length(); ++i) {
            final int start = findNth(OPEN_BRACE, line, ONE, i) + ONE;
            if (start >= i) {
              final int end = findNth(CLOSE_BRACE, line, ONE, i = start);
              final String substring = line.substring(start, i = end);
              newLines.add(transform.transform(substring));
            }
          }
        }

        lines.clear();
        lines.addAll(newLines);
        Collections.reverse(lines);

        return lines;
      }
    };
  }

  enum JsonElements {
    BEGINS_AT(3), OPEN_PRICE(3), HIGH_PRICE(3), LOW_PRICE(3), CLOSE_PRICE(3), VOLUME(2);

    private final String name;
    private final int    offset;

    JsonElements(final int offset) {
      name = name().toLowerCase();
      this.offset = name.length() + offset;
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
        // Robinhood JSON format (after pre-processing)
        // "begins_at":"2018-03-02T00:00:00Z","open_price":"47.2000","close_price":"48.9800","high_price":"49.0500","low_price":"46.9600","volume":33310592,"session":"reg","interpolated":false
        // "begins_at":"2018-03-01T00:00:00Z","open_price":"49.5000","close_price":"47.8400","high_price":"49.7150","low_price":"47.4700","volume":36326639,"session":"reg","interpolated":false
        // "begins_at":"2018-02-28T00:00:00Z","open_price":"50.1800","close_price":"49.2900","high_price":"50.3400","low_price":"49.2800","volume":35541183,"session":"reg","interpolated":false

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
        // INTC,20180302,47.2000,49.0500,46.9600,48.9800,33310592
        // INTC,20180301,49.5000,49.7150,47.4700,47.8400,36326639
        // INTC,20180228,50.1800,50.3400,49.2800,49.2900,35541183

        // indices
        final int ds = line.indexOf(BEGINS_AT.toString())       + BEGINS_AT.offset();
        final int de = line.indexOf(DOUBLE_QUOTE, ds);
        final int os = line.indexOf(OPEN_PRICE.toString(),  de) + OPEN_PRICE.offset();
        final int oe = line.indexOf(DOUBLE_QUOTE, os);
        final int cs = line.indexOf(CLOSE_PRICE.toString(), oe) + CLOSE_PRICE.offset();
        final int ce = line.indexOf(DOUBLE_QUOTE, cs);
        final int hs = line.indexOf(HIGH_PRICE.toString(),  ce) + HIGH_PRICE.offset();
        final int he = line.indexOf(DOUBLE_QUOTE, hs);
        final int ls = line.indexOf(LOW_PRICE.toString(),   he) + LOW_PRICE.offset();
        final int le = line.indexOf(DOUBLE_QUOTE, ls);
        final int vs = line.indexOf(VOLUME.toString(),      le) + VOLUME.offset();
        final int ve = line.indexOf(COMMA, vs);

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
