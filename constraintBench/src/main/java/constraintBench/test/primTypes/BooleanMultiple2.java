package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanMultiple2 {

  public void test() {
    boolean config = Configuration.featureA() || Configuration.featureB();
    if (config) {
      System.out.println();// A v B
    } else {
      System.out.println(); // !A ^ !B
    }
    System.out.println();
  }

}
