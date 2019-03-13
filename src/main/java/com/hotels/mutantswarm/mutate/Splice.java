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
package com.hotels.mutantswarm.mutate;

import org.antlr.runtime.CommonToken;

import com.hotels.mutantswarm.plan.gene.Gene;
import com.hotels.mutantswarm.plan.gene.LexerGene;
import com.hotels.mutantswarm.plan.gene.ParserGene;

/** Represents a the bounds of a character block within the original query string. */
public class Splice {

  static class Factory {

    Splice newInstance(Gene gene) {
      if (gene instanceof ParserGene) {
        return simpleSplice((CommonToken) ((ParserGene) gene).getTree().getToken());
      } else if (gene instanceof LexerGene) {
        return simpleSplice(((LexerGene) gene).getTokens().get(0));
      }
      throw new IllegalArgumentException("Unknown gene type");
    }

    private Splice simpleSplice(CommonToken token) {
      return new Splice(token.getStartIndex(), token.getStopIndex());
    }

  }

  private final int startIndex;
  private final int stopIndex;

  Splice(int startIndex, int stopIndex) {
    this.startIndex = startIndex;
    this.stopIndex = stopIndex;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getStopIndex() {
    return stopIndex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + stopIndex;
    result = prime * result + startIndex;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Splice other = (Splice) obj;
    if (stopIndex != other.stopIndex)
      return false;
    if (startIndex != other.startIndex)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Splice [startIndex=" + startIndex + ", endIndex=" + stopIndex + "]";
  }

}
