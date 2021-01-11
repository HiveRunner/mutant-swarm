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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.SwarmResults.SwarmResultsBuilder;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;
import com.hotels.mutantswarm.plan.gene.Gene;

@RunWith(MockitoJUnitRunner.class)
public class SwarmResultsTest {

  @Mock
  private Swarm swarm;
  @Mock
  private Mutant mutant1, mutant2;
  @Mock
  private Mutation mutation1, mutation2;
  @Mock
  private Gene gene1, gene2;
  @Mock
  private MutantSwarmScript script1;
  @Mock
  private MutantSwarmStatement statement1;

  @Test
  public void testBuild() {
    String suiteName = "testSuite";

    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    SwarmResults swarmResults = builder.build();

    assertThat(swarmResults.getSuiteName(), is(suiteName));
  }

  @Test
  public void testAddOutcome() {
    String suiteName = "testSuite";
    MutantState state = MutantState.KILLED;

    when(mutant1.getGene()).thenReturn(gene1);
    when(gene1.getScriptIndex()).thenReturn(0);
    when(gene1.getStatementIndex()).thenReturn(0);

    when(script1.getIndex()).thenReturn(0);
    when(statement1.getIndex()).thenReturn(0);

    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    builder.addTestOutcome(suiteName, mutant1, mutation1, state);
    SwarmResults swarmResults = builder.build();

    assertTrue(swarmResults.hasOutcomesFor(script1, statement1));

    List<Outcome> outcomes = swarmResults.outcomesFor(script1, statement1);

    assertThat(outcomes.size(), is(1));
    assertThat(outcomes.get(0).getState(), is(MutantState.KILLED));
  }

  @Test
  public void testMultipleOutcomes() {
    String suiteName = "testSuite";
    MutantState state1 = MutantState.KILLED;
    MutantState state2 = MutantState.SURVIVED;

    when(mutant1.getGene()).thenReturn(gene1);
    when(gene1.getScriptIndex()).thenReturn(0);
    when(gene1.getStatementIndex()).thenReturn(0);

    when(mutant2.getGene()).thenReturn(gene2);
    when(gene2.getScriptIndex()).thenReturn(0);
    when(gene2.getStatementIndex()).thenReturn(0);

    when(script1.getIndex()).thenReturn(0);
    when(statement1.getIndex()).thenReturn(0);

    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    builder.addTestOutcome(suiteName, mutant1, mutation1, state1);
    builder.addTestOutcome(suiteName, mutant2, mutation2, state2);

    SwarmResults swarmResults = builder.build();
    assertTrue(swarmResults.hasOutcomesFor(script1, statement1));

    List<Outcome> outcomes = swarmResults.outcomesFor(script1, statement1);
    Collections.sort(outcomes, new Comparator<Outcome>() {
      @Override
      public int compare(Outcome o1, Outcome o2) {
        return o1.getState().compareTo(o2.getState());
      }
    });

    assertThat(outcomes.size(), is(2));
    assertThat(outcomes.get(0).getState(), is(MutantState.SURVIVED));
    assertThat(outcomes.get(1).getState(), is(MutantState.KILLED));
  }

  @Test
  public void testMultipleStatesForSingleMutant() {

    String suiteName = "testSuite";
    MutantState state1 = MutantState.KILLED;
    MutantState state2 = MutantState.SURVIVED;

    when(mutant1.getGene()).thenReturn(gene1);
    when(gene1.getScriptIndex()).thenReturn(0);
    when(gene1.getStatementIndex()).thenReturn(0);

    when(script1.getIndex()).thenReturn(0);
    when(statement1.getIndex()).thenReturn(0);

    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    builder.addTestOutcome(suiteName, mutant1, mutation1, state1);
    builder.addTestOutcome(suiteName, mutant1, mutation1, state2);
    SwarmResults swarmResults = builder.build();

    assertTrue(swarmResults.hasOutcomesFor(script1, statement1));

    List<Outcome> outcomes = swarmResults.outcomesFor(script1, statement1);

    assertThat(outcomes.size(), is(1));
    assertThat(outcomes.get(0).getState(), is(MutantState.KILLED));
  }

  @Test
  public void testNoOutcomes() {
    String suiteName = "testSuite";

    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    SwarmResults swarmResults = builder.build();

    assertFalse(swarmResults.hasOutcomesFor(script1, statement1));
  }

  @Test
  public void testOutcomesListNull() {
    String suiteName = "testSuite";
    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    SwarmResults swarmResults = builder.build();

    List<Outcome> outcomes = swarmResults.outcomesFor(script1, statement1);
    assertThat(outcomes, is(Collections.EMPTY_LIST));
  }

  @Test
  public void equalsNull() {
    String suiteName = "testSuite";
    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    SwarmResults swarmResults = builder.build();
    assertThat(swarmResults.equals(null), is(false));
  }
  
  @Test
  public void equalsSame() {
    String suiteName = "testSuite";
    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    SwarmResults swarmResults = builder.build();
    SwarmResults swarmResults2 = builder.build();
    assertThat(swarmResults.equals(swarmResults2), is(true));
    assertThat(swarmResults.hashCode(), is(swarmResults2.hashCode()));
  }
  
  @Test
  public void checkToString(){
    String suiteName = "testSuite";
    MutantState state = MutantState.KILLED;

    when(mutant1.getGene()).thenReturn(gene1);
    when(gene1.getScriptIndex()).thenReturn(0);
    when(gene1.getStatementIndex()).thenReturn(0);

    SwarmResultsBuilder builder = new SwarmResultsBuilder(swarm, suiteName);
    builder.addTestOutcome(suiteName, mutant1, mutation1, state);
    
    SwarmResults swarmResults = builder.build();
    assertThat(swarmResults.toString(),is("SwarmResults [swarm=swarm, suiteName=testSuite, outcomesByScriptIndex={Key [scriptIndex=0, statementIndex=0]=[Outcome [mutant=mutant1, mutation=mutation1, state=KILLED, testOutcomes=[TestOutcome [testName=testSuite, mutant=mutant1, mutation=mutation1, state=KILLED]]]]}]"));
  }
  
}
