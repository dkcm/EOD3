/**
 * GainCapital.java	v0.1	12 January 2016 10:46:37 pm
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextReader;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.ZipTextReader;

/**
 * A <code>Source</code> representing GAIN Capital, a U.S. provider of online
 * trading services.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class GainCapital extends Source {

  public GainCapital() {
    super("http://ratedata.gaincapital.com/");

    // e.g.
    // http://ratedata.gaincapital.com/2015/04%20April/XAU_CHF_Week1.zip
    // http://ratedata.gaincapital.com/2015/10%20October/EUR_USD_Week4.zip
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // TODO Auto-generated method stub

  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // TODO Auto-generated method stub

  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // TODO Auto-generated method stub

  }

  @Override
  public TextReader newReader() {
    return new ZipTextReader();
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    // TODO Auto-generated method stub
    return null;
  }

}
