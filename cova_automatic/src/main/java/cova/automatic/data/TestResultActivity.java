package cova.automatic.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultActivity {
  private String className;
  private Map<String, Object> constraints;
  private List<TestResultInput> inputs;

  public TestResultActivity(String className, Map<String, Object> constraints) {
    this.className = className;
    this.constraints = new HashMap<>(constraints);
    this.inputs = new ArrayList<>();
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Map<String, Object> getConstraints() {
    return constraints;
  }

  public void setConstraints(Map<String, Object> constraints) {
    this.constraints = constraints;
  }

  public List<TestResultInput> getInputs() {
    return inputs;
  }

  public void setInputs(List<TestResultInput> inputs) {
    this.inputs = inputs;
  }
}
