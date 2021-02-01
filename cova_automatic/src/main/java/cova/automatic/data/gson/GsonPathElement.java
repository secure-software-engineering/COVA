package cova.automatic.data.gson;

import java.util.Map;

public class GsonPathElement {
  private String activity;
  private Map<String, Object> values;
  private String constraint;

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public Map<String, Object> getValues() {
    return values;
  }

  public void setValues(Map<String, Object> values) {
    this.values = values;
  }

  public String getConstraint() {
    return constraint;
  }

  public void setConstraint(String constraint) {
    this.constraint = constraint;
  }
}
