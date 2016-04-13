/**
 * GoogleFinance.java v0.4  21 December 2013 1:33:30 AM
 *
 * Copyright � 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.eod3.sources.GoogleFinance.DateFormats.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Google Finance.
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class GoogleFinance extends Source {

  // Date-related URL parameters
  private static final String START_DATE        = "&startdate=";
  private static final String END_DATE          = "&enddate=";
//  private static final String FREQUENCY         = "&histperiod=";
  private static final String DEFAULT_START_STR = URL.dateFormat.format(DEFAULT_START.getTime());

  // Exchange-related constants
  private static final String LON               = "LON:";
  private static final String HKG               = "HKG:";
  private static final String SHA               = "SHA:";
  private static final String SHE               = "SHE:";
  private static final String TYO               = "TYO:";
  private static final String BOM               = "BOM:";
  private static final String TSE_              = "TSE:";
  private static final String FRA               = "FRA:";
  private static final String EPA               = "EPA:";
  private static final String WSE               = "WSE:";
  private static final String BIT               = "BIT:";
  private static final String TPE               = "TPE:";
  private static final String STO               = "STO:";
  private static final String CPH               = "CPH:";
  private static final String BVMF              = "BVMF:";

  static final Logger         logger            = LoggerFactory.getLogger(GoogleFinance.class);

  public GoogleFinance() {
    super("http://www.google.com/finance/historical?output=csv&q=");

    // supported markets (http://www.google.com/intl/en/googlefinance/disclaimer/)
    // NYSE, NASDAQ, AMEX and NYSEARCA do not require prefixes
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, NYSEARCA)) {
      exchanges.put(exchange, EMPTY);
    }
    for (final Exchanges exchange : EnumSet.of(AMS, SWX, SGX, NSE, KRX, ASX, BCBA, BMV)) {
      exchanges.put(exchange, exchange.toString() + COLON);
    }

    exchanges.put(LSE, LON);
    exchanges.put(FWB, FRA);
    exchanges.put(PAR, EPA);
    exchanges.put(MIB, BIT);
    exchanges.put(HKSE, HKG);
    exchanges.put(SSE, SHA);
    exchanges.put(SZSE, SHE);
    exchanges.put(TSE, TYO);
    exchanges.put(BSE, BOM);
    exchanges.put(TWSE, TPE);
    exchanges.put(TSX, TSE_);
    exchanges.put(GPW, WSE);
    exchanges.put(SB, STO);
    exchanges.put(KFB, CPH);
    exchanges.put(BOVESPA, BVMF);

    // Google Finance (max. 4000 lines unless with explicit start date)
    // http://www.google.com/finance/historical?q=<Stock Symbol>
    //                                         &histperiod=<Frequency>
    //                                         &startdate=<dd>+<Mmm>+<yyyy>
    //                                         &enddate=<dd>+<Mmm>+<yyyy>
    //                                         &output=csv
    // e.g.
    // http://www.google.com/finance/historical?q=MSFT&histperiod=daily&startdate=02+Jan+2010&enddate=28+DEC+2010&output=csv
    // http://www.google.com/finance/historical?q=ASX%3AACR&ei=hUrIVNHTE8yDqQH_toFg
    //
    // Notes:
    // 1. No FX historical data download
  }

  enum DateFormats {
    URL("dd+MMM+yyyy"),
    INPUT("d-MMM-yy"),
    OUTPUT("yyyyMMdd");

    final DateFormat dateFormat;

    DateFormats(final String pattern) {
      dateFormat = new SimpleDateFormat(pattern, Locale.US);
    }
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    // prefix exchange
    appendExchange(url, exchange);
    appendSymbol(url, symbol);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(URL.dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(URL.dateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    // start date defaults to 1 January 1970
    url.append(START_DATE).append(DEFAULT_START_STR);
    logger.debug("Default start dates appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
//    if (frequency != null) {
//      url.append(FREQUENCY).append(frequency);
//      logger.debug("Frequency: {}", url);
//    }
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Google Finance CSV format
        // Date,Open,High,Low,Close,Volume
        // 2-Dec-13,25.15,25.35,25.04,25.06,55383979

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
        String result = EMPTY;

        final ParsePosition pos = new ParsePosition(ZERO);
        final Date date = INPUT.dateFormat.parse(line, pos);
        if (date != null) {
          final char[] characters = new char[symbol.length() + NINE + (line.length() - pos.getIndex())];
          // set row name
          int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
          characters[i] = COMMA;
          // reformat and copy date
          OUTPUT.dateFormat.format(date) // d-MMM-yy -> yyyyMMdd
                           .getChars(ZERO, EIGHT, characters, ++i);
          // copy rest of line
          line.getChars(pos.getIndex(), line.length(), characters, i + EIGHT);
          result = String.valueOf(characters);
        }
        else {
          logger.warn("Invalid date: {}", line);
        }
//        try {
//          // reformat date
//          String date = OUTPUT.dateFormat.format(INPUT.dateFormat.parse(line)); // d-MMM-yy -> yyyyMMdd
//          // set row name
//          String lineSansDate = line.substring(line.indexOf(COMMA));
//          StringBuilder builder = new StringBuilder(symbol).append(COMMA).append(date).append(lineSansDate);
//
//          result = builder.toString();
//          logger.trace("Transformed line: {}", result);
//        }
//        catch (ParseException pE) {
//          logger.warn("Invalid date: {}", line, pE);
//        }

        return result;
      }
    };
  }

}
