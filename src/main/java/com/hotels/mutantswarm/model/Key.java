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
package com.hotels.mutantswarm.model;

import com.hotels.mutantswarm.plan.gene.Gene;

public class Key {

  private final int scriptIndex;
  private final int statementIndex;
  
  public Key(Gene gene) {
    this(gene.getScriptIndex(), gene.getStatementIndex());
  }
  
  public Key(int scriptIndex, int statementIndex){
    this.scriptIndex = scriptIndex;
    this.statementIndex = statementIndex;
  }

  public int getScriptIndex() {
    return scriptIndex;
  }

  public int getStatementIndex() {
    return statementIndex;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + scriptIndex;
    result = prime * result + statementIndex;
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
    Key other = (Key) obj;
    if (scriptIndex != other.scriptIndex)
      return false;
    if (statementIndex != other.statementIndex)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Key [scriptIndex=" + scriptIndex + ", statementIndex=" + statementIndex + "]";
  }
  
}
