/**
 * Converter.java v2.7  28 November 2013 10:14:02 PM
 *
 * Copyright © 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static org.ikankechil.eod3.Converter.PoolSize.*;
import static org.ikankechil.eod3.FilenameConvention.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ikankechil.eod3.io.SymbolsReader;
import org.ikankechil.eod3.sources.Exchanges;
import org.ikankechil.eod3.sources.Source;
import org.ikankechil.io.CompletionServiceFileVisitor;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextWriter;
import org.ikankechil.io.URLInputStreamFactory;
import org.ikankechil.synchronous.TaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts downloaded price and volume data.
 *
 * @author Daniel Kuan
 * @version 2.7
 */
public class Converter {
  // TODO Enhancements
  // 1. [DONE] Fix command-line options - arguments are treated as strings,
  //    files or directories depending on flags
  // 2. [In progress] Complete JUnit tests
  // 3. Leverage TaskExecutor and TaskHelper (initialised with interval,
  //    outputParentDirectory, action)  Can TaskHelper take over ticketing role
  //    of TaskCompletionService?
  // 4. [DONE] Extract update and merge functions
  // 5. [DONE] Reduce accessibility (drop protected for package-private)
  // 6. [DONE] Apply generics to task-oriented methods
  // 7. [DONE] Support non-US exchanges programmatically
  // 8. [DONE] Other-than-daily update and merge functions
  // 9. [DONE] Support stock symbols with numbers in them (to update regexs)
  // 10. [DONE] Abstract file-naming details away (v2.6)
  // 11. Handle share splits / reverse-splits
  // 12. [DONE] v2.8 keep date formatting to a minimum during file updates
  // 13. Retry on failure

  private final Source                         source;
  private final TextReader                     reader;
  private final TextWriter                     writer;

  private final SymbolsReader                  symbolsReader;

  private final Map<PoolSize, ExecutorService> threadPools    = new EnumMap<>(PoolSize.class);

  // Actions
  private final Action<File>                   convert        = new Action<File>() {
    @Override
    public File execute(final String symbol, final Exchanges exchange, final Interval interval, final File outputParentDirectory)
        throws IOException {
      return convert(symbol, exchange, interval, outputParentDirectory);
    }
  };
  private final Action<File>                   download       = new Action<File>() {
    @Override
    public File execute(final String symbol, final Exchanges exchange, final Interval interval, final File outputParentDirectory)
        throws IOException {
      return download(symbol, exchange, interval, outputParentDirectory);
    }
  };

  final DateFormat                             dateFormat     = new SimpleDateFormat("yyyyMMdd", Locale.US);

  // Text-related constants
  private static final char                    COMMA          = ',';
  private static final char                    DOT            = '.';
  private static final char                    SPACE          = ' ';
  private static final char                    TAB            = '\t';
  private static final char                    LF             = '\n';

  // File-related constants
  private static final String                  SYNTAX         = "regex:";

  // Multi-threading constants
  private static final int                     TIME_OUT       = Short.MAX_VALUE;
  private static final TimeUnit                TIME_OUT_UNIT  = TimeUnit.MILLISECONDS;
  private static final int                     PROCESSORS     = Runtime.getRuntime().availableProcessors();

  private static final Comparator<String>      REVERSE_CHRONO = new Comparator<String>() { // reverse chronological order
    @Override
    public int compare(final String o1, final String o2) {
      final String date1 = extractSymbolAndDate(o1).getValue();
      final String date2 = extractSymbolAndDate(o2).getValue();
      return date2.compareTo(date1);
    }
  };

  static final Logger                          logger         = LoggerFactory.getLogger(Converter.class);

  interface Action<V> {
    V execute(final String symbol, final Exchanges exchange, final Interval interval, final File outputParentDirectory)
        throws IOException;
  }

  public Converter(final Source source) {
    this(source, null);
  }

  public Converter(final Source source, final TextReader reader) {
    if (source == null) {
      throw new NullPointerException("Null source");
    }
    logger.debug("Initialising...");

    this.source = source;
    this.reader = (reader == null) ? source.newReader() : reader;
    writer = new TextWriter();

    // use a different TextReader instance as source data might be encoded
    symbolsReader = new SymbolsReader();

    // initialise thread pools
    for (final PoolSize size : PoolSize.values()) {
      threadPools.put(size, Executors.newFixedThreadPool(PROCESSORS * size.loadMultiplier));
    }
    logger.debug("Thread pools created: {}", threadPools.size());

    logger.info("Initialisation completed");
  }

  /**
   * Converts data of all symbols specified in <code>symbolsFile</code> from
   * source format to target format.
   *
   * @param symbolsFile the <code>File</code> of symbols
   * @return the destination directory where all <code>File</code>s are written to
   * @throws FileNotFoundException
   *           if the file does not exist, is a directory rather than a regular
   *           file, or for some other reason cannot be opened for reading
   * @throws IOException if an I/O error occurs
   * @throws InterruptedException
   */
  public File convert(final File symbolsFile)
      throws FileNotFoundException, IOException, InterruptedException {
    return convert(symbolsFile, Interval.SINCE_INCEPTION, symbolsFile.getParentFile());
  }

  /**
   * Converts data of all symbols specified in <code>symbolsFile</code> over
   * <code>interval</code> from source format to target format, writing into
   * <code>outputParentDirectory</code>.
   *
   * @param symbolsFile
   * @param interval start date, end date and frequency
   * @param outputParentDirectory
   * @return the destination directory where all <code>File</code>s are written
   *         to, which is a child of <code>outputParentDirectory</code>
   * @throws FileNotFoundException
   *           if <code>symbolsFile</code> does not exist, is a directory rather
   *           than a regular file, or for some other reason cannot be opened
   *           for reading
   * @throws IOException
   * @throws InterruptedException
   */
  public File convert(final File symbolsFile, final Interval interval, final File outputParentDirectory)
      throws FileNotFoundException, IOException, InterruptedException {
    return execute(symbolsFile, interval, outputParentDirectory, convert);
  }

  private <V> File execute(final File symbolsFile,
                           final Interval interval,
                           final File outputParentDirectory,
                           final Action<V> action)
      throws FileNotFoundException, IOException, InterruptedException {
    if (interval == null) {
      throw new NullPointerException("Null interval");
    }
    logger.info("Conversion commencing: {}", symbolsFile);

    // read symbols and exchanges
    final Map<String, Set<String>> markets = symbolsReader.read(symbolsFile); // Map<Exchange, Set<Symbol>>

    // create root directory
    final String provider = source.directory();
    final File directory = new File(outputParentDirectory, provider);
    if (!directory.exists()) {
      directory.mkdir();
    }
    logger.info("Writing for source: {}", provider);

    // submit tasks
    final Map<Future<V>, String> futures = new HashMap<>();
    final CompletionService<V> completionService = new ExecutorCompletionService<>(threadPools.get(LARGE));
    for (final Entry<String, Set<String>> market : markets.entrySet()) { // Map<Exchange, Set<Symbol>>
      // create sub-directory for each exchange
      final String exchange = market.getKey();
      final File subdir = new File(directory, exchange);
      subdir.mkdir();
      logger.debug("Created folder: {}", subdir);

      // create and submit tasks
      final Set<String> symbols = market.getValue();
      final Map<Future<V>, String> tasks = submitTasks(completionService,
                                                       symbols,
                                                       Exchanges.toExchange(exchange),
                                                       interval,
                                                       subdir,
                                                       action);
      futures.putAll(tasks);
      logger.info("Tasks submitted for {}: {}", exchange, tasks.size());
    }
    final int numberOfTasks = futures.size();
    logger.info("Tasks submitted: {}", numberOfTasks);

    // retrieve results
    final Map<String, Throwable> failures = newMap(numberOfTasks);
    final List<V> destinations = retriveResults(completionService, futures, failures);

    logger.info("Conversion completed: {}", symbolsFile);
    report(destinations, failures);

    return directory;
  }

  /**
   * Conversion of a series of symbols.
   *
   * @param symbols a series of symbols
   * @throws InterruptedException
   */
  public List<File> convert(final Collection<String> symbols) throws InterruptedException {
    return convert(symbols, null, Interval.SINCE_INCEPTION, null);
  }

  public List<File> convert(final Collection<String> symbols,
                            final Exchanges exchange,
                            final Interval interval,
                            final File outputParentDirectory)
      throws InterruptedException {
    return execute(symbols, exchange, interval, outputParentDirectory, convert);
  }

  private <V> List<V> execute(final Collection<String> symbols,
                              final Exchanges exchange,
                              final Interval interval,
                              final File outputParentDirectory,
                              final Action<V> action)
      throws InterruptedException {
    if (interval == null) {
      throw new NullPointerException("Null interval");
    }
    logger.info("Conversion commencing");
    final CompletionService<V> completionService = new ExecutorCompletionService<>(threadPools.get(LARGE));

    // create and submit tasks
    final Map<Future<V>, String> futures = submitTasks(completionService,
                                                       symbols,
                                                       exchange,
                                                       interval,
                                                       outputParentDirectory,
                                                       action);
    final int numberOfTasks = futures.size();
    logger.info("Tasks submitted: {}", numberOfTasks);

    // retrieve results
    final Map<String, Throwable> failures = newMap(numberOfTasks);
    final List<V> destinations = retriveResults(completionService, futures, failures);

    logger.info("Conversion completed");
    report(destinations, failures);

    return destinations;
  }

  private static final <V> Map<Future<V>, String> submitTasks(final CompletionService<V> completionService,
                                                              final Collection<String> symbols,
                                                              final Exchanges exchange,
                                                              final Interval interval,
                                                              final File outputParentDirectory,
                                                              final Action<V> action) {
    final Map<Future<V>, String> futures = newMap(symbols.size());

    for (final String symbol : symbols) {
      final Future<V> future = completionService.submit(new Callable<V>() {
        @Override
        public V call() throws Exception {
          return action.execute(symbol, exchange, interval, outputParentDirectory);
        }
      });
      futures.put(future, symbol);
    }

    return futures;
  }

  private static final <V> List<V> retriveResults(final CompletionService<V> completionService,
                                                  final Map<Future<V>, String> futures,
                                                  final Map<String, Throwable> failures)
      throws InterruptedException {
    final int numberOfTasks = futures.size();
    final List<V> destinations = newList(numberOfTasks);

    for (int t = 0; t < numberOfTasks; ++t) {
      final Future<V> future = completionService.poll(TIME_OUT, TIME_OUT_UNIT); // TODO match Futures to those at submission
      final String symbol = futures.get(future);
      try {
        if (future == null) {
          throw new TimeoutException("Task timed out after " + TIME_OUT + SPACE + TIME_OUT_UNIT.toString().toLowerCase());
        }
        destinations.add(future.get());
      }
      catch (final ExecutionException eE) {
        failures.put(symbol, eE);
        logger.warn("Task failed.  Cause: {}", eE.getCause(), eE);
      }
      catch (final CancellationException cE) {
        failures.put(symbol, cE);
        logger.info("Task cancelled.  Cause: {}", cE.getCause(), cE);
      }
      catch (final TimeoutException tE) {
        failures.put(symbol, tE);
        logger.info("Task timed out.  Cause: {}", tE.getCause(), tE);
      }
      finally {
        if (future != null) {
          future.cancel(true);
        }
      }
    }
    logger.info("Results retrieved: {}", numberOfTasks);

    return destinations;
  }

  private static final <V, W> void report(final List<V> passes, final Map<W, Throwable> failures) {
    logger.info("Total: {}\tPass: {}\tFail: {}",
                passes.size() + failures.size(),
                passes.size(),
                failures.size());

    if (!failures.isEmpty()) {
      int i = 0;
      final StringBuilder builder = new StringBuilder("Failures:\n");
      for (final Entry<W, Throwable> failure : failures.entrySet()) {
        builder.append(TAB)
               .append(++i)
               .append(DOT)
               .append(SPACE)
               .append(failure.getKey())
               .append(SPACE)
               .append(failure.getValue().getCause())
               .append(LF);
      }
      logger.info("{}", builder);
    }
  }

  enum PoolSize {
    TINY(1),
    SMALL(5),
    LARGE(25);

    final int loadMultiplier;

    private PoolSize(final int loadMultiplier) {
      this.loadMultiplier = loadMultiplier;
    }

  }

  /**
   * Converts data of a single symbol.
   *
   * @param symbol
   * @return the destination <code>File</code>
   * @throws IOException if an I/O error occurs
   * @throws MalformedURLException
   */
  public File convert(final String symbol) throws IOException, MalformedURLException {
    return convert(symbol, null, Interval.SINCE_INCEPTION, null);
  }

  public File convert(final String symbol,
                      final Exchanges exchange,
                      final Interval interval,
                      final File outputParentDirectory)
      throws IOException, MalformedURLException {
    logger.info("Converting: {} (Exchange: {})", symbol, exchange);

    // read
    final URL url = source.url(symbol,
                               exchange,
                               interval.start(),
                               interval.end(),
                               interval.frequency());
    // leave column header skipping to transformer
    final List<String> lines = reader.read(url);

    // transform
    source.newTransformer(source.newTransform(symbol)).transform(lines);
    if (lines.isEmpty()) {
      logger.warn("Empty URL: {}", url);
      throw new IOException("Empty URL: " + url);
    }

    // write
    final File destination = new File(outputParentDirectory, getFilename(symbol, interval));
    writer.write(lines, destination);

    logger.info("Symbol converted: {} (Exchange: {})", symbol, exchange);
    return destination;
  }

  public File download(final File symbolsFile, final Interval interval, final File outputParentDirectory)
      throws FileNotFoundException, IOException, InterruptedException {
    return execute(symbolsFile, interval, outputParentDirectory, download);
  }

  public List<File> download(final Collection<String> symbols,
                             final Exchanges exchange,
                             final Interval interval,
                             final File outputParentDirectory)
      throws InterruptedException {
    return execute(symbols, exchange, interval, outputParentDirectory, download);
  }

  public File download(final String symbol,
                       final Exchanges exchange,
                       final Interval interval,
                       final File outputParentDirectory)
      throws IOException, MalformedURLException {
    logger.info("Downloading: {}", symbol);

    final URL url = source.url(symbol,
                               exchange,
                               interval.start(),
                               interval.end(),
                               interval.frequency());
    final File destination = new File(outputParentDirectory, getFilename(symbol, interval));
    try (
      final ReadableByteChannel rbc = Channels.newChannel(URLInputStreamFactory.newInputStream(url));
      final FileChannel fc = FileChannel.open(destination.toPath(),
                                              StandardOpenOption.CREATE,
                                              StandardOpenOption.WRITE);
    ) {
      final long transferred = fc.transferFrom(rbc, 0, Long.MAX_VALUE); // ~8 exabytes (8 x 1024^6)
      logger.debug("Bytes transferred for {}: {}", symbol, transferred);
    }

    logger.info("Symbol downloaded: {}", symbol);
    return destination;
  }

  /**
   * Updates all data files in <code>outputParentDirectory</code>.
   *
   * @param outputParentDirectory the file directory to be updated
   * @return outputParentDirectory
   * @throws IOException
   *           if an I/O error is thrown by the file visitor
   */
  public File update(final File outputParentDirectory) throws IOException {
    // Algorithm
    // 1. find data files (assume some are in directories while others are not)
    // 2. read first line in each data file, assuming it the latest
    // 3. extract symbol and date, and store date in a dictionary
    // 4. download, convert and write to a separate update file
    if (!outputParentDirectory.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + outputParentDirectory);
    }

    logger.info("Updating files in: {}", outputParentDirectory);

    final CompletionServiceFileVisitor<File> visitor =
        new CompletionServiceFileVisitor<>(SYNTAX + FILENAME_REGEX,
                                           new UpdateFile(convert),
                                           threadPools.get(LARGE));
    Files.walkFileTree(outputParentDirectory.toPath(), visitor);
    report(visitor.results(), visitor.failures());

    logger.info("Updated files in: {}", outputParentDirectory);
    return outputParentDirectory;
  }

  class UpdateFile extends AbstractTaskHelper {

    final Action<File>                  action;
    private final Calendar              now;
    final String                        endYYYYMMDD;
    private final Map<Calendar, String> startYYYYMMDDs;
    private final Map<String, Interval> updateIntervals;
    private final Map<Path, Exchanges>  exchanges;

    public UpdateFile(final Action<File> action) {
      this.action = action;
      now = Calendar.getInstance();
      endYYYYMMDD = dateFormat.format(now.getTime());
      startYYYYMMDDs = new HashMap<>();
      updateIntervals = new HashMap<>();
      exchanges = new HashMap<>();
    }

    final String readLatestLine(final Path file) throws IOException {
      String latest;
      try (final BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
        latest = br.readLine();
      }
      if ((latest == null) || latest.isEmpty()) {
        logger.warn("Empty file: {}", file);
        throw new IOException("Empty file: " + file);
      }
      return latest;
    }

    final Interval getUpdateInterval(final String date, final Frequencies frequency)
        throws ParseException {
      Interval interval;
      final String dateAndFrequency = date + frequency.frequency(); // prevent one frequency from masking others
      synchronized (updateIntervals) {
        if ((interval = updateIntervals.get(dateAndFrequency)) == null) {
          // set interval start
          final Calendar start = Calendar.getInstance();
          start.setTime(dateFormat.parse(date));
          switch (frequency) {
            case MONTHLY:
              start.add(Calendar.MONTH, 1);
              start.set(Calendar.DATE, 1); // start 1st of next month
              break;

            case WEEKLY:
              start.add(Calendar.DATE, 7); // start the next week
              break;

            case DAILY:
            default:
              start.add(Calendar.DATE, 1); // start the next day
              break;
          }
          final String startYYYYMMDD = getStartYYYYMMDD(start);

          // terminate task by throwing exception if start >= now
          if (!start.before(now)) {
            throw new IllegalArgumentException("Start date (" + startYYYYMMDD + ") not before end date (" + endYYYYMMDD + ")");
          }
          updateIntervals.put(dateAndFrequency, interval = new Interval(start, now, frequency));
          logger.debug("New interval inserted: {} {} {}", startYYYYMMDD, endYYYYMMDD, frequency);
        }
      }
      return interval;
    }

    final String getStartYYYYMMDD(final Calendar start) {
      String startYYYYMMDD;
      if ((startYYYYMMDD = startYYYYMMDDs.get(start)) == null) {
        // cache to prevent reformatting
        startYYYYMMDDs.put(start, startYYYYMMDD = dateFormat.format(start.getTime()));
      }
      return startYYYYMMDD;
    }

    final Exchanges extractExchange(final Path parent) {
      Exchanges exchange;
      if ((exchange = exchanges.get(parent)) == null) {
        exchanges.put(parent, exchange = Exchanges.toExchange(parent.getFileName().toString()));
      }
      return exchange;
    }

    @Override
    public Callable<File> newTask(final Path file) {
      return new Callable<File>() {
        @Override
        public File call() throws Exception {
          // read most recent entry
          final String latest = readLatestLine(file);

          // extract symbol and date
          final Entry<String, String> symbolAndDate = extractSymbolAndDate(latest);
          final String symbol = symbolAndDate.getKey();
          final String date = symbolAndDate.getValue();

          // form update interval
          final String filename = file.getFileName().toString();
          final Frequencies frequency = getFrequencyFrom(filename);
          final Interval interval = getUpdateInterval(date, frequency);
          final String startYYYYMMDD = getStartYYYYMMDD(interval.start());

          // extract exchange
          final Path parent = file.getParent();
          final Exchanges exchange = extractExchange(parent);

          logger.info("{} current as of: {}.  Updating from {} to {}",
                      symbol,
                      date,
                      startYYYYMMDD,
                      endYYYYMMDD);

          return action.execute(symbol,
                                exchange,
                                interval,
                                parent.toFile());
        }
      };
    }

  }

  /**
   * Merges files in <code>outputParentDirectory</code>.
   *
   * @param outputParentDirectory
   * @return outputParentDirectory
   * @throws IOException
   *           if an I/O error is thrown by the file visitor
   */
  public File merge(final File outputParentDirectory) throws IOException {
    // Algorithm
    // 1. Match update file to existing file for every symbol
    //    a) all update and existing files are in one single directory
    //    b) [NOT SUPPORTED] update and existing files in separate directories
    //    c) one update file per symbol
    //    d) [NOT SUPPORTED] several update files per symbol
    // 2. Read update file and existing file
    // 3. Insert and overwrite updates into existing data
    if (!outputParentDirectory.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + outputParentDirectory);
    }

    logger.info("Merging files in: {}", outputParentDirectory);

    final CompletionServiceFileVisitor<File> visitor =
        new CompletionServiceFileVisitor<>(SYNTAX + FILENAME_WITH_DATES_REGEX,
                                           new MergeFile(),
                                           threadPools.get(TINY));
    Files.walkFileTree(outputParentDirectory.toPath(), visitor);
    report(visitor.results(), visitor.failures());

    logger.info("Merged files in: {}", outputParentDirectory);
    return outputParentDirectory;
  }

  class MergeFile extends AbstractTaskHelper {

    final File target(final Path src) {
      // assume source and target are in the same directory
      final File target = new File(src.getParent().toFile(),
                                   removeDatesFrom(src.getFileName().toString()));
      logger.debug("Source: {}, Target: {}", src, target);
      return target;
    }

    @Override
    public Callable<File> newTask(final Path src) {
      return new Callable<File>() {
        @Override
        public File call() throws Exception {
          final File target = target(src);
          if (target.exists()) {
            merge(src.toFile(), target);
            Files.delete(src);
            logger.debug("File deleted after merging: {}", src);
          }
          else {
            // rename update file as target if latter is absent
            Files.move(src, target.toPath());
            logger.info("File renamed after merging: {} -> {}", src, target);
          }
          return target;
        }
      };
    }

  }

  /**
   * Merges <code>src</code>'s contents into <code>target</code> in reverse
   * chronological order.
   *
   * @param src
   * @param target
   * @return <code>target</code>
   * @throws IOException if <code>src</code> is empty or if an I/O error occurs
   */
  public File merge(final File src, final File target) throws IOException {
    logger.info("Merging files: {} -> {}", src, target);

    if (src.length() <= 0) {
      final String name = src.getName();
      logger.warn("Empty source file: {}", name);
      throw new IOException("Empty source file: " + name);
    }
    // remove duplicates
    // sort in descending / reverse chronological order
    final Set<String> lines = new TreeSet<>(REVERSE_CHRONO);

    // append target to source
    lines.addAll(reader.read(src));
    lines.addAll(reader.read(target));

    // write merged lines to target
    writer.write(lines, target);

    logger.info("Files merged");
    return target;
  }

  abstract class AbstractTaskHelper implements TaskHelper<Path, File> {

    @Override
    public File handleExecutionFailure(final ExecutionException eE, final Path operand) {
      return operand.toFile();
    }

    @Override
    public File handleTaskCancellation(final CancellationException cE, final Path operand) {
      return operand.toFile();
    }

    @Override
    public File handleTimeout(final TimeoutException tE, final Path operand) {
      return operand.toFile();
    }

  }

  /**
   *
   * @throws InterruptedException if interrupted while waiting
   */
  public void stop() throws InterruptedException {
    for (final ExecutorService threadPool : threadPools.values()) {
      threadPool.shutdown();
      threadPool.awaitTermination(Short.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
    logger.info("Shutdown requested");
  }

  static final Entry<String, String> extractSymbolAndDate(final String line) {
    int comma = line.indexOf(COMMA);
    final String symbol = line.substring(0, comma);
    final String date = line.substring(++comma, line.indexOf(COMMA, comma));

    return new AbstractMap.SimpleEntry<>(symbol, date);
  }

  private static final <E> List<E> newList(final int initialCapacity) {
    return new ArrayList<>(initialCapacity);
  }

  private static final <K, V> Map<K, V> newMap(final int initialCapacity) {
    return new HashMap<>(initialCapacity);
  }

}
