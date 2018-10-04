package constraintBench.utils;

/**
 * 
 * 
 *
 */
public class Property extends SuperProperty{
  private int v;
  private int w;
  private int y;

  private boolean on;

  public Property() {
    v = 1;
    w = 2;
    y = 3;
  }

  public boolean isFeatureEnable() {
    return on;
  }

  public int moreThanCompare(Property q) {
    q.on = Configuration.featureA();
    if (w > q.w) {
      return y;
    } else {
      return v;
    }
  }

  public void setOn() {
    on = Configuration.featureA();
  }

  public int callee(Property q)
  {
    if(q.on) {
      return y;
    }
    return v;
  }

};
