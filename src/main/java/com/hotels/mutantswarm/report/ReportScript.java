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

import static java.util.Collections.unmodifiableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmStatement;

class ReportScript implements MutantSwarmScript {

  private final LineFactory lineFactory;
  private final MutantSwarmScript delegate;
  private final List<MutantSwarmStatement> reportStatements;
  private int totalMutations = 0;
  private int totalMutationsKilled = 0;

  ReportScript(MutantSwarmScript delegate, LineFactory lineFactory) {
    this.delegate = delegate;
    this.lineFactory = lineFactory;
    this.reportStatements = setStatements();
  }

  public int getIndex() {
    return delegate.getIndex();
  }

  public Path getPath() {
    return delegate.getPath();
  }

  public String getSql() {
    return delegate.getSql();
  }

  public String getFileName() {
    return delegate.getFileName();
  }

  public int getTotalMutations() {
    return totalMutations;
  }

  public int getTotalMutationsKilled() {
    return totalMutationsKilled;
  }

  List<MutantSwarmStatement> setStatements() {
    Map<Integer, List<Line>> statementLines = lineFactory.buildLinesByStatementIndex(delegate);
    List<MutantSwarmStatement> statements = delegate.getStatements();
    List<MutantSwarmStatement> reportStatements = new ArrayList<>(statements.size());
    
    for (MutantSwarmStatement statement : statements) {
      List<Line> lines = statementLines.get(statement.getIndex());

      for (Line line : lines) {
        totalMutations += line.getMutationCount();
        totalMutationsKilled += line.getKilledCount();
      }
      reportStatements.add(new ReportStatement(statement, lines));
    }
    return unmodifiableList(reportStatements);
  }

  public List<MutantSwarmStatement> getStatements() {
    return unmodifiableList(reportStatements);
  }

}
