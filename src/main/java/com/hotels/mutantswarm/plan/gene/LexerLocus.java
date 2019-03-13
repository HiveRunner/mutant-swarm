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

import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * The location of a gene within the lexer token stream; a list of indexes/pointers to specific tokens in the stream.
 */
public class LexerLocus extends Locus {

  private final List<Integer> indexes;

  public LexerLocus(int scriptIndex, int statementIndex, List<Integer> indexes) {
    super(scriptIndex, statementIndex);
    this.indexes = unmodifiableList(indexes);
  }

  /** Indexes of the pertinent tokens within the stream. */
  public List<Integer> getIndexes() {
    return indexes;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((indexes == null) ? 0 : indexes.hashCode());
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
    LexerLocus other = (LexerLocus) obj;
    if (indexes == null) {
      if (other.indexes != null)
        return false;
    } else if (!indexes.equals(other.indexes))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "LexerLocus [scriptIndex="
        + getScriptIndex()
        + ", statementIndex="
        + getStatementIndex()
        + ", indexes="
        + indexes
        + "]";
  }

}
