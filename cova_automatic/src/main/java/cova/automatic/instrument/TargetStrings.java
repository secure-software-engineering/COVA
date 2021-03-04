package cova.automatic.instrument;

public class TargetStrings {
  private String target;
  private String info;
  private int javaLineNumber;
  private String javaClassName;

  public TargetStrings(String target, String info, int javaLineNumber, String javaClassName) {
    this.target = target;
    this.info = info;
    this.javaLineNumber = javaLineNumber;
    this.javaClassName = javaClassName;
  }

  public String getTarget() {
    return target;
  }

  public String getInfo() {
    return info;
  }

  public int getJavaLineNumber() {
    return javaLineNumber;
  }

  public String getJavaClassName() {
    return javaClassName;
  }
}
