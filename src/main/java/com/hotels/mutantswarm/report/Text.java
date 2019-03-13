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

import java.util.ArrayList;
import java.util.List;

import com.hotels.mutantswarm.exec.Outcome;

/**
 * Represents a formatted block of text, which might optionally contain info about experiement survivors and killed
 * mutations.
 */
class Text {

  /**
   * Incrementally builds Text blocks.
   */
  static class Builder {
    private final int startIndex;
    private final StringBuilder chars = new StringBuilder();
    
    private final List<Outcome> survivors = new ArrayList<>();
    private final List<Outcome> killed = new ArrayList<>();
    
    
    Builder(int position){
      this.startIndex = position;
    }
    
    void addChar(char c){
      chars.append(c);
    }
    
    void addSurvivor(Outcome outcome){
      survivors.add(outcome);
    }
    
    void addKilled(Outcome outcome){
      killed.add(outcome);
    }
    
    private Type determineTextType(){
      if (survivors.isEmpty() && killed.isEmpty()){
        return Type.NON_MUTANT;
      } else if (survivors.isEmpty()){
        return Type.KILLED;
      }
      return Type.SURVIVOR;
    }
    
    Text build(){
      return new Text(startIndex, chars.toString(), survivors, killed, determineTextType());
    }
  }
  
  static enum Type{
    NON_MUTANT, SURVIVOR, KILLED
  }
  
  private final String chars;
  private final List<Outcome> survivors;
  private final List<Outcome> killed;
  private final int startIndex;
  private final int mutationCount;
  private final Type type;
  
  Text(int startIndex, String chars, List<Outcome> survivors, List<Outcome> killed, Type type) {
    this.startIndex = startIndex;
    this.chars = chars;
    this.survivors = survivors;
    this.killed = killed;
    mutationCount = survivors.size() + killed.size();
    this.type = type;
  }

  /**
   * The chars in this text block.
   */
  public String getChars() {
    return chars;
  }

  /**
   * The survivors relating to this text block.
   */
  public List<Outcome> getSurvivors() {
    return survivors;
  }

  /**
   * The killed mutants relating to this text block.
   */
  public List<Outcome> getKilled() {
    return killed;
  }

  /** 
   * The index in the statement where the first character is located.
   */
  public int getStartIndex() {
    return startIndex;
  }
  
  /** 
   * The type of this text block, derived from the state.
   */
  public Type getType(){
    return type;
  }
  
  /**
   * The number of time this text has been mutated.
   */
  public int getMutationCount(){
    return mutationCount;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((chars == null) ? 0 : chars.hashCode());
    result = prime * result + ((killed == null) ? 0 : killed.hashCode());
    result = prime * result + mutationCount;
    result = prime * result + startIndex;
    result = prime * result + ((survivors == null) ? 0 : survivors.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Text other = (Text) obj;
    if (chars == null) {
      if (other.chars != null)
        return false;
    } else if (!chars.equals(other.chars))
      return false;
    if (killed == null) {
      if (other.killed != null)
        return false;
    } else if (!killed.equals(other.killed))
      return false;
    if (mutationCount != other.mutationCount)
      return false;
    if (startIndex != other.startIndex)
      return false;
    if (survivors == null) {
      if (other.survivors != null)
        return false;
    } else if (!survivors.equals(other.survivors))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Text [chars="
        + chars
        + ", survivors="
        + survivors
        + ", killed="
        + killed
        + ", startIndex="
        + startIndex
        + ", mutationCount="
        + mutationCount
        + ", type="
        + type
        + "]";
  }
  
}
