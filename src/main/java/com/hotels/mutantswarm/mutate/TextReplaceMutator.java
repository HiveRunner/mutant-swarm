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

import com.hotels.mutantswarm.plan.gene.Gene;

/** Simple mutator for replacing 'this' with 'that' */
public class TextReplaceMutator implements Mutator {
  private final String description;
  private final String from;
  private final String to;
  private final Splice.Factory spliceFactory;

  TextReplaceMutator(Splice.Factory spliceFactory, String description, String originalText, String mutatedText) {
    this.spliceFactory = spliceFactory;
    this.description = description;
    this.from = originalText;
    this.to = mutatedText;
  }

  TextReplaceMutator(String description, String originalText, String mutatedText) {
    this(new Splice.Factory(), description, originalText, mutatedText);
  }

  /**
   * For debug mainly
   */
  @Override
  public String getName() {
    return from + " -> " + to;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Applies the mutator to the gene, yielding a concrete mutation which should then be applied to the original SQL test
   */
  @Override
  public Mutation apply(final Gene gene) {
    return new Mutation() {
      private Splice splice;

      /**
       * Where to start/stop mutating the text in the original SQL
       */
      @Override
      public Splice getSplice() {
        splice = spliceFactory.newInstance(gene);
        return splice;
      }

      /**
       * The mutated text to splice into the original SQL
       */
      @Override
      public String getReplacementText() {
        return to;
      }

    };

  }

}
