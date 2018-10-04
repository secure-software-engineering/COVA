package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 *
 * 
 */
public class ArgumentToParameter1 {

  class First {
    private Second second;

    public First(Second s) {
      second = s;
    }

    public int getField() {
      return second.getField();
    }

    public void setField(int f) {
      second.setField(f);
    }
  }

  class Second {
    private int field;

    public Second(int f) {
      field = f;
    }

    public int getField() {
      return field;
    }

    public void setField(int f) {
      field = f;
    }
  }

  public void test() {
    int d = Configuration.featureD();
    Second second = new Second(d);
    First first = new First(second);
    callee(first);
    int d1 = first.getField();
    if(d1>10)
    {
      System.out.println();// no constraint
    }
  }

  private void callee(First first) {
    int d2 = first.getField();
    if (d2 > 20) {
      System.out.println();// D>20
    }
    first.setField(10);
    int d3 = first.getField();
    if (d3 > 30) {
      System.out.println();// no constraint
    }
  }

}
