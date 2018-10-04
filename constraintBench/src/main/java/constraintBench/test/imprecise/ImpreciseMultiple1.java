package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple1 {

  public void test() {
    if (!Configuration.fieldA.startsWith("FA") && !Configuration.fieldA.startsWith("FB")) {
      return; // !im(FA)_0 ^ !im(FA)_1
    }
    System.out.println();// im(FA)_0 v im(FA)_1
  }
}
