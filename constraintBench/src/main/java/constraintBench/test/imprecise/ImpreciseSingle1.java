package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseSingle1 {

  public void test() {
    if (Configuration.fieldA.startsWith("FA")) {
      System.out.println();// str.prefixof("FA", FA)
    }
    System.out.println();
  }
}
