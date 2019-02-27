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

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;

/**
 * Represents a set of mutants and includes code to generate the set of mutants for a given source (set of scripts)
 * under test, by consulting a database of mutators
 */
public class Swarm {

  public static class SwarmFactory {

    private final MutantFactory mutantFactory;

    public SwarmFactory(MutantFactory mutantFactory) {
      this.mutantFactory = mutantFactory;
    }

    /**
     * Generates a new gene / mutator combination
     */
    public Swarm newInstance(MutantSwarmSource source) {
      List<Mutant> mutants = new ArrayList<>();
      for (MutantSwarmScript script : source.getScripts()) {
        for (MutantSwarmStatement statement : script.getStatements()) {
          mutants.addAll(mutantFactory.newMutants(script.getIndex(), statement));
        }
      }
      return new Swarm(source, mutants);
    }

  }

  private final MutantSwarmSource source;
  private final List<Mutant> mutants;

  private Swarm(MutantSwarmSource source, List<Mutant> mutants) {
    this.source = source;
    this.mutants = unmodifiableList(mutants);
  }

  /**
   * The original unchanged source
   */
  public MutantSwarmSource getSource() {
    return source;
  }

  /**
   * The mutants to perform on the source
   */
  public List<Mutant> getMutants() {
    return mutants;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mutants == null) ? 0 : mutants.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
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
    Swarm other = (Swarm) obj;
    if (mutants == null) {
      if (other.mutants != null)
        return false;
    } else if (!mutants.equals(other.mutants))
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Swarm [source=" + source + ", mutants=" + mutants + "]";
  }

}
