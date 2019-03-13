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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.hotels.mutantswarm.model.Key;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;
import com.hotels.mutantswarm.plan.gene.Gene;

/**
 * Stores and manages the outcomes from executing the mutants of a swarm with the test suite.
 */
public class SwarmResults {

  /**
   * Incrementally constructs a SwarmResult.
   */
  public static class SwarmResultsBuilder {

    private Map<Mutant, List<TestOutcome>> testOutcomesByMutant = new HashMap<>();

    private final Swarm swarm;
    private final String suiteName;
    private final DateTime startTime;

    public SwarmResultsBuilder(Swarm swarm, String suiteName) {
      this.swarm = swarm;
      this.suiteName = suiteName;
      startTime = new DateTime();
    }
    
    /**
     * Adds the result of running a mutant to the map.
     */
    public void addTestOutcome(String testName, Mutant mutant, Mutation mutation, MutantState state){
      List<TestOutcome> testOutcomes;
      if (testOutcomesByMutant.containsKey(mutant)){
        testOutcomes = testOutcomesByMutant.get(mutant);
      } else {
        testOutcomes = new ArrayList<TestOutcome>();
        testOutcomesByMutant.put(mutant, testOutcomes);
      }
      testOutcomes.add(new TestOutcome(testName, mutant, mutation, state));
    }

    /**
     * Generates a map containing the overall outcome from running each mutation against the statements in a script.
     */
    private Map<Key, List<Outcome>> generateOutcomeList(){
      Map<Key, List<Outcome>> scriptOutcomes = new HashMap<>();
      
      for (Entry<Mutant, List<TestOutcome>> entry : testOutcomesByMutant.entrySet()){
        Mutant mutant = entry.getKey();
        List<TestOutcome> testOutcomes = entry.getValue();
        
        Gene gene = mutant.getGene();
        Mutation mutation = testOutcomes.get(0).getMutation();  // same mutation for all test outcomes
        
        Key key = new Key(gene.getScriptIndex(), gene.getStatementIndex());
        List<Outcome> outcomes;
        
        if (scriptOutcomes.containsKey(key)){
          outcomes = scriptOutcomes.get(key);
        } else {
          outcomes = new ArrayList<>();
          scriptOutcomes.put(key, outcomes);
        }
        outcomes.add(new Outcome(mutant, mutation, testOutcomes));
        
      }
      return scriptOutcomes;
    }
    
    public SwarmResults build() {
      Map<Key, List<Outcome>> outcomes = generateOutcomeList();
      return new SwarmResults(swarm, suiteName, startTime.toString(ISODateTimeFormat.dateHourMinuteSecond()), outcomes);
    }
  }

  private final Swarm swarm;
  private final String suiteName;
  private final String startTime;
  private final Map<Key, List<Outcome>> outcomesByScriptIndex;

  private SwarmResults(Swarm swarm, String suiteName, String startTime, Map<Key, List<Outcome>> scriptOutcomes) {
    this.swarm = swarm;
    this.suiteName = suiteName;
    this.startTime = startTime;
    this.outcomesByScriptIndex = scriptOutcomes;
  }

  /**
   * Gets the outcomes relating to a given statement.
   */
  public List<Outcome> outcomesFor(MutantSwarmScript script, MutantSwarmStatement statement) {
    
    Key key = new Key(script.getIndex(), statement.getIndex());
    List<Outcome> outcomes = outcomesByScriptIndex.get(key);
    if (outcomes != null){
      return outcomes;
    }
    return new ArrayList<>();
  }

  public boolean hasOutcomesFor(MutantSwarmScript script, MutantSwarmStatement statement) {
    Key key = new Key(script.getIndex(), statement.getIndex());
    return outcomesByScriptIndex.containsKey(key);
  }

  /**
   * Gets the swarm to which these results belong.
   */
  public Swarm getSwarm() {
    return swarm;
  }

  public String getSuiteName() {
    return suiteName;
  }

  public String getStartTime() {
    return startTime;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((outcomesByScriptIndex == null) ? 0 : outcomesByScriptIndex.hashCode());
    result = prime * result + ((suiteName == null) ? 0 : suiteName.hashCode());
    result = prime * result + ((swarm == null) ? 0 : swarm.hashCode());
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
    SwarmResults other = (SwarmResults) obj;
    if (outcomesByScriptIndex == null) {
      if (other.outcomesByScriptIndex != null)
        return false;
    } else if (!outcomesByScriptIndex.equals(other.outcomesByScriptIndex))
      return false;
    if (suiteName == null) {
      if (other.suiteName != null)
        return false;
    } else if (!suiteName.equals(other.suiteName))
      return false;
    if (swarm == null) {
      if (other.swarm != null)
        return false;
    } else if (!swarm.equals(other.swarm))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SwarmResults [swarm="
        + swarm
        + ", suiteName="
        + suiteName
        + ", outcomesByScriptIndex="
        + outcomesByScriptIndex
        + "]";
  }

}
