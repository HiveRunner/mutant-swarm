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
package com.hotels.mutantswarm;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.hiverunner.HiveRunnerRule;
import com.klarna.hiverunner.sql.cli.CommandShellEmulator;

import com.hotels.mutantswarm.MutantSwarmCore.ExecutionContext;
import com.hotels.mutantswarm.exec.MutantState;
import com.hotels.mutantswarm.exec.MutatedSourceFactory.MutatedSource;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.exec.SwarmResults.SwarmResultsBuilder;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;

/** A rule to run a standard HR test, and then once again for each mutant. */
class MutantSwarmRule implements TestRule {

  private static final Logger log = LoggerFactory.getLogger(MutantSwarmRule.class);
  private static AtomicReference<ExecutionContext> contextRef = new AtomicReference<>();

  private final HiveRunnerRule hiveRunnerRule;
  private CommandShellEmulator emulator;
  private MutantSwarmCore core = new MutantSwarmCore();

  MutantSwarmRule(HiveRunnerRule hiveRunnerRule, CommandShellEmulator emulator) {
    this.hiveRunnerRule = hiveRunnerRule;
    this.emulator = emulator;
  }
  
  SwarmResults getSwarmResults() {
    return core.getSwarmResults(contextRef.get());
  }

  @Override
  public org.junit.runners.model.Statement apply(org.junit.runners.model.Statement base, Description description) {
    log.debug("mutant swarm apply method");
    String suiteName = description.getClassName();
    String testName = description.getMethodName();
    return new MutantSwarmJUnitStatement(base, suiteName, testName);
  }

  class MutantSwarmJUnitStatement extends org.junit.runners.model.Statement {
    private final org.junit.runners.model.Statement base;
    private final String suiteName;
    private final String testName;

    MutantSwarmJUnitStatement(org.junit.runners.model.Statement base, String suiteName, String testName) {
      this.base = base;
      this.suiteName = suiteName;
      this.testName = testName;
    }

    @Override
    public void evaluate() throws Throwable {
      log.debug("Running regular test");
      base.evaluate();

      if (contextRef.get() == null) {
        Swarm swarm = core.generateSwarm(hiveRunnerRule.getScriptsUnderTest(), emulator);
        SwarmResultsBuilder swarmResultBuilder = new SwarmResultsBuilder(swarm, suiteName);
        contextRef.compareAndSet(null, new ExecutionContext(swarm, swarmResultBuilder));
      }

      ExecutionContext context = contextRef.get();
      for (Mutant mutant : contextRef.get().swarm.getMutants()) {
        MutatedSource mutatedSource = core.mutateSource(context.getSource(), mutant);
        hiveRunnerRule.setScriptsUnderTest(mutatedSource.getScripts());
        try {
          base.evaluate();
          log.debug("Mutant survived - bad");
          context.addTestOutcome(testName, mutant, mutatedSource.getMutation(), MutantState.SURVIVED);
        } catch (AssertionError e) {
          log.debug("Mutant killed - good. " + e);
          context.addTestOutcome(testName, mutant, mutatedSource.getMutation(), MutantState.KILLED);
        }
      }
    }
  }

}
