/**
 * QuoteMedia.java	v0.8	24 December 2013 1:40:26 AM
 *
 * Copyright © 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;
import java.util.EnumSet;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing QuoteMedia.
 *
 * @author Daniel Kuan
 * @version 0.8
 */
public class QuoteMedia extends Source {
// TODO cannot seem to narrow download window!

  private final int           webmasterID;

  private static final String SYMBOL      = "&symbol=";

  // Date-related URL parameters
  private static final String START_DATE  = "&startDay=";
  private static final String START_MONTH = "&startMonth=";
  private static final String START_YEAR  = "&startYear=";
  private static final String END_DATE    = "&endDay=";
  private static final String END_MONTH   = "&endMonth=";
  private static final String END_YEAR    = "&endYear=";
  private static final String MAX_YEARS   = "&maxDownloadYears=" + Short.MAX_VALUE;

  // Exchange-related constants
  private static final String LN          = ":LN";
  private static final String SI          = ":SI";
  private static final String AU          = ":AU";
  private static final String NZ          = ":NZ";
  private static final String CA          = ":CA";
  private static final String FF          = ":FF";
  private static final String HK          = ":HK";
  private static final String SH          = ":SH";
  private static final String SZ          = ":SZ";
  private static final String TK          = ":TK";
  private static final String OK          = ":OK";
  private static final String ID          = ":ID";
  private static final String MY          = ":MY";
  private static final String TH          = ":TH";
  private static final String MB          = ":MB";
  private static final String IN          = ":IN";
  private static final String KR          = ":KR";
  private static final String TW          = ":TW";
  private static final String OS          = ":OS";
  private static final String ST          = ":ST";
  private static final String CO          = ":CO";
  private static final String MI          = ":MI";
  private static final String PA          = ":PA";
  private static final String AS          = ":AS";
  private static final String MA          = ":MA";
  private static final String VN          = ":VN";
  private static final String AT          = ":AT";
  private static final String RU          = ":RU";
  private static final String SM          = ":SM";
  private static final String BV          = ":BV";
  private static final String AR          = ":AR";
  private static final String CL          = ":CL";
  private static final String MX          = ":MX";

  private static final Logger logger      = LoggerFactory.getLogger(QuoteMedia.class);

  public QuoteMedia() {
    this(500);
  }

  public QuoteMedia(final int webmasterID) {
    super(QuoteMedia.class);

    this.webmasterID = webmasterID;

    // supported markets (see http://www.quotemedia.com/legal/tos/#times and http://www.quotemedia.com/quotetools/symbolHelp/SymbolHelp_US_Version_Default.html)
    // NYSE, NASDAQ, AMEX and NYSEARCA do not require suffices
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, NYSEARCA, FX)) {
      exchanges.put(exchange, EMPTY);
    }

    exchanges.put(TSX, CA);
    exchanges.put(LSE, LN);
    exchanges.put(FWB, FF);
    exchanges.put(PAR, PA);
    exchanges.put(AMS, AS);
    exchanges.put(SWX, SM);
    exchanges.put(MIB, MI);
    exchanges.put(BM, MA);
    exchanges.put(WB, VN);
    exchanges.put(ATHEX, AT);
    exchanges.put(OSLO, OS);
    exchanges.put(SB, ST);
    exchanges.put(KFB, CO);
    exchanges.put(MOEX, RU);
    exchanges.put(SGX, SI);
    exchanges.put(HKSE, HK);
    exchanges.put(SSE, SH);
    exchanges.put(SZSE, SZ);
    exchanges.put(TSE, TK);
    exchanges.put(OSE, OK);
    exchanges.put(BSE, MB);
    exchanges.put(NSE, IN);
    exchanges.put(KRX, KR);
    exchanges.put(TWSE, TW);
    exchanges.put(IDX, ID);
    exchanges.put(MYX, MY);
    exchanges.put(SET, TH);
    exchanges.put(ASX, AU);
    exchanges.put(NZX, NZ);
    exchanges.put(BOVESPA, BV);
    exchanges.put(BCBA, AR);
    exchanges.put(BCS, CL);
    exchanges.put(BMV, MX);

    // Notes:
    // 1. only 3 years' rolling window worth of data if maxDownloadYears parameter not supplied
    // 2. start dates ignored; maxDownloadYears can be used as an offset in conjunction with end dates
    // 3. other valid webmaster IDs: 96483 (american association of individual investors)
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    appendWebmasterID(url);
    if (exchange == FX) {
      // prepend currency pair with $ (e.g. $USDJPY)
      url.append(DOLLAR);
      appendSymbol(url, symbol);
    }
    else {
      appendSymbol(url, symbol);
      appendExchange(url, exchange);
    }
  }

  private final void appendWebmasterID(final StringBuilder url) {
    url.append(webmasterID).append(SYMBOL);
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(start.get(DATE))
       .append(START_MONTH).append(start.get(MONTH) + 1)
       .append(START_YEAR).append(start.get(YEAR));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(end.get(DATE))
       .append(END_MONTH).append(end.get(MONTH) + 1)
       .append(END_YEAR).append(end.get(YEAR));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    url.append(MAX_YEARS);
    logger.debug("Maximum number of years requested: {}", url);
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
        // QuoteMedia CSV format
        // date,open,high,low,close,volume,changed,changep,adjclose,tradeval,tradevol
        // 2013-12-24,25.38,25.62,25.35,25.43,12157877,0.11,0.43%,25.43,310050677.73,44813

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final int comma = findNthLast(COMMA, line, FIVE);
        final char[] characters = new char[(symbol.length() + comma) - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy years
        i = getChars(line, ZERO, FOUR, characters, ++i);
        // copy month
        i = getChars(line, FIVE, SEVEN, characters, i);
        // copy rest of line
        line.getChars(EIGHT, comma, characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
