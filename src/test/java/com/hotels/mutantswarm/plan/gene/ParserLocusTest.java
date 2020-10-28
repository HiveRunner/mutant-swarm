package com.hotels.mutantswarm.plan.gene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParserLocusTest {
  
  private ParserLocus parserLocus;
  private ParserLocus parserLocus2;

  @Test
  public void equalsSame() {
    parserLocus = new ParserLocus(0,2,4);
    parserLocus2 = new ParserLocus(0,2,4);
    boolean result = parserLocus.equals(parserLocus2);
    assertTrue(result);
  }
  
  @Test
  public void checkGetNodeIndex() {
    parserLocus = new ParserLocus(0,2,4);
    int result = parserLocus.getNodeIndex();
    assertEquals(result,4);
  }
  
  @Test
  public void checkToString() {
    parserLocus = new ParserLocus(0,2,4);
    String result = parserLocus.toString();
    assertEquals(result,"ParserLocus [scriptIndex=0, statementIndex=2, nodeIndex=4]");
  }
}
