package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseSingle1 {

  public void test() {
    if (Configuration.fieldA.startsWith("FA")) {
      System.out.println();// im(FA)_0
    }
    System.out.println();
  }
}
