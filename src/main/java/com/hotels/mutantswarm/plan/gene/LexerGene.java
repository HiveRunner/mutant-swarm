/*
 * Copyright (C) 2018-2021 Expedia, Inc.
 * Copyright (C) 2021 The HiveRunner Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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

import org.antlr.runtime.CommonToken;

/**
 * A gene represented in the tokens produced by the lexer.
 */
public class LexerGene extends Gene {

  private final List<CommonToken> tokens;

  public LexerGene(LexerLocus locus, List<CommonToken> tokens) {
    super(locus);
    this.tokens = unmodifiableList(tokens);
  }

  /** The lexer token stream. */
  public List<CommonToken> getTokens() {
    return tokens;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
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
    LexerGene other = (LexerGene) obj;
    if (tokens == null) {
      if (other.tokens != null)
        return false;
    } else if (!tokens.equals(other.tokens))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "LexerGene [locus=" + getLocus() + ", tokens=" + tokens + "]";
  }

  
}
