/**
 * EOD3.java  v1.2  1 April 2014 4:37:17 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ikankechil.eod3.Converter;
import org.ikankechil.eod3.Frequencies;
import org.ikankechil.eod3.Interval;
import org.ikankechil.eod3.sources.Exchanges;
import org.ikankechil.eod3.sources.Source;
import org.ikankechil.ui.AbstractCommandLineInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joptsimple.OptionException;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.DateConverter;

/**
 * A command-line application that converts stock price data (open, high, low,
 * close and volume) from <code>Source</code> CSV format to MetaStock CSV
 * format.
 * <p>
 *
 * @author Daniel Kuan
 * @version 1.2
 */
public class EOD3 extends AbstractCommandLineInterface<String, List<File>> {

  private Converter                     converter;
  private List<File>                    destinations;
  private final Source                  source;

  // input symbols
  private final OptionSpec<Void>        inputSymbolsFile;

  // modes / functionalities
  private final OptionSpec<Void>        download;
  private final OptionSpec<Void>        update;
  private final OptionSpec<Void>        merge;

  // parameters
  private final OptionSpec<File>        outputDir;
  private final OptionSpec<Date>        startDate;
  private final OptionSpec<Date>        endDate;
  private final OptionSpec<Frequencies> frequency;
  private final OptionSpec<Exchanges>   exchange;

  private static final char             DOT          = '.';
  private static final DateConverter    DATE_PATTERN = DateConverter.datePattern("yyyyMMdd");

  // properties
  private static final String           SOURCE       = Source.class.getPackage().getName();

  private static final Logger           logger       = LoggerFactory.getLogger(EOD3.class);

  public EOD3(final Source source) {
    super("Symbols / Symbol Files", String.class);

    if (source == null) {
      throw new NullPointerException("Null source");
    }
    this.source = source;

    // command-line options:
    // -i input symbols file
    // -o output directory
    // -d download only, no conversion
    // -s start date
    // -e end date
    // -f frequency [DAILY,WEEKLY,MONTHLY]
    // -x exchange
    // -u update
    // -m merge output files

    // Configuring command-line options
    // input symbols
    inputSymbolsFile = parser.accepts("i", "Input symbols file");

    // modes / functionalities
    download = parser.accepts("d", "Download only, no conversion");
    update = parser.accepts("u", "Update");
    merge = parser.accepts("m", "Merge output files");

    // parameters
    outputDir = parser.accepts("o", "Output directory")
                      .requiredIf(update, merge)
                      .withRequiredArg()
                      .ofType(File.class);
    endDate = parser.accepts("e", "Interval end date")
                    .withRequiredArg()
                    .withValuesConvertedBy(DATE_PATTERN);
    startDate = parser.accepts("s", "Interval start date")
                      .requiredIf(endDate)
                      .withRequiredArg()
                      .withValuesConvertedBy(DATE_PATTERN);
    frequency = parser.accepts("f", "Frequency " + Arrays.asList(Frequencies.values()))
                      .withRequiredArg()
                      .ofType(Frequencies.class);
    exchange = parser.accepts("x", "Exchange " + Arrays.asList(Exchanges.values()))
                     .requiredUnless(inputSymbolsFile, update, merge)
                     .withRequiredArg()
                     .ofType(Exchanges.class);
  }

  public static void main(final String... arguments) throws IOException, InterruptedException {
    // runtime-specified Source
    final String sourceName = SOURCE + DOT + System.getProperty(SOURCE);
    try {
      final Source source = (Source) Class.forName(sourceName)
                                          .getConstructor()
                                          .newInstance();
      new EOD3(source).execute(arguments);
    }
    catch (final ReflectiveOperationException | IllegalArgumentException e) {
      logger.error("Bad source: {}", sourceName, e);
      System.err.println("Bad source: " + sourceName);
      e.printStackTrace();
    }
  }

  @Override
  protected void start() {
    destinations = new ArrayList<>();
    logger.info("Source: {}", source.getClass().getName());
    converter = new Converter(source);
  }

  @Override
  protected void workOn(final OptionSet options, final List<String> symbols)
      throws OptionException, InterruptedException, IOException {
    if (symbols.isEmpty()) {
      // update and / or merge
      final boolean hasUpdate = options.has(update);
      final boolean hasMerge = options.has(merge);
      if (hasUpdate && !hasMerge) {
        // -o <outputDir> -u
        // illegal: -i -d -s -e -f
        checkIllegalOptions(options, inputSymbolsFile, download, startDate, endDate, frequency, exchange);
        destinations.add(converter.update(options.valueOf(outputDir))); // -o <outputDir> -u
      }
      else if (!hasUpdate && hasMerge) {
        // -o <outputDir> -m
        // illegal: -i -d -s -e -f
        checkIllegalOptions(options, inputSymbolsFile, download, startDate, endDate, frequency, exchange);
        destinations.add(converter.merge(options.valueOf(outputDir)));  // -o <outputDir> -m
      }
      else if (hasUpdate && hasMerge) {
        // -o <outputDir> -u -m
        // illegal: -i -d -s -e -f
        checkIllegalOptions(options, inputSymbolsFile, download, startDate, endDate, frequency, exchange);
        final File outputParentDirectory = options.valueOf(outputDir);
        converter.merge(outputParentDirectory);
        converter.update(outputParentDirectory);
        destinations.add(converter.merge(outputParentDirectory));
      }
      else {
        // neither update nor merge
        throw new IllegalArgumentException("Missing symbol(s)");
      }
    }
    else {
      // symbol files or symbols
      checkIllegalOptions(options, update, merge);

      final Interval interval = newInterval(options);
      final File outputDirectory = options.valueOf(outputDir);

      // treat non-option arguments as files
      if (options.has(inputSymbolsFile)) {
        if (options.has(exchange)) {
          logger.info("Option ignored: {} {}", exchange, options.valueOf(exchange));
        }

        if (options.has(download)) {
          for (final String symbolsFile : symbols) {
            final File file = new File(symbolsFile);
            destinations.add(converter.download(file,
                                                interval,
                                                (outputDirectory != null) ? outputDirectory         // -i -d -o <outputDir> <inputSymbolsFiles...>
                                                                          : file.getParentFile())); // -i -d <inputSymbolsFiles...>
          }
        }
        else {
          for (final String symbolsFile : symbols) {
            final File file = new File(symbolsFile);
            destinations.add(converter.convert(file,
                                               interval,
                                               (outputDirectory != null) ? outputDirectory          // -i -o <outputDir> <inputSymbolsFiles...>
                                                                         : file.getParentFile()));  // -i <inputSymbolsFiles...>
          }
        }
      }
      // treat non-option arguments as symbols
      else {
        destinations = options.has(download) ?
                       converter.download(symbols,
                                          options.valueOf(exchange),
                                          interval,
                                          outputDirectory) :  // -d -x <exchange> -o <outputDir> <symbols...>
                       converter.convert(symbols,
                                         options.valueOf(exchange),
                                         interval,
                                         outputDirectory);    // -x <exchange> -o <outputDir> <symbols...>
      }
    }
  }

  @Override
  protected void stop() {
    try {
      converter.stop();
    }
    catch (final InterruptedException iE) {
      logger.error("Could not stop gracefully", iE);
    }
  }

  @Override
  protected List<File> result() {
    return destinations;
  }

  private static final void checkIllegalOptions(final OptionSet options, final OptionSpec<?>... illegalOptions) {
    for (final OptionSpec<?> illegalOption : illegalOptions) {
      if (options.has(illegalOption)) {
        throw new IllegalArgumentException("Illegal option: " + illegalOption);
      }
    }
  }

  private final Interval newInterval(final OptionSet options) {
    // form interval
    Calendar start = null;
    if (options.has(startDate)) { // -s <startDate>
      start = Calendar.getInstance();
      start.setTime(options.valueOf(startDate));
    }
    Calendar end = null;
    if (options.has(endDate)) {   // -e <endDate>
      end = Calendar.getInstance();
      end.setTime(options.valueOf(endDate));
    }
    // default: (null, null, DAILY)
    return new Interval(start,
                        end,
                        options.has(frequency) ? options.valueOf(frequency) // -f <frequency>
                                               : Frequencies.DAILY);
  }

}
