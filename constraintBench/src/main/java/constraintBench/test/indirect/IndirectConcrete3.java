package constraintBench.test.indirect;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class IndirectConcrete3 {

  public void test() {
    int a = getA();
    if (a > 0) {
      System.out.println();// A
    } else {
      System.out.println(); // !A
    }
  }

  private int getA() {
    if (Configuration.featureA()) {
      return 1; // A
    } else {
      return 0; // !A
    }
  }

}
