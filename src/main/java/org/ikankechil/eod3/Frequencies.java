/**
 * Frequencies.java	v0.1	2 April 2014 12:32:09 AM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

/**
 * Type description goes here.
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public enum Frequencies {
  // Frequency-related constants
  DAILY('d'), WEEKLY('w'), MONTHLY('m');

  private final char frequency;

  Frequencies(final char frequency) {
    this.frequency = frequency;
  }

  public char frequency() {
    return frequency;
  }

}
