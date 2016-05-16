/**
 * Exchanges.java  v0.11  13 November 2014 10:38:47 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supported exchanges. The foreign exchange market ("FX") is included here as a
 * pseudo-exchange.
 *
 * @author Daniel Kuan
 * @version 0.11
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
  PAR,
  /** NYSE Euronext Amsterdam */
  AMS,
  /** Bourse de Bruxelles, Brussels Stock Exchange */
  BB,
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
  /** Giełda Papierów Wartościowych w Warszawie, Warsaw Stock Exchange */
  GPW,
  /** Budapesti Értéktőzsde, Budapest Stock Exchange */
  BET,
  /** Burza cenných papírů Praha, Prague Stock Exchange */
  PX,
  /** Bursa de Valori București, Bucharest Stock Exchange */
  BVB,

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

  // Oceania
  /** Australian Securities Exchange */
  ASX,
  /** New Zealand Exchange */
  NZX,

  // Middle East
  /** Tel Aviv Stock Exchange */
  TASE,

  // Africa
  /** Johannesburg Stock Exchange */
  JSE,
  /** Egyptian Exchange */
  EGX,

  // South America
  /** Bolsa de Valores, Mercadorias & Futuros de São Paulo, Sao Paolo Stock Exchange */
  BOVESPA,
  /** Bolsa de Comercio de Buenos Aires, Buenos Aires Stock Exchange */
  BCBA,
  /** Bolsa de Comercio de Santiago, Santiago Stock Exchange */
  BCS,
  /** Bolsa Mexicana de Valores, Mexico Stock Exchange */
  BMV,

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
