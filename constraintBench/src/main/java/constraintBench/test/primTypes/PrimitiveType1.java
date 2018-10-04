package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class PrimitiveType1 {

  public void test() {
    boolean a = Configuration.featureA();
    boolean b = a;
    if (a)
    {
      System.out.println();// A
    }
    if (b) {
      System.out.println();// A
    }
    a = true;
    if (a) {
      System.out.println();// no constraint
    }
    if (b) {
      System.out.println();// A
    }
  }

}
