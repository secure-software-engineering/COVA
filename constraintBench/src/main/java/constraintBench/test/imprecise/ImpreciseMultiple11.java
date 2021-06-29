package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple11 {

  public void test() {
    String fa = Configuration.fieldA;
    boolean a = fa.startsWith("a");
    String fc = Configuration.fieldC;
    if (Configuration.fieldB.startsWith("b")) {
      a = fc.equals("c");// str.prefixof("b", FB)
    }
    if (a) {
      System.out.println();// (str.prefixof("a", FA) v (str.prefixof("b", FB) ^ ("c" = FC)))
    }
  }
}
