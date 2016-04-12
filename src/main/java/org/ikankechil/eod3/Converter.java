/**
 * Converter.java	v1.0	28 November 2013 10:14:02 PM
 *
 * Copyright © 2013-2014 Daniel Kuan. All rights reserved.
 */
package org.ikankechil.eod3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import org.ikankechil.eod3.YahooFinanceDownloader.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that converts stock price data from Yahoo! Finance CSV format to
 * MetaStock CSV format.
 *
 * @author Daniel Kuan
 * @version 1.0
 */
public class Converter {

  private final YahooFinanceDownloader downloader;
  private List<String>                 lines;

  // Constants
  private static final char            COMMA        = ',';
  private static final String          COMMA_STRING = String.valueOf(COMMA);
  private static final String          EMPTY        = "";

  private static final int             ZERO         = 0;
//  private static final int             FOUR         = 4;
//  private static final int             SEVEN        = 7;
  private static final int             FIVE         = 5;
  private static final int             EIGHT        = 8;

  private static final Logger          logger       = LoggerFactory.getLogger(Converter.class);

  public Converter() {
    this(null);
  }

  /**
   * Constructs a <code>Converter</code> instance. If
   * <code>directory</code> is null then all conversions are written to the
   * current directory.
   *
   * @param directory the directory to which all conversions are written.
   */
  public Converter(final File directory) {
    // directory can be null
    downloader = new YahooFinanceDownloader(directory);
  }

  public static void main(final String[] args) {
    if (args.length <= ZERO) {
      throw new IllegalArgumentException();
    }

    Converter converter = new Converter();
    for (String symbol : args) {
      try {
        converter.convert(symbol);
      }
      catch (MalformedURLException murlE) {
        murlE.printStackTrace();
      }
      catch (IOException ioE) {
        ioE.printStackTrace();
      }
    }
  }

  /**
   * Converts
   *
   * @param symbol the Yahoo! Finance stock symbol of interest
   * @return the <code>File</code> to which a conversion is written
   * @throws MalformedURLException
   * @throws IOException
   * @throws IllegalArgumentException when <code>symbol</code> is empty
   * @throws NullPointerException when <code>symbol</code> is null
   */
  public File convert(final String symbol) throws MalformedURLException, IOException {
    return convert(symbol, null, null);
  }

  /**
   * @param symbol the Yahoo! Finance stock symbol of interest
   * @param start the stock data start date
   * @param end the stock data end date
   * @return the <code>File</code> to which a conversion is written
   * @throws MalformedURLException
   * @throws IOException
   * @throws FileNotFoundException when the period specified by
   *           <code>start</code> and <code>end</code> is unavailable
   * @throws IllegalArgumentException when <code>symbol</code> is empty
   * @throws NullPointerException when <code>symbol</code> is null
   */
  public File convert(final String symbol,
                      final Calendar start,
                      final Calendar end)
      throws MalformedURLException, IOException {
    return convert(symbol, start, end, null);
  }

  /**
   * @param symbol the Yahoo! Finance stock symbol of interest
   * @param start the stock data start date
   * @param end the stock data end date
   * @param frequency the stock data frequency
   * @return the <code>File</code> to which a conversion is written
   * @throws MalformedURLException
   * @throws IOException
   * @throws FileNotFoundException when the period specified by
   *           <code>start</code> and <code>end</code> is unavailable
   * @throws IllegalArgumentException when <code>symbol</code> is empty
   * @throws NullPointerException when <code>symbol</code> is null
   */
  public File convert(final String symbol,
                      final Calendar start,
                      final Calendar end,
                      final Frequency frequency)
      throws MalformedURLException, IOException {
    if (symbol.equals(EMPTY)) {
      throw new IllegalArgumentException();
    }

    logger.info("Conversion commencing: " + symbol);
    lines = downloader.readURL(symbol, start, end, frequency);
    format(symbol);

    // TODO decouple read and write
    File file = downloader.write(symbol);
    logger.info("Conversion completed: " + symbol);

    return file;
  }

  /**
   * Transforms downloaded data into MetaStock format.
   *
   * Inserts <code>symbol</code> as row name, ensures dates are of the YYYYMMDD
   * format and removes the last column ("Adj Close").
   *
   * @param symbol the Yahoo! Finance stock symbol of interest
   */
  private void format(final String symbol) {
    // Yahoo! Finance CSV format
    // YYYY-MM-DD, Open, High, Low, Close, Volume, Adj Close

    // MetaStock CSV format
    // Symbol, YYYYMMDD, Open, High, Low, Close, Volume

    int index = ZERO;
    // remove column names row
    lines.remove(index);

    for (String line : lines) {
      // set row name
      StringBuilder builder = new StringBuilder(symbol).append(COMMA).append(line);
      // remove last column
      builder.delete(builder.lastIndexOf(COMMA_STRING), builder.length());
      // remove hyphens from date
      builder.deleteCharAt(symbol.length() + EIGHT).deleteCharAt(symbol.length() + FIVE);

      lines.set(index++, builder.toString());
    }

    logger.info("Data formatted: " + symbol);
  }

}
