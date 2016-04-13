/**
 * FinancialContent.java v0.1 15 January 2016 11:36:44 AM
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.eod3.sources.FinancialContent.DateFormats.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing FinancialContent.com.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class FinancialContent extends Source {

  private final Calendar      today;

  // Date-related URL parameters
  private static final String END_YEAR       = "&Year=";
  private static final String END_MONTH      = "&Month=";
  private static final String MONTHS         = "&Range=";

  private static final int    MONTHS_IN_YEAR = 12;

  static final Logger         logger         = LoggerFactory.getLogger(FinancialContent.class);

  public FinancialContent() {
    this("http://markets.financialcontent.com/stocks/action/gethistoricaldata?Symbol=");

    // FinancialContent API
    // e.g.
    // http://markets.financialcontent.com/stocks/action/gethistoricaldata?Month=12&Year=2015&Symbol=sco&Range=12
  }

  FinancialContent(final String base) {
    super(base);

    // supported markets
    // FX, NYSE, NASDAQ, AMEX and NYSEARCA do not require suffices
    // Currency pairs need to be separated by a hyphen ('-')
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(NYSEARCA, EMPTY);
    exchanges.put(TSX, EMPTY);
    exchanges.put(FX, EMPTY);

    today = Calendar.getInstance();
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendSymbol(url,
                 (exchange == FX) ? new StringBuilder(symbol).insert(THREE, HYPHEN).toString() // currency pair separated by a hyphen ('-')
                                  : symbol);
  }

  @Override
  void appendStartAndEndDates(final StringBuilder url,
                              final Calendar start,
                              final Calendar end) {
    final int startMonth = start.get(MONTH) + ONE;
    final int startYear = start.get(YEAR);
    final int endMonth = end.get(MONTH) + ONE;
    final int endYear = end.get(YEAR);
    final int months = (endYear - startYear) * MONTHS_IN_YEAR + (endMonth - startMonth) + ONE;
    // e.g July 2000 - March 2001: (2001 - 2000) * 12 + (3 - 7) + 1

    url.append(END_MONTH).append(endMonth)
       .append(END_YEAR).append(endYear)
       .append(MONTHS).append(months);
    logger.debug("Date: {}", url);
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
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    appendStartAndEndDates(url, DEFAULT_START, today);
    logger.debug("Default start and end dates appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  enum DateFormats {
    INPUT("MM/dd/yy"),
    OUTPUT("yyyyMMdd");

    final DateFormat dateFormat;

    DateFormats(final String pattern) {
      dateFormat = new SimpleDateFormat(pattern, Locale.US);
    }
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // FinancialContent CSV format
        // Symbol,Date,Open,High,Low,Close,Volume,Change,% Change
        // SCO,12/31/15,,133.64,133.64,133.64,0,-2.35,-1.73%
        // SCO,12/30/15,135.40,138.28,134.67,135.99,340482,6.93,5.37%
        // SCO,12/29/15,131.71,131.75,128.50,129.06,388222,-8.16,-5.95%
        // SCO,12/28/15,134.54,137.25,134.17,137.22,265108,8.35,6.48%
        // SCO,12/24/15,,128.87,128.87,128.87,0,-1.54,-1.18%

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        String result = EMPTY;

        final int onePastFirstComma = ONE + findNth(COMMA, line, ONE, ZERO);
        if (onePastFirstComma > ZERO) {
          final ParsePosition pos = new ParsePosition(onePastFirstComma);
          final Date date = INPUT.dateFormat.parse(line, pos);
          if (date != null) {
            final int volumeComma = findNth(COMMA, line, SIX, pos.getIndex());
            final char[] characters = new char[volumeComma - onePastFirstComma + ONE + symbol.length()];
            // set row name, removing any hyphens (if any)
            int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
            characters[i] = COMMA;
            // reformat and copy date
            OUTPUT.dateFormat.format(date) // MM/dd/yy -> yyyyMMdd
                             .getChars(ZERO, EIGHT, characters, ++i);
            // copy rest of line
            line.getChars(pos.getIndex(), volumeComma, characters, i + EIGHT);
            result = String.valueOf(characters);
          }
          else {
            logger.warn("Invalid date: {}", line);
          }
        }
        else {
          logger.warn("Empty line");
        }

        return result;
      }
    };
  }

}
