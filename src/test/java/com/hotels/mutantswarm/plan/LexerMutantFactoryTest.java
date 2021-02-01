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
package com.hotels.mutantswarm.plan;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.LexerMutatorStore;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.plan.gene.LexerGene;
import com.hotels.mutantswarm.plan.gene.LexerLocus;

@ExtendWith(MockitoExtension.class)
public class LexerMutantFactoryTest {

  @Mock
  private MutantSwarmStatement statement;
  @Mock
  private CommonToken token1, token2, token3;
  @Mock
  private Mutator mutator1, mutator2;
  @Mock
  private LexerMutatorStore store;

  private LexerMutantFactory mutantFactory;

  @BeforeEach
  public void initialiseMocks() {
    mutantFactory = new LexerMutantFactory(store);
  }

  @Test
  public void oneMutatorForSingleGene() {
    List<CommonToken> tokens = singletonList(token1);
    when(statement.getTokens()).thenReturn(tokens);
    when(store.getMutatorsFor(0, tokens)).thenReturn(asList(mutator1));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(1));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    LexerGene gene = (LexerGene) mutant.getGene();
    assertThat(gene.getTokens(), is(tokens));

    LexerLocus locus = (LexerLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getIndexes(), is(singletonList(0)));
  }

  @Test
  public void noMutatorsForAStatement() {
    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(0));
  }

  @Test
  public void multipleMutatorsForASingleGene() {
    List<CommonToken> tokens = singletonList(token1);
    when(statement.getTokens()).thenReturn(tokens);
    when(store.getMutatorsFor(0, tokens)).thenReturn(asList(mutator1, mutator2));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(2));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    LexerGene gene = (LexerGene) mutant.getGene();
    assertThat(gene.getTokens(), is(tokens));

    LexerLocus locus = (LexerLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getIndexes(), is(singletonList(0)));

    mutant = mutants.get(1);
    assertThat(mutant.getMutator(), is(mutator2));

    gene = (LexerGene) mutant.getGene();
    assertThat(gene.getTokens(), is(tokens));

    locus = (LexerLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getIndexes(), is(singletonList(0)));
  }

  @Test
  public void oneMutatorForMultipleGenes() {
    List<CommonToken> tokens = asList(token1, token2, token3);
    when(statement.getTokens()).thenReturn(tokens);
    when(store.getMutatorsFor(0, tokens)).thenReturn(asList(mutator1));
    when(store.getMutatorsFor(1, tokens)).thenReturn(Collections.emptyList());
    when(store.getMutatorsFor(2, tokens)).thenReturn(asList(mutator2));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(2));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    LexerGene gene = (LexerGene) mutant.getGene();
    assertThat(gene.getTokens(), is(asList(token1)));

    LexerLocus locus = (LexerLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getIndexes(), is(singletonList(0)));

    mutant = mutants.get(1);
    assertThat(mutant.getMutator(), is(mutator2));

    gene = (LexerGene) mutant.getGene();
    assertThat(gene.getTokens(), is(asList(token3)));

    locus = (LexerLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getIndexes(), is(singletonList(2)));
  }

}
