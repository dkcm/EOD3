/**
 * Morningstar.java	v0.6	26 December 2015 8:53:35 pm
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing Morningstar.
 * <p>
 *
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class Morningstar extends Source {

  private final DateFormat    dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
  private final String        now;

  // Date-related URL parameters
  private static final String START_DATE = "&sd=";
  private static final String END_DATE   = "&ed=";
  private static final String FREQUENCY  = "&freq=";

  // Exchange-related constants
  private static final String XNYS       = "XNYS:";
  private static final String XNAS       = "XNAS:";
  private static final String XASE       = "XASE:";
  private static final String XTSE       = "XTSE:";
  private static final String XLON       = "XLON:";
  private static final String XDUB       = "XDUB";
  private static final String XFRA       = "XFRA:";
  private static final String XPAR       = "XPAR:";
  private static final String XAMS       = "XAMS:";
  private static final String XBRU       = "XBRU";
  private static final String XSWX       = "XSWX:";
  private static final String XMIL       = "XMIL:";
  private static final String XMAD       = "XMAD:";
  private static final String XLIS       = "XLIS";
  private static final String XWBO       = "XWBO:";
  private static final String XATH       = "XATH:";
  private static final String XIST       = "XIST:";
  private static final String XOSL       = "XOSL:";
  private static final String XSTO       = "XSTO:";
  private static final String XHEL       = "XHEL";
  private static final String XCSE       = "XCSE:";
  private static final String XICE       = "XICE:";
  private static final String XMIC       = "XMIC:";
  private static final String XWAR       = "XWAR:";
  private static final String XBUD       = "XBUD:";
  private static final String XPRA       = "XPRA:";
  private static final String XBSE       = "XBSE:";
  private static final String XSES       = "XSES:";
  private static final String XHKG       = "XHKG:";
  private static final String XSHG       = "XSHG:";
  private static final String XSHE       = "XSHE:";
  private static final String XTKS       = "XTKS:";
  private static final String XBOM       = "XBOM:";
  private static final String XNSE       = "XNSE:";
  private static final String XKRX       = "XKRX:";
  private static final String XTAI       = "XTAI:";
  private static final String XIDX       = "XIDX:";
  private static final String XKLS       = "XKLS:";
  private static final String XBKK       = "XBKK:";
  private static final String XASX       = "XASX:";
  private static final String XNZE       = "XNZE:";
  private static final String XTAE       = "XTAE:";
  private static final String XJSE       = "XJSE";
  private static final String XCAI       = "XCAI";
  private static final String XBSP       = "XBSP:";
  private static final String XBUE       = "XBUE:";
  private static final String XSGO       = "XSGO:";
  private static final String XMEX       = "XMEX:";

  private static final String ZERO_STR   = "0";

  private static final Logger logger     = LoggerFactory.getLogger(Morningstar.class);

  public Morningstar() {
    super(Morningstar.class);

    // supported markets
    exchanges.put(NYSE, XNYS);
    exchanges.put(NASDAQ, XNAS);
    exchanges.put(AMEX, XASE);
    exchanges.put(NYSEARCA, EMPTY);
    exchanges.put(TSX, XTSE);
    exchanges.put(LSE, XLON);
    exchanges.put(ISE, XDUB);
    exchanges.put(FWB, XFRA);
    exchanges.put(PAR, XPAR);
    exchanges.put(AMS, XAMS);
    exchanges.put(BB, XBRU);
    exchanges.put(SWX, XSWX);
    exchanges.put(MIB, XMIL);
    exchanges.put(BM, XMAD);
    exchanges.put(BVLP, XLIS);
    exchanges.put(WB, XWBO);
    exchanges.put(ATHEX, XATH);
    exchanges.put(BIST, XIST);
    exchanges.put(OSLO, XOSL);
    exchanges.put(SB, XSTO);
    exchanges.put(HEX, XHEL);
    exchanges.put(KFB, XCSE);
    exchanges.put(ICEX, XICE);
    exchanges.put(MOEX, XMIC);
    exchanges.put(GPW, XWAR);
    exchanges.put(BET, XBUD);
    exchanges.put(PX, XPRA);
    exchanges.put(BVB, XBSE);
    exchanges.put(SGX, XSES);
    exchanges.put(HKSE, XHKG);
    exchanges.put(SSE, XSHG);
    exchanges.put(SZSE, XSHE);
    exchanges.put(TSE, XTKS);
    exchanges.put(BSE, XBOM);
    exchanges.put(NSE, XNSE);
    exchanges.put(KRX, XKRX);
    exchanges.put(TWSE, XTAI);
    exchanges.put(IDX, XIDX);
    exchanges.put(MYX, XKLS);
    exchanges.put(SET, XBKK);
    exchanges.put(ASX, XASX);
    exchanges.put(NZX, XNZE);
    exchanges.put(TASE, XTAE);
    exchanges.put(JSE, XJSE);
    exchanges.put(EGX, XCAI);
    exchanges.put(BOVESPA, XBSP);
    exchanges.put(BCBA, XBUE);
    exchanges.put(BCS, XSGO);
    exchanges.put(BMV, XMEX);

    now = dateFormat.format(new Date());
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    // prefix exchange
    appendExchange(url, exchange);
    appendSymbol(url, symbol);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    // &sd=10/05/2014
    url.append(START_DATE).append(dateFormat.format(start.getTime()));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    // &ed=12/25/2015
    url.append(END_DATE).append(dateFormat.format(end.getTime()));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    appendStartDate(url, DEFAULT_START);
    url.append(END_DATE).append(now);
    logger.debug("Default start and end dates appended: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // &freq=m
    url.append(FREQUENCY).append((frequency != null) ? frequency.frequency()
                                                     : DEFAULT_FREQUENCY); // default to daily
    logger.debug("Frequency: {}", url);
  }

  @Override
  public TextTransformer newTransformer(final TextTransform transform) {
    return new TextTransformer(transform, TWO, false);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // Citigroup Inc (C) Historical Prices
        // Date,Open,High,Low,Close,Volume
        // 12/25/2015,54.40,55.33,50.50,52.71,"16,742,260"
        // 11/30/2015,53.45,56.46,52.49,54.09,"14,425,116"
        // 10/30/2015,49.42,55.06,47.71,53.17,"17,719,275"
        // 09/30/2015,52.40,53.00,48.47,49.61,"18,610,067"
        // 08/31/2015,58.62,59.25,47.10,53.48,"18,914,981"

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        // locate comma before volume
        final int volumePosition = findNth(COMMA, line, FIVE, TEN) + ONE;
        final int length = line.length();
        final StringBuilder volume = new StringBuilder();
        for (int j = volumePosition; j < length; ++j) {
          final char c = line.charAt(j);
          // filter double quotes (") and commas (,)
          if ((c != DOUBLE_QUOTE) && (c != COMMA)) {
            volume.append(c);
          }
        }
        // default to zero if volume is one or more non-digit characters
        if (volume.length() == ZERO) {
          volume.append(ZERO_STR);
        }
        else if (!Character.isDigit(volume.charAt(ZERO))) {
          volume.replace(ZERO, volume.length(), ZERO_STR);
        }

        final char[] characters = new char[symbol.length() + volumePosition - ONE + volume.length()];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;

        // copy date
        i = getChars(line, SIX, TEN, characters, ++i);  // year
        i = getChars(line, ZERO, TWO, characters, i);   // month
        i = getChars(line, THREE, FIVE, characters, i); // date

        // copy OHLCV
        i = getChars(line, TEN, volumePosition, characters, i); // OHLC
        volume.getChars(ZERO, volume.length(), characters, i);  // volume

        return String.valueOf(characters);
      }
    };
  }

}
