/**
 * GlobalView.java  v0.1  11 October 2016 1:14:40 am
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.Frequencies.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Global-View.com.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class GlobalView extends Source {

  private final DateFormat                      dateFormat  = new SimpleDateFormat("M/d/yyyy", Locale.US);
  private final String                          now;

  // Date-related URL parameters
  private static final String                   START_DATE  = "&start_date=";
  private static final String                   END_DATE    = "&stop_date=";

  private static final Map<Frequencies, String> FREQUENCIES = new EnumMap<>(Frequencies.class);

  private static final Map<String, String>      SYMBOLS     = new HashMap<>();

  static final Logger                           logger      = LoggerFactory.getLogger(GlobalView.class);

  static {
    // frequencies
    FREQUENCIES.put(DAILY, "&Submit=Get Daily Stats");
    FREQUENCIES.put(WEEKLY, "&Submit=Get Weekly Stats");
    FREQUENCIES.put(MONTHLY, "&Submit=Get Monthly Stats");

    // FX symbols
    final String format = "CLOSE_%1$d=ON&HIGH_%1$d=ON&LOW_%1$d=ON";
    final String[] symbols = { "EURUSD", "USDJPY", "USDCHF", "GBPUSD", "USDCAD",
                               "EURGBP", "EURJPY", "EURCHF", "AUDUSD", "GBPJPY",
                               "CHFJPY", "GBPCHF", "NZDUSD" };
    for (int i = ZERO; i < symbols.length; ) {
      SYMBOLS.put(symbols[i], String.format(format, ++i));
    }
  }

  public GlobalView() {
    super(GlobalView.class);

    // supported exchanges
    // FX does not require a suffix
    exchanges.put(FX, EMPTY);

    now = dateFormat.format(new Date());
  }

  @Override
  void appendSymbol(final StringBuilder url, final String symbol) {
    final String s = SYMBOLS.get(symbol);
    if (s != null) {
      url.append(s);
    }
  }

  @Override
  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    // do nothing
    logger.debug(UNSUPPORTED);
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
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    appendStartDate(url, DEFAULT_START);
    url.append(END_DATE).append(now);
    logger.debug("Default start and end dates appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    url.append(FREQUENCIES.get((frequency != null) ? frequency
                                                   : DEFAULT_FREQUENCY)); // default to daily
    logger.debug("Frequency: {}", url);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, EIGHT, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Results,EUR/USD Close,EUR/USD High,EUR/USD Low
        // Average,  1.269,  1.275,  1.264
        // Minimum,   1.048,   1.059,   1.046
        // Maximum,   1.483,   1.494,   1.480
        // Std,   0.112,   0.112,   0.112
        // # In Calc, 1565, 1565, 1565
        //
        // Date,EUR/USD Close,EUR/USD High,EUR/USD Low
        // 2010-10-11,1.3875,1.4011,1.3867
        // 2010-10-12,1.3914,1.3932,1.3774
        // 2010-10-13,1.3962,1.4001,1.3913
        // 2010-10-14,1.4076,1.4121,1.3956
        // 2010-10-15,1.3969,1.4157,1.3938
        //
        // 2006-06-30,0,0,0
        // 2006-07-03,0,0,0
        // 2006-07-04,0.61,0.6115,0.6056
        // 2006-07-05,0.6041,0.6119,0.6028
        // 2006-07-06,0.6047,0.6064,0.6021
        // 2006-07-07,0.6109,0.6118,0.6028
        // 2006-07-10,0.6104,0.6154,0.6095
        // 2006-07-11,0.6165,0.6173,0.6095
        // 2006-07-12,0.6157,0.6189,0.6131
        // 2006-07-13,0.6184,0.62,0.6145
        // 2006-07-14,0.6206,0.6214,0.6146
        // 2006-07-17,0.621,0.6226,0.6185

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[symbol.length() + line.length()];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;

        // copy date
        i = getChars(line, ZERO, FOUR, characters, ++i);  // year
        i = getChars(line, FIVE, SEVEN, characters, i);   // month
        i = getChars(line, EIGHT, ELEVEN, characters, i); // date

        // copy OHLCV
        final int highPosition = findNth(COMMA, line, ONE, ELEVEN);
        i = getChars(line, highPosition, line.length(), characters, i); // high and low
        i = getChars(line, TEN, highPosition, characters, i);           // close

        return String.valueOf(characters);
      }
    };
  }

}
