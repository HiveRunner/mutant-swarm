package com.hotels.mutantswarm.report;

import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.hotels.mutantswarm.model.MutantSwarmScript;

@RunWith(MockitoJUnitRunner.class)
public class ReportScriptTest {

  @Mock
  private MutantSwarmScript delegate;
  @Mock
  private LineFactory lineFactory;
  
  @Test
  public void checkGetSql() {
    when(delegate.getSql()).thenReturn("this is an SQL query");
    ReportScript reportScript = new ReportScript(delegate, lineFactory);
    String result = reportScript.getSql();
    assertThat(result,is("this is an SQL query"));
  }
  
  @Test
  public void checkGetPath() {
    when(delegate.getPath()).thenReturn(Paths.get("/this/is/a/path/to/a/file.sql"));
    ReportScript reportScript = new ReportScript(delegate, lineFactory);
    Path result = reportScript.getPath();
    assertThat(result,is(Paths.get("/this/is/a/path/to/a/file.sql")));
  }
}
