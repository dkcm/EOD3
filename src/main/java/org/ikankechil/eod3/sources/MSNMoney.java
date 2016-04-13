/**
 * MSNMoney.java	v0.1	19 January 2016 12:36:28 am
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
 * @version 0.1
 */
public class MSNMoney extends Source {

  private static final String DURATION = "&chartType=";

  static final Logger         logger = LoggerFactory.getLogger(MSNMoney.class);

  public MSNMoney() {
    super("http://finance.services.appex.bing.com/Market.svc/ChartDataV5?isEOD=true&isCS=true&isVol=true&symbols=");

    // supported markets
    exchanges.put(NYSE, "126.1.%s.NYS");
    exchanges.put(NASDAQ, "126.1.%s.NAS");
    exchanges.put(NYSEARCA, "%s.ARCX");

    exchanges.put(SGX, "%s.SES");


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
