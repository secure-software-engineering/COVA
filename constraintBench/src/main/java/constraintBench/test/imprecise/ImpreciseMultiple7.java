package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple7 {

  public void test() {
    if (Configuration.fieldA.startsWith("FA")) {
      System.out.println();// str.prefixof("FA", FA)
      if (Configuration.fieldB.startsWith("FB")) { //str.prefixof("FA", FA)
        System.out.println(); // (str.prefixof("FA", FA) ^ str.prefixof("FB", FB))
      }
      System.out.println();// str.prefixof("FA", FA)
    }
  }
}
