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

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotels.mutantswarm.model.MutantSwarmStatement;

@ExtendWith(MockitoExtension.class)
public class ReportStatementTest {
  
  @Mock
  private MutantSwarmStatement delegate;
  @Mock
  private Line line1, line2;
  @Mock
  private CommonToken token1, token2;
  @Mock
  private ASTNode tree;
 
  private List<Line> lines = new ArrayList<Line>();
  private ReportStatement reportStatement;
  private List<CommonToken> tokens = new ArrayList<CommonToken>();
  
  @BeforeEach
  public void setUpMocks() {
    
    lines.add(line1);
    lines.add(line2);
    
    tokens.add(token1);
    tokens.add(token2);
    
    reportStatement = new ReportStatement(delegate,lines);
    when(delegate.getIndex()).thenReturn(2);
    when(delegate.getSql()).thenReturn("this is an SQL query");
    when(delegate.getTokens()).thenReturn(tokens);
    when(delegate.getTree()).thenReturn(tree);
  }

  @Test
  public void checkGetIndex() {
    int result = reportStatement.getIndex();
    assertThat(result,is(2));
  }
  
  @Test
  public void checkGetSql() {
    String result = reportStatement.getSql();
    assertThat(result,is("this is an SQL query"));
  }
  
  @Test
  public void checkGetTokens() {
    List<CommonToken> result = reportStatement.getTokens();
    assertThat(result,is(tokens));
  }
  
  @Test
  public void checkGetTree() {
    ASTNode result = reportStatement.getTree();
    assertThat(result,is(tree));
  }
}
