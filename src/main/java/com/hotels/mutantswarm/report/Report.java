/**
 * Copyright (C) 2018-2019 Expedia Inc.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.stringtemplate.v4.ST;

import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.model.MutantSwarmScript;

/**
 * Generates the mutation report.
 */
class Report {

  private final LineFactory lineFactory;
  private final SwarmResults results;
  private final ST template;
  private final String cssFileName;

  Report(SwarmResults results, LineFactory lineFactory, ST template, String cssFileName) {
    this.results = results;
    this.lineFactory = lineFactory;
    this.template = template;
    this.cssFileName = cssFileName;
  }

  void writeTo(OutputStream outputStream) throws IOException {
    List<ReportScript> reportScripts = new ArrayList<>();
    int totalMutations = 0;
    int totalMutationsKilled = 0;
    
    for (MutantSwarmScript script : results.getSwarm().getSource().getScripts()) {
      ReportScript reportScript = new ReportScript(script, lineFactory);
      totalMutations += reportScript.getTotalMutations();
      totalMutationsKilled += reportScript.getTotalMutationsKilled();
      reportScripts.add(reportScript);
    }

    int mutantsKilledPercentage = (int) (((float) totalMutationsKilled / (float) totalMutations) * 100.0);
    
    template.add("cssFile", cssFileName);
    template.add("scripts", reportScripts);
    template.add("results", results);
    template.add("mutantsKilledPercentage", mutantsKilledPercentage);

    outputStream.write(template.render().getBytes());
  }

}
