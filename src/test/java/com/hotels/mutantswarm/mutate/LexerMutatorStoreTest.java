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
package com.hotels.mutantswarm.mutate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.mutate.LexerMutatorStore.Imposters.Imposter;

@RunWith(MockitoJUnitRunner.class)
public class LexerMutatorStoreTest {

  @Mock
  private CommonToken token1, token2;
  @Mock
  private Imposter imposter1, imposter2, imposter3;
  @Mock
  private List<CommonToken> stream;

  @Test
  public void checkImpostersForAsc() {
    int position = 0;
    when(stream.get(position)).thenReturn(token1);
    when(token1.getType()).thenReturn(Vocabulary.INSTANCE.getId("KW_ASC"));
    when(token1.getText()).thenReturn("ASC");

    String description = "KW_ASC → KW_DESC 'DESC'";
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    List<Mutator> mutators = lexerStore.getMutatorsFor(position, stream);

    assertThat(mutators.size(), is(1));
    assertEquals(mutators.get(0).getDescription(), description);
  }

  @Test
  public void checkImpostersForFloat() {
    int position = 0;
    when(stream.get(position)).thenReturn(token1);
    when(token1.getType()).thenReturn(Vocabulary.INSTANCE.getId("KW_FLOAT"));
    when(token1.getText()).thenReturn("FLOAT");

    LexerMutatorStore lexerStore = new LexerMutatorStore();
    List<Mutator> mutators = lexerStore.getMutatorsFor(position, stream);

    assertThat(mutators.size(), is(3));
  }

  @Test
  public void checkImpostersForSelect() {
    int position = 0;
    when(stream.get(position)).thenReturn(token1);
    when(token1.getType()).thenReturn(Vocabulary.INSTANCE.getId("KW_DISTINCT"));

    String description = "KW_DISTINCT → KW_ALL 'ALL'";
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    List<Mutator> mutators = lexerStore.getMutatorsFor(position, stream);

    assertThat(mutators.size(), is(1));
    assertThat(mutators.get(0).getDescription(), is(description));
  }

  @Test
  public void checkImpostersForJoin() {
    int position = 0;
    when(stream.get(position)).thenReturn(token1);
    when(token1.getType()).thenReturn(Vocabulary.INSTANCE.getId("TOK_FULLOUTERJOIN"));

    LexerMutatorStore lexerStore = new LexerMutatorStore();
    List<Mutator> mutators = lexerStore.getMutatorsFor(position, stream);

    assertThat(mutators.size(), is(3));
    assertThat(mutators.get(0).getDescription(), is("TOK_FULLOUTERJOIN → KW_INNER 'INNER'"));
    assertThat(mutators.get(1).getDescription(), is("TOK_FULLOUTERJOIN → TOK_LEFTOUTERJOIN 'LEFT OUTER'"));
    assertThat(mutators.get(2).getDescription(), is("TOK_FULLOUTERJOIN → TOK_RIGHTOUTERJOIN 'RIGHT OUTER'"));
  }

  @Test
  public void checkInvalidType() {
    int position = 0;
    when(stream.get(position)).thenReturn(token1);
    when(token1.getType()).thenReturn(99999);

    LexerMutatorStore lexerStore = new LexerMutatorStore();
    List<Mutator> mutators = lexerStore.getMutatorsFor(position, stream);

    assertThat(mutators.size(), is(0));
  }

  @Test(expected = NullPointerException.class)
  public void checkNullToken() {
    int position = 0;
    when(stream.get(position)).thenReturn(null);
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    lexerStore.getMutatorsFor(position, stream);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void emptyStream() {
    int position = 0;
    stream = new ArrayList<>();
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    lexerStore.getMutatorsFor(position, stream);
  }

  @Test(expected = NullPointerException.class)
  public void nullStream() {
    int position = 0;
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    lexerStore.getMutatorsFor(position, null);
  }


}
