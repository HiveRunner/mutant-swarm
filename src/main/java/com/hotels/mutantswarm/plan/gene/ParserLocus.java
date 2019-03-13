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
package com.hotels.mutantswarm.plan.gene;

/**
 * The location of a gene within the parser abstract syntax tree. A DFS node offset.
 */
public class ParserLocus extends Locus {

  /**
   * The index of the node in the tree, based on a depth-first search. 
   */
  private final int nodeIndex;

  public ParserLocus(int scriptIndex, int statementIndex, int nodeIndex) {
    super(scriptIndex, statementIndex);
    this.nodeIndex = nodeIndex;
  }

  /**
   * Index of gene within all genes in a statement
   */
  public int getNodeIndex() {
    return nodeIndex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + nodeIndex;
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
    ParserLocus other = (ParserLocus) obj;
    if (nodeIndex != other.nodeIndex)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ParserLocus [scriptIndex="
        + getScriptIndex()
        + ", statementIndex="
        + getStatementIndex()
        + ", nodeIndex="
        + nodeIndex
        + "]";
  }

}
