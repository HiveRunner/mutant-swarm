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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.hiverunner.HiveRunnerRule;
import com.klarna.hiverunner.StandaloneHiveRunner;

import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.report.ReportGenerator;

public class MutantSwarmRunner extends StandaloneHiveRunner {

  private static final Logger log = LoggerFactory.getLogger(MutantSwarmRunner.class);
  private MutantSwarmRule mutantSwarmRule;

  public MutantSwarmRunner(Class<?> clazz) throws InitializationError {
    super(clazz);
  }

  @Override
  public void run(final RunNotifier notifier) {
    EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
    testNotifier.fireTestStarted();
    try {
      log.debug("Evaluating statement");
      Statement statement = classBlock(notifier);
      statement.evaluate();
    } catch (AssumptionViolatedException ave) {
      testNotifier.addFailedAssumption(ave);
    } catch (StoppedByUserException sbue) {
      throw sbue;
    } catch (Throwable t) {
      testNotifier.addFailure(t);
    } finally {
      try {
        SwarmResults swarmResults = mutantSwarmRule.getSwarmResults();
        if (swarmResults != null) {
          log.debug("Finished testing. Generating report.");
          new ReportGenerator(swarmResults).generate();
        } else {
          log.debug("Cannot generate report");
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      testNotifier.fireTestFinished();
    }
  }

  @Override
  protected List<TestRule> getTestRules(Object target) {
    log.debug("Setting up test rules");
    List<TestRule> rules = new ArrayList<>(super.getTestRules(target));
    HiveRunnerRule hiveRunnerRule = getHiveRunnerRule(rules);
    mutantSwarmRule = new MutantSwarmRule(hiveRunnerRule, getHiveRunnerConfig().getCommandShellEmulator());

    /*
     * The rules are executed in reverse order to how they are added. The first rule on the list to be executed is the
     * hive runner config. The MutantSwarmRule is added after this rule so that everything but the config is wrapped and
     * repeated multiple times.
     */
    rules.add(rules.size() - 1, mutantSwarmRule);
    return rules;
  }

  private HiveRunnerRule getHiveRunnerRule(List<TestRule> rules) {
    HiveRunnerRule hiveRunnerRule = null;
    for (TestRule rule : rules) {
      if (rule instanceof HiveRunnerRule) {
        hiveRunnerRule = (HiveRunnerRule) rule;
      }
    }
    return hiveRunnerRule;
  }

}
