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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Test;

public class MutantSwarmParseDriverTest {

  private ASTNode node;

  private MutantSwarmParseDriver mutantSwarmParseDriver = new MutantSwarmParseDriver();

  @Test
  public void checkParseCommand() throws ParseException {
    String command = "CREATE TABLE foobar AS\nSELECT c\nFROM bar\nWHERE b = 3";
    node = mutantSwarmParseDriver.parse(command);
    assertThat(node.toStringTree(),is("(tok_createtable (tok_tabname foobar) tok_liketable (tok_query (tok_from (tok_tabref (tok_tabname bar))) (tok_insert (tok_destination (tok_dir tok_tmp_file)) (tok_select (tok_selexpr (tok_table_or_col c))) (tok_where (= (tok_table_or_col b) 3)))))"));
  }
  
  @Test(expected = ParseException.class)
  public void checkParseError() throws ParseException {
    String invalidCommand = "CREAGGGTE TABLE foobar AS\nSELECT c ERROR \nFROM bar\nWHERE b = 3";
    node = mutantSwarmParseDriver.parse(invalidCommand);
  }

}
