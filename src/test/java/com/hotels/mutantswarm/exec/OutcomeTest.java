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
package com.hotels.mutantswarm.exec;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;

@ExtendWith(MockitoExtension.class)
public class OutcomeTest {

  @Mock
  private Mutant mutant;
  @Mock
  private Mutation mutation, mutation2;
  @Mock
  private TestOutcome testOutcome1, testOutcome2, testOutcome3;

  private List<TestOutcome> testOutcomes;

  @BeforeEach
  public void setUp() {
    testOutcomes = asList(testOutcome1, testOutcome2, testOutcome3);
  }

  @Test
  public void testStateKilled() {
    when(testOutcome1.getState()).thenReturn(MutantState.SURVIVED);
    when(testOutcome2.getState()).thenReturn(MutantState.KILLED);
    when(testOutcome3.getState()).thenReturn(MutantState.SURVIVED);

    Outcome outcome = new Outcome(mutant, mutation, testOutcomes);
    assertThat(outcome.getState(), is(MutantState.KILLED));
  }

  @Test
  public void testStateSurvived() {
    when(testOutcome1.getState()).thenReturn(MutantState.SURVIVED);
    when(testOutcome2.getState()).thenReturn(MutantState.SURVIVED);
    when(testOutcome3.getState()).thenReturn(MutantState.SURVIVED);

    Outcome outcome = new Outcome(mutant, mutation, testOutcomes);
    assertThat(outcome.getState(), is(MutantState.SURVIVED));
  }

  @Test
  public void checkToString() {
    Outcome outcome = new Outcome(mutant, mutation, testOutcomes);
    String result = outcome.toString();
    assertThat(result, is(
        "Outcome [mutant=mutant, mutation=mutation, state=SURVIVED, testOutcomes=[testOutcome1, testOutcome2, testOutcome3]]"));
  }

  @Test
  public void equalsNull() {
    Outcome outcome = new Outcome(mutant, mutation, testOutcomes);
    assertThat(outcome.equals(null), is(false));
  }

  @Test
  public void equalsMutantNull() {
    Outcome outcome = new Outcome(null, mutation, testOutcomes);
    Outcome outcome2 = new Outcome(mutant, mutation, testOutcomes);
    assertThat(outcome.equals(outcome2), is(false));
  }

  @Test
  public void equalsMutationNull() {
    Outcome outcome = new Outcome(mutant, null, testOutcomes);
    Outcome outcome2 = new Outcome(mutant, mutation, testOutcomes);
    assertThat(outcome.equals(outcome2), is(false));
  }

  @Test
  public void equalSame() {
    Outcome outcome = new Outcome(mutant, mutation, testOutcomes);
    Outcome outcome2 = new Outcome(mutant, mutation, testOutcomes);
    assertThat(outcome.equals(outcome2), is(true));
    assertThat(outcome.hashCode(), is(outcome2.hashCode()));
  }

  @Test
  public void equalsDifferentMutation() {
    Outcome outcome = new Outcome(mutant, mutation, testOutcomes);
    Outcome outcome2 = new Outcome(mutant, mutation2, testOutcomes);
    assertThat(outcome.equals(outcome2), is(false));
  }

}
