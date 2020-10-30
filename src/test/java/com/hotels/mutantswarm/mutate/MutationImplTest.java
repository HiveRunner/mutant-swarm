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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.mutate.Mutation.MutationImpl;

@RunWith(MockitoJUnitRunner.class)
public class MutationImplTest {
  
  @Mock
  private Splice splice;
  
  private MutationImpl mutationImpl;
  
  @Before
  public void setMocks() {
    splice = new Splice(49,49);
    mutationImpl = new MutationImpl("<",splice);
  }
  
  @Test
  public void checktoString() {
    String result = mutationImpl.toString();
    assertEquals(result,"MutationImpl [splice=Splice [startIndex=49, endIndex=49], replacementText=<]");
  }
  
  @Test
  public void equalSame() {
    MutationImpl mutationImpl2 = new MutationImpl(mutationImpl.getReplacementText(),mutationImpl.getSplice());
    boolean result = mutationImpl.equals(mutationImpl2);
    assertThat(result, is(true));
  }
  
}
