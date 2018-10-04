package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple2 {

  public void test() {
    if (Configuration.fieldA.startsWith("FA") && Configuration.fieldB.endsWith("FB")) {
      System.out.println();// im(FA)_0 ^ im(FB)_0
    }
  }

}
