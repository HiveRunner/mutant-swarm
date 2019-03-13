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

import static java.util.Collections.unmodifiableSortedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.hotels.mutantswarm.exec.Outcome;

public class OutcomeUtil {

  /**
   * Organises outcomes by the startPosition to which their mutations related, so we can easily cross reference as we
   * scan through the statement. A Gene can have multiple mutations - hence multiple outcomes would have the same start
   * position.
   */
  public static SortedMap<Integer, List<Outcome>> outcomesByStartIndex(List<Outcome> outcomes) {
    SortedMap<Integer, List<Outcome>> outcomesByStartIndex = new TreeMap<>();
    for (Outcome outcome : outcomes) {
      int startIndex = outcome.getMutationStartIndex();
      List<Outcome> list;
      if (outcomesByStartIndex.containsKey(startIndex)) {
        list = outcomesByStartIndex.get(startIndex);
      } else {
        list = new ArrayList<>();
        outcomesByStartIndex.put(startIndex, list);
      }
      list.add(outcome);
    }
    return unmodifiableSortedMap(outcomesByStartIndex);
  }

}
