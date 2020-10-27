package com.hotels.mutantswarm.mutate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hotels.mutantswarm.mutate.Mutation.MutationImpl;

//MutationImpl [splice=Splice [startIndex=49, endIndex=49], replacementText=<]

@RunWith(MockitoJUnitRunner.class)
public class MutationImplTest {

//  @Mock
//  private String replacementText;
  @Mock
  private Splice splice;
  @Mock
  private MutationImpl mutationImpl;
//  
//  private Mutator mutator;
//  
  @Before
  public void setMocks() {
    System.out.println("this line of code is read");
    when(mutationImpl.getReplacementText()).thenReturn("<");
    when(mutationImpl.getSplice()).thenReturn(splice);
    mutationImpl = new MutationImpl(mutationImpl.getReplacementText(),mutationImpl.getSplice());
  }
  
  @Test
  public void checktoString() {
    String result = mutationImpl.toString();
    System.out.println(result);
    assertEquals(result,"hello world");
  }
}
