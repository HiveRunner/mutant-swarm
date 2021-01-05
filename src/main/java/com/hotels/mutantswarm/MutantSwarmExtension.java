/**
 * Copyright (C) 2018-2021 Expedia, Inc.
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

import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.klarna.hiverunner.HiveRunnerExtension;
import com.klarna.hiverunner.HiveShellContainer;
import com.klarna.hiverunner.annotations.HiveSQL;
import com.klarna.hiverunner.builder.HiveShellBuilder;
import com.klarna.hiverunner.builder.Script;
import com.klarna.hiverunner.config.HiveRunnerConfig;
import com.klarna.reflection.ReflectionUtils;

import com.hotels.mutantswarm.MutantSwarmCore.ExecutionContext;
import com.hotels.mutantswarm.exec.MutantState;
import com.hotels.mutantswarm.exec.MutatedSourceFactory.MutatedSource;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.exec.SwarmResults.SwarmResultsBuilder;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;
import com.hotels.mutantswarm.report.ReportGenerator;

public class MutantSwarmExtension extends HiveRunnerExtension implements AfterAllCallback, TestWatcher,
    TestTemplateInvocationContextProvider, TestInstancePostProcessor, AfterEachCallback {

  private static final Logger log = LoggerFactory.getLogger(MutantSwarmExtension.class);
  private static AtomicReference<ExecutionContext> contextRef = new AtomicReference<>();

  public HiveShellContainer container;
  private HiveRunnerConfig HiveRunnerConfig = new HiveRunnerConfig();
  private int testNumber = -1;
  private List<Mutant> mutants = new ArrayList<Mutant>();
  private List<MutatedSource> mutatedSources = new ArrayList<MutatedSource>();
  private ExecutionContext resultContext = contextRef.get();
  private boolean firstTestPassed = true;
  private MutantSwarmCore core = new MutantSwarmCore();

  public MutantSwarmExtension() {}

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return context.getTestMethod().isPresent();
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    testNumber = -1;
    firstTestPassed = true;
    setFirstScripts(context);

    if (contextRef.get() == null) {
      Swarm swarm = core.generateSwarm(scriptsUnderTest, HiveRunnerConfig.getCommandShellEmulator());
      SwarmResultsBuilder swarmResultBuilder = new SwarmResultsBuilder(swarm,
          context.getRequiredTestClass().toString());
      contextRef.compareAndSet(null, new ExecutionContext(swarm, swarmResultBuilder));
    }

    ExecutionContext executionContext = contextRef.get();
    mutants = executionContext.swarm.getMutants();
    for (Mutant mutant : mutants) {
      MutatedSource mutatedSource = core.mutateSource(executionContext.getSource(), mutant);
      mutatedSources.add(mutatedSource);
    }

    resultContext = executionContext;

    // Here we specify the number of times a test should be repeated, but the scripts will be mutated accordingly for
    // each individual test in the postProcessTestInstance method
    return IntStream.rangeClosed(1, mutants.size() + 1).mapToObj(repetition -> new MutantSwarmTestTemplate());
  }

  @Override
  public void postProcessTestInstance(Object target, ExtensionContext extensionContext) {
    testNumber++;

    // The first test per swarm should run normally and without mutations
    if (testNumber != 0) {
      scriptsUnderTest.clear();
      MutatedSource mutatedSource = mutatedSources.get(testNumber - 1);
      List<MutantSwarmScript> mutatedScripts = mutatedSource.getScripts();

      for (Script script : mutatedScripts) {
        scriptsUnderTest.add(script);
      }
    }

    super.postProcessTestInstance(target, extensionContext);
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    if (testNumber != 0) {
      if (firstTestPassed == true) {
        log.info("Mutant survived - bad");
        resultContext
            .addTestOutcome(context.getDisplayName(), mutants.get(testNumber - 1),
                mutatedSources.get(testNumber - 1).getMutation(), MutantState.SURVIVED);
      }
    } else {
      firstTestPassed = true;
    }
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    if (testNumber != 0) {
      if (firstTestPassed == true) {
        log.info("Mutant killed - good. " + cause.toString());
        resultContext
            .addTestOutcome(context.getRequiredTestMethod().toString(), mutants.get(testNumber - 1),
                mutatedSources.get(testNumber - 1).getMutation(), MutantState.KILLED);
      }
    } else {
      firstTestPassed = false;
      // TODO: if the first test does not pass, then we should not run it again with the mutations because it's useless
    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    SwarmResults swarmResults = core.getSwarmResults(resultContext);
    log.debug("Finished testing. Generating report.");
    new ReportGenerator(swarmResults).generate();
  }

  // This method sets the scripts for the first time to generate the swarm
  private void setFirstScripts(ExtensionContext context) {
    HiveShellBuilder hiveShellBuilder = new HiveShellBuilder();
    try {
      scriptsUnderTest.clear();
      Set<Field> fields = ReflectionUtils.getAllFields(context.getRequiredTestClass(), withAnnotation(HiveSQL.class));
      Preconditions.checkState(fields.size() == 1, "Exactly one field should be annotated with @HiveSQL");
      Field field = fields.iterator().next();
      HiveSQL annotation = field.getAnnotation(HiveSQL.class);
      List<Path> scriptPaths = getScriptPaths(annotation);
      Charset charset = annotation.encoding().equals("") ? Charset.defaultCharset() : Charset.forName(annotation.encoding());
      scriptsUnderTest = hiveShellBuilder.fromScriptPaths(scriptPaths, charset);
    } catch (Throwable t) {
      throw new IllegalArgumentException("Failed to init field annotated with @HiveSQL: " + t.getMessage(), t);
    }
  }

}
