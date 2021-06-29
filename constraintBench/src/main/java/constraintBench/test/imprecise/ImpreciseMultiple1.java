package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple1 {

  public void test() {
    if (!Configuration.fieldA.startsWith("FA") && !Configuration.fieldA.startsWith("FB")) {
      return; // (!str.prefixof("FA", FA) ^ !str.prefixof("FB", FA))
    }
    System.out.println();// str.prefixof("FA", FA) v str.prefixof("FB", FA)
  }
}
