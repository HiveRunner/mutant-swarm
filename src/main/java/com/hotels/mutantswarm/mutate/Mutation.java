/**
 * Copyright (C) 2018-2020 Expedia, Inc.
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

/**
 * The result of altering a gene Mutated with a specific Mutator Used to modify the original SQL.
 */
public interface Mutation {

  /**
   * The mutated text to replace for the gene in the original sql
   */
  String getReplacementText();

  /**
   * Position to start/stop mutating text in original SQL
   */
  Splice getSplice();

  public class MutationImpl implements Mutation {

    private String replacementText;
    private Splice splice;

    public MutationImpl(String replacementText, Splice splice) {
      this.replacementText = replacementText;
      this.splice = splice;
    }

    @Override
    public String getReplacementText() {
      return replacementText;
    }

    @Override
    public Splice getSplice() {
      return splice;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((replacementText == null) ? 0 : replacementText.hashCode());
      result = prime * result + ((splice == null) ? 0 : splice.hashCode());
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
      MutationImpl other = (MutationImpl) obj;
      if (replacementText == null) {
        if (other.replacementText != null)
          return false;
      } else if (!replacementText.equals(other.replacementText))
        return false;
      if (splice == null) {
        if (other.splice != null)
          return false;
      } else if (!splice.equals(other.splice))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "MutationImpl [splice=" + splice + ", replacementText=" + replacementText + "]";
    }

  }

}
