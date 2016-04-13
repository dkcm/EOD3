/**
 * BudapestStockExchange.java v0.1 21 October 2015 1:55:13 PM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing the Budapest Stock Exchange (BET).
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class BudapestStockExchange extends Source {

  static final Logger logger = LoggerFactory.getLogger(BudapestStockExchange.class);

  public BudapestStockExchange() {
    super("http://www.bet.hu/topmenu/kereskedesi_adatok/product_search?isinquery=");
    // TODO explore "http://www.bamosz.hu/alapoldal?isin=" and www.bse.hu

    // e.g.
    // http://bse.hu/bdataservlet?resolution=d&method=InstrDataDownload&start_date=22.10.2014.&startdate_dayfrom=&startdate_dayfrom_hidden=&startdate_monthfrom=&startdate_yearfrom=-&end_date=22.10.2015.&startdate_dayto=&startdate_dayto_hidden=&startdate_monthto=&startdate_yearto=-&selectedsection=stock&securityid=4042&SUBMIT=&profileid=1084&portleturl=http%3A%2F%2Fbse.hu%2Fmenun_kivuli%2Fdinportl%2Fdownloadable%2Finstrdatadownload
    // http://bet.hu/bdataservlet?resolution=d&method=InstrDataDownload&start_date=2010.10.22.&startdate_yearfrom=-&startdate_dayfrom_hidden=&startdate_monthfrom=&startdate_dayfrom=&end_date=2015.10.22.&startdate_yearto=-&startdate_dayto_hidden=&startdate_monthto=&startdate_dayto=&selectedsection=stock&securityid=4042&SUBMIT=&profileid=1004&portleturl=http%3A%2F%2Fbet.hu%2Fmagyar_egyeb%2Fdinportl%2Finstrdatadownload

    // supported markets
    // BET does not require a suffix
    exchanges.put(BET, EMPTY);
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
