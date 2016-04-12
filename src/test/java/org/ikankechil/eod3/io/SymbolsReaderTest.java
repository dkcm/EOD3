/**
 * SymbolsReaderTest.java	v0.1	9 January 2014 12:43:33 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ikankechil.io.TextReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>SymbolsReader</code>.
 *
 * @author Daniel Kuan
 * @version
 */
public class SymbolsReaderTest {

  private static final SymbolsReader            READER       = new SymbolsReader();
  public static final File                      SYMBOLS_FILE = new File(".//./tst/" + SymbolsReaderTest.class.getName().replace('.', '/'),
                                                                        "Symbols.csv");

  private static final Map<String, Set<String>> EXPECTEDS    = new HashMap<>();
  private static Map<String, Set<String>>       ACTUALS;

  @Rule
  public ExpectedException                      thrown       = ExpectedException.none();

  // Constants
  private static final String                   COMMA        = ",";
  private static final String                   SPACE        = " ";
  private static final String                   EMPTY        = "";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    readSymbolsFile();
    ACTUALS = Collections.unmodifiableMap(READER.read(SYMBOLS_FILE));
  }

  private static final void readSymbolsFile() throws IOException {
    final List<String> lines = Files.readAllLines(SYMBOLS_FILE.toPath(), StandardCharsets.UTF_8);
    for (final String line : lines) {
      // remove spaces and convert to upper case
      final String[] strings = line.replace(SPACE, EMPTY).toUpperCase().split(COMMA);
      String exchange;
      if ((strings.length > 0) &&
          !(exchange = strings[0]).isEmpty()) {
        // filter out duplicates
        final Set<String> symbols = new HashSet<>(Arrays.asList(strings));
        symbols.remove(exchange);
        if (!symbols.isEmpty()) {
          if (EXPECTEDS.containsKey(exchange)) {
            EXPECTEDS.get(exchange).addAll(symbols);
          }
          else {
            EXPECTEDS.put(exchange, symbols);
          }
        }
      }
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    EXPECTEDS.clear();
  }

  @Test
  public void cannotReadNullFile() throws Exception {
    thrown.expect(NullPointerException.class);
    READER.read(null);
  }

  @Test
  public void cannotReadDirectory() throws Exception {
    thrown.expect(FileNotFoundException.class);
    READER.read(SYMBOLS_FILE.getParentFile());
  }

  @Test
  public void fileContentsMatch() throws Exception {
    assertEquals(EXPECTEDS, ACTUALS);
    assertNotSame(EXPECTEDS, ACTUALS);
  }

  @Test
  public void exchangesAreTrimmed() throws Exception {
    for (final String exchange : ACTUALS.keySet()) {
      assertFalse(exchange, exchange.contains(SPACE));
    }
  }

  @Test
  public void symbolsAreTrimmed() throws Exception {
    for (final Set<String> symbols : ACTUALS.values()) {
      for (final String symbol : symbols) {
        assertFalse(symbol, symbol.contains(SPACE));
      }
    }
  }

  @Test
  public void exchangesAreUpperCase() throws Exception {
    assertEquals(EXPECTEDS.keySet(), ACTUALS.keySet());
  }

  @Test
  public void symbolsAreUpperCase() throws Exception {
    for (final Entry<String, Set<String>> expected : EXPECTEDS.entrySet()) {
      final String exchange = expected.getKey();
      assertEquals(exchange, expected.getValue(), ACTUALS.get(exchange));
    }
  }

  @Test
  public void emptyExchangesIgnored() throws Exception {
    final Set<String> exchanges = ACTUALS.keySet();
    assertFalse(exchanges.toString(), exchanges.contains(EMPTY));
  }

  @Test
  public void memberlessExchangesIgnored() throws Exception {
    for (final Entry<String, Set<String>> actual : ACTUALS.entrySet()) {
      final Set<String> symbols = actual.getValue();
      assertTrue(actual.toString(), symbols.size() > 0);
    }
  }

  @Test
  public void emptySymbolsIgnored() throws Exception {
    for (final Entry<String, Set<String>> actual : ACTUALS.entrySet()) {
      final Set<String> symbols = actual.getValue();
      assertFalse(actual.toString(), symbols.contains(EMPTY));
    }
  }

  @Test
  public void spacesSymbolsIgnored() throws Exception {
    for (final Entry<String, Set<String>> actual : ACTUALS.entrySet()) {
      final Set<String> symbols = actual.getValue();
      assertFalse(actual.toString(), symbols.contains(SPACE));
    }
  }

  @Test
  public void symbolsAlphabeticallySorted() throws Exception {
    for (final Entry<String, Set<String>> expected : EXPECTEDS.entrySet()) {
      final String[] symbols = expected.getValue().toArray(new String[0]);
      Arrays.sort(symbols);

      final String exchange = expected.getKey();
      assertArrayEquals(exchange, symbols, ACTUALS.get(exchange).toArray());
    }
  }

  @Test
  public void instantiateWithTextReader() throws Exception {
    final SymbolsReader nullReader = new SymbolsReader(new TextReader() {
      @Override
      public List<String> read(final File source) throws FileNotFoundException, IOException {
        return null;
      }
    });

    thrown.expect(NullPointerException.class);
    nullReader.read(SYMBOLS_FILE);
  }

}
