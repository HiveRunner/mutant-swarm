/**
 * Copyright (C) 2018-2019 Expedia, Inc.
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
package com.hotels.mutantswarm.report;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.Outcome;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.mutate.Splice;

@RunWith(MockitoJUnitRunner.class)
public class OutcomeUtilTest {

  @Mock
  private Outcome outcome1, outcome2, outcome3, outcome4;
  @Mock
  private Mutation mutation1, mutation2, mutation3, mutation4;
  @Mock
  private Splice splice1, splice2, splice3, splice4;

  @Test
  public void outcomesByStartPosition() {
    when(outcome1.getMutation()).thenReturn(mutation1);
    when(mutation1.getSplice()).thenReturn(splice1);
    when(outcome1.getMutationStartIndex()).thenReturn(50);

    when(outcome2.getMutation()).thenReturn(mutation2);
    when(mutation2.getSplice()).thenReturn(splice2);
    when(outcome2.getMutationStartIndex()).thenReturn(1);

    when(outcome3.getMutation()).thenReturn(mutation3);
    when(mutation3.getSplice()).thenReturn(splice3);
    when(outcome3.getMutationStartIndex()).thenReturn(100);

    when(outcome4.getMutation()).thenReturn(mutation4);
    when(mutation4.getSplice()).thenReturn(splice4);
    when(outcome4.getMutationStartIndex()).thenReturn(50);

    SortedMap<Integer, List<Outcome>> actual = OutcomeUtil
        .outcomesByStartIndex(Arrays.asList(outcome1, outcome2, outcome3, outcome4));
    Iterator<Entry<Integer, List<Outcome>>> entries = actual.entrySet().iterator();

    Entry<Integer, List<Outcome>> entry = entries.next();
    assertThat(entry.getKey(), is(1));
    assertThat(entry.getValue(), is(asList(outcome2)));

    entry = entries.next();
    assertThat(entry.getKey(), is(50));
    assertThat(entry.getValue(), is(asList(outcome1, outcome4)));

    entry = entries.next();
    assertThat(entry.getKey(), is(100));
    assertThat(entry.getValue(), is(asList(outcome3)));

    assertThat(entries.hasNext(), is(false));
  }
}
