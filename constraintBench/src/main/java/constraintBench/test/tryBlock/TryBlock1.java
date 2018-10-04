package constraintBench.test.tryBlock;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class TryBlock1 {

  public void test() {
    boolean a = Configuration.featureA();
    if (a) {
      try {
        throw new Exception(); // A
      } catch (Exception e) {
        System.out.println();// A
      }
    }
    System.out.println();
  }

}
