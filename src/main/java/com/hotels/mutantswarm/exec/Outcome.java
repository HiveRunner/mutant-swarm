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
package com.hotels.mutantswarm.exec;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;

/**
 * Represents the outcome of a mutant once applied to the test suite. Describes the mutant, mutation performed, and the
 * final state (killed/survived).
 */
public class Outcome {

  private final Mutant mutant;
  private final Mutation mutation;
  private MutantState state;
  private final List<TestOutcome> testOutcomes;

  Outcome(Mutant mutant, Mutation mutation, List<TestOutcome> testOutcomes) {
    this.mutant = mutant;
    this.mutation = mutation;
    this.testOutcomes = unmodifiableList(testOutcomes);
    this.state = setState(testOutcomes);
  }

  public Mutant getMutant() {
    return mutant;
  }

  public Mutation getMutation() {
    return mutation;
  }

  public MutantState getState() {
    return state;
  }
  
  public List<TestOutcome> getTestOutcomes() {
    return testOutcomes;
  }
  
  public int getMutationStartIndex(){
    return mutation.getSplice().getStartIndex();
  }

  private MutantState setState(List<TestOutcome> testOutcomes) {
    MutantState state = MutantState.SURVIVED;
    for (TestOutcome outcome : testOutcomes) {
      if (outcome.getState() == MutantState.KILLED) {
        state = MutantState.KILLED;
      }
    }
    return state;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mutant == null) ? 0 : mutant.hashCode());
    result = prime * result + ((mutation == null) ? 0 : mutation.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((testOutcomes == null) ? 0 : testOutcomes.hashCode());
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
    Outcome other = (Outcome) obj;
    if (mutant == null) {
      if (other.mutant != null)
        return false;
    } else if (!mutant.equals(other.mutant))
      return false;
    if (mutation == null) {
      if (other.mutation != null)
        return false;
    } else if (!mutation.equals(other.mutation))
      return false;
    if (state != other.state)
      return false;
    if (testOutcomes == null) {
      if (other.testOutcomes != null)
        return false;
    } else if (!testOutcomes.equals(other.testOutcomes))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Outcome [mutant="
        + mutant
        + ", mutation="
        + mutation
        + ", state="
        + state
        + ", testOutcomes="
        + testOutcomes
        + "]";
  }

}
