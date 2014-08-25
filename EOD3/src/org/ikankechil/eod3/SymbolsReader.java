/**
 * SymbolsReader.java	v1.0	29 November 2013 12:08:24 AM
 *
 * Copyright © 2013-2014 Daniel Kuan. All rights reserved.
 */
package org.ikankechil.eod3.classic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikankechil.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that reads stock symbols in Yahoo! Finance format from file.
 *
 * @author Daniel Kuan
 * @version 1.0
 */
public class SymbolsReader {

  private final File              yahoo;
  private final Map<String, File> symbols;
  private final Map<String, File> exchanges;

  // Constants
  private static final char       COMMA  = ',';

  private static final Logger     logger = LoggerFactory.getLogger(SymbolsReader.class);

  public SymbolsReader(final File directory) {
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException();
    }
    yahoo = directory;
    exchanges = new HashMap<>();
    symbols = new HashMap<>();
  }

  public Map<String, File> readSymbols(final File file)
      throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      // Format (in alphabetical order)
      // Exchange, SymbolA1, SymbolA2, SymbolA3, etc.
      // Exchange, SymbolB1, SymbolB2, SymbolB3, etc.

      List<String> strings = StringUtility.split(line, COMMA);
      File exchange = getDirectory(strings.remove(0).toUpperCase());
      for (String symbol : strings) {
        symbols.put(symbol.trim(), exchange);
      }
    }
    reader.close();

    logger.info("Symbols read: " + symbols.size());
    logger.info("Exchanges read: " + exchanges.size());

    return symbols;
  }

  private File getDirectory(final String exchange) {
    File directory = null;
    if (exchanges.containsKey(exchange)) {
      directory = exchanges.get(exchange);
    }
    else {
      // create destination folders
      directory = new File(yahoo, exchange);
      directory.mkdir(); // TODO mkdir can be delayed till there are files to store
      exchanges.put(exchange, directory);
      logger.info("New directory created: " + exchange);
    }
    return directory;
  }

}
