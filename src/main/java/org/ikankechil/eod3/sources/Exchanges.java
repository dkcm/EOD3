/**
 * Exchanges.java  v0.14  13 November 2014 10:38:47 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supported exchanges. The foreign exchange market ("FX") is included here as a
 * pseudo-exchange.
 * <p>
 * http://money.visualcapitalist.com/all-of-the-worlds-stock-exchanges-by-size/
 *
 * @author Daniel Kuan
 * @version 0.14
 */
public enum Exchanges {

  // North America
  /** New York Stock Exchange */
  NYSE,
  /** National Association of Securities Dealers Automated Quotations */
  NASDAQ,
  /** American Stock Exchange */
  AMEX,
  /** Archipelago Exchange */
  NYSEARCA,
  /** Toronto Stock Exchange */
  TSX,

  // Europe
  /** London Stock Exchange */
  LSE,
  /** Stocmhalartán na hÉireann, Irish Stock Exchange */
  ISE,
  /** Deutsche Börse, Frankfurt Stock Exchange */
  FWB,
  /** Paris Stock Exchange / NYSE Euronext Paris */
  PAR,  // TODO to rename as BDP
  /** NYSE Euronext Amsterdam */
  AMS,  // TODO to rename as AEX
  /** Bourse de Bruxelles, Brussels Stock Exchange */
  BB,
  /** Bourse de Luxembourg, Luxembourg Stock Exchange */
  LUX,
  /** SIX Swiss Exchange */
  SWX,
  /** Borsa Italiana */
  MIB,
  /** Bolsa de Madrid, Madrid Stock Exchange */
  BM,
  /** Bolsa de Valores de Lisboa e Porto, Lisbon Stock Exchange */
  BVLP,
  /** Wiener Börse, Vienna Stock Exchange */
  WB,
  /** Athens Stock Exchange */
  ATHEX,
  /** Borsa İstanbul */
  BIST,
  /** Oslo Børs */
  OSLO,
  /** Stockholmsbörsen, Stockholm Stock Exchange */
  SB,
  /** Helsingin Pörssi, Helsinki Stock Exchange */
  HEX,
  /** Københavns Fondsbørs, Copenhagen Stock Exchange */
  KFB,
  /** Kauphöll Íslands, Iceland Stock Exchange */
  ICEX,
  /** Moskovskaya Birzha, Moscow Exchange */
  MOEX,
  /** Українська біржа, Ukrainian Exchange */
  UX,
  /** Riga Stock Exchange */
  RSE,
  /** Tallinn Stock Exchange */
  TALSE,
  /** Vilnius Stock Exchange */
  VSE,
  /** Giełda Papierów Wartościowych w Warszawie, Warsaw Stock Exchange */
  GPW,
  /** Budapesti Értéktőzsde, Budapest Stock Exchange */
  BET,
  /** Burza cenných papírů Praha, Prague Stock Exchange */
  PX,
  /** Bursa de Valori București, Bucharest Stock Exchange */
  BVB,
  /** Ljubljanska borza, Ljubljana Stock Exchange */
  LJSE,

  // Asia-Pacific
  /** Singapore Stock Exchange */
  SGX,
  /** Hong Kong Stock Exchange */
  HKSE,
  /** Shanghai Stock Exchange */
  SSE,
  /** Shenzhen Stock Exchange */
  SZSE,
  /** Tokyo Stock Exchange */
  TSE,
  /** Osaka Securities Exchange */
  OSE,
  /** Bombay Stock Exchange */
  BSE,
  /** National Stock Exchange of India */
  NSE,
  /** Korea Exchange */
  KRX,
  /** Taiwan Stock Exchange */
  TWSE,
  /** Bursa Efek Indonesia, Indonesia Stock Exchange */
  IDX,
  /** Bursa Malaysia */
  MYX,
  /** Stock Exchange of Thailand */
  SET,
  /** Philippine Stock Exchange */
  PSE,

  // Oceania
  /** Australian Securities Exchange */
  ASX,
  /** New Zealand Exchange */
  NZX,

  // Middle East
  /** Tel Aviv Stock Exchange */
  TASE,
  /** Tadawul, Saudi Stock Exchange */
  TADAWUL,
  /** Qatar Stock Exchange */
  QSE,
  /** Abu Dhabi Securities Exchange */
  ADX,
  /** Dubai Financial Market */
  DFM,
  /** Muscat Securities Market */
  MSM,
  /** Amman Stock Exchange */
  ASE,
  /** Bahrain Bourse */
  BHB,

  // Africa
  /** Johannesburg Stock Exchange */
  JSE,
  /** Egyptian Exchange */
  EGX,
  /** Nigerian Stock Exchange */
  NGSE,
  /** Bourse de Casablanca, Casablanca Stock Exchange */
  BC,

  // South America
  /** Bolsa de Valores, Mercadorias & Futuros de São Paulo, Sao Paolo Stock Exchange */
  BOVESPA,
  /** Bolsa de Comercio de Buenos Aires, Buenos Aires Stock Exchange */
  BCBA,
  /** Bolsa de Comercio de Santiago, Santiago Stock Exchange */
  BCS,
  /** Bolsa Mexicana de Valores, Mexico Stock Exchange */
  BMV,
  /** Bolsa de Valores de Colombia, Colombia Stock Exchange */
  BVC,
  /** Bolsa de Valores de Caracas, Caracas Stock Exchange */
  BVCA,
  /** Bolsa de Valores de Lima, Lima Stock Exchange */
  BVL,

  // Foreign Exchange
  /** Foreign exchange market (included here as a pseudo-exchange) */
  FX;

  private static final Logger logger = LoggerFactory.getLogger(Exchanges.class);

  public static final Exchanges toExchange(final String exchange) {
    Exchanges xchg;
    try {
      xchg = valueOf(exchange);
    }
    catch (final IllegalArgumentException iaE) {
      xchg = null;
      logger.warn("No such exchange: {}, returning null", exchange, iaE);
    }
    return xchg;
  }

}
