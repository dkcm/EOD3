/**
 * MoneyControl.java  v0.2  13 November 2014 2:07:22 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing MoneyControl.com.
 *
 * @author Daniel Kuan
 * @version 0.2
 */
class MoneyControl extends Source {

  private final DateFormat    urlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  // Date-related URL parameters
  private static final String START_DATE    = "&fdt=";
  private static final String END_DATE      = "&todt=";
  private static final String FREQUENCY     = "&hdn=";

  private static final Logger logger        = LoggerFactory.getLogger(MoneyControl.class);

  public MoneyControl() {
    super(MoneyControl.class);

    // supported markets
    // only India!
    exchanges.put(NSE, EMPTY);

    // e.g. http://www.moneycontrol.com/stocks/hist_stock_result.php?sc_id=SC20&hdn=daily&fdt=2000-01-01&todt=2014-01-01
    //      http://www.moneycontrol.com/stocks/hist_stock_result.php?ex=N&sc_id=TIS&mycomp=Tata%20Steel&frm_dy=01&frm_mth=01&frm_yr=2015&to_dy=01&to_mth=02&to_yr=2015&x=13&y=11&hdn=daily
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(urlDateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(urlDateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if ((frequency != null) && (frequency != Frequencies.DAILY)) {
      url.append(FREQUENCY).append(frequency.toString().toLowerCase());
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {


        return null;
      }
    };
  }

}
