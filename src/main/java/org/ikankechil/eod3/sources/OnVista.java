/**
 * OnVista.java v0.1 15 January 2016 5:26:05 PM
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing OnVista.de, a finance portal run by a
 * German bank.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class OnVista extends Source {

  public static void main(final String... arguments) throws Exception { // TODO remove
    final String symbol = "OTP";
    final OnVista onvista = new OnVista();
    final List<String> lines = onvista.newReader().read(onvista.url(symbol, BET, null, null, Frequencies.MONTHLY));
    onvista.newTransformer(onvista.newTransform(symbol)).transform(lines);
    System.out.println(lines.get(ZERO));
    System.out.println(lines);
  }

  private final DateFormat    dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

  private static final String START_DATE = "&dateStart=";

  private static final Logger logger     = LoggerFactory.getLogger(OnVista.class);

  public OnVista() {
    super("http://www.onvista.de/onvista/boxes/historicalquote/export.csv?notationId=");

    // supported markets

    // OnVista API
    // e.g.
    // http://www.onvista.de/onvista/boxes/historicalquote/export.csv?notationId=176173&dateStart=15.01.2015&interval=Y5
    // http://www.onvista.de/onvista/boxes/historicalquote/export.csv?notationId=176173&dateStart=15.01.2015&interval=D1
    // http://www.onvista.de/onvista/boxes/historicalquote/export.csv?notationId=176173&dateStart=15.01.2015&interval=M6
    // http://www.onvista.de/index/quote_history.html?ID_NOTATION=8117990&RANGE=12M
    //
    // http://www.onvista.de/onvista/times+sales/popup/historische-kurse/?notationId=176173&dateStart=15.01.2015&interval=D1&assetName=Volkswagen%20VZ&exchange=Xetra
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &dateStart=15.01.2015
    url.append(START_DATE).append(dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // TODO Auto-generated method stub

  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, ONE, true);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Datum;Eroeffnung;Hoch;Tief;Schluss;Volumen
        // 15.01.2015;185,25;187,80;179,70;186,50;1.782.339
        // 16.01.2015;186,00;192,00;185,25;191,80;1.726.791
        // 19.01.2015;192,00;194,10;191,15;192,65;1.054.614
        // 20.01.2015;193,10;194,15;191,80;193,05;889.277
        // 21.01.2015;193,00;195,60;190,75;194,55;1.418.110

        return null;
      }
    };
  }

}
