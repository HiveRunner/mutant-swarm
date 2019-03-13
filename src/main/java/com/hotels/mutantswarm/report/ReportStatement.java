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

import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import com.hotels.mutantswarm.model.MutantSwarmStatement;

class ReportStatement implements MutantSwarmStatement {

  private final MutantSwarmStatement delegate;
  private final List<Line> lines;
  
  ReportStatement(MutantSwarmStatement delegate, List<Line> lines) {
    this.delegate = delegate;
    this.lines = unmodifiableList(lines);
  }

  public int getIndex() {
    return delegate.getIndex();
  }

  public String getSql() {
    return delegate.getSql();
  }

  public List<CommonToken> getTokens() {
    return delegate.getTokens();
  }

  public ASTNode getTree() {
    return delegate.getTree();
  }

  public List<Line> getLines() {
    return lines;
  }
  
}
