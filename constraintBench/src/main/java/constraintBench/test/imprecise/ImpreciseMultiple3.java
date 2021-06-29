package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple3 {

  public void test() {
    if (!Configuration.fieldA.startsWith("FA") && !Configuration.fieldB.startsWith("B")) {
      return; // !str.prefixof("FA", FA) ∧ !str.prefixof("B", FB)
    } else if (Configuration.fieldC.equals("C")) {
      return;// (str.prefixof("FA", FA) ∨  str.prefixof("B", FB)) ∧  "C"= FC
    }
    System.out.println();// (str.prefixof("FA", FA) ∨  str.prefixof("B", FB))∧ !  "C"= FC
  }

}
