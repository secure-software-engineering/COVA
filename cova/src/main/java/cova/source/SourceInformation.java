package cova.source;

import java.util.ArrayList;
import java.util.List;

public class SourceInformation {

  private String methodName;
  private String trigger;
  private int id;
  private int layoutId;
  private List<SourceInformation> allInfos = new ArrayList<>();

  public SourceInformation(int layoutId, int id, String methodName, String trigger) {
    this.methodName = methodName;
    this.id = id;
    this.trigger = trigger;
    this.layoutId = layoutId;
  }

  public SourceInformation(int layoutId, int id) {
    this(layoutId, id, null, null);
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getTrigger() {
    return trigger;
  }

  public void setTrigger(String trigger) {
    this.trigger = trigger;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getLayoutId() {
    return layoutId;
  }

  public void setLayoutId(int layoutId) {
    this.layoutId = layoutId;
  }

  public List<SourceInformation> getAllInfos() {
    return allInfos;
  }
}
