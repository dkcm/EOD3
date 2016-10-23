/**
 * Finam.java  v0.2  16 December 2014 2:03:09 PM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.Frequencies.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Finam.ru, a Russian on-line finance
 * portal.
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
class Finam extends Source {

  private static final Map<Frequencies, Integer> FREQUENCIES = new EnumMap<>(Frequencies.class);

  // Date-related URL parameters
  private static final String                    START_MONTH = "&mf=";
  private static final String                    START_DATE  = "&df=";
  private static final String                    START_YEAR  = "&yf=";
  private static final String                    END_MONTH   = "&mt=";
  private static final String                    END_DATE    = "&dt=";
  private static final String                    END_YEAR    = "&yt=";
  private static final String                    FREQUENCY   = "&p=";

  // Exchange-related constants
  private static final String                    US1         = "US1.";
  private static final String                    US2         = "US2.";

  private static final Logger                    logger      = LoggerFactory.getLogger(Finam.class);

  public static void main(final String... arguments) throws IOException {
    final Finam finam = new Finam();
    final TextReader reader = new TextReader();
    final String symbol = "AAPL";
    final Calendar start = Calendar.getInstance();
    start.set(2015, Calendar.SEPTEMBER, 20);
    final Calendar end = Calendar.getInstance();
    final List<String> strings = reader.read(finam.url(symbol, NASDAQ, start, end, MONTHLY));
    System.out.println(strings);
    final TextTransformer transformer = finam.newTransformer(finam.newTransform(symbol));
    transformer.transform(strings);
    for (final String string : strings) {
      System.out.println(string);
    }
//    final TextWriter writer = new TextWriter();
//    writer.write(strings, new File("IBM.csv"));
  }

  static {
    // daily = 8, weekly = 9 / none, monthly = 10
    FREQUENCIES.put(DAILY, EIGHT);
    FREQUENCIES.put(WEEKLY, NINE);
    FREQUENCIES.put(MONTHLY, TEN);
  }

  public Finam() {
    super("http://195.128.78.52/ohlcv.csv?market=25&em=20569&dtf=1&tmf=1&sep=1&sep2=1&datf=5&at=1&cn=");

    // supported markets
    // MOEX does not require a suffix
    exchanges.put(NYSE, US1);
    exchanges.put(NASDAQ, US2);
    exchanges.put(MOEX, EMPTY);
    exchanges.put(FX, EMPTY);

    // http://www.finam.ru/scripts/export.js
    // http://195.128.78.52/
    // http://195.128.78.52/AFLT_160711_160711.csv?market=1&em=29&code=AFLT&apply=0&df=11&mf=6&yf=2016&from=11.07.2016&dt=11&mt=6&yt=2016&to=11.07.2016&p=9&f=AFLT_160711_160711&e=.csv&cn=AFLT&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=5&at=1
    // http://195.128.78.52/US2.AAPL_951216_141216.csv?market=25&em=20569&code=US2.AAPL&df=16&mf=11&yf=1995&from=16.12.1995&dt=16&mt=11&yt=2014&to=16.12.2014&p=8&f=US2.AAPL_951216_141216&e=.csv&cn=US2.AAPL&dtf=1&tmf=1&MSOR=1&mstimever=0&sep=1&sep2=1&datf=1&at=1
    // http://195.128.78.52/ohlcv.csv?market=25&em=20569&dtf=1&tmf=1&sep=1&sep2=1&datf=5&at=1&cn=US2.AAPL&df=1&mf=0&yf=1970&dt=13&mt=5&yt=2015&p=10

    // # Load file from finam if haven't ever loaded
    // rdict = dict(d='d',
    //              market=ticker.data['market'], # 1 = MOEX
    //              cn=ticker.symbol,
    //              em=ticker.data['id'],
    //              p=p,
    //              yf=dfrom.year,
    //              mf=dfrom.month-1, # In service month's numbers starts from 0
    //              df=dfrom.day,
    //              yt=dto.year,
    //              mt=dto.month-1,
    //              dt=dto.day,
    //              dtf=1,  # Equals %Y%m%d
    //              tmf=1,  # Equals %M%H%S
    //              MSOR=0, # Begin of candles
    //              sep=3,  # Semicolon ';'
    //              sep2=1, # Not set a digit position delimiter
    //              datf=5, # Format: DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL
    //              f=fname,
    //              e=fext,
    //              at=0, # No header
    //              )

    // datf
    // var aDataFormatStrs=new Array(
    // 'TICKER, PER, DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL',
    // 'TICKER, PER, DATE, TIME, OPEN, HIGH, LOW, CLOSE',
    // 'TICKER, PER, DATE, TIME, CLOSE, VOL',
    // 'TICKER, PER, DATE, TIME, CLOSE',
    // 'DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL',
    // 'TICKER, PER, DATE, TIME, LAST, VOL',
    // 'TICKER, DATE, TIME, LAST, VOL',
    // 'TICKER, DATE, TIME, LAST',
    // 'DATE, TIME, LAST, VOL',
    // 'DATE, TIME, LAST',
    // 'DATE, TIME, LAST, VOL, ID');
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
    url.append(START_DATE).append(start.get(DATE))
       .append(START_MONTH).append(start.get(MONTH))
       .append(START_YEAR).append(start.get(YEAR));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(end.get(DATE))
       .append(END_MONTH).append(end.get(MONTH))
       .append(END_YEAR).append(end.get(YEAR));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null) {
      url.append(FREQUENCY).append(FREQUENCIES.get(frequency).intValue());
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    // sort in descending / reverse chronological order
    return new TextTransformer(transform, ONE, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Finam CSV format
        // <DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>
        // 20150608,000000,128.9400000,129.2100000,126.8400000,127.7700000,3399563
        // 20150609,000000,126.7200000,128.0700000,125.6200000,127.4100000,3998136
        // 20150610,000000,127.9800000,129.3400000,127.8500000,128.9100000,2674926
        // 20150611,000000,129.1300000,130.1700000,128.4800000,128.5900000,2413383
        // 20150612,000000,128.1700000,128.3200000,127.1200000,127.1500000,2375203

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final char[] characters = new char[line.length() - SIX + symbol.length()];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy date
        i = getChars(line, ZERO, EIGHT, characters, ++i);
        // copy rest of line
        line.getChars(FIFTEEN, line.length(), characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
