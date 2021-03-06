/**
 * ExchangeSymbolsDownloader.java  v0.19  28 January 2015 12:27:30 am
 *
 * Copyright © 2015-2018 Daniel Kuan.  All rights reserved.
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
import java.util.ListIterator;
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

import org.ikankechil.eod3.io.SymbolsReader;
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
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.19
 */
public class ExchangeSymbolsDownloader {

  private final TextReader                           reader;
  private final SymbolsReader                        symbolsReader;
  private final SymbolsWriter                        symbolsWriter;
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
  private static final String                        NYSEARCA      = "NYSEARCA";
  private static final String                        LON           = "LON";
  private static final String                        FRA           = "FRA";
  private static final String                        EPA           = "EPA";
  private static final String                        AMS           = "AMS";
  private static final String                        EBR           = "EBR";
  private static final String                        BIT           = "BIT";
  private static final String                        MC            = "MC";
  private static final String                        ELI           = "ELI";
  private static final String                        VIE           = "VIE";
  private static final String                        IST           = "IST";
  private static final String                        OSE           = "OSE";
//  private static final String                        STO           = "STO";
  private static final String                        ST            = "ST";
  private static final String                        HEL           = "HEL";
  private static final String                        CPH           = "CPH";
  private static final String                        MCX           = "MCX";
  private static final String                        NZE           = "NZE";
  private static final String                        TAL           = "TAL";
  private static final String                        TLV           = "TLV";
  private static final String                        BVMF          = "BVMF";
  private static final String                        MX            = "MX";

  // Constants
  private static final int                           ZERO          = 0;
  private static final char                          COMMA         = ',';
  private static final char                          QUOTE         = '"';
  private static final char                          UNDERSCORE    = '_';
  private static final char                          COLON         = ':';
  private static final char                          SEMI_COLON    = ';';
  private static final char                          TAB           = '\t';
  private static final char                          LESS_THAN     = '<';
  private static final char                          SPACE         = ' ';
  private static final String                        EMPTY         = "";

  // collate already-merged files only and not update files
  private static final Pattern                       OHLCV_FILE    = Pattern.compile(FILENAME_REGEX);
  private static final Pattern                       PUNCTUATION   = Pattern.compile("\\p{Punct}"); // !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~

  private static final String                        SYMBOL_SOURCE = "Symbol source for {}: {}";

  private static final Map<Exchanges, SymbolsSource> SOURCES       = new EnumMap<>(Exchanges.class);

  private static final Logger                        logger        = LoggerFactory.getLogger(ExchangeSymbolsDownloader.class);

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
      //      http://www.bursamalaysia.com/market/listed-companies/list-of-companies/main-market
      //      http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty_pf.htm
      //      http://www.tase.co.il/_layouts/Tase/ManagementPages/Export.aspx?sn=none&GridId=106&CurGuid={3C7B8E3F-64E3-4A38-9027-6ED04A1F6EE6}&ExportType=3
      //      https://www.gulfbase.com/company-list-saudi-stock-exchange-1 (and other Middle-Eastern markets)
      //      http://www.asmainfo.com/Kuwait/en/list/CompanyList.aspx

      final TextTransformer commaAtFirstColumn = new TextTransformer(new SymbolsTransform(COMMA, 1));

      // USA exchanges
      final Exchanges[] usaExchanges = { NYSE, NASDAQ, AMEX };
      for (final Exchanges usaExchange : usaExchanges) {
        SOURCES.put(usaExchange, new SymbolsSource(1, NASDAQ_BASE + usaExchange, commaAtFirstColumn));
      }

      // other exchanges around the world
      SOURCES.put(ASX, new SymbolsSource(3, ASX_BASE, new TextTransformer(new SymbolsTransform(COMMA, 2))));
      final XMLTagTextTransform xmlTagTextTransform = new XMLTagTextTransform();
      SOURCES.put(ISE, new SymbolsSource(ZERO, ISE_BASE, new ISETextTransformer(xmlTagTextTransform)));
      SOURCES.put(ATHEX, new SymbolsSource(ZERO, ATHEX_BASE, new BreakLongLinesTextTransformer(xmlTagTextTransform, "<td class=\"ticker-symbol\">", "</td>")));
      SOURCES.put(BET, new SymbolsSource(ZERO, BET_BASE, new FilterTextTransformer(new AsymmetricalDelimiterTextTransform(UNDERSCORE, QUOTE), "<tr id=\"P_")));
      SOURCES.put(BVB, new SymbolsSource(1, BVB_BASE, new TextTransformer(new SymbolsTransform(SEMI_COLON, 1))));
//      SOURCES.put(QSE, new SymbolsSource(ZERO, QSE_BASE, new FilterTextTransformer(xmlTagTextTransform, "/web/guest/company-profile-page?CompanyCode=")));
      SOURCES.put(NGSE, new SymbolsSource(ZERO, NGSE_BASE, new BreakLongLinesTextTransformer(new SymbolsTransform(COLON, 2), "\"SYMBOL\":\"", "\"")));
      SOURCES.put(BVC, new SymbolsSource(ZERO, BVC_BASE, new FilterTextTransformer(new SymbolsTransform(TAB, 2), "/pps/tibco/portalbvc/Home/Empresas/Emisores+BVC/Listado+de+Emisores?com.tibco.ps.pagesvc.action=portletAction&action=link&emisorId=", 1)));

      // Middle-Eastern exchanges
      final AsymmetricalDelimiterTextTransform asmaTextTransform = new AsymmetricalDelimiterTextTransform(SPACE, LESS_THAN);
      final Map<Exchanges, String> meExchanges = new EnumMap<>(Exchanges.class);
      meExchanges.put(TADAWUL, "Saudi");
      meExchanges.put(QSE, "Qatar");
      meExchanges.put(ADX, "Abudhabi");
      meExchanges.put(DFM, "Dubai");
      meExchanges.put(MSM, "Muscat");
      meExchanges.put(BHB, "Bahrain");
      for (final Entry<Exchanges, String> meExchange : meExchanges.entrySet()) {
        SOURCES.put(meExchange.getKey(),
                    new SymbolsSource(ZERO,
                                      String.format(ASMA_BASE, meExchange.getValue()),
                                      new AsmaTextTransformer(asmaTextTransform)));
      }

      // exchanges sourced from Google (via Quandl)
      final Map<Exchanges, String> googles = new EnumMap<>(Exchanges.class);
      googles.put(ARCA, NYSEARCA);
      googles.put(LSE, LON);
      googles.put(FWB, FRA);
      googles.put(PAR, EPA);
      googles.put(AEX, AMS);
      googles.put(BB, EBR);
      googles.put(SWX, SWX.toString());
      googles.put(MIB, BIT);
      googles.put(BVLP, ELI);
      googles.put(WB, VIE);
      googles.put(BIST, IST);
      googles.put(HEX, HEL);
      googles.put(MOEX, MCX);
      googles.put(RSE, RSE.toString());
      googles.put(TALSE, TAL);
      googles.put(VSE, VSE.toString());
      googles.put(TSE, TYO);
      googles.put(KRX, KRX.toString());
      googles.put(TWSE, TPE);
      googles.put(SET, BKK);
      googles.put(NZX, NZE);
      googles.put(TASE, TLV);
      googles.put(JSE, JSE.toString());
      googles.put(BOVESPA, BVMF);
//      googles.put(SB, STO); // alternative
      for (final Entry<Exchanges, String> google : googles.entrySet()) {
        SOURCES.put(google.getKey(),
                    new SymbolsSource(1,
                                      String.format(QUANDL_GOOGLE_BASE, google.getValue()),
                                      commaAtFirstColumn));
      }

      // exchanges sourced from Yahoo (via Quandl)
      final Map<Exchanges, String> yahoos = new EnumMap<>(Exchanges.class);
      yahoos.put(BM, MC);
      yahoos.put(SGX, SI);
      yahoos.put(HKEX, HK);
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
                                    new CurrencyTextTransformer(xmlTagTextTransform)));
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
    symbolsReader = new SymbolsReader();
    symbolsWriter = new SymbolsWriter();

    this.isRFC2396Compliant = isRFC2396Compliant;
    executor = new TaskExecutor(Executors.newCachedThreadPool());
    symbolsTaskHelper = new SymbolsTaskHelper();
    ohlcvFilenameFilter = new PatternFilenameFilter(OHLCV_FILE);
  }

  /**
   * Collate all symbols from OHLCV files in the given directory.
   *
   * @param inputParentDirectory
   * @return
   * @throws IOException
   */
  public Map<String, Set<String>> collate(final File inputParentDirectory) throws IOException {
    return collate(inputParentDirectory, Exchanges.values());
  }

  /**
   * Collate all symbols for the specified exchanges from OHLCV files in the
   * given directory. Each exchange is represented by a sub-directory.
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
    final int symbolCount = collateSymbols(markets, inputParentDirectory, exchanges);

    // write to file
    symbolsWriter.write(markets, destination);
    logger.info("Collated {} symbols in: {}", symbolCount, inputParentDirectory);
    logger.info("Symbols written to file: {}", destination);

    return markets;
  }

  private final int collateSymbols(final Map<String, Set<String>> markets,
                                   final File inputParentDirectory,
                                   final Exchanges... exchanges) {
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
    return symbolCount;
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
    return download(Exchanges.toExchanges(exchanges));
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
    symbolsWriter.write(markets, destination);
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

  private final List<String> download(final Exchanges exchange) throws IOException {
    List<String> lines = null;
    final SymbolsSource source = SOURCES.get(exchange);
    if (source != null) {
      logger.info("Downloading symbols for: {}", exchange);
      lines = reader.read(source.url);
      while (lines.remove(EMPTY)) { /* remove all empty lines */ }
      // skip rows
      lines = lines.subList(source.skippedRows, lines.size());
      logger.info("Rows downloaded for {}: {}",
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

  public Map<String, Set<String>> merge(final File... symbolsFiles) throws IOException {
    final Map<String, Set<String>> mergedMarkets = new LinkedHashMap<>();

    for (final File symbolsFile : symbolsFiles) {
      final Map<String, Set<String>> markets = symbolsReader.read(symbolsFile);
      for (final Entry<String, Set<String>> market : markets.entrySet()) {
        final String exchange = market.getKey();
        final Set<String> mergedMarket = mergedMarkets.get(exchange);
        if (mergedMarket != null) {
          mergedMarket.addAll(market.getValue());
        }
        else {
          mergedMarkets.put(exchange, market.getValue());
        }
      }
    }

    return mergedMarkets;
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
    private static final String NASDAQ_BASE        = "https://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=";
    private static final String ASX_BASE           = "https://www.asx.com.au/asx/research/ASXListedCompanies.csv";
    private static final String QUANDL_BASE        = "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/";
    private static final String QUANDL_YAHOO_BASE  = QUANDL_BASE + "Yahoo/%s.csv";
    private static final String QUANDL_GOOGLE_BASE = QUANDL_BASE + "Google/%s.csv";
    private static final String NETFONDS_BASE      = "http://www.netfonds.no/quotes/kurs.php?exchange=%s&sec_types=&ticks=&table=tab&sort=alphabetic";
//    private static final String TWSE_BASE          = "http://isin.twse.com.tw/isin/e_C_public.jsp?strMode=2";
    private static final String ISE_BASE           = "http://www.ise.ie/Market-Data-Announcements/Companies/Company-Codes/?list=full&type=SEDOL&exportTo=excel";
    private static final String ATHEX_BASE         = "http://www.helex.gr/web/guest/securities-market-products";
    private static final String BET_BASE           = "http://www.portfolio.hu/tozsde_arfolyamok/bet_reszveny_arfolyamok.html";
    private static final String BVB_BASE           = "http://www.bvb.ro/FinancialInstruments/Markets/SharesListForDownload.ashx?filetype=csv";
    private static final String ASMA_BASE          = "http://www.asmainfo.com/%s/en/list/CompanyList.aspx";
//    private static final String QSE_BASE           = "https://www.qe.com.qa/listed-securities";
    private static final String NGSE_BASE          = "http://www.nse.com.ng/rest/api/statistics/ticker?$filter=TickerType%20%20eq%20%27EQUITIES%27";
    private static final String BVC_BASE           = "http://en.bvc.com.co/pps/tibco/portalbvc";
    private static final String ISO4217_BASE       = "https://www.currency-iso.org/dam/downloads/lists/list_one.xml";

    /**
     *
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

    private static final int  FIRST = 1;

    /**
     *
     * @param separator column separator
     * @param targetColumn the column to be extracted, index starts at 1
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
        final boolean quote = (line.charAt(from) == QUOTE);
        final int to = quote ?
                       findNth(QUOTE, line, FIRST, ++from) :
                       findNth(separator, line, FIRST, from);
        symbol = (from < to) ? line.substring(from, to) : line.substring(from);
        from = to + (quote ? 2 : 1); // move past separator
      }
      return symbol.trim();
    }

  }

  private static class XMLTagTextTransform implements TextTransform {

    private static final char MORE_THAN = '>';

    @Override
    public String transform(final String line) {
      final int start = line.indexOf(MORE_THAN) + 1;
      return line.substring(start, line.indexOf(LESS_THAN, start)).trim();
    }

  }

  private static class AsymmetricalDelimiterTextTransform implements TextTransform {

    private final char startDelimiter;
    private final char endDelimiter;

    AsymmetricalDelimiterTextTransform(final char start, final char end) {
      startDelimiter = start;
      endDelimiter = end;
    }

    @Override
    public String transform(final String line) {
      final int start = line.indexOf(startDelimiter) + 1;
      return line.substring(start, line.indexOf(endDelimiter, start)).trim();
    }

  }

  private static class CurrencyTextTransformer extends TextTransformer {

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
          if (!base.equals(quote)) {
            lines.add(base + quote);
          }
        }
      }

      logger.info("{} currencies -> {} currency pairs", currencies.size(), lines.size());
      return lines;
    }

  }

  private static class ISETextTransformer extends TextTransformer {

    private final TextTransform transform;

    private static final String DATA_START_TAG = "<td class=\"equityName\">";
    private static final String DATA_END_TAG   = "</tr>";

    public ISETextTransformer(final TextTransform transform) {
      super(transform);
      this.transform = transform;
    }

    @Override
    public List<String> transform(final List<String> lines) {
      final List<String> symbols = new ArrayList<>();

      final ListIterator<String> iterator = lines.listIterator();
      while (iterator.hasNext()) {
        String line = iterator.next();
        if (line.contains(DATA_END_TAG)) {
          iterator.previous();
          line = iterator.previous(); // lookback
          if (line.contains(DATA_START_TAG)) {
            symbols.add(transform.transform(line));
          }
          iterator.next();
          iterator.next();
        }
      }

      lines.clear();
      lines.addAll(symbols);
      return lines;
    }

  }

  private static class FilterTextTransformer extends TextTransformer {

    private final TextTransform transform;
    private final String        textOfInterest;
    private final int           forwardMoves;

    public FilterTextTransformer(final TextTransform transform, final String textOfInterest) {
      this(transform, textOfInterest, ZERO);
    }

    public FilterTextTransformer(final TextTransform transform, final String textOfInterest, final int forwardMoves) {
      super(transform);

      this.transform = transform;
      this.textOfInterest = textOfInterest;
      this.forwardMoves = forwardMoves;

      // e.g.
      // <tr id="P_MTELEKOM" class="">
      // <td><div class="bg dir up"></div></td>
      // <td class="ticker">MTELEKOM</td>
      // <td class="ido">12:41</td>
      // <td class="last"><span class="up">491</span></td>
      // <td class="chg"><span class="up">2.0</span></td>
      // <td class="chgpc"><span class="up">0.4</span></td>
      // <td class="forgdb">522 811</td>
      // <td class="forg">257</td>
      // <td class="kotesdb">103</td>
      // <td class="bid">490</td>
      // <td class="bidsize">36 425</td>
      // <td class="ask">491</td>
      // <td class="asksize">38 033</td>
      // <td class="open">490</td>
      // <td class="min">489</td>
      // <td class="max">492</td>
      // <td class="close">489</td>
      // <td class="pe1">17.5</td>
      // <td class="openint">0</td>
      // <td class="kamattart"></td>
      // </tr>
    }

    @Override
    public List<String> transform(final List<String> lines) {
      final List<String> symbols = new ArrayList<>();

      if (forwardMoves > ZERO) {
        final Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
          String line = iterator.next();
          if (line.contains(textOfInterest)) {
            // move forward
            for (int i = ZERO; i < forwardMoves; ++i) {
              line = iterator.next();
            }
            symbols.add(transform.transform(line));
          }
        }
      }
      else {
        for (final String line : lines) {
          if (line.contains(textOfInterest)) {
            symbols.add(transform.transform(line));
          }
        }
      }

      lines.clear();
      lines.addAll(symbols);
      return lines;
    }

  }

  private static class BreakLongLinesTextTransformer extends TextTransformer {

    private final String dataStartTag;
    private final String dataEndTag;

    public BreakLongLinesTextTransformer(final TextTransform transform, final String dataStartTag, final String dataEndTag) {
      super(transform);

      this.dataStartTag = dataStartTag;
      this.dataEndTag = dataEndTag;

      // e.g.
      // ATHEX
      // <tr> <th class="ticker-symbol">Symbol</th> <th class="isin">ISIN</th> <th class="instrument-name">Name</th> <th class="closing-price">Price 03/09/2017</th> <th class="snapshot-url">Snapshot</th> <th class="historic-url">Historic</th> </tr> <tr> <td class="ticker-symbol">AAAK</td> <td class="isin">GRS059063008 </td> <td class="instrument-name">"WOOL INDUSTRY TRIA ALFA" S.A. (CR)</td> <td class="closing-price">2.59</td> <td class="snapshot-url"> <a class="snapshot-icon-url" href="http://www.helex.gr/web/guest/stock-snapshot/-/select-stock/136">&nbsp;</a> </td> <td class="historic-url"> <a class="historic-icon-url" href="http://www.helex.gr/web/guest/stock-historic/-/select-stock/136">&nbsp;</a> </td> </tr> <tr> <td class="ticker-symbol">AAAP</td> <td class="isin">GRS059064006 </td> <td class="instrument-name">"WOOL INDUSTRY TRIA ALFA" S.A. (PR)</td> <td class="closing-price">1.63</td> <td class="snapshot-url"> <a class="snapshot-icon-url" href="http://www.helex.gr/web/guest/stock-snapshot/-/select-stock/137">&nbsp;</a> </td> <td class="historic-url"> <a class="historic-icon-url" href="http://www.helex.gr/web/guest/stock-historic/-/select-stock/137">&nbsp;</a> </td> </tr>
      //
      // NGSE
      // [{"$id":"1","Id":167,"SYMBOL":"7UP","Value":86.000000,"PercChange":0.000000,"TickerType":"EQUITIES","SYMBOL2":"7UP "},{"$id":"2","Id":82,"SYMBOL":"ABBEYBDS","Value":1.250000,"PercChange":0.000000,"TickerType":"EQUITIES","SYMBOL2":"ABBEYBDS "},{"$id":"3","Id":128,"SYMBOL":"ABCTRANS","Value":0.500000,"PercChange":0.000000,"TickerType":"EQUITIES","SYMBOL2":"ABCTRANS "}]
    }

    @Override
    public List<String> transform(final List<String> lines) {
      final List<String> shorterLines = new ArrayList<>();

      // break long lines
      for (final String line : lines) {
        final StringBuilder longLine = new StringBuilder(line);
        int currentPosition = ZERO;
        while (currentPosition < longLine.length()) {
          final int start = longLine.indexOf(dataStartTag, currentPosition);
          final int end = longLine.indexOf(dataEndTag, start + dataStartTag.length());
          if (start < ZERO || end < ZERO) {
            break;
          }
          shorterLines.add(longLine.substring(start, currentPosition = end + dataEndTag.length()));
        }
      }

      lines.clear();
      lines.addAll(shorterLines);
      return super.transform(lines);
    }

  }

  private static class AsmaTextTransformer extends TextTransformer {

    private final TextTransform transform;

    private static final String DATA_START_TAG = "onmouseover";
    private static final String DATA_END_TAG   = "</tr>";

    public AsmaTextTransformer(final TextTransform transform) {
      super(transform);
      this.transform = transform;
    }

    @Override
    public List<String> transform(final List<String> lines) {
      final List<String> symbols = new ArrayList<>();

      final ListIterator<String> iterator = lines.listIterator();
      while (iterator.hasNext()) {
        String line = iterator.next();
        if (line.contains(DATA_START_TAG)) {
          while (iterator.hasNext()) {
            line = iterator.next();
            if (line.contains(DATA_END_TAG)) {
              iterator.previous();
              iterator.previous();
              line = iterator.previous(); // backtrack
              symbols.add(transform.transform(line));
              iterator.next();
              iterator.next();
              iterator.next();
              break;
            }
          }
        }
      }

      lines.clear();
      lines.addAll(symbols);
      return lines;
    }

  }

}
