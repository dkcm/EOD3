/**
 * ExchangeSymbolsDownloader.java v0.12 28 January 2015 12:27:30 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static org.ikankechil.eod3.ExchangeSymbolsDownloader.SymbolsSource.*;
import static org.ikankechil.eod3.FilenameConvention.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.ikankechil.eod3.io.SymbolsWriter;
import org.ikankechil.eod3.sources.Exchanges;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.ikankechil.synchronous.TaskExecutor;
import org.ikankechil.synchronous.TaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Downloads exchange-listed symbols.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.12
 */
public class ExchangeSymbolsDownloader {

  private final TextReader                           reader;
  private final SymbolsWriter                        writer;
  private final File                                 destination;

  private final boolean                              isRFC2396Compliant;
  private final TaskExecutor                         executor;
  private final SymbolsTaskHelper                    symbolsTaskHelper;
  private final FilenameFilter                       ohlcvFilenameFilter;

  // Exchange-related constants
  private static final String                        SI            = "SI";
  private static final String                        HK            = "HK";
  private static final String                        SS            = "SS";
  private static final String                        SZ            = "SZ";
  private static final String                        TYO           = "TYO";
  private static final String                        BO            = "BO";
  private static final String                        NS            = "NS";
  private static final String                        TPE           = "TPE";
  private static final String                        JK            = "JK";
  private static final String                        BKK           = "BKK";
  private static final String                        LON           = "LON";
  private static final String                        FRA           = "FRA";
  private static final String                        EPA           = "EPA";
  private static final String                        BIT           = "BIT";
  private static final String                        MC            = "MC";
  private static final String                        VIE           = "VIE";
  private static final String                        IST           = "IST";
  private static final String                        OSE           = "OSE";
//  private static final String                        STO           = "STO";
  private static final String                        ST            = "ST";
  private static final String                        CPH           = "CPH";
  private static final String                        MCX           = "MCX";
  private static final String                        NZE           = "NZE";
  private static final String                        TLV           = "TLV";
  private static final String                        BVMF          = "BVMF";
  private static final String                        MX            = "MX";

  // Constants
  private static final int                           ZERO          = 0;
  private static final char                          COMMA         = ',';
  private static final char                          TAB           = '\t';
  private static final String                        EMPTY         = "";

  // collate already-merged files only and not update files
  private static final Pattern                       OHLCV_FILE    = Pattern.compile(FILENAME_REGEX);
  private static final Pattern                       PUNCTUATION   = Pattern.compile("\\p{Punct}"); // !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~

  private static final String                        SYMBOL_SOURCE = "Symbol source for {}: {}";

  private static final Map<Exchanges, SymbolsSource> SOURCES       = new EnumMap<>(Exchanges.class);

  static final Logger                                logger        = LoggerFactory.getLogger(ExchangeSymbolsDownloader.class);

  static { // initialise source URLs
    try {
      // supported markets
      // e.g. http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download
      //      https://www.quandl.com/api/v2/datasets.csv?query=*&source_code=CURRFX&per_page=300&page=1
      //      https://www.quandl.com/api/v3/databases/LSE/codes
      //      https://www.quandl.com/api/v3/databases/TSE/codes
      //      http://data.okfn.org/data/core/nasdaq-listings/r/nasdaq-listed-symbols.csv
      //      ftp://ftp.nasdaqtrader.com/symboldirectory/nasdaqlisted.txt
      //      http://www.nasdaqomx.com/transactions/markets/nordic/membership/membership-lists
      //      https://en.wikipedia.org/wiki/List_of_companies_listed_on_the_Oslo_Stock_Exchange
      //      http://query.sse.com.cn/commonQuery.do?jsonCallBack=jsonpCallback41235&isPagination=true&sqlId=COMMON_SSE_LISTEDCOMPANIES_COMPANYLIST_EN_L&pageHelp.pageSize=15&pageHelp.pageNo=6&pageHelp.beginPage=6&pageHelp.endPage=10&_=1452391535253
      //      http://www.netfonds.no/quotes/kurs.php?exchange=BTSE&sec_types=&ticks=&table=tab&sort=alphabetic
      //      https://www.nzx.com/markets/NZSX/securities
      //      https://www.euronext.com/sites/www.euronext.com/files/ftp/smartpoolsecuritiesumtfcsv.csv

      final TextTransformer commaAtFirstColumn = new TextTransformer(new SymbolsTransform(COMMA, 1));

      // USA exchanges
      final Exchanges[] usaExchanges = { NYSE, NASDAQ, AMEX };
      for (final Exchanges usaExchange : usaExchanges) {
        SOURCES.put(usaExchange, new SymbolsSource(1, NASDAQ_BASE + usaExchange, commaAtFirstColumn));
      }

      // other exchanges around the world
      SOURCES.put(ASX, new SymbolsSource(3, ASX_BASE, new TextTransformer(new SymbolsTransform(COMMA, 2))));

      // exchanges sourced from Google
      final Map<Exchanges, String> googles = new EnumMap<>(Exchanges.class);
      googles.put(NYSEARCA, NYSEARCA.toString());
      googles.put(LSE, LON);
      googles.put(FWB, FRA);
      googles.put(PAR, EPA);
      googles.put(AMS, AMS.toString());
      googles.put(SWX, SWX.toString());
      googles.put(MIB, BIT);
      googles.put(WB, VIE);
      googles.put(BIST, IST);
      googles.put(MOEX, MCX);
      googles.put(TSE, TYO);
      googles.put(KRX, KRX.toString());
      googles.put(TWSE, TPE);
      googles.put(SET, BKK);
      googles.put(NZX, NZE);
      googles.put(TASE, TLV);
      googles.put(BOVESPA, BVMF);
//      googles.put(SB, STO); // alternative
      for (final Entry<Exchanges, String> google : googles.entrySet()) {
        SOURCES.put(google.getKey(),
                    new SymbolsSource(1,
                                      String.format(QUANDL_GOOGLE_BASE, google.getValue()),
                                      commaAtFirstColumn));
      }

      // exchanges sourced from Yahoo
      final Map<Exchanges, String> yahoos = new EnumMap<>(Exchanges.class);
      yahoos.put(BM, MC);
      yahoos.put(SGX, SI);
      yahoos.put(HKSE, HK);
      yahoos.put(SSE, SS);
      yahoos.put(SZSE, SZ);
      yahoos.put(BSE, BO);
      yahoos.put(NSE, NS);
      yahoos.put(IDX, JK);
      yahoos.put(TSX, TSX.toString());
      yahoos.put(BMV, MX);
//      yahoos.put(LSE, L); // alternative
      for (final Entry<Exchanges, String> yahoo : yahoos.entrySet()) {
        SOURCES.put(yahoo.getKey(),
                    new SymbolsSource(1,
                                      String.format(QUANDL_YAHOO_BASE, yahoo.getValue()),
                                      commaAtFirstColumn));
      }

      // Nordic exchanges
      final TextTransformer tabAtSecondColumn = new TextTransformer(new SymbolsTransform(TAB, 2));
      final Map<Exchanges, String> nordics = new EnumMap<>(Exchanges.class);
      nordics.put(OSLO, OSE);
      nordics.put(SB, ST);
      nordics.put(KFB, CPH);
      nordics.put(ICEX, ICEX.toString());
      for (final Entry<Exchanges, String> nordic : nordics.entrySet()) {
        SOURCES.put(nordic.getKey(),
                    new SymbolsSource(1,
                                      String.format(NETFONDS_BASE, nordic.getValue()),
                                      tabAtSecondColumn));
      }

      // FX sourced from ISO
      SOURCES.put(FX,
                  new SymbolsSource(0,
                                    ISO4217_BASE,
                                    new CurrencyTextTransformer(new CurrencyTextTransform())));
    }
    catch (final MalformedURLException murlE) {
      logger.error("Bad URL", murlE);
    }

    for (final Entry<Exchanges, SymbolsSource> exchange : SOURCES.entrySet()) {
      logger.debug(SYMBOL_SOURCE,
                   exchange.getKey(),
                   exchange.getValue().url);
    }

    final List<Exchanges> unsupportedExchanges = new ArrayList<>(Arrays.asList(Exchanges.values()));
    unsupportedExchanges.removeAll(SOURCES.keySet());
    logger.info("Unsupported exchanges: {}", unsupportedExchanges);
  }

  public ExchangeSymbolsDownloader(final File destination) {
    // allows non-compliant symbols by default
    this(destination, false);
  }

  public ExchangeSymbolsDownloader(final File destination, final boolean isRFC2396Compliant) {
    if (destination.isDirectory()) {
      throw new IllegalArgumentException("Destination is a directory: " + destination);
    }
    else if (destination.exists() && !destination.canWrite()) {
      throw new IllegalArgumentException("Cannot write to destination: " + destination);
    }
    else if (destination.getName().trim().equals(EMPTY)) {
      throw new IllegalArgumentException("Empty destination filename");
    }
    this.destination = destination;
    reader = new TextReader();
    writer = new SymbolsWriter();

    this.isRFC2396Compliant = isRFC2396Compliant;
    executor = new TaskExecutor(Executors.newCachedThreadPool());
    symbolsTaskHelper = new SymbolsTaskHelper();
    ohlcvFilenameFilter = new PatternFilenameFilter(OHLCV_FILE);
  }

  /**
   * Collate all symbols in the given directory.
   *
   * @param inputParentDirectory
   * @return
   * @throws IOException
   */
  public Map<String, Set<String>> collate(final File inputParentDirectory) throws IOException {
    return collate(inputParentDirectory, Exchanges.values());
  }

  /**
   * Collate all symbols from the specified exchanges in the given directory.
   * Each exchange is represented by a sub-directory.
   *
   * @param inputParentDirectory
   * @param exchanges the <code>Exchanges</code> of interest
   * @return
   * @throws IOException
   */
  public Map<String, Set<String>> collate(final File inputParentDirectory, final Exchanges... exchanges)
      throws IOException {
    if (!inputParentDirectory.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + inputParentDirectory);
    }

    logger.info("Collating symbols in {} for exchanges {}", inputParentDirectory, exchanges);

    final Map<String, Set<String>> markets = new LinkedHashMap<>(exchanges.length);

    // collate symbols
    int symbolCount = ZERO;
    for (final Exchanges exchange : exchanges) {
      final File directory = new File(inputParentDirectory, exchange.toString());
      // filter files
      final String[] ohlcvs = directory.list(ohlcvFilenameFilter);

      if (ohlcvs == null) {
        logger.debug("Non-existent exchange: {}", exchange);
      }
      else if (ohlcvs.length == 0) {
        logger.info("Empty exchange: {}", exchange);
      }
      else {
        final Set<String> symbols = new TreeSet<>();
        for (final String ohlcv : ohlcvs) {
          symbols.add(FilenameConvention.getSymbolFrom(ohlcv));
        }

        markets.put(exchange.toString(), symbols);
        logger.info("Symbols added from exchange {}: {}", exchange, symbols.size());
        symbolCount += symbols.size();
      }
    }

    // write to file
    writer.write(markets, destination);
    logger.info("Collated {} symbols in: {}", symbolCount, inputParentDirectory);
    logger.info("Symbols written to file: {}", destination);

    return markets;
  }

  private static final class PatternFilenameFilter implements FilenameFilter {

    private final Pattern pattern;

    public PatternFilenameFilter(final Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public boolean accept(final File dir, final String name) {
      return pattern.matcher(name).matches();
    }

  }

  /**
   * Download all symbols for the specified exchanges and write them to file.
   *
   * @param exchanges
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public Map<String, Set<String>> download(final String... exchanges)
      throws IOException, InterruptedException {
    // convert Strings to Exchanges
    final List<Exchanges> xchgs = new ArrayList<>(exchanges.length);
    for (final String exchange : exchanges) {
      final Exchanges xchg = Exchanges.toExchange(exchange);
      if (xchg != null) {
        xchgs.add(xchg);
      }
    }
    return download(xchgs.toArray(new Exchanges[xchgs.size()]));
  }

  /**
   * Download all symbols for the specified exchanges and write them to file.
   *
   * @param exchanges the <code>Exchanges</code> of interest
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public Map<String, Set<String>> download(final Exchanges... exchanges)
      throws IOException, InterruptedException {
    logger.info("Downloading symbols for exchange(s): {}", (Object) exchanges);

    // read from URLs
    final Map<Exchanges, List<String>> lines = read(exchanges);

    // transform into Map<Exchange, Set<Symbol>>
    final Map<String, Set<String>> markets = transform(lines);

    // filter if required
    if (isRFC2396Compliant) {
      logger.info("RFC2396 compliance required, filtering symbols");
      filter(markets);
    }
    else {
      logger.info("RFC2396 compliance not required, symbols not filtered");
    }

    // write to file
    writer.write(markets, destination);
    logger.info("Symbols written to file: {}", destination);

    return markets;
  }

  private final Map<Exchanges, List<String>> read(final Exchanges... exchanges)
      throws InterruptedException {
    final Map<Exchanges, List<String>> lines =
        executor.executeAll(Arrays.asList(exchanges), symbolsTaskHelper);

    // remove exchanges with no symbols
    // iterator used to prevent throwing ConcurrentModificationException
    final Iterator<Entry<Exchanges, List<String>>> iterator = lines.entrySet().iterator();
    while (iterator.hasNext()) {
      final Entry<Exchanges, List<String>> exchange = iterator.next();
      final List<String> strings = exchange.getValue();
      if (strings == null || strings.isEmpty()) {
        iterator.remove();
        logger.warn("No symbols downloaded for: {}", exchange);
      }
    }

    return lines;
  }

  class SymbolsTaskHelper implements TaskHelper<Exchanges, List<String>> {

    @Override
    public Callable<List<String>> newTask(final Exchanges exchange) {
      return new Callable<List<String>>() {
        @Override
        public List<String> call() throws Exception {
          return download(exchange);
        }
      };
    }

    @Override
    public List<String> handleExecutionFailure(final ExecutionException eE, final Exchanges operand) {
      return Collections.emptyList();
    }

    @Override
    public List<String> handleTaskCancellation(final CancellationException cE, final Exchanges operand) {
      return Collections.emptyList();
    }

    @Override
    public List<String> handleTimeout(final TimeoutException tE, final Exchanges operand) {
      return Collections.emptyList();
    }

  }

  final List<String> download(final Exchanges exchange) throws IOException {
    List<String> lines = null;
    final SymbolsSource source = SOURCES.get(exchange);
    if (source != null) {
      logger.info("Downloading symbols for: {}", exchange);
      lines = reader.read(source.url);
      while (lines.remove(EMPTY)) { /* remove all empty lines */ }
      // skip rows
      lines = lines.subList(source.skippedRows, lines.size());
      logger.info("Symbols downloaded for {}: {}",
                  exchange,
                  lines.size());
    }
    else {
      logger.warn("Symbol source unavailable for: {}", exchange);
    }
    return lines;
  }

  private static final Map<String, Set<String>> transform(final Map<Exchanges, List<String>> lines) {
    int total = ZERO;
    int extracted = ZERO;
    final Map<String, Set<String>> markets = new LinkedHashMap<>(lines.size());
    for (final Exchanges exchange : lines.keySet()) {
      final TextTransformer transformer = SOURCES.get(exchange).transformer;
      final List<String> symbols = transformer.transform(lines.get(exchange));
      final Set<String> uniqueSymbols = new TreeSet<>(symbols);
      markets.put(exchange.toString(), uniqueSymbols);

      logger.info("Duplicate symbols removed for {}: {}",
                  exchange,
                  symbols.size() - uniqueSymbols.size());
      total += symbols.size();
      extracted += uniqueSymbols.size();
    }
    logger.info("Symbols extracted: {} (Duplicates removed: {})",
                extracted,
                total - extracted);
    return markets;
  }

  private static final void filter(final Map<String, Set<String>> markets) {
    int total = ZERO;
    for (final Entry<String, Set<String>> market : markets.entrySet()) {
      final List<String> removedSymbols = new ArrayList<>();
      final Set<String> symbols = market.getValue();

      final Iterator<String> iterator = symbols.iterator();
      while (iterator.hasNext()) {
        final String symbol = iterator.next();
        // find and remove symbols with punctuation marks
        if (PUNCTUATION.matcher(symbol).find()) {
          iterator.remove();
          logger.trace("Symbol removed: {}", symbol);
          removedSymbols.add(symbol);
        }
      }

      final String exchange = market.getKey();
      logger.info("Symbols removed for {}: {}", exchange, removedSymbols.size());
      logger.debug("Symbols removed for {}: {}", exchange, removedSymbols);
      logger.info("Symbols remaining for {}: {}", exchange, symbols.size());
      total += symbols.size();
    }
    logger.info("Symbols: {}", total);
  }

  public static final Map<Exchanges, URL> urls() {
    final Map<Exchanges, URL> urls = new EnumMap<>(Exchanges.class);
    for (final Entry<Exchanges, SymbolsSource> exchange : SOURCES.entrySet()) {
      urls.put(exchange.getKey(), exchange.getValue().url);
    }
    return urls;
  }

  public void stop() throws InterruptedException {
    executor.stop();
    logger.info("Shutdown requested");
  }

  static class SymbolsSource {

    final int                   skippedRows;
    final URL                   url;
    final TextTransformer       transformer;

    // base URLs
    private static final String NASDAQ_BASE        = "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=";
    private static final String ASX_BASE           = "http://www.asx.com.au/asx/research/ASXListedCompanies.csv";
    private static final String QUANDL_BASE        = "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/";
    private static final String QUANDL_YAHOO_BASE  = QUANDL_BASE + "Yahoo/%s.csv";
    private static final String QUANDL_GOOGLE_BASE = QUANDL_BASE + "Google/%s.csv";
    private static final String NETFONDS_BASE      = "http://www.netfonds.no/quotes/kurs.php?exchange=%s&sec_types=&ticks=&table=tab&sort=alphabetic";
//    private static final String TWSE_BASE          = "http://isin.twse.com.tw/isin/e_C_public.jsp?strMode=2";
    private static final String ISO4217_BASE       = "http://www.currency-iso.org/dam/downloads/lists/list_one.xml";

    /**
     * @param skippedRows number of rows to skip
     * @param url symbol source <code>URL</code>
     * @param transformer
     * @throws MalformedURLException
     */
    SymbolsSource(final int skippedRows,
                  final String url,
                  final TextTransformer transformer)
        throws MalformedURLException {
      this.skippedRows = skippedRows;
      this.url = new URL(url);
      this.transformer = transformer;
    }

  }

  static class SymbolsTransform implements TextTransform {

    private final char        separator;
    private final int         targetColumn;

    private static final char QUOTE = '"';

    private static final int  FIRST = 1;

    /**
     * @param separator column separator
     * @param targetColumn the column to be extracted
     */
    SymbolsTransform(final char separator, final int targetColumn) {
      if (targetColumn < FIRST) {
        throw new IllegalArgumentException("Illegal target column: " + targetColumn);
      }
      this.separator = separator;
      this.targetColumn = targetColumn;
    }

    @Override
    public String transform(final String line) {
      String symbol = EMPTY;
      for (int column = ZERO, from = ZERO; column < targetColumn; ++column) {
        final boolean quote = line.charAt(from) == QUOTE;
        final int to = quote ?
                       findNth(QUOTE, line, FIRST, ++from) :
                       findNth(separator, line, FIRST, from);
        symbol = line.substring(from, to);
        from = to + (quote ? 2 : 1); // move past separator
      }
      return symbol.trim();
    }

  }

  static class CurrencyTextTransform implements TextTransform {

    private static final char MORE_THAN = '>';
    private static final char LESS_THAN = '<';

    @Override
    public String transform(final String line) {
      final int start = line.indexOf(MORE_THAN) + 1;
      return line.substring(start, line.indexOf(LESS_THAN, start));
    }

  }

  static class CurrencyTextTransformer extends TextTransformer {

    private final TextTransform transform;

    private static final String CCY = "<Ccy>";

    public CurrencyTextTransformer(final TextTransform transform) {
      super(transform);
      this.transform = transform;
    }

    @Override
    public List<String> transform(final List<String> lines) {
      // extract currencies
      final Set<String> currencies = new TreeSet<>();
      for (final String line : lines) {
        if (line.contains(CCY)) {
          currencies.add(transform.transform(line));
        }
      }

      // form currency pairs: base + quote
      lines.clear();
      for (final String base : currencies) {
        for (final String quote : currencies) {
          if (base != quote) {
            lines.add(base + quote);
          }
        }
      }

      logger.info("{} currencies -> {} currency pairs", currencies.size(), lines.size());
      return lines;
    }

  }

}
