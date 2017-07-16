/**
 * Tiingo.java  v0.1  15 July 2017 9:59:53 pm
 *
 * Copyright © 2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.eod3.sources.Tiingo.Elements.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;

/**
 * A <code>Source</code> representing Tiingo.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class Tiingo extends Source {

  private final String        authenticationToken;
  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  // Authentication token / API key property: org.ikankechil.eod3.sources.Tiingo.authToken
  private static final String AUTH_TOKEN    = System.getProperty(Tiingo.class.getName() + ".authToken");

  // Date-related URL parameters
  private static final String START_DATE    = "?startDate=";
  private static final String END_DATE      = "&endDate=";

  private static final String PRICES        = "/prices";
  private static final String TOKEN         = "&token=";

  public Tiingo() {
    this(null);
  }

  public Tiingo(final String authenticationToken) {
    super(Tiingo.class);

    this.authenticationToken = (authenticationToken == null || authenticationToken.isEmpty()) ?
                               AUTH_TOKEN :
                               authenticationToken;

    // supported markets (see https://api.tiingo.com/docs/tiingo/overview)
    for (final Exchanges exchange : new Exchanges[] { NYSE, NASDAQ, AMEX, ARCA, SSE, SZSE }) {
      exchanges.put(exchange, EMPTY);
    }
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url, symbol);
    url.append(PRICES);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(urlDateFormat.format(start.getTime()));
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(urlDateFormat.format(end.getTime()));
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    if (authenticationToken != null && !authenticationToken.isEmpty()) {
      url.append(TOKEN).append(authenticationToken);
    }
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {

      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>();

        for (final String line : lines) {
          for (int i = ZERO; i < line.length(); ++i) {
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

  enum Elements {
    DATE(3), OPEN(2), HIGH(2), LOW(2), CLOSE(2), VOLUME(2);

    private final String name;
    private final int    offset;

    Elements(final int offset) {
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
        // Tiingo JSON format (after pre-processing)
        // {"date":"1980-03-17T00:00:00.000Z","close":62.5,"high":63.5,"low":62.5,"open":62.5,"volume":56900,"adjClose":0.2190423356,"adjHigh":0.2225470129,"adjLow":0.2190423356,"adjOpen":0.2190423356,"adjVolume":199,"divCash":0.0,"splitFactor":1.0},
        // {"date":"1980-03-18T00:00:00.000Z","close":62.0,"high":63.0,"low":62.0,"open":62.5,"volume":88900,"adjClose":0.2172899969,"adjHigh":0.2207946743,"adjLow":0.2172899969,"adjOpen":0.2190423356,"adjVolume":311,"divCash":0.0,"splitFactor":1.0},
        // {"date":"1980-03-19T00:00:00.000Z","close":63.5,"high":64.5,"low":63.5,"open":63.5,"volume":96400,"adjClose":0.2225470129,"adjHigh":0.2260516903,"adjLow":0.2225470129,"adjOpen":0.2225470129,"adjVolume":337,"divCash":0.0,"splitFactor":1.0}

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
        // INTC,19800319,63.5,64.5,63.5,63.5,96400
        // INTC,19800318,62.5,63.0,62.0,62.0,88900
        // INTC,19800317,62.5,63.5,62.5,62.5,56900

        // indices
        final int ds = line.indexOf(DATE.toString())       + DATE.offset();
        final int de = line.indexOf(COMMA, ds);
        final int cs = line.indexOf(CLOSE.toString(),  de) + CLOSE.offset();
        final int ce = line.indexOf(COMMA, cs);
        final int hs = line.indexOf(HIGH.toString(),   ce) + HIGH.offset();
        final int he = line.indexOf(COMMA, hs);
        final int ls = line.indexOf(LOW.toString(),    he) + LOW.offset();
        final int le = line.indexOf(COMMA, ls);
        final int os = line.indexOf(OPEN.toString(),   le) + OPEN.offset();
        final int oe = line.indexOf(COMMA, os);
        final int vs = line.indexOf(VOLUME.toString(), oe) + VOLUME.offset();
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
