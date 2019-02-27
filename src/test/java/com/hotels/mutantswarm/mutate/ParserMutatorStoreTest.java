/**
 * Copyright (C) 2018-2019 Expedia Inc.
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
package com.hotels.mutantswarm.mutate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.Tree;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParserMutatorStoreTest {

  @Mock
  private ASTNode node1, node2;
  @Mock
  private CommonToken token;
  @Mock
  private Tree tree1, tree2;

  private ParserMutatorStore database = new ParserMutatorStore();

  @Before
  public void setupMocks() {
    when(node1.getToken()).thenReturn(token);
    when(token.getStartIndex()).thenReturn(2);
    when(token.getStopIndex()).thenReturn(2);
  }

  @Test
  public void checkMutatorsForEqual() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("EQUAL"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(5));

    Mutator mutator = mutators.get(0);
    assertThat(mutator.getDescription(), is("Relational op EQ → LT '<'"));
    mutator = mutators.get(1);
    assertThat(mutator.getDescription(), is("Relational op EQ → GT '>'"));
  }

  @Test
  public void checkMutatorsForEqualInJoin() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("EQUAL"));
    when((List<Tree>) node1.getAncestors()).thenReturn((List<Tree>) Arrays.asList(tree1, tree2));
    when(tree1.getType()).thenReturn(Vocabulary.INSTANCE.getId("Number"));
    when(tree2.getType()).thenReturn(Vocabulary.INSTANCE.getId("TOK_FULLOUTERJOIN"));

    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(0));
  }

  @Test
  public void checkMutatorsForNotEqual() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("NOTEQUAL"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(5));
  }

  @Test
  public void checkMutatorsForLessThan() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("LESSTHAN"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(5));
  }

  @Test
  public void checkMutatorsForLessThanOrEqualTo() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("LESSTHANOREQUALTO"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(5));
  }

  @Test
  public void checkMutatorsForGreaterThan() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("GREATERTHAN"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(5));

    assertThat(mutators.get(0).getDescription(), is("Relational op GT → LT '<'"));
    assertThat(mutators.get(1).getDescription(), is("Relational op GT → GTE '>='"));
    assertThat(mutators.get(2).getDescription(), is("Relational op GT → LTE '<='"));
    assertThat(mutators.get(3).getDescription(), is("Relational op GT → NEQ '<>'"));
    assertThat(mutators.get(4).getDescription(), is("Relational op GT → EQ '='"));
  }

  @Test
  public void checkMutatorsForGreaterThanOrEqualTo() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("GREATERTHANOREQUALTO"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(5));
  }

  @Test
  public void checkMutatorsForAnd() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("KW_AND"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getDescription(), is("Logical Op AND → OR 'OR'"));
  }

  @Test
  public void checkMutatorsForOr() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("KW_OR"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getDescription(), is("Logical Op OR → AND 'AND'"));
  }

  @Test
  public void checkMutatorsForNot() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("KW_NOT"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getDescription(), is("Logical Op remove NOT ''"));
  }

  @Test
  public void checkMutatorsForNumber() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("Number"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getDescription(), is("NumberLiteral change value: '8438749'"));
  }

  @Test
  public void checkMutatorsForLowerFunction() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("Identifier"));
    when(node1.getText()).thenReturn("lower");

    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getName(), is("lower -> upper"));
  }

  @Test
  public void checkMutatorsForStringLiteral() {
    when(node1.getType()).thenReturn(Vocabulary.INSTANCE.getId("StringLiteral"));
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getDescription(), is("StringLiteral change value: 'jdjfhsj'"));
  }

  @Test
  public void checkInvalidMutator() {
    when(node1.getType()).thenReturn(-1);
    List<Mutator> mutators = database.getMutatorsFor(node1);
    assertThat(mutators.size(), is(0));
  }

}
