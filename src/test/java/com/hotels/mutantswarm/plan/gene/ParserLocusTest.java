/*
 * Copyright (C) 2018-2021 Expedia, Inc.
 * Copyright (C) 2021 The HiveRunner Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.mutantswarm.plan.gene;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class ParserLocusTest {
  
  private ParserLocus parserLocus = new ParserLocus(0,2,4);
  private ParserLocus parserLocus2= new ParserLocus(0,2,4);;

  @Test
  public void equalSame() {
    boolean result = parserLocus.equals(parserLocus2);
    assertThat(result, is(true));
  }
  
  @Test
  public void checkGetNodeIndex() {
    int result = parserLocus.getNodeIndex();
    assertThat(result,is(4));
  }
  
  @Test
  public void checkToString() {
    String result = parserLocus.toString();
    assertThat(result,is("ParserLocus [scriptIndex=0, statementIndex=2, nodeIndex=4]"));
  }
}
