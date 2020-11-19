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

import static org.reflections.ReflectionUtils.withAnnotation;
import static org.reflections.ReflectionUtils.withType;

import java.io.File;
import java.io.IOException;

import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.io.Resources;
import com.hotels.mutantswarm.exec.MutantState;
import com.hotels.mutantswarm.exec.MutatedSourceFactory;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.exec.MutatedSourceFactory.MutatedSource;
import com.hotels.mutantswarm.exec.SwarmResults.SwarmResultsBuilder;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.CompositeMutantFactory;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;
import com.hotels.mutantswarm.plan.Swarm.SwarmFactory;
import com.klarna.hiverunner.HiveRunnerExtension;
import com.klarna.hiverunner.HiveShellContainer;
import com.klarna.hiverunner.annotations.HiveRunnerSetup;
import com.klarna.hiverunner.builder.Script;
import com.klarna.hiverunner.builder.Statement;
import com.klarna.hiverunner.config.HiveRunnerConfig;
import com.klarna.hiverunner.sql.cli.CommandShellEmulator;
import com.klarna.hiverunner.sql.split.StatementSplitter;
import com.klarna.reflection.ReflectionUtils;

public class MutantSwarmTestExtension implements TestTemplateInvocationContextProvider, TestInstancePostProcessor, AfterEachCallback {
  
  private static final Logger log = LoggerFactory.getLogger(MutantSwarmTestExtension.class);
  private final HiveRunnerConfig config = new HiveRunnerConfig();
  private static AtomicReference<ExecutionContext> contextRef = new AtomicReference<>();
  
  public HiveShellContainer container;
  private CommandShellEmulator emulator;
  private List<? extends Script> scriptsUnderTest;
  private Path basedir;
  private MutantSwarmCore core = new MutantSwarmCore();
  
  public MutantSwarmTestExtension() {}

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    //This will need to be filled in in the future, for now it always returns true
    return true;
  }
  
  //This is where the magic should happen and all the tests would be repeated, right now it is set to repeat 
  //3 times each test
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    Method templateMethod = context.getRequiredTestMethod();
    
//    if (contextRef.get() == null) {
//      Swarm swarm = generateSwarm();
//
//    }

    return IntStream.rangeClosed(1,3).mapToObj(repitition -> new MutantSwarmTestTemplate());
  
  }
  
  
  
  
  
  
  
  //Copy pasted from HiveRunner
  @Override
  public void postProcessTestInstance(Object target, ExtensionContext extensionContext) {
    setupConfig(target);
    try {
      basedir = Files.createTempDirectory("hiverunner_test");
      container = createHiveServerContainer(scriptsUnderTest, target, basedir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    scriptsUnderTest = container.getScriptsUnderTest();
  }
  
  private HiveShellContainer createHiveServerContainer(List<? extends Script> scripts, Object testCase, Path basedir)
      throws IOException {
    return core.createHiveServerContainer(scripts, testCase, basedir, config);
  }
  
  private void setupConfig(Object target) {
    Set<Field> fields = ReflectionUtils.getAllFields(target.getClass(),
        Predicates.and(
            withAnnotation(HiveRunnerSetup.class),
            withType(HiveRunnerConfig.class)));

    Preconditions.checkState(fields.size() <= 1,
        "Only one field of type HiveRunnerConfig should be annotated with @HiveRunnerSetup");

    if (!fields.isEmpty()) {
      config.override(ReflectionUtils
          .getFieldValue(target, fields.iterator().next().getName(), HiveRunnerConfig.class));
    }
  }
  
  private void tearDown(Object target) {
    if (container != null) {
      log.info("Tearing down {}", target.getClass());
      container.tearDown();
    }
    deleteTempFolder(basedir);
  }

  private void deleteTempFolder(Path directory) {
    try {
      FileUtils.deleteDirectory(directory.toFile());
    } catch (IOException e) {
      log.debug("Temporary folder was not deleted successfully: " + directory);
    }
  }
  
  @Override
  public void afterEach(ExtensionContext extensionContext) {
    tearDown(extensionContext.getRequiredTestInstance());
  }

  
  
  
  
  
  
  //These are all the methods in the MSRule, in the future we should put them in a core class
  private MutantSwarmSource setUpScripts() {
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

  private Swarm generateSwarm() {
    log.debug("Setting up mutants");
    MutantSwarmSource source = setUpScripts();
    SwarmFactory swarmFactory = new SwarmFactory(new CompositeMutantFactory());
    return swarmFactory.newInstance(source);
  }

  private MutatedSource mutateSource(MutantSwarmSource source, Mutant mutant) {
    MutatedSourceFactory mutatedSourceFactory = new MutatedSourceFactory();
    return mutatedSourceFactory.newMutatedSource(source, mutant);
  }

  static SwarmResults getSwarmResults() {
    if (contextRef.get() == null){
      return null;
    }
    return contextRef.get().swarmResultBuilder.build();
  }
  
  static class ExecutionContext {
    private final Swarm swarm;
    private final SwarmResultsBuilder swarmResultBuilder;

    private ExecutionContext(Swarm swarm, SwarmResultsBuilder swarmResultBuilder) {
      this.swarm = swarm;
      this.swarmResultBuilder = swarmResultBuilder;
    }

    private void addTestOutcome(String testName, Mutant mutant, Mutation mutation, MutantState state) {
      swarmResultBuilder.addTestOutcome(testName, mutant, mutation, state);
    }

    private MutantSwarmSource getSource() {
      return swarm.getSource();
    }
    
  }
  
}
