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
package com.hotels.mutantswarm.mutate;

import java.util.function.Function;

import com.hotels.mutantswarm.plan.gene.Gene;

/** A function that can mutate a gene. */
public interface Mutator extends Function<Gene, Mutation> {

  /**
   * Returns the name of the mutator.
   */
  String getName();

  /**
   * Returns the description of the mutator. 
   */
  String getDescription();

  /**
   * Applies the mutator to the gene, yielding a concrete mutation which should then be applied to the original SQL text.
   */
  Mutation apply(Gene node);

}
