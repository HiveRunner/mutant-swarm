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
package com.hotels.mutantswarm.model;

import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * Describes the whole source under test.
 */
public interface MutantSwarmSource {

  List<MutantSwarmScript> getScripts();

  static public class Impl implements MutantSwarmSource {

    private final List<MutantSwarmScript> scripts;

    public Impl(List<MutantSwarmScript> scripts) {
      this.scripts = unmodifiableList(scripts);
    }

    @Override
    public List<MutantSwarmScript> getScripts() {
      return scripts;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((scripts == null) ? 0 : scripts.hashCode());
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
      MutantSwarmSource.Impl other = (MutantSwarmSource.Impl) obj;
      if (scripts == null) {
        if (other.scripts != null)
          return false;
      } else if (!scripts.equals(other.scripts))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "MutantSwarmSource.Impl [scripts=" + scripts + "]";
    }
    
  }
  
}
