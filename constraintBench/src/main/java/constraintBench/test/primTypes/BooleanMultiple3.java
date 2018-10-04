package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanMultiple3 {

  public void test() {
    boolean b = false;
    if (Configuration.featureA()) {
      b = Configuration.featureB(); // A
    }
    if (b) {
      System.out.println(); // B v !A
    } else {
      System.out.println(); // !B v !A
    }
    System.out.println();
  }
}
