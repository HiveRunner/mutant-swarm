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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LexerLocusTest {

  private LexerLocus lexerLocus;
  private LexerLocus lexerLocus2;

  @Before
  public void setUpMocks() {
    List<Integer> indexes = new ArrayList<Integer>();
    indexes.add(1);
    indexes.add(2);
    lexerLocus = new LexerLocus(2,3,indexes);
    lexerLocus2 = new LexerLocus(2,3,indexes);
  }

  @Test
  public void checkToString() {
    String result = lexerLocus.toString();
    assertEquals(result,"LexerLocus [scriptIndex=2, statementIndex=3, indexes=[1, 2]]");
  }

  @Test
  public void equalsSame() {
    boolean result = lexerLocus.equals(lexerLocus2);
    assertTrue(result);
  }


}
