package cova.automatic.data.gson;

import cova.automatic.results.ConstraintInformation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonConstraintInformation {
  private String output;

  private Map<String, Object> calculatedValues;

  private String constraint;

  private int javaLineNumber;
  private String javaClassName;

  private List<List<GsonPathElement>> paths;

  public GsonConstraintInformation(ConstraintInformation i) {
    this.output = i.getOutput();
    this.calculatedValues = new HashMap<>(i.getConstraintMap());
    this.constraint = i.constraintToReadableString();
    this.javaLineNumber = i.getJavaLineNumber();
    this.javaClassName = i.getClazz().toString();
    this.paths = new ArrayList<>();
    for (List<ConstraintInformation> path : i.getPaths()) {
      List<GsonPathElement> newPath = new ArrayList<>();
      for (ConstraintInformation ele : path) {
        GsonPathElement newEle = new GsonPathElement();
        newEle.setActivity(ele.getClazz().toString());
        newEle.setConstraint(ele.constraintToReadableString());
        newEle.setValues(ele.getConstraintMap());
        newPath.add(newEle);
      }
      this.paths.add(newPath);
    }
  }

  public String getOutput() {
    return output;
  }

  public Map<String, Object> getCalculatedValues() {
    return calculatedValues;
  }

  public String getConstraint() {
    return constraint;
  }

  public int getJavaLineNumber() {
    return javaLineNumber;
  }

  public String getJavaClassName() {
    return javaClassName;
  }
}
