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

import java.io.IOException;

import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
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
import com.hotels.mutantswarm.report.ReportGenerator;
import com.klarna.hiverunner.HiveShellContainer;
import com.klarna.hiverunner.annotations.HiveRunnerSetup;
import com.klarna.hiverunner.annotations.HiveSQL;
import com.klarna.hiverunner.builder.Script;
import com.klarna.hiverunner.builder.Statement;
import com.klarna.hiverunner.config.HiveRunnerConfig;
import com.klarna.hiverunner.sql.cli.CommandShellEmulator;
import com.klarna.hiverunner.sql.split.StatementSplitter;
import com.klarna.reflection.ReflectionUtils;

public class MutantSwarmExtension implements AfterAllCallback,TestWatcher, TestTemplateInvocationContextProvider, TestInstancePostProcessor, AfterEachCallback {

  private static final Logger log = LoggerFactory.getLogger(MutantSwarmExtension.class);
  private final HiveRunnerConfig config = new HiveRunnerConfig();
  private static AtomicReference<ExecutionContext> contextRef = new AtomicReference<>();

  public HiveShellContainer container;
  private CommandShellEmulator emulator;
  private List<Script> scriptsUnderTest = new ArrayList<Script>();
  private Path basedir;
  private MutantSwarmCore core = new MutantSwarmCore();
  private HiveRunnerConfig HiveRunnerConfig = new HiveRunnerConfig();
  private int testNumber = -1;
  private List<Mutant> mutants = new ArrayList<Mutant>();
  private List<MutatedSource> mutatedSources = new ArrayList<MutatedSource>();
  private ExecutionContext results = contextRef.get();
  private boolean firstTestPassed = true;

  public MutantSwarmExtension() {}
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    if (!context.getTestMethod().isPresent()) {
      return false;
    }
    return true;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    testNumber = -1;
    firstTestPassed = true;

    setFirstScripts(context);

    if (contextRef.get() == null) {
      Swarm swarm = generateSwarm();
      SwarmResultsBuilder swarmResultBuilder = new SwarmResultsBuilder(swarm, context.getRequiredTestClass().toString());
      contextRef.compareAndSet(null, new ExecutionContext(swarm, swarmResultBuilder));
    }

    mutants = contextRef.get().swarm.getMutants();
    ExecutionContext executionContext = contextRef.get();
    for (Mutant mutant : contextRef.get().swarm.getMutants()) {
      MutatedSource mutatedSource = mutateSource(executionContext.getSource(), mutant);
      mutatedSources.add(mutatedSource);
    }

    results = contextRef.get();

    // Here we specify the number of times a test should be repeated, but the scripts will be mutated accordingly for each individual test in the postProcessTestInstance method
    return IntStream.rangeClosed(1,mutants.size()+1).mapToObj(repitition -> new MutantSwarmTestTemplate());
  }
  
  @Override
  public void postProcessTestInstance(Object target, ExtensionContext extensionContext) throws Exception {
    testNumber++;

    //The first test per swarm should run normally and without mutations
    if(testNumber!=0) {
      scriptsUnderTest.clear();
      MutatedSource mutatedSource = mutatedSources.get(testNumber-1);
      List<MutantSwarmScript> MScripts = mutatedSource.getScripts();

      for (Script s : MScripts) {
        scriptsUnderTest.add(s);
      }
    }

    setupConfig(target);
    try {
      basedir = Files.createTempDirectory("hiverunner_test");
      container = createHiveServerContainer(scriptsUnderTest, target, basedir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    scriptsUnderTest = container.getScriptsUnderTest();

  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    if(testNumber!=0) {
      if(firstTestPassed==true) {
        log.info("Mutant survived - bad");
        results.addTestOutcome(context.getDisplayName(), mutants.get(testNumber-1), mutatedSources.get(testNumber-1).getMutation(), MutantState.SURVIVED);
      } 
    }
    else {
      firstTestPassed = true;
    }
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    if(testNumber!=0) {
      if(firstTestPassed==true) {
        log.info("Mutant killed - good. "+cause.toString());
        results.addTestOutcome(context.getRequiredTestMethod().toString(), mutants.get(testNumber-1), mutatedSources.get(testNumber-1).getMutation(), MutantState.KILLED  );
      }
    }
    else {
      firstTestPassed = false;
      //TODO: if the first test does not pass, then we should not run it again with the mutations because it's useless
    }
  }
  
  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    SwarmResults swarmResults = getSwarmResults();
    log.debug("Finished testing. Generating report.");
    new ReportGenerator(swarmResults).generate();
  }
  
  //This method sets the scripts for the first time to generate the swarm
  public void setFirstScripts(ExtensionContext context) {
    try {
      Set<Field> fields = ReflectionUtils.getAllFields(context.getRequiredTestClass(), withAnnotation(HiveSQL.class));

      scriptsUnderTest.clear();

      Preconditions.checkState(fields.size() == 1, "Exact one field should to be annotated with @HiveSQL");
      Field field = fields.iterator().next();
      List<Path> scriptPaths = new ArrayList<>();
      HiveSQL annotation = field.getAnnotation(HiveSQL.class);

      for (String scriptFilePath : annotation.files()) {
        Path file = Paths.get(Resources.getResource(scriptFilePath).toURI());
        assertFileExists(file);
        scriptPaths.add(file);
      }

      Charset charset = annotation.encoding().equals("") ?
          Charset.defaultCharset() : Charset.forName(annotation.encoding());

          int index = 0;
          for (Path path : scriptPaths) {
            Preconditions.checkState(Files.exists(path), "File %s does not exist", path);
            try {
              String sqlText = new String(Files.readAllBytes(path), charset);
              //TODO: either make the HiveRunnerScript constructor public, or make our own MutantSwarmScript that could work here
              Script s = new HiveRunnerScript(index++, path, sqlText);
              scriptsUnderTest.add(s);
            } catch (IOException e) {
              throw new IllegalArgumentException("Failed to load script file '" + path + "'");
            }
          }
    }
    catch (Throwable t) {
      throw new IllegalArgumentException("Failed to init field annotated with @HiveSQL: " + t.getMessage(), t);
    } 
  }

  private void assertFileExists(Path file) {
    Preconditions.checkState(Files.exists(file), "File " + file + " does not exist");
  }

  //These methods are the ones to set up the tests, they are copy pasted from HiveRunner
  //TODO: find a way to access these methods from HiveRunner instead of copying everything
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

  //These are all the methods used to generate a swarm
  //TODO: Add this in to a core class and avoid repeating code here and in the MutantSwarmRule
  private MutantSwarmSource setUpScripts() {
    log.debug("Setting up scripts");
    List<MutantSwarmScript> scripts = new ArrayList<>();

    MutantSwarmStatement.Factory statementFactory = new MutantSwarmStatement.Factory();

    for (int i = 0; i < scriptsUnderTest.size(); i++) {
      Script testScript = scriptsUnderTest.get(i);
      emulator = HiveRunnerConfig.getCommandShellEmulator();

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