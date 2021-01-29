/**
 * Copyright (C) 2018-2021 Expedia, Inc.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LexerLocusTest {

  private LexerLocus lexerLocus;
  private LexerLocus lexerLocus2;

  @BeforeEach
  public void setUp() {
    List<Integer> indexes = Arrays.asList(1,2);
    lexerLocus = new LexerLocus(2,3,indexes);
    lexerLocus2 = new LexerLocus(2,3,indexes);
  }

  @Test
  public void checkToString() {
    String result = lexerLocus.toString();
    assertThat(result,is("LexerLocus [scriptIndex=2, statementIndex=3, indexes=[1, 2]]"));
  }

  @Test
  public void equalsSame() {
    boolean result = lexerLocus.equals(lexerLocus2);
    assertThat(result, is(true));
  }

}
