package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple8 {

  public void test() {
    int flag = 0;
    if (Configuration.fieldA.startsWith("FA") && Configuration.fieldB.endsWith("FB")) {
      flag = 1; // im(FA)_0 ^ im(FB)_0
    }
    if (Configuration.fieldA.endsWith("FA") || Configuration.fieldB.startsWith("FB")) {
      flag = 2; // im(FA)_1 v im(FB)_1
    }
    System.out.println(flag);
  }

}
