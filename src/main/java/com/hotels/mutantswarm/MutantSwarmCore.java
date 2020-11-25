package com.hotels.mutantswarm;

import static org.reflections.ReflectionUtils.withAnnotation;
import static org.reflections.ReflectionUtils.withType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.klarna.hiverunner.HiveServerContainer;
import com.klarna.hiverunner.HiveServerContext;
import com.klarna.hiverunner.HiveShell;
import com.klarna.hiverunner.HiveShellContainer;
import com.klarna.hiverunner.StandaloneHiveServerContext;
import com.klarna.hiverunner.annotations.HiveProperties;
import com.klarna.hiverunner.annotations.HiveResource;
import com.klarna.hiverunner.annotations.HiveSQL;
import com.klarna.hiverunner.annotations.HiveSetupScript;
import com.klarna.hiverunner.builder.HiveShellBuilder;
import com.klarna.hiverunner.builder.Script;
import com.klarna.hiverunner.config.HiveRunnerConfig;
import com.klarna.reflection.ReflectionUtils;

public class MutantSwarmCore {
  
  //All the methods in this class are from HiveRunner
  public HiveShellContainer createHiveServerContainer (
      List<? extends Script> scripts,
      Object testCase,
      Path basedir,
      HiveRunnerConfig config) throws IOException{

    HiveServerContext context = new StandaloneHiveServerContext(basedir, config);

    return buildShell(scripts, testCase, config, context);
  }

  private HiveShellContainer buildShell(List<? extends Script> scripts, Object testCase, HiveRunnerConfig config,
      HiveServerContext context) throws IOException {
    HiveServerContainer hiveTestHarness = new HiveServerContainer(context);

    HiveShellBuilder hiveShellBuilder = new HiveShellBuilder();
    hiveShellBuilder.setCommandShellEmulation(config.getCommandShellEmulator());

    HiveShellField shellSetter = loadScriptUnderTest(testCase, hiveShellBuilder);
    if (scripts != null) {
      hiveShellBuilder.overrideScriptsUnderTest(scripts);
    }

    hiveShellBuilder.setHiveServerContainer(hiveTestHarness);

    loadAnnotatedResources(testCase, hiveShellBuilder);

    loadAnnotatedProperties(testCase, hiveShellBuilder);

    loadAnnotatedSetupScripts(testCase, hiveShellBuilder);

    // Build shell
    HiveShellContainer shell = hiveShellBuilder.buildShell();

    // Set shell
    shellSetter.setShell(shell);

    if (shellSetter.isAutoStart()) {
      shell.start();
    }
    return shell;
  }
  
  private void loadAnnotatedResources(Object testCase, HiveShellBuilder workFlowBuilder) throws IOException {
    Set<Field> fields = ReflectionUtils.getAllFields(testCase.getClass(), withAnnotation(HiveResource.class));

    for (Field resourceField : fields) {

      HiveResource annotation = resourceField.getAnnotation(HiveResource.class);
      String targetFile = annotation.targetFile();

      if (ReflectionUtils.isOfType(resourceField, String.class)) {
        String data = ReflectionUtils.getFieldValue(testCase, resourceField.getName(), String.class);
        workFlowBuilder.addResource(targetFile, data);
      } else if (ReflectionUtils.isOfType(resourceField, File.class) ||
          ReflectionUtils.isOfType(resourceField, Path.class)) {
        Path dataFile = getMandatoryPathFromField(testCase, resourceField);
        workFlowBuilder.addResource(targetFile, dataFile);
      } else {
        throw new IllegalArgumentException(
            "Fields annotated with @HiveResource currently only supports field type String, File or Path");
      }
    }
  }
  private void loadAnnotatedProperties(Object testCase, HiveShellBuilder workFlowBuilder) {
    for (Field hivePropertyField : ReflectionUtils.getAllFields(testCase.getClass(),
        withAnnotation(HiveProperties.class))) {
      Preconditions.checkState(ReflectionUtils.isOfType(hivePropertyField, Map.class),
          "Field annotated with @HiveProperties should be of type Map<String, String>");
      workFlowBuilder.putAllProperties(
          ReflectionUtils.getFieldValue(testCase, hivePropertyField.getName(), Map.class));
    }
  }
  
  private void loadAnnotatedSetupScripts(Object testCase, HiveShellBuilder workFlowBuilder) {
    Set<Field> setupScriptFields = ReflectionUtils.getAllFields(testCase.getClass(),
        withAnnotation(HiveSetupScript.class));

    for (Field setupScriptField : setupScriptFields) {
      if (ReflectionUtils.isOfType(setupScriptField, String.class)) {
        String script = ReflectionUtils.getFieldValue(testCase, setupScriptField.getName(), String.class);
        workFlowBuilder.addSetupScript(script);
      } else if (ReflectionUtils.isOfType(setupScriptField, File.class) ||
          ReflectionUtils.isOfType(setupScriptField, Path.class)) {
        Path path = getMandatoryPathFromField(testCase, setupScriptField);
        workFlowBuilder.addSetupScript(readAll(path));
      } else {
        throw new IllegalArgumentException(
            "Field annotated with @HiveSetupScript currently only supports type String, File and Path");
      }
    }
  }
  
  private static String readAll(Path path) {
    try {
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read " + path + ": " + e.getMessage(), e);
    }
  }
  
  private Path getMandatoryPathFromField(Object testCase, Field resourceField) {
    Path path;
    if (ReflectionUtils.isOfType(resourceField, File.class)) {
      File dataFile = ReflectionUtils.getFieldValue(testCase, resourceField.getName(), File.class);
      path = Paths.get(dataFile.toURI());
    } else if (ReflectionUtils.isOfType(resourceField, Path.class)) {
      path = ReflectionUtils.getFieldValue(testCase, resourceField.getName(), Path.class);
    } else {
      throw new IllegalArgumentException(
          "Only Path or File type is allowed on annotated field " + resourceField);
    }

    Preconditions.checkArgument(Files.exists(path), "File %s does not exist", path);
    return path;
  }
  
  private HiveShellField loadScriptUnderTest(Object testCaseInstance, HiveShellBuilder hiveShellBuilder) {
    try {
      Set<Field> fields = ReflectionUtils.getAllFields(testCaseInstance.getClass(), withAnnotation(HiveSQL.class));

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

      boolean isAutoStart = annotation.autoStart();

      hiveShellBuilder.setScriptsUnderTest(scriptPaths, charset);

      return new HiveShellField() {
        @Override
        public void setShell(HiveShell shell) {
          ReflectionUtils.setField(testCaseInstance, field.getName(), shell);
        }

        @Override
        public boolean isAutoStart() {
          return isAutoStart;
        }
      };
    } catch (Throwable t) {
      throw new IllegalArgumentException("Failed to init field annotated with @HiveSQL: " + t.getMessage(), t);
    }
  }
  
  private void assertFileExists(Path file) {
    Preconditions.checkState(Files.exists(file), "File " + file + " does not exist");
  }
  
  interface HiveShellField {

    void setShell(HiveShell shell);

    boolean isAutoStart();
  }
  
}
