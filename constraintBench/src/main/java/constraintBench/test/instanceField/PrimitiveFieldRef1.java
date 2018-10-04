package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 *
 * 
 */
public class PrimitiveFieldRef1 {

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
    First first2 = second.f2;
    first2.f1 = true;// strong update
    if (second.f2.f1) {
      System.out.println();// no constraint
    }
  }

}
