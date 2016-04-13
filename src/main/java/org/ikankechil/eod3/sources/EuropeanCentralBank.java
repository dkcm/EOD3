/**
 * EuropeanCentralBank.java v0.1 17 June 2015 4:01:18 PM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;

/**
 *
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class EuropeanCentralBank extends Source {

  public static void main(final String[] args) {

  }

  public EuropeanCentralBank() {
    super("http://www.ecb.europa.eu/stats/exchange/eurofxref/html/");

    // http://www.ecb.europa.eu/stats/exchange/eurofxref/html/aud.xml
    // http://www.ecb.europa.eu/stats/exchange/eurofxref/html/sgd.xml
    // http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip

    // alternative: Statistical Data Warehouse
    // http://sdw.ecb.europa.eu/export.do?node=2018794&CURRENCY=SGD&DATASET=0&exportType=csv&advFil=y
    // http://sdw.ecb.europa.eu/export.do?node=2018794&CURRENCY=SGD&DATASET=0&exportType=xls&advFil=y
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
