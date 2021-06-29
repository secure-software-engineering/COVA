package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple8 {

  public void test() {
    int flag = 0;
    if (Configuration.fieldA.startsWith("FA") && Configuration.fieldB.endsWith("FB")) {
      flag = 1; // str.prefixof("FA", FA) ^ str.suffixof("FB", FB)
    }
    if (Configuration.fieldA.endsWith("FA") || Configuration.fieldB.startsWith("FB")) {
      flag = 2; // str.suffixof("FA", FA) v str.prefixof("FA", FB)
    }
    System.out.println(flag);
  }

}
