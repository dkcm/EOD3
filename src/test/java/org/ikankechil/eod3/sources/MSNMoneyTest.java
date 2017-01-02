/**
 * MSNMoneyTest.java  v0.2  13 May 2016 7:14:12 pm
 *
 * Copyright © 2016-2017 Daniel Kuan.  All rights reserved.
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
 *
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public class MSNMoneyTest extends SourceTest {

  private static final String    BASE             = baseURL(MSNMoneyTest.class);

  // Exchange-related constants
  private static final String    NYS              = "126.1.%s.NYS";
  private static final String    NAS              = "126.1.%s.NAS";
  private static final String    ASE              = "126.1.%s.ASE";
  private static final String    ARCX             = "126.1.%s.ARCX";
  private static final String    TSE_             = "127.1.%s.TSE";
  private static final String    LON              = "151.1.%s.LON";
  private static final String    DUB              = "190.1.%s.DUB";
  private static final String    FRA              = "200.1.%s.FRA";
  private static final String    PAR_             = "160.1.%s.PAR";
  private static final String    AMS              = "202.1.%s.AMS";
  private static final String    BRU              = "207.1.%s.BRU";
  private static final String    SWX_             = "185.1.%s.SWX";
  private static final String    MIL              = "223.1.%s.MIL";
  private static final String    MCE              = "199.1.%s.MCE";
  private static final String    LIS              = "195.1.%s.LIS";
  private static final String    WBO              = "194.1.%s.WBO";
  private static final String    ATH              = "212.1.%s.ATH";
  private static final String    IST              = "432.1.TR-%s|SLA|E.IST.%s";
  private static final String    OSL              = "174.1.%s.OSL";
  private static final String    STO              = "170.1.%s.STO";
  private static final String    HEL              = "176.1.%s.HEL";
  private static final String    CSE              = "172.1.%s.CSE";
  private static final String    LTS              = "152.1.%s.LTS";
  private static final String    MIC              = "231.1.%s.MIC";
  private static final String    RIS              = "422.1.LA-%s.RIS.%s";
  private static final String    TAL              = "418.1.ES-%s.TAL.%s";
  private static final String    LIT              = "423.1.LI-%s.LIT.%s";
  private static final String    WAR              = "42.1.%s.WAR";
  private static final String    SES              = "143.1.%s.SES";
  private static final String    HKG_             = "134.1.%s.HKG.%s";
  private static final String    SHG              = "136.1.%s.SHG";
  private static final String    SHE              = "202.1.%s.SHE";
  private static final String    TKS              = "133.1.%s.TKS";
  private static final String    BOM              = "139.1.%s.BOM";
  private static final String    NSE_             = "138.1.%s.NSE";
  private static final String    KRX_             = "141.1.A%s.KRX.%s";
  private static final String    TAI              = "144.1.%s.TAI";
  private static final String    IDX_             = "319.1.ID-%s.IDX.%s";
  private static final String    KLS              = "135.1.%s.KLS";
  private static final String    BKK              = "145.1.%s.BKK";
  private static final String    PHS              = "142.1.%s.PHS";
  private static final String    ASX_             = "146.1.%s.ASX";
  private static final String    NZE              = "147.1.%s.NZE";
  private static final String    TAE              = "292.1.IS-%s.TAE.%s";
  private static final String    JSE_             = "193.1.%s.JSE";
  private static final String    BSP              = "56.1.%s.BSP";
  private static final String    BUE              = "237.1.%s.BUE";
  private static final String    SGO              = "233.1.%s.SGO";
  private static final String    MEX              = "50.1.%s.MEX";
  private static final String    BOG              = "390.1.CO-%s.BOG.%s";
  private static final String    LIM              = "398.1.PE-%s.LIM.%s";

  private static final Exchanges DEFAULT_EXCHANGE = NYSE;

  public MSNMoneyTest() throws IOException {
    exchanges.put(NYSE, NYS);
    exchanges.put(NASDAQ, NAS);
    exchanges.put(AMEX, ASE);
    exchanges.put(ARCA, ARCX);
    exchanges.put(TSX, TSE_);
    exchanges.put(LSE, LON);
    exchanges.put(ISE, DUB);
    exchanges.put(FWB, FRA);
    exchanges.put(PAR, PAR_);
    exchanges.put(AEX, AMS);
    exchanges.put(BB, BRU);
    exchanges.put(SWX, SWX_);
    exchanges.put(MIB, MIL);
    exchanges.put(BM, MCE);
    exchanges.put(BVLP, LIS);
    exchanges.put(WB, WBO);
    exchanges.put(ATHEX, ATH);
    exchanges.put(BIST, IST);   // e.g. 432.1.TR-ACSEL|SLA|E.IST.ACSEL, 432.1.TR-AKBNK%7CSLA%7CE.IST.AKBNK
    exchanges.put(OSLO, OSL);
    exchanges.put(SB, STO);
    exchanges.put(HEX, HEL);
    exchanges.put(KFB, CSE);
    exchanges.put(ICEX, LTS);
    exchanges.put(MOEX, MIC);
    exchanges.put(RSE, RIS);
    exchanges.put(TALSE, TAL);  // e.g. 418.1.ES-NCN1T.TAL.NCN1T
    exchanges.put(VSE, LIT);    // e.g. 423.1.LI-GUB1L.LIT.GUB1L
    exchanges.put(GPW, WAR);
    exchanges.put(SGX, SES);
    exchanges.put(HKEX, HKG_);  // e.g. CK Hutchison 134.1.1.HKG.00001, 134.1.2328.HKG.02328
    exchanges.put(SSE, SHG);
    exchanges.put(SZSE, SHE);
    exchanges.put(TSE, TKS);
    exchanges.put(BSE, BOM);
    exchanges.put(NSE, NSE_);
    exchanges.put(KRX, KRX_);   // e.g. 141.1.A012330.KRX.012330, 141.1.A005935.KRX.005935
    exchanges.put(TWSE, TAI);
    exchanges.put(IDX, IDX_);
    exchanges.put(MYX, KLS);
    exchanges.put(SET, BKK);
    exchanges.put(PSE, PHS);
    exchanges.put(ASX, ASX_);
    exchanges.put(NZX, NZE);
    exchanges.put(TASE, TAE);
    exchanges.put(JSE, JSE_);
    exchanges.put(BOVESPA, BSP);
    exchanges.put(BCBA, BUE);
    exchanges.put(BCS, SGO);
    exchanges.put(BMV, MEX);
    exchanges.put(BVC, BOG);   // e.g. 390.1.CO-PFBCOLOM.BOG.PFBCOLOM
    exchanges.put(BVL, LIM);   // e.g. 398.1.PE-ALICORC1.LIM.ALICORC1

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
    return expectedURL(symbol, DEFAULT_EXCHANGE);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    final String format = exchanges.get((exchanges.containsKey(exchange)) ? exchange
                                                                          : DEFAULT_EXCHANGE);
    return new URL(BASE + String.format(format, symbol, symbol));
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
