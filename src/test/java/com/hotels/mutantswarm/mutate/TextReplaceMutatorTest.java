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
package com.hotels.mutantswarm.mutate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotels.mutantswarm.plan.gene.Locus;
import com.hotels.mutantswarm.plan.gene.ParserGene;

@ExtendWith(MockitoExtension.class)
public class TextReplaceMutatorTest {

  @Mock
  private ParserGene gene;
  @Mock
  private Locus locus;
  @Mock
  private Splice splice;
  @Mock
  private CommonToken token;
  @Mock
  private ASTNode tree;

  private Mutator mutator;
  private Splice.Factory spliceFactory;

  @BeforeEach
  public void setupMocks() {
    when(gene.getTree()).thenReturn(tree);
    when(tree.getToken()).thenReturn(token);
    when(token.getStartIndex()).thenReturn(1);
    when(token.getStopIndex()).thenReturn(10);

    spliceFactory = new Splice.Factory();
    splice = spliceFactory.newInstance(gene);
    mutator = new TextReplaceMutator(spliceFactory, "test", "=", "<>");
  }

  @Test
  public void description() {
    assertThat(mutator.getDescription(), is("test"));
  }

  @Test
  public void name() {
    assertThat(mutator.getName(), is("= -> <>"));
  }

  @Test
  public void apply() {
    Mutation mutation = mutator.apply(gene);

    assertThat(mutation.getSplice(), is(splice));
    assertThat(mutation.getReplacementText(), is("<>"));
  }
}
