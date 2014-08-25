/**
 * YahooFinance2MetaStock.java	v1.0	27 November 2013 11:08:50 PM
 *
 * Copyright © 2013-2014 Daniel Kuan. All rights reserved.
 */
package org.ikankechil.eod3.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ikankechil.eod3.Converter;
import org.ikankechil.eod3.SymbolsReader;
import org.ikankechil.synchronous.TaskExecutor;
import org.ikankechil.synchronous.TaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An application that converts stock price data from Yahoo! Finance CSV format
 * to MetaStock CSV format.
 *
 * @author Daniel Kuan
 * @version 1.0
 */
public class YahooFinance2MetaStock {

  private final File          yahoo;
  private final SymbolsReader reader;

  // Constants
  private static final File   SYMBOLS       = new File("Symbols.csv");
  private static final String YAHOO_FINANCE = "Yahoo! Finance";

  private static final Logger logger        = LoggerFactory.getLogger(YahooFinance2MetaStock.class);

  public YahooFinance2MetaStock() /*throws FileAlreadyExistsException, IOException*/ {
    yahoo = new File(YAHOO_FINANCE);
    // check that directory was actually created
    if (!yahoo.mkdir()) {
//      throw new FileAlreadyExistsException(yahoo.getCanonicalPath());
    }

    reader = new SymbolsReader(yahoo);
  }

  public static class YahooFinance2MetaStockTaskHelper implements TaskHelper<Entry<String, File>, File> {

    @Override
    public Callable<File> newTask(final Entry<String, File> operand) {
      return new Callable<File>() {
        Converter converter = new Converter(operand.getValue());

        @Override
        public File call() throws Exception {
          return converter.convert(operand.getKey());
        }
      };
    }

    @Override
    public File handleExecutionFailure(final ExecutionException eE, final Entry<String, File> operand) {
      return null;
    }

    @Override
    public File handleTaskCancellation(final CancellationException cE, final Entry<String, File> operand) {
      return null;
    }

    @Override
    public File handleTimeout(final TimeoutException tE, final Entry<String, File> operand) {
      return null;
    }

  }

  /**
   * Main
   *
   * @param args
   * @throws InterruptedException
   */
  public static void main(final String[] args)
      throws FileNotFoundException, IOException, InterruptedException {
    // TODO leverage CLIs?
    new YahooFinance2MetaStock().run(SYMBOLS);
  }

  public void run(final File file)
      throws FileNotFoundException, IOException, InterruptedException {
    convert(reader.readSymbols(file));
  }

  private void convert(final Map<String, File> symbols) throws InterruptedException {
    // 1. Data has shown that: reading from URLs is the bottleneck
    //    => execute as many reads as early as possible
    //    => decompose tasks into greater granularity
    //    => use up network bandwidth
    // 2. Using CompletionService is feasible

    logger.info("Conversion commencing.");
    TaskExecutor executor = new TaskExecutor(Executors.newFixedThreadPool(100/*symbols.size() + 1*/));

    // TODO file writing contention causing delays
    executor.executeAll(symbols.entrySet(),
                        new YahooFinance2MetaStockTaskHelper(),
                        symbols.size(),
                        TimeUnit.SECONDS);
    // TODO retry failures?

    executor.stop();
    logger.info("Conversion completed.");
  }

}

