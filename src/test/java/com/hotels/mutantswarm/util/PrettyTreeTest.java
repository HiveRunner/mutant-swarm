package com.hotels.mutantswarm.util;

import static org.junit.Assert.assertTrue;

import org.antlr.runtime.CommonToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrettyTreeTest {
  
  @Test
  public void checkHasLocation() {
    CommonToken token = new CommonToken(null, 3, 4, 2, 3);
    boolean result = PrettyTree.hasLocation(token);
    assertTrue(result);
  }
  
}
