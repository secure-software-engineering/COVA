package constraintBench.test.recursion;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Recursion1 {

  public void test() {
    while (true) {
      if (Configuration.featureA()) {
        System.out.println();// A
      } else {
        System.out.println();// !A
      }
    }
  }

}
