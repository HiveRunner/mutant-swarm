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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class VocabularyTest {

  @Test
  public void nameToId() {
    int id = Vocabulary.INSTANCE.getId("TOK_JOIN");
    String name = Vocabulary.INSTANCE.getName(id);
    assertThat("TOK_JOIN", is(name));
  }
  
  @Test
  public void idToName() {
    String name = Vocabulary.INSTANCE.getName(-1);
    int id = Vocabulary.INSTANCE.getId(name);
    assertThat(id, is(-1));
  }

  @Test
  public void nullName() {
    Assertions.assertThrows(NullPointerException.class, () -> Vocabulary.INSTANCE.getId(null));
  }

  @Test
  public void nullId() {
    String name = Vocabulary.INSTANCE.getName(1000000);
    assertThat(null, is(name));
  }

}
