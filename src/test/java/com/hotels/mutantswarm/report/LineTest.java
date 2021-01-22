/**
 * Copyright (C) 2018-2021 Expedia, Inc.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.Outcome;
import com.hotels.mutantswarm.report.Line.LineBuilder;

@RunWith(MockitoJUnitRunner.class)
public class LineTest {

  @Mock
  private Text text1, text2;
  @Mock
  private List<Outcome> survivors, killed;

  @Test
  public void testAddText() {

    when(text1.getMutationCount()).thenReturn(2);
    when(text1.getSurvivors()).thenReturn(survivors);
    when(survivors.size()).thenReturn(2);
    when(text1.getKilled()).thenReturn(killed);
    when(killed.size()).thenReturn(0);
    when(text1.getType()).thenReturn(Text.Type.SURVIVOR);

    LineBuilder lineBuilder = new LineBuilder(0);
    lineBuilder.addText(text1);
    Line line = lineBuilder.build();

    assertThat(line.getElements().size(), is(1));
    assertThat(line.getNumber(), is(0));
    assertThat(line.getMutationCount(), is(2));
    assertThat(line.getSurvivorCount(), is(2));
    assertThat(line.getGeneCount(), is(1));
    assertThat(line.getKilledCount(), is(0));
    assertThat(line.isSurvivors(), is(true));
    assertThat(line.isKilled(), is(false));
  }

  @Test
  public void testBuildLine() {
    LineBuilder lineBuilder = new LineBuilder(0);
    lineBuilder.addText(text1);
    assertThat(lineBuilder.build().getClass(), is(Line.class));
  }

  @Test
  public void multiText() {
    LineBuilder lineBuilder = new LineBuilder(0);
    lineBuilder.addText(text1);
    lineBuilder.addText(text2);
    Line line = lineBuilder.build();
    assertThat(line.getElements().size(), is(2));
  }

  @Test(expected = NullPointerException.class)
  public void nullLineNumber() {
    LineBuilder lineBuilder = new LineBuilder((Integer) null);
  }

  @Test(expected = NullPointerException.class)
  public void nullText() {
    LineBuilder lineBuilder = new LineBuilder(0);
    lineBuilder.addText(null);
  }

}
