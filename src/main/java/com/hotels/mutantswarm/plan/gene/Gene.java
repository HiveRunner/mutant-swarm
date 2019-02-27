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
package com.hotels.mutantswarm.plan.gene;

/**
 * Represents an identifiable sequence within a representation of a query source.
 */
public abstract class Gene {

  private Locus locus;

  Gene(Locus locus) {
    this.locus = locus;
  }

  /**
   * The location of the gene in the source.
   */
  public Locus getLocus() {
    return locus;
  }

  /**
   * Returns the script index of the gene.
   */
  public int getScriptIndex() {
    return locus.getScriptIndex();
  }

  /**
   * Returns the statement index of the gene.
   */
  public int getStatementIndex() {
    return locus.getStatementIndex();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((locus == null) ? 0 : locus.hashCode());
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
    Gene other = (Gene) obj;
    if (locus == null) {
      if (other.locus != null)
        return false;
    } else if (!locus.equals(other.locus))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Gene [locus=" + locus + "]";
  }

}
