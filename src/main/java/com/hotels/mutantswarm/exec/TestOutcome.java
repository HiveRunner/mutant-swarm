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
package com.hotels.mutantswarm.exec;

import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;

/**
 * Represents the outcome of running a mutant query within and individual test case of the test suite.
 */
public class TestOutcome {

  private final String testName;
  private final Mutant mutant;
  private final Mutation mutation;
  private final MutantState state;

  public TestOutcome(String testName, Mutant mutant, Mutation mutation, MutantState state) {
    this.testName = testName;
    this.mutant = mutant;
    this.mutation = mutation;
    this.state = state;
  }
  
  public String getTestName() {
    return testName;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mutant == null) ? 0 : mutant.hashCode());
    result = prime * result + ((mutation == null) ? 0 : mutation.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((testName == null) ? 0 : testName.hashCode());
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
    TestOutcome other = (TestOutcome) obj;
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
    if (testName == null) {
      if (other.testName != null)
        return false;
    } else if (!testName.equals(other.testName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "TestOutcome [testName="
        + testName
        + ", mutant="
        + mutant
        + ", mutation="
        + mutation
        + ", state="
        + state
        + "]";
  }

}
