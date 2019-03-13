/**
 * Copyright (C) 2018-2019 Expedia, Inc.
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
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.LexerMutatorStore;
import com.hotels.mutantswarm.mutate.ParserMutatorStore;

/** Factory that is a composite of both lexer and parser mutant factories */
public class CompositeMutantFactory implements MutantFactory {

  private final List<MutantFactory> factories;

  CompositeMutantFactory(MutantFactory... factories) {
    this.factories = asList(factories);
  }

  public CompositeMutantFactory() {
    this(new LexerMutantFactory(new LexerMutatorStore()), new ParserMutantFactory(new ParserMutatorStore()));
  }

  @Override
  public List<Mutant> newMutants(int scriptIndex, MutantSwarmStatement statement) {
    List<Mutant> mutations = new ArrayList<>();
    for (MutantFactory factory : factories) {
      mutations.addAll(factory.newMutants(scriptIndex, statement));
    }
    return unmodifiableList(mutations);
  }

}
