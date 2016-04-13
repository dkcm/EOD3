/**
 * TaiwanStockExchange.java v0.1 7 July 2015 10:36:52 AM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;

/**
 *
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class TaiwanStockExchange extends Source {

  public TaiwanStockExchange() {
    super("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/MI_INDEX3_print.php?genpage=genpage/Report[yy][mm]/A112[yy][mm][dd]ALLBUT0999_1.php&type=csv");
//    url = url.replace("[yy]", yy).replace("[mm]", mm).replace("[dd]", dd);

    // TODO alternatives
    // 1. "http://mis.twse.com.tw/data/$stockno.csv"
    // 2. "http://brk.twse.com.tw:8000/isin/C_public.jsp?strMode=2"
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
  public TextTransform newTransform(final String symbol) {
    // TODO Auto-generated method stub
    return null;
  }

  public static void main(final String[] args) {
    // TODO Auto-generated method stub

  }

}
