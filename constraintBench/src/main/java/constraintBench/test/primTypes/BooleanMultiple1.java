package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class BooleanMultiple1 {

  public void test() {
    if (Configuration.featureA()) {
      System.out.println();// A
      if (Configuration.featureB()) { // A
        System.out.println();// A ^ B
      } else {
        System.out.println(); // A ^ !B
      }
    }
    System.out.println();
  }

}
