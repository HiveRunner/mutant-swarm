package com.hotels.mutantswarm.model;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
//  MutantSwarmScript.Impl [index=0, name=scriptToTest1.sql, statements=[MutantSwarmStatement.Impl [index=0, sql=CREATE TABLE bar AS
//      SELECT b, c
//      from foo
//      where a = "Green", tokens=[[@0,0:5='CREATE',<70>,1:0], [@2,7:11='TABLE',<280>,1:7], [@4,13:15='bar',<24>,1:13], [@6,17:18='AS',<36>,1:17], [@8,20:25='SELECT',<256>,2:0], [@10,27:27='b',<24>,2:7], [@11,28:28=',',<9>,2:8], [@13,30:30='c',<24>,2:10], [@15,32:35='from',<130>,3:0], [@17,37:39='foo',<24>,3:5], [@19,41:45='where',<323>,4:0], [@21,47:47='a',<24>,4:6], [@23,49:49='=',<18>,4:8], [@25,51:57='"Green"',<352>,4:10]], tree=TOK_CREATETABLE]], 
//      path=/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql]
  
  @Mock
  MutantSwarmScript.Impl mutantSwarmScript;
  
  private Factory factory;
  @Mock
  private MutantSwarmParseDriver parseDriver;
  @Mock
  private ASTNode tree;
  @Mock
  private TokenRewriteStream tokenStream;
  @Mock
  private CommonToken token;

  
  @Before
  public void setupMocks() throws ParseException{
    //set up list of statements
    factory = new MutantSwarmStatement.Factory(parseDriver);
    when(parseDriver.lex("SELECT * FROM x WHERE a = 1")).thenReturn(tokenStream);
    when(parseDriver.parse(tokenStream)).thenReturn(tree);
    when(parseDriver.extractTokens(tokenStream)).thenReturn(singletonList(token));
    
  }
  
  @Test
  public void checkToString() {
    List<MutantSwarmStatement> statements = new ArrayList<MutantSwarmStatement>();
    MutantSwarmStatement statement = factory.newInstance(0, 1, "SELECT * FROM x WHERE a = 1");
    statements.add(statement);
    MutantSwarmScript.Impl script = new MutantSwarmScript.Impl(0,Paths.get("/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    assertEquals(script.toString(), "MutantSwarmScript.Impl [index=0, name=scriptToTest1.sql, statements=[MutantSwarmStatement.Impl [index=1, sql=SELECT * FROM x WHERE a = 1, tokens=[token], tree=tree]], path=/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql]");
  }
  
  @Test
  public void equalSame() {
    List<MutantSwarmStatement> statements = new ArrayList<MutantSwarmStatement>();
    MutantSwarmStatement statement = factory.newInstance(0, 1, "SELECT * FROM x WHERE a = 1");
    statements.add(statement);
    MutantSwarmScript.Impl script = new MutantSwarmScript.Impl(0,Paths.get("/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    MutantSwarmScript.Impl script2 = new MutantSwarmScript.Impl(0,Paths.get("/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    boolean result = script.equals(script2);
    assertTrue(result);
  }
  @Test
  public void checkGetSql() {
    List<MutantSwarmStatement> statements = new ArrayList<MutantSwarmStatement>();
    MutantSwarmStatement statement = factory.newInstance(0, 1, "SELECT * FROM x WHERE a = 1");
    statements.add(statement);
    MutantSwarmScript.Impl script = new MutantSwarmScript.Impl(0,Paths.get("/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    String result = script.getSql();
    assertEquals(result, "SELECT * FROM x WHERE a = 1;\n");
  }
  @Test
  public void checkHashCode() {
    List<MutantSwarmStatement> statements = new ArrayList<MutantSwarmStatement>();
    MutantSwarmStatement statement = factory.newInstance(0, 1, "SELECT * FROM x WHERE a = 1");
    statements.add(statement);
    MutantSwarmScript.Impl script = new MutantSwarmScript.Impl(0,Paths.get("/Users/shermosa/eclipse-workspace/mutant-swarm/target/test-classes/mutantSwarmTest/scriptToTest1.sql"),statements);
    int result = script.hashCode();
    assertThat(result,is(-821144429));
  }
  
}
