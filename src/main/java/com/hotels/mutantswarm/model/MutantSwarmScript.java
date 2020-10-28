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

import java.nio.file.Path;
import java.util.List;

import com.klarna.hiverunner.builder.Script;

/**
 * A script file under test; a collection of statements. Using an interface so can easily decorate in exec package.
 */
public interface MutantSwarmScript extends Script {

  /**
   * Script file name.
   */
  String getFileName();

  /**
   * The statements which make up the script.
   */
  List<MutantSwarmStatement> getStatements();
  
  /**
   * Concrete implementation.
   */
  static public class Impl implements MutantSwarmScript {

    private final int index;
    private final String name;
    private final List<MutantSwarmStatement> statements;
    private Path path;

    public Impl(int index, Path path, List<MutantSwarmStatement> statements) {
      this.index = index;
      this.path = path;
      this.name = path.getFileName().toString();
      this.statements = unmodifiableList(statements);
    }


    @Override
    public int getIndex() {
      return index;
    }

    @Override
    public String getFileName() {
      return name;
    }

    @Override
    public List<MutantSwarmStatement> getStatements() {
      return statements;
    }
    
    @Override
    public String getSql(){
      String sql = "";
      for (MutantSwarmStatement statement : statements) {
        sql += statement.getSql() + ";\n";
      }
      return sql;
    }

    @Override
    public Path getPath() {
      return path;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + index;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((path == null) ? 0 : path.hashCode());
      result = prime * result + ((statements == null) ? 0 : statements.hashCode());
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
      MutantSwarmScript.Impl other = (MutantSwarmScript.Impl) obj;
      if (index != other.index)
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (path == null) {
        if (other.path != null)
          return false;
      } else if (!path.equals(other.path))
        return false;
      if (statements == null) {
        if (other.statements != null)
          return false;
      } else if (!statements.equals(other.statements))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "MutantSwarmScript.Impl [index=" + index + ", name=" + name + ", statements=" + statements + ", path=" + path + "]";
    }

    
  }

}
