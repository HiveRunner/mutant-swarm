/*
 * Copyright (C) 2018-2021 Expedia, Inc.
 * Copyright (C) 2021 The HiveRunner Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.mutantswarm.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a formatted line of a statement, containing all info needed to appropriately render and format the line
 */
class Line {

  /**
   * Incrementally builds Lines. Looks up mutant outcomes and uses this to incrementally build text elements and lines
   */
  static class LineBuilder {

    private final List<Text> elements = new ArrayList<>();
    private final int lineNumber;
    private int geneCount;
    private int mutationCount;
    private int survivorCount;
    private int killedCount;
    
    LineBuilder(int lineNumber) {
      this.lineNumber = lineNumber;
    }

    void addText(Text text) {
      elements.add(text);
      mutationCount += text.getMutationCount();
      survivorCount += text.getSurvivors().size();
      killedCount += text.getKilled().size();
      if (text.getType() != Text.Type.NON_MUTANT){
        geneCount++;
      }
    }

    Line build() {
      return new Line(lineNumber, elements, geneCount, mutationCount, survivorCount, killedCount);
    }
  }

  private final List<Text> elements;
  private final int lineNumber;
  private final int geneCount;
  private final int mutationCount;
  private final int survivorCount;
  private final int killedCount;

  Line(int lineNumber, List<Text> elements, int geneCount, int mutationCount, int survivorCount, int killedCount) {
    this.elements = elements;
    this.lineNumber = lineNumber;
    this.geneCount = geneCount;
    this.mutationCount = mutationCount;
    this.survivorCount = survivorCount;
    this.killedCount = killedCount;
  }

  /**
   * The text blocks which comprise the line.
   */
  public List<Text> getElements() {
    return elements;
  }

  /**
   * The index at which this line occurs in the statement.
   */
  public int getNumber() {
    return lineNumber;
  }

  /**
   * The number of mutants generated on this line.
   */
  public int getMutationCount() {
    return mutationCount;
  }
  
  /**
   * Returns the number of surviving mutants for the line.
   */
  public int getSurvivorCount(){
    return survivorCount;
  }
  
  /**
   * Returns the number of genes on the line
   */
  public int getGeneCount() {
    return geneCount;
  }

  /** 
   * Returns the number of mutations killed on the line
   */
  public int getKilledCount() {
    return killedCount;
  }
  
  public boolean isSurvivors() {
    return survivorCount > 0;
  }
  
  public boolean isKilled() {
    return killedCount > 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elements == null) ? 0 : elements.hashCode());
    result = prime * result + lineNumber;
    result = prime * result + mutationCount;
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
    Line other = (Line) obj;
    if (elements == null) {
      if (other.elements != null)
        return false;
    } else if (!elements.equals(other.elements))
      return false;
    if (lineNumber != other.lineNumber)
      return false;
    if (mutationCount != other.mutationCount)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Line [elements=" + elements + ", lineNumber=" + lineNumber + ", mutationCount=" + mutationCount + "]";
  }
  
}
