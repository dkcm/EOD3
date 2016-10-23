/**
 * Nasdaq.java  v0.1  21 December 2015 9:47:52 pm
 *
 * Copyright � 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;

/**
 *
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class Nasdaq extends Source {

  public Nasdaq() {
    super("http://www.nasdaq.com/symbol/uso/historical");
    // e.g. http://www.nasdaq.com/symbol/uso/historical 10y|false|AAPL
    //      http://www.nasdaq.com/aspx/historical_quotes.aspx?symbol=AAPL&selected=AAPL
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {

  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {

  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {

  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return null;
  }

}
