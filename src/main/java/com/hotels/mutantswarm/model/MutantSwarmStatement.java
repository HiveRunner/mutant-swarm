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
package com.hotels.mutantswarm.model;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.TokenRewriteStream;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

import com.google.common.annotations.VisibleForTesting;
import com.klarna.hiverunner.builder.Statement;

/**
 * Represents a SQL statement within a script, that forms part of the source under test. Incorporates a number of
 * parsable forms for the purposes of mutator resolution.
 */
public interface MutantSwarmStatement extends Statement {

  /**
   * Index of statement within all statements of script.
   */
  int getIndex();

  /**
   * Original sql of the statement.
   */
  String getSql();

  /**
   * The list of all tokens in the tree.
   */
  List<CommonToken> getTokens();

  /**
   * The root node of the tree.
   */
  ASTNode getTree();
  
  /**
   * A class for creating statements from SQL text.
   */
  static class Factory {

    private final MutantSwarmParseDriver parseDriver;

    Factory(MutantSwarmParseDriver parseDriver) {
      this.parseDriver = parseDriver;
    }

    public Factory() {
      this(new MutantSwarmParseDriver());
    }

    public MutantSwarmStatement newInstance(int scriptIndex, int statementIndex, String sql) {
      try {
        //TODO: XX remove comments from sql?
        TokenRewriteStream tokens = parseDriver.lex(sql);
        ASTNode tree = parseDriver.parse(tokens);
        return new Impl(statementIndex, sql, parseDriver.extractTokens(tokens), tree);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }

  }

  /**
   * Concrete implementation.
   */
  public static class Impl implements MutantSwarmStatement {

    private final int index;
    private final String sql;
    private final List<CommonToken> tokens;
    private final ASTNode tree;

    private Impl(int index, String sql, List<CommonToken> tokens, ASTNode tree) {
      this.index = index;
      this.sql = sql;
      this.tokens = unmodifiableList(tokens);
      this.tree = tree;
    }

    @Override
    public int getIndex() {
      return index;
    }

    @Override
    public String getSql() {
      return sql;
    }

    @Override
    public List<CommonToken> getTokens() {
      return tokens;
    }

    @Override
    public ASTNode getTree() {
      return tree;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + index;
      result = prime * result + ((sql == null) ? 0 : sql.hashCode());
      result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
      result = prime * result + ((tree == null) ? 0 : tree.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MutantSwarmStatement.Impl other = (MutantSwarmStatement.Impl) obj;
      if (index != other.index)
        return false;
      if (sql == null) {
        if (other.sql != null)
          return false;
      } else if (!sql.equals(other.sql))
        return false;
      if (tokens == null) {
        if (other.tokens != null)
          return false;
      } else if (!tokens.equals(other.tokens))
        return false;
      if (tree == null) {
        if (other.tree != null)
          return false;
      } else if (!tree.equals(other.tree))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "MutantSwarmStatement.Impl [index=" + index + ", sql=" + sql + ", tokens=" + tokens + ", tree=" + tree + "]";
    }

  }

}
