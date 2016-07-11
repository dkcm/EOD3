/**
 * EOD3Test.java v0.5 8 April 2015 10:41:13 AM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.ui;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.eod3.sources.Source;
import org.ikankechil.eod3.sources.YahooFinance;
import org.ikankechil.util.FileUtility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>EOD3</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.5
 */
public class EOD3Test {
  // TODO scalable way to test option combinations (both legal and illegal)

  @Rule
  public ExpectedException          thrown           = ExpectedException.none();

  private static final YahooFinance YAHOO_FINANCE    = new YahooFinance();
  private static final EOD3         CLI              = new EOD3(YAHOO_FINANCE);

  private static final DateFormat   DATE_FORMAT      = new SimpleDateFormat("yyyyMMdd", Locale.US);

  private static final File         OUTPUT_DIRECTORY = new File(".//./src/test/resources/" + EOD3Test.class.getSimpleName());
  private static final File         SYMBOLS_FILE     = new File(OUTPUT_DIRECTORY, "Symbols.csv");

  private static final String       EMPTY            = "";
  private static final String       SPACE            = " ";
  private static final String       CSV              = ".csv";
  private static final String       UNDERSCORE       = "_";
  private static final char         DASH             = '-';

  // properties
  private static final String       SOURCE           = Source.class.getPackage().getName();

  // command-line options
  private static final String       I                = "-i";
  private static final String       O                = "-o";
  private static final String       D                = "-d";
  private static final String       S                = "-s";
  private static final String       E                = "-e";
  private static final String       F                = "-f";
  private static final String       X                = "-x";
  private static final String       U                = "-u";
  private static final String       M                = "-m";

  // command-line parameters
  private static final String       NYSE             = "NYSE";
  private static final String       HKSE             = "HKSE";
  private static final String       AIC              = "AIC";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // set properties
    setSourceProperty();
  }

  private static final void setSourceProperty() {
    System.setProperty(SOURCE, YAHOO_FINANCE.getClass().getSimpleName());
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    // clear properties
    System.setProperty(SOURCE, EMPTY);
  }

  @SuppressWarnings("unused")
  @Test
  public void cannotInstantiateWithNullSource() {
    thrown.expect(NullPointerException.class);
    new EOD3(null);
  }

  @Test
  public void cannotExecuteWithNullSourceSystemProperty() throws Exception {
    System.clearProperty(SOURCE);
    EOD3.main((String) null);
    setSourceProperty();
  }

  @Test
  public void cannotExecuteWithEmptySourceSystemProperty() throws Exception {
    System.setProperty(SOURCE, EMPTY);
    EOD3.main((String) null);
    setSourceProperty();
  }

  @Test
  public void cannotExecuteWithNullArguments() throws Exception {
    thrown.expect(NullPointerException.class);
    EOD3.main((String[]) null);
  }

  @Test
  public void cannotExecuteWithNullArguments2() throws Exception {
    thrown.expect(NullPointerException.class);
    CLI.execute((String[]) null);
  }

  @Test
  public void cannotExecuteWithNullArguments3() throws Exception {
    EOD3.main((String) null);
    EOD3.main(null, null);

    CLI.execute((String) null);
    CLI.execute(null, null);
    assertTrue(CLI.execute((String) null).isEmpty());
    assertTrue(CLI.execute(null, null).isEmpty());
  }

  @Test
  public void cannotExecuteWithEmptyArguments() throws Exception {
    assertTrue(CLI.execute(EMPTY).isEmpty());
    assertTrue(CLI.execute(EMPTY, EMPTY).isEmpty());
    assertTrue(CLI.execute(EMPTY, EMPTY, EMPTY).isEmpty());

    assertTrue(CLI.execute(new String[0]).isEmpty());
    assertTrue(CLI.execute(new String[1]).isEmpty());
    assertTrue(CLI.execute(new String[2]).isEmpty());
  }

  @Test
  public void cannotUpdateWithoutOutputDirectory() throws Exception {
    withoutOutputDirectory(U);
  }

  @Test
  public void cannotMergeWithoutOutputDirectory() throws Exception {
    withoutOutputDirectory(M);
  }

  private static final void withoutOutputDirectory(final String option) throws Exception {
    assertTrue(CLI.execute(option).isEmpty());
    assertTrue(CLI.execute(option, O).isEmpty());
    assertTrue(CLI.execute(option, O, EMPTY).isEmpty());
    assertTrue(CLI.execute(option, O, null).isEmpty());
  }

  @Test
  public void cannotUpdateNonExistentDirectory() throws Exception {
    nonExistentDirectory(U);
  }

  @Test
  public void cannotMergeNonExistentDirectory() throws Exception {
    nonExistentDirectory(M);
  }

  private final void nonExistentDirectory(final String option) throws Exception {
    final String nonExistent = "non-existent directory";
    assertTrue(CLI.execute(option, O, nonExistent).isEmpty());
  }

  @Test
  public void updateRequiresNoArguments() throws Exception {
    requiresNoArguments(U);
  }

  @Test
  public void mergeRequiresNoArguments() throws Exception {
    requiresNoArguments(M);
  }

  @Test
  public void downloadRequiresNoArguments() throws Exception {
    requiresNoArguments(D);
  }

  @Test
  public void inputSymbolsFileRequiresNoArguments() throws Exception {
    requiresNoArguments(I);
  }

  private static final void requiresNoArguments(final String option) throws Exception {
    assertTrue(CLI.execute(option, EMPTY).isEmpty());
    assertTrue(CLI.execute(option, null).isEmpty());
  }

  @Test
  public void cannotUpdateWithIllegalOptions() throws Exception {
    final String[] illegalOptions = { I, D, F, X };
    illegalOptions(U, illegalOptions);
  }

  @Test
  public void cannotMergeWithIllegalOptions() throws Exception {
    final String[] illegalOptions = { I, D, F, X };
    illegalOptions(M, illegalOptions);
  }

  private static final void illegalOptions(final String legalOption, final String... illegalOptions)
      throws Exception {
    for (final String illegalOption : illegalOptions) {
      final List<File> actuals = CLI.execute(legalOption,
                                             O,
                                             OUTPUT_DIRECTORY.getPath(),
                                             illegalOption);
      assertTrue(illegalOption, actuals.isEmpty());
    }
  }

  @Test
  public void update() throws Exception {
    final List<File> actuals = CLI.execute(U, O, OUTPUT_DIRECTORY.getPath());
    assertEquals(OUTPUT_DIRECTORY, actuals.get(0));
    assertEquals(1, actuals.size());
  }

  @Test
  public void merge() throws Exception {
    final List<File> actuals = CLI.execute(M, O, OUTPUT_DIRECTORY.getPath());
    assertEquals(OUTPUT_DIRECTORY, actuals.get(0));
    assertEquals(1, actuals.size());
  }

  @Test
  public void cannotConvertWithoutSymbols() throws Exception {
    assertTrue(CLI.execute(X, NYSE).isEmpty());
  }

  @Test
  public void cannotConvertWithoutSymbolsFile() throws Exception {
    assertTrue(CLI.execute(I).isEmpty());
  }

  @Test
  public void convertSymbols() throws Exception {
    // -x <exchange> <symbols...>
    final List<File> actuals = CLI.execute(X, NYSE, AIC);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.DAILY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void convertSymbolsWithOutputDirectory() throws Exception {
    // -x <exchange> <symbols...> -o <outputDirectory>
    final List<File> actuals = CLI.execute(X, NYSE, AIC, O, OUTPUT_DIRECTORY.getPath());
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.DAILY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void convertSymbolsWithFrequency() throws Exception {
    // -x <exchange> <symbols...> -f <frequency>
    final List<File> actuals = CLI.execute(X, NYSE, AIC, F, Frequencies.MONTHLY.toString());
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.MONTHLY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void convertSymbolsWithStartDate() throws Exception {
    // -x <exchange> <symbols...> -s <startDate>
    final Calendar start = Calendar.getInstance();
    final String endDate = DATE_FORMAT.format(start.getTime());
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());

    final List<File> actuals = CLI.execute(X, NYSE, AIC, S, startDate);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + startDate + DASH + endDate + UNDERSCORE + Frequencies.DAILY.frequency() + CSV,
                 actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void cannotConvertSymbolsWithEndDateOnly() throws Exception {
    // -x <exchange> <symbols...> -e <endDate>
    final Calendar start = Calendar.getInstance();

    final List<File> actuals = CLI.execute(X, NYSE, AIC, E, DATE_FORMAT.format(start.getTime()));

    assertTrue(actuals.isEmpty());
  }

  @Test
  public void convertSymbolsWithStartAndEndDates() throws Exception {
    // -x <exchange> <symbols...> -s <startDate> -e <endDate>
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(X,
                                           NYSE,
                                           AIC,
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + startDate + DASH + endDate + UNDERSCORE + Frequencies.DAILY.frequency() + CSV,
                 actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void convertSymbolsWithOutputDirectoryStartAndEndDates() throws Exception {
    // -x <exchange> <symbols...> -o <outputDirectory> -s <startDate> -e <endDate>
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(X,
                                           NYSE,
                                           AIC,
                                           O,
                                           OUTPUT_DIRECTORY.getPath(),
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + startDate + DASH + endDate + UNDERSCORE + Frequencies.DAILY.frequency() + CSV,
                 actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void convertIgnoresSpacesAndEmptySymbols() throws Exception {
    final List<File> actuals = CLI.execute(X, NYSE, EMPTY, SPACE, AIC);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.DAILY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void convertSymbolsFile() throws Exception {
    // -i <inputSymbolsFile>
    final List<File> actuals = CLI.execute(I, SYMBOLS_FILE.getPath());
    checkDestinations(actuals, null, null, Frequencies.DAILY);
  }

  @Test
  public void convertSymbolsFileWithOutputDirectory() throws Exception {
    // -i <inputSymbolsFile> -o <outputDirectory>
    final List<File> actuals = CLI.execute(I,
                                           SYMBOLS_FILE.getPath(),
                                           O,
                                           OUTPUT_DIRECTORY.getPath());
    checkDestinations(actuals, null, null, Frequencies.DAILY);
  }

  @Test
  public void cannotConvertSymbolsFileWithEndDateOnly() throws Exception {
    // -i <inputSymbolsFile> -e <endDate>
    final Calendar start = Calendar.getInstance();

    final List<File> actuals = CLI.execute(I,
                                           SYMBOLS_FILE.getPath(),
                                           E,
                                           DATE_FORMAT.format(start.getTime()));

    assertTrue(actuals.isEmpty());
  }

  @Test
  public void convertSymbolsFileWithStartAndEndDates() throws Exception {
    // -i <inputSymbolsFile> -s <startDate> -e <endDate>
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(I,
                                           SYMBOLS_FILE.getPath(),
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    checkDestinations(actuals, startDate, endDate, Frequencies.DAILY);
  }

  @Test
  public void convertSymbolsFileWithOutputDirectoryStartAndEndDates() throws Exception {
    // -i <inputSymbolsFile> -o <outputDirectory> -s <startDate> -e <endDate>
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(I,
                                           SYMBOLS_FILE.getPath(),
                                           O,
                                           OUTPUT_DIRECTORY.getPath(),
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    checkDestinations(actuals, startDate, endDate, Frequencies.DAILY);
  }

  @Test
  public void convertSymbolsFileIgnoresExplicitExchange() throws Exception {
    final List<File> actuals = CLI.execute(I, SYMBOLS_FILE.getPath(), X, HKSE);
    checkDestinations(actuals, null, null, Frequencies.DAILY);
  }

  private static final void checkDestinations(final List<? extends File> actuals,
                                              final String startDate,
                                              final String endDate,
                                              final Frequencies frequency)
      throws IOException {
    final File actual = actuals.get(0);

    assertEquals(YAHOO_FINANCE.directory(), actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.isDirectory());

    final File actualExchange = new File(actual, actual.list()[0]);
    assertEquals(NYSE, actualExchange.getName());
    final String expected = AIC +
                            ((startDate != null && endDate != null) ? UNDERSCORE + startDate + DASH + endDate : EMPTY) +
                            (frequency != null ? UNDERSCORE + frequency.frequency() : EMPTY) +
                            CSV;
    assertEquals(expected,
                 new File(actualExchange, actualExchange.list()[0]).getName());

    FileUtility.deleteFileTree(actual.toPath());
  }

  @Test
  public void cannotDownloadWithoutSymbols() throws Exception {
    assertTrue(CLI.execute(D, X, NYSE).isEmpty());
  }

  @Test
  public void cannotDownloadWithoutSymbolsFile() throws Exception {
    assertTrue(CLI.execute(D, I).isEmpty());
  }

  @Test
  public void downloadSymbols() throws Exception {
    final List<File> actuals = CLI.execute(D, X, NYSE, AIC);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.DAILY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void downloadSymbolsWithOutputDirectory() throws Exception {
    final List<File> actuals = CLI.execute(D, X, NYSE, AIC, O, OUTPUT_DIRECTORY.getPath());
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.DAILY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void downloadSymbolsWithFrequency() throws Exception {
    final List<File> actuals = CLI.execute(D, X, NYSE, AIC, F, Frequencies.MONTHLY.toString());
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.MONTHLY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void downloadSymbolsWithStartDate() throws Exception {
    final Calendar start = Calendar.getInstance();
    final String endDate = DATE_FORMAT.format(start.getTime());
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());

    final List<File> actuals = CLI.execute(D, X, NYSE, AIC, S, startDate);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + startDate + DASH + endDate + UNDERSCORE + Frequencies.DAILY.frequency() + CSV,
                 actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void cannotDownloadSymbolsWithEndDateOnly() throws Exception {
    final Calendar start = Calendar.getInstance();

    final List<File> actuals = CLI.execute(D, X, NYSE, AIC, E, DATE_FORMAT.format(start.getTime()));

    assertTrue(actuals.isEmpty());
  }

  @Test
  public void downloadSymbolsWithStartAndEndDates() throws Exception {
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(D,
                                           X,
                                           NYSE,
                                           AIC,
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + startDate + DASH + endDate + UNDERSCORE + Frequencies.DAILY.frequency() + CSV,
                 actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void downloadSymbolsWithOutputDirectoryStartAndEndDates() throws Exception {
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(D,
                                           X,
                                           NYSE,
                                           AIC,
                                           O,
                                           OUTPUT_DIRECTORY.getPath(),
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + startDate + DASH + endDate + UNDERSCORE + Frequencies.DAILY.frequency() + CSV,
                 actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void downloadIgnoresSpacesAndEmptySymbols() throws Exception {
    final List<File> actuals = CLI.execute(D, X, NYSE, EMPTY, SPACE, AIC);
    final File actual = actuals.get(0);

    assertEquals(AIC + UNDERSCORE + Frequencies.DAILY.frequency() + CSV, actual.getName());
    assertEquals(1, actuals.size());
    assertTrue(actual.delete());
  }

  @Test
  public void downloadSymbolsFile() throws Exception {
    final List<File> actuals = CLI.execute(D, I, SYMBOLS_FILE.getPath());
    checkDestinations(actuals, null, null, Frequencies.DAILY);
  }

  @Test
  public void downloadSymbolsFileWithOutputDirectory() throws Exception {
    final List<File> actuals = CLI.execute(D,
                                           I,
                                           SYMBOLS_FILE.getPath(),
                                           O,
                                           OUTPUT_DIRECTORY.getPath());
    checkDestinations(actuals, null, null, Frequencies.DAILY);
  }

  @Test
  public void cannotDownloadSymbolsFileWithEndDateOnly() throws Exception {
    final Calendar start = Calendar.getInstance();

    final List<File> actuals = CLI.execute(D,
                                           I,
                                           SYMBOLS_FILE.getPath(),
                                           E,
                                           DATE_FORMAT.format(start.getTime()));

    assertTrue(actuals.isEmpty());
  }

  @Test
  public void downloadSymbolsFileWithStartAndEndDates() throws Exception {
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(D,
                                           I,
                                           SYMBOLS_FILE.getPath(),
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    checkDestinations(actuals, startDate, endDate, Frequencies.DAILY);
  }

  @Test
  public void downloadSymbolsFileWithOutputDirectoryStartAndEndDates() throws Exception {
    final Calendar start = Calendar.getInstance();
    final Calendar end = (Calendar) start.clone();
    start.add(Calendar.DATE, -5);
    final String startDate = DATE_FORMAT.format(start.getTime());
    final String endDate = DATE_FORMAT.format(end.getTime());

    final List<File> actuals = CLI.execute(D,
                                           I,
                                           SYMBOLS_FILE.getPath(),
                                           O,
                                           OUTPUT_DIRECTORY.getPath(),
                                           S,
                                           startDate,
                                           E,
                                           endDate);
    checkDestinations(actuals, startDate, endDate, Frequencies.DAILY);
  }

  @Test
  public void downloadSymbolsFileIgnoresExplicitExchange() throws Exception {
    final List<File> actuals = CLI.execute(D, I, SYMBOLS_FILE.getPath(), X, HKSE);
    checkDestinations(actuals, null, null, Frequencies.DAILY);
  }

}
