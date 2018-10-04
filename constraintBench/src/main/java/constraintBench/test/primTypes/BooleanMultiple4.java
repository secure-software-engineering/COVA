package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class BooleanMultiple4 {

  public void test() {
    boolean b = false;
    boolean c = false;
    if (Configuration.featureA()) {
      b = Configuration.featureB(); // A
    }
    if (Configuration.featureE()) {
      c = Configuration.featureC();// E
    }
    if (b == c) {
      System.out.println(); // (B = C) v !A v !E
    } else {
      System.out.println(); // !(B = C) v !A v !E
    }
    System.out.println();
  }

}
