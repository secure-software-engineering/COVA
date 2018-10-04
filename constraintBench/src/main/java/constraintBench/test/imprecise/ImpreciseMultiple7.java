package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple7 {

  public void test() {
    if (Configuration.fieldA.startsWith("FA")) {
      System.out.println();// im(FA)_0
      if (Configuration.fieldB.startsWith("FB")) { // im(FA)_0
        System.out.println(); // im(FA)_0 ^ im(FB)_0
      }
      System.out.println();// im(FA)_0
    }
  }
}
