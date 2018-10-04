package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 *
 * 
 */
public class PrimitiveFieldRef2 {

  class First {
    public boolean f1;
  }

  class Second {
    public First f2;

    public Second(First f) {
      f2 = f;
    }
  }

  public void test() {
    First first = new First();
    first.f1 = Configuration.featureA();
    Second second = new Second(first);
    if (second.f2.f1) {
      System.out.println();// A
    }
    boolean a = second.f2.f1;
    if (a) {
      System.out.println();// A
    }
    a = true;
    if (a) {
      System.out.println();// no constraint
    }
    if (second.f2.f1) {
      System.out.println();// A
    }
  }

}
