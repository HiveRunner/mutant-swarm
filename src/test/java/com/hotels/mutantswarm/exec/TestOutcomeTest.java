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
package com.hotels.mutantswarm.exec;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.hotels.mutantswarm.mutate.Mutation;
import com.hotels.mutantswarm.plan.Mutant;

public class TestOutcomeTest {
  
  @Mock
  private String testName;
  @Mock
  private Mutant mutant;
  @Mock
  private Mutation mutation;
  @Mock
  private MutantState state;
  
  private TestOutcome testOutcome;
  
  private TestOutcome testOutcome2;
  
  @Before
  public void setUpMocks() {
    testOutcome = new TestOutcome(testName,mutant,mutation,state);
    testOutcome2 = new TestOutcome(testName,mutant,mutation,state);
  }
  
  @Test
  public void equalsSame() {
    assertThat(testOutcome.equals(testOutcome2), is(true));
  }
  
  @Test
  public void checkhashCode() {
    assertThat(testOutcome.hashCode() == (int)testOutcome.hashCode(), is(true));
  }
  
}
