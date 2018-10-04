package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanSingle2 {

  public void test() {
    boolean featureA = Configuration.featureA();
    if (!featureA) {
      System.out.println();// !A
    }
    System.out.println();
  }

}
