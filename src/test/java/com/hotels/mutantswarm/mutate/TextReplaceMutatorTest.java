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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.antlr.runtime.CommonToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.plan.gene.Gene;
import com.hotels.mutantswarm.plan.gene.Locus;

@RunWith(MockitoJUnitRunner.class)
public class TextReplaceMutatorTest {

	@Mock
	private Gene gene;
	@Mock
	private Locus locus;
	@Mock
	private Splice.Factory spliceFactory;
	@Mock
	private Splice splice;
	@Mock
	private CommonToken token;

	private Mutator mutator;

	@Before
	public void setupMocks() {
	  mutator = new TextReplaceMutator(spliceFactory, "test", "=", "<>");
	}
	
	@Test
	public void description() {
		assertThat(mutator.getDescription(), is("test"));
	}

	@Test
	public void name() {
		assertThat(mutator.getName(), is("= -> <>"));
	}

	@Test
	public void apply() {
	  when(spliceFactory.newInstance(gene)).thenReturn(splice);

		Mutation mutation = mutator.apply(gene);

		assertThat(mutation.getSplice(), is(splice));
		assertThat(mutation.getReplacementText(), is("<>"));
	}
}
