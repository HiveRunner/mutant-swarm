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

import static org.mockito.Mockito.when;

import static com.hotels.mutantswarm.report.AssetUtil.readResourceAsString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.stringtemplate.v4.ST;

import com.google.common.base.Charsets;
import com.jcabi.matchers.XhtmlMatchers;

import com.hotels.mutantswarm.exec.Outcome;
import com.hotels.mutantswarm.exec.SwarmResults;
import com.hotels.mutantswarm.exec.TestOutcome;
import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.mutate.Mutator;
import com.hotels.mutantswarm.plan.Mutant;
import com.hotels.mutantswarm.plan.Swarm;
import com.hotels.mutantswarm.report.Text.Type;

@RunWith(MockitoJUnitRunner.class)
public class ReportTest {
  @Mock
  private MutantSwarmSource source;
  @Mock
  private Swarm swarm;
  @Mock
  private SwarmResults results;
  @Mock
  private LineFactory lineFactory;
  @Mock
  private MutantSwarmScript script1, script2;
  @Mock
  private MutantSwarmStatement statement1, statement2;
  @Mock
  private Line line1, line2, line3;
  @Mock
  private Text text1, space, text2, text3, text4, text5, text6;
  @Mock
  private Outcome outcome1, outcome2;
  @Mock
  private Mutant mutant1, mutant2;
  @Mock
  private Mutator mutator1, mutator2;
  @Mock
  private TestOutcome testOutcome1, testOutcome2;

  @Test
  public void testingReport() throws IOException {

    when(results.getSwarm()).thenReturn(swarm);
    when(swarm.getSource()).thenReturn(source);
    when(source.getScripts()).thenReturn(asList(script1));

    Map<Integer, List<Line>> map = new HashMap<>();
    map.put(0, asList(line1, line2, line3));

    when(lineFactory.buildLinesByStatementIndex(script1)).thenReturn(map);

    when(script1.getIndex()).thenReturn(0);
    when(script1.getFileName()).thenReturn("Script 1");
    when(script1.getStatements()).thenReturn(Arrays.asList(statement1));
    when(statement1.getIndex()).thenReturn(0);

    when(line1.getElements()).thenReturn(Arrays.asList(text1));
    when(line2.getElements()).thenReturn(Arrays.asList(text2, text3, text4));
    when(line3.getElements()).thenReturn(Arrays.asList(text5, text6, text4));

    // line 1
    when(line1.getMutationCount()).thenReturn(0);
    when(line1.getKilledCount()).thenReturn(0);
    when(line1.getNumber()).thenReturn(0);
    when(line1.getSurvivorCount()).thenReturn(0);
    when(line1.isSurvivors()).thenReturn(false);
    when(line1.isKilled()).thenReturn(false);

    // line 2
    when(line2.getMutationCount()).thenReturn(1);
    when(line2.getKilledCount()).thenReturn(1);
    when(line2.getNumber()).thenReturn(1);
    when(line2.getSurvivorCount()).thenReturn(0);
    when(line2.isSurvivors()).thenReturn(false);
    when(line2.isKilled()).thenReturn(true);

    // line 3
    when(line3.getMutationCount()).thenReturn(2);
    when(line3.getKilledCount()).thenReturn(1);
    when(line3.getNumber()).thenReturn(2);
    when(line3.getSurvivorCount()).thenReturn(1);
    when(line3.isSurvivors()).thenReturn(true);

    // text 1
    when(text1.getSurvivors()).thenReturn(Collections.EMPTY_LIST);
    when(text1.getChars()).thenReturn("select a from b ");
    when(text1.getType()).thenReturn(Type.NON_MUTANT);
    when(text1.getStartIndex()).thenReturn(0);

    // text 2
    when(text2.getSurvivors()).thenReturn(Collections.EMPTY_LIST);
    when(text2.getChars()).thenReturn("where x ");
    when(text2.getType()).thenReturn(Type.NON_MUTANT);
    when(text2.getStartIndex()).thenReturn(0);

    // text3
    when(text3.getSurvivors()).thenReturn(Collections.EMPTY_LIST);

    when(text3.getChars()).thenReturn("=");
    when(text3.getType()).thenReturn(Type.KILLED);
    when(text3.getStartIndex()).thenReturn(24);
    when(text3.getKilled()).thenReturn(Arrays.asList(outcome1));

    // text4
    when(text4.getSurvivors()).thenReturn(Collections.EMPTY_LIST);
    when(text4.getChars()).thenReturn(" 3");
    when(text4.getType()).thenReturn(Type.NON_MUTANT);
    // select a from b where x = 3 and z = 4

    when(text4.getSurvivors()).thenReturn(Collections.EMPTY_LIST);
    when(text5.getChars()).thenReturn("and z ");
    when(text5.getType()).thenReturn(Type.NON_MUTANT);

    when(text6.getChars()).thenReturn("=");
    when(text6.getType()).thenReturn(Type.KILLED);
    when(text6.getStartIndex()).thenReturn(34);
    when(text6.getKilled()).thenReturn(Arrays.asList(outcome1));
    when(text6.getSurvivors()).thenReturn(Arrays.asList(outcome2));

    when(outcome1.getMutant()).thenReturn(mutant1);
    when(mutant1.getMutator()).thenReturn(mutator1);
    when(mutator1.getDescription()).thenReturn("!=");

    when(outcome1.getTestOutcomes()).thenReturn(Arrays.asList(testOutcome1));
    when(testOutcome1.getTestName()).thenReturn("test 1");

    when(outcome2.getMutant()).thenReturn(mutant2);
    when(mutant2.getMutator()).thenReturn(mutator2);
    when(mutator2.getDescription()).thenReturn("+");

    when(outcome2.getTestOutcomes()).thenReturn(Arrays.asList(testOutcome2));
    when(testOutcome2.getTestName()).thenReturn("test 2");

    String reportTemplate = readResourceAsString("/stl/mutation_report.st");
    reportTemplate = reportTemplate.replace(" xmlns=\"http://www.w3.org/1999/xhtml\"", "");

    ST st = new ST(reportTemplate);
    Report report = new Report(results, lineFactory, st, "mutation_report.css");

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    report.writeTo(outputStream);
    String reportXml = new String(outputStream.toByteArray(), Charsets.UTF_8);

    // Line 0 - select a from b
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[1]/td[1]/span/text()[contains(., 0)]")); // line
                                                                                                                            // number
    MatcherAssert.assertThat(reportXml,
        XhtmlMatchers.hasXPath(
            "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[1]/td[2]/p/span/span[@class=\"sqlcode\"]/text()"
                + "[contains(., 'select a from b ')]")); // check line contents

    // // Line 1 - where x = 3
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[2]/td[1]//span[1]/text()[.=1]")); // line
                                                                                                                     // number
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[2]/td[1]//span[2]/text()[contains(., 'Genes: 0')]")); // gene
                                                                                                                                         // count
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[2]/td[1]//span[2]/text()[contains(., 'Mutations: 1')]")); // gene
                                                                                                                                             // count
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[2]/td[1]//span[2]/text()[contains(., 'Survived: 0')]")); // gene
                                                                                                                                            // count
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[2]/td[1]/span[2]/span[@class=\"fg_killed\"]/text()[contains(., 'Killed: 1')]")); // killed
    // count

    // // Line 2 - and z = 3
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[3]/td[1]//span[1]/text()[.=2]")); // line
                                                                                                                     // number
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[3]/td[1]//span[2]/text()[contains(., 'Genes: 0')]")); // gene
                                                                                                                                         // count
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[3]/td[1]//span[2]/text()[contains(., 'Mutations: 2')]")); // gene
                                                                                                                                             // count
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[3]/td[1]/span[2]/span[@class=\"fg_survived\"]/text()[contains(., 'Survived: 1')]")); // gene
    // count
    MatcherAssert.assertThat(reportXml, XhtmlMatchers.hasXPath(
        "/html/body[@onload=\"loadMutationCountBar()\"]/table[@class=\"table\"]/tr[3]/td[1]//span[2]/text()[contains(., 'Killed: 1')]")); // gene
    // count
  }

}
