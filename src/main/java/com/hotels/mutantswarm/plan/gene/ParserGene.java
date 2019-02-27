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
package com.hotels.mutantswarm.plan.gene;

import org.apache.hadoop.hive.ql.parse.ASTNode;

/**
 * A gene represented in the abstract syntax sub-tree produced by the parser.
 */
public class ParserGene extends Gene {

  private final ASTNode tree;

  public ParserGene(Locus locus, ASTNode tree) {
    super(locus);
    this.tree = tree;
  }

  /**
   * The pertinent subtree
   */
  public ASTNode getTree() {
    return tree;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((tree == null) ? 0 : tree.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ParserGene other = (ParserGene) obj;
    if (tree == null) {
      if (other.tree != null)
        return false;
    } else if (!tree.equals(other.tree))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ParserGene [locus=" + getLocus() + ", tree=" + tree + "]";
  }

}
