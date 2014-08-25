/**
 * YahooFinanceDownloader.java	v1.0	3 June 2013 11:28:03 PM
 *
 * Copyright © 2013-2014 Daniel Kuan. All rights reserved.
 */
package org.ikankechil.eod3;

import static java.util.Calendar.*;
import static joptsimple.util.DateConverter.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.DateConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that downloads stock price data from Yahoo! Finance and writes it to
 * a CSV file.
 * <p>
 * Yahoo! Finance API:<br>
 * http://ichart.finance.yahoo.com/table.csv?s=<Stock Symbol><br>
 *                                          &a=<Start Month - 1><br>
 *                                          &b=<Start Date><br>
 *                                          &c=<Start Year><br>
 *                                          &d=<End Month - 1><br>
 *                                          &e=<End Date><br>
 *                                          &f=<End Year><br>
 *                                          &g=<Frequency><br>
 *                                          &ignore=.csv<br>
 *
 * @author Daniel Kuan
 * @version 1.0
 */
public class YahooFinanceDownloader {

  private final File          directory;
  private final List<String>  lines;

  // URL and file constants
  private static final String BASE        = "http://ichart.finance.yahoo.com/table.csv?s=";
  private static final String CSV         = ".csv";

  // Date-related URL parameters
  private static final String START_MONTH = "&a=";
  private static final String START_DATE  = "&b=";
  private static final String START_YEAR  = "&c=";
  private static final String END_MONTH   = "&d=";
  private static final String END_DATE    = "&e=";
  private static final String END_YEAR    = "&f=";
  private static final String FREQUENCY   = "&g=";
  private static final String SUFFIX      = "&ignore=" + CSV;

  // Other constants
  private static final char   SPACE       = ' ';
  private static final String EMPTY       = "";
//  private static final int    ZERO        = 0;

  private static final Logger logger      = LoggerFactory.getLogger(YahooFinanceDownloader.class);

  public enum Frequency {
    // Frequency-related constants
    DAILY('d'), WEEKLY('w'), MONTHLY('m');

    final char frequency;

    Frequency(final char frequency) {
      this.frequency = frequency;
    }

  }

  public YahooFinanceDownloader() {
    this(null);
  }

  /**
   * Constructs a <code>YahooFinanceDownloader</code> instance. If
   * <code>directory</code> is null then all downloads are written to the
   * current directory.
   *
   * @param directory the directory to which all downloads are written.
   */
  public YahooFinanceDownloader(final File directory) {
    this.directory = directory; // can be null
    lines = new ArrayList<>();
  }

  public static void main(final String[] args) throws IOException {
    OptionParser parser = new OptionParser("h*?*");
    parser.allowsUnrecognizedOptions();

    ArgumentAcceptingOptionSpec<String> inputs = parser.accepts("I").withRequiredArg().withValuesSeparatedBy(SPACE);
//    parser.accepts("i").withRequiredArg().ofType(File.class);
//    parser.accepts("o").withOptionalArg().ofType(File.class);

    DateConverter datePattern = datePattern("dd/mm/yyyy");
    OptionSpec<Date> startDate = parser.accepts("s").withRequiredArg().withValuesConvertedBy(datePattern);
    OptionSpec<Date> endDate = parser.accepts("e").withRequiredArg().withValuesConvertedBy(datePattern);

    OptionSet options = parser.parse(args);

    if (options.has("h") || options.has("?") || !options.nonOptionArguments().isEmpty()) {
      parser.printHelpOn(System.out);
    }
    else {
      List<String> symbols = options.valuesOf(inputs);

      YahooFinanceDownloader yfd = new YahooFinanceDownloader();
      for (String symbol : symbols) {
        try {
          if (options.has(startDate) && options.has(endDate)) {
            Calendar start = Calendar.getInstance();
            start.setTime(options.valueOf(startDate));
            Calendar end = Calendar.getInstance();
            end.setTime(options.valueOf(endDate));

            yfd.download(symbol, start, end);
          }
          else {
            yfd.download(symbol);
          }
        }
        catch (MalformedURLException murlE) {
          murlE.printStackTrace();
        }
        catch (IOException ioE) {
          ioE.printStackTrace();
        }
      }
    }

//    if (args.length <= ZERO) {
//      throw new IllegalArgumentException();
//    }

    // Options
    // 1. Number of stocks: single or multiple -i <>
    // 2. Period: All, specified period or a single day -s <> -e <>
    // 3. Frequency: daily, weekly, monthly -f [d, w, m]
    // 4. Destination folder -o [path]
    //
    // Daily data download
    // Last used settings?
    // Favourite settings?
    // Automated?
//    YahooFinanceDownloader yfd = new YahooFinanceDownloader();
//    for (String symbol : args) {
//      try {
//        yfd.download(symbol);
//      }
//      catch (MalformedURLException murlE) {
//        murlE.printStackTrace();
//      }
//      catch (IOException ioE) {
//        ioE.printStackTrace();
//      }
//    }
  }

  /**
   * Downloads stock price data and write it to a CSV file.
   *
   * @param symbol the Yahoo! Finance stock symbol of interest
   * @return the <code>File</code> to which a download is written
   * @throws MalformedURLException
   * @throws IOException
   * @throws IllegalArgumentException when <code>symbol</code> is empty
   * @throws NullPointerException when <code>symbol</code> is null
   */
  public File download(final String symbol) throws MalformedURLException, IOException {
    return download(symbol, null, null);
  }

  /**
   * Downloads stock price data for a specified period.
   *
   * @param symbol the Yahoo! Finance stock symbol of interest
   * @param start the stock data period start date
   * @param end the stock data period end date
   * @return the <code>File</code> to which a download is written
   * @throws MalformedURLException
   * @throws IOException
   * @throws FileNotFoundException when the period specified by
   *           <code>start</code> and <code>end</code> is unavailable
   * @throws IllegalArgumentException when <code>symbol</code> is empty
   * @throws NullPointerException when <code>symbol</code> is null
   */
  public File download(final String symbol,
                       final Calendar start,
                       final Calendar end)
      throws MalformedURLException, IOException {
    return download(symbol, start, end, null);
  }

  /**
   * Downloads stock price data for a specified period with a specified
   * frequency.
   *
   * @param symbol the Yahoo! Finance stock symbol of interest
   * @param start the stock data period start date
   * @param end the stock data period end date
   * @param frequency the stock data frequency
   * @return the <code>File</code> to which a download is written
   * @throws MalformedURLException
   * @throws IOException
   * @throws FileNotFoundException when the period specified by
   *           <code>start</code> and <code>end</code> is unavailable
   * @throws IllegalArgumentException when <code>symbol</code> is empty
   * @throws NullPointerException when <code>symbol</code> is null
   */
  public File download(final String symbol,
                       final Calendar start,
                       final Calendar end,
                       final Frequency frequency)
      throws MalformedURLException, IOException {
    if (symbol.equals(EMPTY)) {
      throw new IllegalArgumentException();
    }

    logger.info("Download commencing: " + symbol);
    readURL(symbol, start, end, frequency);
    File file = write(symbol);
    logger.info("Download completed: " + symbol);

    return file;
  }

  List<String> readURL(final String symbol,
                       final Calendar start,
                       final Calendar end,
                       final Frequency frequency)
      throws MalformedURLException, IOException {
    // build URL using Yahoo! Finance API
    // http://ichart.finance.yahoo.com/table.csv?s=<Stock Symbol>
    //                                          &a=<Start Month - 1>
    //                                          &b=<Start Date>
    //                                          &c=<Start Year>
    //                                          &d=<End Month - 1>
    //                                          &e=<End Date>
    //                                          &f=<End Year>
    //                                          &g=<Frequency>
    //                                          &ignore=.csv

    // Stooq
    // http://stooq.com/q/d/l/?s=nsh.us&i=d

    StringBuilder urlBuilder = new StringBuilder(BASE).append(symbol);

    if ((start != null) && (end != null) && start.before(end)) {
      // append start date
      urlBuilder.append(START_MONTH).append(start.get(MONTH))
                .append(START_DATE).append(start.get(DATE))
                .append(START_YEAR).append(start.get(YEAR));

      // append end date
      urlBuilder.append(END_MONTH).append(end.get(MONTH))
                .append(END_DATE).append(end.get(DATE))
                .append(END_YEAR).append(end.get(YEAR));

      // append frequency and suffix
      urlBuilder.append(FREQUENCY).append((frequency == null) ?
                                          Frequency.DAILY.frequency :
                                          frequency.frequency)
                .append(SUFFIX);
    }

    // read from URL
    String url = urlBuilder.toString();
    BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
    lines.clear();
    String line;
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }
    reader.close();
    logger.info("URL read: " + url);

    return lines;
  }

  File write(final String symbol) throws IOException {
    File file = new File(directory, symbol + CSV);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    for (String line : lines) {
      writer.write(line);
      writer.newLine();
    }
    writer.close();
    logger.info("File written: " + file);

    return file;
  }

}
