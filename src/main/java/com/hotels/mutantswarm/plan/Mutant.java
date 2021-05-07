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
package com.hotels.mutantswarm.plan;

import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.plan.gene.Gene;

/**
 * Represents the application of a singe mutator to a specific gene in the script statement
 */
public class Mutant {

  private Gene gene;
  private final Mutator mutator;

  public Mutant(Gene gene, Mutator mutator) {
    this.gene = gene;
    this.mutator = mutator;
  }

  /**
   * The mutator to apply to the specific gene
   */
  public Mutator getMutator() {
    return mutator;
  }

  /**
   * The gene from the script to mutate
   */
  public Gene getGene() {
    return gene;
  }
  
  public int getScriptIndex(){
    return gene.getScriptIndex();
  }
  
  public int getStatementIndex(){
    return gene.getStatementIndex();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gene == null) ? 0 : gene.hashCode());
    result = prime * result + ((mutator == null) ? 0 : mutator.hashCode());
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
    Mutant other = (Mutant) obj;
    if (gene == null) {
      if (other.gene != null)
        return false;
    } else if (!gene.equals(other.gene))
      return false;
    if (mutator == null) {
      if (other.mutator != null)
        return false;
    } else if (!mutator.equals(other.mutator))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Mutant [gene=" + gene + ", mutator=" + mutator + "]";
  }
  
}
