/**
 * ESD.java  v0.5  4 February 2015 1:29:53 PM
 *
 * Copyright © 2015-2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.ui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ikankechil.eod3.ExchangeSymbolsDownloader;
import org.ikankechil.eod3.sources.Exchanges;
import org.ikankechil.ui.AbstractCommandLineInterface;

import joptsimple.OptionException;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * A command-line application that downloads exchange symbols.
 *
 *
 * @author Daniel Kuan
 * @version 0.5
 */
public class ESD extends AbstractCommandLineInterface<Exchanges, Map<String, Set<String>>> {
  // TODO Enhancements
  // 1. [DONE] collate symbols file from local data directory
  // 2. [DONE] v0.4 new option: filter symbols
  // 3. report symbol counts in symbols file
  // 4. [DONE] v0.5 use abstract CLI superclass

  private Map<String, Set<String>> markets;

  // parameters
  private final OptionSpec<File>   outputSymbolsFile;
  private final OptionSpec<File>   inputDir;
  private final OptionSpec<Void>   filter;

  public ESD() {
    super("Exchanges " + Arrays.asList(Exchanges.values()), Exchanges.class);

    // command-line options:
    // -i input directory
    // -o output symbols file
    // -f filter non-RFC2396 compliant symbols

    // Configuring command-line options
    // parameters
    inputDir = parser.accepts("i", "Input directory")
                     .withRequiredArg()
                     .ofType(File.class);
    outputSymbolsFile = parser.accepts("o", "Output file")
                              .requiredIf(inputDir)
                              .withRequiredArg()
                              .ofType(File.class);
    filter = parser.accepts("f", "Filter non-RFC2396 compliant symbols");
  }

  public static void main(final String... arguments) throws Exception {
    new ESD().execute(arguments);
  }

  @Override
  protected void start() {
    markets = null;
  }

  @Override
  protected void workOn(final OptionSet options, final List<Exchanges> nonOptionArguments)
      throws OptionException, InterruptedException, IOException {
    final ExchangeSymbolsDownloader symbolsDownloader =
        new ExchangeSymbolsDownloader(options.valueOf(outputSymbolsFile), // -o <outputSymbolsFile>
                                      options.has(filter));               // -o <outputSymbolsFile> -f
    try {
      final Exchanges[] exchanges = nonOptionArguments.toArray(new Exchanges[nonOptionArguments.size()]);

      markets = options.has(inputDir) ?
                symbolsDownloader.collate(options.valueOf(inputDir), exchanges) : // -i <inputDir> <exchanges...>
                symbolsDownloader.download(exchanges);                            // <exchanges...>
    }
    finally {
      symbolsDownloader.stop();
    }
  }

  @Override
  protected void stop() { /* do nothing */ }

  @Override
  protected Map<String, Set<String>> result() {
    return markets;
  }

}
