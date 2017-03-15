/**
 * ConverterTest.java  v0.1  5 March 2014 4:48:49 PM
 *
 * Copyright © 2014-2017 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.Interval.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ikankechil.eod3.io.SymbolsReaderTest;
import org.ikankechil.eod3.sources.Exchanges;
import org.ikankechil.eod3.sources.Morningstar;
import org.ikankechil.eod3.sources.Source;
import org.ikankechil.eod3.sources.SourceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>Converter</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class ConverterTest {

  private Converter                 converter;

  private static final Source       SOURCE            = new Morningstar();

  private static final String       SYMBOL1           = "C";
  private static final String       SYMBOL2           = "D";
  private static final String       SYMBOL3           = "E";
  private static final Exchanges    EXCHANGE          = Exchanges.NYSE;
  private static final Interval     INTERVAL_20151001_20151007_d;

  private static final File         OUTPUT_DIRECTORY  = new File(".//./src/test/resources/" + ConverterTest.class.getSimpleName());
  private static final File         SYMBOLS_FILE      = new File(OUTPUT_DIRECTORY, "Symbols.csv");

  private static final String       SPACE             = " ";
  private static final String       EMPTY             = "";
  private static final File         EMPTY_FILE        = new File(EMPTY);

  private static final DateFormat   DATE_FORMAT       = new SimpleDateFormat("yyyyMMdd", Locale.US);

  private static final Calendar     YESTERDAY         = Calendar.getInstance();
  private static final List<String> HOLIDAYS;
  private static final int          WEEKDAYS_PER_YEAR = (5 * 52); // 260
  private static final int          MIN_YEARS         = (YESTERDAY.get(YEAR) - 2002);

  @Rule
  public final ExpectedException    thrown            = ExpectedException.none();

  static {
    YESTERDAY.add(DATE, -1);

    HOLIDAYS = Arrays.asList("20170220",
                             "20110905", "20110704", "20110530", "20110422", "20110221",
                             "20110117", "20101224", "20101125", "20100906", "20100702",
                             "20100528", "20100401", "20100212", "20100115", "20091231",
                             "20091224", "20091125", "20090904", "20090702", "20090522",
                             "20090409", "20090213", "20090116", "20081231", "20081224",
                             "20081126", "20080829", "20080703", "20080523", "20080320",
                             "20080215", "20080118", "20071231", "20071224", "20071121",
                             "20070831", "20070703", "20070525", "20070405", "20070216",
                             "20070112");

    final Calendar start = Calendar.getInstance();
    start.set(2015, OCTOBER, 1);
    final Calendar end = (Calendar) start.clone();
    end.add(DATE, 6);
    INTERVAL_20151001_20151007_d = new Interval(start, end, Frequencies.DAILY);
  }

  @Before
  public void setUp() throws Exception {
    converter = new Converter(SOURCE);
  }

  @After
  public void tearDown() throws Exception {
    try {
      converter.stop();
    }
    finally {
      converter = null;
    }
  }

  @Test
  public void cannotInstantiateWithNullSource() {
    thrown.expect(NullPointerException.class);
    converter = new Converter(null);
  }

  @Test
  public void cannotInstantiateWithNullSource2() {
    thrown.expect(NullPointerException.class);
    converter = new Converter(null, null);
  }

  // Convert

  @Test
  public void cannotConvertEmptySymbol() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(SourceTest.EMPTY_SYMBOL);
    converter.convert(EMPTY);
  }

  @Test
  public void cannotConvertEmptySymbol2() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(SourceTest.EMPTY_SYMBOL);
    converter.convert(EMPTY, Exchanges.NASDAQ, SINCE_INCEPTION, null);
  }

  @Test
  public void cannotConvertSpaceSymbol() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(SourceTest.EMPTY_SYMBOL);
    converter.convert(SPACE);
  }

  @Test
  public void cannotConvertSpaceSymbol2() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(SourceTest.EMPTY_SYMBOL);
    converter.convert(SPACE, Exchanges.NASDAQ, SINCE_INCEPTION, null);
  }

  @Test
  public void cannotConvertNullSymbol() throws Exception {
    final String symbol = null;

    thrown.expect(NullPointerException.class);
    converter.convert(symbol);
  }

  @Test
  public void cannotConvertNullSymbol2() throws Exception {
    final String symbol = null;

    thrown.expect(NullPointerException.class);
    converter.convert(symbol, Exchanges.NASDAQ, SINCE_INCEPTION, null);
  }

  @Test
  public void cannotConvertFromFileWithNullInterval() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.convert(SYMBOLS_FILE, null, null);
  }

  @Test
  public void cannotConvertSymbolSeriesWithNullInterval() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.convert(Arrays.asList(SYMBOL1, SYMBOL2, SYMBOL3), EXCHANGE, null, null);
  }

  @Test
  public void cannotConvertSymbolWithNullInterval() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.convert(SYMBOL1, EXCHANGE, null, OUTPUT_DIRECTORY);
  }

  @Test
  public void convertFromFile() throws Exception {
    final File actualDirectory = converter.convert(SYMBOLS_FILE);
    assertTrue(actualDirectory.isDirectory());
    assertEquals(SOURCE.directory(), actualDirectory.getName());

    final Map<String, Set<String>> expectedMarkets =
        SymbolsReaderTest.readSymbolsFile(SYMBOLS_FILE, new HashMap<>());

    final File[] actualExchanges = actualDirectory.listFiles();
    assertEquals(expectedMarkets.size(), actualExchanges.length);

    for (final File actualExchange : actualExchanges) {
      assertTrue(actualExchange.isDirectory());

      final Set<String> expectedSymbols = expectedMarkets.get(actualExchange.getName());

      final File[] actualFiles = actualExchange.listFiles();
      for (final File actualFile : actualFiles) {
        assertTrue(actualFile.isFile());

        final String actualSymbol = FilenameConvention.getSymbolFrom(actualFile.getName());
        assertTrue(expectedSymbols.contains(actualSymbol));

        check(actualFile, actualSymbol, YESTERDAY, MIN_YEARS);
      }
    }

    delete(Arrays.asList(actualDirectory));
  }

  @Test
  public void convertSymbolSeries() throws Exception {
    final List<File> actualFiles = converter.convert(Arrays.asList(SYMBOL1, SYMBOL2, SYMBOL3));

    Collections.sort(actualFiles);
    final Iterator<File> iterator = actualFiles.iterator();
    final int minRows = WEEKDAYS_PER_YEAR * MIN_YEARS;
    check(iterator.next(), SYMBOL1, YESTERDAY, minRows);
    check(iterator.next(), SYMBOL2, YESTERDAY, minRows);
    check(iterator.next(), SYMBOL3, YESTERDAY, minRows);

    delete(actualFiles);
  }

  @Test
  public void convertSymbolSeriesOverInterval() throws Exception {
    final List<File> actualFiles = converter.convert(Arrays.asList(SYMBOL1, SYMBOL2, SYMBOL3),
                                                     EXCHANGE,
                                                     INTERVAL_20151001_20151007_d,
                                                     OUTPUT_DIRECTORY);

    Collections.sort(actualFiles);
    final Iterator<File> iterator = actualFiles.iterator();
    final Calendar endDate = INTERVAL_20151001_20151007_d.end();
    final int minRows = 5;
    check(iterator.next(), SYMBOL1, endDate, minRows);
    check(iterator.next(), SYMBOL2, endDate, minRows);
    check(iterator.next(), SYMBOL3, endDate, minRows);

    delete(actualFiles);
  }

  @Test
  public void convertSingleSymbol() throws Exception {
    final File actualFile = converter.convert(SYMBOL1);

    check(actualFile, SYMBOL1, YESTERDAY, WEEKDAYS_PER_YEAR * MIN_YEARS);
    delete(Arrays.asList(actualFile));
  }

  @Test
  public void convertSingleSymbolOverInterval() throws Exception {
    final File actualFile = converter.convert(SYMBOL1,
                                              EXCHANGE,
                                              INTERVAL_20151001_20151007_d,
                                              OUTPUT_DIRECTORY);

    check(actualFile, SYMBOL1, INTERVAL_20151001_20151007_d.end(), 5);
    delete(Arrays.asList(actualFile));
  }

  // Download

  @Test
  public void cannotDownloadEmptySymbol() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(SourceTest.EMPTY_SYMBOL);
    converter.download(EMPTY, Exchanges.NASDAQ, SINCE_INCEPTION, null);
  }

  @Test
  public void cannotDownloadNullSymbol() throws Exception {
    final String symbol = null;

    thrown.expect(NullPointerException.class);
    converter.download(symbol, Exchanges.NASDAQ, SINCE_INCEPTION, null);
  }

  @Test
  public void cannotDownloadFromFileWithNullInterval() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.download(SYMBOLS_FILE, null, null);
  }

  @Test
  public void cannotDownloadSymbolSeriesWithNullInterval() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.download(Arrays.asList(SYMBOL1, SYMBOL2, SYMBOL3), EXCHANGE, null, null);
  }

  @Test
  public void cannotDownloadSymbolWithNullInterval() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.download(SYMBOL1, EXCHANGE, null, OUTPUT_DIRECTORY);
  }

  // Update

  @Test
  public void cannotUpdateWithNullOutputParentDirectory() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.update(null);
  }

  @Test
  public void cannotUpdateWithFile() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    converter.update(SYMBOLS_FILE);
  }

  // Merge

  @Test
  public void cannotMergeWithNullOutputParentDirectory() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.merge(null);
  }

  @Test
  public void cannotMergeWithFile() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    converter.merge(EMPTY_FILE);
  }

  @Test
  public void cannotMergeWithEmptySourceFile() throws Exception {
    thrown.expect(IOException.class);
    converter.merge(EMPTY_FILE, EMPTY_FILE);
  }

  @Test
  public void cannotMergeWithNullSourceFile() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.merge(null, EMPTY_FILE);
  }

  @Test
  public void cannotMergeNullFiles() throws Exception {
    thrown.expect(NullPointerException.class);
    converter.merge((File) null, (File) null);
  }

  private static final void check(final File actualFile,
                                  final String symbol,
                                  final Calendar endDate,
                                  final int minRows)
      throws IOException {
    assertTrue(actualFile.isFile());
    final List<String> actuals = Files.readAllLines(actualFile.toPath());
    assertFalse(actuals.isEmpty());

    final Calendar expectedDate = (Calendar) endDate.clone();
    skipWeekend(expectedDate);
    for (final String actual : actuals) {
      assertTrue(String.format("Symbol %s does not prefix: %s", symbol, actual),
                 actual.startsWith(symbol));

      final String expectedDateStr = DATE_FORMAT.format(expectedDate.getTime());
      if (HOLIDAYS.contains(expectedDateStr)) {
        break;
      }
      assertTrue(String.format("Date %s not found in: %s", expectedDateStr, actual),
                 actual.contains(expectedDateStr));

      // get through the weekend
      expectedDate.add(DATE, -1);
      skipWeekend(expectedDate);
    }
    assertTrue("Actual: " + actuals.size() + " < Expected: " + minRows,
               actuals.size() >= minRows);
  }

  private static final void skipWeekend(final Calendar date) {
    int dayOfWeek = date.get(DAY_OF_WEEK);
    while (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY) { // not a weekday
      date.add(DATE, -1);
      dayOfWeek = date.get(DAY_OF_WEEK);
    }
  }

  private static final void delete(final Collection<? extends File> files) {
    for (final File file : files) {
      if (file.isDirectory()) {
        delete(Arrays.asList(file.listFiles()));
      }
      assertTrue(file.delete());
    }
  }

}
