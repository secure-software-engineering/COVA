package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanMultiple6 {

  public void test() {
    boolean config = Configuration.featureC();
    if (Configuration.featureA()) {
      config = Configuration.featureB(); // A
    }
    if (config) {
      System.out.println(); // (!A ^ C) ∨ ( A ^ B)
    } else {
      System.out.println(); // (!A ^ !C) ∨ (A ^ !B)
    }
    System.out.println();
  }

}
