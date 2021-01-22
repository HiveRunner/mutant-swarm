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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.mutantswarm.exec.Outcome;
import com.hotels.mutantswarm.report.Text.Builder;

@RunWith(MockitoJUnitRunner.class)
public class TextTest {

  @Mock
  private Outcome killed, survivor1, survivor2;

  @Test
  public void typical() {
    Builder builder = new Text.Builder(4);
    builder.addChar('h');
    builder.addChar('e');
    builder.addChar('l');
    builder.addChar('l');
    builder.addChar('o');
    builder.addKilled(killed);
    builder.addSurvivor(survivor1);
    builder.addSurvivor(survivor2);
    Text text = builder.build();

    assertThat(text.getStartIndex(), is(4));
    assertThat(text.getChars(), is("hello"));
    assertThat(text.getKilled(), is(singletonList(killed)));
    assertThat(text.getSurvivors(), is(asList(survivor1, survivor2)));
    assertThat(text.getType(), is(Text.Type.SURVIVOR));
  }

  @Test
  public void text() {
    Builder builder = new Text.Builder(4);
    builder.addChar('h');
    builder.addChar('e');
    builder.addChar('l');
    builder.addChar('l');
    builder.addChar('o');
    Text text = builder.build();
    assertThat(text.getChars(), is("hello"));
  }

  @Test
  public void startIndex() {
    Text text = new Text.Builder(4).build();
    assertThat(text.getStartIndex(), is(4));
  }

  @Test
  public void nonMutant() {
    Text text = new Text.Builder(4).build();
    assertThat(text.getType(), is(Text.Type.NON_MUTANT));
  }

  @Test
  public void killed() {
    Builder builder = new Text.Builder(4);
    builder.addKilled(killed);
    Text text = builder.build();
    assertThat(text.getType(), is(Text.Type.KILLED));
  }

  @Test
  public void survivor() {
    Builder builder = new Text.Builder(4);
    builder.addSurvivor(survivor1);
    Text text = builder.build();
    assertThat(text.getType(), is(Text.Type.SURVIVOR));
  }

  @Test
  public void testToString() {
    Builder builder = new Text.Builder(4);	    
    builder.addChar('h');
    builder.addChar('i');
    builder.addKilled(killed);
    builder.addSurvivor(survivor1);
    Text text = builder.build();
    String result = text.toString();
    String expected = "Text [chars=hi, survivors=[survivor1], killed=[killed], startIndex=4, mutationCount=2, type=SURVIVOR]";
    assertThat(result, is(expected));
  }

  @Test
  public void equalsNull() {
    Text text = new Text.Builder(4).build();
    Boolean result = text.equals(null);
    assertThat(result, is(false));
  }

  @Test
  public void equalsDifferentClass() {
    Text text = new Text.Builder(4).build();
    String string = "different class, this is a string";
    Boolean result = text.equals(string);
    assertThat(result, is(false));
  }

  @Test
  public void equalSame() {
    Text text = new Text.Builder(4).build();
    Text text2 = new Text.Builder(4).build();
    Boolean result = text.equals(text2);
    assertThat(result, is(true));
    assertThat(text.hashCode(), is(text2.hashCode()));
  }

  @Test
  public void checkMutationCount() {
    Builder builder = new Text.Builder(4);
    builder.addSurvivor(survivor1);
    Text text = builder.build();
    int result = text.getMutationCount();
    assertThat(result,is(1));
  }

}
