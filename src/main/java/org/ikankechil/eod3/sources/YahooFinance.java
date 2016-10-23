/**
 * YahooFinance.java  v0.9  15 December 2013 8:28:07 PM
 *
 * Copyright � 2013-2016 Daniel Kuan.  All rights reserved.
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
 * A <code>Source</code> representing Yahoo! Finance.
 *
 * @author Daniel Kuan
 * @version 0.9
 */
public class YahooFinance extends Source {
  // TODO adjust prices for splits, etc. (adjust only when close and adj. close are unequal)

  // Date-related URL parameters
  private static final String START_MONTH = "&a=";
  private static final String START_DATE  = "&b=";
  private static final String START_YEAR  = "&c=";
  private static final String END_MONTH   = "&d=";
  private static final String END_DATE    = "&e=";
  private static final String END_YEAR    = "&f=";
  private static final String FREQUENCY   = "&g=";

  // Exchange-related constants
  private static final String L           = ".L";
  private static final String IR          = ".IR";
  private static final String SI          = ".SI";
  private static final String JK          = ".JK";
  private static final String KL          = ".KL";
  private static final String BK          = ".BK";
  private static final String AX          = ".AX";
  private static final String NZ          = ".NZ";
  private static final String TO          = ".TO";
  private static final String F           = ".F";
  private static final String PA          = ".PA";
  private static final String AS          = ".AS";
  private static final String BR          = ".BR";
  private static final String SW          = ".SW";
  private static final String MI          = ".MI";
  private static final String MA          = ".MA";
  private static final String LS          = ".LS";
  private static final String VI          = ".VI";
  private static final String AT          = ".AT";
  private static final String IS          = ".IS";
  private static final String HK          = ".HK";
  private static final String SS          = ".SS";
  private static final String SZ          = ".SZ";
  private static final String BO          = ".BO";
  private static final String NS          = ".NS";
  private static final String OL          = ".OL";
  private static final String KS          = ".KS";
  private static final String TW          = ".TW";
  private static final String ST          = ".ST";
  private static final String HE          = ".HE";
  private static final String CO          = ".CO";
  private static final String IC          = ".IC";
  private static final String ME          = ".ME";
  private static final String RG          = ".RG";
  private static final String TL          = ".TL";
  private static final String VS          = ".VS";
  private static final String PR          = ".PR";
  private static final String TA          = ".TA";
  private static final String CA          = ".CA";
  private static final String QA          = ".QA";
  private static final String SA          = ".SA";
  private static final String BA          = ".BA";
  private static final String SN          = ".SN";
  private static final String MX          = ".MX";
  private static final String CR          = ".CR";

  private static final Logger logger      = LoggerFactory.getLogger(YahooFinance.class);

  public YahooFinance() {
    super(YahooFinance.class);

    // supported markets (see https://help.yahoo.com/kb/finance/exchanges-data-providers-yahoo-finance-sln2310.html, previously: http://finance.yahoo.com/exchanges)
    // NYSE, NASDAQ, AMEX and ARCA do not require suffices
    for (final Exchanges exchange : EnumSet.of(NYSE, NASDAQ, AMEX, ARCA)) {
      exchanges.put(exchange, EMPTY);
    }

    exchanges.put(TSX, TO);
    exchanges.put(LSE, L);
    exchanges.put(ISE, IR);
    exchanges.put(FWB, F);
    exchanges.put(PAR, PA);
    exchanges.put(AEX, AS);
    exchanges.put(BB, BR);
    exchanges.put(SWX, SW);
    exchanges.put(MIB, MI);
    exchanges.put(BM, MA);
    exchanges.put(BVLP, LS);
    exchanges.put(WB, VI);
    exchanges.put(ATHEX, AT);
    exchanges.put(BIST, IS);
    exchanges.put(OSLO, OL);
    exchanges.put(SB, ST);
    exchanges.put(HEX, HE);
    exchanges.put(KFB, CO);
    exchanges.put(ICEX, IC);
    exchanges.put(MOEX, ME);
    exchanges.put(RSE, RG);
    exchanges.put(TALSE, TL);
    exchanges.put(VSE, VS);
    exchanges.put(PX, PR);
    exchanges.put(SGX, SI);
    exchanges.put(HKEX, HK);
    exchanges.put(SSE, SS);
    exchanges.put(SZSE, SZ);
    exchanges.put(BSE, BO);
    exchanges.put(NSE, NS);
    exchanges.put(KRX, KS);
    exchanges.put(TWSE, TW);
    exchanges.put(IDX, JK);
    exchanges.put(MYX, KL);
    exchanges.put(SET, BK);
    exchanges.put(ASX, AX);
    exchanges.put(NZX, NZ);
    exchanges.put(TASE, TA);
    exchanges.put(EGX, CA);
    exchanges.put(QSE, QA);
    exchanges.put(BOVESPA, SA);
    exchanges.put(BCBA, BA);
    exchanges.put(BCS, SN);
    exchanges.put(BMV, MX);
    exchanges.put(BVCA, CR);

    // build URL using Yahoo! Finance API
    // http://ichart.finance.yahoo.com/table.csv?s=<Stock Symbol>
    //                                          &a=<Start Month - 1>
    //                                          &b=<Start Date>
    //                                          &c=<Start Year>
    //                                          &d=<End Month - 1>
    //                                          &e=<End Date>
    //                                          &f=<End Year>
    //                                          &g=<Frequency>
    //                                          &ignore=.csv
    // e.g.
    // http://ichart.finance.yahoo.com/table.csv?s=A&a=0&b=1&c=2015&d=3&e=4&f=2015
    //
    // Note: no historical FX info
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_MONTH).append(start.get(MONTH))
       .append(START_DATE).append(start.get(DATE))
       .append(START_YEAR).append(start.get(YEAR));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_MONTH).append(end.get(MONTH))
       .append(END_DATE).append(end.get(DATE))
       .append(END_YEAR).append(end.get(YEAR));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    if (frequency != null && frequency != Frequencies.DAILY) {
      url.append(FREQUENCY).append(frequency.frequency());
      logger.debug("Frequency: {}", url);
    }
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      /**
       * Transforms the specified line from Yahoo! Finance CSV into MetaStock CSV
       * format.
       *
       * Inserts <code>symbol</code> as row name, ensures dates are of the
       * YYYYMMDD format and removes the last column ("Adj Close").
       *
       * @param line the line to be transformed
       */
      @Override
      public String transform(final String line) {
        // Yahoo! Finance CSV format
        // Date(YYYY-MM-DD),Open,High,Low,Close,Volume,Adj Close
        // 2013-12-20,25.11,25.35,25.04,25.06,55380100,25.06
        // 2013-12-19,25.12,25.24,24.89,25.14,31880600,23.655678
        // 2013-12-18,24.71,25.20,24.53,25.15,42680500,23.665088

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        // locate last comma (if no volume column, e.g. in FX)
        final int comma = findNth(COMMA, line, SIX, TEN);
        final char[] characters = new char[symbol.length() + comma - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy year
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
