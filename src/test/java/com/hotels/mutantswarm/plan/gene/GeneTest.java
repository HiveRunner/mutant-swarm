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
package com.hotels.mutantswarm.plan.gene;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.TestOutcome;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.plan.Mutant;


@RunWith(MockitoJUnitRunner.class)
public class GeneTest {

  @Mock
  private Gene gene;
  @Mock
  private Locus locus;
  @Mock
  private Mutant mutant;
  @Mock
  private Mutator mutator;
  @Mock
  private Mutation mutation;
  
  @Before
  public void setupMocks() {
    when(mutant.getMutator()).thenReturn(mutator);
    when(mutant.getGene()).thenReturn(gene);
    when(mutator.apply(gene)).thenReturn(mutation);
    when(mutant.getScriptIndex()).thenReturn(0);

  }
  
  @Test
  public void checkGetLocus() {
    locus = gene.getLocus();
    String result = locus.toString();
    System.out.println(result);
  }
  
  @Test
  public void CheckGetStatementIndex() {
    int result =  locus.getStatementIndex();
    System.out.println(result);
  }
  
}
