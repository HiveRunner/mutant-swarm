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
package com.hotels.mutantswarm.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.hotels.mutantswarm.model.MutantSwarmStatement.Factory;

public class MutantSwarmStatementTest {

  MutantSwarmParseDriver parseDriver = new MutantSwarmParseDriver();

  private Factory factory = new Factory(parseDriver);

  @Test
  public void checkNewInstance() {
    MutantSwarmStatement mutantSwarmStatement = factory.newInstance(2, 3, "CREATE TABLE foobar AS SELECT c FROM bar WHERE b = 3");
    assertThat(mutantSwarmStatement.toString(), is("MutantSwarmStatement.Impl [index=3, sql=CREATE TABLE foobar AS SELECT c FROM bar WHERE b = 3, tokens=[[@0,0:5='CREATE',<70>,1:0], [@2,7:11='TABLE',<280>,1:7], [@4,13:18='foobar',<24>,1:13], [@6,20:21='AS',<36>,1:20], [@8,23:28='SELECT',<256>,1:23], [@10,30:30='c',<24>,1:30], [@12,32:35='FROM',<130>,1:32], [@14,37:39='bar',<24>,1:37], [@16,41:45='WHERE',<323>,1:41], [@18,47:47='b',<24>,1:47], [@20,49:49='=',<18>,1:49], [@22,51:51='3',<340>,1:51]], tree=TOK_CREATETABLE]"));
  }
  
  @Test
  public void checkIncorrectNewInstance() {
    Assertions.assertThrows(RuntimeException.class, () -> factory.newInstance(2, 3, "incorrectqueryCREATE TABLE foobar AS SELECT c FROM bar WHERE b = 3"));
  }

}
