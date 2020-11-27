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
package com.hotels.mutantswarm.exec;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.gene.Gene;

@RunWith(MockitoJUnitRunner.class)
public class TestOutcomeTest {
  
  @Mock
  private Gene gene;
  private final Mutator mutator = null;

  private String testName = "This is a test name";
  private Mutant mutant = new Mutant(gene,mutator);
  private Mutation mutation = null;
  private MutantState state = MutantState.SURVIVED;
  private TestOutcome testOutcome = new TestOutcome(testName,mutant,mutation,state);
  private TestOutcome testOutcome2 = new TestOutcome(testName,mutant,mutation,state);

  @Test
  public void equalSame() {
    assertThat(testOutcome.equals(testOutcome2), is(true));
    assertThat(testOutcome.hashCode(), is(testOutcome2.hashCode()));
  }
  
  @Test
  public void checkGetMutant() {
    Mutant result = testOutcome.getMutant();
    assertThat(result,is(mutant));
  }

}
