/**
 * MSNMoney.java	v0.2	19 January 2016 12:36:28 am
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing MSN Money.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
class MSNMoney extends Source {

//  private static final String DURATION = "&chartType=";

  // Exchange-related constants
  private static final String NYS    = "126.1.%s.NYS";
  private static final String NAS    = "126.1.%s.NAS";
  private static final String ARCX   = "%s.ARCX";
  private static final String SES    = "%s.SES";

  static final Logger         logger = LoggerFactory.getLogger(MSNMoney.class);

  public MSNMoney() {
    super(MSNMoney.class);

    // supported markets
    exchanges.put(NYSE, NYS);
    exchanges.put(NASDAQ, NAS);
    exchanges.put(NYSEARCA, ARCX);

    exchanges.put(SGX, SES);

    // e.g.
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=max&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=5y&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=1m&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=5d&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    url.append(String.format(exchanges.get(exchange), symbol));
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        return line;
      }
    };
  }

}
