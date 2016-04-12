/**
 * FrequenciesTest.java v0.1  8 April 2014 10:34:28 PM
 *
 * Copyright © 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>Frequencies</code>.
 *
 * @author Daniel Kuan
 * @version
 */
public class FrequenciesTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void frequencyIsOneLowerCaseCharacter() throws Exception {
    for (final Frequencies f : Frequencies.values()) {
      final String fs = f.toString();
      assertEquals(Character.valueOf(Character.toLowerCase(fs.charAt(0))),
                   Character.valueOf(f.frequency()));
    }
  }

  @Test
  public void instantiateUpperCaseFrequencies() throws Exception {
    // upper case
    for (final Frequencies f : Frequencies.values()) {
      final String fsuc = f.toString().toUpperCase();
      assertEquals(Frequencies.valueOf(fsuc), f);
    }
  }

  @Test
  public void cannotInstantiateDummyFrequencies() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    Frequencies.valueOf("dummy");
  }

  @Test
  public void cannotInstantiateLowerCaseFrequencies() throws Exception {
    // lower case
    for (final Frequencies f : Frequencies.values()) {
      final String fslc = f.toString().toLowerCase();
      try {
        Frequencies.valueOf(fslc);
        fail("illegal frequency: " + fslc);
      }
      catch (final IllegalArgumentException iaE) {
        assertNotNull(iaE);
      }
    }
  }

}
