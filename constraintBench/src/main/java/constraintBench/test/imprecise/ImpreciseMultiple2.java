package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple2 {

  public void test() {
    if (Configuration.fieldA.startsWith("FA") && Configuration.fieldB.endsWith("FB")) {
      System.out.println();//(str.prefixof("FA", FA) ^ str.suffixof("FB", FB))
    }
  }

}
