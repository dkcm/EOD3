/**
 * Portfolio.java	v0.1	30 December 2015 11:27:50 am
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;

/**
 * A <code>Source</code> representing Portfolio.hu, a Hungarian financial portal.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class Portfolio extends Source {

  public Portfolio() {
    super("http://www.portfolio.hu/history/adatletoltes.tdp");

    // supported exchanges
    exchanges.put(BET, EMPTY);

    // see http://www.chartoasis.com/free-analysis-software/free-data/portfolio-help.html
    // http://www.portfolio.hu/history/reszveny-adatok.php
    // tipus=1&startdate=2006-08-28&enddate=2015-12-29&open=1&max=1&min=1&close=1&forg=1&ticker=1203:2006-08-28:2015-12-29&text=sz%F6vegf%E1jl

    // http://www.portfolio.hu/reszveny/adatletoltes.tdp?typ=txt&rv=MOL
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
  public TextTransformer newTransformer(final TextTransform transform) {
    // sort in descending / reverse chronological order
    return new TextTransformer(transform, THREE, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    // TODO Auto-generated method stub
    return null;
  }

  public static void main(final String[] args) {
    // TODO Auto-generated method stub

  }

}
