/**
 * Copyright (C) 2018-2020 Expedia, Inc.
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
package com.hotels.mutantswarm.mutate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.plan.gene.Gene;
import com.hotels.mutantswarm.plan.gene.LexerGene;
import com.hotels.mutantswarm.plan.gene.ParserGene;

@RunWith(MockitoJUnitRunner.class)
public class SpliceTest {
  
  @Mock
  private Gene gene;
  @Mock
  private ParserGene parserGene;
  @Mock
  private LexerGene lexerGene;
  @Mock
  private ASTNode tree;
  @Mock
  private CommonToken token;
  @Mock
  private List<CommonToken> tokens;
  
  @Test
  public void testNewParserGeneInstance(){
    when(parserGene.getTree()).thenReturn(tree);
    when(tree.getToken()).thenReturn(token);
    when(token.getStartIndex()).thenReturn(1);
    when(token.getStopIndex()).thenReturn(10);
    
    Splice.Factory spliceFactory = new Splice.Factory();
    Splice splice = spliceFactory.newInstance(parserGene);
    
    assertThat(splice.getStartIndex(), is(1));
    assertThat(splice.getStopIndex(), is(10));
  }
  
  @Test
  public void testNewLexerGeneInstance(){
    when(lexerGene.getTokens()).thenReturn(tokens);
    when(tokens.get(0)).thenReturn(token);
    
    when(token.getStartIndex()).thenReturn(2);
    when(token.getStopIndex()).thenReturn(5);
    
    Splice.Factory spliceFactory = new Splice.Factory();
    Splice splice = spliceFactory.newInstance(lexerGene);
    
    assertThat(splice.getStartIndex(), is(2));
    assertThat(splice.getStopIndex(), is(5));
  }
  
  @Test (expected = IllegalArgumentException.class)
  public void unknownGeneType(){
    Splice.Factory spliceFactory = new Splice.Factory();
    spliceFactory.newInstance(gene);
  }
  
  @Test
  public void checkhashCode() {
    when(lexerGene.getTokens()).thenReturn(tokens);
    when(tokens.get(0)).thenReturn(token);
    
    when(token.getStartIndex()).thenReturn(2);
    when(token.getStopIndex()).thenReturn(5);
    Splice.Factory spliceFactory = new Splice.Factory();
    Splice splice = spliceFactory.newInstance(lexerGene);
    boolean result = (splice.hashCode() == (int)splice.hashCode());
    assertTrue(result);
  }
  
  @Test
  public void checkEquals() {
    when(lexerGene.getTokens()).thenReturn(tokens);
    when(tokens.get(0)).thenReturn(token);
    
    when(token.getStartIndex()).thenReturn(2);
    when(token.getStopIndex()).thenReturn(5);
    
    Splice.Factory spliceFactory = new Splice.Factory();
    Splice splice = spliceFactory.newInstance(lexerGene);
    Splice splice2 = spliceFactory.newInstance(lexerGene);
    boolean result = (splice.equals(splice2));
    assertTrue(result);
  }

}
