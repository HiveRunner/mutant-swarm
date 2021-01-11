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
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.MutatedSourceFactory.MutatedSource;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.mutate.Splice;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.gene.Gene;

@RunWith(MockitoJUnitRunner.class)
public class MutatedSourceFactoryTest {

  @Mock
  private MutantSwarmSource source;
  @Mock
  private MutantSwarmScript script;
  @Mock
  private MutantSwarmStatement statement;
  @Mock
  private Mutant mutant;
  @Mock
  private Mutator mutator;
  @Mock
  private Gene gene;
  @Mock
  private Mutation mutation;
  @Mock
  private Splice splice;
  
  private MutatedSourceFactory mutatedSourceFactory = new MutatedSourceFactory();
  
  @Before
  public void setup(){
    when(mutant.getMutator()).thenReturn(mutator);
    when(mutant.getGene()).thenReturn(gene);
    when(mutator.apply(gene)).thenReturn(mutation);
    
    when(source.getScripts()).thenReturn(Arrays.asList(script));
    when(script.getIndex()).thenReturn(0);
    when(mutant.getScriptIndex()).thenReturn(0);
    when(script.getStatements()).thenReturn(Arrays.asList(statement));
    when(statement.getIndex()).thenReturn(0);
    when(mutant.getStatementIndex()).thenReturn(0);
    
    when(mutation.getSplice()).thenReturn(splice);
  }

  @Test
  public void simpleSourceWithInnerJoin() {
    String sql = "select a from foo inner join bar on foo.a = bar.a";
    // 0 select a from foo inner join bar on foo.a = bar.a
    // --012345678901234567890123456
    String expectedMutatedSql = "select a from foo left outer join bar on foo.a = bar.a;\n";
    
    when(statement.getSql()).thenReturn(sql);
    when(mutation.getReplacementText()).thenReturn("left outer");
    when(splice.getStartIndex()).thenReturn(18);
    when(splice.getStopIndex()).thenReturn(22);

    MutatedSource mutatedSource = mutatedSourceFactory.newMutatedSource(source, mutant);

    String actualMutatedSql = mutatedSource.getScripts().get(0).getSql();
    assertThat(expectedMutatedSql, is(actualMutatedSql));
  }
  
  @Test
  public void simpleSourceWithEqual() {
    String sql = "select a from foo inner join bar on foo.a = bar.a";
    // 0 select a from foo inner join bar on foo.a = bar.a
    // --0123456789012345678901234567890123456789012345678
    String expectedMutatedSql = "select a from foo inner join bar on foo.a <> bar.a;\n";
    
    when(statement.getSql()).thenReturn(sql);
    when(mutation.getReplacementText()).thenReturn("<>");
    when(splice.getStartIndex()).thenReturn(42);
    when(splice.getStopIndex()).thenReturn(42);
    
    MutatedSourceFactory mutatedSourceFactory = new MutatedSourceFactory();
    MutatedSource mutatedSource = mutatedSourceFactory.newMutatedSource(source, mutant);
    
    String actualMutatedSql = mutatedSource.getScripts().get(0).getSql();
    assertThat(expectedMutatedSql, is(actualMutatedSql));
  }

}
