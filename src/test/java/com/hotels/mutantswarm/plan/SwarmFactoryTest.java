/**
 * Copyright (C) 2018-2019 Expedia, Inc.
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
package com.hotels.mutantswarm.plan;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.model.MutantSwarmScript;
import com.hotels.mutantswarm.model.MutantSwarmSource;
import com.hotels.mutantswarm.model.MutantSwarmStatement;
import com.hotels.mutantswarm.plan.Swarm.SwarmFactory;

@RunWith(MockitoJUnitRunner.class)
public class SwarmFactoryTest {

	@Mock
	private MutantSwarmSource source;
	@Mock
	private MutantSwarmScript script1, script2;
	@Mock
	private MutantSwarmStatement statement1_1, statement1_2, statement2_1;
	@Mock
	private MutantFactory mutantFactory;
	@Mock
	private Mutant mutant1_1_1_1, mutant1_1_2_1, mutant1_1_2_2, mutant1_2_1_1;

	@Before
	public void initialiseMocks() {
		when(source.getScripts()).thenReturn(asList(script1, script2));
		when(script1.getStatements()).thenReturn(asList(statement1_1, statement1_2));
		when(script2.getStatements()).thenReturn(asList(statement2_1));
		when(mutantFactory.newMutants(0, statement1_1)).thenReturn(asList(mutant1_1_1_1));
		when(mutantFactory.newMutants(0, statement1_2)).thenReturn(asList(mutant1_1_2_1, mutant1_1_2_2, mutant1_2_1_1));
		when(mutantFactory.newMutants(1, statement2_1)).thenReturn(Collections.<Mutant> emptyList());
	}

	@Test
	public void newInstance() {
		SwarmFactory factory = new Swarm.SwarmFactory(mutantFactory);
		Swarm swarm = factory.newInstance(source);
		
		assertThat(swarm.getSource(), is(source));
		
		List<Mutant> mutants = swarm.getMutants();
		assertThat(mutants.size(), is(4));
		assertThat(mutants.get(0), is(mutant1_1_1_1));
		assertThat(mutants.get(1), is(mutant1_1_2_1));
		assertThat(mutants.get(2), is(mutant1_1_2_2));
		assertThat(mutants.get(3), is(mutant1_2_1_1));
	}

}
