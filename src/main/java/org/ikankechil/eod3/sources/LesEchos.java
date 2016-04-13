/**
 * LesEchos.java  v0.2  4 November 2014 2:10:55 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Les Echos.
 *
 * @author Daniel Kuan
 * @version 0.2
 */
class LesEchos extends Source {

  private final Calendar      now;

  // Date-related URL parameters
  private static final String START_OFFSET_MONTH = "&OFFSET_START_RANGE=-";
  private static final String FREQUENCY          = "&CODE_RESOLUTION=";

  private static final String DAY                = "DAY";
  private static final String WEEK               = "WEEK";
  private static final String MONTH              = "MONTH";

  private static final int    MONTHS_IN_YEAR     = 12;

  // Exchange-related constants
  private static final String EXCHANGE           = "&place=";
  private static final String XNYS               = "XNYS";
  private static final String XPAR               = "XPAR";

  private static final Logger logger             = LoggerFactory.getLogger(LesEchos.class);

  public LesEchos() {
    super("http://bourse.lesechos.fr/bourse/details/donnees_histo.jsp?dataList_sort=date.date&dataList_order=descending&codif=TICK&code=");

    // supported markets
    exchanges.put(NYSE, XNYS);
    exchanges.put(PAR, XPAR);

    now = Calendar.getInstance();

    // e.g. http://bourse.lesechos.fr/bourse/details/donnees_histo.jsp?code=FR0000130007&place=XPAR&codif=ISIN&OFFSET_START_RANGE=-60&CODE_RESOLUTION=DAY
    //      http://bourse.lesechos.fr/bourse/details/donnees_histo.jsp?code=A&place=XNYS&codif=TICK&OFFSET_START_RANGE=-11&CODE_RESOLUTION=DAY
    //
    // Date sorting:
    // &dataList_sort=date.date&dataList_order=ascending | descending
    //
    // Notes:
    // 1. incoming data is in HTML format
    // 2. only 5 years' data available
  }

  @Override
  void appendExchange(final StringBuilder url, final Exchanges exchange) {
    url.append(EXCHANGE);
    super.appendExchange(url, exchange);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    final int offset =
        ((now.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * MONTHS_IN_YEAR) +
        (now.get(Calendar.MONTH) - start.get(Calendar.MONTH));
    url.append(START_OFFSET_MONTH).append(offset);
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null) {
      url.append(FREQUENCY);
      switch (frequency) {
        case MONTHLY:
          url.append(MONTH);
          break;

        case WEEKLY:
          url.append(WEEK);
          break;

        case DAILY:
        default:
          url.append(DAY);
          break;
      }

      logger.debug("Frequency: {}", url);
    }
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
