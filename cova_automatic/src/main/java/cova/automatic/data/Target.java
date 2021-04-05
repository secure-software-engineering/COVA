package cova.automatic.data;

import java.util.HashMap;
import java.util.Map;

public class Target {
  private String method;
  private String unit;
  private Integer lineNumber;
  private Map<String, Boolean> targetStrings = new HashMap<>();

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
  }

  public Map<String, Boolean> getTargetStrings() {
    return targetStrings;
  }
}
