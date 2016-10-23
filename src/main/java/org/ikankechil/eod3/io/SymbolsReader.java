/**
 * SymbolsReader.java  v2.0  7 January 2014 11:41:34 PM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.io;

import static org.ikankechil.util.StringUtility.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads stock symbols from a proprietary format.
 *
 * @author Daniel Kuan
 * @version 2.0
 */
public class SymbolsReader {

  Map<String, Set<String>>      markets;  // Map<Exchange, Set<Symbol>>

  private final TextReader      reader;
  private final TextTransformer transformer;

  private static final String   EMPTY  = "";

  static final Logger           logger = LoggerFactory.getLogger(SymbolsReader.class);

  public SymbolsReader() {
    this(null);
  }

  public SymbolsReader(final TextReader reader) {
    this.reader = (reader == null) ? new TextReader() : reader;
    transformer = new TextTransformer(new SymbolsTransform());
  }

  /**
   * Reads symbols from a file.
   *
   * @param source symbols <code>File</code>
   * @return A <code>Map</code> of exchanges to <code>Set</code>s of symbols as
   *         <code>String</code>s, none of which are empty or spaces
   * @throws FileNotFoundException if the file does not exist, is a directory
   *           rather than a regular file, or for some other reason cannot be
   *           opened for reading
   * @throws IOException if an I/O error occurs
   */
  public Map<String, Set<String>> read(final File source) throws FileNotFoundException, IOException {
    logger.info("Reading symbols from: {}", source);
    markets = new HashMap<>();

    final List<String> lines = reader.read(source);
    transformer.transform(lines);
    logger.info("Markets populated: {}", markets.size());

    sort();

    logger.info("Symbols read from: {}", source);
    return markets; // Map<Exchange, Set<Symbol>>
  }

  private void sort() {
    int count = 0;
    for (final Set<String> symbols : markets.values()) {
      symbols.remove(EMPTY);
      count += symbols.size();
    }
    logger.info("Symbols sorted");
    logger.info("Symbols read: {}", count);
  }

  class SymbolsTransform implements TextTransform {

    // Constants
    private static final char   COMMA = ',';
    private static final String SPACE = " ";

    @Override
    public String transform(final String line) {
      // Format (in alphabetical order)
      // Exchange1, Symbol1A1, Symbol1A2, Symbol1A3, etc.
      // Exchange1, Symbol1B1, Symbol1B2, Symbol1B3, etc.
      //
      // Exchange2, Symbol2A1, Symbol2A2, Symbol2A3, etc.
      // Exchange2, Symbol2C1, Symbol2C2, Symbol2C3, etc.
      //
      // Exchange3, Symbol3D1, Symbol3D2, Symbol3D3, etc.

      // remove spaces
      List<String> symbols = split(line.replace(SPACE, EMPTY).toUpperCase(),
                                   COMMA);
      final String exchange = symbols.get(0);
      symbols = symbols.subList(1, symbols.size());

      // ignore empty exchange and symbols
      if (exchange.isEmpty()) {
        logger.debug("Empty exchange omitted: {}", exchange);
      }
      else if (symbols.isEmpty()) {
        logger.debug("Member-less exchange omitted: {}", exchange);
      }
      else {
        final Set<String> incumbents = markets.get(exchange);
        if (incumbents == null) {
          // Map<Exchange, Set<Symbol>>
          markets.put(exchange, new TreeSet<>(symbols)); // remove duplicates
          logger.debug("New exchange added: {}", exchange);
        }
        else {
          final int count = incumbents.size();
          incumbents.addAll(symbols);
          logger.debug("New symbols added: {}", (incumbents.size() - count));
        }
      }

      return exchange;
    }

  }

}
