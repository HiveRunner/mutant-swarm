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
package com.hotels.mutantswarm.exec;

import static java.util.Collections.unmodifiableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;

/**
 * Given an original source and a mutant, generates a mutated source that can be run through the test suite.
 */
public class MutatedSourceFactory {

  public MutatedSource newMutatedSource(final MutantSwarmSource delegate, Mutant mutant){
    Mutation mutation = mutant.getMutator().apply(mutant.getGene());
    
    List<MutantSwarmScript> newScripts = new ArrayList<>(delegate.getScripts().size());
    for (MutantSwarmScript script : delegate.getScripts()) {
      if (script.getIndex() == mutant.getScriptIndex()) {
        newScripts.add(new MutatedScript(script, mutant, mutation));
      } else {
        newScripts.add(script);
      }
    }
    return new MutatedSource(delegate, mutant, newScripts);
  }

  /**
   * Mutated version of the source. Decorator pattern
   */
  public static class MutatedSource implements MutantSwarmSource {

    private List<MutantSwarmScript> scripts;
    private Mutation mutation;

    MutatedSource(MutantSwarmSource delegate, Mutant mutant, List<MutantSwarmScript> scripts) {
      Mutation mutation = mutant.getMutator().apply(mutant.getGene());
      this.mutation = mutation;
      this.scripts = unmodifiableList(scripts);
    }

    @Override
    public List<MutantSwarmScript> getScripts() {
      return scripts;
    }

    public Mutation getMutation(){
      return mutation;
    }
  }

  /**
   * Mutated version of the script. Decorator pattern
   */
  public static class MutatedScript implements MutantSwarmScript {

    private final MutantSwarmScript delegate;
    private final List<MutantSwarmStatement> statements;

    private MutatedScript(MutantSwarmScript delegate, Mutant mutant, Mutation mutation) {
      this.delegate = delegate;
      statements = runMutation(delegate, mutant, mutation);
    }

    private List<MutantSwarmStatement> runMutation(MutantSwarmScript delegate, Mutant mutant, Mutation mutation) {
      List<MutantSwarmStatement> newStatements = new ArrayList<>(delegate.getStatements().size());
      for (MutantSwarmStatement statement : delegate.getStatements()) {
        if (statement.getIndex() == mutant.getStatementIndex()) {
          newStatements.add(new MutatedStatement(statement, mutation));
        } else {
          newStatements.add(statement);
        }
      }
      return unmodifiableList(newStatements);
    }

    @Override
    public int getIndex() {
      return delegate.getIndex();
    }

    @Override
    public String getFileName() {
      return delegate.getFileName();
    }

    @Override
    public List<MutantSwarmStatement> getStatements() {
      return statements;
    }

    @Override
    public String getSql() {
      String sql = "";
      for (MutantSwarmStatement statement : statements) {
        sql += statement.getSql() + ";\n";
      }
      return sql;
    }

    @Override
    public Path getPath() {
      return delegate.getPath();
    }
  }

  /**
   * Mutated version of the statement. Decorator pattern
   */
  public static class MutatedStatement implements MutantSwarmStatement {

    private final MutantSwarmStatement delegate;
    private final Mutation mutation;

    private MutatedStatement(MutantSwarmStatement delegate, Mutation mutation) {
      this.delegate = delegate;
      this.mutation = mutation;
    }

    @Override
    public String getSql() {
      String replacement = mutation.getReplacementText();
      int startIndex = mutation.getSplice().getStartIndex();
      int endIndex = mutation.getSplice().getStopIndex() + 1;

      String originalText = delegate.getSql();
      String newSql = originalText.substring(0, startIndex)
          + replacement
          + originalText.substring(endIndex, originalText.length());

      return newSql;
    }

    @Override
    public int getIndex() {
      return delegate.getIndex();
    }

    @Override
    public ASTNode getTree() {
      return delegate.getTree();
    }

    @Override
    public List<CommonToken> getTokens() {
      return delegate.getTokens();
    }

  }

}
