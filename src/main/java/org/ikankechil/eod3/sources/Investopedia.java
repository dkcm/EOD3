/**
 * Investopedia.java  v0.2  12 January 2016 9:56:23 pm
 *
 * Copyright © 2016-2019 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Investopedia.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class Investopedia extends Source {

  private final DateFormat                 dateFormat         = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

  private static final String              DIVIDEND           = "Dividend";

  // Date-related URL parameters
  private static final String              START_DATE         = "&StartDate=";
  private static final String              END_DATE           = "&EndDate=";
  private static final String              FREQUENCY          = "&Timeframe=";

  private static final Map<String, String> MONTHS             = new HashMap<>();
  private static final String              DEFAULT_START_DATE = "09/01/2010";

  private static final Logger              logger             = LoggerFactory.getLogger(Investopedia.class);

  static {
    MONTHS.put("Jan", "01");
    MONTHS.put("Feb", "02");
    MONTHS.put("Mar", "03");
    MONTHS.put("Apr", "04");
    MONTHS.put("May", "05");
    MONTHS.put("Jun", "06");
    MONTHS.put("Jul", "07");
    MONTHS.put("Aug", "08");
    MONTHS.put("Sep", "09");
    MONTHS.put("Oct", "10");
    MONTHS.put("Nov", "11");
    MONTHS.put("Dec", "12");
  }

  public Investopedia() {
    super(Investopedia.class);

    // supported markets
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }

    // Investopedia API
    // e.g.
    // https://www.investopedia.com/markets/api/partial/historical/?Symbol=AAPL&Type=Historical+Prices&Timeframe=Weekly&StartDate=Jan+27%2C+2019&EndDate=Feb+08%2C+2019
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url, symbol);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &StartDate=01/01/2010
    url.append(START_DATE).append(dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // &EndDate=01/12/2016
    url.append(END_DATE).append(dateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    // start date defaults to 1 September 2010
    url.append(START_DATE).append(DEFAULT_START_DATE);
    logger.debug("Default start date appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null) {
      // &Timeframe=Daily
      final String f = frequency.toString();
      url.append(FREQUENCY).append(f.charAt(ZERO) + f.toLowerCase().substring(ONE));
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {

      private static final String DATA_START_TAG = "<td class=\"date\">";
      private static final String DATA_END_TAG   = "</tr>";
      private static final String CELL_END_TAG   = "</td>";

      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>(lines.size() >> THREE);

        final Iterator<String> linesIterator = lines.iterator();
        while (linesIterator.hasNext()) {
          String line = linesIterator.next();

          // data block
          if (line.contains(DATA_START_TAG)) {
            final StringBuilder newLine = new StringBuilder(extract(line)).append(COMMA); // date
            while (linesIterator.hasNext()) {
              line = linesIterator.next();

              if (line.contains(DIVIDEND)) { // early termination
                break;
              }
              else if (line.contains(DATA_END_TAG)) { // EOL
                final String transformedLine = transform.transform(newLine.toString());
                newLines.add(transformedLine);
                break;
              }

              newLine.append(extract(line)).append(COMMA); // individual values
            }
          }
        }

        lines.clear();
        lines.addAll(newLines);
        logger.info("Transformation complete");

        return lines;
      }

      private String extract(final String line) {
        String value = EMPTY;
        final int valueEnd = line.lastIndexOf(CELL_END_TAG);
        if (valueEnd > -ONE) {
          final int valueStart = ONE + line.lastIndexOf(MORE_THAN, valueEnd);
          if ((valueStart > ZERO) && (valueStart < valueEnd)) {
            value = line.substring(valueStart, valueEnd);
          }
        }
        return value;
      }

    };
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Investopedia HTML format
        // <th class="date">Date</th><th class="num">Open</th><th class="num">High</th><th class="num">Low</th><th class="num">Adj. Close</th><th class="num">Volume</th>
        // <td class="date">Feb 08, 2019</td><td class="num">168.99</td><td class="num">170.66</td><td class="num">168.42</td><td class="num">170.41</td><td class="num">23,819,966</td>

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        // concatenate segments
        final StringBuilder builder = new StringBuilder(symbol).append(COMMA);
        final int dateEnd = TWELVE;
        builder.append(line.substring(EIGHT, dateEnd))          // year
               .append(MONTHS.get(line.substring(ZERO, THREE))) // month
               .append(line.substring(FOUR, SIX));              // date

        final int volumeStart = ONE + findNth(COMMA, line, FIVE, dateEnd);
        builder.append(line.substring(dateEnd, volumeStart));   // OHLC
        builder.append(line.substring(volumeStart)              // volume
                           .replaceAll(String.valueOf(COMMA), EMPTY));

        return builder.toString();
      }
    };
  }

}
