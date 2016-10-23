/**
 * StockNodTest.java  v0.3  28 December 2015 4:58:25 pm
 *
 * Copyright © 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import java.util.Arrays;

/**
 * JUnit test for <code>StockNod</code>.
 *
 *
 *
 * @author Daniel Kuan
 * @version 0.3
 */
public class StockNodTest extends FinancialContentTest {

  public StockNodTest() {
    super(StockNodTest.class);

    originalLines.addAll(Arrays.asList("Symbol,Date,Open,High,Low,Close,Volume,Change,% Change",
                                       "INTC,12/28/15,34.95,35.00,34.57,34.93,9449390,-0.05,-0.14%",
                                       "INTC,12/23/15,34.95,35.05,34.78,35.00,12526330,0.27,0.78%",
                                       "INTC,12/22/15,34.36,34.78,34.27,34.73,18400623,0.49,1.43%",
                                       "INTC,12/21/15,34.11,34.30,33.81,34.24,18006468,0.38,1.11%",
                                       "INTC,12/18/15,34.73,34.75,33.86,33.87,41336631,-1.04,-2.99%"));
    transformedLines.addAll(Arrays.asList("INTC,20151228,34.95,35.00,34.57,34.93,9449390",
                                          "INTC,20151223,34.95,35.05,34.78,35.00,12526330",
                                          "INTC,20151222,34.36,34.78,34.27,34.73,18400623",
                                          "INTC,20151221,34.11,34.30,33.81,34.24,18006468",
                                          "INTC,20151218,34.73,34.75,33.86,33.87,41336631"));
  }

}
