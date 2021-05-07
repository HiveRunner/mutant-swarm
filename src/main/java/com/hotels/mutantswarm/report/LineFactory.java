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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import com.hotels.mutantswarm.exec.MutantState;
import com.hotels.mutantswarm.exec.Outcome;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.report.Line.LineBuilder;
import com.hotels.mutantswarm.report.Text.Builder;

/** Converts a script into a set of sequentially renderable lines. */
public class LineFactory {

  private final SwarmResults results;

  public LineFactory(SwarmResults results) {
    this.results = results;
  }

  public Map<Integer, List<Line>> buildLinesByStatementIndex(MutantSwarmScript script) {
    Map<Integer, List<Line>> linesByStatementIndex = new HashMap<>();
    List<Line> lines = new ArrayList<>();

    for (MutantSwarmStatement statement : script.getStatements()) {
      int lineNumber = 0;
      LineBuilder lineBuilder = new LineBuilder(lineNumber);

      if (results.hasOutcomesFor(script, statement)) {
        List<Outcome> outcomesForStatement = results.outcomesFor(script, statement);
        SortedMap<Integer, List<Outcome>> outcomesByStartIndex = OutcomeUtil.outcomesByStartIndex(outcomesForStatement);

        SortedSet<Integer> cuttingMarkPositions = new TreeSet<>();
        cuttingMarkPositions.addAll(outcomesByStartIndex.keySet());
        String sql = statement.getSql();
        cuttingMarkPositions.addAll(allIndexOf(sql, "\n"));
        int currentIndex = 0;

        for (int position : cuttingMarkPositions) {
          if (currentIndex != position) {
            String text = sql.substring(currentIndex, position);
            addTextToLine(currentIndex, text, outcomesByStartIndex, lineBuilder);
            currentIndex = position;

          }
          if (sql.charAt(position) == '\n') {
            // create a new line object
            lines.add(lineBuilder.build());
            lineNumber++;
            lineBuilder = new LineBuilder(lineNumber);
            currentIndex = position + 1;

          } else {
            Mutation mutation = outcomesByStartIndex.get(position).get(0).getMutation();
            int endIndex = mutation.getSplice().getStopIndex();
            addTextToLine(position, sql.substring(position, endIndex + 1), outcomesByStartIndex, lineBuilder);
            currentIndex = endIndex + 1;
          }
        }
        if (currentIndex != sql.length()) {
          addTextToLine(currentIndex, sql.substring(currentIndex, sql.length()), outcomesByStartIndex, lineBuilder);
        }
        lines.add(lineBuilder.build());
      }
      linesByStatementIndex.put(statement.getIndex(), lines);
    }
    return linesByStatementIndex;
  }

  private SortedSet<Integer> allIndexOf(String sql, String string) {
    SortedSet<Integer> positions = new TreeSet<>();
    int index = sql.indexOf("\n");
    while (index >= 0) {
      positions.add(index);
      index = sql.indexOf("\n", index + 1);
    }
    return positions;
  }

  private void addTextToLine(
      int startIndex,
      String text,
      Map<Integer, List<Outcome>> outcomesByStartIndex,
      LineBuilder lineBuilder) {
    Builder textBuilder = new Builder(startIndex);
    for (char c : text.toCharArray()) {
      textBuilder.addChar(c);
    }

    if (outcomesByStartIndex != null && outcomesByStartIndex.containsKey(startIndex)) {
      for (Outcome outcome : outcomesByStartIndex.get(startIndex)) {
        if (outcome.getState() == MutantState.KILLED) {
          textBuilder.addKilled(outcome);
        } else {
          textBuilder.addSurvivor(outcome);
        }
      }
    }
    lineBuilder.addText(textBuilder.build());
  }

}
