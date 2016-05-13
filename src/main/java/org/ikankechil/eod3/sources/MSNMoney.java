/**
 * MSNMoney.java	v0.3	19 January 2016 12:36:28 am
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
 * @version 0.3
 */
class MSNMoney extends Source {

//  private static final String DURATION = "&chartType=";

  // Exchange-related constants
  private static final String NYS    = "126.1.%s.NYS";
  private static final String NAS    = "126.1.%s.NAS";
  private static final String ARCX   = "126.1.%s.ARCX";
  private static final String TSE_   = "127.1.%s.TSE";
  private static final String LON    = "151.1.%s.LON";
  private static final String FRA    = "200.1.%s.FRA";
  private static final String PAR_   = "160.1.%s.PAR";
  private static final String AMS_   = "202.1.%s.AMS";
  private static final String SWX_   = "185.1.%s.SWX";
  private static final String MIL    = "223.1.%s.MIL";
  private static final String MCE    = "199.1.%s.MCE";
  private static final String WBO    = "194.1.%s.WBO";
  private static final String ATH    = "212.1.%s.ATH";
  private static final String OSL    = "174.1.%s.OSL";
  private static final String STO    = "170.1.%s.STO";
  private static final String CSE    = "172.1.%s.CSE";
  private static final String MIC    = "231.1.%s.MIC";
  private static final String WAR    = "42.1.%s.WAR";
  private static final String SES    = "143.1.%s.SES";
  private static final String HKG_   = "134.1.%s.HKG.%s"; // e.g. CK Hutchison 134.1.1.HKG.00001, 134.1.2328.HKG.02328
  private static final String SHG    = "136.1.%s.SHG";
  private static final String SHE    = "202.1.%s.SHE";
  private static final String TKS    = "133.1.%s.TKS";
  private static final String BOM    = "139.1.%s.BOM";
  private static final String NSE_   = "138.1.%s.NSE";
  private static final String KRX_   = "138.1.%s.KRX.%s"; // e.g. 141.1.A012330.KRX.012330, 141.1.A005935.KRX.005935
  private static final String TAI    = "144.1.%s.TAI";
  private static final String IDX_   = "319.1.ID-%s.IDX.%s";
  private static final String KLS    = "135.1.%s.KLS";
  private static final String BKK    = "145.1.%s.BKK";
  private static final String ASX_   = "146.1.%s.ASX";
  private static final String NZE    = "147.1.%s.NZE";
  private static final String TAE    = "292.1.IS-%s.TAE.%s";
  private static final String BSP    = "56.1.%s.BSP";
  private static final String BUE    = "237.1.%s.BUE";
  private static final String SGO    = "233.1.%s.SGO";
  private static final String MEX    = "50.1.%s.MEX";

  static final Logger         logger = LoggerFactory.getLogger(MSNMoney.class);

  public MSNMoney() {
    super(MSNMoney.class);

    // supported markets
    exchanges.put(NYSE, NYS);
    exchanges.put(NASDAQ, NAS);
    exchanges.put(NYSEARCA, ARCX);
    exchanges.put(TSX, TSE_);
    exchanges.put(LSE, LON);
    exchanges.put(FWB, FRA);
    exchanges.put(PAR, PAR_);
    exchanges.put(AMS, AMS_);
    exchanges.put(SWX, SWX_);
    exchanges.put(MIB, MIL);
    exchanges.put(BM, MCE);
    exchanges.put(WB, WBO);
    exchanges.put(ATHEX, ATH);
    exchanges.put(OSLO, OSL);
    exchanges.put(SB, STO);
    exchanges.put(KFB, CSE);
    exchanges.put(MOEX, MIC);
    exchanges.put(GPW, WAR);
    exchanges.put(SGX, SES);
    exchanges.put(HKSE, HKG_);
    exchanges.put(SSE, SHG);
    exchanges.put(SZSE, SHE);
    exchanges.put(TSE, TKS);
    exchanges.put(BSE, BOM);
    exchanges.put(NSE, NSE_);
    exchanges.put(KRX, KRX_);
    exchanges.put(TWSE, TAI);
    exchanges.put(IDX, IDX_);
    exchanges.put(MYX, KLS);
    exchanges.put(SET, BKK);
    exchanges.put(ASX, ASX_);
    exchanges.put(NZX, NZE);
    exchanges.put(TASE, TAE);
    exchanges.put(BOVESPA, BSP);
    exchanges.put(BCBA, BUE);
    exchanges.put(BCS, SGO);
    exchanges.put(BMV, MEX);

    // MSN Money API
    // e.g.
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=max&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=5y&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=1m&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    // http://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=126.1.WMT.NYS&chartType=5d&isEOD=False&lang=en-US&isCS=true&isVol=true&callback=document.chartResponseHandler
    //
    // Notes:
    // 1. incoming data is in JSON format
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
