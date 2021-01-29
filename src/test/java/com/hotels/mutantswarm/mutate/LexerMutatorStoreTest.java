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

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class LexerMutatorStoreTest {

  @Mock
  private CommonToken token1, token2;
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
    assertThat(mutators.get(0).getDescription(), is(description));
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

  @Test
  public void checkNullToken() {
    int position = 0;
    when(stream.get(position)).thenReturn(null);
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    Assertions.assertThrows(NullPointerException.class, () -> lexerStore.getMutatorsFor(position, stream));
  }

  @Test
  public void emptyStream() {
    int position = 0;
    stream = new ArrayList<>();
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> lexerStore.getMutatorsFor(position, stream));
  }

  @Test
  public void nullStream() {
    int position = 0;
    LexerMutatorStore lexerStore = new LexerMutatorStore();
    Assertions.assertThrows(NullPointerException.class, () -> lexerStore.getMutatorsFor(position, null));
  }

}
