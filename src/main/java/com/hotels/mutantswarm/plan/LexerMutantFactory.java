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
package com.hotels.mutantswarm.plan;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;

import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.LexerMutatorStore;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.plan.gene.LexerGene;
import com.hotels.mutantswarm.plan.gene.LexerLocus;

/** Generates mutants from lexer token stream */
class LexerMutantFactory implements MutantFactory {

  private final LexerMutatorStore lexerMutatorStore;

  LexerMutantFactory(LexerMutatorStore lexerDatabase) {
    this.lexerMutatorStore = lexerDatabase;
  }

  @Override
  public List<Mutant> newMutants(int scriptIndex, MutantSwarmStatement statement) {
    List<CommonToken> tokens = statement.getTokens();
    List<Mutant> mutations = new ArrayList<>();
    for (int i = 0; i < tokens.size(); i++) {
      List<Mutator> mutators = lexerMutatorStore.getMutatorsFor(i, tokens);
      if (!mutators.isEmpty()) {
        LexerLocus locus = new LexerLocus(scriptIndex, statement.getIndex(), singletonList(i));
        LexerGene gene = new LexerGene(locus, tokens.subList(i, i + 1));
        for (Mutator mutator : mutators) {
          mutations.add(new Mutant(gene, mutator));
        }
      }
    }
    return mutations;
  }

}
