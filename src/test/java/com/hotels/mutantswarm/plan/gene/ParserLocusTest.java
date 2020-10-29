/**
 * Copyright (C) 2018-2020 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
