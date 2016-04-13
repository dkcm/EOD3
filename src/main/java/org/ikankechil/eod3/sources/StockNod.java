/**
 * StockNod.java v0.4 22 October 2015 9:38:33 AM
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

/**
 * A <code>Source</code> representing StockNod.com, which currently sources its
 * data from FinancialContent.com.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class StockNod extends FinancialContent {

  public StockNod() {
    super("http://quotes.stocknod.com/stocknod/action/gethistoricaldata?Symbol=");
//    super("http://quotes.stocknod.com/stocknod/?Method=gethistoricalcsv&Page=HISTORICAL&Ticker=");

    // StockNod API
    // e.g.
    // http://quotes.stocknod.com/stocknod/?Method=gethistoricalcsv&Month=6&Page=HISTORICAL&Ticker=IBM&Year=2005&Range=3
    // http://quotes.stocknod.com/stocknod/action/gethistoricaldata?Month=10&Range=3&Symbol=148%3A275023&Year=2015
    //
    // Note: Cannot request more than 149 months' (12 years 5 months) data at a time
  }

  // StockNod CSV format
  // Ticker,Date(MM/DD/YY),Open,High,Low,Close,Volume,$ Change,% Change
  // IBM,10/21/15,140.25,142.66,139.30,140.92,6996732,+0.28,+0.20%
  // IBM,10/20/15,142.49,142.88,140.27,140.64,15960717,-8.58,-5.75%
  // IBM,10/19/15,149.85,149.97,148.38,149.22,7111896,-1.17,-0.78%
  // IBM,10/16/15,150.45,151.20,149.26,150.39,3477432,+0.30,+0.20%
  // IBM,10/15/15,150.91,151.24,148.58,150.09,3474954,+0.08,+0.05%
  //
  // Symbol,Date,Open,High,Low,Close,Volume,Change,% Change
  // EUR-USD,12/28/15,1.097,1.097,1.097,1.097,0,0.00,0.08%
  // EUR-USD,12/27/15,1.096,1.096,1.096,1.096,0,-0.00,-0.12%
  // EUR-USD,12/25/15,1.097,1.103,1.091,1.098,0,0.00,0.08%
  // EUR-USD,12/24/15,1.097,1.097,1.097,1.097,0,0.01,0.56%
  // EUR-USD,12/23/15,1.091,1.091,1.090,1.091,0,-0.00,-0.40%

  // MetaStock CSV format
  // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

}
