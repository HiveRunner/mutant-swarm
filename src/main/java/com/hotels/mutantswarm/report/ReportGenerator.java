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
package com.hotels.mutantswarm.report;

import static com.hotels.mutantswarm.report.AssetUtil.readResourceAsString;
import static com.hotels.mutantswarm.report.AssetUtil.writeResourceToFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import com.hotels.mutantswarm.exec.SwarmResults;

public class ReportGenerator {

  private static final Logger log = LoggerFactory.getLogger(ReportGenerator.class);

  private static final String TARGET_FOLDER = "target";
  private static final String REPORT_FOLDER = "mutant-swarm-reports";
  private static final String REPORT_FILENAME_FORMAT = "%s_SWARM.html";
  private static final String CSS_FILENAME = "mutation_report.css";
  private static final String CSS_PATH = "/css/" + CSS_FILENAME;
  private static final String LOGO_FILENAME = "logo.png";
  private static final String LOGO_PATH = "/img/" + LOGO_FILENAME;
  private static final String REPORT_TEMPLATE = "/stl/mutation_report.st";

  private final SwarmResults results;
  private final String cssResourcePath;
  private final String cssFileName;
  private final String logoResourcePath;
  private final String logoFileName;
  private final String templateResourcePath;
  private final String reportFolder;
  private final String targetFolder;
  private final String reportFileNameformat;

  public ReportGenerator(SwarmResults results) {
    this(results, CSS_PATH, CSS_FILENAME, LOGO_PATH, LOGO_FILENAME, REPORT_TEMPLATE, REPORT_FOLDER, TARGET_FOLDER,
        REPORT_FILENAME_FORMAT);
  }

  ReportGenerator(
      SwarmResults results,
      String cssResourcePath,
      String cssFileName,
      String logoResourcePath,
      String logoFileName,
      String templateResourcePath,
      String reportFolder,
      String targetFolder,
      String reportFileNameformat) {
    super();
    this.results = results;
    this.cssResourcePath = cssResourcePath;
    this.cssFileName = cssFileName;
    this.logoResourcePath = logoResourcePath;
    this.logoFileName = logoFileName;
    this.templateResourcePath = templateResourcePath;
    this.reportFolder = reportFolder;
    this.targetFolder = targetFolder;
    this.reportFileNameformat = reportFileNameformat;
  }

  public void generate() throws IOException {
    File reportFolder = resolveReportFolder();
    File reportFile = resolveReportFile(reportFolder);
    try (OutputStream outputStream = new FileOutputStream(reportFile)) {
      writeReport(outputStream);
      writeResourceToFile(cssResourcePath, new File(reportFolder, cssFileName));
      writeResourceToFile(logoResourcePath, new File(reportFolder, logoFileName));
    }
  }

  private File resolveReportFolder() {
    File folder = new File(targetFolder, reportFolder);
    folder.mkdirs();
    return folder;
  }

  private File resolveReportFile(File reportFolder) {
    String reportFileName = String.format(reportFileNameformat, results.getSuiteName());
    File reportFile = new File(reportFolder, reportFileName);
    return reportFile;
  }

  private void writeReport(OutputStream outputStream) throws IOException {
    log.debug("Running report");
    LineFactory lineFactory = new LineFactory(results);
    ST template = new ST(readResourceAsString(templateResourcePath));
    Report report = new Report(results, lineFactory, template, cssFileName);
    report.writeTo(outputStream);
  }

}
