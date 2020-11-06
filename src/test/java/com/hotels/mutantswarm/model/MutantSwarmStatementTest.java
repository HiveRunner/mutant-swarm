package com.hotels.mutantswarm.model;

import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.junit.Test;
import org.mockito.Mock;

import com.hotels.mutantswarm.model.MutantSwarmStatement.Factory;

public class MutantSwarmStatementTest {

  MutantSwarmParseDriver parseDriver = new MutantSwarmParseDriver();

  private Factory factory = new Factory(parseDriver);

  @Test(expected = RuntimeException.class)
  public void checkNewInstance() {
    MutantSwarmStatement mutantSwarmStatement = factory.newInstance(2, 3, "\"CREATE TABLE foobar AS\\nSELECT c\\nFROM bar\\nWHERE b = 3\"");
  }

}
