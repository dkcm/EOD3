/**
 * AlphaVantage.java  v0.3  23 March 2017 4:58:12 pm
 *
 * Copyright © 2017-2018 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.AlphaVantage.Formats.*;
import static org.ikankechil.eod3.sources.AlphaVantage.JsonElements.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Alpha Vantage.
 *
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class AlphaVantage extends Source {

  private final Formats       format;
  private final String        apiKey;

  // URL parameters
  private static final String FREQUENCY = "&function=TIME_SERIES_";
  private static final String FORMAT    = "&datatype=";
  private static final String API_KEY_  = "&apikey=";

  // API key property: org.ikankechil.eod3.sources.AlphaVantage.apiKey
  private static final String API_KEY   = System.getProperty(AlphaVantage.class.getName() + ".apiKey", "demo");

  // Exchange-related constants
  private static final String TSE_      = "TSE:";
  private static final String LON       = "LON:";
  private static final String FRA       = "FRA:";
  private static final String EPA       = "EPA:";
  private static final String AMS       = "AMS:";
  private static final String EBR       = "EBR:";
  private static final String BIT       = "BIT:";
  private static final String ELI       = "ELI:";
  private static final String STO       = "STO:";
  private static final String HEL       = "HEL:";
  private static final String CPH       = "CPH:";
  private static final String ICE       = "ICE:";
  private static final String MCX       = "MCX:";
  private static final String SHA       = "SHA:";
  private static final String SHE       = "SHE:";
  private static final String TYO       = "TYO:";
  private static final String BOM       = "BOM:";
  private static final String TPE       = "TPE:";

  private static final Logger logger    = LoggerFactory.getLogger(AlphaVantage.class);

  public AlphaVantage() {
    this(JSON, null);
  }

  public AlphaVantage(final Formats format, final String apiKey) {
    super(AlphaVantage.class);

    this.format = (format == null) ? CSV : format;
    this.apiKey = (apiKey == null || apiKey.isEmpty()) ? API_KEY : apiKey;

    // supported markets
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }
    for (final Exchanges exchange : EnumSet.of(SGX, NSE, ASX)) {
      exchanges.put(exchange, exchange.toString() + COLON);
    }

    exchanges.put(TSX, TSE_);
    exchanges.put(LSE, LON);
    exchanges.put(FWB, FRA);
    exchanges.put(PAR, EPA);
    exchanges.put(AEX, AMS);
    exchanges.put(BB, EBR);
    exchanges.put(MIB, BIT);
    exchanges.put(BVLP, ELI);
    exchanges.put(SB, STO);
    exchanges.put(HEX, HEL);
    exchanges.put(KFB, CPH);
    exchanges.put(ICEX, ICE);
    exchanges.put(MOEX, MCX);
    exchanges.put(SSE, SHA);
    exchanges.put(SZSE, SHE);
    exchanges.put(TSE, TYO);
    exchanges.put(BSE, BOM);
    exchanges.put(TWSE, TPE);

    // Alpha Vantage API
    // http://www.alphavantage.co/documentation/
    //
    // Notes:
    // 1. incoming data is in either JSON or CSV format
    // 2. FX data is real-time
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
    url.append(FREQUENCY).append((frequency != null) ? frequency
                                                     : DEFAULT_FREQUENCY); // default to daily
    logger.debug("Frequency: {}", url);
  }

  @Override
  void appendSuffix(final StringBuilder url) {
    appendFormat(url);
    appendApiKey(url);
  }

  private void appendFormat(final StringBuilder url) {
    url.append(FORMAT).append(format.toString().toLowerCase());
  }

  private final void appendApiKey(final StringBuilder url) {
    if (apiKey != null && !apiKey.isEmpty()) {
      url.append(API_KEY_).append(apiKey);
    }
  }

  enum JsonElements {
    OPEN, HIGH, LOW, CLOSE, VOLUME;

    private final String name;
    private final int    offset;

    JsonElements() {
      final StringBuilder nameBuilder = new StringBuilder();
      nameBuilder.append(ordinal() + ONE).append(DOT).append(SPACE).append(name().toLowerCase());

      name = nameBuilder.toString();
      offset = name.length() + FOUR;
    }

    @Override
    public String toString() {
      return name;
    }

    public int offset() {
      return offset;
    }
  }

  public enum Formats {
    JSON {
      @Override
      public TextTransformer newTransformer(final TextTransform transform) {
        return new TextTransformer(transform) {

          private final String dataStartTag = OPEN.toString();

          @Override
          public List<String> transform(final List<String> lines) {
            final List<String> newLines = new ArrayList<>(lines.size() >> ONE);

            final ListIterator<String> iterator = lines.listIterator();
            while (iterator.hasNext()) {
              if (iterator.next().contains(dataStartTag)) {
                iterator.previous();
                final StringBuilder newLine = new StringBuilder(iterator.previous()); // date
                iterator.next();
                newLine.append(iterator.next()); // open
                newLine.append(iterator.next()); // high
                newLine.append(iterator.next()); // low
                newLine.append(iterator.next()); // close
                newLine.append(iterator.next()); // volume

                newLines.add(transform.transform(newLine.toString()));
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
          @Override
          public String transform(final String line) {
            // Alpha Vantage JSON format (after pre-processing)
            //        "2017-03-24": {            "1. open": "35.65",            "2. high": "35.73",            "3. low": "35.12",            "4. close": "35.16",            "5. volume": "22030600"
            //        "2017-03-23": {            "1. open": "35.49",            "2. high": "35.49",            "3. low": "35.02",            "4. close": "35.27",            "5. volume": "20529600"
            //        "2017-03-22": {            "1. open": "35.22",            "2. high": "35.46",            "3. low": "35.00",            "4. close": "35.37",            "5. volume": "18704600"

            // MetaStock CSV format
            // Symbol,YYYYMMDD,Open,High,Low,Close,Volume
            // INTC,20170324,35.65,35.73,35.12,35.16,22030600
            // INTC,20170323,35.49,35.49,35.02,35.27,20529600
            // INTC,20170322,35.22,35.46,35.00,35.37,18704600

            // indices
            final int ds = line.indexOf(DOUBLE_QUOTE) + ONE;
            final int de = line.indexOf(DOUBLE_QUOTE, ds);
            final int os = line.indexOf(OPEN.toString())             + OPEN.offset();
            final int oe = line.indexOf(DOUBLE_QUOTE, os);
            final int hs = line.indexOf(HIGH.toString(),   oe + TWO) + HIGH.offset();
            final int he = line.indexOf(DOUBLE_QUOTE, hs);
            final int ls = line.indexOf(LOW.toString(),    he + TWO) + LOW.offset();
            final int le = line.indexOf(DOUBLE_QUOTE, ls);
            final int cs = line.indexOf(CLOSE.toString(),  le + TWO) + CLOSE.offset();
            final int ce = line.indexOf(DOUBLE_QUOTE, cs);
            final int vs = line.indexOf(VOLUME.toString(), ce + TWO) + VOLUME.offset();
            final int ve = line.indexOf(DOUBLE_QUOTE, vs);

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
    },
    CSV {
      @Override
      public TextTransformer newTransformer(final TextTransform transform) {
        return new TextTransformer(transform, ONE, false);
      }

      @Override
      public TextTransform newTransform(final String symbol) {
        return new TextTransform() {
          @Override
          public String transform(final String line) {
            // Alpha Vantage CSV format
            // timestamp,open,high,low,close,volume
            // 2018-01-09,44.7000,44.8400,43.4900,43.6200,43492902
            // 2018-01-08,44.2700,44.8400,43.9600,44.7400,33170821
            // 2018-01-05,44.4300,45.1500,43.9000,44.7400,41108418
            // 2018-01-04,43.5200,44.6500,42.6900,44.4300,88431631
            // 2018-01-03,45.4700,46.2100,43.6500,45.2600,113815771
            // 2018-01-02,46.3800,46.9000,46.2100,46.8500,22435059
            // 2017-12-29,46.2100,46.4900,46.0900,46.1600,17136416
            // 2017-12-28,46.3600,46.3600,45.9500,46.2200,9279766
            // 2017-12-27,46.1100,46.3600,46.0000,46.1100,12412977
            // 2017-12-26,46.2800,46.4700,45.9500,46.0800,15477747
            // 2017-12-22,46.3300,47.0200,46.0200,46.7000,33404280
            // 2017-12-21,47.5400,47.5900,46.5600,46.7600,42113273

            // MetaStock CSV format
            // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

            final char[] characters = new char[symbol.length() + line.length() - ONE];
            // set row name
            int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
            characters[i] = COMMA;
            // copy year
            i = getChars(line, ZERO, FOUR, characters, ++i);
            // copy month
            i = getChars(line, FIVE, SEVEN, characters, i);
            // copy rest of line
            line.getChars(EIGHT, line.length(), characters, i);

            return String.valueOf(characters);
          }
        };
      }
    };

    public abstract TextTransformer newTransformer(final TextTransform transform);

    public abstract TextTransform newTransform(final String symbol);

  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return format.newTransformer(transform);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return format.newTransform(symbol);
  }

}
