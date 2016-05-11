/**
 * SymbolsWriterTest.java v0.5 17 December 2014 7:37:07 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>SymbolsWriter</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.5
 */
public class SymbolsWriterTest {

  private static final SymbolsWriter            WRITER       = new SymbolsWriter();
  public static final File                      SYMBOLS_FILE = new File(".//./src/test/resources/" + SymbolsWriterTest.class.getSimpleName(),
                                                                       "Symbols.csv");

  private static final Map<String, Set<String>> EXPECTEDS    = new LinkedHashMap<>();
  private static final Map<String, Set<String>> UNEXPECTEDS  = new LinkedHashMap<>();
  private static final Map<String, Set<String>> ACTUALS      = new LinkedHashMap<>();

  @Rule
  public ExpectedException                      thrown       = ExpectedException.none();

  private static final String                   COMMA        = ",";
  private static final String                   EMPTY        = "";
  private static final String                   SPACE        = " ";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // ought to be written
    EXPECTEDS.put("nyse", new TreeSet<>(Arrays.asList("A", "AA", "ACN", "ALG",
                                                      "B", "BZH",
                                                      "C",
                                                      "IBM")));
    EXPECTEDS.put("nasdaq", new TreeSet<>(Arrays.asList("INTC", "MSFT")));
    EXPECTEDS.put("nysearca", new TreeSet<>(Arrays.asList("SPY")));

    EXPECTEDS.put("lse", new TreeSet<>(Arrays.asList(EMPTY, "BARC")));

    // ought not to be written
    UNEXPECTEDS.put("sgx", new HashSet<>(Arrays.asList(EMPTY)));
    UNEXPECTEDS.put("hkse", new HashSet<>(Arrays.asList(SPACE)));
    UNEXPECTEDS.put("sse", new HashSet<>(Arrays.asList((String) null)));
    UNEXPECTEDS.put(EMPTY, new HashSet<>(Arrays.asList("1")));
    UNEXPECTEDS.put(SPACE, new HashSet<>(Arrays.asList("2")));
    UNEXPECTEDS.put(null, new HashSet<>(Arrays.asList("3")));

    final Map<String, Set<String>> markets = new LinkedHashMap<>(EXPECTEDS);
    markets.putAll(UNEXPECTEDS);
    WRITER.write(markets, SYMBOLS_FILE);

    readSymbolsFile();

    // remove illegal symbols from expected results
    for (final String expected : EXPECTEDS.keySet()) {
      if (expected == null || expected.trim().isEmpty()) {
        EXPECTEDS.remove(expected);
      }
      else {
        final Set<String> symbols = EXPECTEDS.get(expected);
        symbols.remove(EMPTY);
        symbols.remove(SPACE);
        try {
          symbols.remove(null);
        }
        catch (final NullPointerException npE) { /* do nothing */ }
      }
    }
  }

  private static final void readSymbolsFile() throws IOException {
    final List<String> lines = Files.readAllLines(SYMBOLS_FILE.toPath(), StandardCharsets.UTF_8);
    for (final String line : lines) {
      final String[] strings = line.split(COMMA);
      String exchange;
      if ((strings.length > 0) &&
          !(exchange = strings[0]).isEmpty()) {
        if (!ACTUALS.containsKey(exchange)) {
          ACTUALS.put(exchange, new TreeSet<String>());
        }
        final Set<String> symbols = ACTUALS.get(exchange);
        for (int i = 1; i < strings.length; ++i) {
          final String symbol = strings[i];
          symbols.add(symbol);
        }
      }
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    EXPECTEDS.clear();
    ACTUALS.clear();
  }

  @Test
  public void cannotWriteToNullFile() throws Exception {
    thrown.expect(NullPointerException.class);
    WRITER.write(EXPECTEDS, (File) null);
  }

  @Test
  public void cannotWriteToNullOutputStream() throws Exception {
    thrown.expect(NullPointerException.class);
    WRITER.write(EXPECTEDS, (OutputStream) null);
  }

  @Test
  public void cannotWriteToDirectory() throws Exception {
    thrown.expect(FileNotFoundException.class);
    WRITER.write(EXPECTEDS, SYMBOLS_FILE.getParentFile());
  }

  @Test
  public void cannotWriteNullSymbols() throws Exception {
    thrown.expect(NullPointerException.class);
    WRITER.write(null, SYMBOLS_FILE);
  }

  @Test
  public void fileContentsMatch() throws Exception {
    assertEquals(EXPECTEDS, ACTUALS);
    assertNotSame(EXPECTEDS, ACTUALS);

    assertFalse(ACTUALS.keySet().containsAll(UNEXPECTEDS.keySet()));
  }

  @Test
  public void convertFormat() throws Exception {
    // Map<Symbol, Exchange>
    final Map<String, String> symbols = new HashMap<>();
    for (final Entry<String, Set<String>> expected : EXPECTEDS.entrySet()) {
      final String exchange = expected.getKey();
      for (final String symbol : expected.getValue()) {
        symbols.put(symbol, exchange);
      }
    }

    // Map<Symbol, Exchange> -> Map<Exchange, Set<Symbol>>
    assertEquals(EXPECTEDS, WRITER.convert(symbols));
  }

  @Test
  public void convertFormatForNullAndEmptyExchanges() throws Exception {
    final Map<String, String> exchanges = new HashMap<>();
    boolean isAlternate = true;
    for (final Set<String> symbols : EXPECTEDS.values()) {
      for (final String symbol : symbols) {
        exchanges.put(symbol, isAlternate ? null : EMPTY);
      }
      isAlternate = !isAlternate;
    }

    assertTrue(WRITER.convert(exchanges).isEmpty());
  }

}
