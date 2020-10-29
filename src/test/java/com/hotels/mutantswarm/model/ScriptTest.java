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
package com.hotels.mutantswarm.model;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.TokenRewriteStream;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.model.MutantSwarmStatement.Factory;

@RunWith(MockitoJUnitRunner.class)
public class ScriptTest {
  @Mock
  MutantSwarmScript.Impl mutantSwarmScript;
  @Mock
  private MutantSwarmParseDriver parseDriver;
  @Mock
  private ASTNode tree;
  @Mock
  private TokenRewriteStream tokenStream;
  @Mock
  private CommonToken token;

  private MutantSwarmStatement statement;
  private List<MutantSwarmStatement> statements;
  private MutantSwarmScript.Impl script;
  private Factory factory;


  @Before
  public void setupMocks() throws ParseException{
    factory = new MutantSwarmStatement.Factory(parseDriver);
    when(parseDriver.lex("SELECT * FROM x WHERE a = 1")).thenReturn(tokenStream);
    when(parseDriver.parse(tokenStream)).thenReturn(tree);
    when(parseDriver.extractTokens(tokenStream)).thenReturn(singletonList(token));
    statements = new ArrayList<MutantSwarmStatement>();
    statement = factory.newInstance(0, 1, "SELECT * FROM x WHERE a = 1");
    statements.add(statement);
    script = new MutantSwarmScript.Impl(0,Paths.get("/Users/user/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
  }

  @Test
  public void checkToString() {
    assertEquals(script.toString(), "MutantSwarmScript.Impl [index=0, name=scriptToTest1.sql, statements=[MutantSwarmStatement.Impl [index=1, sql=SELECT * FROM x WHERE a = 1, tokens=[token], tree=tree]], path=/Users/user/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql]");
  }

  @Test
  public void equalSame() {
    MutantSwarmScript.Impl script2 = new MutantSwarmScript.Impl(0,Paths.get("/Users/user/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    boolean result = script.equals(script2);
    assertTrue(result);
  }

  @Test
  public void equalDifferentPath() {
    MutantSwarmScript.Impl script2 = new MutantSwarmScript.Impl(0,Paths.get("/Usejjrs/user/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    boolean result = script.equals(script2);
    assertFalse(result);
  }

  @Test
  public void equalNull() {
    boolean result = script.equals(null);
    assertFalse(result);
  }

  @Test
  public void checkGetSql() {
    String result = script.getSql();
    assertEquals(result, "SELECT * FROM x WHERE a = 1;\n");
  }

  @Test
  public void checkGetFileName() {
    String result = script.getFileName();
    assertEquals(result, "scriptToTest1.sql");
  }

  @Test
  public void checkHashCode() {
    boolean result = (script.hashCode() == (int)script.hashCode());
    assertTrue(result);
  }

}
