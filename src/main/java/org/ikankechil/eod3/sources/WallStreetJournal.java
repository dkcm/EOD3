/**
 * WallStreetJournal.java v0.7  14 May 2014 11:49:20 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.eod3.sources.WallStreetJournal.DateFormats.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing the Wall Street Journal.
 *
 * @author Daniel Kuan
 * @version 0.7
 */
public class WallStreetJournal extends Source {

  // Date-related URL parameters
  private static final String START_DATE = "&startDate=";
  private static final String END_DATE   = "&endDate=";

  private static final String ROWS       = "&num_rows=" + Short.MAX_VALUE;

  // Country code
  private static final String COUNTRY    = "&country=";

  // Exchange-related constants
  private static final String US         = "US";
  private static final String CA         = "CA";
  private static final String UK         = "UK";
  private static final String IE         = "IE";
  private static final String DE         = "DE";
  private static final String FR         = "FR";
  private static final String NL         = "NL";
  private static final String BE         = "BE";
  private static final String CH         = "CH";
  private static final String IT         = "IT";
  private static final String ES         = "ES";
  private static final String PT         = "PT";
  private static final String AT         = "AT";
  private static final String GR         = "GR";
  private static final String TR         = "TR";
  private static final String NO         = "NO";
  private static final String SE         = "SE";
  private static final String FI         = "FI";
  private static final String DK         = "DK";
  private static final String IS         = "IS";
  private static final String RU         = "RU";
  private static final String PL         = "PL";
  private static final String HU         = "HU";
  private static final String CZ         = "CZ";
  private static final String RO         = "RO";
  private static final String SG         = "SG";
  private static final String HK         = "HK";
  private static final String CN         = "CN";
  private static final String JP         = "JP";
  private static final String IN         = "IN";
  private static final String KR         = "KR";
  private static final String TW         = "TW";
  private static final String ID         = "ID";
  private static final String MY         = "MY";
  private static final String TH         = "TH";
  private static final String AU         = "AU";
  private static final String NZ         = "NZ";
  private static final String ZA         = "ZA";
  private static final String BR         = "BR";
  private static final String AR         = "AR";
  private static final String CL         = "CL";
  private static final String MX         = "MX";

  static final Logger         logger     = LoggerFactory.getLogger(WallStreetJournal.class);

  public WallStreetJournal() {
    super(WallStreetJournal.class);

    // supported markets (see http://quotes.wsj.com/company-list)
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, NYSEARCA, FX)) {
      exchanges.put(exchange, US);
    }

    exchanges.put(TSX, CA);
    exchanges.put(LSE, UK);
    exchanges.put(ISE, IE);
    exchanges.put(FWB, DE);
    exchanges.put(PAR, FR);
    exchanges.put(AMS, NL);
    exchanges.put(BB, BE);
    exchanges.put(SWX, CH);
    exchanges.put(MIB, IT);
    exchanges.put(BM, ES);
    exchanges.put(BVLP, PT);
    exchanges.put(WB, AT);
    exchanges.put(ATHEX, GR);
    exchanges.put(BIST, TR);
    exchanges.put(OSLO, NO);
    exchanges.put(SB, SE);
    exchanges.put(HEX, FI);
    exchanges.put(KFB, DK);
    exchanges.put(ICEX, IS);
    exchanges.put(MOEX, RU);
    exchanges.put(GPW, PL);
    exchanges.put(BET, HU);
    exchanges.put(PX, CZ);
    exchanges.put(BVB, RO);
    exchanges.put(SGX, SG);
    exchanges.put(HKSE, HK);
    exchanges.put(SSE, CN);
    exchanges.put(SZSE, CN);
    exchanges.put(TSE, JP);
    exchanges.put(OSE, JP);
    exchanges.put(BSE, IN);
    exchanges.put(NSE, IN);
    exchanges.put(KRX, KR);
    exchanges.put(TWSE, TW);
    exchanges.put(IDX, ID);
    exchanges.put(MYX, MY);
    exchanges.put(SET, TH);
    exchanges.put(ASX, AU);
    exchanges.put(NZX, NZ);
    exchanges.put(JSE, ZA);
    exchanges.put(BOVESPA, BR);
    exchanges.put(BCBA, AR);
    exchanges.put(BCS, CL);
    exchanges.put(BMV, MX);

    // Wall Street Journal API
    // http://quotes.wsj.com/cdssvco/marketwatch/dylan/v1/HistoricalPrices?ticker=A
    //                                                                    &countrycode=US
    //                                                                    &startDate=01-01-2000
    //                                                                    &endDate=05-14-2014
    //                                                                    &duration=P1D
    //                                                                    &pagePos=0
    //                                                                    &pageSize=0
    //                                                                    &sortColumn=Time
    //                                                                    &sortOrder=DESC
    //                                                                    &contentType=xls
    //
    // Excel:
    // http://quotes.wsj.com/cdssvco/marketwatch/dylan/v1/HistoricalPrices?ticker=A&countrycode=US&startDate=01-01-2000&endDate=05-14-2014&duration=P1D&pagePos=0&pageSize=0&sortColumn=Time&sortOrder=DESC&contentType=xls
    //
    // XML:
    // http://quotes.wsj.com/cdssvco/marketwatch/dylan/v1/HistoricalPrices?ticker=JCP&countrycode=US&startDate=01-18-1970&endDate=05-15-2014&duration=P1D&pageSize=0&sortColumn=Time&sortOrder=DESC
    //
    // Notes:
    // 1. Downloads a single data point if no dates are specified
  }

  enum DateFormats {
    URL("MM/dd/yyyy"),
    INPUT("MM/dd/yy"),
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
    appendSymbol(url, symbol);
    url.append(QUESTION);
    appendExchange(url, exchange);
  }

  @Override
  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    final String value = exchanges.get(exchange);
    // only append non-null and non-empty exchanges
    if (value != null && !value.isEmpty()) {
      url.append(COUNTRY).append(value);
    }
    else {
      logger.debug("Unsupported exchange {} requested for {}", exchange, url);
    }
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &startDate=01/18/1970
    url.append(START_DATE).append(URL.dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // &endDate=05/14/2014
    url.append(END_DATE).append(URL.dateFormat.format(end.getTime()));
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
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    url.append(ROWS);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform) {

      private static final String ROW_START_TAG     = "<tr>";
      private static final String ROW_END_TAG       = "</tr>";
      private static final String HEADING_START_TAG = "<th>";

      @Override
      public List<String> transform(final List<String> lines) {
        final List<String> newLines = new ArrayList<>(lines.size() >> THREE);

        final StringBuilder newLine = new StringBuilder();
        for (final String line : lines) {
          if (line.contains(ROW_START_TAG)) {
            newLine.delete(ZERO, newLine.length());
          }
          else if (line.contains(ROW_END_TAG)) {
            if (newLine.indexOf(HEADING_START_TAG) < ZERO) {
              newLines.add(transform.transform(newLine.toString()));
            }
            else { // remove column headings
              logger.debug("Removed column headings: {}", newLine);
            }
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

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {

      private static final String DATA_START_TAG = "<td>";
      private static final int    OFFSET         = 4;

      @Override
      public String transform(final String line) {
        // WallStreetJournal XML format
        //        <tr>        <th>Last 5 Days</th>        <th>OPEN</th>        <th>HIGH</th>        <th>LOW</th>          <th>CLOSE</th>          <th>VOLUME</th>      </tr>
        //        <tr>        <td>12/04/15</td>        <td>34.1100</td>        <td>35.0250</td>        <td>34.0000</td>        <td>34.9350</td>          <td>24.90 M</td>      </tr>

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
        logger.trace(line);

        // indices
        final int ds = line.indexOf(DATA_START_TAG)              + OFFSET;
        final int de = line.indexOf(LESS_THAN, ds);
        final int os = line.indexOf(DATA_START_TAG, de + OFFSET) + OFFSET;
        final int oe = line.indexOf(LESS_THAN, os);
        final int hs = line.indexOf(DATA_START_TAG, oe + OFFSET) + OFFSET;
        final int he = line.indexOf(LESS_THAN, hs);
        final int ls = line.indexOf(DATA_START_TAG, he + OFFSET) + OFFSET;
        final int le = line.indexOf(LESS_THAN, ls);
        final int cs = line.indexOf(DATA_START_TAG, le + OFFSET) + OFFSET;
        final int ce = line.indexOf(LESS_THAN, cs);
        final int vs = line.indexOf(DATA_START_TAG, ce + OFFSET) + OFFSET;
        final int ve = line.indexOf(LESS_THAN, vs);

        // concatenate segments
        final StringBuilder builder = new StringBuilder(symbol).append(COMMA);
        try {
          final String date = OUTPUT.dateFormat.format(INPUT.dateFormat.parse(line.substring(ds, de))); // MM/dd/yy -> yyyyMMdd
          builder.append(date)
                 .append(COMMA);
          builder.append(line.substring(os, oe))                // open
                 .append(COMMA);
          builder.append(line.substring(hs, he))                // high
                 .append(COMMA);
          builder.append(line.substring(ls, le))                // low
                 .append(COMMA);
          builder.append(line.substring(cs, ce))                // close
                 .append(COMMA);
          builder.append(parseVolume(line.substring(vs, ve)));  // volume
        }
        catch (final ParseException pE) {
          logger.warn("Invalid date: {}", line, pE);
        }

        return builder.toString();
      }

      private final String parseVolume(final String string) {
        final int dot = string.indexOf(DOT);
        final int space = string.lastIndexOf(SPACE);
        final int end = (space < ZERO) ? string.length() : space;
        final int current = (dot < ZERO) ? ZERO : end - dot - ONE;  // current number of decimal places

        final int shortfall;
        final char last = string.charAt(string.length() - ONE);
        switch (last) {
          case 'M': // millions
          case 'm':
            shortfall = SIX - current;
            break;

          case 'B': // billions
          case 'b':
            shortfall = NINE - current;
            break;

          case 'T': // trillions
          case 't':
            shortfall = TWELVE - current;
            break;

          default:
            shortfall = ZERO;
            break;
        }

        final StringBuilder volume = new StringBuilder();
        if (shortfall == ZERO) {
          volume.append(string);
        }
        else {
          volume.append(string.substring(ZERO, dot))
                .append(string.substring(dot + ONE, end));

          // make up for shortfall
          for (int i = ZERO; i < shortfall; ++i) {
            volume.append(ZERO);
          }
        }
        return volume.toString();
      }

    };
  }

}
