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
package com.apache.hadoop.hive.ql.parse;

import static org.mockito.Mockito.when;

import java.util.LinkedList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.CalcitePlanner;
import org.apache.hadoop.hive.ql.parse.CalcitePlanner.ASTSearcher;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;

public class ParseDriverTest {

  private ASTNode tree;

  private ASTNode node;

  private String command = "CREATE TABLE foobar AS\nSELECT c\nFROM bar\nWHERE b = 3";
  
  private MutantSwarmParseDriver mutantSwarmParseDriver = new MutantSwarmParseDriver();

  @Test
  public void checkParseCommand() throws ParseException {
    node = mutantSwarmParseDriver.parse(command);
    assertThat(node.toStringTree(),is("(tok_createtable (tok_tabname foobar) tok_liketable (tok_query (tok_from (tok_tabref (tok_tabname bar))) (tok_insert (tok_destination (tok_dir tok_tmp_file)) (tok_select (tok_selexpr (tok_table_or_col c))) (tok_where (= (tok_table_or_col b) 3)))))"));
  }
  
  @Test
  public void lexParseError() throws ParseException {
    mutantSwarmParseDriver.lex("eiwjhqfikjqebrfq");
    }

// Ignore this for now, its just my attempt at testing the method processSetColsNode() and failing miserably :(
  
//  @Test
//  public void checkProcessSetColsNode() throws ParseException {
//    MutantSwarmParseDriver mutantSwarmParseDriver = new MutantSwarmParseDriver();
//    tree = mutantSwarmParseDriver.parse(command);
//    CalcitePlanner.ASTSearcher astSearcher = new CalcitePlanner.ASTSearcher(){
//      final LinkedList<ASTNode> searchQueue = new LinkedList<ASTNode>();
//      @Override
//      public ASTNode depthFirstSearch(ASTNode ast, int token) {
//        return ast;
//      }
//    };
//    astSearcher.reset();
//    //assertEquals(node.toStringTree(),"(tok_createtable (tok_tabname foobar) tok_liketable (tok_query (tok_from (tok_tabref (tok_tabname bar))) (tok_insert (tok_destination (tok_dir tok_tmp_file)) (tok_select (tok_selexpr (tok_table_or_col c))) (tok_where (= (tok_table_or_col b) 3)))))");
//    //when(astSearcher.depthFirstSearch(tree,HiveParser.TOK_SETCOLREF)).thenReturn(node);
//    //MutantSwarmParseDriver.handleSetColRefs(tree);
//    ASTNode setCols = astSearcher.depthFirstSearch(tree, HiveParser.TOK_SETCOLREF);
//    mutantSwarmParseDriver.processSetColsNode(setCols, astSearcher);
//    if(setCols == null) {
//      System.out.println("setCols is null");
//    }
//  }

}
