package cova.automatic.results;

import cova.data.IConstraint;
import cova.source.IdManager;
import cova.source.SourceInformation;
import heros.solver.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class ConstraintInformation {
  private SootClass clazz;
  private SootMethod method;
  private int javaLineNumber;
  private Unit unit;
  private IConstraint constraint;
  private String output;
  private Map<Integer, String> mapping;
  private List<SootClass> constraintSootClasses;
  private List<List<ConstraintInformation>> paths;
  private Map<String, String> oldNewMapping;

  public ConstraintInformation(
      SootClass clazz,
      SootMethod method,
      int javaLineNumber,
      Unit unit,
      IConstraint constraint,
      String output,
      Map<Integer, String> mapping) {
    this.clazz = clazz;
    this.method = method;
    this.javaLineNumber = javaLineNumber;
    this.unit = unit;
    this.constraint = constraint;
    this.output = output;
    this.mapping = mapping;
    this.oldNewMapping = new HashMap<>();
  }

  public SootClass getClazz() {
    return clazz;
  }

  public void setClazz(SootClass clazz) {
    this.clazz = clazz;
  }

  public SootMethod getMethod() {
    return method;
  }

  public void setMethod(SootMethod method) {
    this.method = method;
  }

  public int getJavaLineNumber() {
    return javaLineNumber;
  }

  public void setJavaLineNumber(int javaLineNumber) {
    this.javaLineNumber = javaLineNumber;
  }

  public Unit getUnit() {
    return unit;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  public IConstraint getConstraint() {
    return constraint;
  }

  public Map<String, Object> getConstraintMap() {
    constraintSootClasses = new ArrayList<>();
    Map<String, Object> values = constraint.getValues();
    Map<String, Object> newValues = new HashMap<>();
    if (values == null) {
      return newValues;
    }
    for (String key : values.keySet()) {
      if (key == null) continue;
      Object value = values.get(key);

      String tmpKey = key;
      String oldKey = key;
      tmpKey = tmpKey.replace("I", "");
      tmpKey = tmpKey.replace("U", "");
      try {
        int num = Integer.parseInt(tmpKey);
        if (IdManager.getInstance().contains(num)) {
          SourceInformation i = IdManager.getInstance().get(num);
          if (i.getAllInfos().isEmpty()) {
            i.getAllInfos().add(i);
          }
          String tmp = "";
          for (SourceInformation info : i.getAllInfos()) {
            int activityId = info.getLayoutId();
            for (Pair<SootClass, Integer> e : IdManager.getInstance().getLayoutClasses()) {
              if (e.getO2() == activityId) {
                constraintSootClasses.add(e.getO1());
              }
            }
            int fieldId = info.getId();
            if (mapping.containsKey(activityId) && mapping.containsKey(fieldId)) {
              String activityName = mapping.get(activityId);
              String fieldName = mapping.get(fieldId);
              tmp += ";" + activityName + ":" + fieldName;
              if (info.getTrigger() != null && !info.getTrigger().isEmpty()) {
                tmp += ":" + info.getTrigger();
              }
            }
          }
          if (!tmp.isEmpty()) {
            key = tmp.substring(1);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      newValues.put(key, value);
      if (!oldKey.equals(key)) {
        oldNewMapping.put(oldKey, key);
      }
    }
    return newValues;
  }

  public void setConstraint(IConstraint constraint) {
    this.constraint = constraint;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public List<SootClass> getConstraintSootClasses() {
    if (constraintSootClasses == null) {
      getConstraintMap();
    }
    return constraintSootClasses;
  }

  public List<List<ConstraintInformation>> getPaths() {
    return paths;
  }

  public void setPaths(List<List<ConstraintInformation>> paths) {
    this.paths = paths;
  }

  public String constraintToReadableString() {
    if (constraintSootClasses == null) {
      getConstraintMap();
    }
    String s = this.getConstraint().toReadableString();
    for (Entry<String, String> e : this.oldNewMapping.entrySet()) {
      s = s.replace(e.getKey(), e.getValue());
    }
    return s;
  }
}
