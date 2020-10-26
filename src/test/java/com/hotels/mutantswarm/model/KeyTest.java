package com.hotels.mutantswarm.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.plan.gene.Gene;
import com.hotels.mutantswarm.plan.gene.Locus;

@RunWith(MockitoJUnitRunner.class)
public class KeyTest {
  @Mock
  private Locus locus;
  @Mock
  private int scriptIndex;
  @Mock
  private int statementIndex;
  @Mock
  private Gene gene;
  @Mock
  private Key key;
  
  @Before
  public void setupMocks() throws ParseException {
    key = new Key(gene);
  }
  
  @Test
  public void checkScriptIndex() {
    int result = key.getScriptIndex();
    System.out.println(result);
    assertEquals(result,0);
  }
}