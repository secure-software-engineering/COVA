package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class BooleanSingle3 {

  public void test() {
    if (Configuration.featureA()) {
      System.out.println();// A
    } else {// A
      System.out.println();// !A
    }
    System.out.println();
  }

}
