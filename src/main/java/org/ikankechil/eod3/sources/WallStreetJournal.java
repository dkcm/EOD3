/**
 * WallStreetJournal.java v0.4  14 May 2014 11:49:20 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.eod3.sources.WallStreetJournal.Elements.*;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing the Wall Street Journal.
 *
 * @author Daniel Kuan
 * @version 0.4
 */
class WallStreetJournal extends Source {
  // TODO Enhancements
  // 1. v1.0 migrate from dylan to ajax API

  public static void main(final String... args) throws IOException {
    final URL url = new URL("http://quotes.wsj.com/ajax/historicalprices/7/GLE?exchange=XPAR&country=FR&startDate=05/01/2005&endDate=12/31/2015");
    final List<String> lines = new TextReader().read(url);
    for (final String line : lines) {
      logger.info(line);
    }
  }

  private final DateFormat    urlDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

  // Date-related URL parameters
  private static final String START_DATE    = "&startDate=";
  private static final String END_DATE      = "&endDate=";
  private static final String FREQUENCY     = "&duration=P1";

  // Country code
  private static final String COUNTRY_CODE  = "&countrycode=";

  // Exchange-related constants
  private static final String US            = "US";
  private static final String UK            = "UK";
  private static final String DE            = "DE";
  private static final String FR            = "FR";
  private static final String SG            = "SG";
  private static final String HK            = "HK";
  private static final String JP            = "JP";
  private static final String IN            = "IN";
  private static final String AU            = "AU";
  private static final String CA            = "CA";
  private static final String TW            = "TW";
  private static final String IT            = "IT";
  private static final String PL            = "PL";
  private static final String NO            = "NO";
  private static final String SE            = "SE";
  private static final String DK            = "DK";

  static final Logger         logger        = LoggerFactory.getLogger(WallStreetJournal.class);

  public WallStreetJournal() {
    super(WallStreetJournal.class);

    // supported markets
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, NYSEARCA, FX)) {
      exchanges.put(exchange, US);
    }

    exchanges.put(LSE, UK);
    exchanges.put(FWB, DE);
    exchanges.put(PAR, FR);
    exchanges.put(MIB, IT);
    exchanges.put(SGX, SG);
    exchanges.put(HKSE, HK);
    exchanges.put(TSE, JP);
    exchanges.put(NSE, IN);
    exchanges.put(TWSE, TW);
    exchanges.put(ASX, AU);
    exchanges.put(TSX, CA);
    exchanges.put(GPW, PL);
    exchanges.put(OSLO, NO);
    exchanges.put(SB, SE);
    exchanges.put(KFB, DK);

    // Notes:
    // 1. Downloads a single data point if no dates are specified
  }

  @Override
  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    // e.g. &countrycode=US
    url.append(COUNTRY_CODE);
    super.appendExchange(url, exchange);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &startDate=01-18-1970
    url.append(START_DATE).append(urlDateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // &endDate=05-14-2014
    url.append(END_DATE).append(urlDateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    appendStartDate(url, DEFAULT_START);
    appendEndDate(url, Calendar.getInstance());
    logger.debug("Default start and end dates appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if ((frequency != null) && (frequency != Frequencies.DAILY)) {
      url.append(FREQUENCY).append(Character.toUpperCase(frequency.frequency()));
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {

      private static final String DATA_START_TAG  = "<DataSeries>";
      private static final String DATA_END_TAG    = "</DataSeries>";
      private static final String ERROR_START_TAG = "<ErrorResponse>";

      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>(lines.size() >> THREE);

        final StringBuilder newLine = new StringBuilder();
        for (final String line : lines) {
          if (line.contains(DATA_START_TAG)) {
            newLine.delete(ZERO, newLine.length());
          }
          else if (line.contains(DATA_END_TAG)) {
            newLines.add(transform.transform(newLine.toString()));
          }
          else if (line.contains(ERROR_START_TAG)) {
            logger.warn("Erroneous source URL");
            break;
          }
          else {
            newLine.append(line);
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
    OPEN, HIGH, LOW, CLOSE, VOLUME, DATE;

    private final String name;
    private final int    offset;

    Elements() {
      name = name().toLowerCase();
      offset = name.length() + ONE;
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
        // WallStreetJournal XML format
        //    <id>5</id>    <open>8.85</open>    <high>8.85</high>    <low>8.63</low>    <close>8.69</close>    <volume>8550651</volume>    <date>06/11/2014</date>    <time>2014-06-11 00:00:00.246 EDT</time>    <isDividend>false</isDividend>

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
        logger.trace(line);

        // indices
        final int os = line.indexOf(OPEN.toString())                               + OPEN.offset();
        final int oe = line.indexOf(LESS_THAN, os);
        final int hs = line.indexOf(HIGH.toString(),   oe + OPEN.offset()   + TWO) + HIGH.offset();
        final int he = line.indexOf(LESS_THAN, hs);
        final int ls = line.indexOf(LOW.toString(),    he + HIGH.offset()   + TWO) + LOW.offset();
        final int le = line.indexOf(LESS_THAN, ls);
        final int cs = line.indexOf(CLOSE.toString(),  le + LOW.offset()    + TWO) + CLOSE.offset();
        final int ce = line.indexOf(LESS_THAN, cs);
        final int vs = line.indexOf(VOLUME.toString(), ce + CLOSE.offset()  + TWO) + VOLUME.offset();
        final int ve = line.indexOf(LESS_THAN, vs);
        final int ds = line.indexOf(DATE.toString(),   ve + VOLUME.offset() + TWO) + DATE.offset();
        final int de = line.indexOf(LESS_THAN, ds);

        // concatenate segments
        final StringBuilder builder = new StringBuilder(symbol).append(COMMA);
        final String date = line.substring(ds, de);
        builder.append(date.substring(SIX))         // year
               .append(date.substring(ZERO, TWO))   // month
               .append(date.substring(THREE, FIVE)) // date
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
