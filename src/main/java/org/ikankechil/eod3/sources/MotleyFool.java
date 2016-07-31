/**
 * MotleyFool.java  v0.4  6 June 2014 01:19:57 PM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Motley Fool.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class MotleyFool extends Source {

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  // Date-related URL parameters
  private static final String START_DATE    = "&START_DATE=";
  private static final String END_DATE      = "&END_DATE=";

  // Exchange-related constants
  private static final String US            = "US";
  private static final String UK            = "UK";

  static final Logger         logger        = LoggerFactory.getLogger(MotleyFool.class);

  public MotleyFool() {
    super(MotleyFool.class);

    // supported markets (only US and UK)
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, US);
    }

    exchanges.put(LSE, UK);

    // Notes:
    // 1. incoming data is in HTML format
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    // e.g. SYMBOL_US=NSH
    appendExchange(url, exchange);
    url.append(EQUAL);
    appendSymbol(url, symbol);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &START_DATE=2014-06-01
    url.append(START_DATE).append(urlDateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // &END_DATE=2014-06-03
    url.append(END_DATE).append(urlDateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    // start date defaults to 1 January 1970
    appendStartDate(url, DEFAULT_START);
    logger.debug("Default start and end dates appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {

      private static final String DATA_START_TAG = "<div id=\"historical_quotes\">";
      private static final String DATA_END_TAG   = "</div>";
      private static final String ROW_START_TAG  = "<tr>";
      private static final String ROW_END_TAG    = "</tr>";
      private static final String CELL_END_TAG   = "</td>";

      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>(lines.size() >> THREE);

        final Iterator<String> linesIterator = lines.iterator();
        while (linesIterator.hasNext()) {
          String line = linesIterator.next();

          // data block
          if (line.contains(DATA_START_TAG)) {
            while (linesIterator.hasNext()) {
              line = linesIterator.next();

              // line
              if (line.contains(ROW_START_TAG)) {
                final StringBuilder newLine = new StringBuilder();
                while (linesIterator.hasNext()) {
                  line = linesIterator.next();

                  // individual values
                  final int valueEnd = line.lastIndexOf(CELL_END_TAG);
                  if (valueEnd > -ONE) {
                    final int valueStart = ONE + line.lastIndexOf(MORE_THAN, valueEnd);
                    if ((valueStart > ZERO) && (valueStart < valueEnd)) {
                      newLine.append(line.substring(valueStart, valueEnd))
                             .append(COMMA);
                    }
                  }
                  // EOL
                  else if (line.contains(ROW_END_TAG)) {
                    if (newLine.length() > ZERO) {
                      final String transformedLine = transform.transform(newLine.toString());
                      if (transformedLine.length() > ZERO) {
                        newLines.add(transformedLine);
                      }
                    }
                    break;
                  }
                }
              }
              // end of data block
              else if (line.contains(DATA_END_TAG)) {
                break;
              }
            }
          }
        }

        lines.clear();
        lines.addAll(newLines);
        logger.info("Transformation complete");

        return lines;
      }

    };
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {

      private static final String COMMAS_AND_WHITESPACE = "[,\\s]";

      @Override
      public String transform(final String line) {
        // concatenate segments
        final StringBuilder builder = new StringBuilder(symbol).append(COMMA);
        builder.append(line.substring(SIX, TEN))          // year
               .append(line.substring(ZERO, TWO))         // month
               .append(line.substring(THREE, FIVE));      // date

        final int volumeStart = ONE + findNth(COMMA, line, FIVE, TEN);
        builder.append(line.substring(TEN, volumeStart)); // OHLC
        builder.append(line.substring(volumeStart)        // volume
                           .replaceAll(COMMAS_AND_WHITESPACE, EMPTY));

        return builder.toString();
      }

    };
  }

}
