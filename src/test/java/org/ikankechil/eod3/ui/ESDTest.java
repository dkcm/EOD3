/**
 * ESDTest.java v0.1 8 April 2015 10:41:36 AM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.ui;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.ikankechil.eod3.io.SymbolsReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>ESD</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class ESDTest {

  @Rule
  public ExpectedException    thrown              = ExpectedException.none();

  private static final ESD    CLI                 = new ESD();

  private static final String EMPTY               = "";
  private static final String SPACE               = " ";
  private static final String CSV                 = ".csv";

  private static final String O                   = "-o";
  private static final String I                   = "-i";

  private static final String OUTPUT_SYMBOLS_FILE = ESDTest.class.getSimpleName() + CSV;
  private static final File   INPUT_DIRECTORY     = new File(".//./tst/" + ESDTest.class.getName().replace('.', '/'));
  private static final String SOURCE_DIRECTORY    = new File(INPUT_DIRECTORY,
                                                             INPUT_DIRECTORY.list()[0]).getPath();

  private static final String EMPTY_EXCHANGE      = "Empty exchange: ";

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Files.deleteIfExists(Paths.get(OUTPUT_SYMBOLS_FILE));
  }

  @Before
  public void setUp() throws Exception {
    Files.deleteIfExists(Paths.get(OUTPUT_SYMBOLS_FILE));
  }

  @Test
  public void cannotExecuteWithNullArguments() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.main((String[]) null);
  }

  @Test
  public void cannotExecuteWithNullArguments2() throws Exception {
    thrown.expect(NullPointerException.class);
    CLI.execute((String[]) null);
  }

  @Test
  public void cannotExecuteWithNullArguments3() throws Exception {
    assertNull(CLI.execute((String) null));
    assertNull(CLI.execute(null, null));
    assertNull(CLI.execute(null, null, null));
  }

  @Test
  public void cannotExecuteWithEmptyArguments() throws Exception {
    assertNull(CLI.execute(EMPTY));
    assertNull(CLI.execute(EMPTY, EMPTY));
    assertNull(CLI.execute(EMPTY, EMPTY, EMPTY));
  }

  @Test
  public void cannotDownloadWithoutOutputFile() throws Exception {
    assertNull(CLI.execute(SGX.toString()));
    assertNull(CLI.execute(NYSE.toString(), NASDAQ.toString()));
  }

  @Test
  public void cannotDownloadWithoutOutputFile2() throws Exception {
    assertNull(CLI.execute(O, null, NYSE.toString()));
  }

  @Test
  public void cannotDownloadWithoutOutputFile3() throws Exception {
    assertNull(CLI.execute(O, EMPTY, NYSE.toString()));
  }

  @Test
  public void cannotDownloadWithoutOutputFile4() throws Exception {
    assertNull(CLI.execute(O, SPACE, NYSE.toString()));
  }

  @Test
  public void downloadOneExchangeToOutputFile() throws Exception {
    final String exchange = NSE.toString();

    ESD.main(O, OUTPUT_SYMBOLS_FILE, exchange);

    final Map<String, Set<String>> markets = new SymbolsReader().read(new File(OUTPUT_SYMBOLS_FILE));

    assertEquals(1, markets.size());
    assertTrue(EMPTY_EXCHANGE + exchange,
               markets.get(exchange).size() > 0);
  }

  @Test
  public void downloadOneExchangeToOutputFile2() throws Exception {
    final String exchange = SGX.toString();

    final Map<String, Set<String>> markets = CLI.execute(O,
                                                         OUTPUT_SYMBOLS_FILE,
                                                         exchange);

    assertEquals(1, markets.size());
    assertTrue(EMPTY_EXCHANGE + exchange,
               markets.get(exchange).size() > 0);
  }

  @Test
  public void downloadMultipleExchangesToOutputFile() throws Exception {
    final String exchange1 = AMEX.toString();
    final String exchange2 = NSE.toString();

    final Map<String, Set<String>> markets = CLI.execute(O,
                                                         OUTPUT_SYMBOLS_FILE,
                                                         exchange1,
                                                         exchange2);

    assertEquals(2, markets.size());
    assertTrue(EMPTY_EXCHANGE + exchange1,
               markets.get(exchange1).size() > 0);
    assertTrue(EMPTY_EXCHANGE + exchange2,
               markets.get(exchange2).size() > 0);
  }

  @Test
  public void collateSymbolsFromDirectory1() throws Exception {
    final String exchange1 = NASDAQ.toString();
    final String exchange2 = NYSE.toString();
    final String exchange3 = SGX.toString();

    ESD.main(I,
             SOURCE_DIRECTORY,
             O,
             OUTPUT_SYMBOLS_FILE,
             exchange1,
             exchange2,
             exchange3);

    final Map<String, Set<String>> markets = new SymbolsReader().read(new File(OUTPUT_SYMBOLS_FILE));

    assertEquals(2, markets.size());
    assertEquals(2, markets.get(exchange1).size());
    assertEquals(18, markets.get(exchange2).size());
    assertEquals(null, markets.get(exchange3));
  }

  @Test
  public void collateSymbolsFromDirectory2() throws Exception {
    final String exchange1 = NASDAQ.toString();
    final String exchange2 = NYSE.toString();
    final String exchange3 = SGX.toString();

    final Map<String, Set<String>> markets = CLI.execute(I,
                                                         SOURCE_DIRECTORY,
                                                         O,
                                                         OUTPUT_SYMBOLS_FILE,
                                                         exchange1,
                                                         exchange2,
                                                         exchange3);

    assertEquals(2, markets.size());
    assertEquals(2, markets.get(exchange1).size());
    assertEquals(18, markets.get(exchange2).size());
    assertEquals(null, markets.get(exchange3));
  }

}
