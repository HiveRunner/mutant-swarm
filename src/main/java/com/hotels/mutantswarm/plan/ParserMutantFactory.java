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

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.mutate.ParserMutatorStore;
import com.hotels.mutantswarm.plan.gene.ParserGene;
import com.hotels.mutantswarm.plan.gene.ParserLocus;

/** Generates mutants from parser abstract syntax tree */
class ParserMutantFactory implements MutantFactory {

  private final ParserMutatorStore parserMutatorStore;

  ParserMutantFactory(ParserMutatorStore parserDatabase) {
    this.parserMutatorStore = parserDatabase;
  }

  @Override
  public List<Mutant> newMutants(int scriptIndex, MutantSwarmStatement statement) {
    return newParserMutants(scriptIndex, statement.getIndex(), statement.getTree(), 0);
  }

  private List<Mutant> newParserMutants(int scriptIndex, int statementIndex, ASTNode node, int nodeIndex) {
    List<Mutant> mutations = new ArrayList<>();
    List<Mutator> mutators = parserMutatorStore.getMutatorsFor(node);
    if (!mutators.isEmpty()) {
      ParserLocus locus = new ParserLocus(scriptIndex, statementIndex, nodeIndex);
      ParserGene gene = new ParserGene(locus, node);
      for (Mutator mutator : mutators) {
        mutations.add(new Mutant(gene, mutator));
      }
    }
    List<Node> children = node.getChildren();
    if (children != null) {
      for (Node subTree : children) {
        nodeIndex = nodeIndex + 1;
        mutations.addAll(newParserMutants(scriptIndex, statementIndex, (ASTNode) subTree, nodeIndex));
      }
    }
    return mutations;
  }

}
