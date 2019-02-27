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
package com.hotels.mutantswarm.report;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import static com.hotels.mutantswarm.exec.MutantState.KILLED;
import static com.hotels.mutantswarm.exec.MutantState.SURVIVED;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.Outcome;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.mutate.Splice;
import com.hotels.mutantswarm.plan.gene.Gene;
import com.hotels.mutantswarm.plan.gene.Locus;

@RunWith(MockitoJUnitRunner.class)
public class LineFactoryTest {

  @Mock
  private SwarmResults results;
  @Mock
  private Outcome outcome1, outcome2, outcome3, outcome4, outcome5, outcome6;
  @Mock
  private Mutation mutation1, mutation2, mutation3, mutation4, mutation5, mutation6;
  @Mock
  private Splice splice1, splice2, splice3, splice4, splice5, splice6;

  @Mock
  private MutantSwarmScript script1, script2;
  @Mock
  private MutantSwarmStatement statement1, statement2;
  @Mock
  private Gene gene1, gene2;
  @Mock
  private Locus locus1, locus2;

  @Before
  public void setup() {
    when(results.hasOutcomesFor(script1, statement1)).thenReturn(true);
    when(results.outcomesFor(script1, statement1)).thenReturn(asList(outcome1, outcome2, outcome3));
    when(script1.getStatements()).thenReturn(singletonList(statement1));
    when(statement1.getIndex()).thenReturn(0);

    when(results.hasOutcomesFor(script2, statement2)).thenReturn(true);
    when(results.outcomesFor(script2, statement2)).thenReturn(asList(outcome1, outcome2, outcome3));
    when(script2.getStatements()).thenReturn(singletonList(statement2));
    when(statement2.getIndex()).thenReturn(0);

    // 'a' -> 'null'
    when(mutation1.getReplacementText()).thenReturn("null");
    when(outcome1.getMutation()).thenReturn(mutation1);
    when(mutation1.getSplice()).thenReturn(splice1);
    when(outcome1.getState()).thenReturn(KILLED);

    // '=' -> '<>'
    when(mutation2.getReplacementText()).thenReturn("<>");
    when(outcome2.getMutation()).thenReturn(mutation2);
    when(mutation2.getSplice()).thenReturn(splice2);
    when(outcome2.getState()).thenReturn(SURVIVED);

    // '=' -> '>='
    when(mutation3.getReplacementText()).thenReturn(">=");
    when(outcome3.getMutation()).thenReturn(mutation3);
    when(mutation3.getSplice()).thenReturn(splice3);
    when(outcome3.getState()).thenReturn(KILLED);

    // statement 2
    // 'x' -> 'a'
    when(mutation4.getReplacementText()).thenReturn("a");
    when(mutation4.getSplice()).thenReturn(splice4);
    when(outcome4.getMutation()).thenReturn(mutation4);
    when(outcome4.getState()).thenReturn(SURVIVED);

    // '=' -> '>'
    when(mutation5.getReplacementText()).thenReturn(">");
    when(mutation5.getSplice()).thenReturn(splice5);
    when(outcome5.getMutation()).thenReturn(mutation5);
    when(outcome5.getState()).thenReturn(SURVIVED);

    // '=' -> '>='
    when(mutation6.getReplacementText()).thenReturn(">=");
    when(mutation6.getSplice()).thenReturn(splice6);
    when(outcome6.getMutation()).thenReturn(mutation6);
    when(outcome6.getState()).thenReturn(KILLED);
  }

  @Test
  public void singleLine() {

    when(statement1.getSql()).thenReturn("SELECT a FROM b WHERE c = 3");
    // 0 SELECT a FROM b WHERE c = 3
    // --012345678901234567890123456
    when(splice1.getStartIndex()).thenReturn(7);
    when(splice1.getStopIndex()).thenReturn(7);
    when(outcome1.getMutationStartIndex()).thenReturn(7);

    when(splice2.getStartIndex()).thenReturn(24);
    when(splice2.getStopIndex()).thenReturn(24);
    when(outcome2.getMutationStartIndex()).thenReturn(24);

    when(splice3.getStartIndex()).thenReturn(24);
    when(splice3.getStopIndex()).thenReturn(24);
    when(outcome3.getMutationStartIndex()).thenReturn(24);

    LineFactory stl = new LineFactory(results);
    Map<Integer, List<Line>> scriptLines = stl.buildLinesByStatementIndex(script1);
    assertThat(scriptLines.size(), is(1));

    List<Line> statementLines = scriptLines.get(0);
    assertThat(statementLines.size(), is(1));

    Line line = statementLines.get(0);
    assertThat(line.getNumber(), is(0));
    assertThat(line.getMutationCount(), is(3));
    assertThat(line.getSurvivorCount(), is(1));
    assertThat(line.getKilledCount(), is(2));
    assertThat(line.getGeneCount(), is(2));

    List<Text> elements = line.getElements();
    assertThat(elements.size(), is(5));

    // 0,0: 'SELECT '
    Text element = elements.get(0);
    assertThat(element.getStartIndex(), is(0));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is("SELECT "));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));

    // 0,1: 'a'
    element = elements.get(1);
    assertThat(element.getStartIndex(), is(7));
    assertThat(element.getMutationCount(), is(1));
    assertThat(element.getChars(), is("a"));
    assertThat(element.getKilled(), is(singletonList(outcome1)));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.KILLED));

    // 0,2: ' FROM b WHERE c '
    element = elements.get(2);
    assertThat(element.getStartIndex(), is(8));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is(" FROM b WHERE c "));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));

    // 0,3: '='
    element = elements.get(3);
    assertThat(element.getStartIndex(), is(24));
    assertThat(element.getMutationCount(), is(2));
    assertThat(element.getChars(), is("="));
    assertThat(element.getKilled(), is(singletonList(outcome3)));
    assertThat(element.getSurvivors(), is(singletonList(outcome2)));
    assertThat(element.getType(), is(Text.Type.SURVIVOR));

    // 0,4: ' 3'
    element = elements.get(4);
    assertThat(element.getStartIndex(), is(25));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is(" 3"));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));
  }

  @Test
  public void multiLine() {
    when(mutation1.getSplice()).thenReturn(splice1);
    when(mutation2.getSplice()).thenReturn(splice2);
    when(mutation3.getSplice()).thenReturn(splice3);

    when(statement1.getSql()).thenReturn("SELECT\n" + " a\n" + "FROM b\n" + "WHERE c = 3\n");
    // 0 SELECT\ a\FROM b\WHERE c = 3\ <- '\' placeholder for '\n'
    // --012345678901234567890123456789

    // 0 SELECT
    // 1 a
    // 2 FROM b
    // 3 WHERE c = 3
    // --01234567890
    when(splice1.getStartIndex()).thenReturn(8);
    when(splice1.getStopIndex()).thenReturn(8);
    when(outcome1.getMutationStartIndex()).thenReturn(8);

    when(splice2.getStartIndex()).thenReturn(25);
    when(splice2.getStopIndex()).thenReturn(25);
    when(outcome2.getMutationStartIndex()).thenReturn(25);

    when(splice3.getStartIndex()).thenReturn(25);
    when(splice3.getStopIndex()).thenReturn(25);
    when(outcome3.getMutationStartIndex()).thenReturn(25);

    LineFactory stl = new LineFactory(results);
    Map<Integer, List<Line>> scriptLines = stl.buildLinesByStatementIndex(script1);
    assertThat(scriptLines.size(), is(1));

    List<Line> statementLines = scriptLines.get(0);
    assertThat(statementLines.size(), is(5));

    // Line 0
    //
    Line line = statementLines.get(0);
    assertThat(line.getNumber(), is(0));
    assertThat(line.getMutationCount(), is(0));
    assertThat(line.getSurvivorCount(), is(0));
    assertThat(line.getKilledCount(), is(0));
    assertThat(line.getGeneCount(), is(0));

    List<Text> elements = line.getElements();
    assertThat(elements.size(), is(1));

    // 0,0: 'SELECT'
    Text element = elements.get(0);
    assertThat(element.getStartIndex(), is(0));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is("SELECT"));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));

    // Line 1
    //
    line = statementLines.get(1);
    assertThat(line.getNumber(), is(1));
    assertThat(line.getMutationCount(), is(1));
    assertThat(line.getSurvivorCount(), is(0));
    assertThat(line.getKilledCount(), is(1));
    assertThat(line.getGeneCount(), is(1));

    elements = line.getElements();
    assertThat(elements.size(), is(2));

    // 1,0: ' '
    element = elements.get(0);
    assertThat(element.getStartIndex(), is(7));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is(" "));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));

    // 1,1: 'a'
    element = elements.get(1);
    assertThat(element.getStartIndex(), is(8));
    assertThat(element.getMutationCount(), is(1));
    assertThat(element.getChars(), is("a"));
    assertThat(element.getKilled(), is(singletonList(outcome1)));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.KILLED));

    // Line 2: 'FROM b'
    //
    line = statementLines.get(2);
    assertThat(line.getNumber(), is(2));
    assertThat(line.getMutationCount(), is(0));
    assertThat(line.getSurvivorCount(), is(0));
    assertThat(line.getKilledCount(), is(0));
    assertThat(line.getGeneCount(), is(0));

    elements = line.getElements();
    assertThat(elements.size(), is(1));

    // 2,0: 'FROM b'
    element = elements.get(0);
    assertThat(element.getStartIndex(), is(10));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is("FROM b"));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));

    // Line 3: 'WHERE c = 3'
    //
    line = statementLines.get(3);
    assertThat(line.getNumber(), is(3));
    assertThat(line.getMutationCount(), is(2));
    assertThat(line.getSurvivorCount(), is(1));
    assertThat(line.getKilledCount(), is(1));
    assertThat(line.getGeneCount(), is(1));

    elements = line.getElements();

    // 3,0: 'WHERE c '
    element = elements.get(0);
    assertThat(element.getStartIndex(), is(17));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is("WHERE c "));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));

    // 3,1: '='
    element = elements.get(1);
    assertThat(element.getStartIndex(), is(25));
    assertThat(element.getMutationCount(), is(2));
    assertThat(element.getChars(), is("="));
    assertThat(element.getKilled(), is(singletonList(outcome3)));
    assertThat(element.getSurvivors(), is(singletonList(outcome2)));
    assertThat(element.getType(), is(Text.Type.SURVIVOR));

    // 3,2: ' 3'
    element = elements.get(2);
    assertThat(element.getStartIndex(), is(26));
    assertThat(element.getMutationCount(), is(0));
    assertThat(element.getChars(), is(" 3"));
    assertThat(element.getKilled(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getSurvivors(), is(Collections.<Outcome> emptyList()));
    assertThat(element.getType(), is(Text.Type.NON_MUTANT));
  }

  @Test
  public void multiScript() {

    when(statement1.getSql()).thenReturn("SELECT\n" + "a\n" + "FROM b\n" + "WHERE c = 3\n");
    // SELECT
    // a
    // FROM b
    // WHERE c = 3
    //
    // 0 SELECT\a\FROM b\WHERE c = 3\ <- '\' placeholder for '\n'
    // --01234567890123456789012345678
    when(statement2.getSql()).thenReturn("SELECT x\n" + "FROM y\n" + "WHERE z = 2\n");
    // SELECT x
    // FROM y
    // WHERE z = 2
    //
    // 0 SELECT x\FROM y\WHERE z = 2\ <- '\' placeholder for '\n'
    // --01234567890123456789012345678

    // statement 1
    when(splice1.getStartIndex()).thenReturn(7);
    when(splice1.getStopIndex()).thenReturn(7);
    when(outcome1.getMutationStartIndex()).thenReturn(7);

    when(splice2.getStartIndex()).thenReturn(24);
    when(splice2.getStopIndex()).thenReturn(24);
    when(outcome2.getMutationStartIndex()).thenReturn(24);

    when(splice3.getStartIndex()).thenReturn(24);
    when(splice3.getStopIndex()).thenReturn(24);
    when(outcome3.getMutationStartIndex()).thenReturn(24);

    // statement 2
    when(splice4.getStartIndex()).thenReturn(7);
    when(splice4.getStopIndex()).thenReturn(7);
    when(outcome4.getMutationStartIndex()).thenReturn(7);

    when(splice5.getStartIndex()).thenReturn(24);
    when(splice5.getStopIndex()).thenReturn(24);
    when(outcome5.getMutationStartIndex()).thenReturn(24);

    when(splice6.getStartIndex()).thenReturn(24);
    when(splice6.getStopIndex()).thenReturn(24);
    when(outcome6.getMutationStartIndex()).thenReturn(24);

    LineFactory stl = new LineFactory(results);

    // script 1
    Map<Integer, List<Line>> scriptLines = stl.buildLinesByStatementIndex(script1);
    assertThat(scriptLines.size(), is(1));
    List<Line> statementLines = scriptLines.get(0);
    assertThat(statementLines.size(), is(5));

    // script 2
    scriptLines = stl.buildLinesByStatementIndex(script2);
    assertThat(scriptLines.size(), is(1));
    statementLines = scriptLines.get(0);
    assertThat(statementLines.size(), is(4));
  }

}
