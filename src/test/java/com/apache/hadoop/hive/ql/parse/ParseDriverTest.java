package com.apache.hadoop.hive.ql.parse;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

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

import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.model.MutantSwarmStatement.Factory;
import org.apache.hadoop.hive.ql.parse.*;

import org.antlr.runtime.TokenRewriteStream;

//@RunWith(MockitoJUnitRunner.class)
public class ParseDriverTest {
  

//  @Mock
//  private MutantSwarmParseDriver parseDriver;
//  @Mock
//  private ASTNode tree;
//  @Mock
//  private CommonToken token;
//  @Mock
//  private TokenRewriteStream tokenStream;
//
//  private Factory factory;
//
//  @Before
//  public void setupMocks() throws ParseException {
//    factory = new MutantSwarmStatement.Factory(parseDriver);
//    when(parseDriver.lex("SELECT * FROM x WHERE a = 1")).thenReturn(tokenStream);
//    when(parseDriver.parse(tokenStream)).thenReturn(tree);
//    when(parseDriver.extractTokens(tokenStream)).thenReturn(singletonList(token));
//  }
//  
//  @Test
//  public void checkExtractTokens() throws ParseException {
//    TokenRewriteStream tokenStream = parseDriver.lex("SELECT * FROM x WHERE a = 1");
//    MutantSwarmStatement statement = factory.newInstance(0, 1, "SELECT * FROM x WHERE a = 1");
//    List<CommonToken> result = tokenStream.extractTokens();
//  }

  
}
