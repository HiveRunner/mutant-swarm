package com.hotels.mutantswarm;
import java.nio.file.Path;

import com.klarna.hiverunner.builder.Script;
public class HiveRunnerScript implements Script{

  private Path path;
  private String sqlText;
  private int index;

  HiveRunnerScript(int index, Path path, String sqlText) {
    this.index = index;
    this.path = path;
    this.sqlText = sqlText;
  }

  @Override
  public int getIndex() {
    return index;
  }

  /* (non-Javadoc)
   * @see com.klarna.hiverunner.builder.Script#getPath()
   */
  @Override
  public Path getPath() {
    return path;
  }

  /* (non-Javadoc)
   * @see com.klarna.hiverunner.builder.Script#getSqlText()
   */
  @Override
  public String getSql() {
    return sqlText;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + index;
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    result = prime * result + ((sqlText == null) ? 0 : sqlText.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HiveRunnerScript other = (HiveRunnerScript) obj;
    if (index != other.index)
      return false;
    if (path == null) {
      if (other.path != null)
        return false;
    } else if (!path.equals(other.path))
      return false;
    if (sqlText == null) {
      if (other.sqlText != null)
        return false;
    } else if (!sqlText.equals(other.sqlText))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "HiveRunnerScript [path=" + path + ", sqlText=" + sqlText + ", index=" + index + "]";
  }

}

