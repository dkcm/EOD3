/**
 * MSNMoneyTest.java v0.1 13 May 2016 7:14:12 pm
 *
 * Copyright © 2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>MSNMoney</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.1
 */
class MSNMoneyTest extends SourceTest {

  private static final String BASE = baseURL(MorningstarTest.class);

  // Exchange-related constants
  private static final String NYS  = "126.1.%s.NYS";
  private static final String NAS  = "126.1.%s.NAS";
  private static final String ARCX = "126.1.%s.ARCX";
  private static final String TSE_ = "127.1.%s.TSE";
  private static final String LON  = "151.1.%s.LON";
  private static final String FRA  = "200.1.%s.FRA";
  private static final String PAR_ = "160.1.%s.PAR";
  private static final String AMS_ = "202.1.%s.AMS";
  private static final String SWX_ = "185.1.%s.SWX";
  private static final String MIL  = "223.1.%s.MIL";
  private static final String MCE  = "199.1.%s.MCE";
  private static final String WBO  = "194.1.%s.WBO";
  private static final String ATH  = "212.1.%s.ATH";
  private static final String OSL  = "174.1.%s.OSL";
  private static final String STO  = "170.1.%s.STO";
  private static final String CSE  = "172.1.%s.CSE";
  private static final String MIC  = "231.1.%s.MIC";
  private static final String WAR  = "42.1.%s.WAR";
  private static final String SES  = "143.1.%s.SES";
  private static final String HKG_ = "134.1.%s.HKG.%s";
  private static final String SHG  = "136.1.%s.SHG";
  private static final String SHE  = "202.1.%s.SHE";
  private static final String TKS  = "133.1.%s.TKS";
  private static final String BOM  = "139.1.%s.BOM";
  private static final String NSE_ = "138.1.%s.NSE";
  private static final String KRX_ = "138.1.%s.KRX.%s";
  private static final String TAI  = "144.1.%s.TAI";
  private static final String IDX_ = "319.1.ID-%s.IDX.%s";
  private static final String KLS  = "135.1.%s.KLS";
  private static final String BKK  = "145.1.%s.BKK";
  private static final String ASX_ = "146.1.%s.ASX";
  private static final String NZE  = "147.1.%s.NZE";
  private static final String TAE  = "292.1.IS-%s.TAE.%s";
  private static final String BSP  = "56.1.%s.BSP";
  private static final String BUE  = "237.1.%s.BUE";
  private static final String SGO  = "233.1.%s.SGO";
  private static final String MEX  = "50.1.%s.MEX";

  public MSNMoneyTest() throws IOException {
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

    originalLines.addAll(Files.readAllLines(new File(DIRECTORY, getClass().getSimpleName() + JSON).toPath()));

    transformedLines.addAll(Arrays.asList("INTC,20151207,54.77,54.88,53.91,54.40,15077326",
                                          "INTC,20151204,53.66,55.33,53.52,55.09,21463021",
                                          "INTC,20151203,54.42,54.60,53.35,53.51,0",
                                          "INTC,20151202,54.99,55.02,54.03,54.14,0",
                                          "INTC,20151201,54.40,54.91,54.25,54.88,0",
                                          "INTC,20151130,54.34,54.46,53.97,54.09,14603058"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return new URL(BASE + symbol);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return new URL(BASE + String.format(exchanges.get(exchange), symbol));
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol);
  }


  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol);
  }


  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, exchange);
  }


  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, exchange);
  }

}
