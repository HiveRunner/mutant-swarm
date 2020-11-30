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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.hotels.mutantswarm.MutantSwarmCore.ExecutionContext;
import com.hotels.mutantswarm.exec.MutantState;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.exec.SwarmResults.SwarmResultsBuilder;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;
import com.klarna.hiverunner.builder.Script;
import com.klarna.hiverunner.builder.Statement;
import com.klarna.hiverunner.sql.cli.CommandShellEmulator;
import com.klarna.hiverunner.sql.split.StatementSplitter;
import org.slf4j.Logger;

public class MutantSwarmCore {
  
  public MutantSwarmSource setUpScripts(List<? extends Script> scriptsUnderTest, Logger log, CommandShellEmulator emulator ) {
    log.debug("Setting up scripts");
    List<MutantSwarmScript> scripts = new ArrayList<>();

    MutantSwarmStatement.Factory statementFactory = new MutantSwarmStatement.Factory();

    for (int i = 0; i < scriptsUnderTest.size(); i++) {
      Script testScript = scriptsUnderTest.get(i);
      List<Statement> scriptStatements = new StatementSplitter(emulator).split(testScript.getSql());

      List<MutantSwarmStatement> statements = new ArrayList<>();
      for (int j = 0; j < scriptStatements.size(); j++) {
        String statementText = scriptStatements.get(j).getSql();
        MutantSwarmStatement statement = statementFactory.newInstance(i, j, statementText);
        statements.add(statement);
      }
      MutantSwarmScript script = new MutantSwarmScript.Impl(i, testScript.getPath(), statements);
      scripts.add(script);
    }
    return new MutantSwarmSource.Impl(scripts);
  }
  
  SwarmResults getSwarmResults(ExecutionContext context) {
    if (context == null){
      return null;
    }
    return context.swarmResultBuilder.build();
  }
  
  public static class ExecutionContext {
    final Swarm swarm;
    final SwarmResultsBuilder swarmResultBuilder;

    ExecutionContext(Swarm swarm, SwarmResultsBuilder swarmResultBuilder) {
      this.swarm = swarm;
      this.swarmResultBuilder = swarmResultBuilder;
    }

    void addTestOutcome(String testName, Mutant mutant, Mutation mutation, MutantState state) {
      swarmResultBuilder.addTestOutcome(testName, mutant, mutation, state);
    }

    MutantSwarmSource getSource() {
      return swarm.getSource();
    }
  }
}
