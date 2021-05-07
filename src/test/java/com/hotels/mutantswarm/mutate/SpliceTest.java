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
package com.hotels.mutantswarm.mutate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotels.mutantswarm.plan.gene.Gene;
import com.hotels.mutantswarm.plan.gene.LexerGene;
import com.hotels.mutantswarm.plan.gene.ParserGene;

@ExtendWith(MockitoExtension.class)
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
  
  @Test
  public void unknownGeneType(){
    Splice.Factory spliceFactory = new Splice.Factory();
    Assertions.assertThrows(IllegalArgumentException.class, () -> spliceFactory.newInstance(gene));
  }
  
  @Test
  public void equalSame() {
    when(lexerGene.getTokens()).thenReturn(tokens);
    when(tokens.get(0)).thenReturn(token);
    
    when(token.getStartIndex()).thenReturn(2);
    when(token.getStopIndex()).thenReturn(5);
    
    Splice.Factory spliceFactory = new Splice.Factory();
    Splice splice = spliceFactory.newInstance(lexerGene);
    Splice splice2 = spliceFactory.newInstance(lexerGene);
    assertThat((splice.equals(splice2)), is(true));
    assertThat(splice.hashCode(), is(splice2.hashCode()));
  }

}
