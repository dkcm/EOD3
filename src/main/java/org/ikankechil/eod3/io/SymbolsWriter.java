/**
 * SymbolsWriter.java	v0.4	25 January 2014 12:05:34 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.ikankechil.io.TextWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes stock symbols in a proprietary format.
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class SymbolsWriter {

  private final TextWriter    writer;

  private static final int    ZERO   = 0;
  private static final char   SPACE  = ' ';
  private static final char   COMMA  = ',';
  private static final String EMPTY  = "";
//  private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E',
//                                           'F', 'G', 'H', 'I', 'J',
//                                           'K', 'L', 'M', 'N', 'O',
//                                           'P', 'Q', 'R', 'S', 'T',
//                                           'U', 'V', 'W', 'X', 'Y',
//                                           'Z' };

  private static final Logger logger = LoggerFactory.getLogger(SymbolsWriter.class);

  public SymbolsWriter() {
    writer = new TextWriter();
  }

  /**
   * Writes symbols and their exchanges to file in proprietary format.
   *
   * @param markets
   * @param destination
   * @throws IOException if an I/O error occurs
   */
  public void write(final Map<String, Set<String>> markets, final File destination) throws IOException {
    // Prepare lines to be written
    final List<String> lines = prepare(markets);

    writer.write(lines, destination);
  }

  /**
   * Map&ltSymbol, Exchange> -> Map&ltExchange, Set<Symbol>>
   *
   * @param symbols
   * @return
   */
  public Map<String, Set<String>> convert(final Map<String, String> symbols) {
    final Map<String, Set<String>> markets = new LinkedHashMap<>();

    // Convert to proprietary format
    // Map<Symbol, Exchange> -> Map<Exchange, Set<Symbol>>
    for (final Entry<String, String> symbolAndExchange : symbols.entrySet()) { // Map<Symbol, Exchange>
      final String symbol = symbolAndExchange.getKey();
      final String exchange = symbolAndExchange.getValue();

      if ((exchange == null) || exchange.isEmpty()) {
        logger.warn("No exchange for: {}", symbol);
      }
      else {
        if (!markets.containsKey(exchange)) {
          // Map<Exchange, Set<Symbol>>
          // remove duplicates and sort by alphabetical order
          markets.put(exchange, new TreeSet<String>());
          logger.info("New exchange added: {}", exchange);
        }
        markets.get(exchange).add(symbol);
      }
    }

    return markets;
  }

  private static final List<String> prepare(final Map<String, Set<String>> markets) {
    final List<String> lines = newList();

    for (final Entry<String, Set<String>> market : markets.entrySet()) { // Map<Exchange, Set<Symbol>>
      // split into buckets
      // Format (in alphabetical order)
      // Exchange1, Symbol1A1, Symbol1A2, Symbol1A3, etc.
      // Exchange1, Symbol1B1, Symbol1B2, Symbol1B3, etc.
      //
      // Exchange2, Symbol2A1, Symbol2A2, Symbol2A3, etc.
      // Exchange2, Symbol2C1, Symbol2C2, Symbol2C3, etc.
      //
      // Exchange3, Symbol3D1, Symbol3D2, Symbol3D3, etc.

      char current = SPACE;
      final StringBuilder line = new StringBuilder();
      for (final String symbol : market.getValue()) {
        final char start = symbol.charAt(ZERO);
        if (start != current) { // change in starting letter
          final String exchange = market.getKey();
          if (line.length() > ZERO) {
            lines.add(line.toString());
            line.replace(ZERO, line.length(), exchange);
          }
          else {
            line.append(exchange);
          }
          current = start;
        }
        line.append(COMMA).append(symbol);
      }
      lines.add(line.toString());
      lines.add(EMPTY);
    }

    return lines;
  }

  private static final <E> List<E> newList() {
    return new ArrayList<>();
  }

}
