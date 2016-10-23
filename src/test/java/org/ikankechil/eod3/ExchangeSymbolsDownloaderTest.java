/**
 * ExchangeSymbolsDownloaderTest.java  v0.12  7 April 2015 3:51:55 PM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ikankechil.eod3.ExchangeSymbolsDownloader.SymbolsTaskHelper;
import org.ikankechil.eod3.ExchangeSymbolsDownloader.SymbolsTransform;
import org.ikankechil.eod3.sources.Exchanges;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>ExchangeSymbolsDownloader</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.12
 */
public class ExchangeSymbolsDownloaderTest {

  private static final File                      DIRECTORY             = new File(".//./src/test/resources/" + ExchangeSymbolsDownloaderTest.class.getSimpleName());
  private static final File                      SYMBOLS_FILE          = new File(DIRECTORY, "Symbols.csv");
  private static final File                      FX_SYMBOLS_FILE       = new File(DIRECTORY, "Symbols-FX-ISO4217.csv");
  private static final File                      OHLCV_DIRECTORY       = new File(DIRECTORY, "YahooFinance");

  private static final ExchangeSymbolsDownloader ESD                   = new ExchangeSymbolsDownloader(SYMBOLS_FILE, true);
  private static final SymbolsTaskHelper         SYMBOLS_TASK_HELPER   = ESD.new SymbolsTaskHelper();

  private static final Map<String, Set<String>>  MARKETS               = new LinkedHashMap<>();
  private static final Exchanges[]               UNSUPPORTED_EXCHANGES = { LUX, ATHEX, UX, GPW, PX, BVB, LJSE, OSE, MYX, PSE, TADAWUL, QSE, ADX, DFM, MSM, ASE, BHB, EGX, NGSE, BC, BCBA, BCS, BVC, BVCA, BVL };
  private static final String[]                  EXCHANGE_URLS         = { "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=NYSE",
                                                                           "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=NASDAQ",
                                                                           "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=AMEX",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/NYSEARCA.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/TSX.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/LON.csv",
                                                                           "http://www.ise.ie/Market-Data-Announcements/Companies/Company-Codes/?list=full&type=SEDOL&exportTo=excel",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/FRA.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/EPA.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/AMS.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/EBR.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/SWX.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/BIT.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/MC.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/ELI.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/VIE.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/IST.csv",
                                                                           "http://www.netfonds.no/quotes/kurs.php?exchange=OSE&sec_types=&ticks=&table=tab&sort=alphabetic",
                                                                           "http://www.netfonds.no/quotes/kurs.php?exchange=ST&sec_types=&ticks=&table=tab&sort=alphabetic",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/HEL.csv",
                                                                           "http://www.netfonds.no/quotes/kurs.php?exchange=CPH&sec_types=&ticks=&table=tab&sort=alphabetic",
                                                                           "http://www.netfonds.no/quotes/kurs.php?exchange=ICEX&sec_types=&ticks=&table=tab&sort=alphabetic",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/MCX.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/RSE.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/TAL.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/VSE.csv",
                                                                           "http://bse.hu/topmenu/trading_data/cash_market/equities",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/SI.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/HK.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/SS.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/SZ.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/TYO.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/BO.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/NS.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/KRX.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/TPE.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/JK.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/BKK.csv",
                                                                           "http://www.asx.com.au/asx/research/ASXListedCompanies.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/NZE.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/TLV.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/JSE.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/BVMF.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/MX.csv",
                                                                           "http://www.currency-iso.org/dam/downloads/lists/list_one.xml"
                                                                         };

  private static final String                    EMPTY                 = "";
  private static final String                    SPACE                 = " ";
  private static final char                      COMMA                 = ',';
  private static final String                    COMMA_STR             = ",";
  private static final Pattern                   PUNCTUATION           = Pattern.compile("\\p{Punct}");

  @Rule
  public ExpectedException                       thrown                = ExpectedException.none();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    if (SYMBOLS_FILE.exists()) {
      SYMBOLS_FILE.setReadable(true);
      SYMBOLS_FILE.setWritable(true);
    }

    MARKETS.put(NYSE.toString(),
                new TreeSet<>(Arrays.asList("123",
                                            "A", "AA", "AAC", "AB", "ABB",
                                            "B", "BAC", "BAK", "BG", "BH",
                                            "C",
                                            "DD",
                                            "M",
                                            "W",
                                            "ZF", "ZTR", "ZX")));
    MARKETS.put(NASDAQ.toString(),
                new TreeSet<>(Arrays.asList("CSCO", "INTC")));


  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    ESD.stop();
  }

  @Before
  public void setUp() throws Exception {
    if (!SYMBOLS_FILE.canWrite()) {
      SYMBOLS_FILE.setWritable(true);
    }
  }

  @Test
  public void cannotInstantiateWithNullFile() {
    thrown.expect(NullPointerException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(null);
  }

  @Test
  public void cannotInstantiateWithNullFile2() {
    thrown.expect(NullPointerException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(null, true);
  }

  @Test
  public void cannotInstantiateWithEmptyFile() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(new File(EMPTY));
  }

  @Test
  public void cannotInstantiateWithWhitespaceFile() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(new File(SPACE));
  }

  @Test
  public void cannotInstantiateWithUnwriteableFile() {
    final File unwriteable = SYMBOLS_FILE;
    unwriteable.setWritable(false);

    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(unwriteable);
  }

  @Test
  public void cannotInstantiateWithDirectory() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(DIRECTORY);
  }

  @Test
  public void cannotDownloadNullExchange() throws Exception {
    thrown.expect(NullPointerException.class);
    final Exchanges[] exchanges = null;
    ESD.download(exchanges);
  }

  @Test
  public void cannotDownloadNullExchange2() throws Exception {
    thrown.expect(NullPointerException.class);
    final String[] exchanges = null;
    ESD.download(exchanges);
  }

  @Test
  public void cannotDownloadEmptyExchange() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.download(new Exchanges[] { /* empty */ });
  }

  @Test
  public void cannotDownloadInvalidExchange() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.download(EMPTY);
  }

  @Test
  public void cannotDownloadUnsupportedExchanges() throws Exception {
    final Map<String, Set<String>> unsupportedExchanges = ESD.download(UNSUPPORTED_EXCHANGES);
    assertTrue("Potentially supported exchanges: " + unsupportedExchanges.keySet(), unsupportedExchanges.isEmpty());
  }

  @Test
  public void cannotExtractZerothColumn() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final SymbolsTransform transform = new SymbolsTransform(COMMA, 0);
  }

  @Test
  public void cannotExtractNegativeColumn() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final SymbolsTransform transform = new SymbolsTransform(COMMA, -1);
  }

  @Test
  public void exchangeURLs() throws Exception {
    final List<URL> actuals = new ArrayList<>(ExchangeSymbolsDownloader.urls().values());
    for (int i = 0; i < EXCHANGE_URLS.length; ++i) {
      // convert URLs to Strings for comparison
      assertEquals("Index: " + i, EXCHANGE_URLS[i], actuals.get(i).toString());
    }
    assertEquals(EXCHANGE_URLS.length, actuals.size());
  }

  @Test
  public void unsupportedExchanges() throws Exception {
    // start with all exchanges
    final Set<Exchanges> residualExchanges = EnumSet.allOf(Exchanges.class);
    // remove supported exchanges
    residualExchanges.removeAll(ExchangeSymbolsDownloader.urls().keySet());
    final Object[] unsupportedExchanges = residualExchanges.toArray();
    // remove unsupported exchanges
    residualExchanges.removeAll(Arrays.asList(UNSUPPORTED_EXCHANGES));

    assertTrue("Unsupported exchanges unaccounted for: " + residualExchanges, residualExchanges.isEmpty());
    assertArrayEquals(UNSUPPORTED_EXCHANGES, unsupportedExchanges);
  }

  @Test
  public void downloadExchanges() throws Exception {
    final Map<String, Set<String>> expecteds = ESD.download(SGX, NYSE);
    final String sgx = SGX.toString();
    final String nyse = NYSE.toString();
    final Map<String, Set<String>> actuals = ESD.download(sgx, nyse);

    assertTrue(actuals.containsKey(sgx));
    assertTrue(actuals.containsKey(nyse));
    assertEquals(expecteds, actuals);
  }

  @Test
  public void downloadFX() throws Exception {
    final Set<String> actuals = ESD.download(new Exchanges[] { FX }).get(FX.toString());

    // remove currency pairs one row at a time
    for (final String line : Files.readAllLines(FX_SYMBOLS_FILE.toPath())) {
      final String[] expecteds = line.split(COMMA_STR);
      for (int i = 1; i < expecteds.length; ++i) {
        final String expected = expecteds[i];
        assertTrue("Expected currency pair: " + expected, actuals.remove(expected));
      }
    }

    // all currency pairs would have been removed
    assertTrue(actuals.size() + " unexpected currency pairs: " + actuals, actuals.isEmpty());
  }

  @Test
  public void downloadASX() throws Exception {
    download(ASX.toString());
  }

  @Test
  public void downloadISE() throws Exception {
    download(ISE.toString());
  }

  @Test
  public void downloadBET() throws Exception {
    download(BET.toString());
  }

  private static final void download(final String exchange) throws IOException, InterruptedException {
    final Set<String> actuals = ESD.download(new String[] { exchange }).get(exchange);

    assertFalse(actuals.isEmpty());
    assertFalse(actuals.contains(EMPTY));
    assertFalse(actuals.contains(SPACE));
  }

  @Test
  public void downloadRFC2396NonCompliantSymbols() throws Exception {
    // allows non-compliant symbols by default
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(SYMBOLS_FILE);
    final Map<String, Set<String>> actuals = esd.download(new Exchanges[] { KFB });

    for (final Entry<String, Set<String>> market : actuals.entrySet()) {
      final Set<String> symbols = market.getValue();
      int nonCompliantSymbolCount = 0;
      for (final String symbol : symbols) {
        final Matcher matcher = PUNCTUATION.matcher(symbol);
        if (matcher.find()) {
          ++nonCompliantSymbolCount;
        }
      }

      assertTrue(market.getKey(), nonCompliantSymbolCount > 0);
    }
  }

  @Test
  public void cannotCollateNullDirectory() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.collate(null);
  }

  @Test
  public void cannotCollateNullDirectory2() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.collate(null, NYSE);
  }

  @Test
  public void cannotCollateFile() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.collate(SYMBOLS_FILE);
  }

  @Test
  public void cannotCollateFile2() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.collate(SYMBOLS_FILE, NYSE);
  }

  @Test
  public void cannotCollateNullExchange() throws Exception {
    thrown.expect(NullPointerException.class);
    final Exchanges[] exchanges = null;
    ESD.collate(OHLCV_DIRECTORY, exchanges);
  }

  @Test
  public void cannotCollateNullExchange2() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.collate(OHLCV_DIRECTORY, null, null);
  }

  @Test
  public void cannotCollateNonexistentExchanges() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY, FX);
    assertTrue(actuals.toString(), actuals.isEmpty());
  }

  @Test
  public void cannotCollateEmptyExchanges() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY, SGX);
    assertTrue(actuals.toString(), actuals.isEmpty());
  }

  @Test
  public void collateSymbols() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY, NYSE, NASDAQ);

    assertEquals(MARKETS, actuals);
  }

  @Test
  public void collateSymbolsFromAllExchanges() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY);

    assertEquals(MARKETS, actuals);
  }

  @Test
  public void handleExecutionFailure() throws Exception {
    assertEquals(Collections.emptyList(),
                 SYMBOLS_TASK_HELPER.handleExecutionFailure(new ExecutionException(null),
                                                            AMEX));
  }

  @Test
  public void handleTaskCancellation() throws Exception {
    assertEquals(Collections.emptyList(),
                 SYMBOLS_TASK_HELPER.handleTaskCancellation(new CancellationException(),
                                                            AMEX));
  }

  @Test
  public void handleTimeout() throws Exception {
    assertEquals(Collections.emptyList(),
                 SYMBOLS_TASK_HELPER.handleTimeout(new TimeoutException(),
                                                   AMEX));
  }

}
