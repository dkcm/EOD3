/**
 * YahooFinance2MetaStockTest.java	v1.0	7 June 2013 10:09:59 PM
 *
 * Copyright © 2013 Daniel Kuan. All rights reserved.
 */
package org.ikankechil.eod3.apps;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ikankechil.eod3.YahooFinanceDownloader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test for <code>YahooFinance2MetaStock</code>.
 *
 * @author Daniel
 * @version 1.0
 */
public class YahooFinance2MetaStockTest {

  private YahooFinanceDownloader    converter;

  private static final List<String> lines                 = new ArrayList<>();

  private static final String       BASE                  = "http://ichart.finance.yahoo.com/table.csv?s=";
  private static final String       A                     = "A";

  private static final String       CSV                   = ".csv";
  private static final String       EMPTY                 = "";
  private static final String       COMMA                 = ",";
  private static final int          ZERO                  = 0;
  private static final int          ONE                   = 1;
  private static final int          MINUS_ONE             = -1;

  private static final int          MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    String symbol = A;
    URL url = new URL(BASE + symbol);
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      // set row name
      StringBuilder builder = new StringBuilder(symbol).append(COMMA).append(line);
      // remove hyphens from date
      builder.deleteCharAt(7 + symbol.length() + 1).deleteCharAt(4 + symbol.length() + 1);
      // remove last column
      builder.delete(builder.lastIndexOf(COMMA), builder.length());

      lines.add(builder.toString());
    }
    lines.remove(ZERO);
    reader.close();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    lines.clear();
  }

  @Before
  public void setUp() throws Exception {
    converter = new YahooFinanceDownloader();
  }

  @After
  public void tearDown() throws Exception {
    converter = null;
  }

  @Test
  public final void testConvert() throws Exception {
    String symbol = A;
    File file = converter.download(symbol);

    check(file);

    assertEquals(symbol + CSV, file.getName());

    compare(file);

    assertTrue(file.delete());
    assertNotNull(file);
  }

  private static void check(final File file) {
    assertNotNull(file);
    assertTrue(file.isFile());
    assertFalse(file.isHidden());
    assertFalse(file.isDirectory());
  }

  protected void compare(final File file) throws FileNotFoundException, IOException {
    compare(file, null, null);
  }

  protected void compare(final File file, final Calendar start, final Calendar end)
      throws FileNotFoundException, IOException {
    String message = file.getName() + " line: ";

    BufferedReader reader = new BufferedReader(new FileReader(file));
    int lineIndex = MINUS_ONE;
    String actual;
    String expected;
    int days = ((start != null) && (end != null) && start.before(end)) ?
               (end.compareTo(start) / MILLISECONDS_IN_A_DAY):
               lines.size();
    while (((actual = reader.readLine()) != null) && (++lineIndex < days)) {
      expected = lines.get(lineIndex);
      assertEquals(message + lineIndex, expected, actual);
    }
    assertNull(actual);

    reader.close();
  }

  @Test
  public final void testConvertOneDay() throws Exception {
    String symbol = A;

    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DAY_OF_MONTH, MINUS_ONE);
    Calendar today = Calendar.getInstance();

    assertTrue(yesterday.before(today));

    File file = converter.download(symbol, yesterday, today);

    check(file);

    assertEquals(symbol + CSV, file.getName());

    compare(file, yesterday, today);

    assertTrue(file.delete());
    assertNotNull(file);
  }

  @Test
  public final void testConvertUnavailableDatesDisallowed() throws Exception {
    String symbol = A;

    Calendar today = Calendar.getInstance();
    Calendar tomorrow = Calendar.getInstance();
    tomorrow.add(Calendar.DAY_OF_MONTH, ONE);

    assertTrue(today.before(tomorrow));

    try {
      converter.download(symbol, today, tomorrow);
      fail("Unavailable dates");
    }
    catch (FileNotFoundException fnfE) {
      assertTrue(fnfE.getMessage().contains(BASE + A));
    }
  }

  @Test
  public final void testConvertEmptySymbolDisallowed() throws Exception {
    try {
      converter.download(EMPTY);
      fail("Empty symbol");
    }
    catch (IllegalArgumentException iaE) {
      assertNull(iaE.getMessage());
    }
  }

  @Test
  public final void testConvertNullSymbolDisallowed() throws Exception {
    try {
      converter.download(null);
      fail("Null symbol");
    }
    catch (NullPointerException npE) {
      assertNull(npE.getMessage());
    }
  }

}
