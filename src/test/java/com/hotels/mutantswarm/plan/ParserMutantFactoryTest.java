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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.mutate.ParserMutatorStore;
import com.hotels.mutantswarm.plan.gene.ParserGene;
import com.hotels.mutantswarm.plan.gene.ParserLocus;

@ExtendWith(MockitoExtension.class)
public class ParserMutantFactoryTest {

  @Mock
  private MutantSwarmStatement statement;
  @Mock
  private ASTNode tree;
  @Mock
  private ASTNode child1, child2;
  @Mock
  private Mutator mutator1, mutator2;
  @Mock
  private ParserMutatorStore store;

  private ParserMutantFactory mutantFactory;

  @BeforeEach
  public void initialiseMocks() {
    mutantFactory = new ParserMutantFactory(store);
    when(statement.getTree()).thenReturn(tree);
  }

  @Test
  public void oneMutatorForASingleGene() {
    when(store.getMutatorsFor(tree)).thenReturn(asList(mutator1));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(1));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    ParserGene gene = (ParserGene) mutant.getGene();
    assertThat(gene.getTree(), is(tree));

    ParserLocus locus = (ParserLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getNodeIndex(), is(0)); 
  }

  @Test
  public void noMutatorsForAStatement() {
    when(statement.getTree()).thenReturn(tree);
    when(store.getMutatorsFor(tree)).thenReturn(Collections.<Mutator> emptyList());

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(0));
  }

  @Test
  public void multipleMutatorsForASingleGene() {
    when(store.getMutatorsFor(tree)).thenReturn(asList(mutator1, mutator2));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(2));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    ParserGene gene = (ParserGene) mutant.getGene();
    assertThat(gene.getTree(), is(tree));

    ParserLocus locus = (ParserLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getNodeIndex(), is(0));

    mutant = mutants.get(1);
    assertThat(mutant.getMutator(), is(mutator2));

    gene = (ParserGene) mutant.getGene();
    assertThat(gene.getTree(), is(tree));

    locus = (ParserLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getNodeIndex(), is(0)); // genes linked to same node so have same index
  }

  @Test
  public void oneMutatorForASingleNestedGene() {
    when(tree.getChildren()).thenReturn(new ArrayList<Node>(asList(child1, child2)));
    when(store.getMutatorsFor(tree)).thenReturn(Collections.emptyList());
    when(store.getMutatorsFor(child1)).thenReturn(asList(mutator1));
    when(store.getMutatorsFor(child2)).thenReturn(asList(mutator2));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(2));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    ParserGene gene = (ParserGene) mutant.getGene();
    assertThat(gene.getTree(), is(child1));

    ParserLocus locus = (ParserLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getNodeIndex(), is(1));
  }

  @Test
  public void oneMutatorForMultipleNestedGenes() {
    when(tree.getChildren()).thenReturn(new ArrayList<Node>(asList(child1, child2)));
    when(store.getMutatorsFor(tree)).thenReturn(Collections.emptyList());
    when(store.getMutatorsFor(child1)).thenReturn(asList(mutator1));
    when(store.getMutatorsFor(child2)).thenReturn(asList(mutator2));

    List<Mutant> mutants = mutantFactory.newMutants(0, statement);
    assertThat(mutants.size(), is(2));

    Mutant mutant = mutants.get(0);
    assertThat(mutant.getMutator(), is(mutator1));

    ParserGene gene = (ParserGene) mutant.getGene();
    assertThat(gene.getTree(), is(child1));

    ParserLocus locus = (ParserLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getNodeIndex(), is(1));

    mutant = mutants.get(1);
    assertThat(mutant.getMutator(), is(mutator2));

    gene = (ParserGene) mutant.getGene();
    assertThat(gene.getTree(), is(child2));

    locus = (ParserLocus) gene.getLocus();
    assertThat(locus.getScriptIndex(), is(0));
    assertThat(locus.getStatementIndex(), is(0));
    assertThat(locus.getNodeIndex(), is(2));
  }

}
