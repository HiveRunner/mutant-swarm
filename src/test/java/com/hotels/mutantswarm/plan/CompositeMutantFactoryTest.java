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
package com.hotels.mutantswarm.plan;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.mutantswarm.model.MutantSwarmStatement;

@RunWith(MockitoJUnitRunner.class)
public class CompositeMutantFactoryTest {

  @Mock
  private MutantSwarmStatement statement;
  @Mock
  private LexerMutantFactory lexerMutantFactory;
  @Mock
  private ParserMutantFactory parserMutantFactory;
  @Mock
  private Mutant mutant1, mutant2, mutant3;

  @Test
  public void testNewMutants() {
    int scriptIndex = 0;
    List<Mutant> lexerList = Arrays.asList(mutant1, mutant2);
    List<Mutant> parserList = Arrays.asList(mutant3);

    CompositeMutantFactory mutantFactory = new CompositeMutantFactory(lexerMutantFactory, parserMutantFactory);
    when(lexerMutantFactory.newMutants(scriptIndex, statement)).thenReturn(lexerList);
    when(parserMutantFactory.newMutants(scriptIndex, statement)).thenReturn(parserList);

    List<Mutant> newMutants = mutantFactory.newMutants(scriptIndex, statement);
    assertThat(newMutants.size(), is(3));
    assertThat(newMutants.get(0), is(mutant1));
    assertThat(newMutants.get(1), is(mutant2));
    assertThat(newMutants.get(2), is(mutant3));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testUnmodifiableList() {
    int scriptIndex = 0;
    List<Mutant> lexerList = Arrays.asList(mutant1, mutant2);

    CompositeMutantFactory mutantFactory = new CompositeMutantFactory(lexerMutantFactory);
    when(lexerMutantFactory.newMutants(scriptIndex, statement)).thenReturn(lexerList);

    List<Mutant> newMutants = mutantFactory.newMutants(scriptIndex, statement);
    newMutants.clear();
  }

}
